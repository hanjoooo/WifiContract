package contract;

/**
 * Created by khanj on 2018-10-07.
 */


import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.5.0.
 */
public class EtherWifiToken extends Contract {
    private static final String BINARY = "608060405260008054600160a060020a03191633179055610b2f806100256000396000f3006080604052600436106100775763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663409e229a811461007c57806341c0e1b51461016557806374fdc7fd1461017c57806376d4e178146101d7578063ca386496146102d3578063cba676d71461036a575b600080fd5b34801561008857600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261015194369492936024939284019190819084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a99988101979196509182019450925082915084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a9998810197919650918201945092508291508401838280828437509497506103c79650505050505050565b604080519115158252519081900360200190f35b34801561017157600080fd5b5061017a61058a565b005b34801561018857600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261017a94369492936024939284019190819084018382808284375094975050933594506105ad9350505050565b3480156101e357600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261023094369492936024939284019190819084018382808284375094975061061d9650505050505050565b604051808060200185600160a060020a0316600160a060020a0316815260200184815260200183151515158152602001828103825286818151815260200191508051906020019080838360005b8381101561029557818101518382015260200161027d565b50505050905090810190601f1680156102c25780820380516001836020036101000a031916815260200191505b509550505050505060405180910390f35b3480156102df57600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261017a94369492936024939284019190819084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a9998810197919650918201945092508291508401838280828437509497506108669650505050505050565b34801561037657600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261017a94369492936024939284019190819084018382808284375094975050505091351515925061096a915050565b60006001846040518082805190602001908083835b602083106103fb5780518252601f1990920191602091820191016103dc565b51815160209384036101000a6000190180199092169116179052920194855250604051938490030190922060030154600160a060020a0316159150610444905057506000610583565b60c06040519081016040528085815260200184815260200183815260200133600160a060020a0316815260200160008152602001600115158152506001856040518082805190602001908083835b602083106104b15780518252601f199092019160209182019101610492565b51815160209384036101000a60001901801990921691161790529201948552506040519384900381019093208451805191946104f294508593500190610a68565b50602082810151805161050b9260018501920190610a68565b5060408201518051610527916002840191602090910190610a68565b50606082015160038201805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a039092169190911790556080820151600482015560a0909101516005909101805460ff19169115159190911790555060015b9392505050565b600054600160a060020a03163314156105ab57600054600160a060020a0316ff5b565b806001836040518082805190602001908083835b602083106105e05780518252601f1990920191602091820191016105c1565b51815160209384036101000a6000190180199092169116179052920194855250604051938490030190922060040180549390930190925550505050565b606060008060006001856040518082805190602001908083835b602083106106565780518252601f199092019160209182019101610637565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206002016001866040518082805190602001908083835b602083106106be5780518252601f19909201916020918201910161069f565b51815160209384036101000a60001901801990921691161790529201948552506040519384900381018420600301548a51600160a060020a0390911694600194508b9350918291908401908083835b6020831061072c5780518252601f19909201916020918201910161070d565b51815160209384036101000a60001901801990921691161790529201948552506040519384900381018420600401548b519094600194508c9350918291908401908083835b602083106107905780518252601f199092019160209182019101610771565b518151600019602094850361010090810a82019283169219939093169190911790925294909201968752604080519788900382018820600501548b54601f600260018316159098029095011695909504928301829004820288018201905281875260ff9093169594508893509184019050828280156108505780601f1061082557610100808354040283529160200191610850565b820191906000526020600020905b81548152906001019060200180831161083357829003601f168201915b5050505050935093509350935093509193509193565b8133600160a060020a03166001826040518082805190602001908083835b602083106108a35780518252601f199092019160209182019101610884565b51815160209384036101000a6000190180199092169116179052920194855250604051938490030190922060030154600160a060020a03169290921491506108ec905057600080fd5b816001846040518082805190602001908083835b6020831061091f5780518252601f199092019160209182019101610900565b51815160209384036101000a60001901801990921691161790529201948552506040519384900381019093208451610964956002909201949190910192509050610a68565b50505050565b8133600160a060020a03166001826040518082805190602001908083835b602083106109a75780518252601f199092019160209182019101610988565b51815160209384036101000a6000190180199092169116179052920194855250604051938490030190922060030154600160a060020a03169290921491506109f0905057600080fd5b816001846040518082805190602001908083835b60208310610a235780518252601f199092019160209182019101610a04565b51815160209384036101000a60001901801990921691161790529201948552506040519384900301909220600501805460ff1916931515939093179092555050505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610aa957805160ff1916838001178555610ad6565b82800160010185558215610ad6579182015b82811115610ad6578251825591602001919060010190610abb565b50610ae2929150610ae6565b5090565b610b0091905b80821115610ae25760008155600101610aec565b905600a165627a7a72305820f5d1cfcb0d751ccb44614216cdfd3d888adad03b29f858d4c4a64d1a774190f80029";

    public static final String FUNC_ADDACCESSPOINT = "addAccessPoint";

    public static final String FUNC_KILL = "kill";

    public static final String FUNC_ADDCOUNT = "addCount";

    public static final String FUNC_GETACCESSPOINT = "getAccessPoint";

    public static final String FUNC_SETPASSWORD = "setPassword";

    public static final String FUNC_SETSTATUS = "setStatus";

    protected EtherWifiToken(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected EtherWifiToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> addAccessPoint(String _macAddress, String _ssid, String _password) {
        final Function function = new Function(
                FUNC_ADDACCESSPOINT,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_macAddress),
                        new org.web3j.abi.datatypes.Utf8String(_ssid),
                        new org.web3j.abi.datatypes.Utf8String(_password)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> kill() {
        final Function function = new Function(
                FUNC_KILL,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addCount(String _macAddress, BigInteger _num) {
        final Function function = new Function(
                FUNC_ADDCOUNT,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_macAddress),
                        new org.web3j.abi.datatypes.generated.Uint256(_num)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple4<String, String, BigInteger, Boolean>> getAccessPoint(String _macAddress) {
        final Function function = new Function(FUNC_GETACCESSPOINT,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_macAddress)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
        return new RemoteCall<Tuple4<String, String, BigInteger, Boolean>>(
                new Callable<Tuple4<String, String, BigInteger, Boolean>>() {
                    @Override
                    public Tuple4<String, String, BigInteger, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<String, String, BigInteger, Boolean>(
                                (String) results.get(0).getValue(),
                                (String) results.get(1).getValue(),
                                (BigInteger) results.get(2).getValue(),
                                (Boolean) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> setPassword(String _macAddress, String _password) {
        final Function function = new Function(
                FUNC_SETPASSWORD,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_macAddress),
                        new org.web3j.abi.datatypes.Utf8String(_password)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setStatus(String _macAddress, Boolean _switch) {
        final Function function = new Function(
                FUNC_SETSTATUS,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_macAddress),
                        new org.web3j.abi.datatypes.Bool(_switch)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<EtherWifiToken> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(EtherWifiToken.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<EtherWifiToken> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(EtherWifiToken.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static EtherWifiToken load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new EtherWifiToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static EtherWifiToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new EtherWifiToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}