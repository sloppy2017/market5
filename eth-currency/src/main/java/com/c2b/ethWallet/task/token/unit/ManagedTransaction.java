package com.c2b.ethWallet.task.token.unit;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionTimeoutException;

/**
 * Generic transaction manager.
 */
public abstract class ManagedTransaction {

    // https://www.reddit.com/r/ethereum/comments/5g8ia6/attention_miners_we_recommend_raising_gas_limit/
	// up gas price to 30GW
	public static final BigInteger GAS_PRICE = BigInteger.valueOf(25_000_000_000L);

    protected Web3j web3j;

    private TransactionManager transactionManager;

    protected ManagedTransaction(Web3j web3j, TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.web3j = web3j;
    }

    public BigInteger getGasPrice() throws InterruptedException, ExecutionException {
        EthGasPrice ethGasPrice = web3j.ethGasPrice().sendAsync().get();

        return ethGasPrice.getGasPrice();
    }

    protected TransactionReceipt send(
            String to, String data, BigInteger value, BigInteger gasPrice, BigInteger gasLimit)
            throws InterruptedException, ExecutionException, TransactionTimeoutException {
        return transactionManager.executeTransaction(
                gasPrice, gasLimit, to, data, value);
    }
}
