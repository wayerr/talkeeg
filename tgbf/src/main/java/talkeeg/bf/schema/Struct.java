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
 * Struct representation <p/>
 *
 * Created by wayerr on 21.11.14.
 */
public class Struct extends Base implements CompositeSchemaEntry {
    public static class Builder extends Base.Builder {
        private final List<SchemaEntry> fields = new ArrayList<>();
        private String id;

        public List<SchemaEntry> getFields() {
            return fields;
        }

        public void setFields(List<SchemaEntry> fields) {
            this.fields.clear();
            if(fields != null) {
                this.fields.addAll(fields);
            }
        }

        public void addField(SchemaEntry field) {
            this.fields.add(field);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Struct build() {
            return new Struct(this);
        }
    }

    private final List<SchemaEntry> fields;
    private final String id;

    private Struct(Builder b) {
        super(EnumSet.of(EntryType.STRUCT), b);
        this.fields = Collections.unmodifiableList(new ArrayList<>(b.fields));
        this.id = b.id;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public List<SchemaEntry> getChilds() {
        return fields;
    }

    public String getId() {
        return id;
    }
}
