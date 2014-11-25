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

import org.apache.commons.beanutils.PropertyUtils;

/**
 * default implementation based on reflection access to mapped object properties <p/>
 *
 * Created by wayerr on 25.11.14.
 */
public class DefaultStructureReader implements StructureReader {

    private static StructureReader INSTANCE = new DefaultStructureReader();

    private DefaultStructureReader() {
    }

    public static StructureReader getInstance() {
        return INSTANCE;
    }

    @Override
    public Object get(Object obj, String name) throws Exception {
        return PropertyUtils.getProperty(obj, name);
    }
}
