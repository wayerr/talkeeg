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

import talkeeg.bf.StructInfo;
import talkeeg.bf.BinaryData;

/**
 * simple single message with signed and optionally ciphered data
 *
 * Created by wayerr on 21.11.14.
 */
@StructInfo(id = 1)
public class SingleMessage extends BaseSingleMessage {
    public static class Builder extends BaseSingleMessage.Builder {
        private BinaryData clientSign;
        private BinaryData userSign;
        private BinaryData data;
        private MessageCipherType cipherType;

        public BinaryData getData() {
            return data;
        }

        public void setData(BinaryData data) {
            this.data = data;
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

        public SingleMessage build() {
            return new SingleMessage(this);
        }
    }

    private final BinaryData clientSign;
    private final BinaryData userSign;
    private final MessageCipherType cipherType;
    private final BinaryData data;

    private SingleMessage(Builder b) {
        super(b);
        this.clientSign = b.clientSign;
        this.userSign = b.userSign;
        this.data = b.data;
        this.cipherType = b.cipherType;
    }

    public static Builder builder() {
        return new Builder();
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
}
