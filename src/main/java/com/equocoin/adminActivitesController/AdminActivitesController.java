package com.equocoin.adminActivitesController;

import java.util.List;

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
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;

import com.equocoin.admin.service.AdminService;
import com.equocoin.controller.RegisterController;
import com.equocoin.dto.StatusResponseDTO;
import com.equocoin.dto.TokenDTO;
import com.equocoin.handlesolidity.SolidityHandler;
import com.equocoin.service.TokenUserService;
import com.equocoin.utils.EquocoinUtils;
import com.google.gson.Gson;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = "/equocoin/api/admin")
@Api(value = "AdminActivitesController", description = "AdminActivitesController API")
@CrossOrigin
public class AdminActivitesController {

	private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);
	private static final HttpServletRequest HttpServletRequest = null;
	private static final HttpServletResponse HttpServletResponse = null;
	private final Web3j web3j = Web3j.build(new HttpService());
	private static EthGetBalance ethGetBalance;

	@Autowired
	private Environment env;

	@Autowired
	private HttpSession session;

	@Autowired
	private EquocoinUtils equocoinUtils;

	@Autowired
	private AdminService adminService;

	@Autowired
	private TokenUserService tokenUserService;
	@Autowired
	private SolidityHandler solidityHandler;

	@CrossOrigin
	@RequestMapping(value = "/wallet/balance", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Get  walletbalance", notes = "Need to get balance for UserAccount")
	public synchronized ResponseEntity<String> adminWalletBalance(
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean iswalletBalance = adminService.isAdminExists(tokenDTO);
			if (!iswalletBalance) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isCenteraladmin = tokenUserService.validateCentraladmin(tokenDTO);
			if (!isCenteraladmin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalidCentraladmin"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isMainbalance = tokenUserService.checkMainbalance(tokenDTO);

			if (!isMainbalance) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalidMainbalance"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			statusResponseDTO.setStatus("success");
			statusResponseDTO.setMessage(env.getProperty("mainaccount.balance"));
			statusResponseDTO.setMainAccountInfo(tokenDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in getwalletbalance  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/coin/balance", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Get  walletbalance", notes = "Need to get balance for UserAccount")
	public synchronized ResponseEntity<String> getAdminTokenBalance(
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isAdminTokenBalance = adminService.getAdminPendingTokenBalance(tokenDTO);
			if (!isAdminTokenBalance) {

				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			statusResponseDTO.setStatus("success");
			statusResponseDTO.setMessage(env.getProperty("token.balance"));
			statusResponseDTO.setTokenBalanceInfo(tokenDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in getwalletbalance  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/total/coin/balance", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Get  walletbalance", notes = "Need to get balance for UserAccount")
	public synchronized ResponseEntity<String> getAdminTotalTokenBalance(
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean iswalletBalance = adminService.isAdminExists(tokenDTO);
			if (!iswalletBalance) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("admin"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isAdminTotalTokenBalance = adminService.getAdminTotalTokenBalance(tokenDTO);
			if (!isAdminTotalTokenBalance) {

				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			statusResponseDTO.setStatus("success");
			statusResponseDTO.setMessage(env.getProperty("token.balance"));
			statusResponseDTO.setTokenBalanceInfo(tokenDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in getwalletbalance  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	
	
	@CrossOrigin
	@RequestMapping(value = "/sold/coin/balance", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Get  walletbalance", notes = "Need to get balance for UserAccount")
	public synchronized ResponseEntity<String> getSelldTokenBalance(
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean iswalletBalance = adminService.isAdminExists(tokenDTO);
			if (!iswalletBalance) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("admin"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isAdminTotalTokenBalance = adminService.getAdminSoldTokenBalance(tokenDTO);
			if (!isAdminTotalTokenBalance) {

				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			statusResponseDTO.setStatus("success");
			statusResponseDTO.setMessage(env.getProperty("token.balance"));
			statusResponseDTO.setTokenBalanceInfo(tokenDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in getwalletbalance  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/user/count", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Get  walletbalance", notes = "Need to get balance for UserAccount")
	public synchronized ResponseEntity<String> getUserCount(
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean iswalletBalance = adminService.isAdminExists(tokenDTO);
			if (!iswalletBalance) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("admin"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isAdminTotalTokenBalance = adminService.getUserCount(tokenDTO);
			if (!isAdminTotalTokenBalance) {

				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			statusResponseDTO.setStatus("success");
			statusResponseDTO.setMessage(env.getProperty("token.balance"));
			statusResponseDTO.setTokenBalanceInfo(tokenDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in getwalletbalance  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/mint/token", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Get  walletbalance", notes = "Need to get minttoken")
	public synchronized ResponseEntity<String> adminMintTokens(
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean iswalletBalance = adminService.isAdminExists(tokenDTO);
			if (!iswalletBalance) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isCenteraladmin = tokenUserService.validateMintTokenParams(tokenDTO);
			if (!isCenteraladmin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isMainbalance = solidityHandler.mintToken(tokenDTO);

			if (!isMainbalance) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("mint.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			statusResponseDTO.setStatus("success");
			statusResponseDTO.setMessage(env.getProperty("mint.success"));
			statusResponseDTO.setTokenBalanceInfo(tokenDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in getwalletbalance  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/mint/history", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Transaction History", notes = "Need to show transaction history")
	public synchronized ResponseEntity<String> getAllListmintTokens(
			@ApiParam(value = "Required cancel token details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(tokenDTO.getSessionId());

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean iswalletBalance = adminService.isAdminExists(tokenDTO);
			if (!iswalletBalance) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<TokenDTO> list = equocoinUtils.transactionList(tokenDTO.getWalletAddress());
			if (list != null) {
				statusResponseDTO.setStatus("Success");
				statusResponseDTO.setMessage(env.getProperty("mint.list"));
				statusResponseDTO.setMintToken(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("mint.notexist"));
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

}
