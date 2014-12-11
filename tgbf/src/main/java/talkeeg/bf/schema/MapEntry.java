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

import java.util.EnumSet;

/**
 * entry which represent map of items
 * Created by wayerr on 02.12.14.
 */
public final class MapEntry extends Base {
    public static final class Builder extends Base.Builder {
        private SchemaEntry keyEntry;
        private SchemaEntry valueEntry;

        public SchemaEntry getKeyEntry() {
            return keyEntry;
        }

        public Builder keyEntry(SchemaEntry keyEntry) {
            setKeyEntry(keyEntry);
            return this;
        }

        public void setKeyEntry(SchemaEntry keyEntry) {
            this.keyEntry = keyEntry;
        }

        public SchemaEntry getValueEntry() {
            return valueEntry;
        }

        public Builder valueEntry(SchemaEntry valueEntry) {
            setValueEntry(valueEntry);
            return this;
        }
        public void setValueEntry(SchemaEntry valueEntry) {
            this.valueEntry = valueEntry;
        }

        public MapEntry build() {
            return new MapEntry(this);
        }
    }

    private final SchemaEntry keyEntry;
    private final SchemaEntry valueEntry;

    private MapEntry(Builder b) {
        super(EnumSet.of(EntryType.LIST), b);
        this.keyEntry = b.keyEntry;
        this.valueEntry = b.valueEntry;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * schema entry of keys, maybe null
     *
     * @return
     */
    public SchemaEntry getKeyEntry() {
        return keyEntry;
    }

    /**
     * schema entry of values, maybe null
     * @return
     */
    public SchemaEntry getValueEntry() {
        return valueEntry;
    }
}
