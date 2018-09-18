package com.equocoin.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.equocoin.model.RegisterInfo;

public class TokenDTO {

	private Integer id;
	private BigInteger initialValue;
	private String coinName;
	private String coinSymbol;
	private BigInteger decimalUnits;
	private String centralAdmin;
	private BigDecimal mainBalance;
	private Double tokens;
	private String toAddress;
	private String fromAddress;
	private BigDecimal amount;
	private BigDecimal transferAmount;
	private BigInteger gasValue;
	private String walletAddress;
	private String walletPassword;
	private Double tokenBalance;
	private Double TotaltokenBalance;
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
	private Double requestToken;
	private Double soldTokens;
    private Double mintedAmount;
    private Double deleteTokens;
    private String paymentMode;
    private String mintStatus;
    private String transactionStatus;
    private String sender;
    private String receiver;
    private Double transferAmounts;
    private Integer gasPrice;
    private RegisterInfo registerInfo;
	private Long tokenRequestCount;
	private Double requestCoin;
    private String message;
	public Double getSoldTokens() {
		return soldTokens;
	}

	public void setSoldTokens(Double soldTokens) {
		this.soldTokens = soldTokens;
	}

	public Double getDeleteTokens() {
		return deleteTokens;
	}

	public void setDeleteTokens(Double deleteTokens) {
		this.deleteTokens = deleteTokens;
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



	public Double getTokenBalance() {
		return tokenBalance;
	}

	public void setTokenBalance(Double tokenBalance) {
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

	
	public Double getTokens() {
		return tokens;
	}

	public void setTokens(Double tokens) {
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

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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


	public Double getTotaltokenBalance() {
		return TotaltokenBalance;
	}

	public void setTotaltokenBalance(Double totaltokenBalance) {
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



	public Double getRequestToken() {
		return requestToken;
	}

	public void setRequestToken(Double requestToken) {
		this.requestToken = requestToken;
	}


	public Double getMintedAmount() {
		return mintedAmount;
	}

	public void setMintedAmount(Double mintedAmount) {
		this.mintedAmount = mintedAmount;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getMintStatus() {
		return mintStatus;
	}

	public void setMintStatus(String mintStatus) {
		this.mintStatus = mintStatus;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Double getTransferAmounts() {
		return transferAmounts;
	}

	public void setTransferAmounts(Double transferAmounts) {
		this.transferAmounts = transferAmounts;
	}

	public Integer getGasPrice() {
		return gasPrice;
	}

	public void setGasPrice(Integer gasPrice) {
		this.gasPrice = gasPrice;
	}

	public RegisterInfo getRegisterInfo() {
		return registerInfo;
	}

	public void setRegisterInfo(RegisterInfo registerInfo) {
		this.registerInfo = registerInfo;
	}

	public Long getTokenRequestCount() {
		return tokenRequestCount;
	}

	public void setTokenRequestCount(Long tokenRequestCount) {
		this.tokenRequestCount = tokenRequestCount;
	}

	public Double getRequestCoin() {
		return requestCoin;
	}

	public void setRequestCoin(Double requestCoin) {
		this.requestCoin = requestCoin;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
	
}
