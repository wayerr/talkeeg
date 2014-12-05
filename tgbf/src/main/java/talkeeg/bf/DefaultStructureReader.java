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

import com.google.common.primitives.Primitives;
import talkeeg.pojo.PojoClass;
import talkeeg.pojo.PojoManager;

/**
 * default implementation based on reflection access to mapped object properties <p/>
 *
 * Created by wayerr on 25.11.14.
 */
public class DefaultStructureReader implements StructureReader {

    private final Class<?> type;
    private final PojoClass pojoClass;

    public DefaultStructureReader(Class<?> type) {
        this.type = type;
        this.pojoClass = PojoManager.getInstance().getPojoClass(this.type);
    }

    @Override
    public Object get(Object obj, String name) throws Exception {
        return this.pojoClass.getProperty(name).get(obj);
    }

    @Override
    public Class<?> getType(String name) {
        Class<?> type = this.pojoClass.getProperty(name).getType();
        if(type.isPrimitive()) {
            type = Primitives.wrap(type);
        }
        return type;
    }
}
