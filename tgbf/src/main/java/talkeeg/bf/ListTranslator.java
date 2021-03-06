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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import talkeeg.bf.schema.ListEntry;
import talkeeg.bf.schema.SchemaEntry;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * translator for {@link talkeeg.bf.schema.ListEntry }
 * Created by wayerr on 02.12.14.
 */
public final class ListTranslator implements Translator {
    private final ListEntry entry;
    private final SchemaEntry itemEntry;
    private final Translator translator;
    private final Bf bf;
    private final TranslatorStaticContext context;
    private final Class<?> type;

    ListTranslator(TranslatorStaticContext staticContext) {
        this.context = staticContext;
        SchemaEntry schemaEntry = staticContext.getEntry();
        this.entry = (ListEntry)schemaEntry;
        this.type = staticContext.getType();
        checkType(this.type);
        this.bf = staticContext.getBf();
        this.itemEntry = this.entry.getItemEntry();
        Preconditions.checkNotNull(this.itemEntry, "itemEntry is null in " + this.entry);
        this.translator = staticContext.createTranslator(this.itemEntry, null);
    }

    @Override
    public int getSize(TranslationContext context, Object message) throws Exception {
        int size = getDataSize(context, message);
        size += TgbfUtils.getMinimalSize(size);
        size += 1 /* byte for type */ + 1 /*TARG*/ + 1 /*for size value type*/;
        return size;
    }

    protected int getDataSize(TranslationContext context, Object message) throws Exception {
        int size = 0;
        final Class<?> messageType = message.getClass();
        checkType(messageType);
        for(Object item : (Collection)message) {
            size += translator.getSize(context, item);
        }
        return size;
    }

    private void checkType(Class<?> messageType) {
        if(messageType == null) {
            throw new NullPointerException("messageType is null");
        }
        if(!(Collection.class.isAssignableFrom(messageType))) {
            throw new RuntimeException("message for ListTranslator is " + messageType + " but must be instance of Collection");
        }
    }

    @Override
    public int needSize(TranslationContext context, ByteBuffer buffer) throws Exception {
        return TgbfUtils.getEntryLength(buffer, EntryType.LIST);
    }

    @Override
    public void to(TranslationContext context, Object message, ByteBuffer buffer) throws Exception {
        buffer.put(EntryType.LIST.getValue());
        buffer.put(EntryType.NULL.getValue());
        TgbfUtils.writeSignedInteger(buffer, getDataSize(context, message));
        checkType(message.getClass());
        for(Object item : (Collection)message) {
            translator.to(context, item, buffer);
        }
    }

    @Override
    public Object from(TranslationContext context, ByteBuffer buffer) throws Exception {
        final int start = buffer.position();
        TgbfUtils.readAndCheckType(buffer, EntryType.LIST);
        TgbfUtils.skipListTarg(buffer);
        final int length = (int)TgbfUtils.readUnsignedInteger(buffer);
        Collection<Object> objects;
        // in future we can allow user to use custom collection factory or collection builder
        if(List.class.isAssignableFrom(this.type)) {
            objects = new ArrayList<>();
        } else if(Set.class.isAssignableFrom(this.type)) {
            objects = new HashSet<>();
        } else {
            throw new RuntimeException("ListTranslator support only List and Set, but type=" + this.type);
        }
        while((buffer.position() - start) < length) {
            Object from = translator.from(context, buffer);
            objects.add(from);
        }
        return objects;
    }
}
