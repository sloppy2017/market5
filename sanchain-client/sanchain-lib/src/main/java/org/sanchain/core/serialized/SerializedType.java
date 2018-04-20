package org.sanchain.core.serialized;

public interface SerializedType {
    Object toJSON();
    byte[] toBytes();
    String toHex();
    void toBytesSink(BytesSink to);
}
