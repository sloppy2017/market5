package com.c2b.ethWallet.task.token.unit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionTimeoutException;
//import org.web3j.tx.RawTransactionManager;
//import org.web3j.tx.TransactionManager;
import org.web3j.utils.Async;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

public class Transfer extends ManagedTransaction {
	public static final BigInteger GAS_LIMIT = BigInteger.valueOf(200000l);
//	public static final BigInteger CONTRACT_GAS_LIMIT = BigInteger.valueOf(200000l);

	public Transfer(Web3j web3j, TransactionManager transactionManager) {
		super(web3j, transactionManager);
	}

	private TransactionReceipt send(String toAddress, BigDecimal value,
			Unit unit) throws ExecutionException, InterruptedException,
			TransactionTimeoutException {
//		BigInteger gasPrice = this.getGasPrice();
		BigInteger gasPrice = this.GAS_PRICE;
		return this.send(toAddress, value, unit, gasPrice, GAS_LIMIT);
	}
	

	private TransactionReceipt send(String toAddress, BigDecimal value,
			Unit unit, BigInteger gasPrice, BigInteger gasLimit)
			throws ExecutionException, InterruptedException,
			TransactionTimeoutException {
		BigDecimal weiValue = Convert.toWei(value, unit);
		if (!Numeric.isIntegerValue(weiValue)) {
			throw new UnsupportedOperationException(
					"Non decimal Wei value provided: " + value + " "
							+ unit.toString() + " = " + weiValue + " Wei");
		} else {
			return this.send(toAddress, "", weiValue.toBigIntegerExact(),
					gasPrice, gasLimit);
		}
	}

	public Future<TransactionReceipt> sendFundsAsync(String toAddress,
			BigDecimal value, Unit unit) {
		return Async.run(() -> {
			return this.send(toAddress, value, unit);
		});
	}

	public Future<TransactionReceipt> sendFundsAsync(String toAddress,
			BigDecimal value, Unit unit, BigInteger gasPrice,
			BigInteger gasLimit) {
		return Async.run(() -> {
			return this.send(toAddress, value, unit, gasPrice, gasLimit);
		});
	}
	
	


	
	public static EthSendTransaction sendTx(Web3j web3j,
			Credentials credentials, String toAddress, BigDecimal value,
			Unit unit) throws InterruptedException, ExecutionException,
			TransactionTimeoutException {
		RawTransactionManager transactionManager = new RawTransactionManager(
				web3j, credentials);
		//check
		BigDecimal weiValue = Convert.toWei(value, unit);
		
		if (!Numeric.isIntegerValue(weiValue)) {
			throw new UnsupportedOperationException(
					"Non decimal Wei value provided: " + value + " "
							+ unit.toString() + " = " + weiValue + " Wei");
		} else {
			
			Transfer transfer = new Transfer(web3j, transactionManager);
			BigInteger gasPrice = transfer.GAS_PRICE;
			
			EthSendTransaction ethSendTransaction = transactionManager.sendTransaction(gasPrice, GAS_LIMIT, toAddress, "", weiValue.toBigIntegerExact());
			return ethSendTransaction;

		}

	}
	
	
	public static EthSendTransaction sendTokenContractTx(Web3j web3j,
			Credentials credentials, String toAddress, BigDecimal value,String data,TokenConvert.Unit unit) throws InterruptedException, ExecutionException,
			TransactionTimeoutException {
		RawTransactionManager transactionManager = new RawTransactionManager(
				web3j, credentials);
		//check
		
		BigDecimal weiValue = TokenConvert.toWei(value,unit);
		
		if (!Numeric.isIntegerValue(weiValue)) {
			throw new UnsupportedOperationException(
					"Non decimal Wei value provided: " + value + " "+unit.toString()+ "  = " + weiValue + " Wei");
		} else {
			
			Transfer transfer = new Transfer(web3j, transactionManager);
			BigInteger gasPrice = transfer.GAS_PRICE;
			
			EthSendTransaction ethSendTransaction = transactionManager.sendTransaction(gasPrice,GAS_LIMIT, toAddress, data, weiValue.toBigIntegerExact());
			return ethSendTransaction;

		}

	}
	
	
	public static EthSendTransaction sendContractTx(Web3j web3j,
			Credentials credentials, String toAddress, BigDecimal value,
			Unit unit,String data) throws InterruptedException, ExecutionException,
			TransactionTimeoutException {
		RawTransactionManager transactionManager = new RawTransactionManager(
				web3j, credentials);
		//check
		
		BigDecimal weiValue = Convert.toWei(value, unit);
		
		if (!Numeric.isIntegerValue(weiValue)) {
			throw new UnsupportedOperationException(
					"Non decimal Wei value provided: " + value + " "
							+ unit.toString() + " = " + weiValue + " Wei");
		} else {
			
			Transfer transfer = new Transfer(web3j, transactionManager);
			BigInteger gasPrice = transfer.GAS_PRICE;
			

			EthSendTransaction ethSendTransaction = transactionManager.sendTransaction(gasPrice,GAS_LIMIT, toAddress, data, weiValue.toBigIntegerExact());
			return ethSendTransaction;

		}
		

	}
	
	

	
	public static TransactionReceipt getTransactionReceipt(Web3j web3j,
			Credentials credentials,EthSendTransaction tx,int attempts,int sleepDuration) throws InterruptedException, ExecutionException, TransactionTimeoutException{
		RawTransactionManager transactionManager = new RawTransactionManager(
				web3j, credentials,attempts,sleepDuration);
		return transactionManager.processResponse(tx);

	}
	
	
	public static Future<TransactionReceipt> asyncGetTransactionReceipt(Web3j web3j,
			Credentials credentials,EthSendTransaction tx) throws InterruptedException, ExecutionException, TransactionTimeoutException{
		
		return Async.run(() -> {
			return Transfer.getTransactionReceipt(web3j,credentials,tx,40,15000);
		});

	}
	
	
	

	public static TransactionReceipt sendFunds(Web3j web3j,
			Credentials credentials, String toAddress, BigDecimal value,
			Unit unit) throws InterruptedException, ExecutionException,
			TransactionTimeoutException {
		RawTransactionManager transactionManager = new RawTransactionManager(
				web3j, credentials);
		return (new Transfer(web3j, transactionManager)).send(toAddress, value,
				unit);
	}
	
	
	

	public static Future<TransactionReceipt> sendFundsAsync(Web3j web3j,
			Credentials credentials, String toAddress, BigDecimal value,
			Unit unit) throws InterruptedException, ExecutionException,
			TransactionTimeoutException {
		RawTransactionManager transactionManager = new RawTransactionManager(
				web3j, credentials);
		return (new Transfer(web3j, transactionManager)).sendFundsAsync(
				toAddress, value, unit);
	}
}