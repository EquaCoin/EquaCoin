package com.equocoin.dto;

import java.math.BigInteger;
import java.util.Date;

public class CrowdSaleProposalDTO {

	private String proposalCreatedBy;
	private String proposalTitle;
	private String ProposalCategory;
	private String proposalDetails;
	private BigInteger proposalAmount;
	private Date startDate;
	private Date endDate;
	private BigInteger like;
	private BigInteger disLike;
	private String proposalAddress;
	private BigInteger deadLine;
	private String mainAccount;
	private BigInteger proposalNumber;
	private Boolean voted;
	private String startDates;
	private String endDates;
	private String sessionId;

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

}
