package org.sanchain.client.jsonrpc4j.dto;

public class BtcTransactionBlocks {
	private BtcTransactionBlock[] transactions;
	private String lastblock;

	public BtcTransactionBlock[] getTransactions() {
		return transactions;
	}

	public void setTransactions(BtcTransactionBlock[] transactions) {
		this.transactions = transactions;
	}

	public String getLastblock() {
		return lastblock;
	}

	public void setLastblock(String lastblock) {
		this.lastblock = lastblock;
	}

}
