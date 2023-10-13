package com.koder.stock.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockUserDTO implements Serializable {

    private Long id;
    private Instant modifiedTime;
    private Instant createTime;
    private String fundAccount;
    private String mobilePhone;
    private String loginPwd;
    private String tradePwd;
    private Map<String,String> features;

}
