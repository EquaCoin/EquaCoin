package com.equocoin.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.equocoin.model.QRCode;



@Service
public interface QRCodeRepository extends CrudRepository<QRCode, Integer> {
	public QRCode findQRCodeByQrKey(String qrKey);


}
