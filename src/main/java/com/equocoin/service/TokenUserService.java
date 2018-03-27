package com.equocoin.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;

import com.equocoin.dto.TokenDTO;

@Service
public interface TokenUserService {
	public boolean TokenCreate(TokenDTO tokenDTO);

	public boolean saveTokenGenerationInfo(TokenDTO tokenDTO);

	public boolean validateCentraladmin(TokenDTO tokenDTO);

	public boolean checkMainbalance(TokenDTO tokenDTO);

	// public boolean transferAmount(TokenDTO tokenDTO);

	public boolean TransferToken(TokenDTO tokenDTO);

	public boolean TransferAdminship(TokenDTO tokenDTO);

	public List<TokenDTO> listToken();

	public boolean validAmount(TokenDTO tokenDTO, HttpServletRequest request);

	boolean isTokenCancel(TokenDTO tokenDTO) throws Exception;

	// public boolean validateTokenBalancePrams(TokenDTO tokenDTO);

	public boolean getwalletBalancePrams(TokenDTO tokenDTO) throws Exception;

	boolean getUserTokenBalance(TokenDTO tokenDTO) throws Exception;

	public boolean getTransferAccountParams(TokenDTO tokenDTO) throws Exception;

	public boolean TransferCoin(TokenDTO tokenDTO) throws  Exception;

	public boolean sendToken(TokenDTO tokenDTO)throws Exception;

	public boolean validateMintTokenParams(TokenDTO tokenDTO);

	//public boolean isValidTokenBal(TokenDTO tokenDTO) throws Exception;

	/* boolean getAdminTokenBalance(TokenDTO tokenDTO) throws Exception; */

}
