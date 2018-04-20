package com.c2b.ethWallet.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Bytes4;
import org.web3j.abi.datatypes.generated.Uint128;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * Auto generated code.<br>
 * <strong>Do not modify!</strong><br>
 * Please use {@link org.web3j.codegen.SolidityFunctionWrapperGenerator} to update.
 *
 * <p>Generated with web3j version 2.2.1.
 */
public final class DSToken extends Contract {
    private static final String BINARY = "606060405260126006556000600755341561001657fe5b604051602080610fbe83398101604052515b5b60005b600160a060020a03331660009081526001602052604081208290558190555b5060048054600160a060020a03191633600160a060020a03169081179091556040517fce241d7ca1f669fee44b6fc00b8eba2df3bb514eed0f6f668f8f89096e81ed9490600090a25b60058190555b505b610f13806100ab6000396000f3006060604052361561011a5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde03811461011c57806307da68f51461013e578063095ea7b31461015057806313af40351461018357806318160ddd146101a157806323b872dd146101c3578063313ce567146101fc5780633452f51d1461021e5780635ac801fe1461025a57806369d3e20e1461026f57806370a082311461028d57806375f12b21146102bb5780637a9e5e4b146102df5780638402181f146102fd5780638da5cb5b1461033957806390bc16931461036557806395d89b4114610383578063a9059cbb146103a5578063be9a6555146103d8578063bf7e214f146103ea578063dd62ed3e14610416575bfe5b341561012457fe5b61012c61044a565b60408051918252519081900360200190f35b341561014657fe5b61014e610450565b005b341561015857fe5b61016f600160a060020a03600435166024356104f3565b604080519115158252519081900360200190f35b341561018b57fe5b61014e600160a060020a036004351661057e565b005b34156101a957fe5b61012c6105fb565b60408051918252519081900360200190f35b34156101cb57fe5b61016f600160a060020a0360043581169060243516604435610602565b604080519115158252519081900360200190f35b341561020457fe5b61012c61068f565b60408051918252519081900360200190f35b341561022657fe5b61016f600160a060020a03600435166001608060020a0360243516610695565b604080519115158252519081900360200190f35b341561026257fe5b61014e6004356106b3565b005b341561027757fe5b61014e6001608060020a03600435166106db565b005b341561029557fe5b61012c600160a060020a03600435166107d2565b60408051918252519081900360200190f35b34156102c357fe5b61016f6107f1565b604080519115158252519081900360200190f35b34156102e757fe5b61014e600160a060020a0360043516610801565b005b341561030557fe5b61016f600160a060020a03600435166001608060020a036024351661087e565b604080519115158252519081900360200190f35b341561034157fe5b61034961089d565b60408051600160a060020a039092168252519081900360200190f35b341561036d57fe5b61014e6001608060020a03600435166108ac565b005b341561038b57fe5b61012c6109a3565b60408051918252519081900360200190f35b34156103ad57fe5b61016f600160a060020a03600435166024356109a9565b604080519115158252519081900360200190f35b34156103e057fe5b61014e610a34565b005b34156103f257fe5b610349610ad1565b60408051600160a060020a039092168252519081900360200190f35b341561041e57fe5b61012c600160a060020a0360043581169060243516610ae0565b60408051918252519081900360200190f35b60075481565b61046e61046933600035600160e060020a031916610b0d565b610c15565b6040805134808252602082018381523693830184905260043593602435938493869333600160a060020a03169360008035600160e060020a031916949092606082018484808284376040519201829003965090945050505050a46004805474ff0000000000000000000000000000000000000000191660a060020a1790555b5b50505b565b60045460009061050d9060a060020a900460ff1615610c15565b6040805134808252602082018381523693830184905260043593602435938493869333600160a060020a03169360008035600160e060020a031916949092606082018484808284376040519201829003965090945050505050a46105718585610c26565b92505b5b50505b92915050565b61059c61046933600035600160e060020a031916610b0d565b610c15565b6004805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383811691909117918290556040519116907fce241d7ca1f669fee44b6fc00b8eba2df3bb514eed0f6f668f8f89096e81ed9490600090a25b5b50565b6000545b90565b60045460009061061c9060a060020a900460ff1615610c15565b6040805134808252602082018381523693830184905260043593602435938493869333600160a060020a03169360008035600160e060020a031916949092606082018484808284376040519201829003965090945050505050a4610681868686610c91565b92505b5b50505b9392505050565b60065481565b60006106aa83836001608060020a03166109a9565b90505b92915050565b6106d161046933600035600160e060020a031916610b0d565b610c15565b60078190555b5b50565b6106f961046933600035600160e060020a031916610b0d565b610c15565b6004546107109060a060020a900460ff1615610c15565b6040805134808252602082018381523693830184905260043593602435938493869333600160a060020a03169360008035600160e060020a031916949092606082018484808284376040519201829003965090945050505050a4600160a060020a033316600090815260016020526040902054610796906001608060020a038516610de8565b600160a060020a033316600090815260016020526040812091909155546107c6906001608060020a038516610de8565b6000555b5b50505b5b50565b600160a060020a0381166000908152600160205260409020545b919050565b60045460a060020a900460ff1681565b61081f61046933600035600160e060020a031916610b0d565b610c15565b6003805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383811691909117918290556040519116907f1abebea81bfa2637f28358c371278fb15ede7ea8dd28d2e03b112ff6d936ada490600090a25b5b50565b60006106aa8333846001608060020a0316610602565b90505b92915050565b600454600160a060020a031681565b6108ca61046933600035600160e060020a031916610b0d565b610c15565b6004546108e19060a060020a900460ff1615610c15565b6040805134808252602082018381523693830184905260043593602435938493869333600160a060020a03169360008035600160e060020a031916949092606082018484808284376040519201829003965090945050505050a4600160a060020a033316600090815260016020526040902054610967906001608060020a038516610dfc565b600160a060020a033316600090815260016020526040812091909155546107c6906001608060020a038516610dfc565b6000555b5b50505b5b50565b60055481565b6004546000906109c39060a060020a900460ff1615610c15565b6040805134808252602082018381523693830184905260043593602435938493869333600160a060020a03169360008035600160e060020a031916949092606082018484808284376040519201829003965090945050505050a46105718585610e10565b92505b5b50505b92915050565b610a5261046933600035600160e060020a031916610b0d565b610c15565b6040805134808252602082018381523693830184905260043593602435938493869333600160a060020a03169360008035600160e060020a031916949092606082018484808284376040519201829003965090945050505050a46004805474ff0000000000000000000000000000000000000000191690555b5b50505b565b600354600160a060020a031681565b600160a060020a038083166000908152600260209081526040808320938516835292905220545b92915050565b600030600160a060020a031683600160a060020a03161415610b3157506001610578565b600454600160a060020a0384811691161415610b4f57506001610578565b600354600160a060020a03161515610b6957506000610578565b600354604080516000602091820181905282517fb7009613000000000000000000000000000000000000000000000000000000008152600160a060020a0388811660048301523081166024830152600160e060020a0319881660448301529351939094169363b7009613936064808301949391928390030190829087803b1515610bef57fe5b6102c65a03f11515610bfd57fe5b50506040515191506105789050565b5b5b5b92915050565b8015156105f75760006000fd5b5b50565b600160a060020a03338116600081815260026020908152604080832094871680845294825280832086905580518681529051929493927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925929181900390910190a35060015b92915050565b600160a060020a03831660009081526001602052604081205482901015610cb457fe5b600160a060020a038085166000908152600260209081526040808320339094168352929052205482901015610ce557fe5b600160a060020a0380851660009081526002602090815260408083203390941683529290522054610d169083610dfc565b600160a060020a038086166000818152600260209081526040808320339095168352938152838220949094559081526001909252902054610d579083610dfc565b600160a060020a038086166000908152600160205260408082209390935590851681522054610d869083610de8565b600160a060020a0380851660008181526001602090815260409182902094909455805186815290519193928816927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef92918290030190a35060015b9392505050565b8082018281101561057857fe5b5b92915050565b8082038281111561057857fe5b5b92915050565b600160a060020a03331660009081526001602052604081205482901015610e3357fe5b600160a060020a033316600090815260016020526040902054610e569083610dfc565b600160a060020a033381166000908152600160205260408082209390935590851681522054610e859083610de8565b600160a060020a038085166000818152600160209081526040918290209490945580518681529051919333909316927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef92918290030190a35060015b929150505600a165627a7a72305820ffcb768c45be4af4d7bd89c8eed45b05607b9818709a46046b2b567e789b96d10029";

    private DSToken(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private DSToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<LogNoteEventResponse> getLogNoteEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LogNote", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes4>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<DynamicBytes>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<LogNoteEventResponse> responses = new ArrayList<LogNoteEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            LogNoteEventResponse typedResponse = new LogNoteEventResponse();
            typedResponse.sig = (Bytes4) eventValues.getIndexedValues().get(0);
            typedResponse.guy = (Address) eventValues.getIndexedValues().get(1);
            typedResponse.foo = (Bytes32) eventValues.getIndexedValues().get(2);
            typedResponse.bar = (Bytes32) eventValues.getIndexedValues().get(3);
            typedResponse.wad = (Uint256) eventValues.getNonIndexedValues().get(0);
            typedResponse.fax = (DynamicBytes) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogNoteEventResponse> logNoteEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LogNote", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes4>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<DynamicBytes>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogNoteEventResponse>() {
            @Override
            public LogNoteEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                LogNoteEventResponse typedResponse = new LogNoteEventResponse();
                typedResponse.sig = (Bytes4) eventValues.getIndexedValues().get(0);
                typedResponse.guy = (Address) eventValues.getIndexedValues().get(1);
                typedResponse.foo = (Bytes32) eventValues.getIndexedValues().get(2);
                typedResponse.bar = (Bytes32) eventValues.getIndexedValues().get(3);
                typedResponse.wad = (Uint256) eventValues.getNonIndexedValues().get(0);
                typedResponse.fax = (DynamicBytes) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public List<LogSetAuthorityEventResponse> getLogSetAuthorityEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LogSetAuthority", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<LogSetAuthorityEventResponse> responses = new ArrayList<LogSetAuthorityEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            LogSetAuthorityEventResponse typedResponse = new LogSetAuthorityEventResponse();
            typedResponse.authority = (Address) eventValues.getIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogSetAuthorityEventResponse> logSetAuthorityEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LogSetAuthority", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogSetAuthorityEventResponse>() {
            @Override
            public LogSetAuthorityEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                LogSetAuthorityEventResponse typedResponse = new LogSetAuthorityEventResponse();
                typedResponse.authority = (Address) eventValues.getIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public List<LogSetOwnerEventResponse> getLogSetOwnerEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LogSetOwner", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<LogSetOwnerEventResponse> responses = new ArrayList<LogSetOwnerEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            LogSetOwnerEventResponse typedResponse = new LogSetOwnerEventResponse();
            typedResponse.owner = (Address) eventValues.getIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogSetOwnerEventResponse> logSetOwnerEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LogSetOwner", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogSetOwnerEventResponse>() {
            @Override
            public LogSetOwnerEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                LogSetOwnerEventResponse typedResponse = new LogSetOwnerEventResponse();
                typedResponse.owner = (Address) eventValues.getIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.from = (Address) eventValues.getIndexedValues().get(0);
            typedResponse.to = (Address) eventValues.getIndexedValues().get(1);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.from = (Address) eventValues.getIndexedValues().get(0);
                typedResponse.to = (Address) eventValues.getIndexedValues().get(1);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.owner = (Address) eventValues.getIndexedValues().get(0);
            typedResponse.spender = (Address) eventValues.getIndexedValues().get(1);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.owner = (Address) eventValues.getIndexedValues().get(0);
                typedResponse.spender = (Address) eventValues.getIndexedValues().get(1);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public Future<Bytes32> name() {
        Function function = new Function("name", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> stop() {
        Function function = new Function("stop", Arrays.<Type>asList(), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<TransactionReceipt> approve(Address guy, Uint256 wad) {
        Function function = new Function("approve", Arrays.<Type>asList(guy, wad), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<TransactionReceipt> setOwner(Address owner_) {
        Function function = new Function("setOwner", Arrays.<Type>asList(owner_), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Uint256> totalSupply() {
        Function function = new Function("totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> transferFrom(Address src, Address dst, Uint256 wad) {
        Function function = new Function("transferFrom", Arrays.<Type>asList(src, dst, wad), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Uint256> decimals() {
        Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> push(Address dst, Uint128 wad) {
        Function function = new Function("push", Arrays.<Type>asList(dst, wad), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<TransactionReceipt> setName(Bytes32 name_) {
        Function function = new Function("setName", Arrays.<Type>asList(name_), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<TransactionReceipt> mint(Uint128 wad) {
        Function function = new Function("mint", Arrays.<Type>asList(wad), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Uint256> balanceOf(Address src) {
        Function function = new Function("balanceOf", 
                Arrays.<Type>asList(src), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Bool> stopped() {
        Function function = new Function("stopped", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> setAuthority(Address authority_) {
        Function function = new Function("setAuthority", Arrays.<Type>asList(authority_), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<TransactionReceipt> pull(Address src, Uint128 wad) {
        Function function = new Function("pull", Arrays.<Type>asList(src, wad), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Address> owner() {
        Function function = new Function("owner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> burn(Uint128 wad) {
        Function function = new Function("burn", Arrays.<Type>asList(wad), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Bytes32> symbol() {
        Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> transfer(Address dst, Uint256 wad) {
        Function function = new Function("transfer", Arrays.<Type>asList(dst, wad), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<TransactionReceipt> start() {
        Function function = new Function("start", Arrays.<Type>asList(), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Address> authority() {
        Function function = new Function("authority", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Uint256> allowance(Address src, Address guy) {
        Function function = new Function("allowance", 
                Arrays.<Type>asList(src, guy), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public static Future<DSToken> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, Bytes32 symbol_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(symbol_));
        return deployAsync(DSToken.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    public static Future<DSToken> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, Bytes32 symbol_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(symbol_));
        return deployAsync(DSToken.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    public static DSToken load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DSToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static DSToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DSToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class LogNoteEventResponse {
        public Bytes4 sig;

        public Address guy;

        public Bytes32 foo;

        public Bytes32 bar;

        public Uint256 wad;

        public DynamicBytes fax;
    }

    public static class LogSetAuthorityEventResponse {
        public Address authority;
    }

    public static class LogSetOwnerEventResponse {
        public Address owner;
    }

    public static class TransferEventResponse {
        public Address from;

        public Address to;

        public Uint256 value;
    }

    public static class ApprovalEventResponse {
        public Address owner;

        public Address spender;

        public Uint256 value;
    }
}
