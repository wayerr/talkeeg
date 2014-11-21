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

import talkeeg.common.util.EnumWithValue;

/**
 *
 * Type of {@link talkeeg.common.model.SingleMessage single message} ciphering
 * Created by wayerr on 21.11.14.
 */
public enum MessageCipherType implements EnumWithValue<Byte> {
    /**
     * MSG_CIPHER_NONE = 0 (используется во время CA)
     */
    NONE(0),
    /**
     * MSG_CIPHER_DST_PUBK = 1
     */
    DST_PUBK(1);

    private final byte value;

    MessageCipherType(int value) {
        this.value = (byte)value;
    }

    @Override
    public Byte getValue() {
        return value;
    }
}
