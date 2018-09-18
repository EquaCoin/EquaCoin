package com.equocoin.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.equocoin.dto.TokenDTO;
import com.equocoin.model.TransactionHistory;
import com.equocoin.repository.TransactionInfoRepository;
import com.equocoin.service.EquoCoinUserActivitesService;
import com.equocoin.utils.EquocoinUtils;

@Service
public class EquoCoinUserActivitesServiceImpl implements EquoCoinUserActivitesService {

	@Autowired
	private TransactionInfoRepository transactionInfoRepository;

	@Autowired
	private EquocoinUtils equocoinUtils;

	public List<TokenDTO> transactionList(String walletAddress) {

		List<TokenDTO> transactionList = new ArrayList<TokenDTO>();
		List<TransactionHistory> transactionInfos = (List<TransactionHistory>) transactionInfoRepository
				.findByFromAddressOrToAddressOrderByCreatedDateDesc(walletAddress, walletAddress);
		
		for (TransactionHistory transactionInfo : transactionInfos) {
			TokenDTO transaction = equocoinUtils.listTransactions(transactionInfo);
			transactionList.add(transaction);
		}

		return transactionList;

	}

}
