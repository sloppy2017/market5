package org.sanchain.core.types.known.sle;


import org.sanchain.core.fields.Field;
import org.sanchain.core.serialized.enums.LedgerEntryType;
import org.sanchain.core.uint.UInt32;
import org.sanchain.core.hash.Hash256;

// this class has a PreviousTxnID and PreviousTxnLgrSeq
abstract public class ThreadedLedgerEntry extends LedgerEntry {
    public ThreadedLedgerEntry(LedgerEntryType type) {
        super(type);
    }
    public UInt32 previousTxnLgrSeq() {return get(UInt32.PreviousTxnLgrSeq);}
    public Hash256 previousTxnID() {return get(Hash256.PreviousTxnID);}
    public void previousTxnLgrSeq(UInt32 val) {put(Field.PreviousTxnLgrSeq, val);}
    public void previousTxnID(Hash256 val) {put(Field.PreviousTxnID, val);}
}
