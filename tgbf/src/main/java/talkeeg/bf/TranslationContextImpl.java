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

import talkeeg.bf.schema.PrimitiveEntry;
import talkeeg.bf.schema.SchemaEntry;
import talkeeg.bf.schema.Struct;
import talkeeg.bf.schema.StructNavigator;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wayerr on 21.11.14.
 */
final class TranslationContextImpl implements TranslationContext {

    private final Struct message;
    private final Map<Integer, Translator> structs = new TreeMap<>();
    private final StructNavigator navigator;
    private final Bf bf;
    private final StructureReader dafaultReader = DefaultStructureReader.getInstance();

    TranslationContextImpl(Bf bf, Struct message) {
        this.bf = bf;
        this.message = message;
        this.navigator = new StructNavigator(this.message);
    }

    @Override
    public Translator getTranslator(final SchemaEntry schemaEntry) {
        if(schemaEntry == null) {
            throw  new NullPointerException("schemaEntry is null");
        }
        Translator translator = null;
        if(schemaEntry instanceof PrimitiveEntry) {
            translator = this.bf.getMetaTypeResolver().createTranslator((PrimitiveEntry)schemaEntry);
        } else if(schemaEntry instanceof Struct) {
            final Struct struct = (Struct) schemaEntry;
            translator = structs.get(struct.getId());
            if(translator == null) {
                // create default translator
                translator = new DefaultStructTranslator(struct);
                structs.put(struct.getId(), translator);
            }
        }
        if(translator == null) {
            throw new RuntimeException("Can not find translator for schemaEntry=" + schemaEntry);
        }
        return translator;
    }

    @Override
    public StructureBuilder createBuilder(Struct struct) {
        return bf.createBulder(struct);
    }

    @Override
    public StructureReader getReader(Struct entry) {
        return dafaultReader;
    }
}
