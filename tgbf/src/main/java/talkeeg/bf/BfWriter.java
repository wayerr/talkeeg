
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

import talkeeg.bf.schema.Schema;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.TreeMap;

/**
 * Message writer. <p/>
 * Not thread safe. <p/>
 * Created by rad on 17.11.14.
 */
public final class BfWriter {
    private Map<Integer, Translator> structs = new TreeMap<>();
    private Schema schema;
    private ByteBuffer buffer;

    /**
     * Write object to it`s binary representation.
     * @param obj
     * @param target
     * @throws IOException
     */
    public void write(Object obj, WritableByteChannel target) throws IOException {
        final TranslationContextImpl context = new TranslationContextImpl(schema);
        final Translator translator = getTranslator(obj);
        translator.to(context, obj, buffer);
        buffer.rewind();
        target.write(buffer);
    }

    protected Translator getTranslator(Object obj) {
        final Class<?> clazz = obj.getClass();

        // if class is struct
        final StructInfo si = clazz.getAnnotation(StructInfo.class);
        if(si == null) {
            throw new RuntimeException("can not get translator for " + clazz + " it class need " + StructInfo.class.getName() + " annotation.");
        }
        final int id = si.id();
        final Translator translator = structs.get(id);
        if(translator == null) {
            throw new RuntimeException("can not get translator for " + clazz + " it has unknown @StructInfo.id=" + id);
        }
        return translator;
    }
}
