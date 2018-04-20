package org.sanchain.core.binary;


import org.sanchain.core.serialized.SerializedType;
import org.sanchain.core.serialized.BinarySerializer;
import org.sanchain.core.serialized.BytesSink;

public class STWriter {
    BytesSink sink;
    BinarySerializer serializer;
    public STWriter(BytesSink bytesSink) {
        serializer = new BinarySerializer(bytesSink);
        sink = bytesSink;
    }
    public void write(SerializedType obj) {
        obj.toBytesSink(sink);
    }
    public void writeVl(SerializedType obj) {
        serializer.addLengthEncoded(obj);
    }
}
