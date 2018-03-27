package com.equacoin.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

//import static org.web3j.generated.HumanStandardToken.TransferEventResponse;

public class Test {

	/*public static void main(String[] args) throws Exception {*/
		// TODO Auto-generated method stub

		/*
		 * BigInteger GAS = BigInteger.valueOf(4300000); BigInteger GAS_PRICE =
		 * BigInteger.valueOf(2000000); Web3j web3j = Web3j.build(new
		 * HttpService());
		 * 
		 * Credentials credentials = WalletUtils.loadCredentials("User0463",
		 * "E://Ethereum//private-network//keystore//UTC--2017-12-15T02-25-42.161888600Z--e64384ca6dd370234a3c372b844efe2771049c0b"
		 * );
		 * 
		 * Crowdsale crowdSale =
		 * Crowdsale.load("0x99618EE23d442AB62Ff4E9184319A1F8aECc7bA5",web3j ,
		 * credentials, GAS_PRICE, GAS); BigInteger uint =
		 * crowdSale.state().send(); System.out.println("state"+uint);
		 * TransactionReceipt transactionReceipt =
		 * crowdSale.isCrowdSaleEnd(true).send(); List<StatusEventResponse>
		 * StatusEventResponse = crowdSale.getStatusEvents(transactionReceipt);
		 * for(StatusEventResponse StatusEventResponses: StatusEventResponse)
		 */

		/*
		 * long unixSeconds = 1515110400; // convert seconds to milliseconds
		 * Date date = new Date(unixSeconds*1000L); // the format of your date
		 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		 * // give a timezone reference for formatting (see comment at the
		 * bottom) sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); String
		 * formattedDate = sdf.format(date); System.out.println(date);
		 * 
		 * 
		 * SimpleDateFormat sdff = new
		 * SimpleDateFormat("EEE MMM d HH:mm:ss yyyy"); Date theDate =
		 * sdff.parse ("Fri Jan 05 05:30:00  2018"); long minutes2 =
		 * theDate.getTime() / 60000; System.out.println(minutes2);
		 */

		/*
		 * Date date = new Date(); Format formatter = new
		 * SimpleDateFormat("EEE MMM d HH:mm:ss yyyy"); String s =
		 * formatter.format(date); System.out.println(s);
		 * 
		 * long minutes2 = date.getTime() / 60000; System.out.println(minutes2);
		 */

		/*Date startDate = new Date("Mon Jan 08 16:40:00  2018");
		Date endDate = new Date("Tue Jan 09 14:52:00  2018");
		Date currentDate = new Date();
		System.out.println("currentDate " + currentDate);
		long duration = endDate.getTime() - currentDate.getTime();

		long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
		long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);

		System.out.println("diffInMinutes " +diffInMinutes);*/

		

	//}

}
