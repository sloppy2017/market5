package org.sanchain.core.types.shamap;


import org.sanchain.core.types.known.sle.LedgerEntry;
import org.sanchain.core.STObject;
import org.sanchain.core.hash.prefixes.HashPrefix;
import org.sanchain.core.hash.prefixes.Prefix;
import org.sanchain.core.serialized.BytesSink;

public class LedgerEntryItem extends ShaMapItem<LedgerEntry> {
    public LedgerEntryItem(LedgerEntry entry) {
        this.entry = entry;
    }

    public LedgerEntry entry;

    @Override
    void toBytesSink(BytesSink sink) {
        entry.toBytesSink(sink);
    }

    @Override
    public ShaMapItem<LedgerEntry> copy() {
        STObject object = STObject.translate.fromBytes(entry.toBytes());
        LedgerEntry le = (LedgerEntry) object;
        // TODO: what about other auxiliary (non serialized) fields
        le.index(entry.index());
        return new LedgerEntryItem(le);
    }

    @Override
    public Prefix hashPrefix() {
        return HashPrefix.leafNode;
    }
}
