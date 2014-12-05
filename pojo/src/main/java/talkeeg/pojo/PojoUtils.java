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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * some utilities
 *
 * Created by wayerr on 05.12.14.
 */
public final class PojoUtils {

    private static final String PREFIX_SET = "set";
    private static final int PREFIX_SET_LENGTH = PREFIX_SET.length();
    private static final String PREFIX_GET = "get";
    private static final int PREFIX_GET_LENGTH = PREFIX_GET.length();
    private static final String PREFIX_IS = "is";
    private static final int PREFIX_IS_LENGTH = PREFIX_IS.length();

    /**
     * unmodifiable map with class properties
     * @param type
     * @return
     */
    static Map<String, Property> load(Class<?> type) {
        Method[] methods = type.getMethods();
        Map<String, MethodsProperty.Builder> map = new TreeMap<>();
        for(Method method: methods) {
            final String name = method.getName();
            final String propertyName;
            boolean getter = true;
            if(name.startsWith(PREFIX_GET)) {
                propertyName = decapitalize(name.substring(PREFIX_GET_LENGTH));
            } else if(name.startsWith(PREFIX_IS)) {
                propertyName = decapitalize(name.substring(PREFIX_IS_LENGTH));
            } else if(name.startsWith(PREFIX_SET)) {
                propertyName = decapitalize(name.substring(PREFIX_SET_LENGTH));
                getter = false;
            } else {
                continue;
            }
            if(propertyName.isEmpty()) {
                //it`s maybe if name = "is", "get", "set"
                continue;
            }
            MethodsProperty.Builder property = map.get(propertyName);
            if(property == null) {
                property = MethodsProperty.build(propertyName);
                map.put(propertyName, property);
            }
            if(getter) {
                property.setGetter(method);
            } else {
                property.setSetter(method);
            }
        }

        // build properties
        Map<String, Property> propertyMap  = new TreeMap<>();
        for(MethodsProperty.Builder builder: map.values()) {
            final MethodsProperty property = builder.build();
            propertyMap.put(property.getName(), property);
        }
        return Collections.unmodifiableMap(propertyMap);
    }

    /**
     * change first letter to lower case
     * @param string
     * @return
     */
    public static String decapitalize(String string) {
        if(string == null || string.isEmpty()) {
            return string;
        }
        final char chars[] = string.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
}
