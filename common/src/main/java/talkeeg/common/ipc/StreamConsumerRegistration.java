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
import talkeeg.common.model.*;
import javax.crypto.spec.IvParameterSpec;
import java.util.Collections;

/**
 * registration of stream consumer
 * Created by wayerr on 29.12.14.
 */
public final class StreamConsumerRegistration extends StreamBasicRegistration {

    private final StreamConsumer consumer;
    private long _length = -1;

    StreamConsumerRegistration(StreamSupport streamSupport, StreamConsumer consumer, StreamConfig config) {
        super(streamSupport, StreamMessageType.HEAD, config);
        this.consumer = consumer;
    }

    @Override
    public void close() {
        super.close();
        //TODO send END if current state before END
    }

    protected void processDecrypted(StreamMessage message, BinaryData decrypted) throws Exception {
        StreamMessageType type = message.getType();
        switch(type) {
            case HEAD:
                readHead(decrypted);
                this.consumer.open(this);
                break;
            case DATA:
                this.consumer.consume(this, decrypted);
                sendResponse(message.getId());
                break;
            case END:
                this.consumer.close(this);
                break;
            default:
                throw new RuntimeException("Consumer not support " + type + " stream message");
        }
    }

    private void sendResponse(int messageId) throws Exception {
        //in future we can send response after some timeout or after each N-th message
        //TODO detect lost messages
        final StreamResponse response = StreamResponse.builder()
          .accepted(Collections.singletonList(messageId))
          .build();
        send(StreamMessageType.RESPONSE, serialize(response));
    }

    private void readHead(BinaryData decrypted) throws Exception {
        final StreamHead head = (StreamHead)deserialize(decrypted);
        final CipherOptions options = head.getOptions();
        synchronized(this.lock) {
            this._length = head.getLength();
        }
        initStreamParameters(options, new IvParameterSpec(head.getIv().getData()), head.getSeed(), this.getOrCreateSeed());
    }

    /**
     * length of consumed stream, before receiving of stream head may be -1
     * @return
     */
    public long getLength() {
        synchronized(this.lock) {
            return _length;
        }
    }

    /**
     * start stream consuming (send StreamRequest to provider)
     */
    public void start() {
        try {
            final StreamRequest streamRequest = StreamRequest.builder()
              .ciphers(getSupportedCiphers())
              .streamId(getStreamId())
              .seed(getOrCreateSeed())
              .build();
            send(StreamMessageType.REQUEST, serialize(streamRequest));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
