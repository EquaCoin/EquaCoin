package com.equocoin.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "proposalInfo")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProposalInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name="proposal_No")
	private BigInteger proposalNo;
	
	@Column(name="user_Id")
	private Integer userId;
	
	@Column(name="db_path")
	private String dbPath;
	
	@Column(name = "created_Date")
	private Date createdDate;
	
	@Column(name = "proposal_TransferStatus")
	private int proposalTransferStatus;
	
	@Column(name = "proposal_Document")
	private String proposalDocumentName;
	
	@Column(name = "proposal_DetailsLink")
	private String proposalDetailsLink;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BigInteger getProposalNo() {
		return proposalNo;
	}
	public void setProposalNo(BigInteger proposalNo) {
		this.proposalNo = proposalNo;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getDbPath() {
		return dbPath;
	}
	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	public int getProposalTransferStatus() {
		return proposalTransferStatus;
	}
	public void setProposalTransferStatus(Integer proposalTransferStatus) {
		this.proposalTransferStatus = proposalTransferStatus;
	}
	public String getProposalDocumentName() {
		return proposalDocumentName;
	}
	public void setProposalDocumentName(String proposalDocumentName) {
		this.proposalDocumentName = proposalDocumentName;
	}
	public String getProposalDetailsLink() {
		return proposalDetailsLink;
	}
	public void setProposalDetailsLink(String proposalDetailsLink) {
		this.proposalDetailsLink = proposalDetailsLink;
	}
	
	
	
}
