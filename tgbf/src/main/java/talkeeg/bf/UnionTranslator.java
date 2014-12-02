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
import talkeeg.bf.schema.SchemaEntry;
import talkeeg.bf.schema.Struct;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * translator for union schema entry
 *
 * Created by wayerr on 02.12.14.
 */
public final class UnionTranslator implements Translator {

    private final talkeeg.bf.schema.UnionEntry union;
    private final List<UnionEntry> entries = new ArrayList<>();
    private final Class<?> type;
    private final Bf bf;

    UnionTranslator(TranslatorStaticContext context) {
        this.union = (talkeeg.bf.schema.UnionEntry)context.getEntry();
        this.type = context.getType();
        this.bf = context.getBf();
        for(SchemaEntry schemaEntry : this.union.getChilds()) {
            final Translator translator = context.createTranslator(schemaEntry, this.type);
            final UnionEntry unionEntry = new UnionEntry(schemaEntry, translator);
            entries.add(unionEntry);
        }
    }

    private Translator getTranslatorByMessage(Object message) {
        Preconditions.checkNotNull(message, "message is null");
        final int structId = this.bf.getStructId(message.getClass());
        //maybe we need user map instead of list there?
        for(int i = 0; i < entries.size(); ++i) {
            final UnionEntry entry = entries.get(i);
            final SchemaEntry schema = entry.getSchema();
            if(structId > -1) {
                if(schema instanceof Struct && ((Struct)schema).getId() == structId) {
                    return entry.getTranslator();
                }
            } else {
                throw new RuntimeException("TODO implement resolving uinion non structure types");
            }
        }
        throw new RuntimeException("can not find appropriate entry for " + message);
    }

    private Translator getTranslatorByBuffer(ByteBuffer b) {
        final int position = b.position();
        final byte typeByte = b.get();
        final EntryType entryType = EntryType.getEntryType(typeByte);
        final int structId;
        final Translator translator;
        if(entryType == EntryType.STRUCT) {
            structId = (int)TgbfUtils.readUnsignedInteger(b);
        } else {
            structId = Struct.UNKNOWN_ID;
        }
        b.position(position);//rollback position
        //maybe we need user map instead of list there?
        for(int i = 0; i < entries.size(); ++i) {
            final UnionEntry entry = entries.get(i);
            final SchemaEntry schema = entry.getSchema();
            if(structId > -1) {
                if(schema instanceof Struct && ((Struct)schema).getId() == structId) {
                    return entry.getTranslator();
                }
            } else {
                throw new RuntimeException("TODO implement resolving uinion non structure types");
            }
        }
        throw new RuntimeException("can not find appropriate entry for type:" + entryType + " and structId:" + structId);
    }

    @Override
    public int getSize(TranslationContext context, Object message) throws Exception {
        return getTranslatorByMessage(message).getSize(context, message);
    }


    @Override
    public int needSize(TranslationContext context, ByteBuffer buffer) throws Exception {
        return  getTranslatorByBuffer(buffer).needSize(context, buffer);
    }

    @Override
    public void to(TranslationContext context, Object message, ByteBuffer buffer) throws Exception {
        getTranslatorByMessage(message).to(context, message, buffer);
    }

    @Override
    public Object from(TranslationContext context, ByteBuffer buffer) throws Exception {
        return  getTranslatorByBuffer(buffer).from(context, buffer);
    }

    private static final class UnionEntry {
        private SchemaEntry schema;
        private Translator translator;

        private UnionEntry(SchemaEntry schema, Translator translator) {
            this.schema = schema;
            this.translator = translator;
        }

        private SchemaEntry getSchema() {
            return schema;
        }

        private Translator getTranslator() {
            return translator;
        }
    }
}
