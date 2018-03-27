package com.equocoin.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import com.equocoin.dto.CrowdSaleProposalDTO;
import com.equocoin.dto.RegisterDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.model.Config;
import com.equocoin.model.MintInfo;
import com.equocoin.model.RegisterInfo;
import com.equocoin.model.TokenInfo;
import com.equocoin.model.TransactionHistory;
import com.equocoin.repository.ConfigInfoRepository;
import com.equocoin.repository.MintInfoRepository;
import com.equocoin.repository.RegisterInfoRepository;
import com.equocoin.repository.TokenInfoRepository;
import com.equocoin.service.impl.TokenUserServiceImpl;
import com.equocoin.soliditytojava.AssetToken;

@Service
public class EquocoinUtils {
	static final Logger LOG = LoggerFactory.getLogger(EquocoinUtils.class);
	private final Web3j web3j = Web3j.build(new HttpService());
	@Autowired
	TokenInfoRepository tokenInfoRepository;

	@Autowired
	private HttpSession session;

	@Autowired
	private SessionCollector sessionCollector;

	@Autowired
	private RegisterInfoRepository registerInfoRepository;
	@Autowired
	private MintInfoRepository mintInfoRepository;
	
	static final String regex = "[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

	@SuppressWarnings("unused")
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
		LOG.info(emailId + " : " + matcher.matches());
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

			LOG.info("getatribute" + session.getAttribute("emailId"));

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
				&& (crowdSaleDTO.getStartDate() != null
						&& StringUtils.isNotBlank(crowdSaleDTO.getStartDate().toString()))
				&& (crowdSaleDTO.getEndDate() != null
						&& StringUtils.isNotBlank(crowdSaleDTO.getEndDate().toString()))) {
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

	public boolean isSessionExpired(TokenDTO tokenDTO) {
		try {
			HttpSession session = sessionCollector.find(tokenDTO.getSessionId());
			// System.out.println("Session ID "+tokenDTO.getSessionId());
			String email = (String) session.getAttribute("emailId");
			System.out.println("Session EmailId " + email);
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

	/*
	 * public boolean getTokenBalance(TokenDTO tokenDTO) { Credentials
	 * credentials; try { credentials = WalletUtils.loadCredentials("User0505",
	 * "E://Ethereum//private-network//keystore//UTC--2017-11-21T03-55-16.644449300Z--a61cda3cbffbb4e3e4a4af248ee215d56a7b2cb9"
	 * ); AssetToken assetToken = AssetToken.load(tokenDTO.getTokenAddress(),
	 * web3j, credentials, BigInteger.valueOf(3000000),
	 * BigInteger.valueOf(3000000));
	 * 
	 * Address address = new Address(tokenDTO.getCentralAdmin()); try {
	 * BigInteger balance = assetToken.balanceOf(address).get().getValue();
	 * tokenDTO.setTokenBalance(balance); return true; } catch
	 * (InterruptedException e) { e.printStackTrace(); } catch
	 * (ExecutionException e) { e.printStackTrace(); } } catch (IOException e) {
	 * e.printStackTrace(); } catch (CipherException e) { e.printStackTrace(); }
	 * 
	 * return false; }
	 */
	public String getWalletAddress(String fileLocation, String fileName)
			throws FileNotFoundException, IOException, ParseException {
		try {
			fileLocation = fileLocation.replace("/", "\\");
			System.out.println("wallet created" + fileLocation);
			JSONParser parser = new JSONParser();
			// Object object = parser.parse(new
			// FileReader(WalletUtils.getMainnetKeyDirectory() + "//" +
			// fileName));
			Object object = parser.parse(new FileReader(fileLocation + "//" + fileName));
			JSONObject jsonObject = (JSONObject) object;
			String address = "0x" + (String) jsonObject.get("address");
			System.out.println("Name: " + fileName);
			return address;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public boolean validateCancelTokenParam(TokenDTO tokenDTO) {

		BigInteger token=tokenDTO.getTokenBalance();
		if(BigInteger.ZERO.equals(token)){
			return false;
		}
		return true;
	}

	public boolean isSessionExpired(String sessionId) {

		HttpSession session = sessionCollector.find(sessionId);
		System.out.println("Session ID " + sessionId);
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

	/*
	 * public boolean ValidateTokenBalanceParams(TokenDTO tokenDTO) throws
	 * Exception {
	 * 
	 * boolean tokenBalance =
	 * tokenUserServiceImpl.getAdminTokenBalance(tokenDTO); if (tokenBalance) {
	 * BigInteger balanceToken = tokenDTO.getBalance(); BigInteger token =
	 * tokenDTO.getTokenBalance(); int res = token.compareTo(balanceToken); if
	 * (res == 0 && res == 1) {
	 * 
	 * return true; }
	 * 
	 * } return false; }
	 */

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
	public TokenDTO listMintTokens(MintInfo mintInfo) {
		DecimalFormat df = new DecimalFormat("#.####");
		TokenDTO mintInfos = new TokenDTO();
		mintInfos.setFromAddress(mintInfo.getFromAddress());
		mintInfos.setMintedAmount(mintInfo.getMintToken());
		mintInfos.setCreateDate(mintInfo.getCreatedDate());
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

}
