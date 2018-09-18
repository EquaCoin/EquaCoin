package com.equocoin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "qrcode")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class QRCode {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "QRKey")
	@NotNull
	private String qrKey;

	@Column(name = "QRCodeValue")
	@NotNull
	private String qrcodeValue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQrKey() {
		return qrKey;
	}

	public void setQrKey(String qrKey) {
		this.qrKey = qrKey;
	}

	public String getQrcodeValue() {
		return qrcodeValue;
	}

	public void setQrcodeValue(String qrcodeValue) {
		this.qrcodeValue = qrcodeValue;
	}
}
