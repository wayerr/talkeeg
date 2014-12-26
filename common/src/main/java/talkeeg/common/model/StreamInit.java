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
import talkeeg.bf.BinaryData;
import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;

/**
 * structure which reference to stream (it`s appear as {@link talkeeg.common.model.Command } argument) and
 * used for initialization of stream transferring
 * Created by wayerr on 26.12.14.
 */
@StructInfo(id = 22)
public final class StreamInit {

    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static class Builder implements BuilderInterface {

        private short streamId;
        private BinaryData seed;

        public short getStreamId() {
            return streamId;
        }

        public void setStreamId(short streamId) {
            this.streamId = streamId;
        }

        public BinaryData getSeed() {
            return seed;
        }

        public void setSeed(BinaryData seed) {
            this.seed = seed;
        }

        public StreamInit build() {
            return new StreamInit(this);
        }
    }

    private final short streamId;
    private final BinaryData seed;

    private StreamInit(Builder b) {
        this.streamId = b.streamId;
        this.seed = b.seed;
    }

    public short getStreamId() {
        return streamId;
    }

    public BinaryData getSeed() {
        return seed;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof StreamInit)) {
            return false;
        }

        StreamInit that = (StreamInit)o;

        if(streamId != that.streamId) {
            return false;
        }
        if(seed != null? !seed.equals(that.seed) : that.seed != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int)streamId;
        result = 31 * result + (seed != null? seed.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StreamInit{" +
          "streamId=" + streamId +
          ", seed=" + seed +
          '}';
    }
}
