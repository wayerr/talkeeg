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

import com.google.common.io.BaseEncoding;

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
        byte[] thisVal = this.value;
        byte[] othVal = o.value;
        if(thisVal == othVal) {
            return 0;
        }
        int lenDiff = Integer.compare(thisVal.length, othVal.length);
        if(lenDiff != 0) {
            return lenDiff;
        }
        for(int i = 0; i < thisVal.length; ++i) {
            int res = Byte.compare(thisVal[i], othVal[i]);
            if(res != 0) {
                return res;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Int128{" + BaseEncoding.base16().encode(value) + '}';
    }
}
