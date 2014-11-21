/*
 * Copyright (c) 2014, wayerr (radiofun@ya.ru).
 *
 *     This file is part of talkeeg.
 *
 *     talkeeg is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     talkeeg is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with talkeeg.  If not, see <http://www.gnu.org/licenses/>.
 */

package talkeeg.common.util;

import java.util.Arrays;

/**
 * Unsgned 128bit integer value.
 * Created by wayerr on 21.11.14.
 */
public final class Int128 implements Comparable<Int128> {
    /**
     * value array length
     */
    private static final int ARR_LEN = 16;
    private final byte value[];

    public Int128(byte[] value) {
        if(value == null) {
            throw new IllegalArgumentException("value is null");
        }
        if(value.length != ARR_LEN) {
            throw new IllegalArgumentException("value.length != " + ARR_LEN);
        }
        this.value = value.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Int128 int128 = (Int128) o;

        if (!Arrays.equals(value, int128.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public int compareTo(Int128 o) {

        if(this.value == o.value) {
            return 0;
        }
        return 1;
    }
}
