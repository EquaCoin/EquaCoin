package com.equocoin.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.tx.Contract;
import org.web3j.utils.Convert;

import com.equocoin.dto.CrowdSaleProposalDTO;
import com.equocoin.dto.MessageInfoDTO;
import com.equocoin.dto.RegisterDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.model.Config;
import com.equocoin.model.MintInfo;
import com.equocoin.model.ProposalInfo;
import com.equocoin.model.RegisterInfo;
import com.equocoin.model.SupportInfo;
import com.equocoin.model.TransactionHistory;
import com.equocoin.repository.ConfigInfoRepository;
import com.equocoin.repository.MintInfoRepository;
import com.equocoin.repository.ProposalInfoRepository;
import com.equocoin.repository.RegisterInfoRepository;
import com.equocoin.repository.SupportInfoRepository;
import com.equocoin.repository.TokenInfoRepository;
import com.equocoin.service.EmailNotificationService;
import com.equocoin.soliditytojava.EquaZone;
import com.equocoin.soliditytojava.Equacoins;


@Service
public class EquocoinUtils {
	static final Logger LOG = LoggerFactory.getLogger(EquocoinUtils.class);
	
	//private final Web3j web3j = Web3j.build(new HttpService());
	//private final Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/"));
	private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io"));
	
	@SuppressWarnings("unused")
	@Autowired
	private TokenInfoRepository tokenInfoRepository;
	@SuppressWarnings("unused")
	@Autowired
	private ProposalInfoRepository proposalInfoRepository;
	
	@Autowired
	private HttpSession session;

	@Autowired
	private SessionCollector sessionCollector;

	@Autowired
	private RegisterInfoRepository registerInfoRepository;
	@Autowired
	private MintInfoRepository mintInfoRepository;
	@Autowired
	private EmailNotificationService emailNotificationService;
	
	@Autowired
	private ConfigInfoRepository configInfoRepository;
	@Autowired
	private EquocoinUtils equocoinUtils;
	@Autowired
	private SupportInfoRepository supportInfoRepository;
	
	static final String regex = "[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

	@Autowired
	private Environment env;

	public boolean validateRegistrationParam(RegisterDTO registerDTO) {
		if (registerDTO.getMobileNo() != null && StringUtils.isNotBlank(registerDTO.getMobileNo())
				&& registerDTO.getIsdCode() != null && StringUtils.isNotBlank(registerDTO.getIsdCode())
				&& registerDTO.getPassword() != null && StringUtils.isNotBlank(registerDTO.getPassword())
				&& registerDTO.getConfirmPassword() != null && StringUtils.isNotBlank(registerDTO.getConfirmPassword())
				&& registerDTO.getEmailId() != null && StringUtils.isNotBlank(registerDTO.getEmailId())
				&& registerDTO.getWalletPassword() != null && StringUtils.isNotBlank(registerDTO.getWalletPassword())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateEmail(String emailId) {
		emailId = emailId.replaceFirst("^ *", "");
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(emailId);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateLoginParam(RegisterDTO registerDTO) {
		if (registerDTO.getEmailId() != null && StringUtils.isNotBlank(registerDTO.getEmailId())) {
			return true;
		}
		return false;
	}

	public boolean validateUserName(RegisterDTO registerDTO) {
		Pattern pattern = Pattern.compile("^(^/s)(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$");
		// Pattern pattern = Pattern.compile("\\d{3}-\\d{7}");
		Matcher matcher = pattern.matcher(registerDTO.getUserName());
		boolean isValidMail = validateEmail(registerDTO.getUserName());
		if (isValidMail) {
			registerDTO.setEmailId(registerDTO.getUserName());
			return true;
		} else if (matcher.matches()) {
			registerDTO.setMobileNo(registerDTO.getUserName());
			return true;
		} else if (matcher.matches()) {
			registerDTO.setWalletAddress(registerDTO.getWalletAddress());
			return true;
		}

		else {
			return false;
		}
	}

	public boolean validateTokenCreationParam(TokenDTO tokenDTO) {
		if (tokenDTO.getCoinName() != null && StringUtils.isNotBlank(tokenDTO.getCoinName())
				&& tokenDTO.getInitialValue() != null && StringUtils.isNotBlank(tokenDTO.getInitialValue().toString())
				&& tokenDTO.getCoinSymbol() != null && StringUtils.isNotBlank(tokenDTO.getCoinSymbol())
				&& tokenDTO.getDecimalUnits() != null
				&& StringUtils.isNotBlank(tokenDTO.getDecimalUnits().toString())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateTokenBalancePrams(TokenDTO tokenDTO) {

		if (tokenDTO.getTokenAddress() != null && StringUtils.isNotBlank(tokenDTO.getTokenAddress())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateTransferTokenPrams(TokenDTO tokenDTO) {
		if (tokenDTO.getToAddress() != null && StringUtils.isNotBlank(tokenDTO.getToAddress())
				&& tokenDTO.getRequestToken() != null && StringUtils.isNotBlank(tokenDTO.getRequestToken().toString())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateTransferAmountPrams(TokenDTO tokenDTO) {

		if (tokenDTO.getRequestToken() != null && StringUtils.isNotBlank(tokenDTO.getRequestToken().toString())
				
				&& (tokenDTO.getWalletPassword() != null) && StringUtils.isNotBlank(tokenDTO.getWalletPassword())) {
			return true;
		}

		return false;
	}

	public boolean isvalidsession(HttpServletRequest request, HttpServletResponse response, RegisterDTO registerDTO) {

		if (session.getAttribute("emailId") != null) {


			session.setMaxInactiveInterval(30 * 60);
			return true;
		}

		return false;
	}

	public boolean validateCrowdSaleParams(CrowdSaleProposalDTO crowdSaleDTO) {
		if ((crowdSaleDTO.getProposalCategory() != null && StringUtils.isNotBlank(crowdSaleDTO.getProposalCategory()))
				&& (crowdSaleDTO.getProposalTitle() != null && StringUtils.isNotBlank(crowdSaleDTO.getProposalTitle()))
				&& (crowdSaleDTO.getProposalCreatedBy() != null
						&& StringUtils.isNotBlank(crowdSaleDTO.getProposalCreatedBy()))
				&& (crowdSaleDTO.getProposalDetails() != null
						&& StringUtils.isNotBlank(crowdSaleDTO.getProposalDetails()))
				&& (crowdSaleDTO.getProposalAmount() != null
						&& StringUtils.isNotBlank(crowdSaleDTO.getProposalAmount().toString()))
				&& (crowdSaleDTO.getStartDates() != null
						&& StringUtils.isNotBlank(crowdSaleDTO.getStartDates().toString()))
				&& (crowdSaleDTO.getEndDates() != null
						&& StringUtils.isNotBlank(crowdSaleDTO.getEndDates().toString()))) {
			return true;
		}
		return false;
	}

	public boolean validateCrowdSaleVotingParams(CrowdSaleProposalDTO crowdSaleDTO) {
		if ((crowdSaleDTO.getProposalNumber() != null
				&& StringUtils.isNotBlank(crowdSaleDTO.getProposalNumber().toString()))
				&& (crowdSaleDTO.getVoted() != null && StringUtils.isNotBlank(crowdSaleDTO.getVoted().toString()))) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("static-access")
	public boolean validateLogoutParams(RegisterDTO registerDTO) {

		RegisterInfo equocoinUserInfos = registerInfoRepository
				.findEquocoinUserInfoByEmailId(registerDTO.getEmailId().trim());

		if (equocoinUserInfos != null) {
			HttpSession session = sessionCollector.find(registerDTO.getSessionId());
			HttpSessionEvent event = new HttpSessionEvent(session);
			sessionCollector.sessionDestroyed(event);
			session.invalidate();
			return true;
		}
		return false;
	}

	@SuppressWarnings("static-access")
	public boolean isSessionExpired(TokenDTO tokenDTO) {
		try {
			HttpSession session = sessionCollector.find(tokenDTO.getSessionId());
			// System.out.println("Session ID "+tokenDTO.getSessionId());
			String email = (String) session.getAttribute("emailId");
			if (email != null) {
				tokenDTO.setEmailId(email);
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception

			return false;
		}

		return false;
	}


	public String getWalletAddress(String fileLocation, String fileName)
			throws FileNotFoundException, IOException, ParseException {
		try {
			fileLocation = fileLocation.replace("/", "\\");
			JSONParser parser = new JSONParser();
			// Object object = parser.parse(new
			// FileReader(WalletUtils.getMainnetKeyDirectory() + "//" +
			// fileName));
			Object object = parser.parse(new FileReader(fileLocation + "//" + fileName));
			JSONObject jsonObject = (JSONObject) object;
			String address = "0x" + (String) jsonObject.get("address");
			return address;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public boolean validateCancelTokenParam(TokenDTO tokenDTO) {

		BigInteger token=new BigDecimal( tokenDTO.getTokenBalance()).toBigInteger();
		if(BigInteger.ZERO.equals(token)){
			return false;
		}
		return true;
	}

	@SuppressWarnings("static-access")
	public boolean isSessionExpired(String sessionId) {

		HttpSession session = sessionCollector.find(sessionId);
		try {
			if (session.getAttribute("emailId") != null) {
				
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

		return false;
	}

	@SuppressWarnings("static-access")
	public boolean validatePasswordPrams(TokenDTO tokenDTO) {
		HttpSession session = sessionCollector.find(tokenDTO.getSessionId());
		RegisterInfo equocoinUserInfos = registerInfoRepository
				.findEquocoinUserInfoByEmailId(session.getAttribute("emailId"));
		try {
			if (equocoinUserInfos != null) {
				String decryptWalletPassword = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletPassword());
				if (decryptWalletPassword.equals(tokenDTO.getWalletPassword())) {
					tokenDTO.setWalletPassword(tokenDTO.getWalletPassword());
					return true;
				}
				return false;
			}

		} catch (Exception e) {
		}
		return false;
	}



	public TokenDTO listTransactions(TransactionHistory transaction) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("#.####");
		TokenDTO transactions = new TokenDTO();
		transactions.setFromAddress(transaction.getFromAddress());
		transactions.setToAddress(transaction.getToAddress());
		transactions.setTransferAmount(new BigDecimal(df.format(transaction.getAmount())));
		transactions.setTokens(transaction.getToken());
		transactions.setPaymentMode(transaction.getPaymentMode());
		transactions.setCreateDate(transaction.getCreatedDate());
		transactions.setTransactionStatus(transaction.getStatus());
		return transactions;
	}

	public boolean isValidateEthAddress(TokenDTO tokenDTO) {
		// TODO Auto-generated method stub
		Pattern pattern = Pattern.compile("^0x.{40}$");
		Matcher matcher = pattern.matcher(tokenDTO.getToAddress());
		if (matcher.matches()) {

			return true;
		}

		return false;
	}
	@SuppressWarnings("unused")
	public TokenDTO listMintTokens(MintInfo mintInfo) {
		DecimalFormat df = new DecimalFormat("#.####");
		TokenDTO mintInfos = new TokenDTO();
		mintInfos.setFromAddress(mintInfo.getFromAddress());
		mintInfos.setMintedAmount(mintInfo.getMintToken());
		mintInfos.setCreateDate(mintInfo.getCreatedDate());
		mintInfos.setMintStatus(mintInfo.getStatus());
		return mintInfos;
	}
	public List<TokenDTO> transactionList(String walletAddress) {

		List<TokenDTO> mintList = new ArrayList<TokenDTO>();
		List<MintInfo> transactionInfos = (List<MintInfo>) mintInfoRepository
				.findAll();
		
		for (MintInfo mintInfo : transactionInfos) {
			TokenDTO transaction = listMintTokens(mintInfo);
			mintList.add(transaction);
		}

		return mintList;

	}

	@SuppressWarnings("static-access")
	public boolean validateUserIdParams(CrowdSaleProposalDTO crowdSaleDTO) throws Exception {
		
		HttpSession session = sessionCollector.find(crowdSaleDTO.getSessionId());
		RegisterInfo equocoinUserInfos = registerInfoRepository
				.findEquocoinUserInfoByEmailId(session.getAttribute("emailId"));
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		if(equocoinUserInfos !=null && config !=null){
		 crowdSaleDTO.setUserId(equocoinUserInfos.getId());
		 String decryptWalletAddress = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletAddress());
			String decryptWalletPassword = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletPassword());
			crowdSaleDTO.setWalletPassword(decryptWalletPassword);
			crowdSaleDTO.setWalletAddress(config.getConfigValue() + "//" + decryptWalletAddress);
			String walletAddress = equocoinUtils.getWalletAddress(config.getConfigValue(), decryptWalletAddress);
			crowdSaleDTO.setToAddress(walletAddress);
			crowdSaleDTO.setEmailId(equocoinUserInfos.getEmailId());
			if (walletAddress == null) {
				return false;
			}
		return true;
		}
		return false;
	}

	@SuppressWarnings("static-access")
	public boolean validateUserAccount(CrowdSaleProposalDTO crowdSaleDTO,String sessionId) throws Exception {
		HttpSession session = sessionCollector.find(sessionId);
		RegisterInfo equocoinUserInfos = registerInfoRepository
				.findEquocoinUserInfoByEmailId(session.getAttribute("emailId"));
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		if(equocoinUserInfos !=null && config !=null){
			crowdSaleDTO.setUserId(equocoinUserInfos.getId());
		 String decryptWalletAddress = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletAddress());
			String decryptWalletPassword = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletPassword());
			crowdSaleDTO.setWalletPassword(decryptWalletPassword);
			crowdSaleDTO.setWalletAddress(config.getConfigValue() + "//" + decryptWalletAddress);
		return true;
		}
		return false;
	}

	public boolean validateProposalAcceptParams(CrowdSaleProposalDTO crowdSaleDTO) {
		if ((crowdSaleDTO.getProposalNumber() != null
				&& StringUtils.isNotBlank(crowdSaleDTO.getProposalNumber().toString()))
				&& (crowdSaleDTO.getAccepted() != null && StringUtils.isNotBlank(crowdSaleDTO.getAccepted().toString()))) {
			return true;
		}
		return false;
	}

	public boolean validateViewProposalParams(CrowdSaleProposalDTO crowdSaleProposalDTO) {
		if ((crowdSaleProposalDTO.getProposalId() != null
				&& StringUtils.isNotBlank(crowdSaleProposalDTO.getProposalId().toString()))
				) {
			return true;
		}
		return false;
	}

	public boolean validAmount(CrowdSaleProposalDTO crowdSaleProposalDTO) {
		boolean status = checkMainbalance(crowdSaleProposalDTO);

		if (status) {

			BigDecimal balance = crowdSaleProposalDTO.getMainBalance();
			if(balance.doubleValue() >= 0.12){
				
				return true;
			}
			
		}

		return false;
	}

	public boolean checkMainbalance(CrowdSaleProposalDTO crowdSaleProposalDTO) {
		EthGetBalance ethGetBalance;
		try {
			DecimalFormat df = new DecimalFormat("#.########");
			ethGetBalance = web3j.ethGetBalance(crowdSaleProposalDTO.getToAddress(), DefaultBlockParameterName.LATEST)
					.sendAsync().get();

			BigDecimal wei = new BigDecimal(ethGetBalance.getBalance());
			
			// BigInteger wei = ethGetBalance.getBalance();
			BigDecimal amountCheck = Convert.fromWei(wei, Convert.Unit.ETHER);

			crowdSaleProposalDTO.setMainBalance(new BigDecimal(df.format(amountCheck.doubleValue())));

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return true;
	}
	
	
	public boolean validateRequestTokenPrams(TokenDTO tokenDTO) {
		if (tokenDTO.getToAddress() != null && StringUtils.isNotBlank(tokenDTO.getToAddress())
				&& tokenDTO.getRequestToken() != null
				&& StringUtils.isNotBlank(tokenDTO.getRequestToken().toString())
				&& tokenDTO.getWalletPassword() != null && StringUtils.isNotBlank(tokenDTO.getWalletPassword())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean validateTokenVal(TokenDTO tokenDTO)
	{
		if((tokenDTO.getRequestToken())>0)
		{
			return true;
		}
		return false;		
	}

	public boolean isValidSubject(MessageInfoDTO messageInfoDTO) {
		if (messageInfoDTO.getSubject() != null && StringUtils.isNotEmpty(messageInfoDTO.getSubject())
				&& StringUtils.isNotBlank(messageInfoDTO.getSubject())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isValidContent(MessageInfoDTO messageInfoDTO) {
		if (messageInfoDTO.getContent() != null && StringUtils.isNotEmpty(messageInfoDTO.getContent())
				&& StringUtils.isNotBlank(messageInfoDTO.getContent())) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("static-access")
	public boolean sendMessage(MessageInfoDTO messageInfoDTO,TokenDTO tokenDTO) {
		HttpSession session = sessionCollector.find(tokenDTO.getSessionId());
		RegisterInfo equocoinUserInfos = registerInfoRepository
				.findEquocoinUserInfoByEmailId(session.getAttribute("emailId"));
		
		if(equocoinUserInfos != null){
			messageInfoDTO.setUserId(equocoinUserInfos.getId());
		}
		SupportInfo supportInfo=new SupportInfo();
		boolean isEmailSent = emailNotificationService.sendEmailSupport(env.getProperty("support.mail"),
				messageInfoDTO.getSubject(), messageInfoDTO.getContent());
        if(isEmailSent){
	    supportInfo.setSubject(messageInfoDTO.getSubject());
	    supportInfo.setContent(messageInfoDTO.getContent());
	    supportInfo.setUserId(messageInfoDTO.getUserId());
		supportInfoRepository.save(supportInfo);
		if(supportInfoRepository != null){
		return true;
		}
     }
		return false;
	}
	
	public boolean validateSendEtherPrams(TokenDTO tokenDTO) {
		if (tokenDTO.getToAddress() != null && StringUtils.isNotBlank(tokenDTO.getToAddress())
				&& tokenDTO.getAmount() != null && StringUtils.isNotBlank(tokenDTO.getAmount().toString())
				&& tokenDTO.getWalletPassword() != null && StringUtils.isNotBlank(tokenDTO.getWalletPassword())) {
			
			return true;
		} else {
			return false;
		}
		
	}
	
	public boolean validateEtherVal(TokenDTO tokenDTO)
	{
		if(tokenDTO.getAmount().intValue()>0)
		{
			return true;
		}
		return false;		
	}

	
	public boolean allowElligibleUser() throws Exception {
		
		CrowdSaleProposalDTO crowdSaleProposalDTO=new CrowdSaleProposalDTO();
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		List<RegisterInfo> registerInfos = (List<RegisterInfo>) registerInfoRepository.findAll();
         int size=registerInfos.size();
             if(registerInfos.size() == 0){
        	 return false;
         }
				for (int i = 1; i <= size; i++) {
					RegisterInfo registerInfo= registerInfoRepository.findEquocoinUserInfoById(i);
					 String decryptWalletAddress = EncryptDecrypt.decrypt(registerInfo.getWalletAddress());
						String decryptWalletPassword = EncryptDecrypt.decrypt(registerInfo.getWalletPassword());
						crowdSaleProposalDTO.setWalletPassword(decryptWalletPassword);
						crowdSaleProposalDTO.setWalletAddress(config.getConfigValue() + "//" + decryptWalletAddress);
						String walletAddress = equocoinUtils.getWalletAddress(config.getConfigValue(), decryptWalletAddress);
						
					Credentials credentials = WalletUtils.loadCredentials(decryptWalletPassword,
							crowdSaleProposalDTO.getWalletAddress());
					Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,
							BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));

					BigInteger balance = assetToken.balanceOf(walletAddress).send();
					System.out.println("outside comparison" +balance);
					if(balance.compareTo(new BigInteger("0")) == 1){
						System.out.println("registerInfo"+i);
						System.out.println("inside comparison"+balance);
						registerInfoRepository.save(registerInfo);
						
					}
					
			     }
				return true;
	}
	
	public boolean isValidateProposalAddress(CrowdSaleProposalDTO crowdSaleProposalDTO) {
		Pattern pattern = Pattern.compile("^0x.{40}$");
		Matcher matcher = pattern.matcher(crowdSaleProposalDTO.getProposalCreatedBy());
		if (matcher.matches()) {
			return true;
		}

		return false;
	}
	
	public boolean validProposalAmount(CrowdSaleProposalDTO crowdSaleProposalDTO) {
		boolean status = checkMainbalance(crowdSaleProposalDTO);

		if (status) {

			BigDecimal walletBalance = crowdSaleProposalDTO.getMainBalance();
			//BigDecimal balance=walletBalance.add(new BigDecimal(0.1));
			if (walletBalance.compareTo(crowdSaleProposalDTO.getEtherAmount().add(new BigDecimal(0.1))) == 1) {

				return true;
			}
			return false;
			

		}

		return false;
	}
	
	public boolean validProposalToken(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, crowdSaleProposalDTO.getCredentials(),
				BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
		
		BigInteger walletBalance = assetToken.balanceOf(crowdSaleProposalDTO.getToAddress()).send();
		//BigInteger balance=walletBalance.add(new BigInteger("1"));
			if (walletBalance.compareTo(crowdSaleProposalDTO.getEquaAmount().toBigInteger().add(new BigInteger("1"))) == 1) {
           return true;
		
		}

		return false;
	}
	
@SuppressWarnings("rawtypes")
public boolean validateDacMemberBalance(CrowdSaleProposalDTO crowdSaleDTO) throws Exception {
		List<CrowdSaleProposalDTO> crowdSaleProposalDTOs = new ArrayList<CrowdSaleProposalDTO>();
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		Credentials credentials=null;
		List<RegisterInfo> registerDesStatus = (List<RegisterInfo>) registerInfoRepository.findAll();
		RegisterInfo registerInfo = null;
		int successCount=0;
		crowdSaleDTO.setDacUserCount(registerDesStatus.size());
		for (int i = 1; i <= registerDesStatus.size(); i++) {
			CrowdSaleProposalDTO crowdSaleProposalDTO2 = new CrowdSaleProposalDTO();
			    registerInfo = registerInfoRepository.findById(i);
			    crowdSaleProposalDTO2.setDacUserCount(registerDesStatus.size());
				crowdSaleProposalDTO2.setEncryptWalletAddress(registerInfo.getWalletAddress());
				String decryptWalletAddress = EncryptDecrypt.decrypt(registerInfo.getWalletAddress());
				String decryptWalletPassword = EncryptDecrypt.decrypt(registerInfo.getWalletPassword());
				crowdSaleProposalDTO2.setWalletPassword(decryptWalletPassword);
				crowdSaleProposalDTO2.setCredentialAddress(config.getConfigValue() + "//" + decryptWalletAddress);
				String walletAddress = equocoinUtils.getWalletAddress(config.getConfigValue(), decryptWalletAddress);
				crowdSaleProposalDTO2.setWalletAddress(walletAddress);
				crowdSaleProposalDTOs.add(crowdSaleProposalDTO2);
		}
		
			credentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
					env.getProperty("credentials.address"));

			EquaZone equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
					Contract.GAS_LIMIT);

			BigInteger count = (BigInteger) equaZone.countProposalList().send();
			int tokenCount = count.intValue();
			Date date = new Date();
			if (tokenCount != 0) {
				for (int j = 0; j < tokenCount; j++) {
					BigInteger bi = BigInteger.valueOf((long) j);
					crowdSaleDTO.setProposalId(bi);
					Tuple7 Tuple7 = (Tuple7) equaZone.getAllProposals(bi).send();
					Tuple7 Tuple8 = (Tuple7) equaZone.getApproveProposals(bi).send();
				
					String endTime = (String) Tuple7.getValue7();
					Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(endTime);
					crowdSaleDTO.setPaymentMode((String) Tuple8.getValue3());
					crowdSaleDTO.setEndDate(endDate);
					String createdBy = (String) Tuple8.getValue1();
					BigInteger proposalAmnt = (BigInteger) Tuple7.getValue3();
					crowdSaleDTO.setAproveProposalAmount(proposalAmnt.doubleValue());
					Double proposalUsercount = (double) (crowdSaleDTO.getDacUserCount() - 1);
//					BigInteger splitedAmount = crowdSaleDTO.getProposalAmount().divide(BigInteger.valueOf(proposalUsercount));
					Double splitedAmount = crowdSaleDTO.getAproveProposalAmount()/proposalUsercount;
					BigDecimal subSplittedEQCAmount = BigDecimal.valueOf(splitedAmount);
					crowdSaleDTO.setEqcAmount(subSplittedEQCAmount.toBigInteger());
					crowdSaleDTO.setEquaAmount(subSplittedEQCAmount);
					BigDecimal splittedEtherAmount = new BigDecimal(splitedAmount);
					crowdSaleDTO.setEtherAmount(splittedEtherAmount);
//					 &&  crowdSaleDTO.getEndDate().before(date)
					if (crowdSaleDTO.getPaymentMode().equalsIgnoreCase("ETH") &&  date.before(crowdSaleDTO.getEndDate())) {
							for (CrowdSaleProposalDTO crowdSaleProposalDTO2 : crowdSaleProposalDTOs) {
							if (crowdSaleProposalDTO2.getWalletAddress() != createdBy) {
								credentials = WalletUtils.loadCredentials(crowdSaleProposalDTO2.getWalletPassword(),
								crowdSaleProposalDTO2.getCredentialAddress());
								crowdSaleProposalDTO2.setToAddress(crowdSaleProposalDTO2.getWalletAddress());
								crowdSaleProposalDTO2.setEtherAmount(crowdSaleDTO.getEtherAmount());
								boolean status=validProposalAmount(crowdSaleProposalDTO2);
								if(status){
									successCount++;
								}
								else
								{
									return false;
								}
							}
						}
						
					}
//					&&  crowdSaleDTO.getEndDate().before(date)
					else if (crowdSaleDTO.getPaymentMode().equalsIgnoreCase("EQUA") &&  date.before(crowdSaleDTO.getEndDate())) {
						    for (CrowdSaleProposalDTO crowdSaleProposalDTO2 : crowdSaleProposalDTOs) {
							if (crowdSaleProposalDTO2.getWalletAddress() != createdBy) {
								credentials = WalletUtils.loadCredentials(crowdSaleProposalDTO2.getWalletPassword(),
								crowdSaleProposalDTO2.getCredentialAddress());
								crowdSaleProposalDTO2.setCredentials(credentials);
								crowdSaleProposalDTO2.setToAddress(crowdSaleProposalDTO2.getWalletAddress());
								crowdSaleProposalDTO2.setEtherAmount(crowdSaleDTO.getEtherAmount());
								crowdSaleProposalDTO2.setEquaAmount(crowdSaleDTO.getEquaAmount());
								boolean status=validProposalToken(crowdSaleProposalDTO2);
								if(status){
									successCount++;
									}
								else
								{
									return false;
								}
								
							}
						}
						
					}
				
					
			}
			if(successCount !=0){
					return true;
				}
		}
		
		return false;
	}

	public boolean validateRequestCoinParam(TokenDTO tokenDTO) {
		if (tokenDTO.getToAddress() != null && StringUtils.isNotBlank(tokenDTO.getToAddress())
				&& tokenDTO.getPaymentMode() != null && StringUtils.isNotBlank(tokenDTO.getPaymentMode())
				&& tokenDTO.getRequestToken() != null
				&& StringUtils.isNotBlank(tokenDTO.getRequestToken().toString())) {
			return true;
		}
		return false;
	}

	public boolean validateSendCoinParam(TokenDTO tokenDTO) {
		if (tokenDTO.getRequestToken() != null && !StringUtils.isEmpty(tokenDTO.getRequestToken().toString())
				&& tokenDTO.getToAddress() != null && !StringUtils.isEmpty(tokenDTO.getToAddress())
				&& tokenDTO.getSessionId() != null && !StringUtils.isEmpty(tokenDTO.getSessionId())
				&& tokenDTO.getPaymentMode() != null && !StringUtils.isEmpty(tokenDTO.getPaymentMode())
				&& tokenDTO.getWalletPassword() != null && !StringUtils.isEmpty(tokenDTO.getWalletPassword())) {
			return true;

		}
		return false;
	}

	public boolean isAppIdCheck(RegisterDTO registerDTO) {
		if(registerDTO.getAppId()!=null) {
			RegisterInfo userInfoModel = registerInfoRepository.findEquocoinUserInfoByEmailId(registerDTO.getEmailId());
			if(userInfoModel !=null) {
				userInfoModel.setAppId(registerDTO.getAppId());
				userInfoModel.setMobileType(registerDTO.getDeviceType());
				registerInfoRepository.save(userInfoModel);
			return true;
			}	
		}
		return true;
	}

	public boolean isValidateToAddress(TokenDTO tokenDTO) throws Exception {
		RegisterInfo userInfoModel = registerInfoRepository.findEquocoinUserInfoByEmailId(tokenDTO.getEmailId());
		if(userInfoModel !=null){
			String walletAddress = EncryptDecrypt.decrypt(userInfoModel.getEtherWalletAddress());
			if(tokenDTO.getToAddress().equalsIgnoreCase(walletAddress)){
				
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	public boolean validateDacMemberMinimumBalance(CrowdSaleProposalDTO crowdSaleDTO) throws Exception {

		Credentials credentials = null;

		credentials = WalletUtils.loadCredentials(crowdSaleDTO.getWalletPassword(), crowdSaleDTO.getWalletAddress());

		EquaZone equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);

		if (equaZone != null) {
			Date date = new Date();

			BigInteger bi = crowdSaleDTO.getProposalNumber();
			crowdSaleDTO.setProposalId(bi);
			Tuple7 Tuple7 = (Tuple7) equaZone.getAllProposals(bi).send();
			Tuple7 Tuple8 = (Tuple7) equaZone.getApproveProposals(bi).send();
			String endTime = (String) Tuple7.getValue7();
			Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(endTime);
			crowdSaleDTO.setPaymentMode((String) Tuple8.getValue3());
			crowdSaleDTO.setEndDate(endDate);
			BigInteger proposalAmnt = (BigInteger) Tuple7.getValue3();
			crowdSaleDTO.setAproveProposalAmount(proposalAmnt.doubleValue());
			crowdSaleDTO.setCredentials(credentials);
			boolean status = validVotingEquaToken(crowdSaleDTO);
			if (status) {
				return true;
			} else {
				return false;
			}

		}

		return false;

	}

	public boolean validVotingEquaToken(CrowdSaleProposalDTO crowdSaleProposalDTO2) throws Exception {
		Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j,
				crowdSaleProposalDTO2.getCredentials(), BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
		if (assetToken != null) {
			BigInteger walletBalance = assetToken.balanceOf(crowdSaleProposalDTO2.getToAddress()).send();
			BigInteger balance = new BigInteger("1");
			if (walletBalance.compareTo(balance) == 1 || walletBalance.compareTo(balance) == 0)  {
				return true;

			} else {
				return false;
			}
		}
		return false;
	}



	@SuppressWarnings("rawtypes")
	public boolean validateProposalDateParams(CrowdSaleProposalDTO crowdSaleDTO) throws Exception {
		Credentials credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
				this.env.getProperty("credentials.address"));
		Date currentDate = new Date();
		EquaZone equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
				Contract.GAS_LIMIT);
		if (credentials != null && equaZone != null) {
			crowdSaleDTO.setCredentials(credentials);
			Tuple7 Tuple8 = (Tuple7) equaZone.getAllProposals(crowdSaleDTO.getProposalNumber()).send();
			crowdSaleDTO.setProposalAmount((BigInteger) Tuple8.getValue3());
			String dateInString = (String) Tuple8.getValue6();
			String endTime = (String) Tuple8.getValue7();
			Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateInString);
			Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(endTime);
			crowdSaleDTO.setStartDate(startDate);
			crowdSaleDTO.setEndDate(endDate);
			 long difference = endDate.getTime() - startDate.getTime();
			 float daysBetween = (difference / (1000*60*60*24));
			if(daysBetween < 60f){
			return true;
			}
		} else {
			return false;
		}
		return false;
	}

	public boolean validateAdminEQUABalance(CrowdSaleProposalDTO crowdSaleDTO) throws Exception {
		Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, crowdSaleDTO.getCredentials(),
				BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
		if (assetToken != null) {
			BigInteger walletBalance = assetToken.balanceOf(env.getProperty("main.address")).send();
			if (walletBalance.compareTo(crowdSaleDTO.getProposalAmount()) == 1 ) {
				return true;

			}
		}
		return false;
	}

	public boolean uploadProposalLink(CrowdSaleProposalDTO crowdSaleProposalDTO) {
		
		ProposalInfo proposalInfo=proposalInfoRepository.findInfoByProposalNo(crowdSaleProposalDTO.getProposalNumber());
		if(proposalInfo != null){
			
			proposalInfo.setProposalDetailsLink(crowdSaleProposalDTO.getProposalDetailsLink());
			return true;
		}
		return false;
	}

	public boolean validateDacEquaMinimumBalance(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		Credentials credentials = WalletUtils.loadCredentials(crowdSaleProposalDTO.getWalletPassword(),
				crowdSaleProposalDTO.getWalletAddress());
		Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,
				BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
		if (assetToken != null) {
			BigInteger walletBalance = assetToken.balanceOf(crowdSaleProposalDTO.getToAddress()).send();
			if (walletBalance.compareTo(new BigInteger("1")) >= 0) {
				return true;

			}
		}
		return false;
	}
}
