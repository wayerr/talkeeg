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

/**
 * Unsigned 128bit integer data.
 * Created by wayerr on 21.11.14.
 */
public final class Int128 extends BinaryData {
    /**
     * data array length
     */
    private static final int ARR_LEN = 16;
    public static final int STR_LEN = ARR_LEN * 2;

    /**
     * create int128 instance with copy of data
     * @param data
     */
    public Int128(byte[] data) {
        super(data);
    }

    @Override
    protected void check(byte[] data) {
        super.check(data);
        if(data.length != ARR_LEN) {
            throw new IllegalArgumentException("data.length != " + ARR_LEN);
        }
    }

    @Override
    public String toString() {
        return "Int128{" + talkeeg.common.util.Arrays.toHexString(getData()) + '}';
    }

    /**
     * create Int128 instance from hex string
     * @param s
     * @return
     */
    public static Int128 fromString(String s) {
        if(s == null) {
            throw new NullPointerException("string is null");
        }
        if(s.length() != STR_LEN) {
            throw new NullPointerException("string.length is not " + STR_LEN);
        }
        byte[] data = talkeeg.common.util.Arrays.fromHexString(s);
        return new Int128(data);
    }
}
