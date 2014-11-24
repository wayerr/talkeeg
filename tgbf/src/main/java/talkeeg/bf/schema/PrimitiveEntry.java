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

        public Class<?> getJavaType() {
            return javaType;
        }

        public void setJavaType(Class<?> javaType) {
            this.javaType = javaType;
        }

        public PrimitiveEntry build() {
            return new PrimitiveEntry(this);
        }
    }

    private final Class<?> javaType;

    public PrimitiveEntry(Builder b) {
        super(ALLOWED_TYPES, b);
        this.javaType = b.javaType;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public static Builder builder() {
        return new Builder();
    }
}
