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

/**
 * static context for translators, it used for creating translator instances for each schema entry
 * Created by wayerr on 01.12.14.
 */
public final class TranslatorStaticContext {
    private final TranslatorStaticContext parent;
    private final Bf bf;
    private final SchemaEntry entry;
    private final Class<?> type;

    TranslatorStaticContext(Bf bf, TranslatorStaticContext parent, SchemaEntry entry, Class<?> type) {
        this.bf = bf;
        this.parent = parent;
        this.entry = entry;
        this.type = type;
    }

    public Bf getBf() {
        return bf;
    }

    public TranslatorStaticContext getParent() {
        return parent;
    }

    public SchemaEntry getEntry() {
        return entry;
    }

    /**
     * create translator for specified field of current structure
     * @param fieldEntry
     * @return
     */
    public Translator getTranslator(SchemaEntry fieldEntry, Class<?> type) {
        final TranslatorStaticContext context = new TranslatorStaticContext(bf, this, fieldEntry, type);
        return bf.getTranslator(context);
    }

    public Class<?> getType() {
        return type;
    }

    public StructureBuilder createBuilder() {
        return this.bf.createBuilder((Struct)this.entry, this.type);
    }
}
