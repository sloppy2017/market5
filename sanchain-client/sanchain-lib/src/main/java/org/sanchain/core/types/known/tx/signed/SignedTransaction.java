package org.sanchain.core.types.known.tx.signed;


import org.sanchain.core.Amount;
import org.sanchain.core.VariableLength;
import org.sanchain.core.hash.HalfSha512;
import org.sanchain.core.hash.Hash256;
import org.sanchain.core.hash.prefixes.HashPrefix;
import org.sanchain.core.serialized.BytesList;
import org.sanchain.core.serialized.MultiSink;
import org.sanchain.core.serialized.enums.TransactionType;
import org.sanchain.core.types.known.tx.Transaction;
import org.sanchain.core.uint.UInt32;
import org.sanchain.crypto.ecdsa.IKeyPair;

public class SignedTransaction {
    public Transaction txn;
    public Hash256 hash;
    public Hash256 signingHash;
    public Hash256 previousSigningHash;
    public String  tx_blob;

    public SignedTransaction(Transaction txn){
        this.txn = txn;
    }

    public void prepare(IKeyPair keyPair, Amount fee, UInt32 Sequence, UInt32 lastLedgerSequence) {
        VariableLength pubKey = new VariableLength(keyPair.pubBytes());

        // This won't always be specified
        if (lastLedgerSequence != null) {
            txn.put(UInt32.LastLedgerSequence, lastLedgerSequence);
        }
        txn.put(UInt32.Sequence, Sequence);
        txn.put(Amount.Fee, fee);
        txn.put(VariableLength.SigningPubKey, pubKey);

        if (Transaction.CANONICAL_FLAG_DEPLOYED) {
            txn.setCanonicalSignatureFlag();
        }

        signingHash = txn.signingHash();
        if (previousSigningHash != null && signingHash.equals(previousSigningHash)) {
            return;
        }
        try {
            VariableLength signature = new VariableLength(keyPair.sign(signingHash.bytes()));
            txn.put(VariableLength.TxnSignature, signature);

            BytesList blob = new BytesList();
            HalfSha512 id = HalfSha512.prefixed256(HashPrefix.transactionID);

            txn.toBytesSink(new MultiSink(blob, id));
            tx_blob = blob.bytesHex();
            hash = id.finish();
        } catch (Exception e) {
            // electric paranoia
            previousSigningHash = null;
            throw new RuntimeException(e);
        } /*else {*/
            previousSigningHash = signingHash;
        // }
    }

    public TransactionType transactionType() {
        return txn.transactionType();
    }
}
