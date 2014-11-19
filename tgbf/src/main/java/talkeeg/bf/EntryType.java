
/*
 * Copyright (c) 2014 wayerr (radiofun@ya.ru).
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

package talkeeg.bf;

/**
 * Type of tgbf entries.
 *
 * Created by wayerr on 17.11.14.
 */
public enum EntryType {
    /**
     0  -  N null value           no targ  no size  no data
     */
    NULL,
    /**
     1  -  half byte value          no targ  no size  no data
     data stored in four least significant bits
     */
    HALF,
    /**
     2  -  1 byte value           no targ  no size  1 byte
     */
    BYTE_1,
    /**
     3  -  2 byte value           no targ  no size  2 bytes
     */
    BYTE_2,
    /**
     4  -  4 byte value           no targ  no size  4 byte
     */
    BYTE_4,
    /**
     5  -  8 byte value           no targ  no size  8 byte
     */
    BYTE_8,
    /**
     6  - 16 byte value           no targ  no size  16 byte
     */
    BYTE_16,
    /**
     7  -  B blob value           no targ  size     count of bytes in size
     */
    BYTES,
    /**
     8  -  S structure of values  targ     size     count of bytes in size
     */
    STRUCT,
    /**
     9  -  L list of values       no targ  size     count of bytes in size
     */
    LIST;

    private final byte value;

    EntryType() {
        this.value = (byte) this.ordinal();
    }

    @Override
    public String toString() {
        return name() + "(" + value + ')';
    }
}
