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
import java.util.WeakHashMap;

/**
 * cached registry of all classes mappings
 *
 * Created by wayerr on 05.12.14.
 */
public final class PojoManager {
    private static final PojoManager INSTANCE = new PojoManager();

    private final Map<Class<?>, PojoClass> cache = new WeakHashMap<>();

    public PojoManager() {
    }

    public static PojoManager getInstance() {
        return INSTANCE;
    }

    public PojoClass getPojoClass(Class<?> type) {
        synchronized(cache) {
            PojoClass pojoClass = cache.get(type);
            if(pojoClass == null) {
                pojoClass = new PojoClass(type);
                cache.put(type, pojoClass);
            }
            return pojoClass;
        }
    }
}
