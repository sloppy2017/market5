package org.sanchain.core.types.known.tx.txns;

import org.sanchain.client.api.model.TxObj;
import org.sanchain.core.AccountID;
import org.sanchain.core.Amount;
import org.sanchain.core.fields.Field;
import org.sanchain.core.serialized.enums.TransactionType;
import org.sanchain.core.types.known.tx.Transaction;
import org.sanchain.core.STArray;

/**
 * Created by ac
 * since 15/5/7.
 */
public class ActiveAccount extends Transaction {
    public ActiveAccount() {
        super(TransactionType.ActiveAccount);
    }
    public Amount amount() {
        return get(Amount.Amount);
    }
    public AccountID referee() {
        return (AccountID) get(Field.Referee);
    }
    public AccountID reference() {
        return (AccountID) get(Field.Reference);
    }
    public void amount(Amount val) {put(Field.Amount, val);}
    public void referee(AccountID val) {put(Field.Referee, val);}
    public void reference(AccountID val) {put(Field.Reference, val);}

    public void destination(AccountID val) {put(Field.Destination, val);}
    public AccountID destination() {return (AccountID) get(Field.Destination); }

    public void Amounts(STArray val){put(Field.Amounts,val);}
    public void Limits(STArray val){put(Field.Limits,val);}

    @Override
    public TxObj analyze(String address){
        init();
        item.setRecipient(destination().address);
        //not reference
        if(!destination().address.equals(address)){
            item.setType("active_add");
            item.setContact(item.getSender());
        }
        else{
            if(address.equals(item.getSender())){
                item.setType("active_acc");
            }else {
                item.setType("active");
                item.setContact(item.getSender());
            }
        }

        return item;
    }
}
