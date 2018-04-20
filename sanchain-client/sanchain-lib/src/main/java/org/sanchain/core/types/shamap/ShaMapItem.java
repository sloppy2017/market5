package org.sanchain.core.types.shamap;


import org.sanchain.core.hash.prefixes.Prefix;
import org.sanchain.core.serialized.BytesSink;

abstract public class ShaMapItem<T> {
    abstract void toBytesSink(BytesSink sink);
    public abstract ShaMapItem<T> copy();
    public abstract Prefix hashPrefix();
}
