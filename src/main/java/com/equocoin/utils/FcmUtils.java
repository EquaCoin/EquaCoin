package com.equocoin.utils;


import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;

import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;

import java.net.MalformedURLException;

import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class FcmUtils {

	final static private String FCM_URL = "https://fcm.googleapis.com/fcm/send";
	
	private static final Logger LOG = LoggerFactory.getLogger(FcmUtils.class);


	/**
	 * Method to send push notification to Android FireBased Cloud messaging
	 * Server.

	 * @param tokenId
	 *            Generated and provided from Android Client Developer
	 * 
	 * @param server_key
	 *            Key which is Generated in FCM Server
	 * 
	 * @param message
	 *            which contains actual information.
	 */

//	public static void main(String[] args) {
//
//		// Just I am passed dummy information
//
//		String tokenId = "eSTV3K5FFPs:APA91bEEIy2IO1rIiFCvi6-5b7Y6GP_w3iohfDVraZ52wU8PmJMlFJXEt_ajiy_bFdvYUXfGWW5Hl2Ma90Sw6H9gJHqqODP-siveeGzHmzoFsF0WFDsjo5urBWOgW85TgUMJdnQI-7bKobF8nHeJcb7ehGrDaXyZeQ";
//
//		//String server_key = "AIzaSyD-B1yKZ0_wySOqfU8IPgMY7YkjFcF1byM";
//		String server_key = "AAAADZoBN0A:APA91bEPRZZIM29VNmDSV1jRrd6_w0CZqA1SwJtvD4tlcuApSS_0mUFLuaBKvxsYTlXlwEFYllTdfjNKt435LI4wI4flkX05SFg-RmwgUBz9QVmW3ueJ9lLPi5zWTgIJjkaDxqXo5kKVJIsTjglqykRSmbk2MTmPsQ";
//		String message = "Welcome to FCM Server push notification.";
//
//		// Method to send Push Notification
//		FcmUtils fc=new FcmUtils();
//		JSONObject jo = new JSONObject();
//		jo.put("title", "EquacoinRequestCoin Notification");
//
//		jo.put("body", message);
//		fc.send_FCM_Notification(tokenId, server_key, message, jo, "Notification");
//
//	}
	
	
	public  void send_FCM_Notification(String tokenId, String server_key, String message,JSONObject infoJson,String deviceType) {
		try {
			// Create URL instance.

			URL url = new URL(FCM_URL);

			// create connection.

			HttpURLConnection conn;

			conn = (HttpURLConnection) url.openConnection();

			conn.setUseCaches(false);

			conn.setDoInput(true);

			conn.setDoOutput(true);

			// set method as POST or GET

			conn.setRequestMethod("POST");

			
			// pass FCM server key

			conn.setRequestProperty("Authorization", "key=" + server_key);

			// Specify Message Format

			conn.setRequestProperty("Content-Type", "application/json");

			// Create JSON Object & pass value

			JSONObject json = new JSONObject();

			json.put("to", tokenId.trim());

			json.put(deviceType, infoJson);

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

			wr.write(json.toString());

			wr.flush();

			int status = 0;

			if (null != conn) {

				status = conn.getResponseCode();

			}

			if (status != 0) {
				if (status == 200) {

					// SUCCESS message

					BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

				} else if (status == 401) {

					// client side error

				} else if (status == 501) {

					// server side error

				} else if (status == 503) {

					// server side error
				}

			}

		} catch (MalformedURLException mlfexception) {

			// Prototcal Error

		} catch (IOException mlfexception) {

			// URL problem

		} catch (JSONException jsonexception) {

			// Message format error

		} catch (Exception exception) {

			// General Error or exception.

		}

	}

}

/*public class fcmPushNotification {

	public static void main(String[] args) {

		// Just I am passed dummy information

		String tokenId = "dcU-9koasqw:APA91bHdKS1Y_HvVjpqdBAM8WZ5d6rGhCzABMrqAqOD4zf0Uex2q2ZakWdO45KMlhZrnnpyetvmAAMp7lyy41CEoYBUQlfRNNmb50nZW-M8L1IMXi0rzHAHH3-JNBmnh_E-SC_L7skWn";

		//String server_key = "AIzaSyD-B1yKZ0_wySOqfU8IPgMY7YkjFcF1byM";
		String server_key = "AAAAZdDm9IU:APA91bGPg1v1Xs9QeASQmTAopT_JJp4ZlfyhBZ_Yys3BA7wd8jYupZh8ROhHJvrDteY39fqCZpHQZlT72N88I9fvmNuRsjTfdEkWkztFdYFHgpahdPwQxEFomxFgHsgbubBlA97Eg7UE";
		String message = "Welcome to FCM Server push notification.";

		// Method to send Push Notification

		FcmUtils.send_FCM_Notification(tokenId, server_key, message);

	}

}*/
