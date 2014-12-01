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

import com.google.common.base.Preconditions;
import talkeeg.bf.schema.PrimitiveEntry;

import java.nio.ByteBuffer;

/**
 * translator wrapper over {@link talkeeg.bf.IntegerTranslator } for saving enums
 *
 * Created by wayerr on 01.12.14.
 */
public class EnumTranslator implements Translator {
    private final IntegerTranslator translator;
    private final Class<Enum<?>> type;
    private Enum<?>[] enumConstants;

    public EnumTranslator(PrimitiveEntry entry, Class<Enum<?>> type) {
        this.translator = new IntegerTranslator(entry, Integer.class);
        this.type = type;
        this.enumConstants = this.type.getEnumConstants();
        Preconditions.checkNotNull(this.enumConstants, "type.enumConstants is null");
    }

    @Override
    public int getSize(TranslationContext context, Object message) throws Exception {
        return translator.getSize(context, message);
    }

    @Override
    public int needSize(TranslationContext context, ByteBuffer buffer) throws Exception {
        return translator.needSize(context, buffer);
    }

    @Override
    public void to(TranslationContext context, Object message, ByteBuffer buffer) throws Exception {
        Enum<?> enumeration = (Enum<?>) message;
        translator.to(context, enumeration.ordinal(), buffer);
    }

    @Override
    public Object from(TranslationContext context, ByteBuffer buffer) throws Exception {
        Number from = (Number)translator.from(context, buffer);
        return enumConstants[from.intValue()];
    }
}
