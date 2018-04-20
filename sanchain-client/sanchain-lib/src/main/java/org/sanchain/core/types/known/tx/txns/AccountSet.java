package org.sanchain.core.types.known.tx.txns;


import org.sanchain.core.fields.Field;
import org.sanchain.core.types.known.tx.Transaction;
import org.sanchain.core.uint.UInt32;
import org.sanchain.client.api.model.TxObj;
import org.sanchain.core.VariableLength;
import org.sanchain.core.hash.Hash128;
import org.sanchain.core.hash.Hash256;
import org.sanchain.core.serialized.enums.TransactionType;

public class AccountSet extends Transaction {
    public AccountSet() {
        super(TransactionType.AccountSet);
    }
    public UInt32 transferRate() {return get(UInt32.TransferRate);}
    public UInt32 walletSize() {return get(UInt32.WalletSize);}
    public UInt32 setFlag() {return get(UInt32.SetFlag);}
    public UInt32 clearFlag() {return get(UInt32.ClearFlag);}
    public Hash128 emailHash() {return get(Hash128.EmailHash);}
    public Hash256 walletLocator() {return get(Hash256.WalletLocator);}
    public VariableLength messageKey() {return get(VariableLength.MessageKey);}
    public VariableLength domain() {return get(VariableLength.Domain);}
    public void transferRate(UInt32 val) {put(Field.TransferRate, val);}
    public void walletSize(UInt32 val) {put(Field.WalletSize, val);}
    public void setFlag(UInt32 val) {put(Field.SetFlag, val);}
    public void clearFlag(UInt32 val) {put(Field.ClearFlag, val);}
    public void emailHash(Hash128 val) {put(Field.EmailHash, val);}
    public void walletLocator(Hash256 val) {put(Field.WalletLocator, val);}
    public void messageKey(VariableLength val) {put(Field.MessageKey, val);}
    public void domain(VariableLength val) {put(Field.Domain, val);}

    @Override
    public TxObj analyze(String address){
        init();
        item.setType("account_set");
        return item;
    }

}
