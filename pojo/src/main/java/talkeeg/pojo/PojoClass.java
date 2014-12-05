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

package talkeeg.pojo;

import java.util.Map;
import java.util.Set;

/**
 * representation of java pojo class with it`s properties
 *
 * Created by wayerr on 05.12.14.
 */
public final class PojoClass {
    private final Class<?> type;
    private final Map<String, Property> properties;

    public PojoClass(Class<?> type) {
        this.type = type;
        this.properties = PojoUtils.load(this.type);
    }

    /**
     * type of pojo
     * @return
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * type property
     * @param name
     * @return
     */
    public Property getProperty(String name) {
        return properties.get(name);
    }

    /**
     * unmodifiable set of all type properties
     * @return
     */
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    /**
     * unmodifiable map of properties
     * @return
     */
    public Map<String, Property> getProperties() {
        return properties;
    }
}
