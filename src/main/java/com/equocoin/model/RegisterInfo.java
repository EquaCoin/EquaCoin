package com.equocoin.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
	@NotNull
	private String password;

	@Column(name = "Email_Id")
	@NotNull
	private String emailId;
	@Column(name = "Mobile_No")
	@NotNull
	private String mobileNo;
	
	@NotNull
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

	@Column(name = "type")
	@NotNull
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int type;
	
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
	
	

}
