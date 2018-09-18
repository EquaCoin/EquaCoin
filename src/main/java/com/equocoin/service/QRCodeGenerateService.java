package com.equocoin.service;

import java.util.Map;

import org.springframework.stereotype.Service;
@Service
public interface QRCodeGenerateService {

	@SuppressWarnings("rawtypes")
	public void createQRCode(String walletAddr, String filePath, String charset, Map hintMap, int i, int j);

	@SuppressWarnings("rawtypes")
	public void readQRCode(String filePath, String charset, Map hintMap);

}
