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

package talkeeg.bf.schema;

import com.google.common.base.MoreObjects;
import talkeeg.bf.EntryType;

import java.util.EnumSet;
import java.util.Set;

/**
 * Created by wayerr on 24.11.14.
 */
public final class PrimitiveEntry extends Base {
    private static final Set<EntryType> ALLOWED_TYPES = EnumSet.of(
            EntryType.NULL,
            EntryType.HALF,
            EntryType.BYTE_1,
            EntryType.BYTE_2,
            EntryType.BYTE_4,
            EntryType.BYTE_8,
            EntryType.BYTE_16,
            EntryType.BYTES
    );

    public static final class  Builder extends Base.Builder {
        private Class<?> javaType;
        private String metaType;
        private boolean signed;
        private int maxLength;

        public Class<?> getJavaType() {
            return javaType;
        }

        public void setJavaType(Class<?> javaType) {
            this.javaType = javaType;
        }

        /**
         * value of {@link talkeeg.bf.MetaTypes}
         * @return
         */
        public String getMetaType() {
            return metaType;
        }

        /**
         * value of {@link talkeeg.bf.MetaTypes}
         * @param metaType
         */
        public void setMetaType(String metaType) {
            this.metaType = metaType;
        }

        /**
         * flag for signed integer numbers
         * @return
         */
        public boolean isSigned() {
            return signed;
        }

        /**
         * flag for signed integer numbers
         * @param signed
         */
        public void setSigned(boolean signed) {
            this.signed = signed;
        }

        /**
         * maximal length of bytes, or size in bytes for numbers (0 - means half byte value)
         * @return
         */
        public int getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }

        public PrimitiveEntry build() {
            return new PrimitiveEntry(this);
        }
    }

    private final Class<?> javaType;
    private final String metaType;
    private final boolean signed;
    private final int maxLength;

    public PrimitiveEntry(Builder b) {
        super(ALLOWED_TYPES, b);
        this.javaType = b.javaType;
        this.metaType = b.metaType;
        this.signed = b.signed;
        this.maxLength = b.maxLength;
        if(this.javaType == null) {
            throw new NullPointerException("javaType is null");
        }
        if(this.metaType == null) {
            throw new NullPointerException("metaType is null");
        }
        if(this.getType() == EntryType.HALF && signed) {
            throw new IllegalArgumentException("storing signed data in half byte values (EntryType.HALF) is unsupported");
        }
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    /**
     * id of meta type
     * @see talkeeg.bf.MetaTypeResolver
     * @return
     */
    public String getMetaType() {
        return metaType;
    }

    /**
     * flag for signed integer numbers
     * @return
     */
    public boolean isSigned() {
        return signed;
    }

    /**
     * maximal length of bytes, or size in bytes for numbers (0 - means half byte value)
     * @return
     */
    public int getMaxLength() {
        return maxLength;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected void toString(MoreObjects.ToStringHelper sb) {
        super.toString(sb);
        sb.add("javaType", this.javaType);
        sb.add("metaType", this.metaType);
        sb.add("signed", this.signed);
        sb.add("maxLength", this.maxLength);
    }
}
