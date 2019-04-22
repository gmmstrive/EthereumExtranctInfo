package com.gikee.ethereum.dao.es;

import com.gikee.ethereum.model.Address;
import com.gikee.ethereum.model.AddressToken;
import com.gikee.ethereum.model.Token;
import com.gikee.ethereum.service.CommonService;
import com.gikee.ethereum.utils.ElasticSearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ESAddressDao extends ElasticSearchUtil<Address> {

    private static final String INDEX = "ethereum_address";
    private static final String TYPE = "ethAddress";

    @Autowired
    private CommonService commonService;

    @Override
    protected Class<Address> getEntityClass() {
        return Address.class;
    }

    /**
     * 批量查询 String
     *
     * @param list
     * @return
     */
    public Map<String, Address> elasticSearchMultiString(List<String> list) {
        log.info("------> 批量查询 ETHAddress 开始 <------");
        Map<String, Address> addressMap = new HashMap<>();
        try {
            List<Address> ethAddressList = super.elasticSearchMultiString(list, INDEX, TYPE);
            ethAddressList.stream().map(x -> addressMap.put(x.getAddress(), x)).count();
        } catch (IOException e) {
            log.error("{}",list);
            e.printStackTrace();
        }
        log.info("------> 批量查询 ETHAddress 结束 <------");
        return addressMap;
    }

    /**
     * 批量查询 ETHAddress
     *
     * @param list
     * @return
     */
    public Map<String, Address> elasticSearchMulti(List<Address> list) {
        List<String> ethAddressList = list.parallelStream().map(Address::getAddress).collect(Collectors.toList());
        return elasticSearchMultiString(ethAddressList);
    }

    /**
     * 批量插入 Address
     *
     * @param list
     */
    public void elasticSearchBulk(List<Address> list) {
        log.info("------> 批量插入 ETHAddress 开始 <------");
        List<String> ids = list.stream().map(Address::getAddress).collect(Collectors.toList());
        try {
            super.elasticSearchBulk(list, ids, INDEX, TYPE);
        } catch (IOException e) {
            log.error("批量插入 ETHAddress 失败 {}", e.getMessage());
            e.printStackTrace();
        }
        log.info("------> 批量插入 ETHAddress 结束 <------");
    }

    /**
     * 批量更新 ETHAddress
     *
     * @param calculatedAddress
     */
    public void elasticSearchAddressUpdate(Map<String, Set<Token>> calculatedAddress) {
        log.info("------> 开始处理当前块中 token 交易地址余额 <------");
        List<Address> ethAddressList = new ArrayList<>();
        // 存入 ES 地址
        Address ethAddress;
        // ES 地址中包含的 token 集合
        Set<AddressToken> ethAddressTokenSet;
        // token
        AddressToken ethAddressToken;
        // token 信息
        Set<Token> ethToken;
        for (Map.Entry<String, Set<Token>> entry : calculatedAddress.entrySet()) {
            ethAddress = new Address();
            ethAddress.setAddress(entry.getKey().toLowerCase());
            ethAddressTokenSet = new HashSet<>();
            ethToken = entry.getValue();
            for (Token token : ethToken) {
                ethAddressToken = new AddressToken();
                if ("ERC20".equals(token.getType())) {
                    ethAddressToken.setValueNumber(commonService.getTokenBalance(entry.getKey(), token.getAddress(), token.getDecimals()));
                } else {
                    ethAddressToken.setValueNumber(commonService.getTokenBalance(entry.getKey(), token.getAddress()));
                }
                ethAddressToken.setAddress(token.getAddress());
                ethAddressToken.setSymbol(token.getSymbol());
                ethAddressToken.setType(token.getType());
                ethAddressTokenSet.add(ethAddressToken);
            }
            ethAddress.setTokens(ethAddressTokenSet);
            ethAddressList.add(ethAddress);
        }
        integratedAndSave(ethAddressList);
        log.info("------> 处理当前块中 token 交易地址余额结束 <------");
    }

    /**
     * 整合地址余额信息并且保存
     *
     * @param AddressList
     */
    public void integratedAndSave(List<Address> AddressList) {

        log.info("------> 开始整合当前块中 token 交易集合 <------");

        List<Address> insertAddress = new ArrayList<>();

        // 查询 ES 集群上包含当前地址集合的地址
        Map<String, Address> queryAddressMap = elasticSearchMulti(AddressList);

        Address tempAddress;
        Set<AddressToken> tempTokens;
        List<AddressToken> removeToken;

        // 当前块信息中参与 token 转账的地址(包含 token 信息)集合,然后返回待插入 ES 集群的地址对象集合
        for (Address currentAddress : AddressList) {
            /**
             * 如果当前地址在 ES 集群中存在，则更新，否则新增
             */
            if (queryAddressMap.containsKey(currentAddress.getAddress())) {

                // es 返回当前地址对象
                tempAddress = queryAddressMap.get(currentAddress.getAddress());

                // es 当前地址上的所有的 token
                tempTokens = tempAddress.getTokens();

                // 当前块发生交易的地址的所有 tokenAddress
                List<String> insertTokenAddress = currentAddress.getTokens().stream().map(AddressToken::getAddress).collect(Collectors.toList());

                if (tempTokens != null) {
                    // 找出 es 当前地址上包含刚刚获取块地址 token 交易的对象
                    removeToken = tempTokens.parallelStream().filter(t -> insertTokenAddress.contains(t.getAddress())).collect(Collectors.toList());

                    // 删除之前地址的 token 余额信息
                    tempTokens.removeAll(removeToken);

                    // 添加刚刚更新的 token 余额信息
                    tempTokens.addAll(currentAddress.getTokens());

                    // 保存到 es 地址对象中
                    tempAddress.setTokens(tempTokens);
                } else {
                    tempAddress.setTokens(currentAddress.getTokens());
                }

                // 添加到
                insertAddress.add(tempAddress);
            } else {
                insertAddress.add(currentAddress);
            }
        }
        log.info("------> 整合当前块中 token 交易集合结束 <------");
        // 批量插入 ES 集群
        elasticSearchBulk(insertAddress);
    }

    /**
     * 更新以太币余额
     *
     * @param ethAddressSet
     * @param blockNumber
     */
    public void integratedAndSave(Set<String> ethAddressSet, BigInteger blockNumber) {
        log.info("------> 开始整合当前块中 ethAddress 交易集合 <------");
        List<Address> ethAddressList = new ArrayList<>();
        Map<String, Address> queryAddressMap;
        Address ethAddress;
        Address tempAddress;
        if (!ethAddressSet.isEmpty()) {
            queryAddressMap = elasticSearchMultiString(new ArrayList<>(ethAddressSet));
            for (String address : ethAddressSet) {
                if (address != null) {
                    if (queryAddressMap.containsKey(address)) {
                        tempAddress = queryAddressMap.get(address);
                        tempAddress.setEthBalance(commonService.getEthereumBalance(address, blockNumber));
                        ethAddressList.add(tempAddress);
                    } else {
                        ethAddress = new Address();
                        ethAddress.setAddress(address);
                        ethAddress.setEthBalance(commonService.getEthereumBalance(address, blockNumber));
                        ethAddressList.add(ethAddress);
                    }
                }
            }
        }
        log.info("------> 整合当前块中 ethAddress 交易集合结束 <------");
        // 批量插入 ES 集群
        elasticSearchBulk(ethAddressList);
    }

}
