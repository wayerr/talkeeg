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

import talkeeg.bf.Int128;
import talkeeg.common.model.ClientAddress;

/**
 * configuration of stream
 *
 * Created by wayerr on 30.12.14.
 */
public final class StreamConfig {

    public static final class Builder {
        private Int128 otherClientId;
        private short streamId;
        private ClientAddress otherClientAddress;

        /**
         * id of client with which do exchange
         * @return
         */
        public Int128 getOtherClientId() {
            return otherClientId;
        }

        /**
         * id of client with which do exchange
         * @param otherClientId
         */
        public Builder otherClientId(Int128 otherClientId) {
            setOtherClientId(otherClientId);
            return this;
        }

        /**
         * id of client with which do exchange
         * @param otherClientId
         */
        public void setOtherClientId(Int128 otherClientId) {
            this.otherClientId = otherClientId;
        }

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

        /**
         * address of client with which do exchange
         * @return
         */
        public ClientAddress getOtherClientAddress() {
            return otherClientAddress;
        }

        /**
         * address of client with which do exchange
         * @param otherClientAddress
         */
        public Builder otherClientAddress(ClientAddress otherClientAddress) {
            setOtherClientAddress(otherClientAddress);
            return this;
        }

        /**
         * address of client with which do exchange
         * @param otherClientAddress
         */
        public void setOtherClientAddress(ClientAddress otherClientAddress) {
            this.otherClientAddress = otherClientAddress;
        }

        public StreamConfig build() {
            return new StreamConfig(this);
        }
    }

    private final Int128 otherClientId;
    private final short streamId;
    private final ClientAddress otherClientAddress;

    private StreamConfig(Builder b) {
        this.streamId = b.streamId;
        this.otherClientId = b.otherClientId;
        this.otherClientAddress = b.otherClientAddress;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * id of client with which do exchange
     * @return
     */
    public Int128 getOtherClientId() {
        return otherClientId;
    }

    public short getStreamId() {
        return streamId;
    }

    /**
     * address of client with which do exchange
     * @return
     */
    public ClientAddress getOtherClientAddress() {
        return otherClientAddress;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof StreamConfig)) {
            return false;
        }

        StreamConfig that = (StreamConfig)o;

        if(streamId != that.streamId) {
            return false;
        }
        if(otherClientAddress != null? !otherClientAddress.equals(that.otherClientAddress) : that.otherClientAddress != null) {
            return false;
        }
        if(otherClientId != null? !otherClientId.equals(that.otherClientId) : that.otherClientId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = otherClientId != null? otherClientId.hashCode() : 0;
        result = 31 * result + (int)streamId;
        result = 31 * result + (otherClientAddress != null? otherClientAddress.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StreamContext{" +
          "otherClientId=" + otherClientId +
          ", streamId=" + streamId +
          ", otherClientAddress=" + otherClientAddress +
          '}';
    }
}
