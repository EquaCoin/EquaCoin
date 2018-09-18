package com.equocoin.model;

	import java.io.Serializable;
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
	@Table(name = "coinsinfo")
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public class CoinsInfo  implements Serializable{
	private static final long serialVersionUID = 1L;
		
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private Integer id;
		@Column(name = "equacoin")
		@NotNull
		private Integer equacoin;

				
		@Column(name = "eth")
		@NotNull
		private Double eth;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		
		
		public Integer getEquacoin() {
			return equacoin;
		}

		public void setEquacoin(Integer equacoin) {
			this.equacoin = equacoin;
		}

		public Double getEth() {
			return eth;
		}

		public void setEth(Double eth) {
			this.eth = eth;
		}

		



	}


