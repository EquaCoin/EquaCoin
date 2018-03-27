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
	uint proposalAmount;
    uint startDate;
    uint endDate;
    uint like;
    uint dislike;
    mapping(address => bool) voted;
  
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
        uint _proposalAmnt,        
        uint  _startDate,
        uint  _endDate,
        uint _likeVotes,
        uint _dislikeVotes) public
        {
        _proposalCreatedBy = msg.sender;
		_likeVotes = 0;
		_dislikeVotes = 0;
		startTime = now + ( _startDate *  1 minutes);
		endTime = now + (_endDate *  1 minutes);

		var proposalnew = ProposalStruct( _proposalCreatedBy, _proposalTitle, _proposalCategory, _proposalDetails, _proposalAmnt, startTime, endTime,_likeVotes,_dislikeVotes);
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

  
    function getAllProposals(uint index) public constant returns (string , string, uint , uint, uint, uint, uint) {   
        return (proposalArray[index].proposalTitle, proposalArray[index].proposalCategory, proposalArray[index].proposalAmount, proposalArray[index].like, proposalArray[index].dislike,proposalArray[index].startDate, proposalArray[index].endDate);
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
        if(p.startDate < now && p.endDate > now) 
        {           // Set this voter as having voted
        if (voted) {   
            p.voted[msg.sender] = true;         // If they support the proposal
            p.like++; 
                                     // Increase score
        } else {
            p.voted[msg.sender] = true;       // If they don't
            p.dislike++; 
                                 // Decrease the score
        }
        status("success");
        }  
        else
        {
            status("failed");
        }
    }
    else
    {
        status("already voted");
    }



    }

    modifier inState(State _state) {
        require(state == _state);
         _;
    }
    
	// funding complete for proposal 
	function isCrowdSaleEnd(uint limit,uint proposalNumber) public {
		ProposalStruct storage p = proposalArray[proposalNumber]; 
		if ( p.like > limit && now > p.endDate) {
            isCrowdSaleEndStatus("success");
		/* state = State.Successful;
		payOut(); */
	}
    else
    {
        isCrowdSaleEndStatus("failed");
    }

     /* else if ( now > deadline ) {
  state = State.Failed; 
  }  */
 }

 function payOut() public inState(State.Successful)
 {
	require(adminAccount.send(this.balance));
	state = State.Closed;
	//LogWinnerPaid(beneficiary);
 }
 

 }