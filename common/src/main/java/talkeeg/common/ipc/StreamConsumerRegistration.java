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


import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.common.core.AcquaintedClient;
import talkeeg.common.core.OwnedKeyType;
import talkeeg.common.model.StreamMessage;
import talkeeg.common.model.StreamMessageType;
import talkeeg.common.util.Closeable;
import talkeeg.common.util.DateUtils;
import talkeeg.common.util.StateChecker;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Signature;
import java.util.Objects;

/**
 * registration of stream consumer
 * Created by wayerr on 29.12.14.
 */
public class StreamConsumerRegistration implements Closeable {

    private static final StateChecker.Graph<StreamMessageType> STATES = StateChecker.<StreamMessageType>builder()
      .transit(StreamMessageType.HEAD, StreamMessageType.INIT, StreamMessageType.END)
      .transit(StreamMessageType.INIT, StreamMessageType.DATA, StreamMessageType.END)
      .transit(StreamMessageType.DATA, StreamMessageType.END)
      .build();
    private final StreamSupport streamSupport;
    private final StreamConsumer consumer;
    private final short streamId;
    private final long time;
    private final Object lock = new Object();
    private Int128 srcClientId;
    private Key secretKey;
    private IvParameterSpec iv;
    private final StateChecker<StreamMessageType> checker = STATES.createChecker(StreamMessageType.HEAD);

    StreamConsumerRegistration(StreamSupport streamSupport, StreamConsumer consumer, short streamId) {
        this.streamSupport = streamSupport;
        this.consumer = consumer;
        this.streamId = streamId;
        this.time = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "StreamConsumerRegistration{" +
          "streamSupport=" + streamSupport +
          ", streamId=" + streamId +
          ", time=" + DateUtils.toString(time) +
          '}';
    }

    @Override
    public void close() {
        this.streamSupport.unregisterConsumer(this);
    }

    public short getStreamId() {
        return streamId;
    }

    void process(IpcEntryHandlerContext<StreamMessage> context) {
        final StreamMessage message = context.getMessage();
        final StreamMessageType type = message.getType();
        checker.checkPossibility(type);
        updateSourceClientId(message);
        final BinaryData decrypted;
        try {
            decrypted = verifyAndDecrypt(message, type);
        } catch(GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        switch(type) {
            case HEAD:
                throw new RuntimeException("Consumer not support HEAD stream message");
            case INIT:
                initStream(decrypted);
                this.consumer.open();
                break;
            case DATA:
                this.consumer.consume(decrypted);
                break;
            case END:
                this.consumer.close();
        }
    }

    private void initStream(BinaryData decrypted) {

    }

    private void updateSourceClientId(StreamMessage message) {
        synchronized(this.lock) {
            final Int128 src = message.getSrc();
            if(this.srcClientId == null) {
                this.srcClientId = src;
            } else if(!Objects.equals(this.srcClientId, src)) {
                throw new RuntimeException("Source client id was changed from " + this.srcClientId + " to " + src);
            }
        }
    }

    private BinaryData verifyAndDecrypt(StreamMessage message, StreamMessageType type) throws GeneralSecurityException {
        //verify and decrypt
        final Int128 clientId = message.getSrc();
        final BinaryData data = message.getData();
        final BinaryData sign = message.getMac();
        if(type == StreamMessageType.HEAD || type == StreamMessageType.INIT) {
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
            Cipher cipher = this.streamSupport.cryptoService.getCipherService(secretKey, getInitializationVector());
            cipher.update(data.getData());
            return new BinaryData(cipher.doFinal());
        }
    }

    private Key getSecretKey() {
        synchronized(this.lock) {
            return secretKey;
        }
    }

    public IvParameterSpec getInitializationVector() {
        synchronized(this.lock) {
            return iv;
        }
    }
}
