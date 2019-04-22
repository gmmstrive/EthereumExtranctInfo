package com.gikee.ethereum.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class MessageReminder implements Serializable {
    private static final long serialVersionUID = 1L;
    private String symbol = "ETH";
    @JSONField(name = "data")
    private Message message;

    public MessageReminder(Message message) {
        this.message = message;
    }
}