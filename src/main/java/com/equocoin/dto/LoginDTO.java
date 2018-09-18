package com.equocoin.dto;

import java.math.BigDecimal;

public class LoginDTO {

	private String mobileNo;
	private String isdCode;
	private String emailId;
	private String password;
	private String userName;
	private String walletAddress;
	private BigDecimal walletBalance;
	private String qrcodepath;
	private String id;
	private Integer type;
	private Double EQUABalance;
	private BigDecimal ETHBalance;
	private String privatekey;
	private String publickey;
	private Long tokenRequestCount;
	private BigDecimal userETHUSD;
	private Double userEQCUSD;
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal getWalletBalance() {
		return walletBalance;
	}

	public void setWalletBalance(BigDecimal walletBalance) {
		this.walletBalance = walletBalance;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	private String status;

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIsdCode() {
		return isdCode;
	}

	public void setIsdCode(String isdCode) {
		this.isdCode = isdCode;
	}

	public String getQrcodepath() {
		return qrcodepath;
	}

	public void setQrcodepath(String qrcodepath) {
		this.qrcodepath = qrcodepath;
	}


	public Double getEQUABalance() {
		return EQUABalance;
	}

	public void setEQUABalance(Double eQUABalance) {
		EQUABalance = eQUABalance;
	}

	public BigDecimal getETHBalance() {
		return ETHBalance;
	}

	public void setETHBalance(BigDecimal eTHBalance) {
		ETHBalance = eTHBalance;
	}

	public String getPrivatekey() {
		return privatekey;
	}

	public void setPrivatekey(String privatekey) {
		this.privatekey = privatekey;
	}

	public String getPublickey() {
		return publickey;
	}

	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}

	public Long getTokenRequestCount() {
		return tokenRequestCount;
	}

	public void setTokenRequestCount(Long tokenRequestCount) {
		this.tokenRequestCount = tokenRequestCount;
	}

	public BigDecimal getUserETHUSD() {
		return userETHUSD;
	}

	public void setUserETHUSD(BigDecimal userETHUSD) {
		this.userETHUSD = userETHUSD;
	}

	public Double getUserEQCUSD() {
		return userEQCUSD;
	}

	public void setUserEQCUSD(Double userEQCUSD) {
		this.userEQCUSD = userEQCUSD;
	}


	

	
}
