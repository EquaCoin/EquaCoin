package com.equocoin.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.equocoin.dto.CrowdSaleProposalDTO;

@Service
public interface CrowdSaleProposalService {

	boolean CrowdSaleCreate(CrowdSaleProposalDTO crowdSaleDTO)  throws ParseException;

	public List<CrowdSaleProposalDTO> listCrowdSale() throws Exception;

	boolean votingForCrowdSale(CrowdSaleProposalDTO crowdSaleDTO);

	List<CrowdSaleProposalDTO> listCrowdSaleCategory(String categoryName) throws Exception;

	List<CrowdSaleProposalDTO> listOwnProposal(String address) throws Exception;

	List<CrowdSaleProposalDTO> listOtherProposal(String address) throws Exception;

	List<CrowdSaleProposalDTO> listWonProposal() throws Exception;

	List<CrowdSaleProposalDTO> listFailedProposal() throws Exception;

	List<CrowdSaleProposalDTO> isCrowdsaleEnd() throws Exception;

	

}
