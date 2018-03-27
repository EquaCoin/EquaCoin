package com.equocoin.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletUtils;

import com.equocoin.dto.LoginDTO;
import com.equocoin.dto.RegisterDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.model.Config;
import com.equocoin.model.RegisterInfo;
import com.equocoin.repository.ConfigInfoRepository;
import com.equocoin.repository.RegisterInfoRepository;
import com.equocoin.service.EmailNotificationService;
import com.equocoin.service.RegisterUserService;
import com.equocoin.utils.EncryptDecrypt;
import com.equocoin.utils.EquocoinUtils;
import com.equocoin.utils.SessionCollector;

import org.hibernate.sql.Update;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class RegisterUserServiceImpl implements RegisterUserService {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(RegisterUserServiceImpl.class);

	@Autowired
	private Environment env;

	@Autowired
	private RegisterInfoRepository registerInfoRepository;

	@Autowired
	TokenUserServiceImpl tokenUserServiceImpl;

	@Autowired
	SessionCollector sessionCollector;

	@Autowired
	ConfigInfoRepository configInfoRepository;

	@Autowired
	EquocoinUtils equocoinUtils;

	@Autowired
	EmailNotificationService emailNotificationService;

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
		registerInfo.setPassword(password);
		registerInfo.setMobileNo(registerDTO.getMobileNo());
		registerInfo.setIsdCode(registerDTO.getIsdCode());
		registerInfo.setEmailId(registerDTO.getEmailId().trim());
		registerInfo.setWalletAddress(" ");
		registerInfo.setCreatedDate(new Date());
		registerInfo.setVersion(new Date());
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
				/*
				 * String verificationLink = "Hi," + "    " +
				 * env.getProperty("email.welcome.message") + "     " +
				 * env.getProperty("message.success") + "      " +
				 * env.getProperty("purchase.equacoin") + "   " +
				 * env.getProperty("wallet.id") + "       " +
				 * walletAddress+"       " +
				 * env.getProperty("verification.link") +
				 * env.getProperty("accholderotpverification.admin.portal.url");
				 */

				// Send verification link to user email Id - With subject &
				// content
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
				.findEquocoinUserInfoByEmailIdAndPassword(registerDTO.getEmailId().trim(), password);
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
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
				LOG.info("walletAddress " + walletAddress);
				registerDTO.setWalletAddress(walletAddress);
				tokenDTO.setWalletAddress(walletAddress);
				responseDTO.setWalletAddress(walletAddress);
			}
			tokenDTO.setCentralAdmin(responseDTO.getWalletAddress());
			if (tokenUserServiceImpl.checkMainbalance(tokenDTO)) {
				responseDTO.setWalletBalance(tokenDTO.getMainBalance());
			}
			responseDTO.setId(session.getId());
			responseDTO.setStatus("success");
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

				System.out.println("walletFileLocation " + walletFileLocation);

				String fileName = null;

				fileName = WalletUtils.generateNewWalletFile(registerDTO.getWalletPassword(),
						new File(walletFileLocation), false);

				registerDTO.setWalletAddress(fileName);

				/*
				 * String walletAddress =
				 * equocoinUtils.getWalletAddress(walletFileLocation, fileName);
				 * if (walletAddress != null) {
				 * registerDTO.setWalletAddress(walletAddress); } else { return
				 * false; }
				 */

				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return false;
	}

	@Override
	public boolean isEmailIdExist(RegisterDTO registerDTO, HttpServletRequest request) throws Exception {

		RegisterInfo equocoinUserInfos = registerInfoRepository
				.findEquocoinUserInfoByEmailId(registerDTO.getEmailId().trim());
		if (equocoinUserInfos != null) {
			String decryptPassword = EncryptDecrypt.decrypt(equocoinUserInfos.getPassword());
			equocoinUserInfos.setPassword(decryptPassword);
			boolean isEmailSent = emailNotificationService.sendEmailforgot(registerDTO.getEmailId(),
					"Password info from EQC", equocoinUserInfos.getPassword());

			return true;
		}

		return false;
	}

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

	/*
	 * @Override public LoginDTO isEmailIdExist(RegisterDTO
	 * registerDTO,HttpServletRequest request) { RegisterInfo equocoinUserInfos
	 * = registerInfoRepository
	 * .findEquocoinUserInfoByEmailId(registerDTO.getEmailId().trim()); if
	 * (equocoinUserInfos != null) {
	 * 
	 * 
	 * } return null; }
	 */

	/*
	 * @Override public boolean fileWriteIntoLocation(MultipartFile file, String
	 * oldPath, String newPath, String fileName) { // Check file Already Exist!
	 * if (oldPath != null) { oldPath = oldPath.replace("/", "\\"); File oldFile
	 * = new File(oldPath); if (oldFile.exists()) { oldFile.delete(); } }
	 * 
	 * FileInputStream reader = null; FileOutputStream writer = null;
	 * 
	 * try { // Create Folder Location newPath = newPath.replace("/", "\\");
	 * File createfolder = new File(newPath); if (!createfolder.exists()) {
	 * createfolder.mkdirs(); }
	 * 
	 * // Create File in Folder Location reader = (FileInputStream)
	 * file.getInputStream(); byte[] buffer = new byte[1000]; File outputFile =
	 * new File(newPath + fileName); outputFile.createNewFile(); writer = new
	 * FileOutputStream(outputFile); while ((reader.read(buffer)) != -1) {
	 * writer.write(buffer); } LOG.info("Created file in the Path=" + newPath +
	 * fileName); return true; } catch (Exception e) {
	 * LOG.info("Failed to create file in the  Path= " + newPath + fileName);
	 * return false; } finally { try { if (reader != null) { reader.close(); }
	 * if (writer != null) { writer.close(); } } catch (IOException e) {
	 * LOG.info("Failed to create file in the Path= " + newPath + fileName);
	 * return false; } } }
	 */

}
