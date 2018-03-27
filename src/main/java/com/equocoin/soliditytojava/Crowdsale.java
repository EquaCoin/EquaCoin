package com.equocoin.soliditytojava;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint128;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.1.1.
 */
public final class Crowdsale extends Contract {
    private static final String BINARY = "60606040526000805460ff19169055341561001957600080fd5b604051610f17380380610f17833981016040528080519190602001805182019190602001805191906020018051919060200180519190602001805191906020018051600b8054600160a060020a03338116600160a060020a031992831617909255600c8054928a16929091169190911790559150600d90508680516100a29291602001906101fa565b50670de0b6b3a764000084810260085583026009819055603c88024290810160048190556000600355600a8054600160a060020a031916600160a060020a0387811691909117909155600780546001608060020a0319166001608060020a038716179055600592909255600b54600c547f8162f6314365078618b76691719ca4f4c7ab1b30cd94a9f1e2bd2fffb3958e6794918416931691600d9190604051600160a060020a03808716825285166020820152606081018390526080810182905260a0604082018181528554600260001961010060018416150201909116049183018290529060c0830190869080156101dc5780601f106101b1576101008083540402835291602001916101dc565b820191906000526020600020905b8154815290600101906020018083116101bf57829003601f168201915b5050965050505050505060405180910390a150505050505050610295565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061023b57805160ff1916838001178555610268565b82800160010185558215610268579182015b8281111561026857825182559160200191906001019061024d565b50610274929150610278565b5090565b61029291905b80821115610274576000815560010161027e565b90565b610c73806102a46000396000f3006060604052600436106101035763ffffffff60e060020a60003504166302d05d3f81146101085780630783314d1461013757806329dcb0cf14610163578063369a768314610188578063387712421461019e57806338af3eed146101b15780633c8da588146101c45780636e66f6e9146101fc57806371ae8f021461020f57806386f3258614610222578063a6515a9814610237578063aa3676641461024a578063b2d5ae441461025d578063c19d93fb14610270578063c2052403146102a7578063c5c4744c146102ba578063ce845d1d146102cd578063d18e81b3146102e0578063d7bb99ba146102f3578063f851a440146102fb578063fe389e091461030e575b600080fd5b341561011357600080fd5b61011b610321565b604051600160a060020a03909116815260200160405180910390f35b341561014257600080fd5b61014f6004351515610330565b604051901515815260200160405180910390f35b341561016e57600080fd5b61017661048d565b60405190815260200160405180910390f35b341561019357600080fd5b61014f600435610493565b34156101a957600080fd5b6101766104fa565b34156101bc57600080fd5b61011b610500565b34156101cf57600080fd5b6101d761050f565b6040516fffffffffffffffffffffffffffffffff909116815260200160405180910390f35b341561020757600080fd5b61011b610527565b341561021a57600080fd5b610176610536565b341561022d57600080fd5b61023561053c565b005b341561024257600080fd5b6102356105a8565b341561025557600080fd5b6101766105d2565b341561026857600080fd5b61014f6105d8565b341561027b57600080fd5b6102836107f6565b6040518082600381111561029357fe5b60ff16815260200191505060405180910390f35b34156102b257600080fd5b6102356107ff565b34156102c557600080fd5b6101766108b2565b34156102d857600080fd5b6101766108b8565b34156102eb57600080fd5b6101766108be565b6101766108c4565b341561030657600080fd5b61011b610b55565b341561031957600080fd5b610235610b64565b600b54600160a060020a031681565b60006001915060045442111561041f57600854600254106103a0576000805460ff19166002908117909155547fee94ee98208684c00eeba940c34a6060b93671b249abd182b4771b74bf94e2dd9060405190815260200160405180910390a16103976107ff565b426006556103b2565b6000805460ff19166001179055426006555b7fe8ca64f431a8cfb9e9c33860d1c005f5efdf340d799f119d831935b54620b42460405160208082526006908201527f6661696c656400000000000000000000000000000000000000000000000000006040808301919091526060909101905180910390a1506000610488565b7fe8ca64f431a8cfb9e9c33860d1c005f5efdf340d799f119d831935b54620b42460405160208082526007908201527f73756363657373000000000000000000000000000000000000000000000000006040808301919091526060909101905180910390a15060015b919050565b60045481565b600a54600090600160a060020a03166342966c688360405160e060020a63ffffffff84160281526004810191909152602401600060405180830381600087803b15156104de57600080fd5b6102c65a03f115156104ef57600080fd5b506001949350505050565b60065481565b600c54600160a060020a031681565b6007546fffffffffffffffffffffffffffffffff1681565b600a54600160a060020a031681565b60085481565b600954158015906105505750600954600254145b156105a6576000805460ff19166002908117909155547fee94ee98208684c00eeba940c34a6060b93671b249abd182b4771b74bf94e2dd9060405190815260200160405180910390a16105a16107ff565b426006555b565b600e805473ffffffffffffffffffffffffffffffffffffffff191633600160a060020a0316179055565b60095481565b6000808060018060005460ff1660038111156105f057fe5b146105fa57600080fd5b600092505b60015483116107eb5733600160a060020a031660018481548110151561062157fe5b6000918252602090912060016002909202010154600160a060020a031614156107e057600180548490811061065257fe5b9060005260206000209060020201600001549150600060018481548110151561067757fe5b6000918252602090912060029091020155600180548490811061069657fe5b6000918252602090912060016002909202010154600160a060020a031682156108fc0283604051600060405180830381858888f19350505050151561076457816001848154811015156106e557fe5b60009182526020909120600290910201557f6d171103da11c2d8887e9c065d7dfc8ad71bda6a57d34917dae566350b897d4a60405160208082526006908201527f6661696c656400000000000000000000000000000000000000000000000000006040808301919091526060909101905180910390a1600093506107f0565b60028054839003908190556003557f6d171103da11c2d8887e9c065d7dfc8ad71bda6a57d34917dae566350b897d4a60405160208082526007908201527f73756363657373000000000000000000000000000000000000000000000000006040808301919091526060909101905180910390a1600193506107f0565b6001909201916105ff565b600093505b50505090565b60005460ff1681565b60028060005460ff16600381111561081357fe5b1461081d57600080fd5b600c54600160a060020a039081169030163180156108fc0290604051600060405180830381858888f19350505050151561085657600080fd5b6000805460ff19166003908117825555600c547fcc2a9192e91e4dd58845fd890b297de7bfda33e9f07f14bb2e4fe2237c86723d90600160a060020a0316604051600160a060020a03909116815260200160405180910390a150565b60025481565b60035481565b60055481565b600080808060005460ff1660038111156108da57fe5b146108e457600080fd5b6007546fffffffffffffffffffffffffffffffff1634101561090557600080fd5b6007546fffffffffffffffffffffffffffffffff163481151561092457fe5b061561092f57600080fd5b600180543493508082016109438382610bd6565b9160005260206000209060020201600060408051908101604052348152600160a060020a0333166020820152919050815181556020820151600191909101805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0390921691909117905550506002805434019081905560035560095415610a5557600a54600754600160a060020a039091169063364f48649033906fffffffffffffffffffffffffffffffff16858115156109f857fe5b0460405160e060020a63ffffffff8516028152600160a060020a0390921660048301526024820152604401600060405180830381600087803b1515610a3c57600080fd5b6102c65a03f11515610a4d57600080fd5b505050610ae4565b600a54600754600160a060020a03909116906379c650689033906fffffffffffffffffffffffffffffffff1685811515610a8b57fe5b0460405160e060020a63ffffffff8516028152600160a060020a0390921660048301526024820152604401600060405180830381600087803b1515610acf57600080fd5b6102c65a03f11515610ae057600080fd5b5050505b7f304e48bb03eae5e9bf3575d270648664895983e116a51773a65e9f3341b3b40e33346002546040518084600160a060020a0316600160a060020a03168152602001838152602001828152602001935050505060405180910390a1610b4761053c565b600154600019019250505090565b600e54600160a060020a031681565b600b5433600160a060020a03908116911614610b7f57600080fd5b600160005460ff166003811115610b9257fe5b1480610bae5750600260005460ff166003811115610bac57fe5b145b8015610bbf575042600654610e1001105b1515610bca57600080fd5b33600160a060020a0316ff5b815481835581811511610c0257600202816002028360005260206000209182019101610c029190610c07565b505050565b610c4491905b80821115610c40576000815560018101805473ffffffffffffffffffffffffffffffffffffffff19169055600201610c0d565b5090565b905600a165627a7a7230582084d9866b0892c5b18354b4741d85b36fb0b44c6a5f532526f0c9ce9752d56fe10029";

    private Crowdsale(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private Crowdsale(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<LogFundingReceivedEventResponse> getLogFundingReceivedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LogFundingReceived", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<LogFundingReceivedEventResponse> responses = new ArrayList<LogFundingReceivedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            LogFundingReceivedEventResponse typedResponse = new LogFundingReceivedEventResponse();
            typedResponse.addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.currentTotal = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogFundingReceivedEventResponse> logFundingReceivedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LogFundingReceived", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogFundingReceivedEventResponse>() {
            @Override
            public LogFundingReceivedEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                LogFundingReceivedEventResponse typedResponse = new LogFundingReceivedEventResponse();
                typedResponse.addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.currentTotal = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public List<LogWinnerPaidEventResponse> getLogWinnerPaidEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LogWinnerPaid", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<LogWinnerPaidEventResponse> responses = new ArrayList<LogWinnerPaidEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            LogWinnerPaidEventResponse typedResponse = new LogWinnerPaidEventResponse();
            typedResponse.winnerAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogWinnerPaidEventResponse> logWinnerPaidEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LogWinnerPaid", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogWinnerPaidEventResponse>() {
            @Override
            public LogWinnerPaidEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                LogWinnerPaidEventResponse typedResponse = new LogWinnerPaidEventResponse();
                typedResponse.winnerAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<LogFundingSuccessfulEventResponse> getLogFundingSuccessfulEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LogFundingSuccessful", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<LogFundingSuccessfulEventResponse> responses = new ArrayList<LogFundingSuccessfulEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            LogFundingSuccessfulEventResponse typedResponse = new LogFundingSuccessfulEventResponse();
            typedResponse.totalRaised = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogFundingSuccessfulEventResponse> logFundingSuccessfulEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LogFundingSuccessful", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogFundingSuccessfulEventResponse>() {
            @Override
            public LogFundingSuccessfulEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                LogFundingSuccessfulEventResponse typedResponse = new LogFundingSuccessfulEventResponse();
                typedResponse.totalRaised = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<StatusEventResponse> getStatusEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("status", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<StatusEventResponse> responses = new ArrayList<StatusEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            StatusEventResponse typedResponse = new StatusEventResponse();
            typedResponse.status = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<StatusEventResponse> statusEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("status", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, StatusEventResponse>() {
            @Override
            public StatusEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                StatusEventResponse typedResponse = new StatusEventResponse();
                typedResponse.status = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<RefundStatusEventResponse> getRefundStatusEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("RefundStatus", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<RefundStatusEventResponse> responses = new ArrayList<RefundStatusEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            RefundStatusEventResponse typedResponse = new RefundStatusEventResponse();
            typedResponse.RefundStatus = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<RefundStatusEventResponse> refundStatusEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("RefundStatus", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, RefundStatusEventResponse>() {
            @Override
            public RefundStatusEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                RefundStatusEventResponse typedResponse = new RefundStatusEventResponse();
                typedResponse.RefundStatus = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<LogFunderInitializedEventResponse> getLogFunderInitializedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LogFunderInitialized", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<LogFunderInitializedEventResponse> responses = new ArrayList<LogFunderInitializedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            LogFunderInitializedEventResponse typedResponse = new LogFunderInitializedEventResponse();
            typedResponse.creator = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.beneficiary = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.url = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse._fundingMaximumTargetInEther = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.deadline = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogFunderInitializedEventResponse> logFunderInitializedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LogFunderInitialized", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogFunderInitializedEventResponse>() {
            @Override
            public LogFunderInitializedEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                LogFunderInitializedEventResponse typedResponse = new LogFunderInitializedEventResponse();
                typedResponse.creator = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.beneficiary = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.url = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse._fundingMaximumTargetInEther = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.deadline = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<String> creator() {
        Function function = new Function("creator", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> isCrowdSaleEnd(Boolean endDate) {
        Function function = new Function(
                "isCrowdSaleEnd", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Bool(endDate)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> deadline() {
        Function function = new Function("deadline", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> burnAll(BigInteger amount) {
        Function function = new Function(
                "burnAll", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> completedAt() {
        Function function = new Function("completedAt", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> beneficiary() {
        Function function = new Function("beneficiary", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> priceInWei() {
        Function function = new Function("priceInWei", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint128>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> tokenReward() {
        Function function = new Function("tokenReward", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> fundingMinimumTargetInWei() {
        Function function = new Function("fundingMinimumTargetInWei", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> checkIfFundingCompleteOrExpired() {
        Function function = new Function(
                "checkIfFundingCompleteOrExpired", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> admined() {
        Function function = new Function(
                "admined", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> fundingMaximumTargetInWei() {
        Function function = new Function("fundingMaximumTargetInWei", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> getRefund() {
        Function function = new Function(
                "getRefund", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> state() {
        Function function = new Function("state", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> payOut() {
        Function function = new Function(
                "payOut", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalRaised() {
        Function function = new Function("totalRaised", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> currentBalance() {
        Function function = new Function("currentBalance", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> currentTime() {
        Function function = new Function("currentTime", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> contribute(BigInteger weiValue) {
        Function function = new Function(
                "contribute", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<String> admin() {
        Function function = new Function("admin", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> removeContract() {
        Function function = new Function(
                "removeContract", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<Crowdsale> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger _timeInMinutesForFundraising, String _campaignUrl, String _ifSuccessfulSendTo, BigInteger _fundingMinimumTargetInEther, BigInteger _fundingMaximumTargetInEther, String _addressOfTokenUsedAsReward, BigInteger _etherCostOfEachToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_timeInMinutesForFundraising), 
                new org.web3j.abi.datatypes.Utf8String(_campaignUrl), 
                new org.web3j.abi.datatypes.Address(_ifSuccessfulSendTo), 
                new org.web3j.abi.datatypes.generated.Uint256(_fundingMinimumTargetInEther), 
                new org.web3j.abi.datatypes.generated.Uint256(_fundingMaximumTargetInEther), 
                new org.web3j.abi.datatypes.Address(_addressOfTokenUsedAsReward), 
                new org.web3j.abi.datatypes.generated.Uint128(_etherCostOfEachToken)));
        return deployRemoteCall(Crowdsale.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<Crowdsale> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger _timeInMinutesForFundraising, String _campaignUrl, String _ifSuccessfulSendTo, BigInteger _fundingMinimumTargetInEther, BigInteger _fundingMaximumTargetInEther, String _addressOfTokenUsedAsReward, BigInteger _etherCostOfEachToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_timeInMinutesForFundraising), 
                new org.web3j.abi.datatypes.Utf8String(_campaignUrl), 
                new org.web3j.abi.datatypes.Address(_ifSuccessfulSendTo), 
                new org.web3j.abi.datatypes.generated.Uint256(_fundingMinimumTargetInEther), 
                new org.web3j.abi.datatypes.generated.Uint256(_fundingMaximumTargetInEther), 
                new org.web3j.abi.datatypes.Address(_addressOfTokenUsedAsReward), 
                new org.web3j.abi.datatypes.generated.Uint128(_etherCostOfEachToken)));
        return deployRemoteCall(Crowdsale.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static Crowdsale load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Crowdsale(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Crowdsale load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Crowdsale(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class LogFundingReceivedEventResponse {
        public String addr;

        public BigInteger amount;

        public BigInteger currentTotal;
    }

    public static class LogWinnerPaidEventResponse {
        public String winnerAddress;
    }

    public static class LogFundingSuccessfulEventResponse {
        public BigInteger totalRaised;
    }

    public static class StatusEventResponse {
        public String status;
    }

    public static class RefundStatusEventResponse {
        public String RefundStatus;
    }

    public static class LogFunderInitializedEventResponse {
        public String creator;

        public String beneficiary;

        public String url;

        public BigInteger _fundingMaximumTargetInEther;

        public BigInteger deadline;
    }
}
