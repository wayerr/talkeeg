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
 * translator for null value
 * Created by wayerr on 01.12.14.
 */
final class NullTranslator implements Translator {
    static final NullTranslator INSTANCE = new NullTranslator();

    private NullTranslator() {
    }

    @Override
    public int getSize(TranslationContext context, Object message) throws Exception {
        return 1;//size of type byte
    }

    @Override
    public int needSize(TranslationContext context, ByteBuffer buffer) throws Exception {
        return 1;//size of type byte
    }

    @Override
    public void to(TranslationContext context, Object message, ByteBuffer buffer) throws Exception {
        if(message != null) {
            throw new RuntimeException("this translator support only null values");
        }
        buffer.put(EntryType.NULL.getValue());
    }

    @Override
    public Object from(TranslationContext context, ByteBuffer buffer) throws Exception {
        TgbfUtils.readAndCheckType(buffer, EntryType.NULL);
        return null;
    }
}
