package com.gikee.ethereum.model;

import lombok.Data;

@Data
public class TraceAction {
    private String callType;
    private String from;
    private String gas;
    private String input;
    private String to;
    private String value;
    private String address;
    private String refundAddress;
    private String balance;

}
