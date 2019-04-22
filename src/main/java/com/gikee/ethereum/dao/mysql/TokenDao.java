package com.gikee.ethereum.dao.mysql;

import com.gikee.ethereum.model.Token;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TokenDao {

    @Select("select 'ERC20' as type,address,symbol,decimals from spider_ETH_erc20 where address = #{address}")
    Token getETHTokenErc20Info(String address);

    @Select("select 'ERC721' as type,address,symbol,'' as decimals from spider_ETH_erc721 where address = #{address} and cast(token_holder as UNSIGNED  INTEGER) > 0 ")
    Token getETHTokenErc721Info(String address);

}
