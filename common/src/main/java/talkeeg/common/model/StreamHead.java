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
 * header of stream
 * Created by wayerr on 26.12.14.
 */
@StructInfo(id = 21)
public final class StreamHead {
    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static final class Builder implements BuilderInterface {
        private CipherOptions options;
        private long length;
        private BinaryData iv;

        public CipherOptions getOptions() {
            return options;
        }

        public void setOptions(CipherOptions options) {
            this.options = options;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        /**
         * initialization vector of cipher
         * @return
         */
        public BinaryData getIv() {
            return iv;
        }

        /**
         * initialization vector of cipher
         * @param iv
         */
        public void setIv(BinaryData iv) {
            this.iv = iv;
        }

        public StreamHead build() {
            return new StreamHead(this);
        }
    }

    private final CipherOptions options;
    private final long length;
    private final BinaryData iv;

    private StreamHead(Builder b) {
        this.options = b.options;
        this.length = b.length;
        this.iv = b.iv;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CipherOptions getOptions() {
        return options;
    }

    public long getLength() {
        return length;
    }

    /**
     * initialization vector of cipher
     * @return
     */
    public BinaryData getIv() {
        return iv;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof StreamHead)) {
            return false;
        }

        StreamHead that = (StreamHead)o;

        if(length != that.length) {
            return false;
        }
        if(iv != null? !iv.equals(that.iv) : that.iv != null) {
            return false;
        }
        if(options != null? !options.equals(that.options) : that.options != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = options != null? options.hashCode() : 0;
        result = 31 * result + (int)(length ^ (length >>> 32));
        result = 31 * result + (iv != null? iv.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StreamHead{" +
          "options=" + options +
          ", length=" + length +
          '}';
    }
}
