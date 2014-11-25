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

import com.google.common.collect.ImmutableMap;
import talkeeg.bf.schema.PrimitiveEntry;

import java.util.Map;

/**
 * tool for mapping meta type to appropriate translator
 *
 * Created by wayerr on 25.11.14.
 */
final class MetaTypeResolver {

    private interface TranslatorFactory {
        public Translator create(PrimitiveEntry entry);
    }
    private final Map<String, TranslatorFactory> map;

    MetaTypeResolver() {
        map = ImmutableMap.<String, TranslatorFactory>builder()
                .put("integer", new TranslatorFactory() {
                    @Override
                    public Translator create(PrimitiveEntry entry) {
                        return new IntegerTranslator(entry);
                    }
                })
                .put("blob", new TranslatorFactory() {
                    @Override
                    public Translator create(PrimitiveEntry entry) {
                        return new BlobTranslator(entry, BlobTranslator.ADAPTER_BYTES);
                    }
                })
                .put("string", new TranslatorFactory() {
                    @Override
                    public Translator create(PrimitiveEntry entry) {
                        return new BlobTranslator(entry, BlobTranslator.ADAPTER_STRING);
                    }
                })
                .build();
    }

    Translator createTranslator(PrimitiveEntry entry) {
        final String metaType = entry.getMetaType();
        if(metaType == null) {
            throw new NullPointerException("meta type in " + entry + " is null");
        }
        final TranslatorFactory factory = map.get(metaType);
        if(factory == null) {
            throw new RuntimeException("can not find factory for " + metaType);
        }
        return factory.create(entry);
    }
}
