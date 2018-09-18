package com.equocoin.userMobilityController;

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

import com.equocoin.dto.LoginDTO;
import com.equocoin.dto.StatusResponseDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.service.TokenUserService;
import com.equocoin.utils.EquocoinUtils;
import com.equocoin.utils.UserUtils;
import com.google.gson.Gson;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/equacoin/mobile/api")
@Api(value = "UserMobileActivitiesController", description = "UserMobileActivitiesController API")
@CrossOrigin
public class UserMobileActivitiesController {

	static final Logger LOG = LoggerFactory.getLogger(UserMobileActivitiesController.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private EquocoinUtils equocoinUtils;
	
	@Autowired
	private UserUtils userUtils;
	
	@Autowired
	private TokenUserService tokenUserService;
	
	@CrossOrigin
	@RequestMapping(value = "/sendcoin", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Send Coin", notes = "Need to Send Coin")
	public synchronized ResponseEntity<String> sendCoin(
			@ApiParam(value = "Send Coin", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request) throws Exception {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isValid = equocoinUtils.validateSendCoinParam(tokenDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isValidateToAddress = equocoinUtils.isValidateEthAddress(tokenDTO);
			if (!isValidateToAddress) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.to.address"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isvalidateEmail = equocoinUtils.isValidateToAddress(tokenDTO);
			if (!isvalidateEmail) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("not.valid.toaddress"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			if(tokenDTO.getPaymentMode().equals(env.getProperty("user.payment"))){
				
			boolean isValidReqToken = userUtils.validateTokenVal(tokenDTO);
			if (!isValidReqToken) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("valid.transfer.amt"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			}
			if(tokenDTO.getPaymentMode().equals(env.getProperty("admin.payment"))){
			boolean isValidReqToken = userUtils.isTransferAmtZeroUser(tokenDTO);
			if (!isValidReqToken) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("valid.transfer.amt"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			}
			boolean isValidPaymentMode = userUtils.validPaymentModeUser(tokenDTO);
			if (!isValidPaymentMode) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("payment.mode.not.correct"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isPasswordWrong = equocoinUtils.validatePasswordPrams(tokenDTO);
			if (!isPasswordWrong) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("password.incorrect"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isValidRequestCoin = userUtils.validCoin(tokenDTO);
			if (!isValidRequestCoin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(tokenDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isSendAmount = userUtils.isSendAmountToUser(tokenDTO);
			if (!isSendAmount) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("send.coin.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("send.coin.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}

			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in send coin history : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	

	@CrossOrigin
	@RequestMapping(value = "/requestcoin", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Request Coin", notes = "Need to get Request Coin")
	public synchronized ResponseEntity<String> requestCoin(
			@ApiParam(value = "Request Coin", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request) throws Exception {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isValid = equocoinUtils.validateRequestCoinParam(tokenDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
	
			boolean isvalidateWallet= equocoinUtils.isValidateEthAddress(tokenDTO);
			if (!isvalidateWallet) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.to.address"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isvalidateEmail = equocoinUtils.isValidateToAddress(tokenDTO);
			if (!isvalidateEmail) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("not.valid.toaddress"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			if(tokenDTO.getPaymentMode().equals(env.getProperty("user.payment"))){
				
				boolean isValidReqToken = userUtils.validateTokenVal(tokenDTO);
				if (!isValidReqToken) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("valid.transfer.amt"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}
				
				}
				if(tokenDTO.getPaymentMode().equals(env.getProperty("admin.payment"))){
				boolean isValidReqToken = userUtils.isTransferAmtZeroUser(tokenDTO);
				if (!isValidReqToken) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("valid.transfer.amt"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}
				}
				boolean isRequestSave = userUtils.saveRequestInfo(tokenDTO);
				if (!isRequestSave) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("request.coin.failed"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}else {
					tokenUserService.requestcoinPushNotification(tokenDTO);
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("request.coin.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
				}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in request coin : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/requestsendcoin", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Send Request Coin", notes = "Need to Send Request Coin")
	public synchronized ResponseEntity<String> sendRequestCoin(
			@ApiParam(value = "Send Request Coin", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request) throws Exception {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isValid = userUtils.validateSendRequestCoinParam(tokenDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isValidId = userUtils.validateId(tokenDTO);
			if (!isValidId) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("id.not.available"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isValidPaymentMode = userUtils.validPaymentMode(tokenDTO);
			if (!isValidPaymentMode) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("payment.mode.not.correct"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isPasswordWrong = equocoinUtils.validatePasswordPrams(tokenDTO);
			if (!isPasswordWrong) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("password.incorrect"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isValidRequestCoin = userUtils.validRequestCoin(tokenDTO);
			if (!isValidRequestCoin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(tokenDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isSendAmount = userUtils.isSendAmountUser(tokenDTO);
			if (!isSendAmount) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("send.coin.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("send.coin.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in request coin : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/requestcoin/history", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Request Coin History", notes = "Need to get Request Coin History")
	public synchronized ResponseEntity<String> requestCoinHistory(
			@ApiParam(value = "Request Coin History", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request) throws Exception {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
				
			
			List<TokenDTO> count= userUtils.getRequestCoinCount(tokenDTO);
			if (count == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("userList.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("usreList.success"));
				statusResponseDTO.setListToken(count);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in request coin transaction history : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/dashboard", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "dashboard update balance", notes = "Need to update dashboard")
	public synchronized ResponseEntity<String> dashboardUpdate(
			@ApiParam(value = "Required User details", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request) throws Exception {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
				
			LoginDTO responseDTO = userUtils.isGetDashboardValues(tokenDTO);
			if (responseDTO.getStatus().equalsIgnoreCase("failed")) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("dashboard.values.not.show"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("dashboard.values.show"));
			statusResponseDTO.setDashboardInfo(responseDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);


		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in dashboard balances  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}
}
