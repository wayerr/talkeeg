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

package talkeeg.common.model;

import talkeeg.bf.StructureBuilder;
import talkeeg.pojo.PojoClass;
import talkeeg.pojo.PojoManager;

/**
 * builder utility for immutable structures
 *
 * Created by wayerr on 01.12.14.
 */
public final class ImmutableStructureBuilder implements StructureBuilder {
    private final BuilderInterface builder;
    private final PojoClass pojoClass;

    public ImmutableStructureBuilder(BuilderInterface builder) {
        this.builder = builder;
        this.pojoClass = PojoManager.getInstance().getPojoClass(this.builder.getClass());
    }

    @Override
    public void set(String name, Object value) throws Exception {
        this.pojoClass.getProperty(name).set(this.builder, value);
    }

    @Override
    public Object create() throws Exception {
        return builder.build();
    }
}
