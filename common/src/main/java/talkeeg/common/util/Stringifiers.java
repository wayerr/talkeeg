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

package talkeeg.common.util;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * a various object to string converters
 *
 * Created by wayerr on 23.12.14.
 */
public final class Stringifiers implements Stringifier<Object> {
    private final Map<Class<?>, Stringifier<?>> stringifiers;

    public Stringifiers(Map<Class<?>, Stringifier<?>> stringifiers) {
        this.stringifiers = ImmutableMap.copyOf(stringifiers);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void toString(Object src, StringBuilder sb) {
        if(src == null) {
            return;
        }
        if(src instanceof CharSequence) {
            sb.append(src);
        }
        final Class<?> srcClass = src.getClass();

        final Stringifier<?> stringifier = findHandler(srcClass);
        if(stringifier != null) {
            ((Stringifier<Object>)stringifier).toString(src, sb);
        } else {
            final String str = StringUtils.toString(src);
            if(str != null) {
                sb.append(str);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> Stringifier<? super T> findHandler(Class<T> type) {
        Stringifier<? super T> stringifier = null;
        Class<?> currentClass = type;
        while(stringifier == null && currentClass != null) {
            stringifier = (Stringifier<? super T>)this.stringifiers.get(currentClass);
            currentClass = currentClass.getSuperclass();
        }
        return stringifier;
    }

    public <T> Function<T, String> getToStringFunction(Class<T> type) {
        final Stringifier<? super T> handler = findHandler(type);
        if(handler != null) {
            return new StringUtils.StringifierFunction<>(handler);
        }
        return StringUtils.getToStringFunction();
    }


}
