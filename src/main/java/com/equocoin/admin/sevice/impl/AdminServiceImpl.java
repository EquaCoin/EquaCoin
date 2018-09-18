package com.equocoin.admin.sevice.impl;

import java.math.BigInteger;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import com.equocoin.admin.service.AdminService;
import com.equocoin.dto.TokenDTO;
import com.equocoin.model.RegisterInfo;
import com.equocoin.repository.ConfigInfoRepository;
import com.equocoin.repository.RegisterInfoRepository;
import com.equocoin.service.impl.TokenUserServiceImpl;
import com.equocoin.soliditytojava.Equacoins;
import com.equocoin.utils.EquocoinUtils;
import com.equocoin.utils.SessionCollector;

@Service
public class AdminServiceImpl implements AdminService {

	private static final Logger LOG = LoggerFactory.getLogger(TokenUserServiceImpl.class);

	// private final Web3j web3j = Web3j.build(new HttpService());
	// private final Web3j web3j = Web3j.build(new
	// HttpService("https://rinkeby.infura.io"));
	private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io"));

	@Autowired
	private Environment env;

	@SuppressWarnings("unused")
	@Autowired
	private HttpSession session;

	@Autowired
	private RegisterInfoRepository registerInfoRepository;

	@Autowired
	private SessionCollector sessionCollector;

	@SuppressWarnings("unused")
	@Autowired
	private EquocoinUtils equocoinUtils;

	@SuppressWarnings("unused")
	@Autowired
	private ConfigInfoRepository configInfoRepository;

	public boolean isAdminExists(TokenDTO tokenDTO) throws Exception {
		@SuppressWarnings("static-access")
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
			Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,
					BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));

			BigInteger TransferTokenBalance = assetToken.balanceOf(env.getProperty("main.address")).send();
			tokenDTO.setTotaltokenBalance(TransferTokenBalance.doubleValue() / 10000);
			@SuppressWarnings("unused")
			BigInteger totaltokens = assetToken._totalSupply().send();
			BigInteger mintTokens = assetToken.mintAmount().send();
			BigInteger soldtokens = assetToken.soldToken().send();
			BigInteger deleteCoins = assetToken.deleteToken().send();
			tokenDTO.setMintedAmount(mintTokens.doubleValue() / 10000);
			tokenDTO.setSoldTokens(soldtokens.doubleValue() / 10000);
			tokenDTO.setDeleteTokens(deleteCoins.doubleValue() / 10000);
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
			Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,
					BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));

			BigInteger b = assetToken._totalSupply().send();
			tokenDTO.setTotaltokenBalance(b.doubleValue() / 10000);

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

			Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,
					BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));

			tokenDTO.setSelledTokens(assetToken.soldToken().send());

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
			Equacoins assetToken = Equacoins.load(env.getProperty("token.address"), web3j, credentials,
					BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));

			BigInteger balance = assetToken.balanceOf(env.getProperty("main.address")).send();
			Double balance1 = balance.doubleValue() / 10000;
			int count = balance1.compareTo(tokenDTO.getTokenBalance());
			if (count == 1) {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}
