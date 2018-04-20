package org.sanchain.core.types.shamap;


import org.sanchain.core.hash.prefixes.Prefix;
import org.sanchain.core.serialized.BytesSink;

public class BytesItem extends ShaMapItem<byte[]> {
    private byte[] item;

    public BytesItem(byte[] item) {
        this.item = item;
    }

    @Override
    void toBytesSink(BytesSink sink) {
        sink.add(item);
    }

    @Override
    public ShaMapItem<byte[]> copy() {
        return this;
    }

    @Override
    public Prefix hashPrefix() {
        return new Prefix() {
            @Override
            public byte[] bytes() {
                return new byte[0];
            }
        };
    }
}
