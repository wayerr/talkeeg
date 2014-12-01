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

/**
 * translator for primitives
 *
 * Created by wayerr on 25.11.14.
 */
final class IntegerTranslator implements Translator {

    private final PrimitiveEntry primitive;
    private final Class<?> type;

    IntegerTranslator(PrimitiveEntry primitive, Class<?> type) {
        this.primitive = primitive;
        this.type = type;
        if(!TgbfUtils.isIntegerNumber(type)) {
            throw new RuntimeException("it`s is not a integer number: " + type);
        }
    }

    @Override
    public int getSize(TranslationContext context, Object message) throws Exception {
        final EntryType type = primitive.getType();
        return TgbfUtils.getTypeSize(type) + 1 /*additional byte for type*/;
    }

    @Override
    public int needSize(TranslationContext context, ByteBuffer buffer) throws Exception {
        return TgbfUtils.getEntryLength(buffer);
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
