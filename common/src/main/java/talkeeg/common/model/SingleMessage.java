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
import talkeeg.common.util.BinaryData;

/**
 * Created by wayerr on 21.11.14.
 */
@StructInfo(id = "1")
public class SingleMessage extends BaseSingleMessage {
    public static class Builder extends BaseSingleMessage.Builder {
        private Sign sign;
        private BinaryData data;
        private MessageCipherType cipher;

        public BinaryData getData() {
            return data;
        }

        public void setData(BinaryData data) {
            this.data = data;
        }

        public Sign getSign() {
            return sign;
        }

        public void setSign(Sign sign) {
            this.sign = sign;
        }

        public MessageCipherType getCipher() {
            return cipher;
        }

        public void setCipher(MessageCipherType cipher) {
            this.cipher = cipher;
        }

        public SingleMessage build() {
            return new SingleMessage(this);
        }
    }

    private final Sign sign;
    private final MessageCipherType cipher;
    private final BinaryData data;

    private SingleMessage(Builder b) {
        super(b);
        this.sign = b.sign;
        this.data = b.data;
        this.cipher = b.cipher;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * signature of data field of this message
     * @return
     */
    public Sign getSign() {
        return sign;
    }

    /**
     * message data
     * @return
     */
    public BinaryData getData() {
        return data;
    }

    /**
     * type of data field cipher
     * @return
     */
    public MessageCipherType getCipher() {
        return cipher;
    }
}
