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

import talkeeg.common.model.IdSequenceGenerator;
import talkeeg.common.util.Closeable;
import talkeeg.common.util.DateUtils;

/**
 * registration of stream provider
 * Created by wayerr on 29.12.14.
 */
public final class StreamProviderRegistration implements Closeable {
    private static final IdSequenceGenerator GENERATOR = new IdSequenceGenerator(Integer.MAX_VALUE);

    private final StreamProvider provider;
    private final long time;
    private final short streamId;
    private final StreamSupport streamSupport;

    StreamProviderRegistration(StreamSupport streamSupport, StreamProvider provider) {
        this.streamSupport = streamSupport;
        this.provider = provider;
        this.time = System.currentTimeMillis();
        this.streamId = GENERATOR.next();
    }

    public short getStreamId() {
        return streamId;
    }

    public long getTime() {
        return time;
    }

    public StreamProvider getProvider() {
        return provider;
    }

    @Override
    public String toString() {
        return "StreamProviderRegistration{" +
          "provider=" + provider +
          ", time=" + DateUtils.toString(time) +
          ", streamId=" + streamId +
          '}';
    }

    /**
     * unregister stream provider
     */
    @Override
    public void close() {
        this.streamSupport.unregister(this);
    }
}
