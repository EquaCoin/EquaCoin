package com.equocoin.repository;


import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.equocoin.model.MintInfo;

public interface MintInfoRepository extends CrudRepository<MintInfo, Integer> {

	

	

	public List<MintInfo> findAll();
	
}
