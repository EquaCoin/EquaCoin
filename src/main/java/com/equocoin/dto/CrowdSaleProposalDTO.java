package com.equocoin.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.web3j.crypto.Credentials;

public class CrowdSaleProposalDTO {
	
	private Integer id;
	
	private String amountAproval;
	
	private String proposalCreatedBy;
	
	private String proposalTitle;
	
	private String ProposalCategory;
	
	private String proposalDetails;
	
	private BigInteger proposalAmount;
	
	private Date startDate;
	
	private Date endDate;
	
	private BigInteger like;
	
	private BigInteger disLike;
	
	private BigInteger approve;
	
	private BigInteger reject;
	
	private String proposalAddress;
	
	private BigInteger deadLine;
	
	private String mainAccount;
	
	private BigInteger proposalNumber;
	
	private Boolean voted;
	
	private Boolean accepted;
	
	private String startDates;
	
	private String endDates;
	
	private String sessionId;
	
	private Integer userId;
	
	private String toAddress;
	
	private String walletAddress;
	
	private String walletPassword;
	
	private BigInteger proposalId;
	
	private BigDecimal mainBalance;
	
	private String status;
	
	private String proposalDOC;
	
	private String paymentMode;
	
	private String fromAddress;
	
	private BigInteger amount;
	
	private Integer DACstatus;
	
	private Integer proposalTransactionStatus;
	
	private String credentialAddress;
	
	private Integer dacUserCount;
	
	private String encryptWalletAddress;
	
	private BigDecimal etherAmount;
	
	private Integer proposalTransferStatus;
	
	private Integer proposalCount;
	
	private Credentials credentials;
	
	private String aprovalAmount;
	
	private Double aproveProposalAmount;
	
	private BigInteger eqcAmount;
	
	private BigDecimal equaAmount;
	
	private String message;
	
	private BigInteger likeCount;
	
	private BigInteger dislikeCount;
	
	private BigInteger totalVoteCount;
	
	private Float percentage;
	
	private Float failPercentage;
	
	private String approveOrRejectAmount;
	
	private String fileType;
	
	private String emailId;
	
	private String proposalCreatedByName;
	
	private String proposalDetailsLink;
	
	private String proposalFundingStatus;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Double getAproveProposalAmount() {
		return aproveProposalAmount;
	}

	public void setAproveProposalAmount(Double aproveProposalAmount) {
		this.aproveProposalAmount = aproveProposalAmount;
	}

	public BigInteger getEqcAmount() {
		return eqcAmount;
	}

	public void setEqcAmount(BigInteger eqcAmount) {
		this.eqcAmount = eqcAmount;
	}

	public String getAmountAproval() {
		return amountAproval;
	}

	public void setAmountAproval(String amountAproval) {
		this.amountAproval = amountAproval;
	}

	public String getAprovalAmount() {
		return aprovalAmount;
	}

	public void setAprovalAmount(String aprovalAmount) {
		this.aprovalAmount = aprovalAmount;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public Integer getProposalCount() {
		return proposalCount;
	}

	public void setProposalCount(Integer proposalCount) {
		this.proposalCount = proposalCount;
	}

	public Integer getProposalTransferStatus() {
		return proposalTransferStatus;
	}

	public void setProposalTransferStatus(Integer proposalTransferStatus) {
		this.proposalTransferStatus = proposalTransferStatus;
	}

	public BigDecimal getEtherAmount() {
		return etherAmount;
	}

	public void setEtherAmount(BigDecimal etherAmount) {
		this.etherAmount = etherAmount;
	}

	public String getEncryptWalletAddress() {
		return encryptWalletAddress;
	}

	public void setEncryptWalletAddress(String encryptWalletAddress) {
		this.encryptWalletAddress = encryptWalletAddress;
	}

	public Integer getDacUserCount() {
		return dacUserCount;
	}

	public void setDacUserCount(Integer dacUserCount) {
		this.dacUserCount = dacUserCount;
	}

	public String getCredentialAddress() {
		return credentialAddress;
	}

	public void setCredentialAddress(String credentialAddress) {
		this.credentialAddress = credentialAddress;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getProposalDOC() {
		return proposalDOC;
	}

	public void setProposalDOC(String proposalDOC) {
		this.proposalDOC = proposalDOC;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getMainBalance() {
		return mainBalance;
	}

	public void setMainBalance(BigDecimal mainBalance) {
		this.mainBalance = mainBalance;
	}

	public BigInteger getProposalId() {
		return proposalId;
	}

	public void setProposalId(BigInteger proposalId) {
		this.proposalId = proposalId;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	public String getWalletPassword() {
		return walletPassword;
	}

	public void setWalletPassword(String walletPassword) {
		this.walletPassword = walletPassword;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getMainAccount() {
		return mainAccount;
	}

	public void setMainAccount(String mainAccount) {
		this.mainAccount = mainAccount;
	}

	public BigInteger getDeadLine() {
		return deadLine;
	}

	public void setDeadLine(BigInteger deadLine) {
		this.deadLine = deadLine;
	}

	public String getProposalAddress() {
		return proposalAddress;
	}

	public void setProposalAddress(String proposalAddress) {
		this.proposalAddress = proposalAddress;
	}

	public String getProposalTitle() {
		return proposalTitle;
	}

	public void setProposalTitle(String proposalTitle) {
		this.proposalTitle = proposalTitle;
	}

	public String getProposalCategory() {
		return ProposalCategory;
	}

	public void setProposalCategory(String proposalCategory) {
		ProposalCategory = proposalCategory;
	}

	public BigInteger getProposalAmount() {
		return proposalAmount;
	}

	public void setProposalAmount(BigInteger proposalAmount) {
		this.proposalAmount = proposalAmount;
	}

	public String getProposalCreatedBy() {
		return proposalCreatedBy;
	}

	public void setProposalCreatedBy(String proposalCreatedBy) {
		this.proposalCreatedBy = proposalCreatedBy;
	}

	public String getProposalDetails() {
		return proposalDetails;
	}

	public void setProposalDetails(String proposalDetails) {
		this.proposalDetails = proposalDetails;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigInteger getLike() {
		return like;
	}

	public void setLike(BigInteger like) {
		this.like = like;
	}

	public BigInteger getDisLike() {
		return disLike;
	}

	public void setDisLike(BigInteger disLike) {
		this.disLike = disLike;
	}

	public BigInteger getProposalNumber() {
		return proposalNumber;
	}

	public void setProposalNumber(BigInteger proposalNumber) {
		this.proposalNumber = proposalNumber;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Boolean getVoted() {
		return voted;
	}

	public void setVoted(Boolean voted) {
		this.voted = voted;
	}

	public String getStartDates() {
		return startDates;
	}

	public void setStartDates(String startDates) {
		this.startDates = startDates;
	}

	public String getEndDates() {
		return endDates;
	}

	public void setEndDates(String endDates) {
		this.endDates = endDates;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public BigInteger getReject() {
		return reject;
	}

	public void setReject(BigInteger reject) {
		this.reject = reject;
	}

	public BigInteger getApprove() {
		return approve;
	}

	public void setApprove(BigInteger approve) {
		this.approve = approve;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public BigInteger getAmount() {
		return amount;
	}

	public void setAmount(BigInteger amount) {
		this.amount = amount;
	}

	public Integer getDACstatus() {
		return DACstatus;
	}

	public void setDACstatus(Integer dACstatus) {
		DACstatus = dACstatus;
	}

	public Integer getProposalTransactionStatus() {
		return proposalTransactionStatus;
	}

	public void setProposalTransactionStatus(Integer proposalTransactionStatus) {
		this.proposalTransactionStatus = proposalTransactionStatus;
	}

	public BigDecimal getEquaAmount() {
		return equaAmount;
	}

	public void setEquaAmount(BigDecimal equaAmount) {
		this.equaAmount = equaAmount;
	}

	public BigInteger getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(BigInteger likeCount) {
		this.likeCount = likeCount;
	}

	public BigInteger getDislikeCount() {
		return dislikeCount;
	}

	public void setDislikeCount(BigInteger dislikeCount) {
		this.dislikeCount = dislikeCount;
	}

	public BigInteger getTotalVoteCount() {
		return totalVoteCount;
	}

	public void setTotalVoteCount(BigInteger totalVoteCount) {
		this.totalVoteCount = totalVoteCount;
	}

	public Float getPercentage() {
		return percentage;
	}

	public void setPercentage(Float percentage) {
		this.percentage = percentage;
	}

	public Float getFailPercentage() {
		return failPercentage;
	}

	public void setFailPercentage(Float failPercentage) {
		this.failPercentage = failPercentage;
	}

	public String getApproveOrRejectAmount() {
		return approveOrRejectAmount;
	}

	public void setApproveOrRejectAmount(String approveOrRejectAmount) {
		this.approveOrRejectAmount = approveOrRejectAmount;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getProposalCreatedByName() {
		return proposalCreatedByName;
	}

	public void setProposalCreatedByName(String proposalCreatedByName) {
		this.proposalCreatedByName = proposalCreatedByName;
	}

	public String getProposalDetailsLink() {
		return proposalDetailsLink;
	}

	public void setProposalDetailsLink(String proposalDetailsLink) {
		this.proposalDetailsLink = proposalDetailsLink;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProposalFundingStatus() {
		return proposalFundingStatus;
	}

	public void setProposalFundingStatus(String proposalFundingStatus) {
		this.proposalFundingStatus = proposalFundingStatus;
	}
	
	

}
