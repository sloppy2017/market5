package org.sanchain.core.types.known.tx.txns;

import org.sanchain.core.AccountID;
import org.sanchain.core.fields.Field;
import org.sanchain.core.types.known.tx.Transaction;
import org.sanchain.client.api.model.TxObj;
import org.sanchain.core.serialized.enums.TransactionType;

/**
 * Created by wenfeng on 15/8/17.
 */
public class SetRegularKey extends Transaction {
    public SetRegularKey() {
        super(TransactionType.SetRegularKey);
    }

    public AccountID RegularKey() {return get(AccountID.RegularKey);}
    public void RegularKey(AccountID val) {put(Field.RegularKey, val);}


    @Override
    public TxObj analyze(String address){
        init();
        item.setType("set_regular_key");
        if(RegularKey()!=null)
            item.setRecipient(RegularKey().address);
        return item;
    }
}
