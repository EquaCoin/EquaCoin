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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("unused")
@Entity
@Table(name = "TOKEN_INFO")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TokenInfo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	// @NotNull
	private Integer id;

	@Column(name = "initial_Supply")
	@NotNull
	private BigInteger initialValue;
	
	@Column(name = "token_Name")
	@NotNull
	private String coinName;
	
	@Column(name = "token_Symbol")
	@NotNull
	private String coinSymbol;
	
	@Column(name = "decimal_Units")
	@NotNull
	private double decimalUnits;
	
	@Column(name = "central_Admin")
	@NotNull
	private String centralAdmin;
	
	
	@Column(name = "token_Address")
	@NotNull
	private String tokenAddress;
	
	
	@Column(name = "created_Date")
	private Date createdDate;
	
	
	@Column(name = "version")
	private Date version;


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	
	public double getDecimalUnits() {
		return decimalUnits;
	}


	public void setDecimalUnits(double decimalUnits) {
		this.decimalUnits = decimalUnits;
	}


	

	public String getCentralAdmin() {
		return centralAdmin;
	}


	public void setCentralAdmin(String centralAdmin) {
		this.centralAdmin = centralAdmin;
	}


	public String getTokenAddress() {
		return tokenAddress;
	}


	public void setTokenAddress(String tokenAddress) {
		this.tokenAddress = tokenAddress;
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




}
