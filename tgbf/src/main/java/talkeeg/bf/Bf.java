
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
import com.google.common.base.Supplier;
import talkeeg.bf.schema.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Message writer. <p/>
 * Not thread safe. <p/>
 * Created by rad on 17.11.14.
 */
public final class Bf {

    /**
     * constanc sche,a entry for list of generic
     */
    static final ListEntry LIST_OF_GENERIC = ListEntry.builder()
      .itemEntry(GenericEntry.builder().build())
      .build();
    static final MapEntry MAP_OF_GENERIC = MapEntry.builder()
      .keyEntry(GenericEntry.builder().build())
      .valueEntry(GenericEntry.builder().build())
      .build();

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
            final int id = getStructIdByAnnotaion(type);
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
        final SchemaEntry message = getSchemaEntry(obj);
        final TranslationContextImpl context = new TranslationContextImpl(this, message);
        final TranslatorStaticContext staticContext = new TranslatorStaticContext(this, null, message, obj.getClass());
        final Translator translator = createTranslator(staticContext);
        final int size = translator.getSize(context, obj);
        ByteBuffer buffer = ByteBuffer.allocate(size);
        translator.to(context, obj, buffer);
        buffer.rewind();
        return buffer;
    }

    SchemaEntry getSchemaEntry(Object obj) {
        final SchemaEntry message;
        if(obj instanceof List) {
            message = LIST_OF_GENERIC;
        } else {
            final int messageId = getStructId(obj);
            message = getStructEntry(messageId);
            if(message == null) {
                throw new RuntimeException("Can not find message for structId=" + messageId);
            }
        }
        return message;
    }

    Struct getStructEntry(int structId) {
        return this.schema.getMessage(structId);
    }

    private static int getStructId(Object obj) {
        final Class<?> clazz = obj.getClass();

        // if class is struct
        return getStructIdByAnnotaion(clazz);
    }

    /**
     * structid defined by {@link talkeeg.bf.StructInfo }
     * @param clazz
     * @return {@link Struct#UNKNOWN_ID } if no annotation
     */
    private static int getStructIdByAnnotaion(Class<?> clazz) {
        final StructInfo si = clazz.getAnnotation(StructInfo.class);
        if(si == null) {
            return Struct.UNKNOWN_ID;
        }
        return si.id();
    }

    /**
     * structure id which mapped to specified class
     * @param type
     * @return -1 if no mapped struct id
     */
    public int getStructId(Class<?> type) {
        //method can use internal registry for resolving id of structure, therefore it must be non static
        return getStructIdByAnnotaion(type);
    }

    public Object read(ByteBuffer buffer) throws Exception {
        //after reading struct header current buffer position must remain untouched
        final SchemaEntry message = GenericTranslator.getSchemaEntry(this, buffer.asReadOnlyBuffer());
        final TranslationContextImpl context = new TranslationContextImpl(this, message);
        final Translator translator = createTranslator(message);
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
     * @param schemaEntry
     * @return a class or throw exception if no mapping
     */
    public Class<?> getType(SchemaEntry schemaEntry) {
        final Class<?> type;
        if(schemaEntry instanceof Struct) {
            final TypeData typeData = getTypeData((Struct)schemaEntry);
            type = typeData.getType();
        } else if(schemaEntry instanceof PrimitiveEntry) {
            type = ((PrimitiveEntry)schemaEntry).getJavaType();
        } else if(schemaEntry instanceof ListEntry) {
            type = ArrayList.class;
        } else {
            type = null;
        }
        return type;
    }

    private TypeData getTypeData(Struct struct) {
        final TypeData typeData = types.get(struct.getId());
        if(typeData == null) {
            throw new RuntimeException("no types mapped on specified structId: " + struct.getId());
        }
        return typeData;
    }

    /**
     * translator for specified schema entry (allow only structs and messages) <p/>
     * If context does not have appropriate translator, then error will be thrown
     * @param schemaEntry
     * @return translator
     */
    public Translator createTranslator(final SchemaEntry schemaEntry) {
        if(schemaEntry == null) {
            throw  new NullPointerException("schemaEntry is null");
        }
        final Class<?> type = getType(schemaEntry);
        final TranslatorStaticContext factoryContext = new TranslatorStaticContext(this, null, schemaEntry, type);
        final Translator translator = createTranslator(factoryContext);
        return translator;
    }

    Translator createTranslator(TranslatorStaticContext staticContext) {
        Translator translator = null;
        SchemaEntry schemaEntry = staticContext.getEntry();
        if(schemaEntry instanceof PrimitiveEntry) {
            translator = resolver.createTranslator(staticContext);
        } else if(schemaEntry instanceof Struct) {
            final Struct struct = (Struct) schemaEntry;
            translator = structs.get(struct.getId());
            if(translator == null) {
                // create default translator
                translator = new DefaultStructTranslator(staticContext);
                structs.put(struct.getId(), translator);
            }
        } else if(schemaEntry instanceof UnionEntry) {
            translator = new UnionTranslator(staticContext);
        } else if(schemaEntry instanceof ListEntry) {
            translator = new ListTranslator(staticContext);
        } else if(schemaEntry instanceof MapEntry) {
            translator = new MapTranslator(staticContext);
        } else if(schemaEntry instanceof GenericEntry) {
            translator = new GenericTranslator(staticContext);
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
