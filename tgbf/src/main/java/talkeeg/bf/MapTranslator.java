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

import talkeeg.bf.schema.MapEntry;
import talkeeg.bf.schema.SchemaEntry;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * translator for {@link talkeeg.bf.schema.MapEntry }
 * Created by wayerr on 02.12.14.
 */
final class MapTranslator implements  Translator {

    private final MapEntry entry;
    private final SchemaEntry keyEntry;
    private final Translator keyTranslator;
    private final SchemaEntry valueEntry;
    private final Translator valueTranslator;
    private final Bf bf;
    private final TranslatorStaticContext context;
    private final Class<?> type;

    MapTranslator(TranslatorStaticContext staticContext) {
        this.context = staticContext;
        SchemaEntry schemaEntry = staticContext.getEntry();
        this.entry = (MapEntry)schemaEntry;
        this.type = staticContext.getType();
        this.bf = staticContext.getBf();
        this.keyEntry = this.entry.getKeyEntry();
        this.keyTranslator = staticContext.createTranslator(this.keyEntry, null);
        this.valueEntry = this.entry.getValueEntry();
        this.valueTranslator = staticContext.createTranslator(this.valueEntry, null);
    }

    @Override
    public int getSize(TranslationContext context, Object message) throws Exception {
        int size = 1 /* byte for type */;
        for(Map.Entry<?, ?> item : ((Map<?, ?>)message).entrySet()) {
            Object key = item.getKey();
            Object value = item.getValue();
            size += keyTranslator.getSize(context, key);
            size += valueTranslator.getSize(context, value);
        }
        return size;
    }

    @Override
    public int needSize(TranslationContext context, ByteBuffer buffer) throws Exception {
        return TgbfUtils.getEntryLength(buffer, EntryType.LIST);
    }

    @Override
    public void to(TranslationContext context, Object message, ByteBuffer buffer) throws Exception {
        for(Map.Entry<?, ?> item : ((Map<?, ?>)message).entrySet()) {
            Object key = item.getKey();
            Object value = item.getValue();
            keyTranslator.to(context, key, buffer);
            valueTranslator.to(context, value, buffer);
        }
    }

    @Override
    public Object from(TranslationContext context, ByteBuffer buffer) throws Exception {
        final int start = buffer.position();
        TgbfUtils.readAndCheckType(buffer, EntryType.LIST);
        TgbfUtils.skipListTarg(buffer);
        final int length = (int)TgbfUtils.readUnsignedInteger(buffer);
        final Map<Object, Object> map = new HashMap<>();
        while((buffer.position() - start) < length) {
            Object key = keyTranslator.from(context, buffer);
            Object value = valueTranslator.from(context, buffer);
            map.put(key, value);
        }
        return map;
    }
}

