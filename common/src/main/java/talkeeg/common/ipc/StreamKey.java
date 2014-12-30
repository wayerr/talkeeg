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
import talkeeg.bf.Int128;

/**
 * key of stream registration. <p/>
 * the problem is that the flow identifier is unique for the client-client pair, therefore different clients can use
 * same streamId for communicate with us <p/>
 * Created by wayerr on 30.12.14.
 */
final class StreamKey {
    private final Int128 otherClientId;
    private final short streamId;

    public StreamKey(Int128 otherClientId, short streamId) {
        Preconditions.checkNotNull(otherClientId, "otherClientId is null");
        this.otherClientId = otherClientId;
        this.streamId = streamId;
    }

    public Int128 getOtherClientId() {
        return otherClientId;
    }

    public short getStreamId() {
        return streamId;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof StreamKey)) {
            return false;
        }

        StreamKey streamKey = (StreamKey)o;

        if(streamId != streamKey.streamId) {
            return false;
        }
        if(otherClientId != null? !otherClientId.equals(streamKey.otherClientId) : streamKey.otherClientId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = otherClientId != null? otherClientId.hashCode() : 0;
        result = 31 * result + (int)streamId;
        return result;
    }

    @Override
    public String toString() {
        return "StreamKey{" +
          "otherClientId=" + otherClientId +
          ", streamId=" + streamId +
          '}';
    }
}
