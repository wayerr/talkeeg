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

import talkeeg.bf.schema.PrimitiveEntry;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

/**
 * translator for data which stored as {@link talkeeg.bf.EntryType#BYTES bytes array} <p/>
 * @see talkeeg.bf.BlobTranslator.BlobAdapter
 * Created by wayerr on 25.11.14.
 */
public class BlobTranslator implements Translator {

    /**
     * iface for customising adapters which can read complex data from byte array
     * @see talkeeg.bf.BlobTranslator#ADAPTER_BYTES
     * @see talkeeg.bf.BlobTranslator#ADAPTER_STRING
     */
    public interface BlobAdapter {
        int size(Object data) throws Exception;
        void to(Object data, ByteBuffer to) throws Exception;
        Object from(ByteBuffer from, int length) throws Exception;
    }

    /**
     * simple adapter for bytes[]
     */
    public static final BlobAdapter ADAPTER_BYTES = new BlobAdapter() {

        @Override
        public int size(Object data) {
            return ((byte[])data).length;
        }

        @Override
        public void to(Object data, ByteBuffer to) {
            to.put((byte[])data);
        }

        @Override
        public Object from(ByteBuffer from, int length) {
            final byte buff[] = new byte[length];
            from.get(buff);
            return buff;
        }
    };
    /**
     * adapter for converting string to bytes in utf-8.
     * note that {@link talkeeg.bf.BlobTranslator.BlobAdapter#size(Object)} method will convert
     * string into temporally buffer, this means that we need a way to cache size from translators
     */
    public static final BlobAdapter ADAPTER_STRING = new BlobAdapter() {


        @Override
        public int size(Object data) {
            return getBytes(data).length;
        }

        protected byte[] getBytes(Object data) {
            return ((String) data).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public void to(Object data, ByteBuffer to) {
            to.put(getBytes(data));
        }

        @Override
        public Object from(ByteBuffer from, int length) throws Exception {
            final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            final ByteBuffer dup = from.duplicate();
            dup.limit(length);
            final CharBuffer res = decoder.decode(dup);
            return res.toString();
        }
    };
    private final PrimitiveEntry entry;
    private final BlobAdapter adapter;

    public BlobTranslator(PrimitiveEntry entry, BlobAdapter adapter) {
        //in future we can read and check max size from this entry
        this.entry = entry;
        this.adapter = adapter;
        if(this.adapter == null) {
            throw new NullPointerException("adapter is null");
        }
    }

    @Override
    public int getSize(TranslationContext context, Object message) throws Exception {
        final int dataSize = adapter.size(message);
        return 1 /*one byte for type*/+ dataSize + TgbfUtils.getSizeOfPrimitiveEntry(dataSize);
    }

    @Override
    public int needSize(TranslationContext context, ByteBuffer buffer) throws Exception {
        final int position = buffer.position();
        try {
            readAndCheckType(buffer);
            final long length = TgbfUtils.readUnsignedInteger(buffer);
            checkLength(length);
            return (int) length;
        } finally {
            buffer.position(position);
        }
    }

    protected void checkLength(long length) {
        if(length > Integer.MAX_VALUE) {
            throw new RuntimeException("size of BYTES bigger than integer max value: " + length);
        }
    }

    @Override
    public void to(TranslationContext context, Object message, ByteBuffer buffer) throws Exception {
        buffer.put(EntryType.BYTES.getValue());
        final int dataSize = adapter.size(message);
        TgbfUtils.writeSignedInteger(buffer, dataSize);
        adapter.to(message, buffer);
    }

    @Override
    public Object from(TranslationContext context, ByteBuffer buffer) throws Exception {
        readAndCheckType(buffer);
        final long dataSize = TgbfUtils.readUnsignedInteger(buffer);
        checkLength(dataSize);
        return adapter.from(buffer, (int) dataSize);
    }

    protected void readAndCheckType(ByteBuffer buffer) {
        final byte typeByte = buffer.get();//read type of entity, we believe that it type is correct
        final EntryType readedType = EntryType.getEntryType(typeByte);
        if(readedType != EntryType.BYTES) {
            throw new RuntimeException("unexpected type " + readedType + " when expect BYTES");
        }
    }
}
