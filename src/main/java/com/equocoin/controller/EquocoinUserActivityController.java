package com.equocoin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.equocoin.dto.LoginDTO;
import com.equocoin.dto.RegisterDTO;
import com.equocoin.dto.StatusResponseDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.handlesolidity.SolidityHandler;
import com.equocoin.service.EquoCoinUserActivitesService;
import com.equocoin.service.RegisterUserService;
import com.equocoin.service.TokenUserService;
import com.equocoin.service.impl.TokenUserServiceImpl;
import com.equocoin.soliditytojava.AssetToken;
import com.equocoin.utils.EncryptDecrypt;
import com.equocoin.utils.EquocoinUtils;
import com.google.gson.Gson;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/equocoin/api/user")
@Api(value = "EquocoinUserActivityController", description = "EquocoinUserActivityController API")
@CrossOrigin
public class EquocoinUserActivityController {
	private static final Logger LOG = LoggerFactory.getLogger(TokenController.class);
	@Autowired
	private Environment env;

	@Autowired
	RegisterUserService registerUserService;

	@Autowired
	private EquocoinUtils equocoinUtils;
	@Autowired
	TokenUserServiceImpl tokenUserServiceImpl;
	@Autowired
	private EquoCoinUserActivitesService equoCoinUserActivitesService;

	@CrossOrigin
	@RequestMapping(value = "/transaction/history", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Transaction History", notes = "Need to show transaction history")
	public synchronized ResponseEntity<String> getAllListTokens(
			@ApiParam(value = "Required cancel token details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO.getSessionId());

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<TokenDTO> list = equoCoinUserActivitesService.transactionList(tokenDTO.getWalletAddress());
			if (list != null) {
				statusResponseDTO.setStatus("Success");
				statusResponseDTO.setMessage(env.getProperty("transaction.list"));
				statusResponseDTO.setListToken(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("transaction.notexist"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in tokencreation  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/forgot/password", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Forgot password", notes = "Need to update the new password")
	public synchronized ResponseEntity<String> forgotPassword(
			@ApiParam(value = "Required User details", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {

			boolean isvalid = equocoinUtils.validateLoginParam(registerDTO);
			if (!isvalid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.faild"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isvalidemail = equocoinUtils.validateEmail(registerDTO.getEmailId());
			if (!isvalidemail) {

				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.validate.email"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			boolean isEmailIdExist = registerUserService.isEmailIdExist(registerDTO, request);
			if (!isEmailIdExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("email.notexist"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("password.update"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in forgotpassword  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/getrefund", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "refund amount", notes = "Need to show refund amount")
	public synchronized ResponseEntity<String> getRefund(
			@ApiParam(value = "Required refund amount details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO.getSessionId());

			if (!isSessionExpired) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isAddressExist = tokenUserServiceImpl.isAddressExist(tokenDTO);
			if (!isAddressExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrect.address"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			String getRefund = tokenUserServiceImpl.getRefund(tokenDTO);
			/*if (!getRefund) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("refund.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}*/
			statusResponseDTO.setStatus("success");
			statusResponseDTO.setMessage(getRefund);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in tokencreation  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	/*
	 * @CrossOrigin
	 * 
	 * @RequestMapping(value = "/get/mainbalance", method = RequestMethod.GET,
	 * produces = { "application/json" })
	 * 
	 * @ApiOperation(value = "Get  Mainbalance", notes =
	 * "Need to get balance for mainAccount") public synchronized
	 * ResponseEntity<String> getmainBalance() { StatusResponseDTO
	 * statusResponseDTO = new StatusResponseDTO(); TokenDTO tokenDTO = new
	 * TokenDTO(); try {
	 * 
	 * 
	 * 
	 * boolean isCenteraladmin =
	 * tokenUserService.validateCentraladmin(tokenDTO); if (!isCenteraladmin) {
	 * statusResponseDTO.setStatus(env.getProperty("failure"));
	 * statusResponseDTO.setMessage(env.getProperty("invalidCentraladmin"));
	 * return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO),
	 * HttpStatus.PARTIAL_CONTENT); }
	 * 
	 * boolean isMainbalance = tokenUserService.checkMainbalance(tokenDTO);
	 * 
	 * if (!isMainbalance) {
	 * statusResponseDTO.setStatus(env.getProperty("failure"));
	 * statusResponseDTO.setMessage(env.getProperty("invalidMainbalance"));
	 * return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO),
	 * HttpStatus.PARTIAL_CONTENT); }
	 * 
	 * statusResponseDTO.setStatus("success");
	 * statusResponseDTO.setMessage(env.getProperty("mainaccount.balance"));
	 * statusResponseDTO.setMainAccountInfo(tokenDTO); return new
	 * ResponseEntity<String>(new Gson().toJson(statusResponseDTO),
	 * HttpStatus.PARTIAL_CONTENT);
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * LOG.error("Problem in getMainbalance  : ", e);
	 * statusResponseDTO.setStatus(env.getProperty("failure"));
	 * statusResponseDTO.setMessage(env.getProperty("server.problem")); return
	 * new ResponseEntity<String>(new Gson().toJson(statusResponseDTO),
	 * HttpStatus.PARTIAL_CONTENT); } }
	 */
}
