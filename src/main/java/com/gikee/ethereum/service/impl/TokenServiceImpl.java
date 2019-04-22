package com.gikee.ethereum.service.impl;

import com.gikee.ethereum.dao.mysql.TokenDao;
import com.gikee.ethereum.model.Token;
import com.gikee.ethereum.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "Token")
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenDao tokenDao;

    @Cacheable
    @Override
    public Token getTokenErc20Info(String address) {
        return tokenDao.getETHTokenErc20Info(address);
    }

    @Cacheable
    @Override
    public Token getTokenErc721Info(String address) {
        return tokenDao.getETHTokenErc721Info(address);
    }

    @Override
    public Token getFinalETHTokenInfo(String address) {
        Token tokenErc20Info = getTokenErc20Info(address);
        if (tokenErc20Info != null) {
            return tokenErc20Info;
        }
        Token tokenErc721Info = getTokenErc721Info(address);
        if (tokenErc721Info != null) {
            return tokenErc721Info;
        }
        return null;
    }

}
