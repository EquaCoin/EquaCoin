package com.equocoin.utils;


import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.equocoin.dto.CrowdSaleProposalDTO;
import com.equocoin.handlesolidity.SolidityHandler;
import com.equocoin.service.CrowdSaleProposalService;




@Service
@EnableScheduling
public class CrowdSaleScheduler {
	
	private static final Logger LOG = LoggerFactory.getLogger(CrowdSaleScheduler.class);
	
	@Autowired
	SolidityHandler solidityHandler;

	@Autowired
	private Environment env;
	
	@Autowired
	EquocoinUtils equocoinUtils;

	
	 //@Scheduled(cron = "30 * * * * *")
		public void cronTask() throws Exception {
		 
		 LOG.info("Cron crowdsale schecduler : "+new Date() + " ends");
		 
		 solidityHandler.isCrowdsaleEnd();
	 }

}
