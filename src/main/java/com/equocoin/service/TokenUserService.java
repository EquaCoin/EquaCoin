package com.equocoin.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import com.equocoin.dto.TokenDTO;
import com.equocoin.model.RegisterInfo;

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

	public boolean TransferCoin(TokenDTO tokenDTO) throws Exception;

	public boolean sendToken(TokenDTO tokenDTO) throws Exception;

	public boolean validateMintTokenParams(TokenDTO tokenDTO);

	public boolean isAddressExist(TokenDTO tokenDTO) throws Exception;

	public String getRefund(TokenDTO tokenDTO) throws Exception;

	public String isValidTokenBalForTokenTransfer(TokenDTO tokenDTO) throws Exception;

	public boolean sendEquacoin(TokenDTO tokenDTO) throws Exception;

	// public boolean isValidTokenBal(TokenDTO tokenDTO) throws Exception;

	/* boolean getAdminTokenBalance(TokenDTO tokenDTO) throws Exception; */

	public boolean validEther(TokenDTO tokenDTO);

	public boolean amountTransfer(TokenDTO tokenDTO) throws Exception;

	void sendETHCoinPushNotificationFrom(RegisterInfo registerInfo, TokenDTO tokenDTO);

	void sendETHCoinPushNotificationTo(RegisterInfo registerInfo, TokenDTO tokenDTO);
	
	void requestcoinPushNotification(TokenDTO tokenDTO) throws Exception;

}
