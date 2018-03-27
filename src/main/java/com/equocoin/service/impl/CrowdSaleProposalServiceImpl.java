package com.equocoin.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;

import com.equocoin.dto.CrowdSaleProposalDTO;
import com.equocoin.handlesolidity.SolidityHandler;
import com.equocoin.service.CrowdSaleProposalService;

@Service
public class CrowdSaleProposalServiceImpl implements CrowdSaleProposalService {

	@Autowired
	SolidityHandler solidityHandler;

	@Override
	public boolean CrowdSaleCreate(CrowdSaleProposalDTO crowdSaleDTO) throws ParseException {

		try {
			boolean status = solidityHandler.CrowdSaleCreate(crowdSaleDTO);
			if (status) {
				return true;
			}

		} catch (IOException | CipherException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

	@Override
	public List<CrowdSaleProposalDTO> listCrowdSale() throws Exception {
		try {
			return solidityHandler.crowdsaleProposalList();
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean votingForCrowdSale(CrowdSaleProposalDTO crowdSaleDTO) {

		try {
			boolean status = solidityHandler.votingForCrowdSale(crowdSaleDTO);
			if (status) {
				return true;
			}

		} catch (IOException | CipherException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}
	
	@Override
	public List<CrowdSaleProposalDTO> listCrowdSaleCategory(String categoryName) throws Exception {
		try {
			return solidityHandler.crowdsaleProposalCategoryList(categoryName);
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<CrowdSaleProposalDTO> listOwnProposal(String address) throws Exception {
		try {
			return solidityHandler.listOwnProposal(address);
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<CrowdSaleProposalDTO> listOtherProposal(String address) throws Exception {
		try {
			return solidityHandler.listOtherProposal(address);
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<CrowdSaleProposalDTO> listWonProposal() throws Exception {
		try {
			return solidityHandler.listWonProposal();
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<CrowdSaleProposalDTO> listFailedProposal() throws Exception {
		try {
			return solidityHandler.listFailedProposal();
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<CrowdSaleProposalDTO> isCrowdsaleEnd() throws Exception {
		try {
			return solidityHandler.crowdsaleProposalList();
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}

}
