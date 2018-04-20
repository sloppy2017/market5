package org.sanchain.core.types.known.tx.txns;


import org.sanchain.client.api.model.TxObj;
import org.sanchain.core.hash.Hash256;
import org.sanchain.core.serialized.enums.TransactionType;
import org.sanchain.core.types.known.tx.Transaction;

public class TicketCancel extends Transaction {
    public TicketCancel() {
        super(TransactionType.TicketCancel);
    }
    public Hash256 ticketID() {
        return get(Hash256.TicketID);
    }
    public void ticketID(Hash256 id) {
        put(Hash256.TicketID, id);
    }
    @Override
    public TxObj analyze(String address){
        init();
        item.setType("ticket_cancel");
        return item;
    }
}
