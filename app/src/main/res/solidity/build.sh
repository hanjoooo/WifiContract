# cd solc
solc ../contract/EtherWifiToken.sol --bin --abi --optimize -o ../web3j-3.5.0/build
# cd ../web3j-3.5.0/bin
web3j solidity generate ../build/EtherWifiToken.bin ../build/EtherWifiToken.abi -o ./ -p com.KS-KIM.etherwifitoken