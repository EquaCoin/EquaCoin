package com.equocoin.service;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import com.equocoin.dto.LoginDTO;
import com.equocoin.dto.RegisterDTO;

@Service
public interface RegisterUserService {

	public boolean validateConformPassword(RegisterDTO registerDTO);

	public boolean saveAccountInfo(RegisterDTO registerDTO,String password) throws Exception;

	public boolean isMobileNoExist(String mobileNo) ;

	public boolean isAccountExistCheckByEmailId(String emailId);

	//public LoginDTO isMobileNoAndPasswordExist(RegisterDTO registerDTO,HttpServletRequest request);

	boolean isWalletCreate(RegisterDTO registerDTO, Integer Id) throws InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, NoSuchProviderException, CipherException, IOException, ParseException;

	LoginDTO isEmailIdAndPasswordExist(RegisterDTO registerDTO, HttpServletRequest request,String password)
			throws Exception;

	

	public boolean isEmailIdExist(RegisterDTO registerDTO, HttpServletRequest request) throws Exception;

	public boolean updatePassword(RegisterDTO registerDTO, String encryptPassword);

	
	



}
