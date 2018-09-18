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
@Table(name = "user_request_Info_Model")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserRequestCoinInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "from_address")
	@NotNull
	private String fromAddress;
	
	@Column(name = "to_address")
	@NotNull
	private String toAddress;
	
	@Column(name = "payment_mode")
	@NotNull
	private String paymentMode;
	
	@Column(name = "request_coin")
	@NotNull
	private Double requestCoin;
	
	@Column(name = "transcation_type")
	@NotNull
	private Integer transcationType;
	
	@Column(name = "created_Date")
	private Date createdDate;
	
	@Column(name = "sender_name")
	@NotNull
	private String senderName;
	
	@Column(name = "receiver_name")
	@NotNull
	private String receiveraName;

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	


	public Double getRequestCoin() {
		return requestCoin;
	}

	public void setRequestCoin(Double requestCoin) {
		this.requestCoin = requestCoin;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Integer getTranscationType() {
		return transcationType;
	}

	public void setTranscationType(Integer transcationType) {
		this.transcationType = transcationType;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getReceiveraName() {
		return receiveraName;
	}

	public void setReceiveraName(String receiveraName) {
		this.receiveraName = receiveraName;
	}
	
}