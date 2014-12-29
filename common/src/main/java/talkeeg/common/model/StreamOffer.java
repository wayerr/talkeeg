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

package talkeeg.common.model;

import com.google.common.base.Supplier;
import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;

/**
 * stream offer <p/>
 * used in IPC as argument for offering stream transfer <p/>
 * Created by wayerr on 29.12.14.
 */
@StructInfo(id = 24)
public final class StreamOffer {

    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static final class Builder implements BuilderInterface {
        private short streamId;
        private long length;

        public short getStreamId() {
            return streamId;
        }

        public Builder streamId(short streamId) {
            setStreamId(streamId);
            return this;
        }

        public void setStreamId(short streamId) {
            this.streamId = streamId;
        }

        public long getLength() {
            return length;
        }

        public Builder length(long length) {
            setLength(length);
            return this;
        }

        public void setLength(long length) {
            this.length = length;
        }

        @Override
        public Object build() {
            return new StreamOffer(this);
        }
    }


    private final short streamId;
    private final long length;

    private StreamOffer(Builder b) {
        this.streamId = b.streamId;
        this.length = b.length;
    }

    public short getStreamId() {
        return streamId;
    }

    public long getLength() {
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof StreamOffer)) {
            return false;
        }

        StreamOffer that = (StreamOffer)o;

        if(length != that.length) {
            return false;
        }
        if(streamId != that.streamId) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int)streamId;
        result = 31 * result + (int)(length ^ (length >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "StreamOffer{" +
          "streamId=" + streamId +
          ", length=" + length +
          '}';
    }
}
