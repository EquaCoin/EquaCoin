package com.equocoin.admin.sevice.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpSession;

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
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import com.equocoin.admin.service.AdminService;
import com.equocoin.dto.TokenDTO;
import com.equocoin.model.Config;
import com.equocoin.model.RegisterInfo;
import com.equocoin.repository.ConfigInfoRepository;
import com.equocoin.repository.RegisterInfoRepository;
import com.equocoin.service.impl.TokenUserServiceImpl;
import com.equocoin.soliditytojava.AssetToken;
import com.equocoin.utils.EncryptDecrypt;
import com.equocoin.utils.EquocoinUtils;
import com.equocoin.utils.SessionCollector;

@Service
public class AdminServiceImpl implements AdminService {

	private static final Logger LOG = LoggerFactory.getLogger(TokenUserServiceImpl.class);

	private final Web3j web3j = Web3j.build(new HttpService());

	@Autowired
	private Environment env;

	@Autowired
	private HttpSession session;

	@Autowired
	private RegisterInfoRepository registerInfoRepository;

	@Autowired
	private SessionCollector sessionCollector;

	@Autowired
	private EquocoinUtils equocoinUtils;

	@Autowired
	private ConfigInfoRepository configInfoRepository;

	public boolean isAdminExists(TokenDTO tokenDTO) throws Exception {
		HttpSession session = sessionCollector.find(tokenDTO.getSessionId());
		RegisterInfo equocoinUserInfos = registerInfoRepository
				.findEquocoinUserInfoByEmailId(session.getAttribute("emailId"));

		try {

			if (equocoinUserInfos != null) {

				return true;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return false;

	}

	@Override
	public boolean getAdminPendingTokenBalance(TokenDTO tokenDTO) throws Exception {

		try {

			Credentials credentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
					env.getProperty("credentials.address"));
			AssetToken assetToken = AssetToken.load(env.getProperty("token.address"), web3j, credentials,
					BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));

			//LOG.info("Total Supply" + assetToken.totalSupply().send());
			
			LOG.info("Total Supply" + assetToken.totalSupply().send());

			BigInteger TransferTokenBalance = assetToken.balanceOf(env.getProperty("main.address")).send();
			tokenDTO.setTotaltokenBalance(TransferTokenBalance);
			BigInteger totaltokens=assetToken.totalSupply().send();
			BigInteger mintTokens=assetToken.mintAmount().send();
			BigInteger soldtokens=assetToken.soldToken().send();
			BigInteger deleteCoins=assetToken.deleteToken().send();
			tokenDTO.setMintedAmount(mintTokens);
			tokenDTO.setSoldTokens(soldtokens);
			tokenDTO.setDeleteTokens(deleteCoins);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean getAdminTotalTokenBalance(TokenDTO tokenDTO) throws Exception {

		try {

			Credentials credentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
					env.getProperty("credentials.addres"));
			AssetToken assetToken = AssetToken.load(env.getProperty("token.address"), web3j, credentials,
					BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));

			LOG.info("Total Supply" + assetToken.totalSupply().send());

			tokenDTO.setTotaltokenBalance(assetToken.totalSupply().send());

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean getAdminSoldTokenBalance(TokenDTO tokenDTO) throws Exception {

		try {

			Credentials credentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
					env.getProperty("credentials.addres"));

			AssetToken assetToken = AssetToken.load(env.getProperty("token.address"), web3j, credentials,
					BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));

			tokenDTO.setSelledTokens(assetToken.soldToken().send());

			LOG.info("******* soldToken ******* " + assetToken.soldToken().send());

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean getUserCount(TokenDTO tokenDTO) {

		List<RegisterInfo> equocoinUserInfos = (List<RegisterInfo>) registerInfoRepository.findAll();
		tokenDTO.setTotalUserCount(BigInteger.valueOf(equocoinUserInfos.size()));
		return true;
	}

	@Override
	public boolean checkTokenBalance(TokenDTO tokenDTO) throws Exception {

		try {

			Credentials credentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
					env.getProperty("credentials.address"));
			AssetToken assetToken = AssetToken.load(env.getProperty("token.address"), web3j, credentials,
					BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));

			LOG.info("Total Supply" + assetToken.totalSupply().send());
			
			BigInteger balance = assetToken.balanceOf(env.getProperty("main.address")).send();

			int count = balance.compareTo(tokenDTO.getTokenBalance());
			if ( count == 1) {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}
