package com.equocoin.utils;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.equocoin.handlesolidity.SolidityHandler;


@Service
public class Scheduler {

@Autowired	
private SolidityHandler solidityHandler;
@Autowired
private EquocoinUtils equocoinUtils;

	@Scheduled(cron = "5 * * * * *")
	public boolean proposalTransactions() throws ParseException {
		
		System.out.println("Inside proposal::::::::::::::::"+new Date());
		try {
			
           boolean isProposalSuccess=solidityHandler.sentAmountByprposal();
			
			if(isProposalSuccess){
				return true;
			}else{
				return false;	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;		
	    }

  //@Scheduled(cron = "5 * * * * *")	 
   public boolean propsalDACHandler() throws ParseException {
		
		try {
			
           boolean isEligibleUser=equocoinUtils.allowElligibleUser();
			
			if(isEligibleUser){
				return true;
			}else{
				return false;	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;		
	    }
}
