/*
 * Copyright (c) 2014, wayerr (radiofun@ya.ru).
 *
 *      This file is part of talkeeg.
 *
 *      talkeeg is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      talkeeg is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with talkeeg.  If not, see <http://www.gnu.org/licenses/>.
 */

package talkeeg.common.model;

import com.google.common.base.Supplier;
import talkeeg.bf.StructInfo;
import talkeeg.bf.BinaryData;
import talkeeg.bf.StructureBuilder;

/**
 * simple single message with signed and optionally ciphered data
 *
 * Created by wayerr on 21.11.14.
 */
@StructInfo(id = 1)
public class SingleMessage extends BaseMessage {
    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(SingleMessage.builder());
        }
    };

    public static class Builder extends BaseMessage.Builder {
        private short id;
        private BinaryData clientSign;
        private BinaryData userSign;
        private StatusCode status;
        private BinaryData data;
        private MessageCipherType cipherType;

        public short getId() {
            return id;
        }

        public void setId(short id) {
            this.id = id;
        }

        public BinaryData getData() {
            return data;
        }

        public void setData(BinaryData data) {
            this.data = data;
        }

        /**
         * status code of message
         * @see talkeeg.common.model.StatusCode
         * @return
         */
        public StatusCode getStatus() {
            return status;
        }

        /**
         * status code of message
         * @see talkeeg.common.model.StatusCode
         * @param status
         * @return
         */
        public Builder status(StatusCode status) {
            setStatus(status);
            return this;
        }

        /**
         * status code of message
         * @see talkeeg.common.model.StatusCode
         * @param status
         */
        public void setStatus(StatusCode status) {
            this.status = status;
        }

        /**
         * signature of data field of this message by client key
         * @return
         */
        public BinaryData getClientSign() {
            return clientSign;
        }

        /**
         * signature of data field of this message by client key
         * @param clientSign
         */
        public void setClientSign(BinaryData clientSign) {
            this.clientSign = clientSign;
        }

        /**
         * signature of data field of this message by user key
         * @return
         */
        public BinaryData getUserSign() {
            return userSign;
        }

        /**
         * signature of data field of this message by user key
         * @param userSign
         */
        public void setUserSign(BinaryData userSign) {
            this.userSign = userSign;
        }

        public MessageCipherType getCipherType() {
            return cipherType;
        }

        public void setCipherType(MessageCipherType cipherType) {
            this.cipherType = cipherType;
        }

        @Override
        public SingleMessage build() {
            return new SingleMessage(this);
        }
    }

    /**
     * `id(T02)`:  циклический идентификатор (используется для фильтрации дублей, уникален для каждого src)
     */
    private final short id;
    private final BinaryData clientSign;
    private final BinaryData userSign;
    private final StatusCode status;
    private final MessageCipherType cipherType;
    private final BinaryData data;

    private SingleMessage(Builder b) {
        super(b);
        this.id = b.id;
        this.clientSign = b.clientSign;
        this.userSign = b.userSign;
        this.status = b.status;
        this.data = b.data;
        this.cipherType = b.cipherType;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * `id(T02)`:  циклический идентификатор (используется для фильтрации дублей, уникален для каждого src)
     * @return
     */
    public short getId() {
        return id;
    }

    /**
     * signature of data field of this message by client key
     * @return
     */
    public BinaryData getClientSign() {
        return clientSign;
    }

    /**
     * signature of data field of this message by user key
     * @return
     */
    public BinaryData getUserSign() {
        return userSign;
    }

    /**
     * status code of message
     * @see talkeeg.common.model.StatusCode
     * @return
     */
    public StatusCode getStatus() {
        return status;
    }

    /**
     * message data
     * @return
     */
    public BinaryData getData() {
        return data;
    }

    /**
     * type of data field cipherType
     * @return
     */
    public MessageCipherType getCipherType() {
        return cipherType;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof SingleMessage)) {
            return false;
        }
        if(!super.equals(o)) {
            return false;
        }

        SingleMessage that = (SingleMessage)o;

        if(id != that.id) {
            return false;
        }
        if(cipherType != that.cipherType) {
            return false;
        }
        if(clientSign != null? !clientSign.equals(that.clientSign) : that.clientSign != null) {
            return false;
        }
        if(data != null? !data.equals(that.data) : that.data != null) {
            return false;
        }
        if(status != that.status) {
            return false;
        }
        if(userSign != null? !userSign.equals(that.userSign) : that.userSign != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int)id;
        result = 31 * result + (clientSign != null? clientSign.hashCode() : 0);
        result = 31 * result + (userSign != null? userSign.hashCode() : 0);
        result = 31 * result + (status != null? status.hashCode() : 0);
        result = 31 * result + (cipherType != null? cipherType.hashCode() : 0);
        result = 31 * result + (data != null? data.hashCode() : 0);
        return result;
    }
}
