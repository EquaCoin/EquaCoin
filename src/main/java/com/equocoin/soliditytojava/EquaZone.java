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
    private static final String BINARY = "60606040526000805460ff19168155600355341561001c57600080fd5b6040517f6869000000000000000000000000000000000000000000000000000000000000815260020160405180910390a0610f9d8061005c6000396000f3006060604052600436106100ae5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630151fa4081146100b3578063173ba514146101b05780633197cbb6146101c957806378e97925146101ee5780637991502e14610201578063bf8ca1aa14610297578063c19d93fb146103b1578063c2052403146103e8578063c9d27afe146103fb578063ea23364514610416578063eb23f18d14610429575b600080fd5b34156100be57600080fd5b6101ae60048035600160a060020a03169060446024803590810190830135806020601f8201819004810201604051908101604052818152929190602084018383808284378201915050505050509190803590602001908201803590602001908080601f01602080910402602001604051908101604052818152929190602084018383808284378201915050505050509190803590602001908201803590602001908080601f0160208091040260200160405190810160405281815292919060208401838380828437509496505084359460208101359450604081013593506060810135925060800135905061045b565b005b34156101bb57600080fd5b6101ae60043560243561067b565b34156101d457600080fd5b6101dc61078c565b60405190815260200160405180910390f35b34156101f957600080fd5b6101dc610792565b341561020c57600080fd5b610220600160a060020a0360043516610798565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561025c578082015183820152602001610244565b50505050905090810190601f1680156102895780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156102a257600080fd5b6102ad600435610868565b60405180806020018060200188815260200187815260200186815260200185815260200184815260200183810383528a818151815260200191508051906020019080838360005b8381101561030c5780820151838201526020016102f4565b50505050905090810190601f1680156103395780820380516001836020036101000a031916815260200191505b50838103825289818151815260200191508051906020019080838360005b8381101561036f578082015183820152602001610357565b50505050905090810190601f16801561039c5780820380516001836020036101000a031916815260200191505b50995050505050505050505060405180910390f35b34156103bc57600080fd5b6103c4610ab6565b604051808260038111156103d457fe5b60ff16815260200191505060405180910390f35b34156103f357600080fd5b6101ae610abf565b341561040657600080fd5b6101ae6004356024351515610b2b565b341561042157600080fd5b6101dc610d3d565b341561043457600080fd5b61043f600435610d44565b604051600160a060020a03909116815260200160405180910390f35b610463610d75565b42603c8087028201600155850201600255339950600092508291506101206040519081016040908152600160a060020a038c1680835260208084018d90528284018c9052606084018b9052608084018a905260015460a085015260025460c085015260e0840187905261010084018690526000918252600490522090915081908151815473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0391909116178155602082015181600101908051610527929160200190610dd4565b50604082015181600201908051610542929160200190610dd4565b5060608201518160030190805161055d929160200190610dd4565b506080820151816004015560a0820151816005015560c0820151816006015560e082015181600701556101008201516008909101555060068054600181016105a58382610e52565b600092835260209092208391600a02018151815473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a03919091161781556020820151816001019080516105f9929160200190610dd4565b50604082015181600201908051610614929160200190610dd4565b5060608201518160030190805161062f929160200190610dd4565b506080820151816004015560a0820151816005015560c0820151816006015560e08201518160070155610100820151600890910155505060038054600101905550505050505050505050565b600060068281548110151561068c57fe5b90600052602060002090600a020190508281600701541180156106b25750806006015442115b15610721577f8c8f4c2fde92a14d59b06d1c2a952390d56d207a75644f07b1716556a12dfd4460405160208082526007908201527f73756363657373000000000000000000000000000000000000000000000000006040808301919091526060909101905180910390a1610787565b7f8c8f4c2fde92a14d59b06d1c2a952390d56d207a75644f07b1716556a12dfd4460405160208082526006908201527f6661696c656400000000000000000000000000000000000000000000000000006040808301919091526060909101905180910390a15b505050565b60025481565b60015481565b6107a0610e7e565b6005600083600160a060020a0316600160a060020a031681526020019081526020016000206001018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561085c5780601f106108315761010080835404028352916020019161085c565b820191906000526020600020905b81548152906001019060200180831161083f57829003601f168201915b50505050509050919050565b610870610e7e565b610878610e7e565b600080600080600060068881548110151561088f57fe5b90600052602060002090600a02016001016006898154811015156108af57fe5b90600052602060002090600a020160020160068a8154811015156108cf57fe5b90600052602060002090600a02016004015460068b8154811015156108f057fe5b90600052602060002090600a02016007015460068c81548110151561091157fe5b90600052602060002090600a02016008015460068d81548110151561093257fe5b90600052602060002090600a02016005015460068e81548110151561095357fe5b90600052602060002090600a020160060154868054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109fa5780601f106109cf576101008083540402835291602001916109fa565b820191906000526020600020905b8154815290600101906020018083116109dd57829003601f168201915b50505050509650858054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610a965780601f10610a6b57610100808354040283529160200191610a96565b820191906000526020600020905b815481529060010190602001808311610a7957829003601f168201915b505050505095509650965096509650965096509650919395979092949650565b60005460ff1681565b60028060005460ff166003811115610ad357fe5b14610add57600080fd5b600054600160a060020a0361010090910481169030163180156108fc0290604051600060405180830381858888f193505050501515610b1b57600080fd5b506000805460ff19166003179055565b6000600683815481101515610b3c57fe5b60009182526020808320600160a060020a03331684526009600a90930201918201905260409091205490915060ff161515610cd357428160050154108015610b875750428160060154115b15610c68578115610bca57600160a060020a03331660009081526009820160205260409020805460ff191660019081179091556007820180549091019055610bfe565b600160a060020a03331660009081526009820160205260409020805460ff1916600190811790915560088201805490910190555b7fe8ca64f431a8cfb9e9c33860d1c005f5efdf340d799f119d831935b54620b42460405160208082526007908201527f73756363657373000000000000000000000000000000000000000000000000006040808301919091526060909101905180910390a1610cce565b7fe8ca64f431a8cfb9e9c33860d1c005f5efdf340d799f119d831935b54620b42460405160208082526006908201527f6661696c656400000000000000000000000000000000000000000000000000006040808301919091526060909101905180910390a15b610787565b7fe8ca64f431a8cfb9e9c33860d1c005f5efdf340d799f119d831935b54620b4246040516020808252600d908201527f616c726561647920766f746564000000000000000000000000000000000000006040808301919091526060909101905180910390a1505050565b6003545b90565b6000600682815481101515610d5557fe5b60009182526020909120600a9091020154600160a060020a031692915050565b6101206040519081016040526000815260208101610d91610e7e565b8152602001610d9e610e7e565b8152602001610dab610e7e565b815260200160008152602001600081526020016000815260200160008152602001600081525090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610e1557805160ff1916838001178555610e42565b82800160010185558215610e42579182015b82811115610e42578251825591602001919060010190610e27565b50610e4e929150610e90565b5090565b81548183558181151161078757600a0281600a0283600052602060002091820191016107879190610eaa565b60206040519081016040526000815290565b610d4191905b80821115610e4e5760008155600101610e96565b610d4191905b80821115610e4e57805473ffffffffffffffffffffffffffffffffffffffff191681556000610ee26001830182610f2a565b610ef0600283016000610f2a565b610efe600383016000610f2a565b506000600482018190556005820181905560068201819055600782018190556008820155600a01610eb0565b50805460018160011615610100020316600290046000825580601f10610f505750610f6e565b601f016020900490600052602060002090810190610f6e9190610e90565b505600a165627a7a723058208da1f65ea6aea28890873f5183de89d595e3f2d9f1523b4d9ac1d9d9f6596c890029";

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

    public RemoteCall<TransactionReceipt> createProposal(String _proposalCreatedBy, String _proposalTitle, String _proposalCategory, String _proposalDetails, BigInteger _proposalAmnt, BigInteger _startDate, BigInteger _endDate, BigInteger _likeVotes, BigInteger _dislikeVotes) {
        Function function = new Function(
                "createProposal", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_proposalCreatedBy), 
                new org.web3j.abi.datatypes.Utf8String(_proposalTitle), 
                new org.web3j.abi.datatypes.Utf8String(_proposalCategory), 
                new org.web3j.abi.datatypes.Utf8String(_proposalDetails), 
                new org.web3j.abi.datatypes.generated.Uint256(_proposalAmnt), 
                new org.web3j.abi.datatypes.generated.Uint256(_startDate), 
                new org.web3j.abi.datatypes.generated.Uint256(_endDate), 
                new org.web3j.abi.datatypes.generated.Uint256(_likeVotes), 
                new org.web3j.abi.datatypes.generated.Uint256(_dislikeVotes)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> isCrowdSaleEnd(BigInteger limit, BigInteger proposalNumber) {
        Function function = new Function(
                "isCrowdSaleEnd", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(limit), 
                new org.web3j.abi.datatypes.generated.Uint256(proposalNumber)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> endTime() {
        Function function = new Function("endTime", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> startTime() {
        Function function = new Function("startTime", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> getProposals(String _proposalCreatedBy) {
        Function function = new Function("getProposals", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_proposalCreatedBy)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Tuple7<String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>> getAllProposals(BigInteger index) {
        final Function function = new Function("getAllProposals", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple7<String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple7<String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple7<String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple7<String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue());
                    }
                });
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

    public RemoteCall<TransactionReceipt> vote(BigInteger proposalNumber, Boolean voted) {
        Function function = new Function(
                "vote", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(proposalNumber), 
                new org.web3j.abi.datatypes.Bool(voted)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> countProposalList() {
        Function function = new Function("countProposalList", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> getAllProposalsCreatedBy(BigInteger index) {
        Function function = new Function("getAllProposalsCreatedBy", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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
