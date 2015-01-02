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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Signature;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * basic class for stream registrations
 * Created by wayerr on 29.12.14.
 */
abstract class StreamBasicRegistration implements Closeable {
    private static final StateChecker.Graph<StreamState> STATES = StateChecker.<StreamState>builder()
      .transit(StreamState.WAIT_HEAD, StreamState.WAIT_DATA, StreamState.WAIT_END)
      .transit(StreamState.WAIT_DATA, StreamState.WAIT_DATA, StreamState.WAIT_END)
      .transit(StreamState.WAIT_REQUEST, StreamState.WAIT_RESPONSE, StreamState.WAIT_END)
      .transit(StreamState.WAIT_END, StreamState.END)
      .build();
    private static final List<CipherOptions> SUPPORTED_CIPHERS = Collections.singletonList(CipherOptions.builder()
      .cipher(SymmetricCipherType.AES_128)
      .mode(CipherMode.CTR)
      .padding(PaddingType.PKCS5)
      .mac(MacType.HMAC_SHA1)
      .build());
    protected final StreamSupport streamSupport;
    protected final StreamConfig config;
    protected final long time;
    protected final StateChecker<StreamState> checker;
    protected final Object lock = new Object();
    private Key _secretKey;
    private IvParameterSpec _iv;
    private CipherOptions _options;
    private BinaryData _seed;
    private long _lastUpdate;
    private final IdSequenceGenerator idGenerator = new IdSequenceGenerator(Integer.MAX_VALUE);
    private final StreamKey streamKey;

    /**
     * @param streamSupport
     * @param initialState initial registration state
     * @param config parameters of stream
     */
    StreamBasicRegistration(StreamSupport streamSupport, StreamState initialState, StreamConfig config) {
        this.checker = STATES.createChecker(initialState);
        this.streamSupport = streamSupport;
        this.config = config;
        this.time = System.currentTimeMillis();
        this.streamKey = new StreamKey(config.getOtherClientId(), config.getStreamId());
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
          "streamSupport=" + streamSupport +
          ", config=" + config +
          ", time=" + DateUtils.toString(time) +
          '}';
    }

    @Override
    public void close() {
        this.streamSupport.unregister(this);
    }

    public final short getStreamId() {
        return this.config.getStreamId();
    }

    public final StreamKey getStreamKey() {
        return this.streamKey;
    }

    /**
     * creation time of stream registration
     * @return
     */
    public long getCreationTime() {
        return time;
    }

    public long getLastUpdateTime() {
        synchronized(this.lock) {
            return this._lastUpdate;
        }
    }

    protected BinaryData getOrCreateSeed() {
        synchronized(this.lock) {
            if(this._seed == null) {
                this._seed = this.streamSupport.createRandomData(16);
            }
        }
        return _seed;
    }

    final void process(IpcEntryHandlerContext<StreamMessage> context) throws Exception {
        final StreamMessage message = context.getMessage();
        if(!Objects.equals(message.getDst(), getOwnClientId())) {
            throw new RuntimeException("Destination client id: " + message.getDst() + " not equals with current client id: " + getOwnClientId());
        }
        updateLastTime();
        checkOtherClientId(message.getSrc());
        final BinaryData decrypted = verifyAndDecrypt(message);
        final StreamState state = processDecrypted(message, decrypted);
        try {
            checker.transit(state);
        } catch(StateChecker.StateCheckerException e) {
            throw new RuntimeException("on " + this, e);
        }
    }

    private void updateLastTime() {
        synchronized(this.lock) {
            _lastUpdate = System.currentTimeMillis();
        }
    }

    /**
     * own client id
     * @return
     */
    protected Int128 getOwnClientId() {
        return this.streamSupport.getOwnClientId();
    }

    protected abstract StreamState processDecrypted(StreamMessage message, BinaryData decrypted) throws Exception;

    protected final void initStreamParameters(CipherOptions options, IvParameterSpec iv, BinaryData providerSeed, BinaryData consumerSeed) throws GeneralSecurityException {
        synchronized(this.lock) {
            this._iv = iv;
            this._options = options;
            short streamId = getStreamId();
            BinaryData salt = new BinaryData(new byte[]{(byte)(streamId >>> 8), (byte)streamId});
            this._secretKey =this.streamSupport.cryptoService.generateSecretKey(salt, providerSeed, consumerSeed, options.getCipher());
        }
    }

    protected final void checkOtherClientId(Int128 clientId) {
        final Int128 otherClientId = this.config.getOtherClientId();
        if(!Objects.equals(otherClientId, clientId)) {
            throw new RuntimeException("Client id was changed from " + otherClientId + " to " + clientId);
        }
    }

    private BinaryData verifyAndDecrypt(StreamMessage message) throws GeneralSecurityException {
        //verify and decrypt
        final Int128 clientId = message.getSrc();
        final BinaryData data = message.getData();
        if(data == null) {
            return null;
        }
        final BinaryData sign = message.getMac();
        if(sign == null) {
            throw new RuntimeException("Message with non null data and null MAC");
        }
        if(isCipheredByPublicKey(message.getType())) {
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
            byte[] decipheredData = ciphering(data, cipher);
            return new BinaryData(decipheredData);
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
            Cipher cipher = this.streamSupport.cryptoService.getDecipherService(localOptions, secretKey, localIv);
            byte[] decipheredData = ciphering(data, cipher);
            return new BinaryData(decipheredData);
        }
    }

    private boolean isCipheredByPublicKey(StreamMessageType type) {
        return type == StreamMessageType.HEAD || type == StreamMessageType.REQUEST;
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
        Preconditions.checkNotNull(type, "type is null");
        final StreamMessage.Builder builder = new StreamMessage.Builder();
        builder.setType(type);
        builder.setStreamId(this.config.getStreamId());
        builder.setId(idGenerator.next());
        final Int128 clientId = getOtherClientId();
        Preconditions.checkNotNull(clientId, "clientId is null");
        builder.setDst(clientId);
        builder.setSrc(getOwnClientId());
        signAndEncrypt(builder, type, binaryData);
        final StreamMessage streamMessage = builder.build();
        this.streamSupport.send(streamMessage, this.config.getOtherClientAddress());
    }

    private void signAndEncrypt(StreamMessage.Builder builder, StreamMessageType type, BinaryData data) throws GeneralSecurityException {
        Int128 clientId = getOtherClientId();
        final BinaryData result;
        final BinaryData sign;
        if(isCipheredByPublicKey(type)) {
            final AcquaintedClient client = this.streamSupport.clientsService.getClient(clientId);
            if(client == null) {
                throw new RuntimeException("Client " + clientId + " is not acquainted");
            }
            //encrypt
            Cipher cipher = this.streamSupport.cryptoService.getCipherAsymmetricService(client.getKey());
            byte[] cipheredData = ciphering(data, cipher);
            result = new BinaryData(cipheredData);
            //sign
            final Signature signService = this.streamSupport.cryptoService.getSignService(OwnedKeyType.CLIENT);
            signService.update(cipheredData);
            sign = new BinaryData(signService.sign());
        } else {
            //encrypt
            IvParameterSpec localIv;
            CipherOptions localOptions;
            synchronized(this.lock) {
                localIv = this._iv;
                localOptions = this._options;
            }
            final Key secretKey = this.getSecretKey();
            Cipher cipher = this.streamSupport.cryptoService.getCipherService(localOptions, secretKey, localIv);
            byte[] cipheredData = ciphering(data, cipher);
            result = new BinaryData(cipheredData);
            //create MAC
            final Mac mac = this.streamSupport.cryptoService.getMac(secretKey);
            mac.update(cipheredData);
            sign = new BinaryData(mac.doFinal());
        }
        builder.setData(result);
        builder.setMac(sign);
    }

    private byte[] ciphering(BinaryData data, Cipher cipher) throws GeneralSecurityException {
        if(data == null) {
            return new byte[0];
        }
        byte[] bytes = cipher.update(data.getData());
        byte[] end = cipher.doFinal();
        if(end.length == 0) {
            if(bytes == null) {
                return new byte[0];
            }
            return bytes;
        }
        byte[] res = new byte[bytes.length + end.length];
        System.arraycopy(bytes, 0, res, 0, bytes.length);
        System.arraycopy(end, 0, res, bytes.length, end.length);
        return res;
    }

    /**
     * id of client with which does exchange
     * @return
     */
    public Int128 getOtherClientId() {
        return this.config.getOtherClientId();
    }

    List<CipherOptions> getSupportedCiphers() {
        return SUPPORTED_CIPHERS;
    }

    final void start() {
        this.onStart();
    }

    protected void onStart() {

    }

    void closeIfTimeExceed(final long maxTime) {
        synchronized(this.lock) {
            final long timeout = this._lastUpdate - this.time;
            if(timeout >= maxTime) {
                Logger.getLogger(getClass().getName()).warning("stream time exceeded, closing: " + this);
                close();
            }
        }
    }
}
