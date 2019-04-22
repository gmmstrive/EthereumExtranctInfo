package com.gikee.ethereum.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@Document(collection = "Uncle")
public class Uncle extends Block implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger uncleNumber;
    private Integer unclePosition;
}
