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

package talkeeg.common.model;

import talkeeg.common.util.Arrays;
import talkeeg.common.util.BinaryData;

/**
 * digital signature
 * @see java.security.Signature
 * Created by wayerr on 21.11.14.
 */
public final class Sign extends BinaryData {

    /**
     * create sing instance with copy of byte array data.
     * @param data
     */
    public Sign(byte[] data) {
        super(data);
    }

    /**
     * create Sign from hex string
     * @param string
     * @return
     */
    public static Sign fromString(String string) {
        return new Sign(Arrays.fromHexString(string));
    }
}
