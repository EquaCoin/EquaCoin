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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.equocoin.dto.MessageInfoDTO;
import com.equocoin.dto.RegisterDTO;
import com.equocoin.dto.StatusResponseDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.service.EquoCoinUserActivitesService;
import com.equocoin.service.RegisterUserService;
import com.equocoin.service.TokenUserService;
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
	private RegisterUserService registerUserService;

	@Autowired
	private EquocoinUtils equocoinUtils;
	
	@Autowired
	private TokenUserService tokenUserService;
	
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
	@RequestMapping(value = "/send/equacoin", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Token transfer", notes = "Need to transfer Token for address")
	public synchronized ResponseEntity<String> sendToken(
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isTokenTransfer = equocoinUtils.validateRequestTokenPrams(tokenDTO);
			if (!isTokenTransfer) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidateEthAddress = equocoinUtils.isValidateEthAddress(tokenDTO);
			if (!isValidateEthAddress) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.eth.address"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isvalidateEmail = equocoinUtils.isValidateToAddress(tokenDTO);
			if (!isvalidateEmail) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("not.valid.toaddress"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			
			boolean isPasswordWrong = equocoinUtils.validatePasswordPrams(tokenDTO);
			if (!isPasswordWrong) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("password.incorrect"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isValidReqToken = equocoinUtils.validateTokenVal(tokenDTO);
			if (!isValidReqToken) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.request.token"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isAddressValid = tokenUserService.getTransferAccountParams(tokenDTO);
			{

				if (!isAddressValid) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("incorrect.address"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}
			}
			String isValidTokenBal = tokenUserService.isValidTokenBalForTokenTransfer(tokenDTO);
			if (isValidTokenBal != "success") {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(isValidTokenBal);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isTransfer = tokenUserService.sendEquacoin(tokenDTO);
			if (isTransfer) {

				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("mint.success"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}else{
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("send.token.failed"));
				
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in send Equacoin   : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	
	@CrossOrigin
	@RequestMapping(value = "/support", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Support Infos", notes = "Need to send support message")
	public synchronized ResponseEntity<String> getMessageFromApi(
			@ApiParam(value = "Required support Info details", required = true) @RequestBody MessageInfoDTO messageInfoDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
       TokenDTO tokenDTO=new TokenDTO();
       tokenDTO.setSessionId(messageInfoDTO.getSessionId());
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO.getSessionId());

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidSubject = equocoinUtils.isValidSubject(messageInfoDTO);

			if (!isValidSubject) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("subject.empty"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidContent = equocoinUtils.isValidContent(messageInfoDTO);

			if (!isValidContent) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("content.empty"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isMessageSend = equocoinUtils.sendMessage(messageInfoDTO,tokenDTO);
			if(isMessageSend){
				
				statusResponseDTO.setStatus("success");
				statusResponseDTO.setMessage(env.getProperty("message.send"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}
			else{
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("message.not.send"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in support api  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/send/ether", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Token transfer", notes = "Need to transfer Token for address")
	public synchronized ResponseEntity<String> etherTransfer(
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isTokenTransfer = equocoinUtils.validateSendEtherPrams(tokenDTO);
			if (!isTokenTransfer) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
		/*	boolean isValidReqToken = equocoinUtils.validateEtherVal(tokenDTO);
			if (!isValidReqToken) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.request.ether"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}*/

			boolean isValidateEthAddress = equocoinUtils.isValidateEthAddress(tokenDTO);
			if (!isValidateEthAddress) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.eth.address"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isvalidateEmail = equocoinUtils.isValidateToAddress(tokenDTO);
			if (!isvalidateEmail) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("not.valid.toaddress"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isPasswordWrong = equocoinUtils.validatePasswordPrams(tokenDTO);
			if (!isPasswordWrong) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("password.incorrect"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isAddressValid = tokenUserService.getTransferAccountParams(tokenDTO);
			{

				if (!isAddressValid) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("incorrect.address"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}
			}

			boolean isToken = tokenUserService.validEther(tokenDTO);
			if (!isToken) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(tokenDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}

			boolean isTransfer = tokenUserService.amountTransfer(tokenDTO);
			if (isTransfer) {

				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("amount.transfer.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}else{
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("transfer.amount.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in send ether  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
	
}
