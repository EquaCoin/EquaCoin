package com.equocoin.adminController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.equocoin.controller.RegisterController;
import com.equocoin.dto.LoginDTO;
import com.equocoin.dto.RegisterDTO;
import com.equocoin.dto.StatusResponseDTO;
import com.equocoin.service.RegisterUserService;
import com.equocoin.utils.EncryptDecrypt;
import com.equocoin.utils.EquocoinUtils;
import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/equocoin/api/equocoinuser")
@Api(value = "RegistrationController", description = "RegistrationController API")
@CrossOrigin
public class AdminController {

	private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

	@Autowired
	private Environment env;

	@Autowired
	EquocoinUtils equocoinUtils;

	@Autowired
	RegisterUserService registerUserService;

	/*@CrossOrigin
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
	}*/

}
