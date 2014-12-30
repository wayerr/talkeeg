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

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import talkeeg.bf.BinaryData;
import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;

/**
 * message of stream
 *
 * Created by wayerr on 26.12.14.
 */
@StructInfo(id = 2)
public final class StreamMessage extends BaseMessage {
    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };
    public static final class Builder extends BaseMessage.Builder implements BuilderInterface {

        private short streamId;
        private int id;
        private StreamMessageType type;
        private BinaryData mac;
        private BinaryData data;

        public short getStreamId() {
            return streamId;
        }

        public void setStreamId(short streamId) {
            this.streamId = streamId;
        }

        /**
         * number of message in stream sequence
         * @return
         */
        public int getId() {
            return id;
        }

        /**
         * number of message in stream sequence
         * @param id
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         * type of message
         * @return
         */
        public StreamMessageType getType() {
            return type;
        }

        /**
         * type of message
         * @param type
         */
        public void setType(StreamMessageType type) {
            this.type = type;
        }

        /**
         * message authentication code
         * @return
         */
        public BinaryData getMac() {
            return mac;
        }

        /**
         * message authentication code
         * @param mac
         */
        public void setMac(BinaryData mac) {
            this.mac = mac;
        }

        public BinaryData getData() {
            return data;
        }

        public void setData(BinaryData data) {
            this.data = data;
        }

        public StreamMessage build() {
            return new StreamMessage(this);
        }
    }
    private final short streamId;
    private final int id;
    private final StreamMessageType type;
    private final BinaryData mac;
    private final BinaryData data;

    private StreamMessage(Builder b) {
        super(b);

        this.streamId = b.streamId;
        this.id = b.id;
        Preconditions.checkNotNull(b.type, "type is null");
        this.type = b.type;
        this.mac = b.mac;
        this.data = b.data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public short getStreamId() {
        return streamId;
    }

    /**
     * number of message in stream sequence
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * type of message
     * @return
     */
    public StreamMessageType getType() {
        return type;
    }

    /**
     * message authentication code
     * @return
     */
    public BinaryData getMac() {
        return mac;
    }

    public BinaryData getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof StreamMessage)) {
            return false;
        }
        if(!super.equals(o)) {
            return false;
        }

        StreamMessage that = (StreamMessage)o;

        if(id != that.id) {
            return false;
        }
        if(streamId != that.streamId) {
            return false;
        }
        if(data != null? !data.equals(that.data) : that.data != null) {
            return false;
        }
        if(mac != null? !mac.equals(that.mac) : that.mac != null) {
            return false;
        }
        if(type != that.type) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int)streamId;
        result = 31 * result + id;
        result = 31 * result + (type != null? type.hashCode() : 0);
        result = 31 * result + (mac != null? mac.hashCode() : 0);
        result = 31 * result + (data != null? data.hashCode() : 0);
        return result;
    }
}
