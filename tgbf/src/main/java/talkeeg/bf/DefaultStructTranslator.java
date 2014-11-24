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
 * Created by wayerr on 24.11.14.
 */
final class DefaultStructTranslator implements Translator {


    @Override
    public int getSize(Object message) {
        return 0;
    }

    @Override
    public int needSize(ByteBuffer buffer) {
        return 0;
    }

    @Override
    public void to(TranslationContext context, Object message, ByteBuffer buffer) {

    }

    @Override
    public Object from(TranslationContext context, ByteBuffer buffer) {
        return null;
    }
}
