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

package talkeeg.bf.schema;

import talkeeg.bf.EntryType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by wayerr on 02.12.14.
 */
public final class UnionEntry extends Base implements CompositeSchemaEntry {
    public static class Builder extends Base.Builder {
        private final List<SchemaEntry> entries = new ArrayList<>();

        public List<SchemaEntry> getEntries() {
            return entries;
        }

        public void setEntries(List<SchemaEntry> entries) {
            this.entries.clear();
            if(entries != null) {
                this.entries.addAll(entries);
            }
        }

        public void addEntry(SchemaEntry field) {
            this.entries.add(field);
        }

        public UnionEntry build() {
            return new UnionEntry(this);
        }
    }

    private final List<SchemaEntry> entries;

    private UnionEntry(Builder b) {
        super(EnumSet.of(EntryType.STRUCT), b);
        this.entries = Collections.unmodifiableList(new ArrayList<>(b.entries));
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public List<SchemaEntry> getChilds() {
        return entries;
    }
}
