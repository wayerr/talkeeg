
/*
 * Copyright (c) 2014 wayerr (radiofun@ya.ru).
 *
 *     This file is part of talkeeg.
 *
 *     talkeeg is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     talkeeg is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with talkeeg.  If not, see <http://www.gnu.org/licenses/>.
 */

package talkeeg.bf;

import com.google.common.base.Preconditions;
import talkeeg.bf.schema.PrimitiveEntry;
import talkeeg.bf.schema.Schema;
import talkeeg.bf.schema.SchemaEntry;
import talkeeg.bf.schema.Struct;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Message writer. <p/>
 * Not thread safe. <p/>
 * Created by rad on 17.11.14.
 */
public final class Bf {

    public static class Builder {
        private final Map<Integer, TypeData.Builder> types = new TreeMap<>();
        private Schema schema;
        private MetaTypeResolver resolver = MetaTypeResolver.DEFAULT;

        public Builder putTypes(Class<?> ... types) {
            for(Class<?> type : types) {
                putType(type, null);
            }
            return this;
        }

        /**
         * register type
         * @param type
         * @param structureBuilder builders factory of objects this type, allow null value
         * @return this
         */
        public Builder putType(Class<?> type, Supplier<StructureBuilder> structureBuilder) {
            final int id = getStructId(type);
            TypeData.Builder source = this.types.get(id);
            if(source == null) {
                source = new TypeData.Builder(id, type);
                this.types.put(id, source);
            } else {
                Class<?> oldType = source.getType();
                if(oldType != null && oldType != type) {
                    throw new RuntimeException("Conflict, two types with equal id: " + oldType + ", " + type);
                }
            }
            source.setBuilderFactory(structureBuilder);
            return this;
        }

        public Schema getSchema() {
            return schema;
        }

        public Builder schema(Schema schema) {
            setSchema(schema);
            return this;
        }

        public void setSchema(Schema schema) {
            this.schema = schema;
        }

        public MetaTypeResolver getResolver() {
            return resolver;
        }

        public Builder resolver(MetaTypeResolver resolver) {
            setResolver(resolver);
            return this;
        }

        public void setResolver(MetaTypeResolver resolver) {
            this.resolver = resolver;
        }

        public Bf build() {
            return new Bf(this);
        }
    }

    private final Schema schema;
    private final Map<Integer, TypeData> types = new TreeMap<>();
    private final MetaTypeResolver resolver;
    private final Map<Integer, Translator> structs = new TreeMap<>();

    private Bf(Builder b) {
        this.resolver = b.resolver;
        this.schema = b.schema;
        Preconditions.checkNotNull(this.resolver, "resolver is null");
        Preconditions.checkNotNull(this.schema, "schema is null");
        for(TypeData.Builder typeDataBuilder: b.types.values()) {
            TypeData typeData = typeDataBuilder.build();
            this.types.put(typeData.getId(), typeData);
        }
    }

    public static Builder build() {
        return new Builder();
    }

    /**
     * Write object to it`s binary representation.
     * @param obj
     * @throws IOException
     */
    public ByteBuffer write(Object obj) throws Exception {

        final int messageId = getStructId(obj);
        Struct message = schema.getMessage(messageId);
        if(message == null) {
            throw new RuntimeException("Can not find message for structId=" + messageId);
        }
        final TranslationContextImpl context = new TranslationContextImpl(this, message);
        final Translator translator = getTranslator(message);
        final int size = translator.getSize(context, obj);
        ByteBuffer buffer = ByteBuffer.allocate(size);
        translator.to(context, obj, buffer);
        buffer.rewind();
        return buffer;
    }

    private static int getStructId(Object obj) {
        final Class<?> clazz = obj.getClass();

        // if class is struct
        return getStructId(clazz);
    }

    private static int getStructId(Class<?> clazz) {
        final StructInfo si = clazz.getAnnotation(StructInfo.class);
        if(si == null) {
            throw new RuntimeException("can not get translator for " + clazz + " it class need " + StructInfo.class.getName() + " annotation.");
        }
        return si.id();
    }

    public Object read(ByteBuffer buffer) throws Exception {
        //after reading struct header current buffer position must remain untouched
        final int structId = DefaultStructTranslator.readStructId(buffer.asReadOnlyBuffer());
        final Struct message = schema.getMessage(structId);
        final TranslationContextImpl context = new TranslationContextImpl(this, message);
        final Translator translator = getTranslator(message);
        return translator.from(context, buffer);
    }

    /**
     * create builder instance for object mapped to specified entry
     * @param struct
     * @return
     */
    public StructureBuilder createBuilder(Struct struct, Class<?> type) {
        final TypeData typeData = getTypeData(struct);
        final Supplier<StructureBuilder> factory = typeData.getBuilderFactory();
        if(factory != null) {
            return factory.get();
        }
        return new DefaulStructureBuilder(struct, type);
    }

    /**
     * structure representation type
     * @param struct
     * @return a class or throw exception if no mapping
     */
    public Class<?> getType(Struct struct) {
        final TypeData typeData = getTypeData(struct);
        final Class<?> type = typeData.getType();
        return type;
    }

    private TypeData getTypeData(Struct struct) {
        final TypeData typeData = types.get(struct.getId());
        if(typeData == null) {
            throw new RuntimeException("no types mapped on specified structId: " + struct.getId());
        }
        return typeData;
    }

    MetaTypeResolver getMetaTypeResolver() {
        return resolver;
    }


    /**
     * translator for specified schema entry (allow only structs and messages) <p/>
     * If context does not have appropriate translator, then error will be thrown
     * @param schemaEntry
     * @return translator
     */
    public Translator getTranslator(final SchemaEntry schemaEntry) {
        if(schemaEntry == null) {
            throw  new NullPointerException("schemaEntry is null");
        }
        final Class<?> type = getType((Struct)schemaEntry);
        final TranslatorStaticContext factoryContext = new TranslatorStaticContext(this, null, schemaEntry, type);
        final Translator translator = getTranslator(factoryContext);
        return translator;
    }

    Translator getTranslator(TranslatorStaticContext factoryContext) {
        Translator translator = null;
        SchemaEntry schemaEntry = factoryContext.getEntry();
        if(schemaEntry instanceof PrimitiveEntry) {
            translator = resolver.createTranslator(factoryContext);
        } else if(schemaEntry instanceof Struct) {
            final Struct struct = (Struct) schemaEntry;
            translator = structs.get(struct.getId());
            if(translator == null) {
                // create default translator
                translator = new DefaultStructTranslator(factoryContext);
                structs.put(struct.getId(), translator);
            }
        }
        if(translator == null) {
            throw new RuntimeException("Can not find translator for schemaEntry=" + schemaEntry);
        }
        return translator;
    }

    /**
     * predefined reader for structure
     * @param context
     * @return
     */
    public StructureReader createReader(TranslatorStaticContext context) {
        return new DefaultStructureReader(context.getType());
    }
}
