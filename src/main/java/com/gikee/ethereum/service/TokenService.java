package com.gikee.ethereum.service;

import com.gikee.ethereum.model.Token;

public interface TokenService {

    Token getTokenErc20Info(String address);

    Token getTokenErc721Info(String address);

    Token getFinalETHTokenInfo(String address);

}
