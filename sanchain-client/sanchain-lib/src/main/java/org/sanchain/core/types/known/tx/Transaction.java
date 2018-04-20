package org.sanchain.core.types.known.tx;


import org.sanchain.client.api.model.AmountObj;
import org.sanchain.core.AccountID;
import org.sanchain.core.enums.TransactionFlag;
import org.sanchain.core.fields.Field;
import org.sanchain.core.formats.TxFormat;
import org.sanchain.client.api.model.TxObj;
import org.sanchain.core.Amount;
import org.sanchain.core.STObject;
import org.sanchain.core.VariableLength;
import org.sanchain.core.hash.HalfSha512;
import org.sanchain.core.hash.Hash256;
import org.sanchain.core.hash.prefixes.HashPrefix;
import org.sanchain.core.serialized.enums.TransactionType;
import org.sanchain.core.uint.UInt16;
import org.sanchain.core.uint.UInt32;

import java.math.BigInteger;

public class Transaction extends STObject {
    public static final boolean CANONICAL_FLAG_DEPLOYED = true;
    public static final UInt32 CANONICAL_SIGNATURE = new UInt32(TransactionFlag.FullyCanonicalSig);
    public static TxObj item = new TxObj();

    public Transaction(TransactionType type) {
        setFormat(TxFormat.formats.get(type));
        put(Field.TransactionType, type);
    }

    public TransactionType transactionType() {
        return transactionType(this);
    }

    public Hash256 signingHash() {
        HalfSha512 signing = HalfSha512.prefixed256(HashPrefix.txSign);
        toBytesSink(signing, new FieldFilter() {
            @Override
            public boolean evaluate(Field a) {
                return a.isSigningField();
            }
        });
        return signing.finish();
    }

    public void setCanonicalSignatureFlag() {
        UInt32 flags = get(UInt32.Flags);
        if (flags == null) {
            flags = CANONICAL_SIGNATURE;
        } else {
            flags = flags.or(CANONICAL_SIGNATURE);
        }
        put(UInt32.Flags, flags);
    }

    public void init(){
        item = new TxObj();
        item.setSender(account().address);
        //if is tx maker, then set fee obj;
        AmountObj fee = new AmountObj(fee().valueText(), "SAN", null);
        item.setFee(fee);
    }

    public TxObj analyze(String address){
        item.setType("unknown");
        return item;
    }

    public boolean isAskOffer(){
        UInt32 flags = get(UInt32.Flags);
        BigInteger f1 = new BigInteger(flags.toBytes());
        BigInteger f2 = new BigInteger(String.valueOf(0x80000));

        return f1.and(f2).compareTo(new BigInteger("0")) > 0;
    }

    public UInt32 flags() {return get(UInt32.Flags);}
    public UInt32 sourceTag() {return get(UInt32.SourceTag);}
    public UInt32 sequence() {return get(UInt32.Sequence);}
    public UInt32 lastLedgerSequence() {return get(UInt32.LastLedgerSequence);}
    public UInt32 operationLimit() {return get(UInt32.OperationLimit);}
    public Hash256 previousTxnID() {return get(Hash256.PreviousTxnID);}
    public Hash256 accountTxnID() {return get(Hash256.AccountTxnID);}
    public Amount fee() {return get(Amount.Fee);}
    public VariableLength signingPubKey() {return get(VariableLength.SigningPubKey);}
    public VariableLength txnSignature() {return get(VariableLength.TxnSignature);}
    public AccountID account() {return get(AccountID.Account);}
    public void transactionType(UInt16 val) {put(Field.TransactionType, val);}
    public void flags(UInt32 val) {put(Field.Flags, val);}
    public void sourceTag(UInt32 val) {put(Field.SourceTag, val);}
    public void sequence(UInt32 val) {put(Field.Sequence, val);}
    public void lastLedgerSequence(UInt32 val) {put(Field.LastLedgerSequence, val);}
    public void operationLimit(UInt32 val) {put(Field.OperationLimit, val);}
    public void previousTxnID(Hash256 val) {put(Field.PreviousTxnID, val);}
    public void accountTxnID(Hash256 val) {put(Field.AccountTxnID, val);}
    public void fee(Amount val) {put(Field.Fee, val);}
    public void signingPubKey(VariableLength val) {put(Field.SigningPubKey, val);}
    public void txnSignature(VariableLength val) {put(Field.TxnSignature, val);}
    public void account(AccountID val) {put(Field.Account, val);}

}
