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


import com.google.common.collect.ImmutableList;
import talkeeg.bf.BinaryData;
import talkeeg.common.model.*;
import javax.crypto.spec.IvParameterSpec;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * registration of stream consumer
 * Created by wayerr on 29.12.14.
 */
public final class StreamConsumerRegistration extends StreamBasicRegistration {

    private final StreamConsumer consumer;
    private long _length = -1;
    private int _lastId;
    private final Set<Integer> expected = new ConcurrentSkipListSet<>();

    StreamConsumerRegistration(StreamSupport streamSupport, StreamConsumer consumer, StreamConfig config) {
        super(streamSupport, StreamState.WAIT_HEAD, config);
        this.consumer = consumer;
    }

    @Override
    public void close() {
        super.close();
        //TODO send END if current state before END
        try {
            consumer.close(this);
        } catch(Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", e);
        }
    }

    protected StreamState processDecrypted(StreamMessage message, BinaryData decrypted) throws Exception {
        final StreamMessageType type = message.getType();
        StreamState newState;
        final int messageId = message.getId();
        updateLost(messageId);
        switch(type) {
            case HEAD:
                readHead(decrypted);
                this.consumer.open(this);
                newState = StreamState.WAIT_DATA;
                break;
            case DATA:
                this.consumer.consume(this, decrypted);
                sendResponse(messageId);
                newState = StreamState.WAIT_DATA;
                break;
            case END:
                if(!this.expected.isEmpty()) {
                    Logger.getLogger(getClass().getName()).severe("Stream is broken, lost: " + this.expected);
                    newState = StreamState.WAIT_DATA;
                } else {
                    newState = StreamState.WAIT_END;
                    this.consumer.close(this);
                }
                break;
            default:
                throw new RuntimeException("Consumer not support " + type + " stream message");
        }
        return newState;
    }

    private void updateLost(int messageId) {
        synchronized(this.lock) {
            final int lastId = this._lastId;
            if(messageId > lastId) {
                this._lastId = messageId;
            }
            if(messageId == lastId || messageId - lastId == 1) {
                return;
            }
            if(messageId < lastId) {
                this.expected.remove(messageId);
            } else {
                for(int i = lastId + 1; i < (messageId - 1); ++i) {
                    this.expected.add(i);
                }
            }
        }
    }

    private void sendResponse(int messageId) throws Exception {
        //in future we can send response after some timeout or after each N-th message
        final StreamResponse response = StreamResponse.builder()
          .accepted(Collections.singletonList(messageId))
          .needed(ImmutableList.copyOf(this.expected))
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
    protected void onStart() {
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
