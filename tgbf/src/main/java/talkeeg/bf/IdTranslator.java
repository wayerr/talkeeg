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

import java.nio.ByteBuffer;

/**
 * 128 bit value translator
 *
 * @see Int128
 *
 * Created by wayerr on 01.12.14.
 */
final class IdTranslator implements Translator {

    private static final int VALUE_LEN = 16;
    /**
     * type byte + 16 bytes of value
     */
    private static final int SIZE = 1 + VALUE_LEN;

    @Override
    public int getSize(TranslationContext context, Object message) throws Exception {
        return SIZE;
    }

    @Override
    public int needSize(TranslationContext context, ByteBuffer buffer) throws Exception {
        return SIZE;
    }

    @Override
    public void to(TranslationContext context, Object message, ByteBuffer buffer) throws Exception {
        Int128 int128 = (Int128)message;
        buffer.put(EntryType.BYTE_16.getValue());
        buffer.put(int128.getData());
    }

    @Override
    public Int128 from(TranslationContext context, ByteBuffer buffer) throws Exception {
        TgbfUtils.readAndCheckType(buffer, EntryType.BYTE_16);
        byte bytes[] = new byte[VALUE_LEN];
        buffer.get(bytes);
        return new Int128(bytes);
    }
}
