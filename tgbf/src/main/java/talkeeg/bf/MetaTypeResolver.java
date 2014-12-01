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
import com.google.common.collect.ImmutableMap;
import talkeeg.bf.schema.PrimitiveEntry;
import java.util.HashMap;
import java.util.Map;

/**
 * tool for mapping meta type to appropriate translator
 * @see talkeeg.bf.MetaTypes
 * Created by wayerr on 25.11.14.
 */
public final class MetaTypeResolver {

    public static final class Builder {
        private final Map<String, Function<TranslatorStaticContext, Translator>> map = new HashMap<>();
        private MetaTypeResolver parent = MetaTypeResolver.DEFAULT;

        /**
         * a map between string name of metatype and its translator factory
         * @see talkeeg.bf.MetaTypes
         * @return
         */
        public Map<String, Function<TranslatorStaticContext, Translator>> getFactories() {
            return map;
        }

        /**
         * a map between string name of metatype and its translator factory
         * @see talkeeg.bf.MetaTypes
         * @param map
         */
        public void setFactories(Map<String, Function<TranslatorStaticContext, Translator>> map) {
            this.map.clear();
            this.map.putAll(map);
        }

        /**
         * register translator factory for string name of metatype
         * @see talkeeg.bf.MetaTypes
         * @param name
         * @param factory
         * @return
         */
        public Builder putFactory(String name, Function<TranslatorStaticContext, Translator> factory) {
            this.map.put(name, factory);
            return this;
        }

        /**
         * parent which provide default registry
         * * by default it`s has value from MetaTypeResolver.DEFAULT
         * @see talkeeg.bf.MetaTypeResolver#DEFAULT
         * @return
         */
        public MetaTypeResolver getParent() {
            return parent;
        }

        public Builder parent(MetaTypeResolver parent) {
            setParent(parent);
            return this;
        }

        /**
         * parent which provide default registry
         * by default it`s has value from MetaTypeResolver.DEFAULT
         * @see talkeeg.bf.MetaTypeResolver#DEFAULT
         * @param parent
         */
        public void setParent(MetaTypeResolver parent) {
            this.parent = parent;
        }

        public MetaTypeResolver build() {
            return new MetaTypeResolver(this);
        }
    }

    /**
     * default instance of meta type resolver
     */
    public static final MetaTypeResolver DEFAULT = createDefaultResolver();

    private final MetaTypeResolver parent;
    private final Map<String, Function<TranslatorStaticContext, Translator>> map;

    private MetaTypeResolver(Builder b) {
        this.map = ImmutableMap.copyOf(b.map);
        this.parent = b.parent;
    }

    /**
     * create new Builder for meta type resolver
     * @return
     */
    public static final Builder builder() {
        return new Builder();
    }

    Translator createTranslator(TranslatorStaticContext context) {
        PrimitiveEntry entry = (PrimitiveEntry)context.getEntry();
        final String metaType = entry.getMetaType();
        if(metaType == null) {
            throw new NullPointerException("meta type in " + entry + " is null");
        }
        final Function<TranslatorStaticContext, Translator> factory = getTranslatorFactory(metaType);
        if(factory == null) {
            throw new RuntimeException("can not find factory for " + metaType);
        }
        return factory.apply(context);
    }

    private Function<TranslatorStaticContext, Translator> getTranslatorFactory(String metaType) {
        Function<TranslatorStaticContext, Translator> factory = map.get(metaType);
        if(factory != null || this.parent == null) {
            return factory;
        }
        return this.parent.getTranslatorFactory(metaType);
    }

    private static MetaTypeResolver createDefaultResolver() {
        return builder()
                .parent(null)
                .putFactory(MetaTypes.INTEGER, new Function<TranslatorStaticContext, Translator>() {
                    @Override
                    public Translator apply(TranslatorStaticContext context) {
                        final Class<?> type = context.getType();
                        final PrimitiveEntry entry = (PrimitiveEntry)context.getEntry();
                        if(Number.class.isAssignableFrom(type)) {
                            return new IntegerTranslator(entry);
                        } else if(Enum.class.isAssignableFrom(type)) {
                            return new EnumTranslator(entry, (Class<Enum<?>>)type);
                        }
                        throw new RuntimeException("can not create translator between " + entry.getType() + " and " + type);
                    }
                })
                .putFactory(MetaTypes.BLOB, new Function<TranslatorStaticContext, Translator>() {
                    @Override
                    public Translator apply(TranslatorStaticContext context) {
                        return new BlobTranslator((PrimitiveEntry)context.getEntry(), BlobTranslator.ADAPTER_BYTES);
                    }
                })
                .putFactory(MetaTypes.STRING, new Function<TranslatorStaticContext, Translator>() {
                    @Override
                    public Translator apply(TranslatorStaticContext context) {
                        return new BlobTranslator((PrimitiveEntry)context.getEntry(), BlobTranslator.ADAPTER_STRING);
                    }
                })
                .putFactory(MetaTypes.ID, new Function<TranslatorStaticContext, Translator>() {
                    @Override
                    public Translator apply(TranslatorStaticContext context) {
                        return new IdTranslator();
                    }
                })
                .build();
    }
}
