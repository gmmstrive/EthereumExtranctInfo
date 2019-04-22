package com.gikee.ethereum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressToken {
    private String type;
    private String symbol;
    private String address;
    private String valueNumber;
}
