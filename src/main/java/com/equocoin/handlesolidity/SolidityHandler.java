package com.equocoin.handlesolidity;

import com.equocoin.dto.CoinsInfoDTO;
import com.equocoin.dto.CrowdSaleProposalDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.model.CoinsInfo;
import com.equocoin.model.Config;
import com.equocoin.model.MintInfo;
import com.equocoin.model.RegisterInfo;
import com.equocoin.model.TransactionHistory;
import com.equocoin.repository.CoinInfoRepository;
import com.equocoin.repository.ConfigInfoRepository;
import com.equocoin.repository.MintInfoRepository;
import com.equocoin.repository.RegisterInfoRepository;
import com.equocoin.repository.TransactionInfoRepository;
import com.equocoin.service.impl.TokenUserServiceImpl;
import com.equocoin.soliditytojava.AssetToken;
import com.equocoin.soliditytojava.Crowdsale;
import com.equocoin.soliditytojava.EquaZone;
import com.equocoin.soliditytojava.EquaZone.IsCrowdSaleEndStatusEventResponse;
import com.equocoin.soliditytojava.Crowdsale.RefundStatusEventResponse;
import com.equocoin.soliditytojava.Crowdsale.StatusEventResponse;
import com.equocoin.utils.EncryptDecrypt;
import com.equocoin.utils.SessionCollector;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.tx.Contract;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

@Service
public class SolidityHandler {
	private static final Logger LOG = LoggerFactory.getLogger(SolidityHandler.class);
	private static final int COUNT = 1;
	private final Web3j web3j = Web3j.build(new HttpService());
	@Autowired
	private Environment env;
	@Autowired
	private SessionCollector sessionCollector;
	@Autowired
	private RegisterInfoRepository registerInfoRepository;
	@Autowired
	private ConfigInfoRepository configInfoRepository;
	@Autowired
	private TokenUserServiceImpl tokenUserServiceImpl;
	@Autowired
	private TransactionInfoRepository transactionInfoRepository;
	@Autowired
	private CoinInfoRepository coinInfoRepository;
	@Autowired
	private MintInfoRepository mintInfoRepository;
	BigInteger GAS = BigInteger.valueOf(4300000L);
	BigInteger GAS_PRICE = BigInteger.valueOf(2000000L);
	BigInteger initialWeiValue = BigInteger.valueOf(0L);
	private BigInteger GAS_LIMIT = BigInteger.valueOf(3000000L);

	public static EquaZone equaZone = null;

	public String tokenCreation(TokenDTO tokenDTO)
			throws IOException, CipherException, InterruptedException, ExecutionException {
		return null;
	}

	public boolean TransferToken(TokenDTO tokenDTO)
			throws IOException, CipherException, InterruptedException, ExecutionException {
		try {
			BigInteger e = Convert.toWei(tokenDTO.getTransferAmount(), Unit.ETHER).toBigInteger();
			LOG.info("******  amountCheck  **********" + e);
			LOG.info("******  fromAddress   **********" + tokenDTO.getFromAddress());
			Admin admin = Admin.build(new HttpService());
			PersonalUnlockAccount personalUnlockAccount = (PersonalUnlockAccount) admin
					.personalUnlockAccount(tokenDTO.getFromAddress(), tokenDTO.getWalletPassword()).send();
			LOG.info("personalUnlockAccount  " + personalUnlockAccount.accountUnlocked());
			personalUnlockAccount.accountUnlocked();
			if (Boolean.valueOf(true).booleanValue()) {
				LOG.info("Account unlocked successfully");
				Function function = new Function("contribute", Arrays.asList(new Type[0]), Collections.emptyList());
				String encodedFunction = FunctionEncoder.encode(function);
				EthGetTransactionCount ethGetTransactionCount = (EthGetTransactionCount) this.web3j
						.ethGetTransactionCount(tokenDTO.getFromAddress(), DefaultBlockParameterName.LATEST).sendAsync()
						.get();
				BigInteger nonce = ethGetTransactionCount.getTransactionCount();
				Transaction transaction = Transaction.createFunctionCallTransaction(tokenDTO.getFromAddress(), nonce,
						Contract.GAS_PRICE, Contract.GAS_LIMIT, this.env.getProperty("crowdsale.address"), e,
						encodedFunction);
				LOG.info(" ********* transaction ********  " + transaction.toString());
				EthSendTransaction transactionResponse = (EthSendTransaction) this.web3j.ethSendTransaction(transaction)
						.sendAsync().get();
				String transactionHash = transactionResponse.getTransactionHash();
				LOG.info(" ********* transactionHash  ********  " + transactionHash);
				EthGetTransactionReceipt transactionReceipt = (EthGetTransactionReceipt) this.web3j
						.ethGetTransactionReceipt(transactionHash).send();
				LOG.info(" ********* transactionReceipt  ********  " + transactionReceipt.getResult());
				Thread.sleep(90000L);
				tokenDTO.setCentralAdmin(tokenDTO.getFromAddress());
				boolean status = this.tokenUserServiceImpl.checkMainbalance(tokenDTO);
				LOG.info(" ********* checkMainbalance  ********  " + status);
				if (!status) {
					return false;
				}

				if (transactionResponse.getResult() != null) {
					TransactionHistory transactionHistory = new TransactionHistory();
					transactionHistory.setFromAddress(tokenDTO.getFromAddress());
					transactionHistory.setToAddress(this.env.getProperty("crowdsale.address"));
					transactionHistory.setAmount(tokenDTO.getTransferAmount().doubleValue());
					//transactionHistory.setAmount(Double.valueOf(tokenDTO.getTransferAmount().doubleValue()));
					LOG.info("transfer amount" + tokenDTO.getTransferAmount());
					transactionHistory.setCreatedDate(new Date());
					this.transactionInfoRepository.save(transactionHistory);
					if (transactionHistory != null) {
						return true;
					}
				}
			}

			return false;
		} catch (Exception arg14) {
			arg14.printStackTrace();
			return false;
		}
	}

	public boolean TransferAdminship(TokenDTO tokenDTO)
			throws IOException, CipherException, InterruptedException, ExecutionException {
		return false;
	}

	public List<TokenDTO> listTokens() throws InterruptedException, ExecutionException, IOException, CipherException {
		return null;
	}

	public boolean tokenCancel(TokenDTO tokenDTO) throws Exception {

	
			LOG.info("token balance" + tokenDTO.getTokenBalance());
			Credentials credentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
				env.getProperty("credentials.address"));
			AssetToken assetToken = AssetToken.load(env.getProperty("token.address"), web3j, credentials,
					BigInteger.valueOf(1000000000L), BigInteger.valueOf(100000L));
			new Uint256(tokenDTO.getTokenBalance());
			TransactionReceipt transactionReceipt = assetToken.burn(tokenDTO.getTokenBalance()).send();
			System.out.println("check condition");
			if (transactionReceipt.getBlockHash() != null) {
				return true;
			}
			
			
		return false;
	}

	public boolean TransferCoin(TokenDTO tokenDTO) throws Exception {
		Credentials credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
				this.env.getProperty("credentials.address"));
		AssetToken assetToken = AssetToken.load(this.env.getProperty("token.address"), this.web3j, credentials,
				BigInteger.valueOf(1000000000L), BigInteger.valueOf(3000000L));
		BigInteger amount=tokenDTO.getRequestToken().toBigInteger();
		TransactionReceipt transactionReceipt =assetToken
				.transfer(tokenDTO.getToAddress(), amount).send();
		if(transactionReceipt != null){
			TransactionHistory transactionHistory = new TransactionHistory();
			transactionHistory.setFromAddress(this.env.getProperty("main.address"));
			transactionHistory.setToAddress(tokenDTO.getToAddress());
			transactionHistory.setAmount(new Double(0));
			transactionHistory.setToken(tokenDTO.getRequestToken().toBigInteger());
			transactionHistory.setPaymentMode(env.getProperty("admin.payment"));
			transactionHistory.setCreatedDate(new Date());
			this.transactionInfoRepository.save(transactionHistory);
			if (transactionHistory != null) {
				return true;
			}
		}
		return transactionReceipt != null;
	}

	public String isValidTokenBalForCrowdsale(TokenDTO tokenDTO) throws Exception {
		Credentials credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
				this.env.getProperty("credentials.address"));
		AssetToken assetToken = AssetToken.load(this.env.getProperty("token.address"), this.web3j, credentials,
				BigInteger.valueOf(3000000L), BigInteger.valueOf(3000000L));
		BigInteger crowdsaleBal = (BigInteger) assetToken.balanceOf(this.env.getProperty("main.address")).send();
		BigInteger token=tokenDTO.getRequestToken().toBigInteger();
		if(BigInteger.ZERO.equals(token)){
			return "Please enter valid token";
		}
		if (BigInteger.ZERO.equals(crowdsaleBal)) {
			return "SOLD OUT";
		} else {
			int count = crowdsaleBal.compareTo(tokenDTO.getRequestToken().toBigInteger());
			return count == -1 ? "We have only " + crowdsaleBal + "tokens left" : "success";
		}
	}

	

	public boolean isCrowdSaleExits(TokenDTO tokenDTO) throws Exception {
		Credentials credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
				this.env.getProperty("credentials.addres"));
		Crowdsale crowdSale = Crowdsale.load(this.env.getProperty("crowdsale.address"), this.web3j, credentials,
				this.GAS_PRICE, this.GAS);
		TransactionReceipt transactionReceipt = (TransactionReceipt) crowdSale.isCrowdSaleEnd(Boolean.valueOf(true))
				.send();
		List StatusEventResponse = crowdSale.getStatusEvents(transactionReceipt);
		LOG.info("go to status" + StatusEventResponse.size());
		if (StatusEventResponse.size() > 0) {
			Iterator arg5 = StatusEventResponse.iterator();

			while (arg5.hasNext()) {
				StatusEventResponse StatusEventResponses = (StatusEventResponse) arg5.next();
				LOG.info("check status" + StatusEventResponses.status);
				if (StatusEventResponses.status.equalsIgnoreCase("success")) {
					return true;
				}
			}
		}

		return false;
	}

	public String getRefund(TokenDTO tokenDTO) throws Exception {
		Credentials credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),
				tokenDTO.getWalletAddress());
		Crowdsale crowdSale = Crowdsale.load(this.env.getProperty("crowdsale.address"), this.web3j, credentials,
				this.GAS_PRICE, this.GAS);
		BigInteger CrowdSaleState = (BigInteger) crowdSale.state().send();
		LOG.info("CrowdSaleState" + CrowdSaleState);
		if (CrowdSaleState.intValue() == 3) {
			return "Your ICO has been successful";
		} else {
			if (CrowdSaleState.intValue() == 1) {
				TransactionReceipt transactionReceipt = (TransactionReceipt) crowdSale.getRefund().send();
				List StatusEventResponse = crowdSale.getRefundStatusEvents(transactionReceipt);
				if (StatusEventResponse.size() > 0) {
					Iterator arg6 = StatusEventResponse.iterator();

					while (arg6.hasNext()) {
						RefundStatusEventResponse StatusEventResponses = (RefundStatusEventResponse) arg6.next();
						LOG.info("isCrowdSaleFailure" + StatusEventResponses.RefundStatus);
						if (StatusEventResponses.RefundStatus.equalsIgnoreCase("success")) {
							return "ICO Failed, ETH Refunded";
						}
					}
				}
			}

			return "No ICO yet";
		}
	}

	public void deployContract() throws IOException, CipherException, InterruptedException, ExecutionException {
		Credentials credentials = SolidityHandler.getCredentials();
		// equaZone = EquaZone.deploy(web3j, credentials, Contract.GAS_PRICE,
		// Contract.GAS_LIMIT).sendAsync().get();
		equaZone = EquaZone.load("0xF9543cB757766143dCEb0449530C54083D8e3453", web3j, credentials, GAS_PRICE,
				GAS_LIMIT);
		LOG.info("inside equaZone " + equaZone);
		LOG.info("Inside Cryptogenesis Contract deployment");
	}

	public static Credentials getCredentials() throws IOException, CipherException {
		Credentials credentials = WalletUtils.loadCredentials("User0505",
				"E://Ethereum//private-network//keystore//UTC--2018-01-30T10-10-32.557680600Z--e575c6dedd9f1eb6c3f0c48d14821273ef70d55e");
		return credentials;
	}

	public boolean CrowdSaleCreate(CrowdSaleProposalDTO crowdSaleDTO)
			throws IOException, CipherException, InterruptedException, ExecutionException, ParseException {

	
		  Date dt = new Date();
		  Calendar c = Calendar.getInstance(); 
		  c.setTime(dt); 
		  c.add(Calendar.DATE, 1);
		  dt = c.getTime();
/*		  System.out.println("StartDate::::::::" + crowdSaleDTO.getStartDates());
		  System.out.println("EndDate::::::::" + crowdSaleDTO.getEndDates());
		  */
		  
		
		
		
		
		long startTime = getMinutesBetweenDates(new Date());
		long endTime = getMinutesBetweenDates(dt);
		crowdSaleDTO.setLike(new BigInteger("0"));
		crowdSaleDTO.setDisLike(new BigInteger("0"));
		System.out.println("CreatedBy::::::::" + crowdSaleDTO.getProposalCreatedBy());
		System.out.println("proposalTitle::::::::" + crowdSaleDTO.getProposalTitle());
		System.out.println("proposalCategory::::::::" + crowdSaleDTO.getProposalCategory());
		System.out.println("proposalDetails::::::::" + crowdSaleDTO.getProposalDetails());
		System.out.println("proposalAmnt::::::::" + crowdSaleDTO.getProposalAmount());
		System.out.println("StartDate::::::::" + startTime);
		System.out.println("EndDate::::::::" + endTime);
		if (equaZone == null) {
			SolidityHandler solidityHandler = new SolidityHandler();
			solidityHandler.deployContract();
		}
		TransactionReceipt transactionReceipt = (TransactionReceipt) equaZone
				.createProposal(crowdSaleDTO.getProposalCreatedBy(), crowdSaleDTO.getProposalTitle(),
						crowdSaleDTO.getProposalCategory(), crowdSaleDTO.getProposalDetails(),
						crowdSaleDTO.getProposalAmount(), BigInteger.valueOf(startTime), BigInteger.valueOf(endTime),
						crowdSaleDTO.getLike(), crowdSaleDTO.getDisLike())
				.sendAsync().get();
		LOG.info("create proposal in inside");
		if (transactionReceipt != null) {
			LOG.info("Token Address is  :" + equaZone.getContractAddress());
			return true;
		} else {
			return false;
		}
	}

	public Long getMinutesBetweenDates(Date inputDate) throws ParseException {
		Date date = new Date();
		DateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
		String s = formatter.format(date);
		LOG.info("s " + s);

		Date d1 = null;
		Date d2 = null;
		try {

			d2 = new Date(s);
			LOG.info("currentDate " + d2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		long duration = inputDate.getTime() - date.getTime();

		long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
		long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
		LOG.info("diffInMinutes   " + diffInMinutes);
		return diffInMinutes;
	}

	public List<CrowdSaleProposalDTO> crowdsaleProposalList() throws Exception {
		ArrayList crowdSaleList = new ArrayList();

		if (equaZone == null) {
			SolidityHandler solidityHandler = new SolidityHandler();
			solidityHandler.deployContract();
		}
		LOG.info("equaZone    " + equaZone);
		BigInteger count = (BigInteger) equaZone.countProposalList().send();
		int tokenCount = count.intValue();
		LOG.info("tokenCount " + tokenCount);
		new ArrayList();
		if (tokenCount != 0) {
			for (int i = 0; i < tokenCount; ++i) {
				BigInteger bi = BigInteger.valueOf((long) i);
				Tuple7 Tuple7 = (Tuple7) equaZone.getAllProposals(bi).send();
				CrowdSaleProposalDTO crowdSaleProposalDTO = new CrowdSaleProposalDTO();
				crowdSaleProposalDTO.setProposalCreatedBy((String) Tuple7.getValue1());
				crowdSaleProposalDTO.setProposalTitle((String) Tuple7.getValue2());
				crowdSaleProposalDTO.setProposalAmount((BigInteger) Tuple7.getValue3());
				crowdSaleProposalDTO.setLike((BigInteger) Tuple7.getValue4());
				crowdSaleProposalDTO.setDisLike((BigInteger) Tuple7.getValue5());
				Date startTime = new Date((long) ((BigInteger) Tuple7.getValue6()).intValue());
				Date endTime = new Date((long) ((BigInteger) Tuple7.getValue7()).intValue());
				crowdSaleProposalDTO.setStartDate(startTime);
				crowdSaleProposalDTO.setEndDate(endTime);
				crowdSaleList.add(crowdSaleProposalDTO);
			}

			return crowdSaleList;
		}

		return null;
	}

	public boolean votingForCrowdSale(CrowdSaleProposalDTO crowdSaleDTO)
			throws IOException, CipherException, InterruptedException, ExecutionException {
		if (equaZone == null) {
			SolidityHandler solidityHandler = new SolidityHandler();
			solidityHandler.deployContract();
		}
		System.out.println("ProposalNumber::::::::" + crowdSaleDTO.getProposalNumber());
		System.out.println("Voted::::::::" + crowdSaleDTO.getVoted());
		TransactionReceipt transactionReceipt = (TransactionReceipt) equaZone
				.vote(crowdSaleDTO.getProposalNumber(), crowdSaleDTO.getVoted()).sendAsync().get();
		LOG.info("create proposal in inside");
		if (transactionReceipt != null) {

			List<com.equocoin.soliditytojava.EquaZone.StatusEventResponse> StatusEventResponse = equaZone
					.getStatusEvents(transactionReceipt);

			LOG.info("go to status" + StatusEventResponse.size());
			if (StatusEventResponse.size() > 0) {
				// Iterator arg5 = StatusEventResponse.iterator();

				for (com.equocoin.soliditytojava.EquaZone.StatusEventResponse StatusEventResponses : StatusEventResponse) {

					LOG.info("check status" + StatusEventResponses.status);
					if (StatusEventResponses.status.equalsIgnoreCase("success")) {
						return true;
					}
				}
			}

		}
		return false;
	}

	public List<CrowdSaleProposalDTO> crowdsaleProposalCategoryList(String categoryName) throws Exception {
		ArrayList crowdSaleList = new ArrayList();

		if (equaZone == null) {
			SolidityHandler solidityHandler = new SolidityHandler();
			solidityHandler.deployContract();
		}
		BigInteger count = (BigInteger) equaZone.countProposalList().send();
		int tokenCount = count.intValue();
		LOG.info("tokenCount " + tokenCount);
		new ArrayList();
		if (tokenCount != 0) {
			for (int i = 0; i < tokenCount; ++i) {
				BigInteger bi = BigInteger.valueOf((long) i);
				Tuple7 Tuple7 = (Tuple7) equaZone.getAllProposals(bi).send();
				CrowdSaleProposalDTO crowdSaleProposalDTO = new CrowdSaleProposalDTO();
				if (categoryName.equalsIgnoreCase((String) Tuple7.getValue2())) {
					crowdSaleProposalDTO.setProposalTitle((String) Tuple7.getValue1());
					crowdSaleProposalDTO.setProposalCategory((String) Tuple7.getValue2());
					crowdSaleProposalDTO.setProposalAmount((BigInteger) Tuple7.getValue3());
					crowdSaleProposalDTO.setLike((BigInteger) Tuple7.getValue4());
					crowdSaleProposalDTO.setDisLike((BigInteger) Tuple7.getValue5());
					Date startTime = new Date((long) ((BigInteger) Tuple7.getValue6()).intValue());
					Date endTime = new Date((long) ((BigInteger) Tuple7.getValue7()).intValue());
					crowdSaleProposalDTO.setStartDate(startTime);
					crowdSaleProposalDTO.setEndDate(endTime);
					crowdSaleList.add(crowdSaleProposalDTO);
				}
			}
		}

		return crowdSaleList;
	}

	public List<CrowdSaleProposalDTO> listOwnProposal(String address) throws Exception {
		ArrayList crowdSaleList = new ArrayList();
		if (equaZone == null) {
			SolidityHandler solidityHandler = new SolidityHandler();
			solidityHandler.deployContract();
		}

		BigInteger count = (BigInteger) equaZone.countProposalList().send();
		int tokenCount = count.intValue();
		LOG.info("tokenCount " + tokenCount);
		new ArrayList();
		if (tokenCount != 0) {
			for (int i = 0; i < tokenCount; ++i) {
				BigInteger bi = BigInteger.valueOf((long) i);
				Tuple7 Tuple7 = (Tuple7) equaZone.getAllProposals(bi).send();
				String createdBy = (String) equaZone.getAllProposalsCreatedBy(bi).sendAsync().get();
				CrowdSaleProposalDTO crowdSaleProposalDTO = new CrowdSaleProposalDTO();
				if (address.equalsIgnoreCase(createdBy)) {
					crowdSaleProposalDTO.setProposalTitle((String) Tuple7.getValue1());
					crowdSaleProposalDTO.setProposalCategory((String) Tuple7.getValue2());
					crowdSaleProposalDTO.setProposalAmount((BigInteger) Tuple7.getValue3());
					crowdSaleProposalDTO.setLike((BigInteger) Tuple7.getValue4());
					crowdSaleProposalDTO.setDisLike((BigInteger) Tuple7.getValue5());
					Date startTime = new Date((long) ((BigInteger) Tuple7.getValue6()).intValue());
					Date endTime = new Date((long) ((BigInteger) Tuple7.getValue7()).intValue());
					crowdSaleProposalDTO.setStartDate(startTime);
					crowdSaleProposalDTO.setEndDate(endTime);
					crowdSaleList.add(crowdSaleProposalDTO);
				}
			}
		}
		LOG.info("crowdSaleList " + crowdSaleList);
		return crowdSaleList;
	}

	public List<CrowdSaleProposalDTO> listOtherProposal(String address) throws Exception {
		ArrayList crowdSaleList = new ArrayList();

		if (equaZone == null) {
			SolidityHandler solidityHandler = new SolidityHandler();
			solidityHandler.deployContract();
		}
		BigInteger count = (BigInteger) equaZone.countProposalList().send();
		int tokenCount = count.intValue();
		LOG.info("tokenCount " + tokenCount);
		new ArrayList();
		if (tokenCount != 0) {
			for (int i = 0; i < tokenCount; ++i) {
				BigInteger bi = BigInteger.valueOf((long) i);
				Tuple7 Tuple7 = (Tuple7) equaZone.getAllProposals(bi).send();
				String createdBy = (String) equaZone.getAllProposalsCreatedBy(bi).sendAsync().get();
				CrowdSaleProposalDTO crowdSaleProposalDTO = new CrowdSaleProposalDTO();
				if (!address.equalsIgnoreCase(createdBy)) {
					crowdSaleProposalDTO.setProposalTitle((String) Tuple7.getValue1());
					crowdSaleProposalDTO.setProposalCategory((String) Tuple7.getValue2());
					crowdSaleProposalDTO.setProposalAmount((BigInteger) Tuple7.getValue3());
					crowdSaleProposalDTO.setLike((BigInteger) Tuple7.getValue4());
					crowdSaleProposalDTO.setDisLike((BigInteger) Tuple7.getValue5());
					Date startTime = new Date((long) ((BigInteger) Tuple7.getValue6()).intValue());
					Date endTime = new Date((long) ((BigInteger) Tuple7.getValue7()).intValue());
					crowdSaleProposalDTO.setStartDate(startTime);
					crowdSaleProposalDTO.setEndDate(endTime);
					crowdSaleList.add(crowdSaleProposalDTO);
				}
			}
		}

		return crowdSaleList;
	}

	public List<CrowdSaleProposalDTO> listWonProposal() throws Exception {
		ArrayList crowdSaleList = new ArrayList();

		if (equaZone == null) {
			SolidityHandler solidityHandler = new SolidityHandler();
			solidityHandler.deployContract();
		}
		BigInteger count = (BigInteger) equaZone.countProposalList().send();
		int tokenCount = count.intValue();
		LOG.info("tokenCount " + tokenCount);
		new ArrayList();
		if (tokenCount != 0) {
			for (int i = 0; i < tokenCount; ++i) {
				BigInteger bi = BigInteger.valueOf((long) i);
				Tuple7 Tuple7 = (Tuple7) equaZone.getAllProposals(bi).send();
				String createdBy = (String) equaZone.getAllProposalsCreatedBy(bi).sendAsync().get();
				CrowdSaleProposalDTO crowdSaleProposalDTO = new CrowdSaleProposalDTO();
				BigInteger like = (BigInteger) Tuple7.getValue4();
				int check = like.compareTo(new BigInteger("1"));
				if (check == 1) {
					crowdSaleProposalDTO.setProposalTitle((String) Tuple7.getValue1());
					crowdSaleProposalDTO.setProposalCategory((String) Tuple7.getValue2());
					crowdSaleProposalDTO.setProposalAmount((BigInteger) Tuple7.getValue3());
					crowdSaleProposalDTO.setLike((BigInteger) Tuple7.getValue4());
					crowdSaleProposalDTO.setDisLike((BigInteger) Tuple7.getValue5());
					Date startTime = new Date((long) ((BigInteger) Tuple7.getValue6()).intValue());
					Date endTime = new Date((long) ((BigInteger) Tuple7.getValue7()).intValue());
					crowdSaleProposalDTO.setStartDate(startTime);
					crowdSaleProposalDTO.setEndDate(endTime);
					crowdSaleList.add(crowdSaleProposalDTO);
				}
			}
		}

		return crowdSaleList;
	}

	public List<CrowdSaleProposalDTO> listFailedProposal() throws Exception {
		ArrayList crowdSaleList = new ArrayList();

		if (equaZone == null) {
			SolidityHandler solidityHandler = new SolidityHandler();
			solidityHandler.deployContract();
		}
		BigInteger count = (BigInteger) equaZone.countProposalList().send();
		int tokenCount = count.intValue();
		LOG.info("tokenCount " + tokenCount);
		new ArrayList();
		if (tokenCount != 0) {
			for (int i = 0; i < tokenCount; ++i) {
				BigInteger bi = BigInteger.valueOf((long) i);
				Tuple7 Tuple7 = (Tuple7) equaZone.getAllProposals(bi).send();
				String createdBy = (String) equaZone.getAllProposalsCreatedBy(bi).sendAsync().get();
				CrowdSaleProposalDTO crowdSaleProposalDTO = new CrowdSaleProposalDTO();
				BigInteger like = (BigInteger) Tuple7.getValue4();
				int check = like.compareTo(new BigInteger("1"));
				if (check == -1) {
					crowdSaleProposalDTO.setProposalTitle((String) Tuple7.getValue1());
					crowdSaleProposalDTO.setProposalCategory((String) Tuple7.getValue2());
					crowdSaleProposalDTO.setProposalAmount((BigInteger) Tuple7.getValue3());
					crowdSaleProposalDTO.setLike((BigInteger) Tuple7.getValue4());
					crowdSaleProposalDTO.setDisLike((BigInteger) Tuple7.getValue5());
					Date startTime = new Date((long) ((BigInteger) Tuple7.getValue6()).intValue());
					Date endTime = new Date((long) ((BigInteger) Tuple7.getValue7()).intValue());
					crowdSaleProposalDTO.setStartDate(startTime);
					crowdSaleProposalDTO.setEndDate(endTime);
					crowdSaleList.add(crowdSaleProposalDTO);
				}
			}
		}

		return crowdSaleList;
	}

	public void isCrowdsaleEnd() throws Exception {
		ArrayList crowdSaleList = new ArrayList();

		if (equaZone == null) {
			SolidityHandler solidityHandler = new SolidityHandler();
			solidityHandler.deployContract();
		}
		LOG.info("equaZone    " + equaZone);
		BigInteger count = (BigInteger) equaZone.countProposalList().send();
		int tokenCount = count.intValue();
		LOG.info("tokenCount " + tokenCount);
		new ArrayList();
		if (tokenCount != 0) {
			for (int i = 0; i < tokenCount; ++i) {
				BigInteger bi = BigInteger.valueOf((long) i);
				TransactionReceipt transactionReceipt = equaZone
						.isCrowdSaleEnd(new BigInteger("0"), new BigInteger("0")).sendAsync().get();

				if (transactionReceipt != null) {
					LOG.info("transactionReceipt " + transactionReceipt.getGasUsed());
					List<IsCrowdSaleEndStatusEventResponse> isCrowdsaleEndStatus = equaZone
							.getIsCrowdSaleEndStatusEvents(transactionReceipt);
					LOG.info("isCrowdsaleEndStatus " + isCrowdsaleEndStatus.size());
					if (isCrowdsaleEndStatus.size() > 0) {
						Iterator arg5 = isCrowdsaleEndStatus.iterator();

						while (arg5.hasNext()) {
							IsCrowdSaleEndStatusEventResponse isCrowdSaleEndStatusEventResponse = (IsCrowdSaleEndStatusEventResponse) arg5
									.next();
							LOG.info("isCrowdSaleFailure" + isCrowdSaleEndStatusEventResponse.isCrowdSaleEndStatus);
							if (isCrowdSaleEndStatusEventResponse.isCrowdSaleEndStatus.equalsIgnoreCase("success")) {
								LOG.info("success");
							} else if (isCrowdSaleEndStatusEventResponse.isCrowdSaleEndStatus
									.equalsIgnoreCase("failed")) {
								LOG.info("failed");
							}
						}
					}
				}

			}

		}
	}

	public boolean sendToken(TokenDTO tokenDTO) throws Exception {
		
		BigDecimal requestToken = tokenDTO.getRequestToken();
		List<CoinsInfo> coinsInfoList = coinInfoRepository.findAll();
		CoinsInfoDTO coinsInfoDTO = new CoinsInfoDTO();
		Config configInfo = configInfoRepository.findConfigByConfigKey("walletfile");
			for (CoinsInfo coinsInfo : coinsInfoList) {
			coinsInfoDTO.setId(coinsInfo.getId());
			coinsInfoDTO.setEth(coinsInfo.getEth());
		}
		
			double 	Token=requestToken.doubleValue();	
			double amount = Token * coinsInfoDTO.getEth();
			DecimalFormat df = new DecimalFormat("#.########");
			tokenDTO.setAmount(new BigDecimal(df.format(amount)));
			Credentials credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),
					configInfo.getConfigValue() + "//" + tokenDTO.getWalletAddress());
			TransactionReceipt transactionReceipt = Transfer.sendFunds(web3j, credentials,
					env.getProperty("main.address"), tokenDTO.getAmount(), Convert.Unit.ETHER).send();
			
			if (transactionReceipt != null) {
				
				tokenDTO.setToAddress(tokenDTO.getFromAddress());
				boolean status = sendCoin(tokenDTO);
				if (status) {
					return true;
				} else {
					
					Credentials recredentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
							env.getProperty("credentials.addres"));
					@SuppressWarnings("unused")
					TransactionReceipt retransactionReceipt = Transfer.sendFunds(web3j, recredentials,
							tokenDTO.getToAddress(), tokenDTO.getAmount(), Convert.Unit.ETHER).send();
					 
				}

			}
		
			return false;
	}
	
	public boolean sendCoin(TokenDTO tokenDTO) throws Exception {
		Credentials credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
				this.env.getProperty("credentials.address"));
		AssetToken assetToken = AssetToken.load(this.env.getProperty("token.address"), this.web3j, credentials,
				BigInteger.valueOf(1000000000L), BigInteger.valueOf(3000000L));
		BigInteger amount=tokenDTO.getRequestToken().toBigInteger();
		TransactionReceipt transactionReceipt =assetToken
				.transfer(tokenDTO.getToAddress(), amount).send();
		if(transactionReceipt != null){
			TransactionHistory transactionHistory = new TransactionHistory();
			transactionHistory.setFromAddress(tokenDTO.getFromAddress());
			transactionHistory.setToAddress(this.env.getProperty("main.address"));
			transactionHistory.setAmount(tokenDTO.getAmount().doubleValue());
			transactionHistory.setToken(tokenDTO.getRequestToken().toBigInteger());
			transactionHistory.setPaymentMode(env.getProperty("user.payment"));
			LOG.info("transfer amount" + tokenDTO.getAmount());
			transactionHistory.setCreatedDate(new Date());
			this.transactionInfoRepository.save(transactionHistory);
			if (transactionHistory != null) {
				return true;
			}
		}
		return transactionReceipt != null;
	}

	public boolean mintToken(TokenDTO tokenDTO) throws Exception {
		Credentials credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
				this.env.getProperty("credentials.address"));
		AssetToken assetToken = AssetToken.load(this.env.getProperty("token.address"), this.web3j, credentials,
				Contract.GAS_PRICE, Contract.GAS_LIMIT);
		System.out.println("mainaddress"+env.getProperty("main.address"));
		
		TransactionReceipt transactionReceipt = assetToken.mintToken(env.getProperty("main.address"), tokenDTO.getMintedAmount()).send();
		if(transactionReceipt!=null){
			MintInfo mintHistory = new MintInfo();
			mintHistory.setFromAddress(env.getProperty("main.address"));
			mintHistory.setMintToken(tokenDTO.getMintedAmount());
			mintHistory.setCreatedDate(new Date());
			this.mintInfoRepository.save(mintHistory);
			if (mintHistory != null) {
				return true;
			}
			
			
		}
		
		return false;
	}
}