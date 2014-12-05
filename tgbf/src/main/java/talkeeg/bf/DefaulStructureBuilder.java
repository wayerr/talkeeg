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

import talkeeg.bf.schema.Struct;
import talkeeg.pojo.PojoClass;
import talkeeg.pojo.PojoManager;

/**
 * Created by wayerr on 25.11.14.
 */
public class DefaulStructureBuilder implements StructureBuilder {
    private final Struct struct;
    private final Class<?> type;
    private final Object intance;
    private final PojoClass pojoClass;

    public DefaulStructureBuilder(Struct struct, Class<?> type) {
        this.struct = struct;
        this.type = type;
        try {
            this.intance = this.type.newInstance();
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        this.pojoClass = PojoManager.getInstance().getPojoClass(this.type);
    }

    @Override
    public void set(String name, Object value) throws Exception {
        this.pojoClass.getProperty(name).set(this.intance, value);
    }

    @Override
    public Object create() {
        return intance;
    }
}
