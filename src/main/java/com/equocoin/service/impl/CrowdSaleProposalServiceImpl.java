package com.equocoin.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import com.equocoin.dto.CrowdSaleProposalDTO;
import com.equocoin.handlesolidity.SolidityHandler;
import com.equocoin.model.Config;
import com.equocoin.model.ProposalInfo;
import com.equocoin.repository.ConfigInfoRepository;
import com.equocoin.repository.ProposalInfoRepository;
import com.equocoin.service.CrowdSaleProposalService;
import com.equocoin.soliditytojava.EquaZone;
import com.google.common.io.Files;

@Service
public class CrowdSaleProposalServiceImpl implements CrowdSaleProposalService {

	//private final Web3j web3j = Web3j.build(new HttpService());
	//private final Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io"));
	private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io"));
	
	@Autowired
	private SolidityHandler solidityHandler;
	
	@Autowired
	private ConfigInfoRepository configInfoRepository;
	
	@Autowired
	private ProposalInfoRepository proposalInfoRepository;
	
	@Autowired
	private Environment env;

	private static final Logger LOG = LoggerFactory.getLogger(CrowdSaleProposalServiceImpl.class);
	@Override
	public boolean createProposal(CrowdSaleProposalDTO crowdSaleDTO) throws Exception {

		try {
			boolean status = solidityHandler.createProposal(crowdSaleDTO);
			if (status) {
				return true;
			}

		} catch (IOException | CipherException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

	@Override
	public List<CrowdSaleProposalDTO> listCrowdSale(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		try {
			return solidityHandler.crowdsaleProposalList(crowdSaleProposalDTO);
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean votingForCrowdSale(CrowdSaleProposalDTO crowdSaleDTO) throws Exception {

		try {
			boolean status = solidityHandler.votingForProposal(crowdSaleDTO);
			if (status) {
				return true;
			}

		} catch (IOException | CipherException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return false;

	}
	
	@Override
	public List<CrowdSaleProposalDTO> listCrowdSaleCategory(String categoryName,CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		try {
			return solidityHandler.crowdsaleProposalCategoryList(categoryName,crowdSaleProposalDTO);
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<CrowdSaleProposalDTO> listOwnProposal(String address,CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		try {
			return solidityHandler.listOwnProposal(address,crowdSaleProposalDTO);
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<CrowdSaleProposalDTO> listOtherProposal(String address,CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		try {
			return solidityHandler.listOtherProposal(address,crowdSaleProposalDTO);
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<CrowdSaleProposalDTO> listWonProposal(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		try {
			return solidityHandler.listWonProposal(crowdSaleProposalDTO);
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<CrowdSaleProposalDTO> listFailedProposal(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		try {
			return solidityHandler.listFailedProposal(crowdSaleProposalDTO);
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<CrowdSaleProposalDTO> isCrowdsaleEnd(CrowdSaleProposalDTO crowdSaleDTO) throws Exception {
		try {
			return solidityHandler.crowdsaleProposalList(crowdSaleDTO);
		} catch (InterruptedException | ExecutionException | IOException | CipherException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unused")
	@Override
	public String userDocumentUpload(MultipartFile proposalDoc, CrowdSaleProposalDTO crowdSaleProposalDTO, String docType) throws Exception {
				FileInputStream reader = null;
				FileOutputStream writer = null;
				String path = null;
				String dbPath = null;
				
				try {
					Config configInfo = configInfoRepository.findConfigByConfigKey("location");
					String basePath = configInfo.getConfigValue();
					Credentials credentials = WalletUtils.loadCredentials(crowdSaleProposalDTO.getWalletPassword(),
							crowdSaleProposalDTO.getWalletAddress());
					
					EquaZone equaZone = EquaZone.load(env.getProperty("equazone.address"), web3j, credentials, Contract.GAS_PRICE,
							Contract.GAS_LIMIT);
					BigInteger count = (BigInteger) equaZone.countProposalList().send();
					ProposalInfo proposalInfo=new ProposalInfo();
					String uploadingdir = basePath + File.separator + 
							File.separator+env.getProperty("proposal.document.location")+File.separator +File.separator+crowdSaleProposalDTO.getUserId();
					File file = new File(uploadingdir);
					if (!file.exists()) {
						file.mkdirs();
					}

					String fileType = Files.getFileExtension(proposalDoc.getOriginalFilename());
					String fileName = Files.getNameWithoutExtension(proposalDoc.getOriginalFilename());
																						
					path = uploadingdir + File.separator + crowdSaleProposalDTO.getUserId() + "." + fileType;
					
					dbPath =env.getProperty("proposal.document.location")+"//"+crowdSaleProposalDTO.getUserId()+ "//"+ crowdSaleProposalDTO.getUserId() + "." + fileType;
					proposalInfo.setProposalNo(count);
					crowdSaleProposalDTO.setProposalId(proposalInfo.getProposalNo());
					proposalInfo.setUserId(crowdSaleProposalDTO.getUserId());
					proposalInfo.setDbPath(dbPath);
					proposalInfo.setCreatedDate(new Date());
					proposalInfo.setProposalDocumentName(fileName+"."+fileType);
					if(crowdSaleProposalDTO.getProposalDetailsLink() ==null){
						
					}
					else{
						proposalInfo.setProposalDetailsLink(crowdSaleProposalDTO.getProposalDetailsLink());
						proposalInfo.setProposalTransferStatus(new Integer(0));
						proposalInfoRepository.save(proposalInfo);
					}
					proposalInfo = proposalInfoRepository.save(proposalInfo);
					crowdSaleProposalDTO.setId(proposalInfo.getId());
					
					byte[] buffer = new byte[1000];
					File outputFile = new File(path);

					int totalBytes = 0;
					outputFile.createNewFile();
					reader = (FileInputStream) proposalDoc.getInputStream();
					writer = new FileOutputStream(outputFile);

					int bytesRead = 0;
					while ((bytesRead = reader.read(buffer)) != -1) {
						writer.write(buffer);
						totalBytes += bytesRead;
					}
					
					/*reader.close();
					writer.close();
					*/
					
				} catch (IOException e) {
					path = null;
					dbPath = null;
					reader.close();
					writer.close();
					e.printStackTrace();
				} 
//				finally{
//					reader.close();
//					writer.close();
//				}
				return path;
	}

	@Override
	public boolean acceptingForProposal(CrowdSaleProposalDTO crowdSaleDTO) throws Exception{
		boolean status = solidityHandler.acceptingForProposal(crowdSaleDTO);
		if (status) {
			return true;
		}

		return false;
	}

	@Override
	public List<CrowdSaleProposalDTO> adminProposallist(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception {
		try {
		return solidityHandler.adminProposallist(crowdSaleProposalDTO);
		}catch(Exception e){
		e.printStackTrace();
		}
		return null;
		
	}

	@Override
	public List<CrowdSaleProposalDTO> adminViewProposallist(CrowdSaleProposalDTO crowdSaleProposalDTO) throws Exception  {
		try {
			return solidityHandler.adminViewProposallist(crowdSaleProposalDTO);
			}catch(Exception e){
			e.printStackTrace();
			}
			return null;
			
	}

}
