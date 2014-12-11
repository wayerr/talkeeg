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

import talkeeg.bf.schema.SchemaEntry;
import talkeeg.bf.schema.Struct;

import java.nio.ByteBuffer;

/**
 * generic translator
 *
 * Created by wayerr on 11.12.14.
 */
final class GenericTranslator implements Translator {

    private final TranslatorStaticContext context;

    public GenericTranslator(TranslatorStaticContext context) {
        this.context = context;
    }

    @Override
    public int getSize(TranslationContext context, Object message) throws Exception {
        Translator translator = getWriteTranslator(message);
        return translator.getSize(context, message);
    }

    protected Translator getWriteTranslator(Object message) {
        final Bf bf = this.context.getBf();
        final SchemaEntry schemaEntry = bf.getSchemaEntry(message);
        return this.context.createTranslator(schemaEntry, message.getClass());
    }

    @Override
    public int needSize(TranslationContext context, ByteBuffer buffer) throws Exception {
        return TgbfUtils.getEntryLength(buffer, null);
    }

    @Override
    public void to(TranslationContext context, Object message, ByteBuffer buffer) throws Exception {
        Translator translator = getWriteTranslator(message);
        translator.to(context, message, buffer);
    }

    @Override
    public Object from(TranslationContext context, ByteBuffer buffer) throws Exception {
        final Bf bf = this.context.getBf();
        final SchemaEntry schemaEntry = getSchemaEntry(bf, buffer);
        final Class<?> clazz = bf.getType(schemaEntry);
        Translator translator = this.context.createTranslator(schemaEntry, clazz);
        return translator.from(context, buffer);
    }

    static SchemaEntry getSchemaEntry(Bf bf, ByteBuffer buffer) {
        final int begin = buffer.position();
        final EntryType type = TgbfUtils.readAndCheckType(buffer, null);
        final SchemaEntry schemaEntry;
        if(type == EntryType.STRUCT) {
            final long structId = TgbfUtils.readSignedInteger(buffer);
            if(structId > Integer.MAX_VALUE) {
                throw new RuntimeException("reader structId " + structId + " is greater than integer max value");
            }
            schemaEntry = bf.getStructEntry((int)structId);
        } else if(type == EntryType.LIST) {
            schemaEntry = Bf.LIST_OF_GENERIC;
        } else {
            throw new RuntimeException(type + " as generic entry is not supported ");
        }
        buffer.position(begin);
        return schemaEntry;
    }
}
