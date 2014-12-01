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
 * Created by wayerr on 21.11.14.
 */
public class BaseSingleMessage {

    public static abstract class Builder implements BuilderInterface {
        /**
         * `id(T02)`:  циклический идентификатор (используется для фильтрации дублей, уникален для каждого src)
         */
        protected short id;
        /**
         * `scr(T16)`: отпечаток CPubK клиента отправителя
         */
        protected Int128 src;
        /**
         *  `dst(T16)`: отпечаток CPubK клиента адресата
         */
        protected Int128 dst;

        public Builder() {
        }

        public short getId() {
            return id;
        }

        public void setId(short id) {
            this.id = id;
        }

        public Int128 getSrc() {
            return src;
        }

        public void setSrc(Int128 src) {
            this.src = src;
        }

        public Int128 getDst() {
            return dst;
        }

        public void setDst(Int128 dst) {
            this.dst = dst;
        }

        public BaseSingleMessage build() {
            return new BaseSingleMessage(this);
        }
    }

    /**
     * `id(T02)`:  циклический идентификатор (используется для фильтрации дублей, уникален для каждого src)
     */
    protected final short id;
    /**
     * `scr(T16)`: отпечаток CPubK клиента отправителя
     */
    protected final Int128 src;
    /**
     *  `dst(T16)`: отпечаток CPubK клиента адресата
     */
    protected final Int128 dst;

    protected BaseSingleMessage(Builder b) {
        this.id = b.id;
        this.src = b.src;
        this.dst = b.dst;
    }

    public Int128 getDst() {
        return dst;
    }

    public short getId() {
        return id;
    }

    public Int128 getSrc() {
        return src;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof BaseSingleMessage)) {
            return false;
        }

        final BaseSingleMessage that = (BaseSingleMessage)o;

        if(id != that.id) {
            return false;
        }
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
        int result = (int)id;
        result = 31 * result + (src != null ? src.hashCode() : 0);
        result = 31 * result + (dst != null ? dst.hashCode() : 0);
        return result;
    }
}
