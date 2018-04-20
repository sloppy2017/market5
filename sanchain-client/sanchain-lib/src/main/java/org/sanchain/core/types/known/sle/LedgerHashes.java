package org.sanchain.core.types.known.sle;


import org.sanchain.core.serialized.enums.LedgerEntryType;
import org.sanchain.core.uint.UInt32;
import org.sanchain.core.Vector256;

public class LedgerHashes extends LedgerEntry {
    public LedgerHashes() {
        super(LedgerEntryType.LedgerHashes);
    }

    public Vector256 hashes() {
        return get(Vector256.Hashes);
    }

    public void hashes(Vector256 hashes) {
        put(Vector256.Hashes, hashes);
    }

    public UInt32 lastLedgerSequence() {
        return get(UInt32.LastLedgerSequence);
    }
}
