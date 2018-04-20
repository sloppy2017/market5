package org.sanchain.core.types.known.tx.txns;

import org.sanchain.core.AccountID;
import org.sanchain.client.api.model.AmountObj;
import org.sanchain.client.api.model.TxObj;
import org.sanchain.core.fields.Field;
import org.sanchain.core.serialized.enums.TransactionType;
import org.sanchain.core.types.known.tx.Transaction;
import org.sanchain.core.uint.UInt64;

/**
 * Created by A
 * since 15/1/4.
 */
public class Dividend extends Transaction{
    public Dividend() {
        super(TransactionType.Dividend);
    }

    public AccountID destination() {return get(AccountID.Destination);}
    public UInt64 dividendCoins(){return (UInt64)get(Field.DividendCoins);}

    @Override
    public TxObj analyze(String address){
        init();
        if (!destination().address.equals(address)) {
            return null;
        }
        item.setRecipient(address);
        item.setType("dividend");
        item.setAmount(new AmountObj(String.valueOf(dividendCoins().doubleValue() / 1000000), "SAN", null));

        return item;
    }
}
