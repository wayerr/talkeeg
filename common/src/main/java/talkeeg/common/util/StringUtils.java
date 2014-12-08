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

import java.util.Collection;

/**
 * Some string utilities
 *
 * some utils created because String.split has some overhead (see source code of {@link String#split(String)})
 * Created by wayerr on 30.11.14.
 */
public final class StringUtils {

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
}
