package com.equocoin.admin.service;

import org.springframework.stereotype.Service;

import com.equocoin.dto.TokenDTO;

@Service
public interface AdminService {

	public boolean isAdminExists(TokenDTO tokenDTO) throws Exception;

	public boolean getAdminPendingTokenBalance(TokenDTO tokenDTO) throws Exception;

	public boolean getAdminTotalTokenBalance(TokenDTO tokenDTO) throws Exception;

	public boolean getAdminSoldTokenBalance(TokenDTO tokenDTO) throws Exception;

	public boolean getUserCount(TokenDTO tokenDTO);

	public boolean checkTokenBalance(TokenDTO tokenDTO) throws Exception;
	

}
