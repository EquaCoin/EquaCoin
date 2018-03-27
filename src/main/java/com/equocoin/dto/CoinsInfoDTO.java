package com.equocoin.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CoinsInfoDTO {
	
	public Integer id;
	public BigInteger equacoin;
	public Double eth;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BigInteger getEquacoin() {
		return equacoin;
	}
	public void setEquacoin(BigInteger equacoin) {
		this.equacoin = equacoin;
	}
	public Double getEth() {
		return eth;
	}
	public void setEth(Double eth) {
		this.eth = eth;
	}
	

}
