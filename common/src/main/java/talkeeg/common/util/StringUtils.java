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

import java.util.Collection;

/**
 * Some string utilities
 *
 * some utils created because String.split has some overhead (see source code of {@link String#split(String)})
 * Created by wayerr on 30.11.14.
 */
public final class StringUtils {

    /**
     * * wrapper of {@link StringUtils#toString(Object)}
     */
    public static final Function<?,String> FUNCTION_TO_STRING = new Function<Object, String>() {
        @Override
        public String apply(Object input) {
            return StringUtils.toString(input);
        }
    };

    public static final class StringifierFunction<T> implements Function<T, String> {
        private final Stringifier<? super T> handler;

        public StringifierFunction(Stringifier<? super T> handler) {
            this.handler = handler;
        }

        @Override
        public String apply(T input) {
            StringBuilder sb = new StringBuilder();
            handler.toString(input, sb);
            return sb.toString();
        }
    }

    /**
     * wrapper of {@link StringUtils#toString(Object)}
     * @see #FUNCTION_TO_STRING
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Function<T, String> getToStringFunction() {
        return (Function<T, String>)FUNCTION_TO_STRING;
    }

    /**
     * split string to target collection
     * @param target
     * @param s
     * @param <T>
     * @return
     */
    public static <T extends Collection<String>> T splitTo(T target, String s, char delimiter) {
        int index = 0;
        while(true) {
            int nextIndex = s.indexOf(delimiter, index);
            if(nextIndex != index && index < s.length()) {
                final String chunk;
                if(nextIndex < 0) {
                    chunk = s.substring(index);
                } else {
                    chunk = s.substring(index, nextIndex);
                }
                target.add(chunk);
            }
            if(nextIndex < 0) {
                break;
            }
            index = nextIndex + 1;
        }
        return target;
    }

    /**
     * if object instance of {@link talkeeg.common.util.Printable } then invoke appropriate print(), else toString()
     * @param object
     * @return
     */
    public static String print(Object object) {
        String text;
        if(object instanceof Printable) {
            StringBuilder sb = new StringBuilder();
            ((Printable)object).print(sb);
            text = sb.toString();
        } else {
            text = String.valueOf(object);
        }
        return text;
    }

    /**
     * if o is null then null else {@link Object#toString()}
     * @param o
     * @return
     */
    public static String toString(Object o) {
        if(o == null) {
            return null;
        }
        return o.toString();
    }
}
