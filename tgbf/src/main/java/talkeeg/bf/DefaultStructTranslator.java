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

import com.google.common.collect.ImmutableList;
import talkeeg.bf.schema.SchemaEntry;
import talkeeg.bf.schema.Struct;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * default translator for structures
 *
 * Created by wayerr on 24.11.14.
 */
final class DefaultStructTranslator implements Translator {

    private final Struct struct;
    private final List<Translator> fieldTranslators;
    private final Bf bf;
    private final Class<?> type;
    private final StructureReader reader;
    private final TranslatorStaticContext factoryContext;

    public DefaultStructTranslator(TranslatorStaticContext context) {
        this.struct = (Struct)context.getEntry();
        this.type = context.getType();
        this.bf = context.getBf();
        this.reader = this.bf.createReader(context);
        this.factoryContext = context;
        ImmutableList.Builder<Translator> builder = ImmutableList.builder();
        for(SchemaEntry fieldEntry: this.struct.getChilds()) {
            final String fieldName = fieldEntry.getFieldName();
            final Class<?> propertyType = this.reader.getType(fieldName);
            builder.add(context.getTranslator(fieldEntry, propertyType));
        }
        this.fieldTranslators = builder.build();
    }

    @Override
    public int getSize(TranslationContext context, Object message) throws Exception {
        int size = 1 /*one byte for type */ + TgbfUtils.getSizeOfPrimitiveEntry(this.struct.getId());
        final List<SchemaEntry> fields = this.struct.getChilds();
        for(int i = 0; i < fields.size(); ++i) {
            SchemaEntry field = fields.get(i);
            Object fieldValue = this.reader.get(message, field.getFieldName());
            Translator translator = getWriteTranslator(context, i, fieldValue);
            size += translator.getSize(context, fieldValue);
        }
        return size;
    }

    protected Translator getWriteTranslator(TranslationContext context, int i, Object fieldValue) {
        Translator translator;
        if(fieldValue == null) {
            translator = NullTranslator.INSTANCE;
        } else {
            translator = this.fieldTranslators.get(i);
        }
        return translator;
    }

    @Override
    public int needSize(TranslationContext context, ByteBuffer buffer) {
        readStructId(buffer);
        return 0;
    }

    @Override
    public void to(TranslationContext context, Object message, ByteBuffer buffer) throws Exception {
        buffer.put(EntryType.STRUCT.getValue());
        TgbfUtils.writeSignedInteger(buffer, struct.getId());
        final List<SchemaEntry> fields = this.struct.getChilds();
        for(int i = 0; i < fields.size(); ++i) {
            final SchemaEntry field = fields.get(i);
            final Object fieldValue = this.reader.get(message, field.getFieldName());
            final Translator translator = getWriteTranslator(context, i, fieldValue);
            translator.to(context, fieldValue, buffer);
        }
    }

    @Override
    public Object from(TranslationContext context, ByteBuffer buffer) throws Exception {
        final int structId = readStructId(buffer);
        if(struct.getId() != structId) {
            throw new RuntimeException("unexpected structure: " + structId + "  when expect: " + struct.getId());
        }
        final StructureBuilder builder = this.factoryContext.createBuilder();
        final List<SchemaEntry> fields = this.struct.getChilds();
        for(int i = 0; i < fields.size(); ++i) {
            final SchemaEntry field = fields.get(i);
            final Translator translator = this.fieldTranslators.get(i);
            final Object value = translator.from(context, buffer);
            builder.set(field.getFieldName(), value);
        }
        return builder.create();
    }

    /**
     * read struct type byte and struct id value
     * @param buffer
     */
    static int readStructId(ByteBuffer buffer) {
        TgbfUtils.readAndCheckType(buffer, EntryType.STRUCT);
        final long rawStructId = TgbfUtils.readUnsignedInteger(buffer);
        if(rawStructId > Integer.MAX_VALUE) {
            throw new RuntimeException("reader structId " + rawStructId + " is greater than integer max value");
        }
        return (int)rawStructId;
    }
}
