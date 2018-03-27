package com.equocoin.repository;

import org.springframework.data.repository.CrudRepository;

import com.equocoin.model.Config;

public interface ConfigInfoRepository extends CrudRepository<Config, Integer> {
	
	public Config findConfigByConfigKey(String configKey);

}
