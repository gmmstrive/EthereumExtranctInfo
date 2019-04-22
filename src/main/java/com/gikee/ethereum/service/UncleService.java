package com.gikee.ethereum.service;

import com.gikee.ethereum.model.Block;
import com.gikee.ethereum.model.Uncle;

import java.util.List;

public interface UncleService {

    List<Uncle> etherumUncles(List<String> unclesAddress, Block block);

}
