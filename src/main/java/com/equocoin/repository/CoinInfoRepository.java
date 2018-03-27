package com.equocoin.repository;

import java.util.List;



import org.springframework.data.repository.CrudRepository;

import com.equocoin.model.CoinsInfo;



public interface CoinInfoRepository extends CrudRepository<CoinsInfo, Integer> {
	
 public List<CoinsInfo> findAll();
}
