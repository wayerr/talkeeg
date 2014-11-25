
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
import talkeeg.bf.schema.SchemaEntry;
import talkeeg.bf.schema.Struct;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Message writer. <p/>
 * Not thread safe. <p/>
 * Created by rad on 17.11.14.
 */
public final class BfWriter {
    private final Schema schema;
    private final MetaTypeResolver resolver = new MetaTypeResolver();

    public BfWriter(Schema schema) {
        this.schema = schema;
    }

    /**
     * Write object to it`s binary representation.
     * @param obj
     * @throws IOException
     */
    public ByteBuffer write(Object obj) throws Exception {

        final int mesageId = getStructId(obj);
        Struct message = schema.getMessage(mesageId);
        if(message == null) {
            throw new RuntimeException("Can not find message for structId=" + mesageId);
        }
        final TranslationContextImpl context = new TranslationContextImpl(resolver, message);
        final Translator translator = context.getTranslator(message);
        final int size = translator.getSize(context, obj);
        ByteBuffer buffer = ByteBuffer.allocate(size);
        translator.to(context, obj, buffer);
        buffer.rewind();
        return buffer;
    }

    private static int getStructId(Object obj) {
        final Class<?> clazz = obj.getClass();

        // if class is struct
        final StructInfo si = clazz.getAnnotation(StructInfo.class);
        if(si == null) {
            throw new RuntimeException("can not get translator for " + clazz + " it class need " + StructInfo.class.getName() + " annotation.");
        }
        return si.id();
    }
}
