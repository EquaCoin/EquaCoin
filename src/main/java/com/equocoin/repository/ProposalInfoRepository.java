package com.equocoin.repository;

import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;

import com.equocoin.model.ProposalInfo;

public interface ProposalInfoRepository extends CrudRepository<ProposalInfo, Integer> {

	public ProposalInfo findInfoByProposalNo(BigInteger bigInteger);

	

}
