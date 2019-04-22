package com.gikee.ethereum.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Message {

    private String height;
    private String tx_id;
    private String coin_name = "ETH";
    private List<TransactionAddress> address;
    private List<TransactionAddress> to_address;
    private String amount;
    private String time_stamp;

    public Message(String height, String tx_id, String address, String to_address, String amount, String time_stamp) {
        this.height = height;
        this.tx_id = tx_id;
        this.address = new ArrayList();
        this.address.add(new TransactionAddress(address));
        this.to_address = new ArrayList();
        this.to_address.add(new TransactionAddress(to_address));
        this.amount = amount;
        this.time_stamp = time_stamp;
    }
}
