package com.gikee.ethereum.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Pool implements Serializable {
    private static final long serialVersionUID = 1L;
    private String author;
    private String poolName;
}
