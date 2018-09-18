package com.equocoin.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import com.equocoin.dto.LoginDTO;
import com.equocoin.dto.RegisterDTO;
import com.equocoin.dto.StatusResponseDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.service.EmailNotificationService;
import com.equocoin.service.RegisterUserService;
import com.equocoin.utils.EncryptDecrypt;
import com.equocoin.utils.EquocoinUtils;
import com.equocoin.utils.SessionCollector;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/equocoin/api/equocoinuser")
@Api(value = "RegistrationController", description = "RegistrationController API")
@CrossOrigin
public class RegisterController {

	private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);
	
	@SuppressWarnings("unused")
	private static final HttpServletRequest HttpServletRequest = null;
	
	@SuppressWarnings("unused")
	private static final HttpServletResponse HttpServletResponse = null;
	
	//private final Web3j web3j = Web3j.build(new HttpService());
	//private final Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/"));
	//private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io"));
	@SuppressWarnings("unused")
	private EthGetBalance ethGetBalance;
	
	@Autowired
	private Environment env;
	
	@SuppressWarnings("unused")
	@Autowired
	private HttpSession session;

	@Autowired
	private RegisterUserService registerUserService;

	@SuppressWarnings("unused")
	@Autowired
	private EmailNotificationService emailNotificationService;

	@Autowired
	private EquocoinUtils equocoinUtils;

	@SuppressWarnings("unused")
	@Autowired
	private SessionCollector sessionCollector;

	@CrossOrigin
	@RequestMapping(value = "/register", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "register account", notes = "Need to get user details and send email verification link and otp to mobile")
	public synchronized ResponseEntity<String> registerUser(

			@ApiParam(value = "Required user details", required = true) @RequestBody RegisterDTO registerDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValid = equocoinUtils.validateRegistrationParam(registerDTO);
			if (!isValid) {
				// User registration validation failed
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidEmailId = equocoinUtils.validateEmail(registerDTO.getEmailId());
			if (!isValidEmailId) {

				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.validate.email"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}

			boolean isConformPassword = registerUserService.validateConformPassword(registerDTO);
			if (!isConformPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("password.exist"));

				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isAccountExistCheckByMobileNo = registerUserService.isMobileNoExist(registerDTO.getMobileNo());
			if (isAccountExistCheckByMobileNo) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("mobileno.exist"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isAccountExistCheckByEmailId = registerUserService
					.isAccountExistCheckByEmailId(registerDTO.getEmailId().toLowerCase());
			if (isAccountExistCheckByEmailId) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("email.exist"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			String encryptedPassword = EncryptDecrypt.encrypt(registerDTO.getPassword());

			// Save user registration & verification details in db
			boolean isRegister = registerUserService.saveAccountInfo(registerDTO, encryptedPassword);
			if (isRegister) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("register.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in registration  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = { "application/json" }, produces = {
			"application/json" })
	@ApiOperation(value = "login user", notes = "Validate user and allow user to login. return auth token as response")
	public ResponseEntity<String> userLoginVerification(
			@ApiParam(value = "Required user mobile no and password ", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request, HttpServletResponse response) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {

			if (!equocoinUtils.validateLoginParam(registerDTO)) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.faild"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidEmailId = equocoinUtils.validateEmail(registerDTO.getEmailId());
			if (!isValidEmailId) {

				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.validate.email"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			boolean isAppIdCheck = equocoinUtils.isAppIdCheck(registerDTO);
			if (!isAppIdCheck) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("app.id"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			String encryptPassword = EncryptDecrypt.encrypt(registerDTO.getPassword());
			LoginDTO responseDTO = registerUserService.isEmailIdAndPasswordExist(registerDTO, request, encryptPassword);
			if (responseDTO.getStatus().equalsIgnoreCase("failed")) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("login.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("login.success"));
			statusResponseDTO.setLoginInfo(responseDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in login  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/logout", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "login user", notes = "Validate user and allow user to logout. return auth token as response")
	public ResponseEntity<String> userLogout(
			@ApiParam(value = "Required user emailId ", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request, HttpServletResponse response) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {
			TokenDTO tokenDTO = new TokenDTO();

			tokenDTO.setSessionId(registerDTO.getSessionId());
			tokenDTO.setEmailId(registerDTO.getEmailId());
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			LOG.info("SessionId " + registerDTO.getSessionId());

			boolean isvalidate = equocoinUtils.validateLogoutParams(registerDTO);
			if (!isvalidate) {
				// session.invalidate();
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("logout.failure"));

			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("logout.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in logout  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
}
