package com.gikee.ethereum.dao.mysql;

import com.gikee.ethereum.model.Pool;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PoolDao {

    @Results({
            @Result(property = "author", column = "address"),
            @Result(property = "poolName", column = "name")
    })
    @Select("select address,name from special_address where type = 'mining' and coin = 'eth'")
    List<Pool> getEthereumPools();

    @Results({
            @Result(property = "author", column = "address"),
            @Result(property = "poolName", column = "name")
    })
    @Select("select address,name from special_address where type = 'mining' and coin = 'eth' and address = #{author} ")
    Pool getEthereumPool(String poolAddress);

}
