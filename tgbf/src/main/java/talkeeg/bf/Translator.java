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

package talkeeg.bf;

import java.nio.ByteBuffer;

/**
 * Object which does conversion between object and his binary representation.
 *
 * Created by wayerr on 17.11.14.
 */
public interface Translator {
    /**
     * Calculate size of message in binary representation
     * @param context
     * @param message
     * @return
     */
    int getSize(TranslationContext context, Object message) throws Exception;

    /**
     * Return count of insufficient bytes for translating buffer to message
     * @param context
     * @param buffer
     * @return
     */
    int needSize(TranslationContext context, ByteBuffer buffer) throws Exception;

    /**
     * translate message to buffer
     * @param message
     * @param buffer
     */
    void to(TranslationContext context, Object message, ByteBuffer buffer) throws Exception;

    /**
     * Translate buffer to message
     * @param context
     * @param buffer
     * @return
     */
    Object from(TranslationContext context, ByteBuffer buffer) throws Exception;
}
