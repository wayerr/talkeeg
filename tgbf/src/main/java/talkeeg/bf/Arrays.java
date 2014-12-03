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

package talkeeg.bf;

import com.google.common.io.BaseEncoding;

import java.nio.ByteBuffer;

/**
 * Some common utilities for arrays
 * Created by wayerr on 21.11.14.
 */
public final class Arrays {
    private Arrays() {
    }

    /**
     * Compare two byte arrays
     * @param thisVal
     * @param othVal
     * @return
     */
    public static int compare(byte[] thisVal, byte[] othVal) {
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

    /**
     * return hex representation of byte array
     * @param value
     * @return
     */
    public static String toHexString(byte[] value) {
        return BaseEncoding.base16().encode(value);
    }

    /**
     * return hex representation of bytebuffer data
     * @param value
     * @return
     */
    public static String toHexString(ByteBuffer value) {
        final int position = value.position();
        return BaseEncoding.base16().encode(value.array(), position, value.limit() - position);
    }

    /**
     * array with buffer data from position to limit
     * @param buffer
     * @return
     */
    public static byte[] toArray(ByteBuffer buffer) {
        final int position = buffer.position();
        final int limit = buffer.limit();
        byte data[] = new byte[limit - position];
        buffer.duplicate().get(data);
        return data;
    }

    /**
     * convert hex string to byte array
     * @param s
     * @return
     */
    public static byte[] fromHexString(String s) {
        return BaseEncoding.base16().decode(s);
    }
}
