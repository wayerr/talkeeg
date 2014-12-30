/*
 * Copyright (c) 2014, wayerr (radiofun@ya.ru).
 *
 *      This file is part of talkeeg-parent.
 *
 *      talkeeg-parent is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      talkeeg-parent is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with talkeeg-parent.  If not, see <http://www.gnu.org/licenses/>.
 */

package talkeeg.common.ipc;

import com.google.common.base.Preconditions;
import talkeeg.bf.Arrays;
import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.common.core.AcquaintedClient;
import talkeeg.common.core.OwnedKeyType;
import talkeeg.common.model.*;
import talkeeg.common.util.Closeable;
import talkeeg.common.util.DateUtils;
import talkeeg.common.util.StateChecker;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * basic class for stream registrations
 * Created by wayerr on 29.12.14.
 */
abstract class StreamBasicRegistration implements Closeable {
    private static final StateChecker.Graph<StreamMessageType> STATES = StateChecker.<StreamMessageType>builder()
      .transit(StreamMessageType.REQUEST, StreamMessageType.HEAD, StreamMessageType.END)
      .transit(StreamMessageType.HEAD, StreamMessageType.DATA, StreamMessageType.END)
      .transit(StreamMessageType.DATA, StreamMessageType.END)
      .build();
    private static final List<CipherOptions> SUPPORTED_CIPHERS = Collections.singletonList(CipherOptions.builder()
      .cipher(SymmetricCipherType.AES_128)
      .mode(CipherMode.CTR)
      .padding(PaddingType.PKCS5)
      .mac(MacType.HMAC_SHA1)
      .build());
    protected final StreamSupport streamSupport;
    protected final short streamId;
    protected final long time;
    protected final StateChecker<StreamMessageType> checker;
    protected final Object lock = new Object();
    private final Int128 otherClientId;
    private Key _secretKey;
    private IvParameterSpec _iv;
    private CipherOptions _options;
    private BinaryData _seed;
    private final IdSequenceGenerator idGenerator = new IdSequenceGenerator(Integer.MAX_VALUE);


    /**
     * @param streamSupport
     * @param initialState initial registration state
     * @param otherClientId id of client with which do exchange
     * @param streamId id of stream
     */
    StreamBasicRegistration(StreamSupport streamSupport, StreamMessageType initialState, Int128 otherClientId, short streamId) {
        this.checker = STATES.createChecker(initialState);
        this.streamSupport = streamSupport;
        this.streamId = streamId;
        this.time = System.currentTimeMillis();
        this.otherClientId = otherClientId;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
          "streamSupport=" + streamSupport +
          ", streamId=" + streamId +
          ", time=" + DateUtils.toString(time) +
          '}';
    }

    @Override
    public void close() {
    }

    public final short getStreamId() {
        return streamId;
    }

    /**
     * creation time of stream registration
     * @return
     */
    public long getTime() {
        return time;
    }

    protected BinaryData getOrCreateSeed() {
        synchronized(this.lock) {
            if(this._seed == null) {
                this._seed = new BinaryData(SecureRandom.getSeed(128));
            }
        }
        return _seed;
    }

    final void process(IpcEntryHandlerContext<StreamMessage> context) throws Exception {
        final StreamMessage message = context.getMessage();
        if(!Objects.equals(message.getDst(), getOwnClientId())) {
            throw new RuntimeException("Destination client id: " + message.getDst() + " not equals with current client id: " + getOwnClientId());
        }
        final StreamMessageType type = message.getType();
        checker.transit(type);
        checkOtherClientId(message.getSrc());
        final BinaryData decrypted = verifyAndDecrypt(message, type);
        processDecrypted(message, decrypted);
    }

    /**
     * own client id
     * @return
     */
    protected Int128 getOwnClientId() {
        return this.streamSupport.getOwnClientId();
    }

    protected abstract void processDecrypted(StreamMessage message, BinaryData decrypted) throws Exception;

    protected final void initStreamParameters(CipherOptions options, IvParameterSpec iv, BinaryData providerSeed, BinaryData consumerSeed) {
        synchronized(this.lock) {
            this._iv = iv;
            this._options = options;
            this._secretKey =this.streamSupport.cryptoService.generateSecretKey(providerSeed, consumerSeed, options.getCipher());
        }
    }

    protected final void checkOtherClientId(Int128 clientId) {
        if(!Objects.equals(this.otherClientId, clientId)) {
            throw new RuntimeException("Client id was changed from " + this.otherClientId + " to " + clientId);
        }
    }

    private BinaryData verifyAndDecrypt(StreamMessage message, StreamMessageType type) throws GeneralSecurityException {
        //verify and decrypt
        final Int128 clientId = message.getSrc();
        final BinaryData data = message.getData();
        final BinaryData sign = message.getMac();
        if(type == StreamMessageType.HEAD || type == StreamMessageType.REQUEST) {
            //while cipher not configured sign contains client signature
            //check client sign
            final AcquaintedClient client = this.streamSupport.clientsService.getClient(clientId);
            if(client == null) {
                throw new RuntimeException("Client " + clientId + " is not acquainted");
            }
            final Signature verifyService = this.streamSupport.cryptoService.getVerifyService(client.getKey());
            verifyService.update(data.getData());
            boolean verify = verifyService.verify(sign.getData());
            if(!verify) {
                throw new RuntimeException("Bad signature from " + clientId);
            }
            //decrypt
            Cipher cipher = this.streamSupport.cryptoService.getDecipherAsymmetricService(OwnedKeyType.CLIENT);
            cipher.update(data.getData());
            return new BinaryData(cipher.doFinal());
        } else {
            //check MAC
            final Key secretKey = this.getSecretKey();
            final Mac mac = this.streamSupport.cryptoService.getMac(secretKey);
            mac.update(data.getData());
            boolean verify = Objects.deepEquals(sign.getData(), mac.doFinal());
            if(!verify) {
                throw new RuntimeException("Bad MAC from " + clientId);
            }
            //decrypt
            IvParameterSpec localIv;
            CipherOptions localOptions;
            synchronized(this.lock) {
                localIv = this._iv;
                localOptions = this._options;
            }
            Cipher cipher = this.streamSupport.cryptoService.getCipherService(localOptions, secretKey, localIv);
            cipher.update(data.getData());
            return new BinaryData(cipher.doFinal());
        }
    }

    protected final Object deserialize(BinaryData data) throws Exception {
        return this.streamSupport.bf.read(ByteBuffer.wrap(data.getData()));
    }

    protected final BinaryData serialize(Object object) throws Exception {
        ByteBuffer buffer = this.streamSupport.bf.write(object);
        return new BinaryData(Arrays.toArray(buffer));
    }

    protected final Key getSecretKey() {
        synchronized(this.lock) {
            return _secretKey;
        }
    }

    protected void send(StreamMessageType type, BinaryData binaryData) throws Exception {
        final StreamMessage.Builder builder = new StreamMessage.Builder();
        builder.setStreamId(streamId);
        builder.setId(idGenerator.next());
        final Int128 clientId = getOtherClientId();
        Preconditions.checkNotNull(clientId, "clientId is null");
        builder.setDst(clientId);
        builder.setSrc(getOwnClientId());
        signAndEncrypt(builder, type, binaryData);
        final StreamMessage streamMessage = builder.build();
        this.streamSupport.send(streamMessage);
    }

    private void signAndEncrypt(StreamMessage.Builder builder, StreamMessageType type, BinaryData binaryData) {

    }

    /**
     * id of client with which does exchange
     * @return
     */
    public Int128 getOtherClientId() {
        synchronized(this.lock) {
            return otherClientId;
        }
    }

    List<CipherOptions> getSupportedCiphers() {
        return SUPPORTED_CIPHERS;
    }
}
