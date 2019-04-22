package com.gikee.ethereum.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;
    private String address;
    private String ethBalance;
    private Set<AddressToken> tokens;
}
