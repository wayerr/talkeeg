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
 * type of symmetric ciphering
 * Created by wayerr on 26.12.14.
 */
public enum SymmetricCipherType implements EnumWithValue<Byte> {
    NONE(0, null, 0, 0),
    /**
     * AES with 128 bit key
     */
    AES_128(1, "AES", 16, 16);

    private final byte value;
    private final String algorithm;
    private final int blockSize;
    private final int keySize;

    SymmetricCipherType(int value, String algorithm, int keySize, int blockSize) {
        this.value = (byte)value;
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.blockSize = blockSize;
    }

    @Override
    public Byte getValue() {
        return value;
    }

    public String getName() {
        return algorithm;
    }

    /**
     * size of key in bytes
     * @return
     */
    public int getKeySize() {
        return keySize;
    }

    /**
     * size of block in bytes
     * @return
     */
    public int getBlockSize() {
        return blockSize;
    }
}
