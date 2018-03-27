package com.equocoin.repository;

import org.springframework.data.repository.CrudRepository;

import com.equocoin.model.TokenInfo;

public interface TokenInfoRepository extends CrudRepository<TokenInfo, Integer> {

	TokenInfo findByTokenAddress(String tokenAddress);

	TokenInfo findByCoinSymbol(String string);

}
