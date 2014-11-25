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
 * some utilities
 *
 * Created by wayerr on 24.11.14.
 */
public final class TgbfUtils {
    private TgbfUtils() {
    }


    public static int getMinimalSize(final int value) {
        if(value >>> 8 == 0) {
            return 1;
        }
        if(value >>> 16 == 0) {
            return 2;
        }
        return 4;
    }

    public static int getMinimalSize(final long value) {
        if(value >>> 8 == 0) {
            return 1;
        }
        if(value >>> 16 == 0) {
            return 2;
        }
        if(value >>> 32 == 0) {
            return 4;
        }
        return 8;
    }

    /**
     * size of stored primitive entry with specified value
     * @param value
     * @return
     */
    public static int getSizeOfPrimitiveEntry(final long value) {
        return getMinimalSize(value)  + 1/* one byte for type*/;
    }

    /**
     * size of stored primitive entry with specified value
     * @param value
     * @return
     */
    public static int getSizeOfPrimitiveEntry(final int value) {
        return getMinimalSize(value)  + 1/* one byte for type*/;
    }

    /**
     * test where class an integer number, i.e. Integer, Long, Short, or Byte
     * @param clazz
     * @return
     */
    public static boolean isIntegerNumber(Class<?> clazz) {
        return  Integer.class.equals(clazz) ||
                Long.class.equals(clazz) ||
                Short.class.equals(clazz) ||
                Byte.class.equals(clazz)
                ;
    }

    /**
     * test where class an float point number, i.e. Float or Double
     * @param clazz
     * @return
     */
    public static boolean isFloatNumber(Class<?> clazz) {
        return  Float.class.equals(clazz) ||
                Double.class.equals(clazz)
                ;
    }

    /**
     * return predefined size of storage type (0 for null and HALF, because it contains only type declaration),
     * or -1 if type has dynamic type (e.g. bytes, list or struct)
     * @param type
     * @return
     */
    public static int getTypeSize(EntryType type) {
        switch (type) {
            case NULL:
            case HALF:
                return 0;
            case BYTE_1:
                return 1;
            case BYTE_2:
                return 2;
            case BYTE_4:
                return 4;
            case BYTE_8:
                return 8;
            case BYTE_16:
                return 16;
        }
        return -1;
    }

    /**
     * size of entry founded at current position in buffer
     * @param buffer
     * @return
     */
    public static int getEntryLength(ByteBuffer buffer) {
        final int position = buffer.position();
        try {
            final byte b = buffer.get();
            final EntryType type = EntryType.getEntryType(b);
            int size;
            if(type == EntryType.BYTES) {
                final long val = readUnsignedInteger(buffer);
                if(val > Integer.MAX_VALUE) {
                    throw new RuntimeException("too long bytes length: " + val);
                }
                size = (int)val;
            } else if(type == EntryType.STRUCT) {
                //skip structure id
                readUnsignedInteger(buffer);
                final long val = readUnsignedInteger(buffer);
                if(val > Integer.MAX_VALUE) {
                    throw new RuntimeException("too long structure length: " + val);
                }
                size = (int)val;
            } else if(type == EntryType.LIST) {
                //skip list type definition
                skipListTarg(buffer);
                final long val = readUnsignedInteger(buffer);
                if(val > Integer.MAX_VALUE) {
                    throw new RuntimeException("too long structure length: " + val);
                }
                size = (int)val;
            } else {
                size = TgbfUtils.getTypeSize(type);
            }
            return size;
        } finally {
            buffer.position(position);
        }
    }

    /**
     * read and skip list TARG (type argument), which may be recursive
     * @param buffer
     */
    static void skipListTarg(ByteBuffer buffer) {
        final byte listItemType = buffer.get();
        EntryType listItemEntryType = EntryType.getEntryType(listItemType);
        if(listItemEntryType == EntryType.STRUCT) {
            readUnsignedInteger(buffer);
        } else if(listItemEntryType == EntryType.LIST) {
            // we already read LIST type, because need revert position
            buffer.position(buffer.position() - 1);
            skipListTarg(buffer);
        }
    }

    /**
     * read unsigned integer number as long value <p/>
     * note that long values with MSB == 1 will be look like negative long
     * @see #readSignedInteger(java.nio.ByteBuffer)
     * @param buffer
     * @return
     */
    public static long readUnsignedInteger(ByteBuffer buffer) {
        final byte b = buffer.get();
        final EntryType type = EntryType.getEntryType(b);
        switch (type) {
            case HALF:
                return b & 0x0f;
            case BYTE_1:
                return buffer.get() & 0xffl;
            case BYTE_2:
                return buffer.getShort() & 0xffffl;
            case BYTE_4:
                return buffer.getInt() & 0xffffffffl;
            case BYTE_8:
                return buffer.getLong();
        }
        // BYTE_16 - is unsupported!
        throw new RuntimeException("Can not read " + type + " as integer");
    }

    /**
     * read signed integer number as long value <p/>
     * @see #readUnsignedInteger(java.nio.ByteBuffer)
     * @param buffer
     * @return
     */
    public static long readSignedInteger(ByteBuffer buffer) {
        final byte b = buffer.get();
        final EntryType type = EntryType.getEntryType(b);
        switch (type) {
            case HALF:
                long i = b & 0x0f;
                if((i & 0b1000) != 0) {
                    i = -i;
                }
                return i;
            case BYTE_1:
                return buffer.get();
            case BYTE_2:
                return buffer.getShort();
            case BYTE_4:
                return buffer.getInt();
            case BYTE_8:
                return buffer.getLong();
        }
        // BYTE_16 - is unsupported!
        throw new RuntimeException("Can not read " + type + " as integer");
    }

    /**
     * write unsigned integer value to appropriate storage type
     * @param to
     * @param value
     */
    public static void writeSignedInteger(ByteBuffer to, long value) {
        final int bytes = getMinimalSize(value);
        final EntryType type;
        if(bytes <= 1) {
            type = EntryType.BYTE_1;
        } else if(bytes <= 2) {
            type = EntryType.BYTE_2;
        } else if(bytes <= 4) {
            type = EntryType.BYTE_4;
        } else if(bytes <= 8) {
            type = EntryType.BYTE_8;
        } else {
            throw new RuntimeException("Too big value size: " + bytes);
        }
        writeSignedInteger(to, value, type);
    }

    /**
     * write signed integer as specified type
     * @param to
     * @param value
     * @param type
     */
    public static void writeSignedInteger(ByteBuffer to, long value, EntryType type) {
        byte typeByte = type.getValue();
        if(type == EntryType.HALF) {
            if(value > 0x0fl) {
                throw new IllegalArgumentException(value + " is too long for " + type);
            }
            final byte b = (byte) (typeByte | ((value) & 0x0f));
            to.put(b);
        } else {
            to.put(typeByte);
            if(type == EntryType.BYTE_1) {
                if(value > 0xffl) {
                    throw new IllegalArgumentException(value + " is too long for " + type);
                }
                to.put((byte) value);
            } else if(type == EntryType.BYTE_2) {
                if(value > 0xffffl) {
                    throw new IllegalArgumentException(value + " is too long for " + type);
                }
                to.putShort((short) value);
            } else if(type == EntryType.BYTE_4) {
                if(value > 0xffffffffl) {
                    throw new IllegalArgumentException(value + " is too long for " + type);
                }
                to.putInt((int) value);
            } else if(type == EntryType.BYTE_8) {
                to.putLong(value);
            }
        }
    }

    /**
     * read first byte from buffer and check that it matches with specified
     * @param buffer
     * @param expected
     */
    public static void readAndCheckType(ByteBuffer buffer, EntryType expected) {
        final byte typeByte = buffer.get();//read type of entity, we believe that it type is correct
        final EntryType readedType = EntryType.getEntryType(typeByte);
        if(readedType != expected) {
            throw new RuntimeException("unexpected type " + readedType + " when expect " + expected);
        }
    }
}
