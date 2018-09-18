pragma solidity ^0.4.15;


contract EquaZone {
     enum State {
         Fundraising,
         Failed,
         Successful,
         Closed
        }

State public state = State.Fundraising;
 
address adminAccount;
uint public startTime;
uint public endTime;
 
struct ProposalStruct {
	address proposalCreatedBy;
	string proposalTitle;
	string proposalCategory;
    string proposalDetails;
	string proposalCreatedByName;
	uint proposalAmount;
    string startDate;
    string endDate;
    uint like;
    uint dislike;
	uint approve;
	uint reject;

    mapping(address => bool) voted;
    mapping(address => bool) accepted;
    }
	
	
	
    uint proposalCount = 0;
    mapping(address => ProposalStruct) proposalList;
    mapping(address => ProposalStruct)proposalListBasedOnAddress;
    ProposalStruct[] proposalArray;
   
	
 event LogFunderInitialized(
        address proposalCreatedBy,
        string proposalTitle,
        string proposalCategory,
        string proposalDetails,
        uint _proposalAmount, 
        uint256 deadline);

 event status(string status);
 event isCrowdSaleEndStatus(string isCrowdSaleEndStatus);
  
 function EquaZone() public {
        log0('hi');
    } 
  function createProposal( 
		address _proposalCreatedBy, 
        string _proposalTitle,
        string _proposalCategory,
        string _proposalDetails,
		string _proposalCreatedByName,
        uint _proposalAmnt,        
        string  _startDate,
        string  _endDate,
        uint _likeVotes,
		uint _approve,
	    uint _reject,
	
	
        uint _dislikeVotes) public
        {
        _proposalCreatedBy = msg.sender;
		_likeVotes = 0;
		_dislikeVotes = 0;
		//startTime = now + ( _startDate *  1 minutes);
		//endTime = now + (_endDate *  1 minutes);

		var proposalnew = ProposalStruct( _proposalCreatedBy, _proposalTitle, _proposalCategory, _proposalDetails,_proposalCreatedByName,_proposalAmnt, _startDate, _endDate,_likeVotes,_dislikeVotes,_approve,_reject);
         proposalList[_proposalCreatedBy] = proposalnew;
         proposalArray.push(proposalnew);
         proposalCount++;
      
	}
	
   
	function countProposalList() public constant returns (uint count) {     
        return proposalCount;
    }
     

    function getProposals(address _proposalCreatedBy) public constant returns ( string) { 
    
        return proposalListBasedOnAddress[_proposalCreatedBy].proposalTitle;
    }   

  
    function getAllProposals(uint index) public constant returns (string , string, uint , uint, uint, string, string) {   
        return (proposalArray[index].proposalTitle, proposalArray[index].proposalCategory, proposalArray[index].proposalAmount, proposalArray[index].like, proposalArray[index].dislike,proposalArray[index].startDate,proposalArray[index].endDate);
    }
	
	 function getApproveProposals(uint index) public constant returns (address , string, string , string, uint, uint, uint) {   
        return (proposalArray[index].proposalCreatedBy, proposalArray[index].proposalCategory, proposalArray[index].proposalCreatedByName, proposalArray[index].proposalDetails, proposalArray[index].like,proposalArray[index].approve,proposalArray[index].reject);
    }

	
	
    function getAllProposalsCreatedBy(uint index) public constant returns (address) {   
        return (proposalArray[index].proposalCreatedBy);
    }  

 
	function vote(uint proposalNumber,bool voted) public  
	{
        ProposalStruct storage p = proposalArray[proposalNumber];         // Get the proposal
       // require(!p.voted[msg.sender]);         // If has already voted, cancel
       // p.voted[msg.sender] = true;

    
    if(!p.voted[msg.sender])
    {
                // Set this voter as having voted
        if (voted) {   
            p.voted[msg.sender] = true;         // If they support the proposal
            p.like++; 
                                     // Increase score
         status("success");
        } else {
            p.voted[msg.sender] = true;       // If they don't
            p.dislike++; 
                                 // Decrease the score
           status("success");
        }
       
        }  else
        {
            status("already voted");
        }
  



    }
	
		function accept(uint proposalNumber,bool accepted) public  
	{
        ProposalStruct storage a = proposalArray[proposalNumber];
       
        // Get the proposal
      

    
    if(!a.accepted[msg.sender])
    {
                 // Set this voter as having voted
        if (accepted) {   
            a.accepted[msg.sender] = true;         // If they support the proposal
            a.approve += 2; 
                        // Increase score
                                       status("success");
        } else {
            a.accepted[msg.sender] = true;       // If they don't
            a.reject += 1; 
                                 // Decrease the score
                                 
         status("proposal rejected successfully");
        }
      
        }  
        else
        {
           status("already accepted");
        }
  


    }

    modifier inState(State _state) {
        require(state == _state);
         _;
    }
    


 //function payOut() public inState(State.Successful)
// {
//	require(adminAccount.send(this.balance));
//	state = State.Closed;
	
 //}
 

 }