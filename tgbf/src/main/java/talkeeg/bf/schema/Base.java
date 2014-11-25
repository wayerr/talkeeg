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

import com.google.common.base.MoreObjects;
import talkeeg.bf.EntryType;

import java.util.Set;

/**
 * Created by wayerr on 21.11.14.
 */
public abstract class Base implements SchemaEntry {

    public static abstract class Builder {
        private EntryType type;
        private String fieldName;

        /**
         * persistence type for current entry
         * @return
         */
        public EntryType getType() {
            return type;
        }

        /**
         * persistence type for current entry
         * @param type
         */
        public void setType(EntryType type) {
            this.type = type;
        }

        /**
         * name of field in ascended (parent) entry
         * @return
         */
        public String getFieldName() {
            return fieldName;
        }

        /**
         * name of field in ascended (parent) entry
         * @param fieldName
         */
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public abstract Base build();
    }

    private final EntryType type;
    private final String fieldName;

    protected Base(Set<EntryType> allowedTypes, Builder b) {
        if(b.type == null && allowedTypes.size() == 1) {
            // most simply way to retrieve first element
            b.type = (EntryType) allowedTypes.toArray()[0];
        } else if(!allowedTypes.contains(b.type)) {
            throw new RuntimeException("type of " + getClass().getName() + " must be from " + allowedTypes + ", but now type is " + b.type);
        }
        this.type = b.type;
        this.fieldName = b.fieldName;
    }

    /**
     * name of field in ascended (parent) entry
     * @return
     */
    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public EntryType getType() {
        return type;
    }

    @Override
    public final String toString() {
        MoreObjects.ToStringHelper sb = MoreObjects.toStringHelper(this);
        toString(sb);
        return sb.toString();
    }

    protected void toString(MoreObjects.ToStringHelper sb) {
        sb.add("type", type);
        sb.add("fieldName", fieldName);
    }
}
