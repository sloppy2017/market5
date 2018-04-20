package org.sanchain.core.types.known.tx.txns;


import org.sanchain.client.api.model.TxObj;
import org.sanchain.core.serialized.enums.TransactionType;
import org.sanchain.core.types.known.tx.Transaction;

public class TicketCreate extends Transaction {
    public TicketCreate() {
        super(TransactionType.TicketCreate);
    }

    @Override
    public TxObj analyze(String address){
        init();
        item.setType("ticket_create");
        return item;
    }
}
