package com.equocoin.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.equocoin.model.RegisterInfo;

public interface RegisterInfoRepository extends CrudRepository<RegisterInfo, Integer> {

	Integer countEquocoinUserInfoByMobileNo(String mobileNo);

	Integer countEquocoinUserInfoByEmailIdIgnoreCase(String emailId);

	List<RegisterInfo> findEquocoinUserInfoByMobileNoOrEmailId(String mobileNo, String emailId);

	RegisterInfo findEquocoinUserInfoByEmailId(Object attribute);

	RegisterInfo findEquocoinUserInfoByEmailIdAndPassword(String emailId, String password);

	RegisterInfo findEquocoinUserInfoByPassword(String password);

}
