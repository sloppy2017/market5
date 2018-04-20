package org.sanchain.core.types.known.tx.txns;


import org.sanchain.core.AccountID;
import org.sanchain.client.api.model.AmountObj;
import org.sanchain.client.api.model.TxObj;
import org.sanchain.core.Amount;
import org.sanchain.core.PathSet;
import org.sanchain.core.fields.Field;
import org.sanchain.core.hash.Hash256;
import org.sanchain.core.serialized.enums.TransactionType;
import org.sanchain.core.types.known.tx.Transaction;
import org.sanchain.core.uint.UInt32;

public class Payment extends Transaction {
    public Payment() {
        super(TransactionType.Payment);
    }

    public UInt32 destinationTag() {return get(UInt32.DestinationTag);}
    public Hash256 invoiceID() {return get(Hash256.InvoiceID);}
    public Amount amount() {return get(Amount.Amount);}
    public Amount sendMax() {return get(Amount.SendMax);}
    public AccountID destination() {return get(AccountID.Destination);}
    public PathSet paths() {return get(PathSet.Paths);}
    public void destinationTag(UInt32 val) {put(Field.DestinationTag, val);}
    public void invoiceID(Hash256 val) {put(Field.InvoiceID, val);}
    public void amount(Amount val) {put(Field.Amount, val);}
    public void sendMax(Amount val) {put(Field.SendMax, val);}
    public void destination(AccountID val) {put(Field.Destination, val);}
    public void paths(PathSet val) {put(Field.Paths, val);}

    @Override
    public TxObj analyze(String address){
        init();
        item.setRecipient(destination().address);
        if (!destination().address.equals(address))
            item.setContact(destination().address);
        String paymentAmount = amount().valueText();
        AmountObj amount = new AmountObj(paymentAmount, amount().currencyString(), amount().issuerString());
        amount.setCurrency(amount().currencyString());
        item.setAmount(amount);

        if (!address.equals(destination().address) && !address.equals(account().address)) {
            if (paths() != null) {
                for (PathSet.Path path : paths()) {
                    for (PathSet.Hop hop : path) {
                        if (hop.account != null && address.equals(hop.account.address)) {
                            item.setType("rippling");
                            break;
                        }
                    }
                }
                if (item.getType() == null) {
                    item.setType("offercreate");
                }
            } else
                item.setType("rippling");
        } else {
            if (address.equals(destination().address) && address.equals(account().address)) {
                try {
                    item.setType("exchange");
                    item.setSendMax(new AmountObj(sendMax().valueText(),
                            sendMax().currencyString().equals("SAN") ? "SAN" : sendMax().currencyString(), sendMax().issuerString()));
                }catch (Exception e){
                    return null;
                }
            } else
                item.setType(destination().address.equals(address) ? "received" : "sent");
        }
        return item;
    }

}
