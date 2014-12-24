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

import java.util.Objects;

/**
 * equal function utilities
 * Created by wayerr on 24.12.14.
 */
public final class EqualFunctions {
    /**
     * function `==`
     */
    public static final EqualFunction<Object> FUNCTION_IDENTITY = new EqualFunction<Object>() {
        @Override
        public boolean equal(Object left, Object right) {
            return left == right;
        }
    };

    /**
     * function of {@link Objects#equals(Object, Object)}
     */
    public static final EqualFunction<Object> FUNCTION_EQUALITY = new EqualFunction<Object>() {
        @Override
        public boolean equal(Object left, Object right) {
            return Objects.equals(left, right);
        }
    };

    private EqualFunctions() {
    }

    /**
     * @see #FUNCTION_IDENTITY
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> EqualFunction<T> functionIdentity() {
        return (EqualFunction<T>)FUNCTION_IDENTITY;
    }

    /**
     * @see #FUNCTION_EQUALITY
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> EqualFunction<T> functionEquality() {
        return (EqualFunction<T>)FUNCTION_EQUALITY;
    }
}
