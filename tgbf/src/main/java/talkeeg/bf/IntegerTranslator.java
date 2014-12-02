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

package talkeeg.bf;

import talkeeg.bf.schema.PrimitiveEntry;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Set;

/**
 * translator for primitives
 *
 * Created by wayerr on 25.11.14.
 */
final class IntegerTranslator implements Translator {
    private static final Set<EntryType> ALLOWED_TYPES = EnumSet.of(
            EntryType.HALF,
            EntryType.BYTE_1,
            EntryType.BYTE_2,
            EntryType.BYTE_4,
            EntryType.BYTE_8
    );
    private final PrimitiveEntry primitive;
    private final Class<?> type;

    IntegerTranslator(PrimitiveEntry primitive, Class<?> type) {
        this.primitive = primitive;
        this.type = type;
        if(!TgbfUtils.isIntegerNumber(type)) {
            throw new RuntimeException("it`s is not a integer number: " + type);
        }
        final EntryType entryType = this.primitive.getType();
        if(!ALLOWED_TYPES.contains(entryType)) {
            throw new RuntimeException("type of primitive is " + entryType + ", but this translator support only: " + ALLOWED_TYPES);
        }
    }

    @Override
    public int getSize(TranslationContext context, Object message) throws Exception {
        final EntryType type = primitive.getType();
        return TgbfUtils.getTypeSize(type) + 1 /*additional byte for type*/;
    }

    @Override
    public int needSize(TranslationContext context, ByteBuffer buffer) throws Exception {
        return TgbfUtils.getEntryLength(buffer, primitive.getType());
    }

    @Override
    public void to(TranslationContext context, Object message, ByteBuffer buffer) throws Exception {
        final EntryType type = primitive.getType();
        final Number number = (Number) message;
        long longValue = number.longValue();
        TgbfUtils.writeSignedInteger(buffer, longValue, type);
    }

    @Override
    public Object from(TranslationContext context, ByteBuffer buffer) throws Exception {
        final long value = TgbfUtils.readSignedInteger(buffer);
        if(Long.class.equals(type)) {
            return value;
        } else if(Integer.class.equals(type)) {
            return (int)value;
        } else if(Short.class.equals(type)) {
            return (short)value;
        } else if(Byte.class.equals(type)) {
            return (byte)value;
        }
        return value;
    }
}
