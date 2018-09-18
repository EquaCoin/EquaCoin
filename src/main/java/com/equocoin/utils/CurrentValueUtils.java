package com.equocoin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CurrentValueUtils {
	private static final Logger LOG = LoggerFactory.getLogger(CurrentValueUtils.class);

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public Double getBitcoinValueForOneINR() throws JSONException, IOException {
		
		LOG.info("Inside getBitcoinValueForOneINR Start---->:::"+ new java.util.Date());
		
		JSONObject json = readJsonFromUrl("https://api.coinbase.com/v2/prices/BTC-INR/spot");
		JSONObject json2 = (JSONObject) json.get("data");
		String amount = (String) json2.get("amount");
		BigDecimal Dollars = new BigDecimal(amount);

		BigDecimal btcValueFor1CDG = new BigDecimal("0.03");
		BigDecimal decimal = new BigDecimal("100");

		BigDecimal percent = (Dollars.multiply(btcValueFor1CDG)).divide(decimal);
		LOG.info("Inside getBitcoinValueForOneINR End---->:::"+ new java.util.Date());
		
		return percent.doubleValue();
	}

	public Double getBitcoinValueForOneDollar() throws JSONException, IOException {
		JSONObject json = readJsonFromUrl("https://api.coinbase.com/v2/prices/BTC-INR/spot");
		JSONObject json2 = (JSONObject) json.get("data");
		System.out.println(json.toString());
		System.out.println(json.get("data"));
		System.out.println(json2.get("amount"));
		String amount = (String) json2.get("amount");
		BigDecimal Dollars = new BigDecimal(amount);
		System.out.println("Big Decimal: " + Dollars);
		System.out.println("Dollars: " + Dollars.doubleValue());

		BigDecimal btcValueFor1CDG = new BigDecimal("0.03");
		BigDecimal decimal = new BigDecimal("100");

		BigDecimal percent = (Dollars.multiply(btcValueFor1CDG)).divide(decimal);
		return percent.doubleValue();
	}

	public Double getEtherValueForOneINR() throws JSONException, IOException {
		LOG.info("Inside getEtherValueForOneINR Start---->:::"+ new java.util.Date());
		JSONObject json = readJsonFromUrl("https://api.coinbase.com/v2/prices/ETH-INR/spot");
		JSONObject json2 = (JSONObject) json.get("data");
		String amount = (String) json2.get("amount");
		BigDecimal Dollars = new BigDecimal(amount);
		LOG.info("Inside getEtherValueForOneINR End---->:::"+ new java.util.Date());
		return Dollars.doubleValue();
	}
	public BigDecimal getEtherValueForOneUSD() throws JSONException, IOException {
		LOG.info("Inside getEtherValueForOneUSD Start---->:::"+ new java.util.Date());
		JSONObject json = readJsonFromUrl("https://api.coinbase.com/v2/prices/ETH-USD/spot");
		JSONObject json2 = (JSONObject) json.get("data");
		String amount = (String) json2.get("amount");
		BigDecimal Dollars = new BigDecimal(amount);
		LOG.info("Inside getEtherValueForOneUSD End---->:::"+ new java.util.Date());
		return Dollars;
	}
	public BigDecimal getBTCValueForOneUSD() throws JSONException, IOException {
		LOG.info("Inside getBTCValueForOneUSD Start---->:::"+ new java.util.Date());
		JSONObject json = readJsonFromUrl("https://api.coinbase.com/v2/prices/BTC-USD/spot");
		JSONObject json2 = (JSONObject) json.get("data");
		String amount = (String) json2.get("amount");
		BigDecimal Dollars = new BigDecimal(amount);
		LOG.info("Inside getBTCValueForOneUSD End---->:::"+ new java.util.Date());
		return Dollars;
	}

}
