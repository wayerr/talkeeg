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
 * translator for boolean values
 *
 * Created by wayerr on 02.12.14.
 */
class BooleanTranslator implements Translator {
    private final IntegerTranslator translator;
    private final PrimitiveEntry entry;

    BooleanTranslator(PrimitiveEntry entry) {
        this.entry = entry;
        this.translator = new IntegerTranslator(this.entry, Long.class);
    }

    @Override
    public int getSize(TranslationContext context, Object message) throws Exception {
        return translator.getSize(context, toInt(message));
    }

    @Override
    public int needSize(TranslationContext context, ByteBuffer buffer) throws Exception {
        return translator.needSize(context, buffer);
    }

    @Override
    public void to(TranslationContext context, Object message, ByteBuffer buffer) throws Exception {
        int intValue = toInt(message);
        translator.to(context, intValue, buffer);
    }

    protected int toInt(Object message) {
        final Boolean boolValue = (Boolean)message;
        return boolValue ? 1 : 0;
    }

    @Override
    public Object from(TranslationContext context, ByteBuffer buffer) throws Exception {
        Number intValue = (Number)translator.from(context, buffer);
        return intValue.intValue() != 0;
    }
}
