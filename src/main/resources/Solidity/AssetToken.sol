pragma solidity ^0.4.11;
 
contract admined {
	address public admin;

	function admined(){
		admin = msg.sender;
	}

	modifier onlyAdmin(){
		require(msg.sender == admin);
		_;
	}

	function transferAdminship(address newAdmin) onlyAdmin {
		admin = newAdmin;
	}

}

contract Token {

	mapping (address => uint256) public balanceOf;
	// balanceOf[address] = 5;
	string public name;
	string public symbol;
	uint8 public decimal; 
	uint256 public totalSupply;
	uint256 public soldToken;
	uint256 public mintAmount=0;
	uint256 public deleteToken=0;

	event Transfer(address indexed from, address indexed to, uint256 value);
	event Burn(address indexed from, uint256 value);

	function Token(uint256 initialSupply, string tokenName, string tokenSymbol, uint8 decimalUnits){
		balanceOf[msg.sender] = initialSupply;
		totalSupply = initialSupply;
		decimal = decimalUnits;
		symbol = tokenSymbol;
		name = tokenName;
	}

	function transfer(address _to, uint256 _value){
		require(balanceOf[msg.sender] >= _value);
		require(balanceOf[_to] + _value >= balanceOf[_to]);
		balanceOf[msg.sender] -= _value;
		balanceOf[_to] += _value;
		Transfer(msg.sender, _to, _value);
	}

}

contract AssetToken is admined, Token{

	function AssetToken(uint256 initialSupply, string tokenName, string tokenSymbol, uint8 decimalUnits, address centralAdmin) Token (0, tokenName, tokenSymbol, decimalUnits ){
		totalSupply = initialSupply;
		soldToken = 0;
		
		if(centralAdmin != 0)
			admin = centralAdmin;
		else
			admin = msg.sender;
		balanceOf[admin] = initialSupply;
		totalSupply = initialSupply;	
	}

	function mintToken(address target, uint256 mintedAmount) onlyAdmin{
		balanceOf[target] += mintedAmount;
		mintAmount += mintedAmount;
		totalSupply += mintedAmount;
		Transfer(0, this, mintedAmount);
		Transfer(this, target, mintedAmount);
	}

	function transfer(address _to, uint256 _value){
		require(balanceOf[msg.sender] > 0);
		require(balanceOf[msg.sender] >= _value);
		require(balanceOf[_to] + _value >= balanceOf[_to]);
		//if(admin)
		balanceOf[msg.sender] -= _value;
		balanceOf[_to] += _value;
		soldToken +=  _value;
		Transfer(msg.sender, _to, _value);
	}

	function transferCrwdsale(address _to, uint256 _value){
		require(balanceOf[msg.sender] > 0);
		require(balanceOf[msg.sender] >= _value);
		require(balanceOf[_to] + _value >= balanceOf[_to]);
		//if(admin)
		balanceOf[msg.sender] -= _value;
		balanceOf[_to] += _value;
		soldToken +=  _value;
		Transfer(msg.sender, _to, _value);
	}
	
	
	function burn(uint256 _value) public returns (bool success) {
        require(balanceOf[msg.sender] >= _value);   
        balanceOf[msg.sender] -= _value;
        deleteToken  += _value;           
        totalSupply -= _value;                      
        Burn(msg.sender, _value);
        return true;
    }

}





