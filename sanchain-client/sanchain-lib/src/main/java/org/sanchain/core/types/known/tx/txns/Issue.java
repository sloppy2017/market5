package org.sanchain.core.types.known.tx.txns;

import org.sanchain.client.api.model.AmountObj;
import org.sanchain.client.api.model.TxObj;
import org.sanchain.core.AccountID;
import org.sanchain.core.Amount;
import org.sanchain.core.STArray;
import org.sanchain.core.fields.Field;
import org.sanchain.core.serialized.enums.TransactionType;
import org.sanchain.core.types.known.tx.Transaction;

/**
 * Created by ac
 * since 15/5/13.
 */
public class Issue extends Transaction {
    public Issue() {
        super(TransactionType.Issue);
    }

    public Amount amount() {
        return get(Amount.Amount);
    }
    public AccountID destination() {
        return (AccountID) get(Field.Destination);
    }
    public AccountID releaseSchedule() {
        return (AccountID) get(Field.ReleaseSchedule);
    }
    public void amount(Amount val) {put(Field.Amount, val);}
    public void destination(AccountID val) {put(Field.Destination, val);}
    public void releaseSchedule(STArray val) {put(Field.ReleaseSchedule, val);}

    @Override
    public TxObj analyze(String address){
        init();
        item.setAmount(new AmountObj(amount().valueText(), amount().currencyString(), amount().issuerString()));
        item.setSender(account().address);
        item.setRecipient(destination().address);
        item.setType("issue");
        return item;
    }
}
