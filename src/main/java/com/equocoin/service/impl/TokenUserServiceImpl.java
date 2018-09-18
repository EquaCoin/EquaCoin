package com.equocoin.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import com.equocoin.dto.TokenDTO;
import com.equocoin.handlesolidity.SolidityHandler;
import com.equocoin.model.Config;
import com.equocoin.model.RegisterInfo;
import com.equocoin.model.TokenInfo;
import com.equocoin.model.TransactionHistory;
import com.equocoin.repository.ConfigInfoRepository;
import com.equocoin.repository.RegisterInfoRepository;
import com.equocoin.repository.TokenInfoRepository;
import com.equocoin.repository.TransactionInfoRepository;
import com.equocoin.service.TokenUserService;
import com.equocoin.soliditytojava.Equacoins;
import com.equocoin.utils.EncryptDecrypt;
import com.equocoin.utils.EquocoinUtils;
import com.equocoin.utils.FcmUtils;
import com.equocoin.utils.SessionCollector;

@Service
public class TokenUserServiceImpl implements TokenUserService {

	private static final Logger LOG = LoggerFactory.getLogger(TokenUserServiceImpl.class);
	
	//private final Web3j web3j = Web3j.build(new HttpService());
	 //private final Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io"));
	
	 private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/"));


	@Autowired
	private Environment env;

	@Autowired
	private SolidityHandler solidityHandler;

	@Autowired
	private TokenInfoRepository tokenInfoRepository;

	@SuppressWarnings("unused")
	@Autowired
	private HttpSession session;

	@Autowired
	private RegisterInfoRepository registerInfoRepository;

	@Autowired
	private SessionCollector sessionCollector;

	@Autowired
	private ConfigInfoRepository configInfoRepository;

	@Autowired
	private EquocoinUtils equocoinUtils;

	@Autowired
	private TransactionInfoRepository transactionInfoRepository;
	
	private TransactionReceipt transactionReceipt;
	
	@Autowired
	private FcmUtils fcmUtils;
	
	
	@Override
	public boolean TokenCreate(TokenDTO tokenDTO) {
//		try {
//			String status = solidityHandler.tokenCreation(tokenDTO);
//			@SuppressWarnings("unused")
//			Integer isEmailExist = 0;
//			if (status != null) {
//				System.out.println("Token Address" + status);
//				return true;
//			}
//
//		} catch (IOException | CipherException | InterruptedException | ExecutionException e) {
//			e.printStackTrace();
//		}

		return false;
	}

	@Override
	public boolean saveTokenGenerationInfo(TokenDTO tokenDTO) {
		TokenInfo tokenInfo = new TokenInfo();

		

		tokenInfo.setInitialValue(tokenDTO.getInitialValue());
		tokenInfo.setCoinName(tokenDTO.getCoinName());
		tokenInfo.setCoinSymbol(tokenDTO.getCoinSymbol());
		tokenInfo.setDecimalUnits(Double.parseDouble(tokenDTO.getDecimalUnits().toString()));
		tokenInfo.setCentralAdmin(tokenDTO.getCentralAdmin());
		tokenInfo.setTokenAddress(tokenDTO.getTokenAddress());
		tokenInfo.setCreatedDate(new Date());
		tokenInfo.setVersion(new Date());
		tokenInfoRepository.save(tokenInfo);
		return true;
	}

	@Override
	public boolean validateCentraladmin(TokenDTO tokenDTO) {
		String address = env.getProperty("main.address");
		if (address != null && StringUtils.isNotBlank(address)) {
			tokenDTO.setCentralAdmin(address);
			return true;
		}
		return false;
	}

	@Override
	public boolean checkMainbalance(TokenDTO tokenDTO) {

		EthGetBalance ethGetBalance;
		try {
			DecimalFormat df = new DecimalFormat("#.########");
			ethGetBalance = web3j.ethGetBalance(tokenDTO.getCentralAdmin(), DefaultBlockParameterName.LATEST)
					.sendAsync().get();

			BigDecimal wei = new BigDecimal(ethGetBalance.getBalance());
			
			// BigInteger wei = ethGetBalance.getBalance();
			BigDecimal amountCheck = Convert.fromWei(wei, Convert.Unit.ETHER);

			tokenDTO.setMainBalance(new BigDecimal(df.format(amountCheck.doubleValue())));

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return true;
	}

	/*
	 * @Override public boolean transferAmount(TokenDTO tokenDTO) { try {
	 * boolean status = solidityHandler.transferAmount(tokenDTO);
	 * 
	 * if (status) { System.out.println("Token Address" + status); return true;
	 * }
	 * 
	 * } catch (IOException | CipherException | InterruptedException |
	 * ExecutionException e) { e.printStackTrace(); }
	 * 
	 * return false;
	 * 
	 * }
	 */
	@Override
	public boolean TransferToken(TokenDTO tokenDTO) {
//		try {
//			boolean status = solidityHandler.TransferToken(tokenDTO);
//
//			if (status) {
//
//				return true;
//			}
//
//		} catch (IOException | CipherException | InterruptedException | ExecutionException e) {
//			e.printStackTrace();
//		}

		return false;

	}

	@Override
	public boolean TransferAdminship(TokenDTO tokenDTO) {
//		try {
//			boolean status = solidityHandler.TransferAdminship(tokenDTO);
//
//			if (status) {
//				System.out.println("Token Address" + status);
//				return true;
//			}
//
//		} catch (IOException | CipherException | InterruptedException | ExecutionException e) {
//			e.printStackTrace();
//		}

		return false;
	}

	/*
	 * @Override public boolean getTokenBalance(TokenDTO tokenDTO) { Credentials
	 * credentials; try { credentials = WalletUtils.loadCredentials("User0505",
	 * "E://Ethereum//private-network//keystore//UTC--2017-11-21T03-55-16.644449300Z--a61cda3cbffbb4e3e4a4af248ee215d56a7b2cb9"
	 * ); AssetToken assetToken = AssetToken.load(tokenDTO.getTokenAddress(),
	 * web3j, credentials, BigInteger.valueOf(3000000),
	 * BigInteger.valueOf(3000000));
	 * 
	 * Address address = new Address(tokenDTO.getWalletAddress()); try {
	 * BigInteger balance = assetToken.balanceOf(address).get().getValue();
	 * tokenDTO.setTokenBalance(balance); return true; } catch
	 * (InterruptedException e) { e.printStackTrace(); } catch
	 * (ExecutionException e) { e.printStackTrace(); } } catch (IOException e) {
	 * e.printStackTrace(); } catch (CipherException e) { e.printStackTrace(); }
	 * 
	 * return false; }
	 */

	@Override
	public List<TokenDTO> listToken() {
//		try {
//			return solidityHandler.listTokens();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return null;

	}

	@Override
	public boolean validAmount(TokenDTO tokenDTO, HttpServletRequest request) {

		tokenDTO.setCentralAdmin(tokenDTO.getFromAddress());
		boolean status = checkMainbalance(tokenDTO);

		if (status) {

			BigDecimal balance = tokenDTO.getMainBalance();
			BigDecimal equaCoin = tokenDTO.getTransferAmount();

		

			int res = balance.compareTo(equaCoin);
			if (res == 1) {
				return true;

			}

		}

		return false;
	}

	@Override
	public boolean isTokenCancel(TokenDTO tokenDTO) throws Exception {

		boolean isSuccess = solidityHandler.tokenCancel(tokenDTO);
		if (isSuccess) {
			return true;
		}
		return false;

	}


	@Override
	public boolean getUserTokenBalance(TokenDTO tokenDTO) throws Exception {
        TokenDTO tokenDTO2=new TokenDTO();
		Credentials credentials;
		try {

			HttpSession session = sessionCollector.find(tokenDTO.getSessionId());
			Config config = configInfoRepository.findConfigByConfigKey("walletfile");
			RegisterInfo equocoinUserInfos = registerInfoRepository
					.findEquocoinUserInfoByEmailId(session.getAttribute("emailId"));
			if (equocoinUserInfos != null && config != null) {

				String decryptWalletAddress = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletAddress());
				String decryptWalletPassword = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletPassword());

				tokenDTO2.setToAddress(config.getConfigValue() + "//" + decryptWalletAddress);

				String walletAddress = equocoinUtils.getWalletAddress(config.getConfigValue(), decryptWalletAddress);
				if (walletAddress == null) {
					return false;
				}
				tokenDTO2.setWalletAddress(walletAddress);
				tokenDTO2.setWalletPassword(decryptWalletPassword);

				credentials = WalletUtils.loadCredentials(tokenDTO2.getWalletPassword(),
						new File(tokenDTO2.getToAddress()));
				Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,
						Contract.GAS_PRICE, Contract.GAS_LIMIT);

				BigInteger balance = assetToken.balanceOf(tokenDTO2.getWalletAddress()).send();
				tokenDTO.setTokenBalance(balance.doubleValue()/10000);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CipherException e) {
			e.printStackTrace();
		}

		return false;
	}

	@SuppressWarnings("static-access")
	public boolean getwalletBalancePrams(TokenDTO tokenDTO) throws Exception {
		HttpSession session = sessionCollector.find(tokenDTO.getSessionId());
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		RegisterInfo equocoinUserInfos = registerInfoRepository
				.findEquocoinUserInfoByEmailId(session.getAttribute("emailId"));
		String walletAddress;
		try {

			if (equocoinUserInfos != null && config != null) {
				String decryptWalletAddress = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletAddress());
				walletAddress = equocoinUtils.getWalletAddress(config.getConfigValue(), decryptWalletAddress);
				if (walletAddress == null) {
					return false;
				}
				tokenDTO.setCentralAdmin(walletAddress);
				return true;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

	@SuppressWarnings("static-access")
	@Override
	public boolean getTransferAccountParams(TokenDTO tokenDTO) throws Exception {
		HttpSession session = sessionCollector.find(tokenDTO.getSessionId());
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		RegisterInfo equocoinUserInfos = registerInfoRepository
				.findEquocoinUserInfoByEmailId(session.getAttribute("emailId"));
		String walletAddress;
		try {

			if (equocoinUserInfos != null && config != null) {
				String decryptWalletAddress = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletAddress());
				tokenDTO.setWalletAddress(decryptWalletAddress);
				walletAddress = equocoinUtils.getWalletAddress(config.getConfigValue(), decryptWalletAddress);
				tokenDTO.setRegisterInfo(equocoinUserInfos);
				if (walletAddress == null) {
					return false;
				}
				tokenDTO.setFromAddress(walletAddress);
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean TransferCoin(TokenDTO tokenDTO) throws Exception {
		boolean status = solidityHandler.manualTokenTransfer(tokenDTO);

		if (status) {

			return true;
		}

		return false;
	}

	@SuppressWarnings("static-access")
	public boolean isAddressExist(TokenDTO tokenDTO) throws Exception {
		HttpSession session = sessionCollector.find(tokenDTO.getSessionId());
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		RegisterInfo equocoinUserInfos = registerInfoRepository
				.findEquocoinUserInfoByEmailId(session.getAttribute("emailId"));
		try {

			if (equocoinUserInfos != null && config != null) {
				String decryptWalletAddress = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletAddress());
				String decryptWalletPassword = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletPassword());
				if (decryptWalletAddress == null && decryptWalletPassword == null) {
					return false;
				}
				tokenDTO.setWalletAddress(config.getConfigValue() + decryptWalletAddress);
				tokenDTO.setWalletPassword(decryptWalletPassword);
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return false;

	}

	public String getRefund(TokenDTO tokenDTO) throws Exception {
//
//		String isSuccess = solidityHandler.getRefund(tokenDTO);
//
//		return isSuccess;
		return null;
	}

	@Override
	public boolean sendToken(TokenDTO tokenDTO) throws Exception {
		boolean isSuccess = solidityHandler.purchaseToken(tokenDTO);
		if (isSuccess) {
			return true;
		}
		return false;

	}

	@Override
	public boolean validateMintTokenParams(TokenDTO tokenDTO) {
	if (tokenDTO.getMintedAmount() != null && StringUtils.isNotBlank(tokenDTO.getMintedAmount().toString()))
	{
				
			return true;
		}

		return false;
	}
	@Override
	public String isValidTokenBalForTokenTransfer(TokenDTO tokenDTO) throws Exception {
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");

		Credentials credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),
				config.getConfigValue() + "//" + tokenDTO.getWalletAddress());
		Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,
				Contract.GAS_PRICE, Contract.GAS_LIMIT);
		List<TransactionHistory> txHistory1 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("EQUA", "Pending",
						 tokenDTO.getToAddress(), tokenDTO.getFromAddress());
		List<TransactionHistory> txHistory2 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("EQUA", "Pending", tokenDTO.getFromAddress(),
						tokenDTO.getToAddress());
		List<TransactionHistory> txHistory3 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("ETH", "Pending", tokenDTO.getToAddress(),
						tokenDTO.getFromAddress());
		List<TransactionHistory> txHistory4 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("ETH", "Pending", tokenDTO.getFromAddress(),
						tokenDTO.getToAddress());
		if (txHistory1.size() > 0 || txHistory2.size()  > 0 || txHistory3.size()  > 0 || txHistory4.size()  > 0) {
			return "You won't be able to send any ETH or other tokens until the Status transaction gets confirmed";
		}
		BigInteger crowdsaleBal = assetToken.balanceOf(tokenDTO.getFromAddress()).send();

		if (BigInteger.ZERO.equals(crowdsaleBal)) {
			return "Token sold out";
		}
		Double sendTokenBalance = crowdsaleBal.doubleValue()/10000;
		Double k = tokenDTO.getRequestToken();
		int count = sendTokenBalance.compareTo(k);

		if (count == -1) {
			return "Only  " + sendTokenBalance + "  token available . if you want means try that available range";
		}

		return "success";
		
	}
	
	@Override
	public boolean sendEquacoin(TokenDTO tokenDTO) throws Exception {
		transactionReceipt = new TransactionReceipt();
		TransactionHistory transactionHistory = new TransactionHistory();
		CompletableFuture.supplyAsync(() -> {
			
			Credentials credentials;
			try {
				transactionHistory.setFromAddress(tokenDTO.getFromAddress());
				transactionHistory.setToAddress(tokenDTO.getToAddress());
				transactionHistory.setAmount(new BigDecimal(0).doubleValue());
				transactionHistory.setToken(tokenDTO.getRequestToken());
				transactionHistory.setCreatedDate(new Date());
				transactionHistory.setPaymentMode(env.getProperty("admin.payment"));
				transactionHistory.setStatus(env.getProperty("status.pending"));
				transactionHistory.setRegisterInfo(tokenDTO.getRegisterInfo());
				transactionInfoRepository.save(transactionHistory);
				Config config = configInfoRepository.findConfigByConfigKey("walletfile");

				credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),
						config.getConfigValue() + "//" + tokenDTO.getWalletAddress());
				
						
				Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,
						Contract.GAS_PRICE, Contract.GAS_LIMIT);
				
					BigInteger amount = BigDecimal.valueOf(tokenDTO.getRequestToken() * 10000).toBigInteger();
				transactionReceipt = assetToken
						.transfer(tokenDTO.getToAddress(), amount).send();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "call send token";
		
	}).thenAccept(product -> {
		if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
	  //if(transactionReceipt !=null){
			if(transactionHistory!= null)
			{
				transactionHistory.setStatus(env.getProperty("status.success"));
				transactionInfoRepository.save(transactionHistory);	
			}

		}
		else if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed")))  {
		//else if(transactionReceipt ==null){
		if(transactionHistory!= null)			{
			transactionHistory.setStatus(env.getProperty("status.failed"));
			transactionInfoRepository.save(transactionHistory);
			}
			
		}

	});

		return true;
		}
	
	@Override
	public boolean validEther(TokenDTO tokenDTO) {
		tokenDTO.setCentralAdmin(tokenDTO.getFromAddress());
		boolean status = checkMainbalance(tokenDTO);
		List<TransactionHistory> txHistory1 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("EQUA", "Pending",
						tokenDTO.getFromAddress(), tokenDTO.getToAddress());
		List<TransactionHistory> txHistory2 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("EQUA", "Pending", tokenDTO.getToAddress(),
						tokenDTO.getFromAddress());
		List<TransactionHistory> txHistory3 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("ETH", "Pending", tokenDTO.getFromAddress(),
						tokenDTO.getToAddress());
		List<TransactionHistory> txHistory4 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("ETH", "Pending", tokenDTO.getToAddress(),
						tokenDTO.getFromAddress());
		if (txHistory1.size() > 0 || txHistory2.size()  > 0 || txHistory3.size()  > 0 || txHistory4.size()  > 0) {
			tokenDTO.setMessage("You won't be able to send any ETH or other tokens until the Status transaction gets confirmed");
			return false;
		}
		
		if (status) {

			BigDecimal balance = tokenDTO.getMainBalance();
			BigDecimal equaCoin = tokenDTO.getAmount();

		
			int res = balance.compareTo(equaCoin);
			if (res == -1 || res == 0) {
				tokenDTO.setMessage("Insufficient ETH balance  ");
				return false;
				

			}
		
	}
		return status;
	}
	
	
	@Override
	public boolean amountTransfer(TokenDTO tokenDTO) throws Exception {

		
		transactionReceipt = new TransactionReceipt();
		TransactionHistory transactionHistory = new TransactionHistory();
		CompletableFuture.supplyAsync(() -> {
		try{
		Credentials credentials;
		transactionHistory.setFromAddress(tokenDTO.getFromAddress());
		transactionHistory.setToAddress(tokenDTO.getToAddress());
		transactionHistory.setAmount(Double.valueOf(tokenDTO.getAmount().doubleValue()));
		transactionHistory.setCreatedDate(new Date());
		transactionHistory.setToken(new Double("0.0"));
		transactionHistory.setPaymentMode(env.getProperty("user.payment"));
		transactionHistory.setStatus(env.getProperty("status.pending"));
		transactionHistory.setRegisterInfo(tokenDTO.getRegisterInfo());
		transactionInfoRepository.save(transactionHistory);
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");

		credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),
				config.getConfigValue() + "//" + tokenDTO.getWalletAddress());
		if (credentials != null) {
			 transactionReceipt = Transfer
					.sendFunds(web3j, credentials, tokenDTO.getToAddress(), tokenDTO.getAmount(), Convert.Unit.ETHER)
					.send();
		}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "call send ether";
	
		}).thenAccept(product -> {
		if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
		//if(transactionReceipt !=null){
			if(transactionHistory!= null)
			{
				transactionHistory.setStatus(env.getProperty("status.success"));
				transactionInfoRepository.save(transactionHistory);	
			}

		}
	else if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed")))  {
		//else if(transactionReceipt ==null){
		if(transactionHistory!= null)			{
			transactionHistory.setStatus(env.getProperty("status.failed"));
			transactionInfoRepository.save(transactionHistory);
			}
			
		}

	});

		return true;
	}	
	
	@Override
	public void sendETHCoinPushNotificationFrom(RegisterInfo registerInfo,TokenDTO tokenDTO) {
		
		String message="Successfully Sent Your "+tokenDTO.getPaymentMode()+" "+tokenDTO.getRequestToken()+" Coin To "+tokenDTO.getReceiver();
		String appId=registerInfo.getAppId();
		String server_key=env.getProperty("server_key");
		JSONObject infoJson = new JSONObject();
		
		infoJson.put("title", "EQUASendCoin Notification");

		infoJson.put("body", message);
		
		String deviceType="notification";
		if(registerInfo.getMobileType().equals("ios")) {
			fcmUtils.send_FCM_Notification(appId, server_key, message,infoJson,deviceType);
		}else {
			fcmUtils.send_FCM_Notification(appId, server_key, message,infoJson,deviceType);
			deviceType="data";
			fcmUtils.send_FCM_Notification(appId, server_key, message,infoJson,deviceType);
		}
	}

	@Override
	public void sendETHCoinPushNotificationTo(RegisterInfo registerInfo, TokenDTO tokenDTO) {
		String message="Received "+tokenDTO.getPaymentMode()+" "+tokenDTO.getTransferAmount()+" from "+tokenDTO.getSender();
		String appId=registerInfo.getAppId();
		String server_key=env.getProperty("server_key");
		JSONObject infoJson = new JSONObject();

		infoJson.put("title", "EQUASendCoin Notification");

		infoJson.put("body", message);
		String deviceType="notification";
		if(registerInfo.getMobileType().equals("ios")) {
			fcmUtils.send_FCM_Notification(appId, server_key, message,infoJson,deviceType);
			}else {
				fcmUtils.send_FCM_Notification(appId, server_key, message,infoJson,deviceType);
			    deviceType="data";
			    fcmUtils.send_FCM_Notification(appId, server_key, message,infoJson,deviceType);
		}
		
		
	}

	@Override
	public void requestcoinPushNotification(TokenDTO tokenDTO) throws Exception {
		

		RegisterInfo userInfoModel = registerInfoRepository.findEquocoinUserInfoByEmailId(tokenDTO.getEmailId());

		RegisterInfo userInfoModel1= registerInfoRepository.findEquacoinUserInfoByEtherWalletAddress(EncryptDecrypt.encrypt(tokenDTO.getToAddress()));
		String appId= userInfoModel1.getAppId();
		if(appId.length()>=1) {
		String server_key=env.getProperty("server_key");
		
		String message = userInfoModel.getEmailId()+" Has Requested "+tokenDTO.getRequestCoin()+" "+tokenDTO.getPaymentMode()+" From You";
		JSONObject infoJson = new JSONObject();

		infoJson.put("title", "EQUARequestCoin Notification");

		infoJson.put("body", message);
		
		infoJson.put("id",tokenDTO.getId());
		String deviceType="notification";
		if(userInfoModel1.getMobileType().equals("ios")) {
			fcmUtils.send_FCM_Notification(appId, server_key, message,infoJson,deviceType);
		}else {
			fcmUtils.send_FCM_Notification(appId, server_key, message,infoJson,deviceType);
			deviceType="data";
			fcmUtils.send_FCM_Notification(appId, server_key, message,infoJson,deviceType);
		}
		}
	}

		
}
