package com.equocoin.utils;


import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import com.equocoin.handlesolidity.SolidityHandler;




@Service
@EnableScheduling
public class CrowdSaleScheduler {
	
	private static final Logger LOG = LoggerFactory.getLogger(CrowdSaleScheduler.class);
	
	@SuppressWarnings("unused")
	@Autowired
	private SolidityHandler solidityHandler;

	@SuppressWarnings("unused")
	@Autowired
	private Environment env;
	
	@SuppressWarnings("unused")
	@Autowired
	private EquocoinUtils equocoinUtils;

	
	 //@Scheduled(cron = "30 * * * * *")
		public void cronTask() throws Exception {
		 
		 LOG.info("Cron crowdsale schecduler : "+new Date() + " ends");
		 
//		 solidityHandler.isCrowdsaleEnd();
	 }

}
