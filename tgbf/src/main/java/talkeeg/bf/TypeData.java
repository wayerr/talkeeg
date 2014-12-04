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

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

/**
 * source for type specific data
 *
 * Created by wayerr on 01.12.14.
 */
final class TypeData {
    public static final class Builder {
        private final int id;
        private final Class<?> type;
        private Supplier<StructureBuilder> builderFactory;

        Builder(int id, Class<?> type) {
            this.id = id;
            this.type =type;
        }

        public int getId() {
            return id;
        }

        public Class<?> getType() {
            return type;
        }

        public Supplier<StructureBuilder> getBuilderFactory() {
            return builderFactory;
        }

        public void setBuilderFactory(Supplier<StructureBuilder> builderFactory) {
            this.builderFactory = builderFactory;
        }

        public TypeData build() {
            return new TypeData(this);
        }
    }

    private final int id;
    private final Class<?> type;
    private final Supplier<StructureBuilder> builderFactory;

    TypeData(Builder b) {
        this.id = b.id;
        this.type = b.type;
        Preconditions.checkNotNull(this.type, "type is null");
        this.builderFactory = b.builderFactory;
    }

    int getId() {
        return id;
    }

    Class<?> getType() {
        return type;
    }

    Supplier<StructureBuilder> getBuilderFactory() {
        return builderFactory;
    }
}
