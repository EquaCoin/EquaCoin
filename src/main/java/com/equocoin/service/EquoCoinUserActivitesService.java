package com.equocoin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.equocoin.dto.TokenDTO;

@Service
public interface EquoCoinUserActivitesService {

	public List<TokenDTO> transactionList(String walletAddress);
	
	
	

}
