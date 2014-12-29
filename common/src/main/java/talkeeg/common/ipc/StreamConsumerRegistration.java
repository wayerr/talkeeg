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


import talkeeg.common.util.Closeable;
import talkeeg.common.util.DateUtils;

/**
 * registration of stream consumer
 * Created by wayerr on 29.12.14.
 */
public class StreamConsumerRegistration implements Closeable {

    private final StreamSupport streamSupport;
    private final StreamConsumer consumer;
    private final short streamId;
    private final long time;

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
}
