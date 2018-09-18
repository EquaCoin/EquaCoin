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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.equocoin.dto.CrowdSaleProposalDTO;
import com.equocoin.dto.StatusResponseDTO;
import com.equocoin.service.CrowdSaleProposalService;
import com.equocoin.utils.EquocoinUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/dac/api")
@Api(value = "CroedSaleTokenController", description = "CroedSaleTokenController API")
@CrossOrigin
public class CrowdSaleProposalController {
	private static final Logger LOG = LoggerFactory.getLogger(CrowdSaleProposalController.class);

	@Autowired
	private CrowdSaleProposalService crowdSaleProposalService;

	@Autowired
	private Environment env;

	@Autowired
	private EquocoinUtils equocoinUtils;

	@CrossOrigin
	@RequestMapping(value = "/proposal/create", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "register account", notes = "Need to get user details and send email verification link and otp to mobile")
	public synchronized ResponseEntity<String> registerUser(
		
			@ApiParam(value = "Required user details", required = true) @RequestParam(name = "proposalInfo", value = "proposalInfo", required = true) String proposalInfo,
			@ApiParam(value = "Required file attachment", required = false) @RequestParam(name = "proposalDoc", value = "proposalDoc", required = false) MultipartFile proposalDoc)
	 {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			LOG.info("inside create ");
			ObjectMapper mapper = new ObjectMapper();
			CrowdSaleProposalDTO crowdSaleProposalDTO = null;
			
			try {
				crowdSaleProposalDTO = mapper.readValue(proposalInfo, CrowdSaleProposalDTO.class);
			} catch (Exception e) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.update.faild"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			
			boolean isSessionExpired = equocoinUtils.isSessionExpired(crowdSaleProposalDTO.getSessionId());

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			
			
			boolean isValidInput = equocoinUtils.validateCrowdSaleParams(crowdSaleProposalDTO);
			if (!isValidInput) {
					statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
					statusResponseDTO.setStatus(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isValidateEthAddress = equocoinUtils.isValidateProposalAddress(crowdSaleProposalDTO);
			if (!isValidateEthAddress) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.eth.address"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isUserId = equocoinUtils.validateUserIdParams(crowdSaleProposalDTO);
			if (!isUserId) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("account.invalid"));
				
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isToken = equocoinUtils.validAmount(crowdSaleProposalDTO);
			if (!isToken) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("balance.insufficient"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			boolean isvalidBalance = equocoinUtils.validateDacEquaMinimumBalance(crowdSaleProposalDTO);
			if (!isvalidBalance) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("not.allow.vote"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			if (proposalDoc!=null) {
				String proposalFilePath = crowdSaleProposalService.userDocumentUpload(proposalDoc,crowdSaleProposalDTO,"proposalId");
				if (proposalFilePath == null) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage("Please Upload Proper Document");
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}
			}
	
			boolean status = crowdSaleProposalService.createProposal(crowdSaleProposalDTO);
			if (status) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("proposal.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("server.problem"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in create proposal  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/proposal/list/{sessionId}", method = RequestMethod.GET, produces = {
			"application/json" })
	public synchronized ResponseEntity<String> AllCrowdSaleList(
			@ApiParam(value = "Required category name ", required = true) @PathVariable(value = "sessionId") String sessionId) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		CrowdSaleProposalDTO crowdSaleProposalDTO = new CrowdSaleProposalDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(sessionId);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isUserAccount = equocoinUtils.validateUserAccount(crowdSaleProposalDTO,sessionId);
			if (!isUserAccount) {
				statusResponseDTO.setMessage(env.getProperty("account.invalid"));
				statusResponseDTO.setStatus(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.listCrowdSale(crowdSaleProposalDTO);

			if (list != null) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("proposal.list.success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("proposal.list.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in proposal list  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/proposal/voting", method = RequestMethod.POST, produces = { "application/json" })
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
				statusResponseDTO.setMessage(env.getProperty("account.invalid"));
				statusResponseDTO.setStatus(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			
			boolean isUserId = equocoinUtils.validateUserIdParams(crowdSaleDTO);
			System.out.println("crowdlase userId");
			if (!isUserId) {
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				statusResponseDTO.setStatus(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isToken = equocoinUtils.validAmount(crowdSaleDTO);
			if (!isToken) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("balance.insufficient"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			
			boolean isvalidBalance = equocoinUtils.validateDacMemberMinimumBalance(crowdSaleDTO);
			if (!isvalidBalance) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("not.allow.vote"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			boolean isValidDate = equocoinUtils.validateProposalDateParams(crowdSaleDTO);
			if (!isValidDate) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("date.exceed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			
			boolean status = crowdSaleProposalService.votingForCrowdSale(crowdSaleDTO);

			if (status) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("voting.proposal.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(crowdSaleDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in proposal voting  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/proposal/category/{sessionId}/{categoryname}", method = RequestMethod.GET, produces = {
			"application/json" })
	public synchronized ResponseEntity<String> crowdSaleList(
			@ApiParam(value = "Required category name ", required = true) @PathVariable(value = "categoryname") String categoryName,
			@ApiParam(value = "Required category name ", required = true) @PathVariable(value = "sessionId") String sessionId) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		CrowdSaleProposalDTO crowdSaleProposalDTO=new CrowdSaleProposalDTO();
		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(sessionId);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isUserAccount = equocoinUtils.validateUserAccount(crowdSaleProposalDTO,sessionId);
			if (!isUserAccount) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("account.invalid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.listCrowdSaleCategory(categoryName,crowdSaleProposalDTO);

			if (list != null) {
				statusResponseDTO.setMessage(env.getProperty("proposal.list.success"));
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setMessage(env.getProperty("proposal.list.failure"));
				statusResponseDTO.setStatus(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in category proposal list  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/own/proposal/{sessionId}/{address}", method = RequestMethod.GET, produces = {
			"application/json" })
	public synchronized ResponseEntity<String> crowdSaleProposalList(
			@ApiParam(value = "Required address for filter proposal ", required = true) @PathVariable(value = "address") String address,
			@ApiParam(value = "Required category name ", required = true) @PathVariable(value = "sessionId") String sessionId) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		CrowdSaleProposalDTO crowdSaleProposalDTO=new CrowdSaleProposalDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(sessionId);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isUserAccount = equocoinUtils.validateUserAccount(crowdSaleProposalDTO,sessionId);
			if (!isUserAccount) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("account.invalid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.listOwnProposal(address,crowdSaleProposalDTO);

			if (list != null) {
				
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("proposal.list.success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("proposal.list.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in own proposal list  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/proposal/otheruser/{sessionId}/{address}", method = RequestMethod.GET, produces = {
			"application/json" })
	public synchronized ResponseEntity<String> crowdSaleProposalOtherList(
			@ApiParam(value = "Required address for filter proposal ", required = true) @PathVariable(value = "address") String address,
			@ApiParam(value = "Required category name ", required = true) @PathVariable(value = "sessionId") String sessionId) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		CrowdSaleProposalDTO crowdSaleProposalDTO=new CrowdSaleProposalDTO();
		try {
			boolean isSessionExpired = equocoinUtils.isSessionExpired(sessionId);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isUserAccount = equocoinUtils.validateUserAccount(crowdSaleProposalDTO,sessionId);
			if (!isUserAccount) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("account.invalid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.listOtherProposal(address,crowdSaleProposalDTO);

			if (list != null) {
				statusResponseDTO.setMessage(env.getProperty("proposal.list.success"));
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setMessage(env.getProperty("proposal.list.failure"));
				statusResponseDTO.setStatus(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in other proposal list  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/proposal/won/{sessionId}", method = RequestMethod.GET, produces = { "application/json" })
	public synchronized ResponseEntity<String> crowdSaleProposalWonList(
			@ApiParam(value = "Required for session id", required = true) @PathVariable(value = "sessionId") String sessionId) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		CrowdSaleProposalDTO crowdSaleProposalDTO=new CrowdSaleProposalDTO();

		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(sessionId);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isUserAccount = equocoinUtils.validateUserAccount(crowdSaleProposalDTO,sessionId);
			if (!isUserAccount) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("account.invalid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.listWonProposal(crowdSaleProposalDTO);

			if (list != null) {
				statusResponseDTO.setMessage(env.getProperty("proposal.list.success"));
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setMessage(env.getProperty("proposal.list.failure"));
				statusResponseDTO.setStatus(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in won proposal list  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/proposal/failed/{sessionId}", method = RequestMethod.GET, produces = {
			"application/json" })
	public synchronized ResponseEntity<String> crowdSaleProposalFailedList(
			@ApiParam(value = "Required for session id", required = true) @PathVariable(value = "sessionId") String sessionId) {
		CrowdSaleProposalDTO crowdSaleProposalDTO=new CrowdSaleProposalDTO();

		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(sessionId);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isUserAccount = equocoinUtils.validateUserAccount(crowdSaleProposalDTO,sessionId);
			if (!isUserAccount) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("account.invalid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.listFailedProposal(crowdSaleProposalDTO);

			if (list != null) {
				statusResponseDTO.setMessage(env.getProperty("proposal.list.success"));
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setMessage(env.getProperty("proposal.list.failure"));
				statusResponseDTO.setStatus(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in proposal failed list  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/approve/proposal", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Approve for proposal", notes = "Need to put Approve on crowdsale proposal")
	public synchronized ResponseEntity<String> adminAcceptInProposal(
			@ApiParam(value = "Required approve details", required = true) @RequestBody CrowdSaleProposalDTO crowdSaleDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			LOG.info("inside accept controller");
			boolean isSessionExpired = equocoinUtils.isSessionExpired(crowdSaleDTO.getSessionId());

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidInput = equocoinUtils.validateProposalAcceptParams(crowdSaleDTO);
			if (!isValidInput) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			if(crowdSaleDTO.getAccepted() != false){
			boolean isValidDate = equocoinUtils.validateProposalDateParams(crowdSaleDTO);
			if (!isValidDate) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("date.exceed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			}
			if(crowdSaleDTO.getAccepted() != false){
			boolean isvalidBalance = equocoinUtils.validateAdminEQUABalance(crowdSaleDTO);
			if (!isvalidBalance) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("not.enough.fund"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			}
			boolean status = crowdSaleProposalService.acceptingForProposal(crowdSaleDTO);

			if (status) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("Accept.proposal.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(crowdSaleDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Accepting Proposal  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/admin/proposal/list/{sessionId}", method = RequestMethod.GET, produces = {
			"application/json" })
	public synchronized ResponseEntity<String> AllProposalList(
			@ApiParam(value = "Required session id ", required = true) @PathVariable(value = "sessionId") String sessionId) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		CrowdSaleProposalDTO crowdSaleProposalDTO = new CrowdSaleProposalDTO();
		try {

			boolean isSessionExpired = equocoinUtils.isSessionExpired(sessionId);

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			
			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.adminProposallist(crowdSaleProposalDTO);

			if (list != null) {
				statusResponseDTO.setMessage(env.getProperty("proposal.list.success"));
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setMessage(env.getProperty("proposal.list.failure"));
				statusResponseDTO.setStatus(env.getProperty("failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in admin proposal list  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/admin/view/proposal", method = RequestMethod.POST, produces = {
			"application/json" })
	public synchronized ResponseEntity<String> viewProposalList(
			@ApiParam(value = "Required view details", required = true) @RequestBody CrowdSaleProposalDTO crowdSaleProposalDTO)
			 {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
            System.out.println("inside view proposal");
			boolean isSessionExpired = equocoinUtils.isSessionExpired(crowdSaleProposalDTO.getSessionId());

			if (!isSessionExpired) {
				statusResponseDTO.setStatus("failure");
				statusResponseDTO.setMessage(env.getProperty("session.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isValidInput = equocoinUtils.validateViewProposalParams(crowdSaleProposalDTO);
			if (!isValidInput) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrect.proposalId"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			
			List<CrowdSaleProposalDTO> list = crowdSaleProposalService.adminViewProposallist(crowdSaleProposalDTO);

			if (list != null) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("proposal.list.success"));
				statusResponseDTO.setCrowdSaleProposalLists(list);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("proposal.list.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in view proposal  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
}
