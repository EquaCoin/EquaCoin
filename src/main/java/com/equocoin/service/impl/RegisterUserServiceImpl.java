package com.equocoin.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import com.equocoin.dto.LoginDTO;
import com.equocoin.dto.RegisterDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.model.Config;
import com.equocoin.model.QRCode;
import com.equocoin.model.RegisterInfo;
import com.equocoin.repository.ConfigInfoRepository;
import com.equocoin.repository.QRCodeRepository;
import com.equocoin.repository.RegisterInfoRepository;
import com.equocoin.repository.UserRequestCoinRepository;
import com.equocoin.service.EmailNotificationService;
import com.equocoin.service.RegisterUserService;
import com.equocoin.utils.EncryptDecrypt;
import com.equocoin.utils.EquocoinUtils;
import com.equocoin.utils.SessionCollector;
import com.equocoin.utils.UserUtils;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;



@Service
public class RegisterUserServiceImpl implements RegisterUserService {
	private static final Logger LOG = LoggerFactory.getLogger(RegisterUserServiceImpl.class);

	@Autowired
	private Environment env;

	@Autowired
	private RegisterInfoRepository registerInfoRepository;

	@Autowired
	private TokenUserServiceImpl tokenUserServiceImpl;

	@Autowired
	private SessionCollector sessionCollector;

	@Autowired
	private ConfigInfoRepository configInfoRepository;

	@Autowired
	private EquocoinUtils equocoinUtils;

	@Autowired
	private EmailNotificationService emailNotificationService;
	
	@Autowired
	private QRCodeRepository qrCodeRepository;
	
	@Autowired
	private QRCodeGenerateServiceimpl qrCodeGenerateService;

	@SuppressWarnings("unused")
	@Autowired
	private UserRequestCoinRepository userRequestCoinRepository;
	
	@Autowired
	private UserUtils userUtils;
	
	@Override
	public boolean validateConformPassword(RegisterDTO registerDTO) {
		if (registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean saveAccountInfo(RegisterDTO registerDTO, String password) throws Exception {
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		RegisterInfo registerInfo = new RegisterInfo();
		registerInfo.setPassword(password.trim());
		registerInfo.setMobileNo(registerDTO.getMobileNo());
		registerInfo.setIsdCode(registerDTO.getIsdCode());
		registerInfo.setEmailId(registerDTO.getEmailId().trim());
		registerInfo.setWalletAddress(" ");
		registerInfo.setCreatedDate(new Date());
		registerInfo.setVersion(new Date());
		registerInfo.setAppId("");
		registerInfo.setMobileType("");
		registerInfoRepository.save(registerInfo);
		if (registerInfo != null) {
			boolean status = isWalletCreate(registerDTO, registerInfo.getId());
			if (status) {
				String encryptWalletAddress = EncryptDecrypt.encrypt(registerDTO.getWalletAddress());
				String encryptWalletPassword = EncryptDecrypt.encrypt(registerDTO.getWalletPassword());
				registerInfo.setWalletAddress(encryptWalletAddress);
				registerInfo.setWalletPassword(encryptWalletPassword);
				registerInfoRepository.save(registerInfo);
				String walletAddress = equocoinUtils.getWalletAddress(config.getConfigValue(),
						registerDTO.getWalletAddress());
				
				if(walletAddress!=null){
					
				 Credentials credentials = WalletUtils.loadCredentials(registerDTO.getWalletPassword(),
						 config.getConfigValue() + registerDTO.getWalletAddress());
	               BigInteger privateKeyInDec = credentials.getEcKeyPair().getPrivateKey();
	               BigInteger publicKeyInDec = credentials.getEcKeyPair().getPublicKey();
	               String sPrivatekeyInHex = privateKeyInDec.toString(16);
	               String sPublickeyInHex = publicKeyInDec.toString(16);
	               String etherwalletAddress= EncryptDecrypt.encrypt(walletAddress);
	               registerInfo.setPrivate_key(sPrivatekeyInHex);
	               registerInfo.setPublicKey(sPublickeyInHex);
	               registerInfo.setEtherWalletAddress(etherwalletAddress);
	               qrCodeGenerater(registerInfo,walletAddress);
	               registerInfoRepository.save(registerInfo);
	               
				}
				boolean isEmailSent = emailNotificationService.sendEmail(registerDTO.getEmailId(),
						"WELCOME TO THE EQUACOIN WALLET !", walletAddress);

				return true;

			}
			registerInfoRepository.delete(registerInfo.getId());
			return false;
		}
		return false;
	}

	@Override
	public boolean isMobileNoExist(String mobileNo) {
		Integer accHolderInfoCount = registerInfoRepository.countEquocoinUserInfoByMobileNo(mobileNo);
		if (accHolderInfoCount > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isAccountExistCheckByEmailId(String emailId) {
		Integer isEmailExist = registerInfoRepository.countEquocoinUserInfoByEmailIdIgnoreCase(emailId.trim());
		if (isEmailExist > 0) {
			return true;
		}
		return false;
	}

	@Override
	public LoginDTO isEmailIdAndPasswordExist(RegisterDTO registerDTO, HttpServletRequest request, String password)
			throws Exception {
		LoginDTO responseDTO = new LoginDTO();
		TokenDTO tokenDTO = new TokenDTO();
		RegisterInfo equocoinUserInfos = registerInfoRepository
				.findEquocoinUserInfoByEmailIdAndPassword(registerDTO.getEmailId().trim(), password.trim());
		//LOG.info("insidewalletAddress "+equocoinUserInfos .getEmailId());
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		Config configInfo = configInfoRepository.findConfigByConfigKey("apache.server");
		
		//long userRequestCoinCount=userRequestCoinRepository.count();
		// LOGIN Success
		if (equocoinUserInfos != null && config != null) {
			HttpSession session = request.getSession();
			session.setAttribute("emailId", equocoinUserInfos.getEmailId());
			HttpSessionEvent event = new HttpSessionEvent(session);
			sessionCollector.sessionCreated(event);
			responseDTO.setUserName(equocoinUserInfos.getEmailId());
			String decryptWalletAddress = EncryptDecrypt.decrypt(equocoinUserInfos.getWalletAddress());
			String walletAddress = equocoinUtils.getWalletAddress(config.getConfigValue(), decryptWalletAddress);
		
			responseDTO.setType(equocoinUserInfos.getType());
			if (walletAddress != null) {
				registerDTO.setWalletAddress(walletAddress);
				tokenDTO.setWalletAddress(walletAddress);
				responseDTO.setWalletAddress(walletAddress);
				List<TokenDTO> count= userUtils.getRequestCoinCountInfo(tokenDTO);
				Long cc=(long) count.size();
				responseDTO.setTokenRequestCount(cc);
			}
			
			tokenDTO.setCentralAdmin(responseDTO.getWalletAddress());
			if (tokenUserServiceImpl.checkMainbalance(tokenDTO)) {
				responseDTO.setWalletBalance(tokenDTO.getMainBalance());
			}
			
			
			responseDTO.setPrivatekey(equocoinUserInfos.getPrivate_key());
			responseDTO.setPublickey(equocoinUserInfos.getPublicKey());
			String qrcodepath=configInfo.getConfigValue()+env.getProperty("user.qrcode.folder")+"//"+equocoinUserInfos.getId()+"//"+equocoinUserInfos.getId() + ".png";
			responseDTO.setId(session.getId());
			responseDTO.setStatus("success");
			responseDTO.setQrcodepath(qrcodepath);
			return responseDTO;

		}
		responseDTO.setStatus("failed");
		return responseDTO;
	}

	public boolean isWalletCreate(RegisterDTO registerDTO, Integer Id) throws InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, NoSuchProviderException, CipherException, IOException, ParseException {
		try {
			Config configInfo = configInfoRepository.findConfigByConfigKey("walletfile");
			if (configInfo != null) {
				String walletFileLocation = configInfo.getConfigValue();

				walletFileLocation = walletFileLocation.replace("/", "\\");
				File createfolder = new File(walletFileLocation);
				if (!createfolder.exists()) {
					createfolder.mkdirs();
				}

				String fileName = null;

				fileName = WalletUtils.generateNewWalletFile(registerDTO.getWalletPassword(),
						new File(walletFileLocation), false);

				registerDTO.setWalletAddress(fileName);

				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return false;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean isEmailIdExist(RegisterDTO registerDTO, HttpServletRequest request) throws Exception {

		RegisterInfo equocoinUserInfos = registerInfoRepository
				.findEquocoinUserInfoByEmailId(registerDTO.getEmailId().trim());
		if (equocoinUserInfos != null) {
			String decryptPassword = EncryptDecrypt.decrypt(equocoinUserInfos.getPassword());
			equocoinUserInfos.setPassword(decryptPassword);
			boolean isEmailSent = emailNotificationService.sendEmailforgot(registerDTO.getEmailId(),
					"Password info from EquaCoin Team", equocoinUserInfos.getPassword());

			return true;
		}

		return false;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean updatePassword(RegisterDTO registerDTO, String Password) {

		RegisterInfo registerInfo = new RegisterInfo();
		registerInfo.setPassword(Password);
		if (registerInfo != null) {
			registerInfoRepository.save(registerInfo);
			return true;
		}
		return false;

	}



	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	private void qrCodeGenerater(RegisterInfo registerInfo,String walletAddress) throws Exception{
		int id = registerInfo.getId();
		String dynamicQRFolder = Integer.toString(id);
		if (registerInfo != null) {
				QRCode qrcode = qrCodeRepository.findQRCodeByQrKey("QRKey");
				String qrFileLocation = null;
                String path=env.getProperty("user.qrcode.folder");

				if (qrcode != null) {
					
					qrFileLocation = qrcode.getQrcodeValue()+File.separator+path+File.separator;	
					File createfolder = new File(qrFileLocation.concat(dynamicQRFolder));
					if (!createfolder.exists()) {
						createfolder.mkdir();
						qrFileLocation = createfolder.getPath().replace("//", "/");
						qrFileLocation = qrFileLocation+"/";
					}
				}
				String filePath = qrFileLocation+File.separator+id + ".png";
				String charset = "UTF-8"; // or "ISO-8859-1"
				Map hintMap = new HashMap();
				hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
				qrCodeGenerateService.createQRCode(walletAddress, filePath, charset, hintMap, 200, 200);
				qrCodeGenerateService.readQRCode(filePath, charset, hintMap);
		}
	}

}
