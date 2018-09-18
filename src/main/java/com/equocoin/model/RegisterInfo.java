package com.equocoin.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name = "Equocoin_USER_INFO")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RegisterInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "password")
	private String password;

	@Column(name = "Email_Id")
	private String emailId;
	@Column(name = "Mobile_No")
	private String mobileNo;

	@Column(name = "ISD_CODE")
	private String isdCode;

	@Column(name = "created_Date")
	private Date createdDate;

	@Column(name = "version")
	private Date version;

	@Column(name = "wallet_address")
	private String walletAddress;

	@Column(name = "wallet_password")
	private String walletPassword;

	@Column(name = "usertype")
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int type;
	
	@Column(name = "app_id")
	private String appId;
	
	@Column(name = "mobile_type")
	private String mobileType;

	@Column(name = "public_key")
	private String publicKey;
	
	@Column(name = "private_key")
	private String private_key;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "registerInfo", fetch = FetchType.LAZY)
	private Set<TransactionHistory> transactionHistoryInfos;
	
	@Column(name="ether_wallet_address")
	private String etherWalletAddress;
	
	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getVersion() {
		return version;
	}

	public void setVersion(Date version) {
		this.version = version;
	}

	public String getWalletPassword() {
		return walletPassword;
	}

	public void setWalletPassword(String walletPassword) {
		this.walletPassword = walletPassword;
	}

	public String getIsdCode() {
		return isdCode;
	}

	public void setIsdCode(String isdCode) {
		this.isdCode = isdCode;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMobileType() {
		return mobileType;
	}

	public void setMobileType(String mobileType) {
		this.mobileType = mobileType;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPrivate_key() {
		return private_key;
	}

	public void setPrivate_key(String private_key) {
		this.private_key = private_key;
	}

	public Set<TransactionHistory> getTransactionHistoryInfos() {
		return transactionHistoryInfos;
	}

	public void setTransactionHistoryInfos(Set<TransactionHistory> transactionHistoryInfos) {
		this.transactionHistoryInfos = transactionHistoryInfos;
	}

	public String getEtherWalletAddress() {
		return etherWalletAddress;
	}

	public void setEtherWalletAddress(String etherWalletAddress) {
		this.etherWalletAddress = etherWalletAddress;
	}

	
}
