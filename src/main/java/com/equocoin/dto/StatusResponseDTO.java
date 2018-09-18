package com.equocoin.dto;

import java.util.List;



public class StatusResponseDTO {

	private String status;
	private String message;
	private LoginDTO loginInfo;
	private List<TokenDTO> tokenInfos;
	private TokenDTO mainAccountInfo;
	private TokenDTO tokenBalanceInfo;
	private List<TokenDTO> listToken;
	private List<TokenDTO> mintToken;
	private LoginDTO dashboardInfo;
	private List<CrowdSaleProposalDTO> crowdSaleProposalLists;
	
	public List<TokenDTO> getListToken() {
		return listToken;
	}

	public void setListToken(List<TokenDTO> listToken) {
		this.listToken = listToken;
	}

	public List<TokenDTO> getTokenInfos() {
		return tokenInfos;
	}

	public void setTokenInfos(List<TokenDTO> tokenInfos) {
		this.tokenInfos = tokenInfos;
	}

	public LoginDTO getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(LoginDTO loginInfo) {
		this.loginInfo = loginInfo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public TokenDTO getMainAccountInfo() {
		return mainAccountInfo;
	}

	public void setMainAccountInfo(TokenDTO mainAccountInfo) {
		this.mainAccountInfo = mainAccountInfo;
	}

	public TokenDTO getTokenBalanceInfo() {
		return tokenBalanceInfo;
	}

	public void setTokenBalanceInfo(TokenDTO tokenBalanceInfo) {
		this.tokenBalanceInfo = tokenBalanceInfo;
	}

	public List<CrowdSaleProposalDTO> getCrowdSaleProposalLists() {
		return crowdSaleProposalLists;
	}

	public void setCrowdSaleProposalLists(List<CrowdSaleProposalDTO> crowdSaleProposalLists) {
		this.crowdSaleProposalLists = crowdSaleProposalLists;
	}

	public List<TokenDTO> getMintToken() {
		return mintToken;
	}

	public void setMintToken(List<TokenDTO> mintToken) {
		this.mintToken = mintToken;
	}

	public LoginDTO getDashboardInfo() {
		return dashboardInfo;
	}

	public void setDashboardInfo(LoginDTO dashboardInfo) {
		this.dashboardInfo = dashboardInfo;
	}

	
	
	

}
