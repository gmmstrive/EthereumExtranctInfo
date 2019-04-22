package com.gikee.ethereum.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Token implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private String symbol;
    private String address;
    private int decimals;
}

