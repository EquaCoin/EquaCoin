package com.equocoin.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.equocoin.model.RegisterInfo;

public class TokenDTO {

	private BigInteger id;
	private BigInteger initialValue;
	private String coinName;
	private String coinSymbol;
	private BigInteger decimalUnits;
	private String centralAdmin;
	private BigDecimal mainBalance;
	private BigInteger tokens;
	private String toAddress;
	private String fromAddress;
	private BigDecimal amount;
	private BigDecimal transferAmount;
	private BigInteger gasValue;
	private String walletAddress;
	private String walletPassword;
	private BigInteger tokenBalance;
	private BigInteger TotaltokenBalance;
	private BigInteger selledTokens;
	private String tokenAddress;
	private String sessionId;
	private String emailId;
	private Date createDate;
	private BigInteger balance;
	private BigInteger totalUserCount;
	private BigInteger crowdSaleTokenBalance;
	private BigInteger crowdSaleSoldTokens;
	private BigInteger transferTokenBalance;
	private BigDecimal requestToken;
	private BigInteger soldTokens;
    private BigInteger mintedAmount;
    private BigInteger deleteTokens;
    private String paymentMode;
    
	public BigInteger getDeleteTokens() {
		return deleteTokens;
	}

	public void setDeleteTokens(BigInteger deleteTokens) {
		this.deleteTokens = deleteTokens;
	}

	public BigInteger getSoldTokens() {
		return soldTokens;
	}

	public void setSoldTokens(BigInteger soldTokens) {
		this.soldTokens = soldTokens;
	}

	public BigInteger getBalance() {
		return balance;
	}

	public void setBalance(BigInteger balance) {
		this.balance = balance;
	}

	public BigInteger getGasValue() {
		return gasValue;
	}

	public void setGasValue(BigInteger gasValue) {
		this.gasValue = gasValue;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getTokenAddress() {
		return tokenAddress;
	}

	public void setTokenAddress(String tokenAddress) {
		this.tokenAddress = tokenAddress;
	}

	public BigInteger getTokenBalance() {
		return tokenBalance;
	}

	public void setTokenBalance(BigInteger tokenBalance) {
		this.tokenBalance = tokenBalance;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public BigInteger getTokens() {
		return tokens;
	}

	public void setTokens(BigInteger tokens) {
		this.tokens = tokens;
	}

	public BigDecimal getMainBalance() {
		return mainBalance;
	}

	public void setMainBalance(BigDecimal mainBalance) {
		this.mainBalance = mainBalance;
	}

	public BigInteger getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(BigInteger initialValue) {
		this.initialValue = initialValue;
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public String getCoinSymbol() {
		return coinSymbol;
	}

	public void setCoinSymbol(String coinSymbol) {
		this.coinSymbol = coinSymbol;
	}

	public BigInteger getDecimalUnits() {
		return decimalUnits;
	}

	public void setDecimalUnits(BigInteger decimalUnits) {
		this.decimalUnits = decimalUnits;
	}

	public String getCentralAdmin() {
		return centralAdmin;
	}

	public void setCentralAdmin(String centralAdmin) {
		this.centralAdmin = centralAdmin;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getWalletPassword() {
		return walletPassword;
	}

	public void setWalletPassword(String walletPassword) {
		this.walletPassword = walletPassword;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	public BigInteger getTotaltokenBalance() {
		return TotaltokenBalance;
	}

	public void setTotaltokenBalance(BigInteger totaltokenBalance) {
		TotaltokenBalance = totaltokenBalance;
	}

	public BigInteger getSelledTokens() {
		return selledTokens;
	}

	public void setSelledTokens(BigInteger selledTokens) {
		this.selledTokens = selledTokens;
	}

	public BigInteger getTotalUserCount() {
		return totalUserCount;
	}

	public void setTotalUserCount(BigInteger totalUserCount) {
		this.totalUserCount = totalUserCount;
	}

	public BigInteger getCrowdSaleTokenBalance() {
		return crowdSaleTokenBalance;
	}

	public void setCrowdSaleTokenBalance(BigInteger crowdSaleTokenBalance) {
		this.crowdSaleTokenBalance = crowdSaleTokenBalance;
	}

	public BigInteger getCrowdSaleSoldTokens() {
		return crowdSaleSoldTokens;
	}

	public void setCrowdSaleSoldTokens(BigInteger crowdSaleSoldTokens) {
		this.crowdSaleSoldTokens = crowdSaleSoldTokens;
	}

	public BigInteger getTransferTokenBalance() {
		return transferTokenBalance;
	}

	public void setTransferTokenBalance(BigInteger transferTokenBalance) {
		this.transferTokenBalance = transferTokenBalance;
	}

	public BigDecimal getRequestToken() {
		return requestToken;
	}

	public void setRequestToken(BigDecimal requestToken) {
		this.requestToken = requestToken;
	}

	public BigInteger getMintedAmount() {
		return mintedAmount;
	}

	public void setMintedAmount(BigInteger mintedAmount) {
		this.mintedAmount = mintedAmount;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	
}
