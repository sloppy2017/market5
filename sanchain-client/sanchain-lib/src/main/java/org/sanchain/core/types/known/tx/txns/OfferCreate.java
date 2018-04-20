package org.sanchain.core.types.known.tx.txns;


import org.sanchain.client.api.model.AmountObj;
import org.sanchain.client.api.model.TxObj;
import org.sanchain.core.Amount;
import org.sanchain.core.fields.Field;
import org.sanchain.core.serialized.enums.TransactionType;
import org.sanchain.core.types.known.tx.Transaction;
import org.sanchain.core.uint.UInt32;

public class OfferCreate extends Transaction {
    public OfferCreate() {
        super(TransactionType.OfferCreate);
    }
    public UInt32 expiration() {return get(UInt32.Expiration);}
    public UInt32 offerSequence() {return get(UInt32.OfferSequence);}
    public Amount takerPays() {return get(Amount.TakerPays);}
    public Amount takerGets() {return get(Amount.TakerGets);}
    public void expiration(UInt32 val) {put(Field.Expiration, val);}
    public void offerSequence(UInt32 val) {put(Field.OfferSequence, val);}
    public void takerPays(Amount val) {put(Field.TakerPays, val);}
    public void takerGets(Amount val) {put(Field.TakerGets, val);}

    @Override
    public TxObj analyze(String address){
        init();
        //if tx result is not success, then set type to failed;
        if (item.getSender().equals(address)) {
            item.setSender(address);
            item.setOfferType(isAskOffer()?"sell":"buy");
        }
        if (address.equals(takerGets().issuerString()) || address.equals(takerPays().issuerString())) {
            item.setType("offer_rippling");
        } else
            item.setType("offercreate");
        if (item.getSender().equals(address) || item.getType().equals("offer_rippling")) {
            AmountObj takerGets = new AmountObj(takerGets().valueText(), takerGets().currencyString(), takerGets().issuerString());
            AmountObj takerPays = new AmountObj(takerPays().valueText(), takerPays().currencyString(), takerPays().issuerString());
            item.setTakerGets(takerGets);
            item.setTakerPays(takerPays);
        }

        return item;
    }
}
