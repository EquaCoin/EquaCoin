
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
	@Table(name = "mintinfo")
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public class MintInfo  implements Serializable{
	private static final long serialVersionUID = 1L;
		
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private Integer id;
		@Column(name = "fromAddress")
		@NotNull
		private String fromAddress;
				
		@Column(name = "mintToken")
		@NotNull
		private Double mintToken;
		
		@Column(name = "created_Date")
		private Date createdDate;
		
		@Column(name = "status")
		//@NotNull
		private String status;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getFromAddress() {
			return fromAddress;
		}

		public void setFromAddress(String formAddress) {
			this.fromAddress = formAddress;
		}

		

	
		public Double getMintToken() {
			return mintToken;
		}

		public void setMintToken(Double mintToken) {
			this.mintToken = mintToken;
		}

		public Date getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(Date createdDate) {
			this.createdDate = createdDate;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		
		



	}




