package com.equocoin.handlesolidity;

import com.equocoin.dto.CoinsInfoDTO;
import com.equocoin.dto.CrowdSaleProposalDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.model.CoinsInfo;
import com.equocoin.model.Config;
import com.equocoin.model.DacUserInfo;
import com.equocoin.model.MintInfo;
import com.equocoin.model.ProposalInfo;
import com.equocoin.model.RegisterInfo;
import com.equocoin.model.TransactionHistory;
import com.equocoin.repository.CoinInfoRepository;
import com.equocoin.repository.ConfigInfoRepository;
import com.equocoin.repository.DacUserInfoRepository;
import com.equocoin.repository.MintInfoRepository;
import com.equocoin.repository.ProposalInfoRepository;
import com.equocoin.repository.RegisterInfoRepository;
import com.equocoin.repository.TransactionInfoRepository;
import com.equocoin.service.impl.TokenUserServiceImpl;
import com.equocoin.soliditytojava.EquaZone;
import com.equocoin.soliditytojava.Equacoins;
import com.equocoin.utils.EncryptDecrypt;
import com.equocoin.utils.EquocoinUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.tx.Contract;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

@Service
public class SolidityHandler {
	private static final Logger LOG = LoggerFactory.getLogger(SolidityHandler.class);

	@SuppressWarnings("unused")
	private static final int COUNT = 1;

	// private final Web3j web3j = Web3j.build(new HttpService());

	// private final Web3j web3j = Web3j.build(new
	// HttpService("https://rinkeby.infura.io/"));

	private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/"));

	@Autowired
	private Environment env;

	@Autowired
	private ConfigInfoRepository configInfoRepository;

	@SuppressWarnings("unused")
	@Autowired
	private TokenUserServiceImpl tokenUserServiceImpl;

	@Autowired
	private TransactionInfoRepository transactionInfoRepository;

	@Autowired
	private CoinInfoRepository coinInfoRepository;

	@Autowired
	private MintInfoRepository mintInfoRepository;

	@Autowired
	private ProposalInfoRepository proposalInfoRepository;

	@Autowired
	private RegisterInfoRepository registerInfoRepository;

	@Autowired
	private EquocoinUtils equocoinUtils;

	@Autowired
	private DacUserInfoRepository dacUserInfoRepository;

	BigInteger GAS = BigInteger.valueOf(4300000L);
	BigInteger GAS_PRICE = BigInteger.valueOf(2000000L);
	BigInteger initialWeiValue = BigInteger.valueOf(0L);
	@SuppressWarnings("unused")
	private BigInteger GAS_LIMIT = BigInteger.valueOf(3000000L);
	private TransactionReceipt transactionReceipt;

	// public static EquaZone equaZone = null;
	private EquaZone equaZone = null;

	public boolean tokenCancel(TokenDTO tokenDTO) throws Exception {
		transactionReceipt = new TransactionReceipt();

		CompletableFuture.supplyAsync(() -> {
			try {
				Credentials credentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
						env.getProperty("credentials.address"));
				Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,
						Contract.GAS_PRICE, Contract.GAS_LIMIT);

				transactionReceipt = assetToken.burn(new BigDecimal(tokenDTO.getTokenBalance() * 10000).toBigInteger())
						.sendAsync().get();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "call cancel token";
		}).thenAccept(product -> {
			if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
				// if (transactionReceipt != null) {
				// return true;
				LOG.info("after calling blackchain token canceled successsfully");
			}
		});
		return true;
	}

	public boolean purchaseToken(TokenDTO tokenDTO) throws Exception {
		transactionReceipt = new TransactionReceipt();
		TransactionHistory transactionHistory = new TransactionHistory();
		CompletableFuture.supplyAsync(() -> {
			try {

				List<CoinsInfo> coinsInfoList = coinInfoRepository.findAll();
				CoinsInfoDTO coinsInfoDTO = new CoinsInfoDTO();
				Config configInfo = configInfoRepository.findConfigByConfigKey("walletfile");
				for (CoinsInfo coinsInfo : coinsInfoList) {
					coinsInfoDTO.setId(coinsInfo.getId());
					coinsInfoDTO.setEth(coinsInfo.getEth());
				}

				double Token = tokenDTO.getRequestToken();
				double amount = Token * coinsInfoDTO.getEth();
				DecimalFormat df = new DecimalFormat("#.########");
				tokenDTO.setAmount(new BigDecimal(df.format(amount)));
				Credentials credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),
						configInfo.getConfigValue() + "//" + tokenDTO.getWalletAddress());
				transactionHistory.setFromAddress(tokenDTO.getFromAddress());
				transactionHistory.setToAddress(this.env.getProperty("main.address"));
				transactionHistory.setAmount(tokenDTO.getAmount().doubleValue());
				transactionHistory.setToken(tokenDTO.getRequestToken());
				transactionHistory.setPaymentMode(env.getProperty("user.payment"));
				transactionHistory.setCreatedDate(new Date());
				transactionHistory.setStatus(env.getProperty("status.pending"));
				transactionHistory.setRegisterInfo(tokenDTO.getRegisterInfo());
				transactionInfoRepository.save(transactionHistory);
				if (transactionHistory != null) {
					tokenDTO.setId(transactionHistory.getId());
				}
				transactionReceipt = Transfer.sendFunds(web3j, credentials, env.getProperty("main.address"),
						tokenDTO.getAmount(), Convert.Unit.ETHER).send();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "call purchase token";
		}).thenAccept(product -> {
			if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
				// if (transactionReceipt != null) {
				tokenDTO.setToAddress(tokenDTO.getFromAddress());
				boolean status;
				try {
					status = sendCrowdsaleEquaCoin(tokenDTO);
					if (status) {
						LOG.info("Token Purchased Successfully");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
		return true;
	}

	public boolean manualTokenTransfer(TokenDTO tokenDTO) throws Exception {
		RegisterInfo registerInfo = registerInfoRepository.findEquocoinUserInfoByEmailId(tokenDTO.getEmailId());
		transactionReceipt = new TransactionReceipt();
		TransactionHistory transactionHistory = new TransactionHistory();
		CompletableFuture.supplyAsync(() -> {
			Credentials credentials;
			try {
				transactionHistory.setFromAddress(this.env.getProperty("main.address"));
				transactionHistory.setToAddress(tokenDTO.getToAddress());
				transactionHistory.setAmount(new Double(0));
				transactionHistory.setToken(tokenDTO.getRequestToken());
				transactionHistory.setPaymentMode(env.getProperty("admin.payment"));
				transactionHistory.setCreatedDate(new Date());
				transactionHistory.setStatus(env.getProperty("status.pending"));
				transactionHistory.setRegisterInfo(registerInfo);
				transactionInfoRepository.save(transactionHistory);
				if (transactionHistory != null) {
					credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
							env.getProperty("credentials.address"));
					Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,
							Contract.GAS_PRICE, Contract.GAS_LIMIT);
					BigInteger amount = BigDecimal.valueOf(tokenDTO.getRequestToken() * 10000).toBigInteger();
					transactionReceipt = assetToken.transfer(tokenDTO.getToAddress(), amount).sendAsync().get();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//
			return "call blackchain";
		}).thenAccept(product -> {
			if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
				// if(transactionReceipt!=null){
				if (transactionHistory != null) {
					transactionHistory.setStatus(env.getProperty("status.success"));
					transactionInfoRepository.save(transactionHistory);
					// return true;
				}
			}
			// else if(transactionReceipt!=null){
			else if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed"))) {
				if (transactionHistory != null) {
					transactionHistory.setStatus(env.getProperty("status.failed"));
					transactionInfoRepository.save(transactionHistory);
					// return true;
				}
			}
		});
		return true;
	}

	public boolean mintToken(TokenDTO tokenDTO) throws Exception {
		transactionReceipt = new TransactionReceipt();
		MintInfo mintHistory = new MintInfo();
		CompletableFuture.supplyAsync(() -> {
			Credentials credentials;
			try {
				mintHistory.setFromAddress(env.getProperty("main.address"));
				mintHistory.setMintToken(tokenDTO.getMintedAmount());
				mintHistory.setCreatedDate(new Date());
				mintHistory.setStatus(env.getProperty("status.pending"));
				mintInfoRepository.save(mintHistory);

				credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
						this.env.getProperty("credentials.address"));
				Equacoins assetToken = Equacoins.load(this.env.getProperty("token.address"), this.web3j, credentials,
						Contract.GAS_PRICE, Contract.GAS_LIMIT);

				transactionReceipt = assetToken.mint(env.getProperty("main.address"),
						new BigDecimal((tokenDTO.getMintedAmount() * 10000)).toBigInteger()).sendAsync().get();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "call mint token";
		}).thenAccept(product -> {
			if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
				// if(transactionReceipt !=null){
				if (mintHistory != null) {
					mintHistory.setStatus(env.getProperty("status.success"));
					mintInfoRepository.save(mintHistory);
				}

			} else if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed"))) {
				// else if(transactionReceipt ==null){
				if (mintHistory != null) {
					mintHistory.setStatus(env.getProperty("status.failed"));
					mintInfoRepository.save(mintHistory);
				}

			}

		});

		return true;
	}

	public String isValidTokenBalForCrowdsale(TokenDTO tokenDTO) throws Exception {
		Credentials credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
				this.env.getProperty("credentials.address"));
		Equacoins assetToken = Equacoins.load(this.env.getProperty("token.address"), this.web3j, credentials,
				BigInteger.valueOf(3000000L), BigInteger.valueOf(3000000L));
		if (tokenDTO.getToAddress() != null && !tokenDTO.getToAddress().isEmpty()) {
			tokenDTO.setFromAddress(tokenDTO.getToAddress());
		}

		List<TransactionHistory> txHistory1 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("EQUA", "Pending",
						env.getProperty("main.address"), tokenDTO.getFromAddress());
		List<TransactionHistory> txHistory2 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("EQUA", "Pending", tokenDTO.getFromAddress(),
						env.getProperty("main.address"));
		List<TransactionHistory> txHistory3 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("ETH", "Pending", env.getProperty("main.address"),
						tokenDTO.getFromAddress());
		List<TransactionHistory> txHistory4 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("ETH", "Pending", tokenDTO.getFromAddress(),
						env.getProperty("main.address"));
		if (txHistory1.size() > 0 || txHistory2.size() > 0 || txHistory3.size() > 0 || txHistory4.size() > 0) {

			return "You won't be able to send any ETH or other tokens until the Status transaction gets confirmed";
		}

		BigInteger crowdsaleBal = (BigInteger) assetToken.balanceOf(env.getProperty("main.address")).send();
		BigInteger token = new BigDecimal(tokenDTO.getRequestToken()).toBigInteger();
		Double sendTokenBalance = crowdsaleBal.doubleValue() / 10000;
		if (BigInteger.ZERO.equals(token)) {
			return "Please enter valid token";
		}
		if (BigInteger.ZERO.equals(sendTokenBalance)) {
			return "SOLD OUT";
		} else {
			int count = sendTokenBalance.compareTo(tokenDTO.getRequestToken());
			return count == -1 ? "We have only " + sendTokenBalance + "tokens left" : "success";
		}
	}

	public boolean createProposal(CrowdSaleProposalDTO crowdSaleDTO) throws Exception {
		ProposalInfo info = proposalInfoRepository.findOne(crowdSaleDTO.getId());
		String startDate = crowdSaleDTO.getStartDates();
		String endDate = crowdSaleDTO.getEndDates();
		crowdSaleDTO.setLike(new BigInteger("0"));
		crowdSaleDTO.setDisLike(new BigInteger("0"));
		crowdSaleDTO.setApprove(new BigInteger("0"));
		crowdSaleDTO.setReject(new BigInteger("0"));
		crowdSaleDTO.setProposalCreatedByName(crowdSaleDTO.getEmailId().split("@")[0]);
		Credentials credentials = WalletUtils.loadCredentials(crowdSaleDTO.getWalletPassword(),
				crowdSaleDTO.getWalletAddress());

		equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);
		TransactionReceipt transactionReceipt = (TransactionReceipt) equaZone
				.createProposal(crowdSaleDTO.getProposalCreatedBy(), crowdSaleDTO.getProposalTitle(),
						crowdSaleDTO.getProposalCategory(), crowdSaleDTO.getProposalDetails(),
						crowdSaleDTO.getProposalCreatedByName(), crowdSaleDTO.getProposalAmount(), startDate, endDate,
						crowdSaleDTO.getLike(), crowdSaleDTO.getDisLike(), crowdSaleDTO.getApprove(),
						crowdSaleDTO.getReject())
				.send();

		if (transactionReceipt != null) {
			return true;
		} else {
			proposalInfoRepository.delete(info.getId());
			return false;
		}
	}

	@SuppressWarnings({ "deprecation", "unused" })
	public Long getMinutesBetweenDates(Date inputDate) throws ParseException {
		Date date = new Date();
		DateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
		String s = formatter.format(date);

		Date d1 = null;
		Date d2 = null;
		try {

			d2 = new Date(s);
		} catch (Exception e) {
			e.printStackTrace();
		}

		long duration = inputDate.getTime() - date.getTime();

		long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
		long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
		return diffInMinutes;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<CrowdSaleProposalDTO> crowdsaleProposalList(CrowdSaleProposalDTO crowdSaleDTO) throws Exception {
		ArrayList crowdSaleList = new ArrayList();
		@SuppressWarnings("unused")
		List<RegisterInfo> registerDesStatus = (List<RegisterInfo>) registerInfoRepository.findAll();

		Credentials credentials = WalletUtils.loadCredentials(crowdSaleDTO.getWalletPassword(),
				crowdSaleDTO.getWalletAddress());
		equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);

		BigInteger count = (BigInteger) equaZone.countProposalList().send();

		int tokenCount = count.intValue();

		new ArrayList();
		if (tokenCount != 0) {
			for (int i = 0; i < tokenCount; ++i) {

				CrowdSaleProposalDTO crowdSaleProposalDTO = new CrowdSaleProposalDTO();
				BigInteger bi = BigInteger.valueOf((long) i);
				Tuple7 Tuple7 = (Tuple7) equaZone.getApproveProposals(bi).send();
				Tuple7 Tuple8 = (Tuple7) equaZone.getAllProposals(bi).send();
				BigInteger accept = (BigInteger) Tuple7.getValue6();

				if (accept.compareTo(new BigInteger("2")) == 0) {
					crowdSaleProposalDTO.setLike((BigInteger) Tuple8.getValue4());
					crowdSaleProposalDTO.setDisLike((BigInteger) Tuple8.getValue5());
					DacUserInfo dacUserInfo = dacUserInfoRepository.findOne(new Integer(1));
					crowdSaleProposalDTO.setDacUserCount(dacUserInfo.getUserCount());
					Float votePercentage = (((crowdSaleProposalDTO.getLike().floatValue())
							/ crowdSaleProposalDTO.getDacUserCount()) * 100);
					Float roundPercentage = (float) Math.round(votePercentage);
					Float dislikepercentage = (((crowdSaleProposalDTO.getDisLike().floatValue())
							/ crowdSaleProposalDTO.getDacUserCount()) * 100);
					Float roundDislikePercentage = (float) Math.round(dislikepercentage);
					crowdSaleProposalDTO.setPercentage(roundPercentage);
					crowdSaleProposalDTO.setFailPercentage(roundDislikePercentage);
					crowdSaleProposalDTO.setProposalId(bi);
					Config configInfo = configInfoRepository.findConfigByConfigKey("apache.server");
					ProposalInfo proposalInfo = proposalInfoRepository
							.findInfoByProposalNo(crowdSaleProposalDTO.getProposalId());
					String dbPath = proposalInfo.getDbPath();
					String basePath = configInfo.getConfigValue();
					crowdSaleProposalDTO.setProposalDOC(basePath + dbPath);
					crowdSaleProposalDTO.setProposalDetails((String) Tuple7.getValue4());
					crowdSaleProposalDTO.setProposalCreatedBy((String) Tuple7.getValue1());
					crowdSaleProposalDTO.setProposalCreatedByName((String) Tuple7.getValue3());
					crowdSaleProposalDTO.setPaymentMode((String) Tuple7.getValue3());
					crowdSaleProposalDTO.setProposalCategory((String) Tuple8.getValue1());
					crowdSaleProposalDTO.setProposalTitle((String) Tuple8.getValue2());
					crowdSaleProposalDTO.setProposalAmount((BigInteger) Tuple8.getValue3());
					crowdSaleProposalDTO.setStatus(env.getProperty("proposal.accept"));
					crowdSaleProposalDTO.setStartDates((String) Tuple8.getValue6());
					crowdSaleProposalDTO.setEndDates((String) Tuple8.getValue7());
					if (proposalInfo.getProposalDetailsLink() != null) {
						crowdSaleProposalDTO.setProposalDetailsLink(proposalInfo.getProposalDetailsLink());
					}
					crowdSaleList.add(crowdSaleProposalDTO);
				}
			}
			return crowdSaleList;
		}

		return null;
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public boolean votingForProposal(CrowdSaleProposalDTO crowdSaleDTO) throws Exception {
		crowdSaleDTO.setMessage("Voting Failed!");
		Date currentDate = new Date();
		Credentials credentials = WalletUtils.loadCredentials(crowdSaleDTO.getWalletPassword(),
				crowdSaleDTO.getWalletAddress());
		equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);
		crowdSaleDTO.setCredentials(credentials);
		Tuple7 Tuple8 = (Tuple7) equaZone.getAllProposals(crowdSaleDTO.getProposalNumber()).send();
		Tuple7 Tuple7 = (Tuple7) equaZone.getApproveProposals(crowdSaleDTO.getProposalNumber()).send();
		String dateInString = (String) Tuple8.getValue6();
		String endTime = (String) Tuple8.getValue7();
		Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateInString);
		Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(endTime);
		if (currentDate.after(startDate) && currentDate.before(endDate)) {

			TransactionReceipt transactionReceipt = (TransactionReceipt) equaZone
					.vote(crowdSaleDTO.getProposalNumber(), crowdSaleDTO.getVoted()).send();

			if (transactionReceipt != null) {

				List<com.equocoin.soliditytojava.EquaZone.StatusEventResponse> StatusEventResponse = equaZone
						.getStatusEvents(transactionReceipt);

				if (StatusEventResponse.size() > 0) {

					for (com.equocoin.soliditytojava.EquaZone.StatusEventResponse StatusEventResponses : StatusEventResponse) {

						if (StatusEventResponses.status.equalsIgnoreCase("success")) {
							boolean sendEquacoin = sendEQUA(crowdSaleDTO);
							if (sendEquacoin) {
								return true;
							}
						} else {
							crowdSaleDTO.setMessage("User cannot  vote more than once");
							return false;
						}
					}
				} else {
					crowdSaleDTO.setMessage("Voting failure!");
					return false;
				}

			} else {

				crowdSaleDTO.setMessage("Voting failed");
				return false;
			}

		} else {
			crowdSaleDTO.setMessage("Time exceeded");
			return false;
		}
		return false;

	}

	private boolean sendEQUA(CrowdSaleProposalDTO crowdSaleDTO) {
		transactionReceipt = new TransactionReceipt();
		Double EQUA = 1d;
		RegisterInfo registerInfo = registerInfoRepository.findEquocoinUserInfoByEmailId(crowdSaleDTO.getEmailId());
		transactionReceipt = new TransactionReceipt();
		TransactionHistory transactionHistory = new TransactionHistory();
		CompletableFuture.supplyAsync(() -> {
			Credentials credentials;
			try {
				transactionHistory.setFromAddress(this.env.getProperty("main.address"));
				transactionHistory.setToAddress(crowdSaleDTO.getToAddress());
				transactionHistory.setAmount(new Double(0));
				transactionHistory.setToken(EQUA);
				transactionHistory.setPaymentMode(env.getProperty("admin.payment"));
				transactionHistory.setCreatedDate(new Date());
				transactionHistory.setStatus(env.getProperty("status.pending"));
				transactionHistory.setRegisterInfo(registerInfo);
				transactionInfoRepository.save(transactionHistory);
				if (transactionHistory != null) {
					credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
							env.getProperty("credentials.address"));
					Equacoins assetToken = Equacoins.load(this.env.getProperty("token.address"), this.web3j,
							credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
					BigInteger amount = BigDecimal.valueOf(EQUA * 10000).toBigInteger();
					transactionReceipt = assetToken.transfer(crowdSaleDTO.getToAddress(), amount).sendAsync().get();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//
			return "call blackchain";
		}).thenAccept(product -> {
			if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
				// if(transactionReceipt!=null){
				if (transactionHistory != null) {
					transactionHistory.setStatus(env.getProperty("status.success"));
					transactionInfoRepository.save(transactionHistory);
					// return true;
				}
			}
			// else if(transactionReceipt!=null){
			else if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed"))) {
				if (transactionHistory != null) {
					transactionHistory.setStatus(env.getProperty("status.failed"));
					transactionInfoRepository.save(transactionHistory);
					// return true;
				}
			}
		});
		return true;

	}

	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	public List<CrowdSaleProposalDTO> crowdsaleProposalCategoryList(String categoryName,
			CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		ArrayList crowdSaleList = new ArrayList();
		DacUserInfo dacUserInfo = dacUserInfoRepository.findOne(new Integer(1));
		List<RegisterInfo> registerDesStatus = (List<RegisterInfo>) registerInfoRepository.findAll();
		Credentials credentials = WalletUtils.loadCredentials(crowdSaleProposalDTO.getWalletPassword(),
				crowdSaleProposalDTO.getWalletAddress());
		equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);
		BigInteger count = (BigInteger) equaZone.countProposalList().send();
		int tokenCount = count.intValue();
		new ArrayList();
		if (tokenCount != 0) {
			for (int i = 0; i < tokenCount; ++i) {

				BigInteger bi = BigInteger.valueOf((long) i);
				Tuple7 Tuple7 = (Tuple7) equaZone.getApproveProposals(bi).send();
				BigInteger accept = (BigInteger) Tuple7.getValue6();
				if (accept.compareTo(new BigInteger("2")) == 0) {
					crowdSaleProposalDTO.setProposalId(bi);
					Tuple7 Tuple8 = (Tuple7) equaZone.getAllProposals(bi).send();
					CrowdSaleProposalDTO crowdSaleDTO = new CrowdSaleProposalDTO();
					if (categoryName.equalsIgnoreCase((String) Tuple8.getValue2())) {
						Config configInfo = configInfoRepository.findConfigByConfigKey("apache.server");
						ProposalInfo proposalInfo = proposalInfoRepository
								.findInfoByProposalNo(crowdSaleProposalDTO.getProposalId());
						String dbPath = proposalInfo.getDbPath();
						String basePath = configInfo.getConfigValue();
						crowdSaleDTO.setProposalDOC(basePath + dbPath);
						crowdSaleDTO.setProposalId(crowdSaleProposalDTO.getProposalId());
						crowdSaleDTO.setLike((BigInteger) Tuple8.getValue4());
						crowdSaleDTO.setDisLike((BigInteger) Tuple8.getValue5());
						crowdSaleDTO.setDacUserCount(dacUserInfo.getUserCount());
						Float votePercentage = (((crowdSaleDTO.getLike().floatValue()) / crowdSaleDTO.getDacUserCount())
								* 100);
						Float roundPercentage = (float) Math.round(votePercentage);
						Float dislikepercentage = (((crowdSaleDTO.getDisLike().floatValue())
								/ crowdSaleDTO.getDacUserCount()) * 100);
						Float roundDislikePercentage = (float) Math.round(dislikepercentage);
						crowdSaleDTO.setPercentage(roundPercentage);
						crowdSaleDTO.setFailPercentage(roundDislikePercentage);
						crowdSaleDTO.setProposalDetails((String) Tuple7.getValue4());
						crowdSaleDTO.setPaymentMode((String) Tuple7.getValue3());
						crowdSaleDTO.setProposalCreatedByName((String) Tuple7.getValue3());
						crowdSaleDTO.setProposalTitle((String) Tuple8.getValue1());
						crowdSaleDTO.setProposalCategory((String) Tuple8.getValue2());
						crowdSaleDTO.setProposalAmount((BigInteger) Tuple8.getValue3());
						crowdSaleDTO.setStatus(env.getProperty("proposal.accept"));
						crowdSaleDTO.setProposalCreatedBy((String) Tuple7.getValue1());

						crowdSaleDTO.setStartDates((String) Tuple8.getValue6());
						crowdSaleDTO.setEndDates((String) Tuple8.getValue7());
						if (proposalInfo.getProposalDetailsLink() != null) {
							crowdSaleDTO.setProposalDetailsLink(proposalInfo.getProposalDetailsLink());
						}
						crowdSaleList.add(crowdSaleDTO);
					}
				}
			}
		}

		return crowdSaleList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<CrowdSaleProposalDTO> listOwnProposal(String address, CrowdSaleProposalDTO crowdSaleDTO)
			throws Exception {
		ArrayList crowdSaleList = new ArrayList();

		Credentials credentials = WalletUtils.loadCredentials(crowdSaleDTO.getWalletPassword(),
				crowdSaleDTO.getWalletAddress());

		equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);
		if (equaZone != null) {

			BigInteger count = (BigInteger) equaZone.countProposalList().send();
			int tokenCount = count.intValue();

			new ArrayList();
			if (tokenCount != 0) {
				for (int i = 0; i < tokenCount; ++i) {
					BigInteger bi = BigInteger.valueOf((long) i);
					Tuple7 Tuple7 = (Tuple7) equaZone.getApproveProposals(bi).send();
					BigInteger accept = (BigInteger) Tuple7.getValue6();
					BigInteger reject = (BigInteger) Tuple7.getValue7();
					String createdby = (String) Tuple7.getValue1();
					if (createdby.equalsIgnoreCase(address)) {
						Tuple7 Tuple8 = (Tuple7) equaZone.getAllProposals(bi).send();

						CrowdSaleProposalDTO crowdSaleProposalDTO = new CrowdSaleProposalDTO();
						if (accept.compareTo(new BigInteger("2")) == 0) {
							crowdSaleProposalDTO.setStatus(env.getProperty("proposal.accept"));

						} else if (reject.compareTo(new BigInteger("1")) == 0) {
							crowdSaleProposalDTO.setStatus(env.getProperty("proposal.reject"));
						} else if (accept.compareTo(new BigInteger("0")) == 0
								&& reject.compareTo(new BigInteger("0")) == 0) {
							crowdSaleProposalDTO.setStatus(env.getProperty("proposal.pending"));
						}

						crowdSaleProposalDTO.setProposalId(bi);
						Config configInfo = configInfoRepository.findConfigByConfigKey("apache.server");
						ProposalInfo proposalInfo = proposalInfoRepository
								.findInfoByProposalNo(crowdSaleProposalDTO.getProposalId());
						String dbPath = proposalInfo.getDbPath();
						String basePath = configInfo.getConfigValue();
						crowdSaleProposalDTO.setProposalDOC(basePath + dbPath);
						crowdSaleProposalDTO.setPaymentMode((String) Tuple7.getValue3());
						crowdSaleProposalDTO.setProposalCreatedByName((String) Tuple7.getValue3());
						crowdSaleProposalDTO.setProposalCategory((String) Tuple8.getValue2());
						crowdSaleProposalDTO.setProposalTitle((String) Tuple8.getValue1());
						crowdSaleProposalDTO.setProposalCreatedBy((String) Tuple7.getValue1());
						crowdSaleProposalDTO.setProposalDetails((String) Tuple7.getValue4());
						crowdSaleProposalDTO.setProposalAmount((BigInteger) Tuple8.getValue3());
						crowdSaleProposalDTO.setLike((BigInteger) Tuple8.getValue4());
						crowdSaleProposalDTO.setDisLike((BigInteger) Tuple8.getValue5());
						crowdSaleProposalDTO.setStartDates((String) Tuple8.getValue6());
						crowdSaleProposalDTO.setEndDates((String) Tuple8.getValue7());
						if (proposalInfo.getProposalDetailsLink() != null) {
							crowdSaleProposalDTO.setProposalDetailsLink(proposalInfo.getProposalDetailsLink());
						}
						crowdSaleList.add(crowdSaleProposalDTO);
					}
				}
			}
		}
		return crowdSaleList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<CrowdSaleProposalDTO> listOtherProposal(String address, CrowdSaleProposalDTO crowdSaleProposalDTO)
			throws Exception {
		ArrayList crowdSaleList = new ArrayList();

		Credentials credentials = WalletUtils.loadCredentials(crowdSaleProposalDTO.getWalletPassword(),
				crowdSaleProposalDTO.getWalletAddress());

		equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);
		BigInteger count = (BigInteger) equaZone.countProposalList().send();
		int tokenCount = count.intValue();
		new ArrayList();
		if (tokenCount != 0) {
			for (int i = 0; i < tokenCount; ++i) {
				BigInteger bi = BigInteger.valueOf((long) i);
				Tuple7 Tuple7 = (Tuple7) equaZone.getApproveProposals(bi).send();
				BigInteger accept = (BigInteger) Tuple7.getValue6();
				BigInteger reject = (BigInteger) Tuple7.getValue7();
				String createdby = (String) Tuple7.getValue1();
				if (accept.compareTo(new BigInteger("2")) == 0 && !createdby.equalsIgnoreCase(address)) {
					Tuple7 Tuple8 = (Tuple7) equaZone.getAllProposals(bi).send();
					Tuple7 Tuple9 = (Tuple7) equaZone.getApproveProposals(bi).send();

					crowdSaleProposalDTO.setProposalId(bi);
					CrowdSaleProposalDTO crowdSaleDTO = new CrowdSaleProposalDTO();
					Config configInfo = configInfoRepository.findConfigByConfigKey("apache.server");
					ProposalInfo proposalInfo = proposalInfoRepository
							.findInfoByProposalNo(crowdSaleProposalDTO.getProposalId());
					String dbPath = proposalInfo.getDbPath();
					String basePath = configInfo.getConfigValue();

					if (accept.compareTo(new BigInteger("2")) == 0) {
						crowdSaleDTO.setStatus(env.getProperty("proposal.accept"));

					} else if (reject.compareTo(new BigInteger("1")) == 0) {
						crowdSaleDTO.setStatus(env.getProperty("proposal.reject"));
					} else if (accept.compareTo(new BigInteger("0")) == 0
							&& reject.compareTo(new BigInteger("0")) == 0) {
						crowdSaleDTO.setStatus(env.getProperty("proposal.pending"));
					}
					crowdSaleDTO.setProposalId(crowdSaleProposalDTO.getProposalId());
					crowdSaleDTO.setProposalTitle((String) Tuple8.getValue1());
					crowdSaleDTO.setProposalCreatedBy((String) Tuple9.getValue1());
					crowdSaleDTO.setProposalCategory((String) Tuple8.getValue2());
					crowdSaleDTO.setProposalAmount((BigInteger) Tuple8.getValue3());
					crowdSaleDTO.setProposalDOC(basePath + dbPath);
					crowdSaleDTO.setPaymentMode((String) Tuple9.getValue3());
					crowdSaleDTO.setProposalCreatedByName((String) Tuple9.getValue3());
					crowdSaleDTO.setProposalDetails((String) Tuple9.getValue4());
					crowdSaleDTO.setLike((BigInteger) Tuple8.getValue4());
					crowdSaleDTO.setDisLike((BigInteger) Tuple8.getValue5());
					crowdSaleDTO.setStartDates((String) Tuple8.getValue6());
					crowdSaleDTO.setEndDates((String) Tuple8.getValue7());
					if (proposalInfo.getProposalDetailsLink() != null) {
						crowdSaleDTO.setProposalDetailsLink(proposalInfo.getProposalDetailsLink());
					}
					crowdSaleList.add(crowdSaleDTO);

				}
			}
		}

		return crowdSaleList;
	}

	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	public List<CrowdSaleProposalDTO> listWonProposal(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		ArrayList crowdSaleList = new ArrayList();
		Date currentdate = new Date();
		List<RegisterInfo> registerDesStatus = (List<RegisterInfo>) registerInfoRepository.findAll();
		Credentials credentials = WalletUtils.loadCredentials(crowdSaleProposalDTO.getWalletPassword(),
				crowdSaleProposalDTO.getWalletAddress());

		equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);

		BigInteger count = (BigInteger) equaZone.countProposalList().send();
		int tokenCount = count.intValue();

		new ArrayList();
		if (tokenCount != 0) {
			for (int i = 0; i < tokenCount; ++i) {
				BigInteger bi = BigInteger.valueOf((long) i);
				Tuple7 Tuple7 = (Tuple7) equaZone.getApproveProposals(bi).send();
				BigInteger accept = (BigInteger) Tuple7.getValue6();
				if (accept.compareTo(new BigInteger("2")) == 0) {
					Tuple7 Tuple8 = (Tuple7) equaZone.getAllProposals(bi).send();
					String createdBy = (String) equaZone.getAllProposalsCreatedBy(bi).sendAsync().get();
					CrowdSaleProposalDTO crowdSaleDTO = new CrowdSaleProposalDTO();
					BigInteger like = (BigInteger) Tuple8.getValue4();
					String endTime = (String) Tuple8.getValue7();
					Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(endTime);
					BigInteger likeCount = (BigInteger) Tuple8.getValue4();
					crowdSaleProposalDTO.setLikeCount(likeCount);
					BigInteger disLikeCount = (BigInteger) Tuple8.getValue5();
					crowdSaleProposalDTO.setDislikeCount(disLikeCount);
					crowdSaleProposalDTO.setLike((BigInteger) Tuple8.getValue4());
					crowdSaleProposalDTO.setDisLike((BigInteger) Tuple8.getValue5());
					int check = like.compareTo(new BigInteger("1"));
					if (currentdate.after(endDate)) {

						DacUserInfo dacUserInfo = dacUserInfoRepository.findOne(new Integer(1));
						crowdSaleDTO.setDacUserCount(dacUserInfo.getUserCount());
						Float votePercentage = (((crowdSaleProposalDTO.getLike().floatValue())
								/ crowdSaleDTO.getDacUserCount()) * 100);
						Float roundPercentage = (float) Math.round(votePercentage);
						Float dislikepercentage = (((crowdSaleProposalDTO.getDisLike().floatValue())
								/ crowdSaleDTO.getDacUserCount()) * 100);
						Float roundDislikePercentage = (float) Math.round(dislikepercentage);
						if (roundPercentage >= 60) {
							crowdSaleDTO.setProposalId(bi);
							Config configInfo = configInfoRepository.findConfigByConfigKey("apache.server");
							ProposalInfo proposalInfo = proposalInfoRepository
									.findInfoByProposalNo(crowdSaleDTO.getProposalId());
							String dbPath = proposalInfo.getDbPath();
							String basePath = configInfo.getConfigValue();
							crowdSaleDTO.setProposalDOC(basePath + dbPath);
							crowdSaleDTO.setPercentage(roundPercentage);
							crowdSaleDTO.setFailPercentage(roundDislikePercentage);
							crowdSaleDTO.setPaymentMode((String) Tuple7.getValue3());
							crowdSaleDTO.setProposalCreatedByName((String) Tuple7.getValue3());
							crowdSaleDTO.setProposalDetails((String) Tuple7.getValue4());
							crowdSaleDTO.setProposalTitle((String) Tuple8.getValue1());
							crowdSaleDTO.setProposalCategory((String) Tuple8.getValue2());
							crowdSaleDTO.setProposalCreatedBy((String) Tuple7.getValue1());
							crowdSaleDTO.setProposalAmount((BigInteger) Tuple8.getValue3());
							crowdSaleDTO.setStatus(env.getProperty("proposal.accept"));
							crowdSaleDTO.setLike((BigInteger) Tuple8.getValue4());
							crowdSaleDTO.setDisLike((BigInteger) Tuple8.getValue5());
							crowdSaleDTO.setStartDates((String) Tuple8.getValue6());
							crowdSaleDTO.setEndDates((String) Tuple8.getValue7());
							if (proposalInfo.getProposalDetailsLink() != null) {
								crowdSaleDTO.setProposalDetailsLink(proposalInfo.getProposalDetailsLink());
							}
							crowdSaleList.add(crowdSaleDTO);
						}
					}
				}

			}
		}

		return crowdSaleList;
	}

	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	public List<CrowdSaleProposalDTO> listFailedProposal(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		ArrayList crowdSaleList = new ArrayList();
		Date currentdate = new Date();
		List<RegisterInfo> registerDesStatus = (List<RegisterInfo>) registerInfoRepository.findAll();
		Credentials credentials = WalletUtils.loadCredentials(crowdSaleProposalDTO.getWalletPassword(),
				crowdSaleProposalDTO.getWalletAddress());

		equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);

		BigInteger count = (BigInteger) equaZone.countProposalList().send();
		int tokenCount = count.intValue();
		new ArrayList();
		if (tokenCount != 0) {
			for (int i = 0; i < tokenCount; ++i) {
				BigInteger bi = BigInteger.valueOf((long) i);
				Tuple7 Tuple7 = (Tuple7) equaZone.getApproveProposals(bi).send();
				BigInteger accept = (BigInteger) Tuple7.getValue6();
				if (accept.compareTo(new BigInteger("2")) == 0) {
					Tuple7 Tuple8 = (Tuple7) equaZone.getAllProposals(bi).send();
					CrowdSaleProposalDTO crowdSaleDTO = new CrowdSaleProposalDTO();
					BigInteger like = (BigInteger) Tuple8.getValue4();
					String endTime = (String) Tuple8.getValue7();
					Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(endTime);
					BigInteger likeCount = (BigInteger) Tuple8.getValue4();
					crowdSaleProposalDTO.setLikeCount(likeCount);
					BigInteger disLikeCount = (BigInteger) Tuple8.getValue5();
					crowdSaleProposalDTO.setDislikeCount(disLikeCount);
					crowdSaleProposalDTO.setLike((BigInteger) Tuple8.getValue4());
					crowdSaleProposalDTO.setDisLike((BigInteger) Tuple8.getValue5());
					if (currentdate.after(endDate)) {

						DacUserInfo dacUserInfo = dacUserInfoRepository.findOne(new Integer(1));
						crowdSaleDTO.setDacUserCount(dacUserInfo.getUserCount());
						Float votePercentage = (((crowdSaleProposalDTO.getLike().floatValue())
								/ crowdSaleDTO.getDacUserCount()) * 100);
						Float roundPercentage = (float) Math.round(votePercentage);
						Float dislikepercentage = (((crowdSaleProposalDTO.getDisLike().floatValue())
								/ crowdSaleDTO.getDacUserCount()) * 100);
						Float roundDislikePercentage = (float) Math.round(dislikepercentage);
						if (roundPercentage < 60) {
							crowdSaleDTO.setProposalId(bi);
							Config configInfo = configInfoRepository.findConfigByConfigKey("apache.server");
							ProposalInfo proposalInfo = proposalInfoRepository
									.findInfoByProposalNo(crowdSaleDTO.getProposalId());
							String dbPath = proposalInfo.getDbPath();
							String basePath = configInfo.getConfigValue();
							crowdSaleDTO.setProposalDOC(basePath + dbPath);
							crowdSaleDTO.setPercentage(roundPercentage);
							crowdSaleDTO.setFailPercentage(roundDislikePercentage);
							crowdSaleDTO.setPaymentMode((String) Tuple7.getValue3());
							crowdSaleDTO.setProposalCreatedByName((String) Tuple7.getValue3());
							crowdSaleDTO.setProposalDetails((String) Tuple7.getValue4());
							crowdSaleDTO.setProposalTitle((String) Tuple8.getValue1());
							crowdSaleDTO.setProposalCategory((String) Tuple8.getValue2());
							crowdSaleDTO.setProposalAmount((BigInteger) Tuple8.getValue3());
							crowdSaleDTO.setProposalCreatedBy((String) Tuple7.getValue1());
							crowdSaleDTO.setLike((BigInteger) Tuple8.getValue4());
							crowdSaleDTO.setStatus(env.getProperty("proposal.accept"));
							crowdSaleDTO.setDisLike((BigInteger) Tuple8.getValue5());
							crowdSaleDTO.setStartDates((String) Tuple8.getValue6());
							crowdSaleDTO.setEndDates((String) Tuple8.getValue7());
							if (proposalInfo.getProposalDetailsLink() != null) {
								crowdSaleDTO.setProposalDetailsLink(proposalInfo.getProposalDetailsLink());
							}
							crowdSaleList.add(crowdSaleDTO);
						}
					}
				}
			}
		}

		return crowdSaleList;
	}

	public boolean sendCrowdsaleEquaCoin(TokenDTO tokenDTO) throws Exception {
		transactionReceipt = new TransactionReceipt();
		TransactionHistory transactionHistory = transactionInfoRepository.findOne(tokenDTO.getId());
		CompletableFuture.supplyAsync(() -> {
			try {
				if (transactionHistory != null) {
					Credentials credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
							this.env.getProperty("credentials.address"));
					Equacoins assetToken = Equacoins.load(this.env.getProperty("token.address"), this.web3j,
							credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
					BigInteger amount = new BigDecimal(tokenDTO.getRequestToken() * 10000).toBigInteger();
					transactionReceipt = assetToken.transfer(tokenDTO.getToAddress(), amount).send();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "call purchase token";
		}).thenAccept(product -> {

			if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
				// if (transactionReceipt != null) {
				transactionHistory.setStatus(env.getProperty("status.success"));
				transactionInfoRepository.save(transactionHistory);
			} else if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed"))) {
				// else if (transactionReceipt == null) {
				CompletableFuture.supplyAsync(() -> {

					Credentials recredentials;
					try {
						recredentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
								env.getProperty("credentials.address"));
						transactionReceipt = Transfer.sendFunds(web3j, recredentials, tokenDTO.getToAddress(),
								tokenDTO.getAmount(), Convert.Unit.ETHER).sendAsync().get();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return "call cancel token";
				}).thenAccept(products -> {
					if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
						// if (transactionReceipt != null) {
						transactionHistory.setStatus(env.getProperty("status.failed"));
						transactionInfoRepository.save(transactionHistory);

					} else if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed"))) {
						// else if (transactionReceipt == null) {
						transactionHistory.setStatus(env.getProperty("status.amount.refund.failed"));
						transactionInfoRepository.save(transactionHistory);
					}

				});

			}
		});

		return true;
	}

	@SuppressWarnings("rawtypes")
	public boolean acceptingForProposal(CrowdSaleProposalDTO crowdSaleDTO) throws Exception {

		crowdSaleDTO.setMessage("Approval Failed!");

		Credentials credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
				this.env.getProperty("credentials.address"));
		Date currentDate = new Date();
		equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);
		Tuple7 Tuple8 = (Tuple7) equaZone.getAllProposals(crowdSaleDTO.getProposalNumber()).send();
		Tuple7 Tuple7 = (Tuple7) equaZone.getApproveProposals(crowdSaleDTO.getProposalNumber()).send();
		String dateInString = (String) Tuple8.getValue6();
		String endTime = (String) Tuple8.getValue7();
		BigInteger reject = (BigInteger) Tuple7.getValue7();
		Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateInString);
		Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(endTime);

		// currentDate.after(startDate)&&
		if (reject.intValue() != 1) {
			if (currentDate.before(endDate) || crowdSaleDTO.getAccepted() == false) {
				TransactionReceipt transactionReceipt = (TransactionReceipt) equaZone
						.accept(crowdSaleDTO.getProposalNumber(), crowdSaleDTO.getAccepted()).send();

				if (transactionReceipt != null) {

					List<com.equocoin.soliditytojava.EquaZone.StatusEventResponse> StatusEventResponse = equaZone
							.getStatusEvents(transactionReceipt);

					if (StatusEventResponse.size() > 0) {

						for (com.equocoin.soliditytojava.EquaZone.StatusEventResponse StatusEventResponses : StatusEventResponse) {

							if (StatusEventResponses.status.equalsIgnoreCase("success")) {

								return true;
							} else {
								crowdSaleDTO.setMessage("Proposal rejected succesfully!");
								return false;
							}
						}
					} else {
						crowdSaleDTO.setMessage("Proposal Approval Failure");
						return false;
					}

				} else {
					crowdSaleDTO.setMessage("Proposal Approval Failed");
					return false;
				}

			} else {
				crowdSaleDTO.setMessage("Proposal Date Expired!");
				return false;
			}
		} else {
			crowdSaleDTO.setMessage("Proposal already aproved");
			return false;
		}
		return false;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<CrowdSaleProposalDTO> adminProposallist(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		ArrayList crowdSaleList = new ArrayList();

		Credentials credentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
				env.getProperty("credentials.address"));

		equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);

		BigInteger count = (BigInteger) equaZone.countProposalList().send();

		int tokenCount = count.intValue();

		new ArrayList();
		if (tokenCount != 0) {
			for (int i = 0; i < tokenCount; ++i) {

				CrowdSaleProposalDTO crowdSaleDTO = new CrowdSaleProposalDTO();
				BigInteger bi = BigInteger.valueOf((long) i);
				crowdSaleDTO.setProposalId(bi);
				Config configInfo = configInfoRepository.findConfigByConfigKey("apache.server");
				ProposalInfo proposalInfo = proposalInfoRepository.findInfoByProposalNo(crowdSaleDTO.getProposalId());
				String dbPath = proposalInfo.getDbPath();
				String basePath = configInfo.getConfigValue();
				Tuple7 Tuple8 = (Tuple7) equaZone.getAllProposals(bi).send();
				Tuple7 Tuple7 = (Tuple7) equaZone.getApproveProposals(bi).send();
				BigInteger accept = (BigInteger) Tuple7.getValue6();
				BigInteger reject = (BigInteger) Tuple7.getValue7();

				if (accept.compareTo(new BigInteger("2")) == 0) {
					crowdSaleDTO.setStatus(env.getProperty("proposal.accept"));

				} else if (reject.compareTo(new BigInteger("1")) == 0) {
					crowdSaleDTO.setStatus(env.getProperty("proposal.reject"));

				} else if (accept.compareTo(new BigInteger("0")) == 0 && reject.compareTo(new BigInteger("0")) == 0) {
					crowdSaleDTO.setStatus(env.getProperty("proposal.pending"));
				}
				if (reject.intValue() != 1) {
					crowdSaleDTO.setProposalCreatedBy((String) Tuple7.getValue1());
					crowdSaleDTO.setProposalTitle((String) Tuple8.getValue1());
					crowdSaleDTO.setProposalAmount((BigInteger) Tuple8.getValue3());
					crowdSaleDTO.setLike((BigInteger) Tuple8.getValue4());
					crowdSaleDTO.setDisLike((BigInteger) Tuple8.getValue5());
					crowdSaleDTO.setProposalCategory((String) Tuple8.getValue2());
					crowdSaleDTO.setStartDates((String) Tuple8.getValue6());
					crowdSaleDTO.setEndDates((String) Tuple8.getValue7());
					crowdSaleDTO.setProposalCreatedByName((String) Tuple7.getValue3());
					crowdSaleDTO.setPaymentMode((String) Tuple7.getValue3());
					crowdSaleDTO.setProposalDetails((String) Tuple7.getValue4());
					crowdSaleDTO.setProposalDOC(basePath + dbPath);
					if (proposalInfo.getProposalDetailsLink() != null) {
						crowdSaleDTO.setProposalDetailsLink(proposalInfo.getProposalDetailsLink());
					}
					if (proposalInfo != null) {
						if (proposalInfo.getProposalTransferStatus() == 1) {
							crowdSaleDTO.setProposalFundingStatus(env.getProperty("fund.success"));
						}
					}
					crowdSaleList.add(crowdSaleDTO);
				}
			}
			return crowdSaleList;
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<CrowdSaleProposalDTO> adminViewProposallist(CrowdSaleProposalDTO crowdSaleProposalDTO)
			throws Exception {
		ArrayList crowdSaleList = new ArrayList();
		Config configInfo = configInfoRepository.findConfigByConfigKey("apache.server");
		ProposalInfo proposalInfo = proposalInfoRepository.findInfoByProposalNo(crowdSaleProposalDTO.getProposalId());
		String dbPath = proposalInfo.getDbPath();
		String basePath = configInfo.getConfigValue();
		Credentials credentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
				env.getProperty("credentials.address"));

		equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);

		BigInteger count = (BigInteger) equaZone.countProposalList().send();

		int tokenCount = count.intValue();

		new ArrayList();
		if (tokenCount != 0) {
			CrowdSaleProposalDTO crowdSaleDTO = new CrowdSaleProposalDTO();
			BigInteger bi = crowdSaleProposalDTO.getProposalId();
			crowdSaleDTO.setProposalId(crowdSaleProposalDTO.getProposalId());
			Tuple7 Tuple8 = (Tuple7) equaZone.getAllProposals(bi).send();
			crowdSaleDTO.setProposalCreatedBy((String) Tuple8.getValue1());
			crowdSaleDTO.setProposalTitle((String) Tuple8.getValue2());
			crowdSaleDTO.setProposalAmount((BigInteger) Tuple8.getValue3());
			crowdSaleDTO.setLike((BigInteger) Tuple8.getValue4());
			crowdSaleDTO.setDisLike((BigInteger) Tuple8.getValue5());
			Date startTime = new Date((long) ((BigInteger) Tuple8.getValue6()).intValue());
			Date endTime = new Date((long) ((BigInteger) Tuple8.getValue7()).intValue());
			crowdSaleDTO.setStartDate(startTime);
			crowdSaleDTO.setEndDate(endTime);
			crowdSaleDTO.setProposalDOC(basePath + dbPath);
			crowdSaleList.add(crowdSaleDTO);

			return crowdSaleList;
		}
		return null;
	}

	public boolean sentAmountByprposal() throws Exception {

		CrowdSaleProposalDTO crowdSaleProposalDTO = new CrowdSaleProposalDTO();
		Credentials credentials = null;
		Equacoins assetToken = null;

		credentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
				env.getProperty("credentials.address"));
		equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);

		BigInteger count = (BigInteger) equaZone.countProposalList().send();
		int tokenCount = count.intValue();
		Date date = new Date();
		if (tokenCount != 0) {
			for (int j = 0; j < tokenCount; ++j) {
				BigInteger bi = BigInteger.valueOf((long) j);
				crowdSaleProposalDTO.setProposalId(bi);
				Tuple7 Tuple7 = (Tuple7) equaZone.getAllProposals(bi).send();
				Tuple7 Tuple8 = (Tuple7) equaZone.getApproveProposals(bi).send();

				String endTime = (String) Tuple7.getValue7();
				BigInteger likeCount = (BigInteger) Tuple7.getValue4();
				crowdSaleProposalDTO.setLikeCount(likeCount);
				BigInteger disLikeCount = (BigInteger) Tuple7.getValue5();
				crowdSaleProposalDTO.setDislikeCount(disLikeCount);
				BigInteger totalVoteCount = likeCount.add(disLikeCount);
				crowdSaleProposalDTO.setTotalVoteCount(totalVoteCount);
				Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(endTime);
				crowdSaleProposalDTO.setPaymentMode((String) Tuple8.getValue3());
				ProposalInfo proposalInfo = proposalInfoRepository
						.findInfoByProposalNo(crowdSaleProposalDTO.getProposalId());
				if (proposalInfo != null) {
					crowdSaleProposalDTO.setProposalTransactionStatus(proposalInfo.getProposalTransferStatus());

					crowdSaleProposalDTO.setLike((BigInteger) Tuple7.getValue4());
					crowdSaleProposalDTO.setEndDate(endDate);
					String createdBy = (String) Tuple8.getValue1();
					crowdSaleProposalDTO.setToAddress((String) Tuple8.getValue1());
					BigInteger proposalAmnt = (BigInteger) Tuple7.getValue3();
					crowdSaleProposalDTO.setProposalAmount(proposalAmnt);

					if (crowdSaleProposalDTO.getProposalTransactionStatus() == 0 && date.after(endDate)) {
						Float percentage = countPercentage(crowdSaleProposalDTO);
						Float roundPercentage = (float) Math.round(percentage);
						if (roundPercentage >= 60) {
							Double personaValEQAVal = crowdSaleProposalDTO.getProposalAmount().doubleValue();
							TransactionReceipt transactionReceipt = null;

							if (createdBy != null) {
								assetToken = Equacoins.load(this.env.getProperty("token.address"), this.web3j,
										credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
								transactionReceipt = assetToken
										.transfer(createdBy, new BigDecimal(personaValEQAVal * 10000).toBigInteger())
										.send();
								if (transactionReceipt != null) {
									proposalInfo.setProposalTransferStatus(1);
									proposalInfoRepository.save(proposalInfo);
									TransactionHistory transactionHistory = new TransactionHistory();
									RegisterInfo registerInfo = registerInfoRepository
											.findEquacoinUserInfoByEtherWalletAddress(
													EncryptDecrypt.encrypt(createdBy));
									transactionHistory.setFromAddress(this.env.getProperty("main.address"));
									transactionHistory.setToAddress(createdBy);
									transactionHistory.setAmount(new Double(0d));
									transactionHistory.setToken(personaValEQAVal);
									transactionHistory.setPaymentMode(env.getProperty("admin.payment"));
									transactionHistory.setCreatedDate(new Date());
									transactionHistory.setStatus(env.getProperty("status.success"));
									transactionHistory.setRegisterInfo(registerInfo);

									transactionInfoRepository.save(transactionHistory);
								}
							}

						}

					}
				}
			}

		}

		// }

		return false;
	}

	public Integer validVotingEquaToken(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {

		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		List<RegisterInfo> registerDesStatus = (List<RegisterInfo>) registerInfoRepository.findAll();
		Credentials credentials = null;
		int count = 0;
		RegisterInfo registerInfo = null;
		if (registerDesStatus != null) {
			for (int i = 1; i <= registerDesStatus.size(); i++) {
				CrowdSaleProposalDTO crowdSaleProposalDTO2 = new CrowdSaleProposalDTO();
				registerInfo = registerInfoRepository.findById(i);
				if (registerInfo != null) {
					if (registerInfo.getType() == 0) {
						crowdSaleProposalDTO2
								.setEncryptWalletAddress(EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()));
						String decryptWalletAddress = EncryptDecrypt.decrypt(registerInfo.getWalletAddress());
						String decryptWalletPassword = EncryptDecrypt.decrypt(registerInfo.getWalletPassword());
						crowdSaleProposalDTO2.setWalletPassword(decryptWalletPassword);
						crowdSaleProposalDTO2
								.setCredentialAddress(config.getConfigValue() + "//" + decryptWalletAddress);
						String walletAddress = equocoinUtils.getWalletAddress(config.getConfigValue(),
								decryptWalletAddress);
						crowdSaleProposalDTO2.setWalletAddress(walletAddress);

						credentials = WalletUtils.loadCredentials(decryptWalletPassword,
								crowdSaleProposalDTO2.getCredentialAddress());

						Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,
								BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
						if (assetToken != null) {
							BigInteger walletBalance = assetToken.balanceOf(walletAddress).send();
							BigInteger balance = new BigInteger("1");
							if (walletBalance.compareTo(balance) == 1 || walletBalance.compareTo(balance) == 0) {
								count++;

							}
						}
					}
				}
			}
		}

		crowdSaleProposalDTO.setDacUserCount(count++);
		return crowdSaleProposalDTO.getDacUserCount();

	}

	public Float countPercentage(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		Integer validVotingEquaToken = validVotingEquaToken(crowdSaleProposalDTO);
		DacUserInfo dacUserInfo = dacUserInfoRepository.findOne(new Integer(1));
		dacUserInfo.setUserCount(crowdSaleProposalDTO.getDacUserCount());
		dacUserInfoRepository.save(dacUserInfo);
		if (validVotingEquaToken != 0) {
			Float votePercentage = (((crowdSaleProposalDTO.getLike().floatValue())
					/ crowdSaleProposalDTO.getDacUserCount()) * 100);
			crowdSaleProposalDTO.setPercentage(votePercentage);
			return votePercentage;
		}
		return null;
	}

	public Float dislikeCountPercentage(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		Integer validVotingEquaToken = validVotingEquaToken(crowdSaleProposalDTO);
		if (validVotingEquaToken != 0) {
			Float votePercentage = (((crowdSaleProposalDTO.getDisLike().floatValue())
					/ crowdSaleProposalDTO.getDacUserCount()) * 100);
			crowdSaleProposalDTO.setPercentage(votePercentage);
			return votePercentage;
		}
		return null;
	}

}
