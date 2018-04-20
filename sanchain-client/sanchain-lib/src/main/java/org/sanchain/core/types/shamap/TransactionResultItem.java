package org.sanchain.core.types.shamap;


import org.sanchain.core.hash.prefixes.HashPrefix;
import org.sanchain.core.hash.prefixes.Prefix;
import org.sanchain.core.serialized.BinarySerializer;
import org.sanchain.core.serialized.BytesSink;
import org.sanchain.core.types.known.tx.result.TransactionResult;

public class TransactionResultItem extends ShaMapItem<TransactionResult> {
    TransactionResult result;

    public TransactionResultItem(TransactionResult result) {
        this.result = result;
    }

    @Override
    void toBytesSink(BytesSink sink) {
        BinarySerializer write = new BinarySerializer(sink);
        write.addLengthEncoded(result.txn);
        write.addLengthEncoded(result.meta);
    }

    @Override
    public ShaMapItem<TransactionResult> copy() {
        // that's ok right ;) these bad boys are immutable anyway
        return this;
    }

    @Override
    public Prefix hashPrefix() {
        return HashPrefix.txNode;
    }
}
