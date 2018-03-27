package com.equocoin.controller;

import java.util.List;

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

import com.equocoin.dto.CrowdSaleProposalDTO;
import com.equocoin.dto.StatusResponseDTO;
import com.equocoin.service.CrowdSaleProposalService;
import com.equocoin.utils.EquocoinUtils;
import com.google.gson.Gson;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/crowdsale/api")
@Api(value = "CroedSaleTokenController", description = "CroedSaleTokenController API")
@CrossOrigin

public class CrowdSaleProposalController {
	private static final Logger LOG = LoggerFactory.getLogger(CrowdSaleProposalController.class);

	@Autowired
	CrowdSaleProposalService crowdSaleProposalService;

	@Autowired
	private Environment env;

	@Autowired
	EquocoinUtils equocoinUtils;

	@CrossOrigin
	@RequestMapping(value = "/crowdsale/create", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "register account", notes = "Need to get user details and send email verification link and otp to mobile")
	public synchronized ResponseEntity<String> registerUser(
			@ApiParam(value = "Required user details", required = true) @RequestBody CrowdSaleProposalDTO crowdSaleDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(crowdSaleDTO.getSessionId());

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isValidInput = equocoinUtils.validateCrowdSaleParams(crowdSaleDTO);
			if (!isValidInput) {
				statusResponseDTO.setStatus(env.getProperty("incorrectDetails"));
				statusResponseDTO.setMessage(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean status = crowdSaleProposalService.CrowdSaleCreate(crowdSaleDTO);

			if (status) {
				statusResponseDTO.setStatus(env.getProperty("proposal.success"));
				statusResponseDTO.setMessage(env.getProperty("success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("server.problem"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
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
	@RequestMapping(value = "/crowdsale/list/{sessionId}", method = RequestMethod.GET, produces = {
			"application/json" })
	public synchronized ResponseEntity<String> AllCrowdSaleList(
			@ApiParam(value = "Required category name ", required = true) @PathVariable(value = "sessionId") String sessionId) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(sessionId);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.listCrowdSale();

			if (list != null) {
				statusResponseDTO.setStatus(env.getProperty("proposal.list.success"));
				statusResponseDTO.setMessage(env.getProperty("success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("proposal.list.failure"));
				statusResponseDTO.setMessage(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
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
	@RequestMapping(value = "/crowdsale/voting", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "voting for proposal", notes = "Need to put vote on crowdsale proposal")
	public synchronized ResponseEntity<String> userVotingInProposal(
			@ApiParam(value = "Required voting details", required = true) @RequestBody CrowdSaleProposalDTO crowdSaleDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(crowdSaleDTO.getSessionId());

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidInput = equocoinUtils.validateCrowdSaleVotingParams(crowdSaleDTO);
			if (!isValidInput) {
				statusResponseDTO.setStatus(env.getProperty("incorrectDetails"));
				statusResponseDTO.setMessage(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			boolean status = crowdSaleProposalService.votingForCrowdSale(crowdSaleDTO);

			if (status) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("voting.proposal.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("voting.proposal.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
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
	@RequestMapping(value = "/crowdsale/category/{sessionId}/{categoryname}", method = RequestMethod.GET, produces = {
			"application/json" })
	public synchronized ResponseEntity<String> crowdSaleList(
			@ApiParam(value = "Required category name ", required = true) @PathVariable(value = "categoryname") String categoryName,
			@ApiParam(value = "Required category name ", required = true) @PathVariable(value = "sessionId") String sessionId) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(sessionId);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.listCrowdSaleCategory(categoryName);

			if (list != null) {
				statusResponseDTO.setStatus(env.getProperty("proposal.list.success"));
				statusResponseDTO.setMessage(env.getProperty("success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("proposal.list.failure"));
				statusResponseDTO.setMessage(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
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
	@RequestMapping(value = "/crowdsale/user/{sessionId}/{address}", method = RequestMethod.GET, produces = {
			"application/json" })
	public synchronized ResponseEntity<String> crowdSaleProposalList(
			@ApiParam(value = "Required address for filter proposal ", required = true) @PathVariable(value = "address") String address,
			@ApiParam(value = "Required category name ", required = true) @PathVariable(value = "sessionId") String sessionId) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(sessionId);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.listOwnProposal(address);

			if (list != null) {
				statusResponseDTO.setStatus(env.getProperty("proposal.list.success"));
				statusResponseDTO.setMessage(env.getProperty("success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("proposal.list.failure"));
				statusResponseDTO.setMessage(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
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
	@RequestMapping(value = "/crowdsale/otheruser/{sessionId}/{address}", method = RequestMethod.GET, produces = {
			"application/json" })
	public synchronized ResponseEntity<String> crowdSaleProposalOtherList(
			@ApiParam(value = "Required address for filter proposal ", required = true) @PathVariable(value = "address") String address,
			@ApiParam(value = "Required category name ", required = true) @PathVariable(value = "sessionId") String sessionId) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(sessionId);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.listOtherProposal(address);

			if (list != null) {
				statusResponseDTO.setStatus(env.getProperty("proposal.list.success"));
				statusResponseDTO.setMessage(env.getProperty("success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("proposal.list.failure"));
				statusResponseDTO.setMessage(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
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
	@RequestMapping(value = "/crowdsale/won/{sessionId}", method = RequestMethod.GET, produces = { "application/json" })
	public synchronized ResponseEntity<String> crowdSaleProposalWonList(
			@ApiParam(value = "Required for session id", required = true) @PathVariable(value = "sessionId") String sessionId) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(sessionId);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.listWonProposal();

			if (list != null) {
				statusResponseDTO.setStatus(env.getProperty("proposal.list.success"));
				statusResponseDTO.setMessage(env.getProperty("success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("proposal.list.failure"));
				statusResponseDTO.setMessage(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
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
	@RequestMapping(value = "/crowdsale/failed/{sessionId}", method = RequestMethod.GET, produces = {
			"application/json" })
	public synchronized ResponseEntity<String> crowdSaleProposalFailedList(
			@ApiParam(value = "Required for session id", required = true) @PathVariable(value = "sessionId") String sessionId) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(sessionId);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.listFailedProposal();

			if (list != null) {
				statusResponseDTO.setStatus(env.getProperty("proposal.list.success"));
				statusResponseDTO.setMessage(env.getProperty("success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("proposal.list.failure"));
				statusResponseDTO.setMessage(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in registration  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

}
