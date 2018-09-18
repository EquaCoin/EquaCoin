package com.equocoin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.equocoin.dto.CrowdSaleProposalDTO;

@Service
public interface CrowdSaleProposalService {

	boolean createProposal(CrowdSaleProposalDTO crowdSaleDTO)  throws Exception;

	public List<CrowdSaleProposalDTO> listCrowdSale(CrowdSaleProposalDTO crowdSaleDTO) throws Exception;

	public boolean votingForCrowdSale(CrowdSaleProposalDTO crowdSaleDTO) throws Exception;

	public List<CrowdSaleProposalDTO> listCrowdSaleCategory(String categoryName,CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception;

	public List<CrowdSaleProposalDTO> listOwnProposal(String address,CrowdSaleProposalDTO crowdSaleDTO) throws Exception;

	public List<CrowdSaleProposalDTO> listOtherProposal(String address,CrowdSaleProposalDTO crowdSaleDTO) throws Exception;

	public List<CrowdSaleProposalDTO> listWonProposal(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception;

	public List<CrowdSaleProposalDTO> listFailedProposal(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception;

	public List<CrowdSaleProposalDTO> isCrowdsaleEnd(CrowdSaleProposalDTO crowdSaleDTO) throws Exception;

	public String userDocumentUpload(MultipartFile proposalDoc, CrowdSaleProposalDTO crowdSaleProposalDTO, String string)throws Exception;

	public boolean acceptingForProposal(CrowdSaleProposalDTO crowdSaleDTO)throws Exception;

	public List<CrowdSaleProposalDTO> adminProposallist(CrowdSaleProposalDTO crowdSaleProposalDTO)throws Exception;

	public List<CrowdSaleProposalDTO> adminViewProposallist(CrowdSaleProposalDTO crowdSaleProposalDTO)throws Exception;


	

}
