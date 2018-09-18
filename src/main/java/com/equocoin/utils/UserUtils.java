package com.equocoin.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang.StringUtils;
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
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import com.equocoin.dto.LoginDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.model.Config;
import com.equocoin.model.RegisterInfo;
import com.equocoin.model.TransactionHistory;
import com.equocoin.model.UserRequestCoinInfo;
import com.equocoin.repository.ConfigInfoRepository;
import com.equocoin.repository.RegisterInfoRepository;
import com.equocoin.repository.TransactionInfoRepository;
import com.equocoin.repository.UserRequestCoinRepository;
import com.equocoin.service.TokenUserService;
import com.equocoin.service.impl.TokenUserServiceImpl;
import com.equocoin.soliditytojava.Equacoins;



@Service
public class UserUtils {
	
    @Autowired
	private RegisterInfoRepository registerInfoRepository;
    
    @Autowired
    private Environment env;
    
    @Autowired
    private EquocoinUtils equocoinUtils;
    
    @Autowired
    private ConfigInfoRepository configInfoRepository;
    
    @Autowired
    private TransactionInfoRepository transactionInfoRepository;
    
    @Autowired
    private TokenUserServiceImpl tokenUserServiceImpl;
    
    @Autowired
    private UserRequestCoinRepository userRequestCoinRepository;
    
	@Autowired
	private TokenUserService tokenUserService;
	
	@Autowired   
	private CurrentValueUtils currentValueUtils;
	
	//private final Web3j web3j = Web3j.build(new HttpService());
    
    //private final Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/"));
    
	private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io"));
    
    private BigInteger gasPrice = BigInteger.valueOf(3000000);
    
	private BigInteger gasLimit = BigInteger.valueOf(3000000);
	
	private TransactionReceipt transactionReceipt;
	
	private static final Logger LOG = LoggerFactory.getLogger(UserUtils.class);
    
	
	public boolean validPaymentModeUser(TokenDTO tokenDTO) {
			if(tokenDTO.getPaymentMode().equals(env.getProperty("user.payment"))|| tokenDTO.getPaymentMode().equals(env.getProperty("admin.payment"))) {
				return true;
			}
		return false;
	}

	public boolean validCoin(TokenDTO tokenDTO) throws Exception {

		Config configInfo = configInfoRepository.findConfigByConfigKey("walletfile");
		RegisterInfo equacoinUserInfo = registerInfoRepository.findEquocoinUserInfoByEmailId(tokenDTO.getEmailId());
		if (equacoinUserInfo != null && configInfo != null) {
			String decryptedWalletAddress = EncryptDecrypt.decrypt(equacoinUserInfo.getWalletAddress());
			String etherWalletAddress = equocoinUtils.getWalletAddress(configInfo.getConfigValue(),
					decryptedWalletAddress);
			tokenDTO.setWalletAddress(etherWalletAddress);
			List<TransactionHistory> txHistory1 = transactionInfoRepository
					.findByPaymentModeAndStatusAndFromAddressAndToAddress("EQUA", "Pending",
							tokenDTO.getWalletAddress(), tokenDTO.getToAddress());
			List<TransactionHistory> txHistory2 = transactionInfoRepository
					.findByPaymentModeAndStatusAndFromAddressAndToAddress("EQUA", "Pending", tokenDTO.getToAddress(),
							tokenDTO.getWalletAddress());
			List<TransactionHistory> txHistory3 = transactionInfoRepository
					.findByPaymentModeAndStatusAndFromAddressAndToAddress("ETH", "Pending", tokenDTO.getWalletAddress(),
							tokenDTO.getToAddress());
			List<TransactionHistory> txHistory4 = transactionInfoRepository
					.findByPaymentModeAndStatusAndFromAddressAndToAddress("ETH", "Pending", tokenDTO.getToAddress(),
							tokenDTO.getWalletAddress());
			if (txHistory1.size() > 0 || txHistory2.size()  > 0 || txHistory3.size()  > 0 || txHistory4.size()  > 0) {
				tokenDTO.setMessage("You won't be able to send any ETH or other tokens until the Status transaction gets confirmed");
				return false;
			}
			
			
			if (tokenDTO.getPaymentMode().equals(env.getProperty("user.payment"))) {

				tokenDTO.setWalletAddress(etherWalletAddress);
				tokenDTO.setCentralAdmin(tokenDTO.getWalletAddress());
				EthGetBalance ethGetBalance;
				ethGetBalance = web3j.ethGetBalance(tokenDTO.getCentralAdmin(), DefaultBlockParameterName.LATEST)
						.sendAsync().get();
				BigInteger wei = ethGetBalance.getBalance();
				BigDecimal amountCheck = Convert.fromWei(wei.toString(), Convert.Unit.ETHER);

				if (tokenDTO.getRequestToken() < amountCheck.doubleValue()) {
					return true;
				}
			}

			if (tokenDTO.getPaymentMode().equals(env.getProperty("admin.payment"))) {

				Credentials credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),

						configInfo.getConfigValue() + decryptedWalletAddress);

				Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials, gasPrice,
						gasLimit);
				BigInteger b = assetToken.balanceOf(etherWalletAddress).send();
				
				if (b.doubleValue()/10000 > tokenDTO.getRequestToken()) {
					return true;
				}
			}

		}
		return false;
	}

	public boolean isSendAmountToUser(TokenDTO tokenDTO) throws Exception {
		Config configInfo = configInfoRepository.findConfigByConfigKey("walletfile");
		
		TransactionHistory transactionHistory=new TransactionHistory();
	
		
		if (tokenDTO.getPaymentMode().equals(env.getProperty("user.payment"))) {
			RegisterInfo equacoinUserInfos = registerInfoRepository.findEquacoinUserInfoByEtherWalletAddress(EncryptDecrypt.encrypt(tokenDTO.getToAddress()));
			RegisterInfo equacoinUserInfo = registerInfoRepository.findEquocoinUserInfoByEmailId(tokenDTO.getEmailId());
			String decryptedWalletAddress = EncryptDecrypt.decrypt(equacoinUserInfo.getWalletAddress());
			String etherWalletAddress = equocoinUtils.getWalletAddress(configInfo.getConfigValue(), decryptedWalletAddress);
			transactionReceipt = new TransactionReceipt();
			
			CompletableFuture.supplyAsync(() -> {
				   Credentials credentials;
				   try {
					   transactionHistory.setFromAddress(etherWalletAddress);
						transactionHistory.setToAddress(tokenDTO.getToAddress());
						transactionHistory.setAmount(tokenDTO.getRequestToken());
						transactionHistory.setToken(new Double("0.0"));
						transactionHistory.setPaymentMode(env.getProperty("user.payment"));
						transactionHistory.setCreatedDate(new Date());
						transactionHistory.setSenderId(equacoinUserInfo.getId());
						transactionHistory.setReceiverId(equacoinUserInfos.getId());
						
						transactionHistory.setStatus("Pending");
						transactionHistory.setRegisterInfo(equacoinUserInfo);
						transactionInfoRepository.save(transactionHistory);
				    if (transactionHistory != null) {
				     credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),
				    		 configInfo.getConfigValue()+ decryptedWalletAddress);
				     transactionReceipt = Transfer
								.sendFunds(web3j, credentials, tokenDTO.getToAddress(), new BigDecimal(tokenDTO.getRequestToken()), Convert.Unit.ETHER)
								.send();
				    }
				   } catch (Exception e) {

				    e.printStackTrace();
				   }

				   return "call blackchain";
				  }).thenAccept(product -> {
					  
					 // if (transactionReceipt != null) {
					   if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
				    if (transactionHistory != null) {
				    	transactionHistory.setStatus(env.getProperty("status.success"));
				     transactionInfoRepository.save(transactionHistory);
				     tokenDTO.setReceiver(equacoinUserInfos.getEmailId());
				     tokenDTO.setSender(equacoinUserInfo.getEmailId());
				     tokenDTO.setTransferAmounts(transactionHistory.getAmount());
				     tokenUserServiceImpl.sendETHCoinPushNotificationFrom(equacoinUserInfo,tokenDTO);
					 tokenUserServiceImpl.sendETHCoinPushNotificationTo(equacoinUserInfos,tokenDTO);
				    }
				    
				   } //else if (transactionReceipt !=null) {
					  else if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed"))) {
					   if (transactionHistory != null) {
						   transactionHistory.setStatus(env.getProperty("status.failed"));
				    transactionInfoRepository.save(transactionHistory);
					   }
				   }
				  });
				  return true;

		}
			

	if (tokenDTO.getPaymentMode().equals(env.getProperty("admin.payment"))) {
		
	

		RegisterInfo userInfoModel = registerInfoRepository.findEquocoinUserInfoByEmailId(tokenDTO.getEmailId());
		RegisterInfo userInfoModel1 = registerInfoRepository.findEquacoinUserInfoByEtherWalletAddress(EncryptDecrypt.encrypt(tokenDTO.getToAddress()));

		String decryptedWalletAddress = EncryptDecrypt.decrypt(userInfoModel.getWalletAddress());
		String etherWalletAddress = equocoinUtils.getWalletAddress(configInfo.getConfigValue(), decryptedWalletAddress);
		transactionReceipt = new TransactionReceipt();
		
		CompletableFuture.supplyAsync(() -> {
			   Credentials credentials;
			   try {
				   transactionHistory.setFromAddress(etherWalletAddress);
					transactionHistory.setToAddress(tokenDTO.getToAddress());
					transactionHistory.setAmount(new Double("0.0"));
					transactionHistory.setToken(tokenDTO.getRequestToken());
					transactionHistory.setPaymentMode(env.getProperty("admin.payment"));
					transactionHistory.setCreatedDate(new Date());
					transactionHistory.setSenderId(userInfoModel.getId());
					transactionHistory.setReceiverId(userInfoModel1.getId());
					transactionHistory.setRegisterInfo(userInfoModel);
					transactionHistory.setStatus("Pending");
				   	transactionInfoRepository.save(transactionHistory);
			    if (transactionHistory != null) {
			     credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),
			    		 configInfo.getConfigValue() + decryptedWalletAddress);
			     Equacoins assetToken = null;
					if(tokenDTO.getGasPrice()==1) {
					assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials, Contract.GAS_PRICE,
							Contract.GAS_LIMIT);
					}
					if(tokenDTO.getGasPrice()==2) {
						assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,Contract.GAS_PRICE,
								Contract.GAS_LIMIT);
						}
					if(tokenDTO.getGasPrice()==3) {
						assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials, Contract.GAS_PRICE,
								Contract.GAS_LIMIT);
						}
					
					BigInteger token=new BigDecimal(tokenDTO.getRequestToken()*10000).toBigInteger();
					transactionReceipt = assetToken.transfer(tokenDTO.getToAddress(),
							token).send();
			   
			    }
			   } catch (Exception e) {

			    e.printStackTrace();
			   }

			   return true;
			  }).thenAccept(product -> {
				
				  //if (transactionReceipt != null) {
			   if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
			    if (transactionHistory != null) {
			    	transactionHistory.setStatus(env.getProperty("status.success"));
			     transactionInfoRepository.save(transactionHistory);
			     tokenDTO.setReceiver(userInfoModel1.getEmailId());
			     tokenDTO.setSender(userInfoModel.getEmailId());
			     tokenDTO.setTransferAmounts(transactionHistory.getAmount().doubleValue());
			     tokenUserServiceImpl.sendETHCoinPushNotificationFrom(userInfoModel,tokenDTO);
			     tokenUserServiceImpl.sendETHCoinPushNotificationTo(userInfoModel1,tokenDTO);
			    }
			   } // else if (transactionReceipt !=null) {
				  else if(transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed"))) {
				   if (transactionHistory != null) {
				   transactionHistory.setStatus(env.getProperty("status.failed"));
			    transactionInfoRepository.save(transactionHistory);
			}
			   }
			  });
	
		
	}
	return true;

	}
	
	public boolean isTransferAmtZeroUser(TokenDTO tokenDTO) {
		int length =String.valueOf(tokenDTO.getRequestToken()/10000).length();
		if (tokenDTO.getRequestToken()/10000 > 0 && length <= 10) {
			return true;
		}
		return false;
	}

	public boolean validateTokenVal(TokenDTO tokenDTO)
	{
		if(new BigDecimal(tokenDTO.getRequestToken()).compareTo(new BigDecimal(0)) == 1)
		{
			return true;
		}
		return false;		
	}
	
	public boolean saveRequestInfo(TokenDTO tokenDTO) throws Exception {
		Config configInfo=configInfoRepository.findConfigByConfigKey("walletfile");
		RegisterInfo userInfoModel = registerInfoRepository.findEquocoinUserInfoByEmailId(tokenDTO.getEmailId());
		RegisterInfo userInfoModel1 = registerInfoRepository.findEquacoinUserInfoByEtherWalletAddress(EncryptDecrypt.encrypt(tokenDTO.getToAddress()));
		if(tokenDTO.getPaymentMode().equals(env.getProperty("user.payment"))||tokenDTO.getPaymentMode().equals(env.getProperty("admin.payment"))){
		
		if(userInfoModel!=null) {
			String walletAddress = EncryptDecrypt.decrypt(userInfoModel.getWalletAddress());
			String walletAddress1 = equocoinUtils.getWalletAddress(configInfo.getConfigValue(), walletAddress);
			UserRequestCoinInfo userRequestCoinInfo= new UserRequestCoinInfo();
			userRequestCoinInfo.setFromAddress(walletAddress1);
			userRequestCoinInfo.setToAddress(tokenDTO.getToAddress());
			userRequestCoinInfo.setPaymentMode(tokenDTO.getPaymentMode());
			userRequestCoinInfo.setRequestCoin(tokenDTO.getRequestToken());
			userRequestCoinInfo.setTranscationType(0);
			userRequestCoinInfo.setCreatedDate(new Date());
			userRequestCoinInfo.setSenderName(userInfoModel.getEmailId());
			userRequestCoinInfo.setReceiveraName(userInfoModel1.getEmailId());
			userRequestCoinRepository.save(userRequestCoinInfo);
			tokenDTO.setId(userRequestCoinInfo.getId());
			tokenDTO.setRequestToken(userRequestCoinInfo.getRequestCoin());
			return true;
		}
		}
		return false;
	}
	
	public boolean validateSendRequestCoinParam(TokenDTO tokenDTO) {
		if (tokenDTO.getId() != null &&StringUtils.isNotBlank(tokenDTO.getId().toString())
				&&tokenDTO.getRequestToken() != null && !StringUtils.isEmpty(tokenDTO.getRequestToken().toString())) {
			return true;
		}
		return false;
	}
	
	public boolean validateId(TokenDTO tokenDTO) {
		UserRequestCoinInfo  userRequestCoinInfo= userRequestCoinRepository.findOne(tokenDTO.getId());
		if(userRequestCoinInfo!=null) {
			return true;
		}
		return false;
	}
	
	public boolean validPaymentMode(TokenDTO tokenDTO) {
		UserRequestCoinInfo  userRequestCoinInfo= userRequestCoinRepository.findOne(tokenDTO.getId());
		if(userRequestCoinInfo.getPaymentMode()!=null) {
			if(userRequestCoinInfo.getPaymentMode().equals(env.getProperty("user.payment"))|| userRequestCoinInfo.getPaymentMode().equals(env.getProperty("admin.payment"))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean validRequestCoin(TokenDTO tokenDTO) throws Exception {
		
		UserRequestCoinInfo  userRequestCoinInfo= userRequestCoinRepository.findOne(tokenDTO.getId());
		RegisterInfo userInfoModel = registerInfoRepository.findEquocoinUserInfoByEmailId(tokenDTO.getEmailId());
		String decryptedWalletAddress = EncryptDecrypt.decrypt(userInfoModel.getWalletAddress());
		Config configInfo=configInfoRepository.findConfigByConfigKey("walletfile");
		String etherWalletAddress = equocoinUtils.getWalletAddress(configInfo.getConfigValue(), decryptedWalletAddress);
		tokenDTO.setWalletAddress(etherWalletAddress);
		tokenDTO.setToAddress(userRequestCoinInfo.getFromAddress());
		if(userRequestCoinInfo.getTranscationType() == 1){
			tokenDTO.setMessage("Token has been sent already for this request!");
			return false;
			
		}
		List<TransactionHistory> txHistory1 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("EQUA", "Pending",
						tokenDTO.getWalletAddress(), tokenDTO.getToAddress());
		List<TransactionHistory> txHistory2 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("EQUA", "Pending", tokenDTO.getToAddress(),
						tokenDTO.getWalletAddress());
		List<TransactionHistory> txHistory3 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("ETH", "Pending", tokenDTO.getWalletAddress(),
						tokenDTO.getToAddress());
		List<TransactionHistory> txHistory4 = transactionInfoRepository
				.findByPaymentModeAndStatusAndFromAddressAndToAddress("ETH", "Pending", tokenDTO.getToAddress(),
						tokenDTO.getWalletAddress());
		if (txHistory1.size() > 0 || txHistory2.size()  > 0 || txHistory3.size()  > 0 || txHistory4.size()  > 0) {
			tokenDTO.setMessage("You won't be able to send any ETH or other tokens until the Status transaction gets confirmed");
			return false;
		}
		
		if (userRequestCoinInfo.getPaymentMode().equals(env.getProperty("user.payment"))) {
				tokenDTO.setWalletAddress(etherWalletAddress);
			tokenDTO.setCentralAdmin(tokenDTO.getWalletAddress());
					EthGetBalance ethGetBalance;
			ethGetBalance = web3j.ethGetBalance(tokenDTO.getCentralAdmin(), DefaultBlockParameterName.LATEST).sendAsync()
					.get();
			BigInteger wei = ethGetBalance.getBalance();
			BigDecimal amountCheck = Convert.fromWei(wei.toString(), Convert.Unit.ETHER);
			if(tokenDTO.getRequestToken()< amountCheck.doubleValue()){
			return true;
			}
		}
	
		if (userRequestCoinInfo.getPaymentMode().equals(env.getProperty("admin.payment"))) {

			Credentials credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),
					configInfo.getConfigValue() + decryptedWalletAddress);
			Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials, Contract.GAS_PRICE,
					Contract.GAS_LIMIT);
            BigInteger b= assetToken.balanceOf(etherWalletAddress).send();
         	if(b.doubleValue()/10000>tokenDTO.getRequestToken()) {
         		
         		return true;
			}
		}
		return false;	
	
	}
	
	public boolean isSendAmountUser(TokenDTO tokenDTO) throws Exception {
		
		UserRequestCoinInfo  userRequestCoinInfo= userRequestCoinRepository.findOne(tokenDTO.getId());
		
		Config configInfo=configInfoRepository.findConfigByConfigKey("walletfile");
		RegisterInfo userInfoModel1 = registerInfoRepository.findEquacoinUserInfoByEtherWalletAddress(EncryptDecrypt.encrypt(userRequestCoinInfo.getFromAddress()));
		RegisterInfo userInfoModel = registerInfoRepository.findEquocoinUserInfoByEmailId(tokenDTO.getEmailId());
		String decryptedWalletAddress = EncryptDecrypt.decrypt(userInfoModel.getWalletAddress());
		TransactionHistory transactionHistory = new TransactionHistory();
		String etherWalletAddress =equocoinUtils.getWalletAddress(configInfo.getConfigValue(), decryptedWalletAddress);
		if (userRequestCoinInfo.getPaymentMode().equals(env.getProperty("user.payment"))) {
				
			
			transactionReceipt = new TransactionReceipt();
			
			CompletableFuture.supplyAsync(() -> {
				   Credentials credentials;
				   try {
					   transactionHistory.setFromAddress(etherWalletAddress);
						transactionHistory.setToAddress(userRequestCoinInfo.getFromAddress());
						transactionHistory.setAmount(tokenDTO.getRequestToken().doubleValue());
						transactionHistory.setToken(new Double("0.0"));
						transactionHistory.setPaymentMode(env.getProperty("user.payment"));
						transactionHistory.setCreatedDate(new Date());
						transactionHistory.setSenderId(userInfoModel.getId());
						transactionHistory.setReceiverId(userInfoModel1.getId());
						transactionHistory.setStatus(env.getProperty("status.pending"));
						transactionHistory.setRegisterInfo(userInfoModel);
						transactionInfoRepository.save(transactionHistory);
				    if (transactionHistory != null) {
				     credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),
								configInfo.getConfigValue() + decryptedWalletAddress);
				     transactionReceipt = Transfer
								.sendFunds(web3j, credentials, userRequestCoinInfo.getFromAddress(), new BigDecimal(tokenDTO.getRequestToken()), Convert.Unit.ETHER).send();

				    }
				   } catch (Exception e) {

				    e.printStackTrace();
				   }

				   return "call blackchain";
				  }).thenAccept(product -> {
					   if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
				    if (transactionHistory != null) {
				     transactionHistory.setStatus(env.getProperty("status.success"));
					 userRequestCoinInfo.setTranscationType(1);
					 userRequestCoinRepository.save(userRequestCoinInfo);
				     transactionInfoRepository.save(transactionHistory);
				     tokenDTO.setPaymentMode(userRequestCoinInfo.getPaymentMode());
				     tokenDTO.setReceiver(userRequestCoinInfo.getReceiveraName());
				     tokenDTO.setSender(userRequestCoinInfo.getSenderName());
				     tokenDTO.setTransferAmount(new BigDecimal(userRequestCoinInfo.getRequestCoin()));
				     tokenUserServiceImpl.sendETHCoinPushNotificationFrom(userInfoModel,tokenDTO);
				     tokenUserServiceImpl.sendETHCoinPushNotificationTo(userInfoModel1,tokenDTO);
				    }
				   } else if(transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed"))) {
					   if (transactionHistory != null) {
						   transactionHistory.setStatus(env.getProperty("status.failed"));
					    transactionInfoRepository.save(transactionHistory);
					          }
					   }
				  });
				  return true;

		}

	if (userRequestCoinInfo.getPaymentMode().equals(env.getProperty("admin.payment"))) {
	
		transactionReceipt = new TransactionReceipt();
		CompletableFuture.supplyAsync(() -> {
			   Credentials credentials;
			   try {
				   transactionHistory.setFromAddress(etherWalletAddress);
					transactionHistory.setToAddress(userRequestCoinInfo.getFromAddress());
					transactionHistory.setAmount(new Double(0.0));
					transactionHistory.setToken(tokenDTO.getRequestToken());
					transactionHistory.setPaymentMode(env.getProperty("admin.payment"));
					transactionHistory.setCreatedDate(new Date());
					transactionHistory.setSenderId(userInfoModel.getId());
					transactionHistory.setReceiverId(userInfoModel1.getId());
					transactionHistory.setStatus(env.getProperty("status.pending"));
					transactionHistory.setRegisterInfo(userInfoModel);
				   	transactionInfoRepository.save(transactionHistory);
			    if (transactionHistory != null) {
			     credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),
							configInfo.getConfigValue()+ decryptedWalletAddress);
			     Equacoins assetToken = null;
					if(tokenDTO.getGasPrice()==1) {
					assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials, Contract.GAS_PRICE,
							Contract.GAS_LIMIT);
					}
					if(tokenDTO.getGasPrice()==2) {
						assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials, Contract.GAS_PRICE,
								Contract.GAS_LIMIT);
						}
					if(tokenDTO.getGasPrice()==3) {
						assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials, Contract.GAS_PRICE,
								Contract.GAS_LIMIT);
						}
					BigInteger Token=new BigDecimal(tokenDTO.getRequestToken()*10000).toBigInteger();
					transactionReceipt = assetToken.transfer(userRequestCoinInfo.getFromAddress(),
							Token).send();
			   
			    }
			   } catch (Exception e) {

			    e.printStackTrace();
			   }
			   return true;
			  }).thenAccept(product -> {
				  if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
			    if (transactionHistory != null) {
			    	 transactionHistory.setStatus(env.getProperty("status.success"));
					userRequestCoinInfo.setTranscationType(1);
					userRequestCoinRepository.save(userRequestCoinInfo);
			     transactionInfoRepository.save(transactionHistory);
			     tokenDTO.setPaymentMode(userRequestCoinInfo.getPaymentMode());
			     tokenDTO.setReceiver(userRequestCoinInfo.getReceiveraName());
			     tokenDTO.setSender(userRequestCoinInfo.getSenderName());
			     tokenDTO.setTransferAmount(new BigDecimal(userRequestCoinInfo.getRequestCoin()) );
			     tokenUserServiceImpl.sendETHCoinPushNotificationFrom(userInfoModel,tokenDTO);
			     tokenUserServiceImpl.sendETHCoinPushNotificationTo(userInfoModel1,tokenDTO);
			    }
			   } else if(transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed"))) {
				   if (transactionHistory != null) {
				   transactionHistory.setStatus(env.getProperty("status.failed"));
			    transactionInfoRepository.save(transactionHistory);
			          }
			   }
			  });
	}
	return true;			
	}
	
	public List<TokenDTO> getRequestCoinCount(TokenDTO tokenDTO) throws Exception {
		RegisterInfo userInfoModel = registerInfoRepository.findEquocoinUserInfoByEmailId(tokenDTO.getEmailId());
		String decryptedWalletAddress = EncryptDecrypt.decrypt(userInfoModel.getEtherWalletAddress());
		tokenDTO.setToAddress(decryptedWalletAddress);
		List<TokenDTO> requestCoinList = new ArrayList<TokenDTO>();
		List<UserRequestCoinInfo> requestCoinHistory = userRequestCoinRepository
				.findByToAddressOrderByCreatedDateDesc(tokenDTO.getToAddress());
		for (UserRequestCoinInfo requestCoinInfo : requestCoinHistory) {
			if(requestCoinInfo.getTranscationType()==0){
			TokenDTO requestCoin = listRequestCoin(requestCoinInfo);
			requestCoinList.add(requestCoin);
			}
		}
		return requestCoinList;
	}
	
	public TokenDTO listRequestCoin(UserRequestCoinInfo transaction) throws Exception {

		TokenDTO request = new TokenDTO();
		request.setFromAddress(transaction.getFromAddress());
		request.setToAddress(transaction.getToAddress());
		request.setPaymentMode(transaction.getPaymentMode());
		request.setRequestToken(transaction.getRequestCoin());
		request.setCreateDate(transaction.getCreatedDate());
		request.setId(transaction.getId());
		request.setSender(transaction.getSenderName());
		request.setReceiver(transaction.getReceiveraName());
	    return request;
		
		
	}
	
	public LoginDTO isGetDashboardValues(TokenDTO tokenDTO) throws Exception {
		LoginDTO responseDTO = new LoginDTO();
		RegisterInfo userInfoModel = registerInfoRepository.findEquocoinUserInfoByEmailId(tokenDTO.getEmailId());
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		
		if (userInfoModel != null && config.getConfigValue() !=null) {
			String decryptWalletAddress = EncryptDecrypt.decrypt(userInfoModel.getWalletAddress());
			String walletAddress = EncryptDecrypt.decrypt( userInfoModel.getEtherWalletAddress());
			tokenDTO.setWalletAddress(walletAddress);
			String decryptWalletPassword = EncryptDecrypt.decrypt(userInfoModel.getWalletPassword());
			if (walletAddress == null) {
				return null;
			}
			tokenDTO.setCentralAdmin(walletAddress);
			boolean isToken = tokenUserService.checkMainbalance(tokenDTO);
			if(isToken){
				responseDTO.setETHBalance(tokenDTO.getMainBalance());
				Credentials	credentials = WalletUtils.loadCredentials(decryptWalletPassword,
						config.getConfigValue()+decryptWalletAddress);
				Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,
						BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
				BigInteger balance = assetToken.balanceOf(walletAddress).send();
				BigDecimal balance1=new BigDecimal(balance);
				Double EQCBalance=balance1.doubleValue()/10000;
				BigDecimal b = new BigDecimal(EQCBalance, MathContext.DECIMAL64);
				BigDecimal oneether=currentValueUtils.getEtherValueForOneUSD();
				BigDecimal etherusd=responseDTO.getETHBalance().multiply(oneether);
				responseDTO.setUserETHUSD(etherusd);
				BigDecimal oneeqc=b.multiply(new BigDecimal("0.65"));
				//BigDecimal eqcusd=currentValueUtils.getBTCValueForOneUSD().multiply(oneeqc);
				responseDTO.setUserEQCUSD(oneeqc.doubleValue());
        	  	responseDTO.setEQUABalance(balance1.doubleValue()/10000);
        		List<TokenDTO> count= getRequestCoinCountInfo(tokenDTO);
				Long cc=(long) count.size();
				responseDTO.setTokenRequestCount(cc);
				responseDTO.setStatus("success");
				return responseDTO;
			}
           }
			else {
			responseDTO.setStatus("failed");
			return responseDTO;

		}
		return null;

	}
	
	public List<TokenDTO> getRequestCoinCountInfo(TokenDTO tokenDTO) throws Exception {
	
		List<TokenDTO> requestCoinList = new ArrayList<TokenDTO>();
		List<UserRequestCoinInfo> requestCoinHistory = userRequestCoinRepository
				.findByToAddressOrderByCreatedDateDesc(tokenDTO.getWalletAddress());
		for (UserRequestCoinInfo requestCoinInfo : requestCoinHistory) {
			if(requestCoinInfo.getTranscationType()==0){
			TokenDTO requestCoin = listRequestCoin(requestCoinInfo);
			requestCoinList.add(requestCoin);
			}
		}
		return requestCoinList;
	}

}
