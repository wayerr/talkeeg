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
 * entry which represent sequence of items
 * Created by wayerr on 24.11.14.
 */
public final class ListEntry extends Base {
    public static final class Builder extends Base.Builder {
        private SchemaEntry itemEntry;

        public SchemaEntry getItemEntry() {
            return itemEntry;
        }

        public Builder itemEntry(SchemaEntry itemEntry) {
            setItemEntry(itemEntry);
            return this;
        }

        public void setItemEntry(SchemaEntry itemEntry) {
            this.itemEntry = itemEntry;
        }

        public ListEntry build() {
            return new ListEntry(this);
        }
    }

    private final SchemaEntry itemEntry;

    private ListEntry(Builder b) {
        super(EnumSet.of(EntryType.LIST), b);
        this.itemEntry = b.itemEntry;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * schema entry of list item, maybe null
     * @return
     */
    public SchemaEntry getItemEntry() {
        return itemEntry;
    }
}
