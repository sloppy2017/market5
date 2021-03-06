package org.sanchain.core.serialized;

import org.sanchain.core.fields.Field;
import org.ripple.bouncycastle.util.encoders.Hex;

public class BinaryParser {
    protected int size;
    protected byte[] bytes;
    protected int cursor = 0;

    public BinaryParser(byte[] bytes) {
        this.size = bytes.length;
        this.bytes = bytes;
    }

    public BinaryParser(int size) {
        this.size = size;
    }

    public BinaryParser(String hex) {
        this(Hex.decode(hex));
    }

    public void skip(int n) {
        cursor += n;
    }

    public byte readOne() {
        return bytes[cursor++];
    }
    protected byte[] read(int n, boolean advance) {
        byte[] ret = new byte[n];
        System.arraycopy(bytes, cursor, ret, 0, n);
        if (advance) {
            cursor += n;
        }
        return ret;
    }

    public byte[] read(int n) {
        return read(n, true);
    }

    public Field readField() {
        int fieldCode = readFieldCode();
        Field field = Field.fromCode(fieldCode);
        if (field == null) {
            throw new IllegalStateException("Couldn't parse field from " +
                    Integer.toHexString(fieldCode));
        }

        return field;
    }

    public int readFieldCode() {
        byte tagByte = readOne();

        int typeBits = (tagByte & 0xFF) >>> 4;
        if (typeBits == 0) typeBits = readOne();

        int fieldBits = tagByte & 0x0F;
        if (fieldBits == 0) fieldBits = readOne();
        fieldBits = fieldBits & 0xFF;
        return (typeBits << 16 | fieldBits);
    }

    public boolean end() {
        return cursor >= size; // greater guard against infinite loops
    }

    public int pos() {
        return cursor;
    }

    public int readOneInt() {
        return readOne() & 0xFF;
    }

    public int readVLLength() {
        int b1 = readOneInt();
        int result;

        if (b1 <= 192) {
            result = b1;
        } else if (b1 <= 240) {
            int b2 = readOneInt();
            result = 193 + (b1 - 193) * 256 + b2;
        } else if (b1 <= 254) {
            int b2 = readOneInt();
            int b3 = readOneInt();
            result = 12481 + (b1 - 241) * 65536 + b2 * 256 + b3;
        } else {
            throw new RuntimeException("Invalid varint length indicator");
        }

        return result;
    }

    public int size() {
        return size;
    }

}
