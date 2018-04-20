package org.sanchain.core.types.known.sle.entries;

import org.sanchain.core.AccountID;
import org.sanchain.core.Amount;
import org.sanchain.core.VariableLength;
import org.sanchain.core.fields.Field;
import org.sanchain.core.serialized.enums.LedgerEntryType;
import org.sanchain.core.types.known.sle.ThreadedLedgerEntry;
import org.sanchain.core.uint.UInt32;
import org.sanchain.core.enums.LedgerFlag;
import org.sanchain.core.hash.Hash128;
import org.sanchain.core.hash.Hash256;

public class AccountRoot extends ThreadedLedgerEntry {
    public AccountRoot() {
        super(LedgerEntryType.AccountRoot);
    }

    public UInt32 sequence() {return get(UInt32.Sequence);}
    public UInt32 transferRate() {return get(UInt32.TransferRate);}
    public UInt32 walletSize() {return get(UInt32.WalletSize);}
    public UInt32 ownerCount() {return get(UInt32.OwnerCount);}
    public Hash128 emailHash() {return get(Hash128.EmailHash);}
    public Hash256 walletLocator() {return get(Hash256.WalletLocator);}
    public Amount balance() {return get(Amount.Balance);}
    public VariableLength messageKey() {return get(VariableLength.MessageKey);}
    public VariableLength domain() {return get(VariableLength.Domain);}
    public AccountID account() {return get(AccountID.Account);}
    public AccountID regularKey() {return get(AccountID.RegularKey);}

    public void sequence(UInt32 val) {put(Field.Sequence, val);}
    public void transferRate(UInt32 val) {put(Field.TransferRate, val);}
    public void walletSize(UInt32 val) {put(Field.WalletSize, val);}
    public void ownerCount(UInt32 val) {put(Field.OwnerCount, val);}
    public void emailHash(Hash128 val) {put(Field.EmailHash, val);}
    public void walletLocator(Hash256 val) {put(Field.WalletLocator, val);}
    public void balance(Amount val) {put(Field.Balance, val);}
    public void messageKey(VariableLength val) {put(Field.MessageKey, val);}
    public void domain(VariableLength val) {put(Field.Domain, val);}
    public void account(AccountID val) {put(Field.Account, val);}
    public void regularKey(AccountID val) {put(Field.RegularKey, val);}

    public boolean requiresAuth() {
        return flags().testBit(LedgerFlag.RequireAuth);
    }


    @Override
    public void setDefaults() {
        super.setDefaults();
        if (ownerCount() == null) {
            ownerCount(new UInt32(0));
        }
    }
}
