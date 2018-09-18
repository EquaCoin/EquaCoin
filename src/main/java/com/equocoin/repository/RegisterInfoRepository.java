package com.equocoin.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.equocoin.model.RegisterInfo;

public interface RegisterInfoRepository extends CrudRepository<RegisterInfo, Integer> {

	public Integer countEquocoinUserInfoByMobileNo(String mobileNo);

	public Integer countEquocoinUserInfoByEmailIdIgnoreCase(String emailId);

	public List<RegisterInfo> findEquocoinUserInfoByMobileNoOrEmailId(String mobileNo, String emailId);

	public RegisterInfo findEquocoinUserInfoByEmailId(Object attribute);

	public RegisterInfo findEquocoinUserInfoByEmailIdAndPassword(String emailId, String password);

	public RegisterInfo findEquocoinUserInfoByPassword(String password);

	public RegisterInfo findEquocoinUserInfoById(Integer userId);

	public RegisterInfo findById(Integer i);

	public RegisterInfo findEquacoinUserInfoByWalletAddress(String encryptWalletAddress);

	public RegisterInfo findEquacoinUserInfoByEtherWalletAddress(String encrypt);

	public List<RegisterInfo> findEquocoinUserInfoByType(Integer type);



}
