pragma solidity ^0.4.24;

import "./node_modules/zeppelin-solidity/contracts/token/ERC20/StandardToken.sol";

contract mortal {
    /* Define variable owner of the type address*/
    address owner;

    /* this function is executed at initialization and sets the owner of the contract */
    constructor() public { owner = msg.sender; }

    /* Function to recover the funds on the contract */
    function kill() public { if (msg.sender == owner) suicide(owner); }
}

contract EtherWifiToken is StandardToken, mortal {
    /**
     * @notice  토큰 기본설정
     * @param   name            토큰명
     * @param   symbol          토큰 약칭 
     * @param   INITIAL_SUPPLY  총 발행량
     * @param   COST
     * @author  KS-KIM
     */
    string public name = "EtherWifiToken";
    string public symbol = "WFT";
    uint8 public decimals = 18;
    uint public INITIAL_SUPPLY = 10000 * (10 ** uint256(decimals));
    uint public COST = 100;

    /**
     * @notice  ERC-20 토큰 생성자
     *          최초 발행량 전부 스마트 컨트랙트 호출자가 갖도록 설정
     * @author  KS-KIM
     */
    constructor() public {
        totalSupply_ = INITIAL_SUPPLY;
        balances[msg.sender] = INITIAL_SUPPLY;
    }

    /**
     * @notice  와이파이 정보를 저장하는 구조체
     *          macAddress는 key값으로 이용되므로 넣지 않음
     * @param   macAddress  맥주소
     * @param   ssid        와이파이명
     * @param   password    비밀번호 (대칭키로 암호화됨)
     * @param   sharer      공유한 사람의 지갑 주소
     * @param   count       광고시청 횟수
     * @param   isShared    공유상태 (활성 / 비활성)
     * @author  KS-KIM
     */
    struct AccessPoint {
        string macAddress;
        string ssid;
        string password;
        address sharer;
        uint count;
        bool isShared;
    }

    /**
     * @notice  고유한 맥주소를 통해 와이파이 정보 찾기 (key-value)
     * @author  KS-KIM
     */
    mapping (string => AccessPoint) accessPoints;

    /**
     * @notice  와이파이 소유주 확인
     * @author  KS-KIM
     */
    modifier accessPointOwner(string _macAddress) {
        require(accessPoints[_macAddress].sharer == msg.sender);
        _;
    }

    /**
     * @notice  와이파이 신규 등록
     *          이미 등록된 MAC 주소인 경우에는 등록하지 않음
     * @author  KS-KIM
     */
    function addAccessPoint(string _macAddress, string _ssid, string _password) public returns(bool) {
        if (accessPoints[_macAddress].sharer != 0) {
            return false;
        } else {
            accessPoints[_macAddress] = AccessPoint(_macAddress, _ssid, _password, msg.sender, 0, true);
            return true;
        }
    }

    /**
     * @notice  와이파이 공유상태 활성 및 비활성
     *          소유주만 변경 가능
     * @author  KS-KIM
     */
    function setStatus(string _macAddress, bool _switch) public accessPointOwner(_macAddress) {
        accessPoints[_macAddress].isShared = _switch;
    }

    /**
     * @notice  와이파이 비밀번호 재설정
     *          소유주만 변경 가능
     * @author  KS-KIM
     */
    function setPassword(string _macAddress, string _password) public accessPointOwner(_macAddress) {
        accessPoints[_macAddress].password = _password;
    }

    /**
     * @notice  와이파이 공유 시간 재설정
     *          소유주만 변경 가능
     * @author  KS-KIM
     */
    function setSharedTime(string _macAddress, uint _sharedTime) public accessPointOwner(_macAddress) {
        accessPoints[_macAddress].sharedTime = _sharedTime;
    }

    /**
     * @notice  와이파이 정보 조회
     *          비밀번호, 공유자 지갑 주소, 사용 시간, 활성여부
     * @author  KS-KIM
     */
    function getAccessPoint(string _macAddress) constant returns(string, address, uint, bool) {
        return (accessPoints[_macAddress].password, accessPoints[_macAddress].sharer, accessPoints[_macAddress].sharedTime, accessPoints[_macAddress].isShared);
    }

    /**
     * @notice  광고 카운트를 증가시킴
     * @author  KS-KIM
     */
    function addCount(string _macAddress, uint _num) {
        accessPoints[_macAddress].sharedTime += _num;
    }

    /**
     * @notice  와이파이 정산 받기
     * @author  KS-KIM
     */
    function getPaidToken(string _macAddress) public accessPointOwner(_macAddress) {
        uint tokenValue = accessPoints[_macAddress].count * COST;
        bool isSuccess = transferFrom(owner, accessPoints[_macAddress].sharer, tokenValue);
        if (isSuccess == true) {
            accessPoints[_macAddress].count = 0;
        }
    }
}