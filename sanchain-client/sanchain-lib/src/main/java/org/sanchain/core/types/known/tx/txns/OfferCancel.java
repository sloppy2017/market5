package org.sanchain.core.types.known.tx.txns;


import org.sanchain.client.api.model.TxObj;
import org.sanchain.core.fields.Field;
import org.sanchain.core.serialized.enums.TransactionType;
import org.sanchain.core.types.known.tx.Transaction;
import org.sanchain.core.uint.UInt32;

public class OfferCancel extends Transaction {
    public OfferCancel() {
        super(TransactionType.OfferCancel);
    }
    public UInt32 offerSequence() {return get(UInt32.OfferSequence);}
    public void offerSequence(UInt32 val) {put(Field.OfferSequence, val);}
    @Override
    public TxObj analyze(String address){
        init();
        item.setType("offer_cancelled");
        item.setOfferType(isAskOffer()?"sell":"buy");
        return item;
    }
}
