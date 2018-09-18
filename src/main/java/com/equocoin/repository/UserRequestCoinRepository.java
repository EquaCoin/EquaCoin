package com.equocoin.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.equocoin.model.UserRequestCoinInfo;



@Service
public interface UserRequestCoinRepository extends CrudRepository<UserRequestCoinInfo, Integer> {

	List<UserRequestCoinInfo> findByToAddressOrderByCreatedDateDesc(String emailId);




}
