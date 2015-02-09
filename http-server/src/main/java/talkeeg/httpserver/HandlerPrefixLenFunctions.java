/*
 * Copyright (c) 2015, wayerr (radiofun@ya.ru).
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

package talkeeg.httpserver;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * function of prefix length for handler <p/>
 * Created by wayerr on 09.02.15.
 */
public final class HandlerPrefixLenFunctions {
    private HandlerPrefixLenFunctions() {
    }

    public static Function<String, Integer> from(final String prefix) {
        Preconditions.checkNotNull(prefix, "prefix is null");
        return new Function<String, Integer>() {
            @Override
            public Integer apply(String input) {
                return input.startsWith(prefix)? prefix.length() : -1;
            }
        };
    }
}
