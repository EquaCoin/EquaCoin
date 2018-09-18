package com.equocoin.soliditytojava;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple7;
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
public final class EquaZone extends Contract {
    private static final String BINARY = "60606040526000805460ff19168155600355341561001c57600080fd5b6040517f6869000000000000000000000000000000000000000000000000000000000000815260020160405180910390a06118878061005c6000396000f3006060604052600436106100b95763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416633197cbb681146100be5780633b41e19c146100e357806378e97925146101005780637991502e14610113578063bf8ca1aa146101a9578063c19d93fb14610387578063c2052403146103be578063c9d27afe146103d1578063d9186e8b146103ec578063ea233645146105b1578063eb23f18d146105c4578063ef30ce33146105f6575b600080fd5b34156100c957600080fd5b6100d1610784565b60405190815260200160405180910390f35b34156100ee57600080fd5b6100fe600435602435151561078a565b005b341561010b57600080fd5b6100d1610945565b341561011e57600080fd5b610132600160a060020a036004351661094b565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561016e578082015183820152602001610156565b50505050905090810190601f16801561019b5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101b457600080fd5b6101bf600435610a1b565b604051808060200180602001888152602001878152602001868152602001806020018060200185810385528c818151815260200191508051906020019080838360005b8381101561021a578082015183820152602001610202565b50505050905090810190601f1680156102475780820380516001836020036101000a031916815260200191505b5085810384528b818151815260200191508051906020019080838360005b8381101561027d578082015183820152602001610265565b50505050905090810190601f1680156102aa5780820380516001836020036101000a031916815260200191505b50858103835287818151815260200191508051906020019080838360005b838110156102e05780820151838201526020016102c8565b50505050905090810190601f16801561030d5780820380516001836020036101000a031916815260200191505b50858103825286818151815260200191508051906020019080838360005b8381101561034357808201518382015260200161032b565b50505050905090810190601f1680156103705780820380516001836020036101000a031916815260200191505b509b50505050505050505050505060405180910390f35b341561039257600080fd5b61039a610dab565b604051808260038111156103aa57fe5b60ff16815260200191505060405180910390f35b34156103c957600080fd5b6100fe610db4565b34156103dc57600080fd5b6100fe6004356024351515610e20565b34156103f757600080fd5b6100fe60048035600160a060020a03169060446024803590810190830135806020601f8201819004810201604051908101604052818152929190602084018383808284378201915050505050509190803590602001908201803590602001908080601f01602080910402602001604051908101604052818152929190602084018383808284378201915050505050509190803590602001908201803590602001908080601f01602080910402602001604051908101604052818152929190602084018383808284378201915050505050509190803590602001908201803590602001908080601f01602080910402602001604051908101604052818152929190602084018383808284378201915050505050509190803590602001909190803590602001908201803590602001908080601f01602080910402602001604051908101604052818152929190602084018383808284378201915050505050509190803590602001908201803590602001908080601f016020809104026020016040519081016040528181529291906020840183838082843750949650508435946020810135945060408101359350606001359150610fdc9050565b34156105bc57600080fd5b6100d16112b9565b34156105cf57600080fd5b6105da6004356112c0565b604051600160a060020a03909116815260200160405180910390f35b341561060157600080fd5b61060c6004356112f1565b6040518088600160a060020a0316600160a060020a0316815260200180602001806020018060200187815260200186815260200185815260200184810384528a818151815260200191508051906020019080838360005b8381101561067b578082015183820152602001610663565b50505050905090810190601f1680156106a85780820380516001836020036101000a031916815260200191505b50848103835289818151815260200191508051906020019080838360005b838110156106de5780820151838201526020016106c6565b50505050905090810190601f16801561070b5780820380516001836020036101000a031916815260200191505b50848103825288818151815260200191508051906020019080838360005b83811015610741578082015183820152602001610729565b50505050905090810190601f16801561076e5780820380516001836020036101000a031916815260200191505b509a505050505050505050505060405180910390f35b60025481565b600060068381548110151561079b57fe5b60009182526020808320600160a060020a0333168452600d600e90930201918201905260409091205490915060ff1615156108ec57811561085f57600160a060020a0333166000908152600d8201602052604090819020805460ff19166001179055600a82018054600201905560008051602061183c833981519152905160208082526007908201527f73756363657373000000000000000000000000000000000000000000000000006040808301919091526060909101905180910390a16108e7565b600160a060020a0333166000908152600d8201602052604090819020805460ff19166001908117909155600b83018054909101905560008051602061183c83398151915290516020808252601e908201527f70726f706f73616c2072656a6563746564207375636365737366756c6c7900006040808301919091526060909101905180910390a15b610940565b60008051602061183c83398151915260405160208082526010908201527f616c7265616479206163636570746564000000000000000000000000000000006040808301919091526060909101905180910390a15b505050565b60015481565b6109536115eb565b6005600083600160a060020a0316600160a060020a031681526020019081526020016000206001018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610a0f5780601f106109e457610100808354040283529160200191610a0f565b820191906000526020600020905b8154815290600101906020018083116109f257829003601f168201915b50505050509050919050565b610a236115eb565b610a2b6115eb565b6000806000610a386115eb565b610a406115eb565b6006805489908110610a4e57fe5b90600052602060002090600e0201600101600689815481101515610a6e57fe5b90600052602060002090600e020160020160068a815481101515610a8e57fe5b90600052602060002090600e02016005015460068b815481101515610aaf57fe5b90600052602060002090600e02016008015460068c815481101515610ad057fe5b90600052602060002090600e02016009015460068d815481101515610af157fe5b90600052602060002090600e020160060160068e815481101515610b1157fe5b90600052602060002090600e0201600701868054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610bb75780601f10610b8c57610100808354040283529160200191610bb7565b820191906000526020600020905b815481529060010190602001808311610b9a57829003601f168201915b50505050509650858054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610c535780601f10610c2857610100808354040283529160200191610c53565b820191906000526020600020905b815481529060010190602001808311610c3657829003601f168201915b50505050509550818054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610cef5780601f10610cc457610100808354040283529160200191610cef565b820191906000526020600020905b815481529060010190602001808311610cd257829003601f168201915b50505050509150808054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610d8b5780601f10610d6057610100808354040283529160200191610d8b565b820191906000526020600020905b815481529060010190602001808311610d6e57829003601f168201915b505050505090509650965096509650965096509650919395979092949650565b60005460ff1681565b60028060005460ff166003811115610dc857fe5b14610dd257600080fd5b600054600160a060020a0361010090910481169030163180156108fc0290604051600060405180830381858888f193505050501515610e1057600080fd5b506000805460ff19166003179055565b6000600683815481101515610e3157fe5b60009182526020808320600160a060020a0333168452600c600e90930201918201905260409091205490915060ff161515610f84578115610ef857600160a060020a0333166000908152600c8201602052604090819020805460ff19166001908117909155600883018054909101905560008051602061183c833981519152905160208082526007908201527f73756363657373000000000000000000000000000000000000000000000000006040808301919091526060909101905180910390a16108e7565b600160a060020a0333166000908152600c8201602052604090819020805460ff19166001908117909155600983018054909101905560008051602061183c833981519152905160208082526007908201527f73756363657373000000000000000000000000000000000000000000000000006040808301919091526060909101905180910390a1610940565b60008051602061183c8339815191526040516020808252600d908201527f616c726561647920766f746564000000000000000000000000000000000000006040808301919091526060909101905180910390a1505050565b610fe46115fd565b339c506000945060009150610180604051908101604052808e600160a060020a031681526020018d81526020018c81526020018b81526020018a815260200189815260200188815260200187815260200186815260200183815260200185815260200184815250905080600460008f600160a060020a0316600160a060020a031681526020019081526020016000206000820151815473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a03919091161781556020820151816001019080516110ba929160200190611683565b506040820151816002019080516110d5929160200190611683565b506060820151816003019080516110f0929160200190611683565b5060808201518160040190805161110b929160200190611683565b5060a0820151816005015560c082015181600601908051611130929160200190611683565b5060e08201518160070190805161114b929160200190611683565b506101008201518160080155610120820151816009015561014082015181600a0155610160820151600b9091015550600680546001810161118c8382611701565b600092835260209092208391600e02018151815473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a03919091161781556020820151816001019080516111e0929160200190611683565b506040820151816002019080516111fb929160200190611683565b50606082015181600301908051611216929160200190611683565b50608082015181600401908051611231929160200190611683565b5060a0820151816005015560c082015181600601908051611256929160200190611683565b5060e082015181600701908051611271929160200190611683565b506101008201518160080155610120820151816009015561014082015181600a0155610160820151600b90910155505060038054600101905550505050505050505050505050565b6003545b90565b60006006828154811015156112d157fe5b60009182526020909120600e9091020154600160a060020a031692915050565b60006112fb6115eb565b6113036115eb565b61130b6115eb565b600080600060068881548110151561131f57fe5b60009182526020909120600e909102015460068054600160a060020a03909216918a90811061134a57fe5b90600052602060002090600e020160020160068a81548110151561136a57fe5b90600052602060002090600e020160040160068b81548110151561138a57fe5b90600052602060002090600e020160030160068c8154811015156113aa57fe5b90600052602060002090600e02016008015460068d8154811015156113cb57fe5b90600052602060002090600e0201600a015460068e8154811015156113ec57fe5b90600052602060002090600e0201600b0154858054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156114935780601f1061146857610100808354040283529160200191611493565b820191906000526020600020905b81548152906001019060200180831161147657829003601f168201915b50505050509550848054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561152f5780601f106115045761010080835404028352916020019161152f565b820191906000526020600020905b81548152906001019060200180831161151257829003601f168201915b50505050509450838054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156115cb5780601f106115a0576101008083540402835291602001916115cb565b820191906000526020600020905b8154815290600101906020018083116115ae57829003601f168201915b505050505093509650965096509650965096509650919395979092949650565b60206040519081016040526000815290565b61018060405190810160405260008152602081016116196115eb565b81526020016116266115eb565b81526020016116336115eb565b81526020016116406115eb565b8152602001600081526020016116546115eb565b81526020016116616115eb565b8152602001600081526020016000815260200160008152602001600081525090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106116c457805160ff19168380011785556116f1565b828001600101855582156116f1579182015b828111156116f15782518255916020019190600101906116d6565b506116fd92915061172d565b5090565b81548183558181151161094057600e0281600e0283600052602060002091820191016109409190611747565b6112bd91905b808211156116fd5760008155600101611733565b6112bd91905b808211156116fd57805473ffffffffffffffffffffffffffffffffffffffff19168155600061177f60018301826117f4565b61178d6002830160006117f4565b61179b6003830160006117f4565b6117a96004830160006117f4565b60058201600090556006820160006117c191906117f4565b6117cf6007830160006117f4565b5060006008820181905560098201819055600a8201819055600b820155600e0161174d565b50805460018160011615610100020316600290046000825580601f1061181a5750611838565b601f016020900490600052602060002090810190611838919061172d565b505600e8ca64f431a8cfb9e9c33860d1c005f5efdf340d799f119d831935b54620b424a165627a7a7230582081d2caed638f79a6f5c34ecb2dc31a2352b8adb65985516bc42a316506fba6470029";

    private EquaZone(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private EquaZone(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<LogFunderInitializedEventResponse> getLogFunderInitializedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LogFunderInitialized", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<LogFunderInitializedEventResponse> responses = new ArrayList<LogFunderInitializedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            LogFunderInitializedEventResponse typedResponse = new LogFunderInitializedEventResponse();
            typedResponse.proposalCreatedBy = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.proposalTitle = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.proposalCategory = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.proposalDetails = (String) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse._proposalAmount = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.deadline = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogFunderInitializedEventResponse> logFunderInitializedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LogFunderInitialized", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogFunderInitializedEventResponse>() {
            @Override
            public LogFunderInitializedEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                LogFunderInitializedEventResponse typedResponse = new LogFunderInitializedEventResponse();
                typedResponse.proposalCreatedBy = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.proposalTitle = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.proposalCategory = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.proposalDetails = (String) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse._proposalAmount = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.deadline = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
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

    public List<IsCrowdSaleEndStatusEventResponse> getIsCrowdSaleEndStatusEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("isCrowdSaleEndStatus", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<IsCrowdSaleEndStatusEventResponse> responses = new ArrayList<IsCrowdSaleEndStatusEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            IsCrowdSaleEndStatusEventResponse typedResponse = new IsCrowdSaleEndStatusEventResponse();
            typedResponse.isCrowdSaleEndStatus = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<IsCrowdSaleEndStatusEventResponse> isCrowdSaleEndStatusEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("isCrowdSaleEndStatus", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, IsCrowdSaleEndStatusEventResponse>() {
            @Override
            public IsCrowdSaleEndStatusEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                IsCrowdSaleEndStatusEventResponse typedResponse = new IsCrowdSaleEndStatusEventResponse();
                typedResponse.isCrowdSaleEndStatus = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }
    @SuppressWarnings("rawtypes")
    public RemoteCall<BigInteger> endTime() {
        Function function = new Function("endTime", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }
    @SuppressWarnings("rawtypes")
    public RemoteCall<TransactionReceipt> accept(BigInteger proposalNumber, Boolean accepted) {
        Function function = new Function(
                "accept", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(proposalNumber), 
                new org.web3j.abi.datatypes.Bool(accepted)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }
    @SuppressWarnings("rawtypes")
    public RemoteCall<BigInteger> startTime() {
        Function function = new Function("startTime", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }
    @SuppressWarnings("rawtypes")
    public RemoteCall<String> getProposals(String _proposalCreatedBy) {
        Function function = new Function("getProposals", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_proposalCreatedBy)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }
    @SuppressWarnings("rawtypes")
    public RemoteCall<Tuple7<String, String, BigInteger, BigInteger, BigInteger, String, String>> getAllProposals(BigInteger index) {
        final Function function = new Function("getAllProposals", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        return new RemoteCall<Tuple7<String, String, BigInteger, BigInteger, BigInteger, String, String>>(
                new Callable<Tuple7<String, String, BigInteger, BigInteger, BigInteger, String, String>>() {
                    @Override
                    public Tuple7<String, String, BigInteger, BigInteger, BigInteger, String, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple7<String, String, BigInteger, BigInteger, BigInteger, String, String>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (String) results.get(5).getValue(), 
                                (String) results.get(6).getValue());
                    }
                });
    }
    @SuppressWarnings("rawtypes")
    public RemoteCall<BigInteger> state() {
        Function function = new Function("state", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }
    @SuppressWarnings("rawtypes")
    public RemoteCall<TransactionReceipt> payOut() {
        Function function = new Function(
                "payOut", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }
    @SuppressWarnings("rawtypes")
    public RemoteCall<TransactionReceipt> vote(BigInteger proposalNumber, Boolean voted) {
        Function function = new Function(
                "vote", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(proposalNumber), 
                new org.web3j.abi.datatypes.Bool(voted)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }
    @SuppressWarnings("rawtypes")
    public RemoteCall<TransactionReceipt> createProposal(String _proposalCreatedBy, String _proposalTitle, String _proposalCategory, String _proposalDetails, String _proposalCreatedByName, BigInteger _proposalAmnt, String _startDate, String _endDate, BigInteger _likeVotes, BigInteger _approve, BigInteger _reject, BigInteger _dislikeVotes) {
        Function function = new Function(
                "createProposal", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_proposalCreatedBy), 
                new org.web3j.abi.datatypes.Utf8String(_proposalTitle), 
                new org.web3j.abi.datatypes.Utf8String(_proposalCategory), 
                new org.web3j.abi.datatypes.Utf8String(_proposalDetails), 
                new org.web3j.abi.datatypes.Utf8String(_proposalCreatedByName), 
                new org.web3j.abi.datatypes.generated.Uint256(_proposalAmnt), 
                new org.web3j.abi.datatypes.Utf8String(_startDate), 
                new org.web3j.abi.datatypes.Utf8String(_endDate), 
                new org.web3j.abi.datatypes.generated.Uint256(_likeVotes), 
                new org.web3j.abi.datatypes.generated.Uint256(_approve), 
                new org.web3j.abi.datatypes.generated.Uint256(_reject), 
                new org.web3j.abi.datatypes.generated.Uint256(_dislikeVotes)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }
    @SuppressWarnings("rawtypes")
    public RemoteCall<BigInteger> countProposalList() {
        Function function = new Function("countProposalList", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }
    @SuppressWarnings("rawtypes")
    public RemoteCall<String> getAllProposalsCreatedBy(BigInteger index) {
        Function function = new Function("getAllProposalsCreatedBy", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }
    @SuppressWarnings("rawtypes")
    public RemoteCall<Tuple7<String, String, String, String, BigInteger, BigInteger, BigInteger>> getApproveProposals(BigInteger index) {
        final Function function = new Function("getApproveProposals", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple7<String, String, String, String, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple7<String, String, String, String, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple7<String, String, String, String, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple7<String, String, String, String, BigInteger, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (String) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue());
                    }
                });
    }

    public static RemoteCall<EquaZone> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(EquaZone.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<EquaZone> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(EquaZone.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static EquaZone load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new EquaZone(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static EquaZone load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new EquaZone(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class LogFunderInitializedEventResponse {
        public String proposalCreatedBy;

        public String proposalTitle;

        public String proposalCategory;

        public String proposalDetails;

        public BigInteger _proposalAmount;

        public BigInteger deadline;
    }

    public static class StatusEventResponse {
        public String status;
    }

    public static class IsCrowdSaleEndStatusEventResponse {
        public String isCrowdSaleEndStatus;
    }
}
