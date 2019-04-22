package com.gikee.ethereum.model;

import lombok.Data;

@Data
public class TraceResult {
    private String address;
    private String gasUsed;
    private String output;
}
