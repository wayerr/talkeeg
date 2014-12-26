/*
 * Copyright (c) 2014, wayerr (radiofun@ya.ru).
 *
 *     This file is part of talkeeg.
 *
 *     talkeeg is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     talkeeg is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with talkeeg.  If not, see <http://www.gnu.org/licenses/>.
 */

package talkeeg.common.model;

import talkeeg.bf.Int128;

/**
 * base message
 * Created by wayerr on 21.11.14.
 */
public class BaseMessage {

    public static abstract class Builder implements BuilderInterface {
        /**
         * fingerprint of source client public key (clientId)
         */
        protected Int128 src;
        /**
         * fingerprint of destination client public key (clientId)
         */
        protected Int128 dst;

        public Builder() {
        }

        /**
         * fingerprint of source client public key (clientId)
         * @return
         */
        public Int128 getSrc() {
            return src;
        }

        /**
         * fingerprint of source client public key (clientId)
         * @param src
         */
        public void setSrc(Int128 src) {
            this.src = src;
        }

        /**
         * fingerprint of destination client public key (clientId)
         * @return
         */
        public Int128 getDst() {
            return dst;
        }

        /**
         * fingerprint of destination client public key (clientId)
         * @param dst
         */
        public void setDst(Int128 dst) {
            this.dst = dst;
        }

        public BaseMessage build() {
            return new BaseMessage(this);
        }
    }

    /**
     * fingerprint of source client public key (clientId)
     */
    protected final Int128 src;
    /**
     * fingerprint of destination client public key (clientId)
     */
    protected final Int128 dst;

    protected BaseMessage(Builder b) {
        this.src = b.src;
        this.dst = b.dst;
    }

    /**
     * fingerprint of destination client public key (clientId)
     * @return
     */
    public Int128 getDst() {
        return dst;
    }

    /**
     * fingerprint of source client public key (clientId)
     * @return
     */
    public Int128 getSrc() {
        return src;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof BaseMessage)) {
            return false;
        }

        final BaseMessage that = (BaseMessage)o;

        if(dst != null ? !dst.equals(that.dst) : that.dst != null) {
            return false;
        }
        if(src != null ? !src.equals(that.src) : that.src != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = src != null? src.hashCode() : 0;
        result = 31 * result + (dst != null? dst.hashCode() : 0);
        return result;
    }
}
