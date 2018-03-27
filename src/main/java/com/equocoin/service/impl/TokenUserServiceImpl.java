package com.equocoin.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Env;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import com.equocoin.dto.LoginDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.handlesolidity.SolidityHandler;
import com.equocoin.model.Config;
import com.equocoin.model.RegisterInfo;
import com.equocoin.model.TokenInfo;
import com.equocoin.repository.ConfigInfoRepository;
import com.equocoin.repository.RegisterInfoRepository;
import com.equocoin.repository.TokenInfoRepository;
import com.equocoin.service.TokenUserService;
import com.equocoin.soliditytojava.AssetToken;
import com.equocoin.utils.EncryptDecrypt;
import com.equocoin.utils.EquocoinUtils;
import com.equocoin.utils.SessionCollector;

@Service
public class TokenUserServiceImpl implements TokenUserService {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(TokenUserServiceImpl.class);
	private final Web3j web3j = Web3j.build(new HttpService());

	@Autowired
	private Environment env;

	@Autowired
	SolidityHandler solidityHandler;

	@Autowired
	TokenInfoRepository tokenInfoRepository;

	@Autowired
	private HttpSession session;

	@Autowired
	RegisterInfoRepository registerInfoRepository;

	@Autowired
	SessionCollector sessionCollector;

	@Autowired
	ConfigInfoRepository configInfoRepository;

	@Autowired
	EquocoinUtils equocoinUtils;

	@Override
	public boolean TokenCreate(TokenDTO tokenDTO) {
		try {
			String status = solidityHandler.tokenCreation(tokenDTO);
			@SuppressWarnings("unused")
			Integer isEmailExist = 0;
			if (status != null) {
				System.out.println("Token Address" + status);
				return true;
			}

		} catch (IOException | CipherException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean saveTokenGenerationInfo(TokenDTO tokenDTO) {
		TokenInfo tokenInfo = new TokenInfo();

		System.out.println("token address" + tokenDTO.getTokenAddress());

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
		try {
			String address = web3j.ethCoinbase().send().getAddress();
			if (address != null && StringUtils.isNotBlank(address)) {
				tokenDTO.setCentralAdmin(address);
				return true;
			}

		} catch (IOException e) {

			e.printStackTrace();
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
			LOG.info("wei " + wei);
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
		try {
			boolean status = solidityHandler.TransferToken(tokenDTO);

			if (status) {

				return true;
			}

		} catch (IOException | CipherException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return false;

	}

	@Override
	public boolean TransferAdminship(TokenDTO tokenDTO) {
		try {
			boolean status = solidityHandler.TransferAdminship(tokenDTO);

			if (status) {
				System.out.println("Token Address" + status);
				return true;
			}

		} catch (IOException | CipherException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

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
		try {
			return solidityHandler.listTokens();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public boolean validAmount(TokenDTO tokenDTO, HttpServletRequest request) {

		tokenDTO.setCentralAdmin(tokenDTO.getFromAddress());
		boolean status = checkMainbalance(tokenDTO);

		if (status) {

			BigDecimal balance = tokenDTO.getMainBalance();
			BigDecimal equaCoin = tokenDTO.getTransferAmount();

			LOG.info("balance" + balance);
			LOG.info("equaCoin" + equaCoin);

			int res = balance.compareTo(equaCoin);
			System.out.println(res);
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

		Credentials credentials;
		try {

			HttpSession session = sessionCollector.find(tokenDTO.getSessionId());
			Config config = configInfoRepository.findConfigByConfigKey("walletfile");
			RegisterInfo equocoinUserInfos = registerInfoRepository
					.findEquocoinUserInfoByEmailId(session.getAttribute("emailId"));
			if (equocoinUserInfos != null && config != null) {

				String decryptWalletAddress = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletAddress());
				String decryptWalletPassword = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletPassword());

				tokenDTO.setToAddress(config.getConfigValue() + "//" + decryptWalletAddress);

				String walletAddress = equocoinUtils.getWalletAddress(config.getConfigValue(), decryptWalletAddress);
				if (walletAddress == null) {
					return false;
				}
				tokenDTO.setWalletAddress(walletAddress);
				tokenDTO.setWalletPassword(decryptWalletPassword);

				credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),
						new File(tokenDTO.getToAddress()));
				AssetToken assetToken = AssetToken.load(env.getProperty("token.address"), web3j, credentials,
						BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));

				BigInteger balance = assetToken.balanceOf(tokenDTO.getWalletAddress()).send();
				tokenDTO.setTokenBalance(balance);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CipherException e) {
			e.printStackTrace();
		}

		return false;
	}

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
		boolean status = solidityHandler.TransferCoin(tokenDTO);

		if (status) {

			return true;
		}

		return false;
	}

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

		String isSuccess = solidityHandler.getRefund(tokenDTO);

		return isSuccess;
	}

	@Override
	public boolean sendToken(TokenDTO tokenDTO) throws Exception {
		boolean isSuccess = solidityHandler.sendToken(tokenDTO);
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

	
}
