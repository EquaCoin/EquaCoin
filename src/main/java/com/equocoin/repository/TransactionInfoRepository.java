package com.equocoin.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.equocoin.model.TransactionHistory;

public interface TransactionInfoRepository extends CrudRepository<TransactionHistory, Integer> {

	

//	public List<TransactionHistory> findByFromAddressOrToAddress(String walletAddress, String walletAddress2);

	public List<TransactionHistory> findByFromAddressOrToAddressOrderByCreatedDateDesc(String walletAddress,
			String walletAddress2);

	//public List<TransactionHistory> findByFromAddressOrToAddressDesc(String walletAddress, String walletAddress2);
	
    public List<TransactionHistory> findByPaymentModeAndStatusAndFromAddressAndToAddress(String paymentMode, String status, String fromAddress, String toAddress);
	
	
	public List<TransactionHistory> findByPaymentModeAndStatusAndFromAddressOrToAddress(String paymentMode, String status, String fromAddress, String toAddress);
	
	
	

}
