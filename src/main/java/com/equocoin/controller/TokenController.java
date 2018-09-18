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

import com.equocoin.admin.service.AdminService;
import com.equocoin.dto.StatusResponseDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.handlesolidity.SolidityHandler;
import com.equocoin.service.TokenUserService;
import com.equocoin.utils.EquocoinUtils;
import com.equocoin.utils.SessionCollector;
import com.google.gson.Gson;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/equocoin/api")
@Api(value = "TokenController", description = "TokenController API")
@CrossOrigin
public class TokenController {
	private static final Logger LOG = LoggerFactory.getLogger(TokenController.class);

	@Autowired
	private Environment env;

	@Autowired
	private TokenUserService tokenUserService;

	@Autowired
	private EquocoinUtils equocoinUtils;

	@SuppressWarnings("unused")
	@Autowired
	private SessionCollector sessionCollector;

	@Autowired
	private AdminService adminService;

	@Autowired
	private SolidityHandler solidityHandler;


// Admin side token transfer api
	@CrossOrigin
	@RequestMapping(value = "/token/transfer", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Token transfer", notes = "Need to transfer Token for address")
	public synchronized ResponseEntity<String> transferToken(
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isTokenTransfer = equocoinUtils.validateTransferTokenPrams(tokenDTO);
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

			String isValidTokenBal = solidityHandler.isValidTokenBalForCrowdsale(tokenDTO);
			if (isValidTokenBal != "success") {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(isValidTokenBal);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isTransfer = tokenUserService.TransferCoin(tokenDTO);
			if (isTransfer) {

				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("token.transfer.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("transfer.token.failed"));
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
	@RequestMapping(value = "/token/balance", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Get  Mainbalance", notes = "Need to get balance for mainAccount")
	public synchronized ResponseEntity<String> tokenBalance(
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isToken = tokenUserService.getUserTokenBalance(tokenDTO);
			if (isToken) {
				statusResponseDTO.setStatus("success");
				statusResponseDTO.setMessage(env.getProperty("token.balance"));
				statusResponseDTO.setTokenBalanceInfo(tokenDTO);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in getTokenbalance  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}



	@CrossOrigin
	@RequestMapping(value = "/token/list", method = RequestMethod.GET, produces = { "application/json" })
	@ApiOperation(value = "Token transfer", notes = "Need to transfer Token for address")
	public synchronized ResponseEntity<String> getAllListTokens() {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {
			List<TokenDTO> list = tokenUserService.listToken();
			if (list != null) {
				statusResponseDTO.setStatus("Success");
				statusResponseDTO.setMessage("All Token List");
				statusResponseDTO.setListToken(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus("Failed");
				statusResponseDTO.setMessage("No Token is available");
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
	@RequestMapping(value = "/token/cancel", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Token details", notes = "Need to cancel on ERC223 token details")
	public synchronized ResponseEntity<String> cancelToken(
			@ApiParam(value = "Required cancel token details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO.getSessionId());

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage("Session Expired ");
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

//			boolean isValidParam = equocoinUtils.validateCancelTokenParam(tokenDTO);
//
//			if (!isValidParam) {
//				statusResponseDTO.setStatus(env.getProperty("failure"));
//				statusResponseDTO.setMessage(env.getProperty("token.invalid"));
//				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
//			}

			boolean isValidToken = adminService.checkTokenBalance(tokenDTO);

			if (!isValidToken) {

				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.balance.invalid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean status = tokenUserService.isTokenCancel(tokenDTO);

			if (status) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("mint.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.cancel.failed"));
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
	@RequestMapping(value = "/wallet/balance", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Get  walletbalance", notes = "Need to get balance for UserAccount")
	public synchronized ResponseEntity<String> walletBalance(
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean iswalletBalance = tokenUserService.getwalletBalancePrams(tokenDTO);
			if (!iswalletBalance) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isToken = tokenUserService.checkMainbalance(tokenDTO);
			if (isToken) {
				statusResponseDTO.setStatus("success");
				statusResponseDTO.setMessage(env.getProperty("wallet.balance"));
				statusResponseDTO.setTokenBalanceInfo(tokenDTO);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("wallet.balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in getwalletbalance  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/purchase/token", method = RequestMethod.POST, consumes = { "application/json" }, produces = {
	"application/json" })
	@ApiOperation(value = "Token transfer", notes = "Need to transfer Token for address")
	public synchronized ResponseEntity<String> purchaseCoin(
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
                   LOG.info("inside controller");
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isAmountTransfer = equocoinUtils.validateTransferAmountPrams(tokenDTO);
			if (!isAmountTransfer) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
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

			boolean isToken = tokenUserService.validAmount(tokenDTO, request);
			if (!isToken) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("balance.insufficient"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}

		
			String isValidTokenBal = solidityHandler.isValidTokenBalForCrowdsale(tokenDTO);
			if (isValidTokenBal != "success") {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(isValidTokenBal);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isTransferd = tokenUserService.sendToken(tokenDTO);
			if (!isTransferd) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("transfer.token.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("mint.success"));
			//statusResponseDTO.setTokenBalanceInfo(tokenDTO.getMainBalance());
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in amount transfer  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
	
}
