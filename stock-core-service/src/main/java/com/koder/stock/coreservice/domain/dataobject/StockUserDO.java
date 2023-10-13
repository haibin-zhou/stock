package com.koder.stock.coreservice.domain.dataobject;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StockUserDO implements Serializable {

    private Long id;
    private Long modifiedTime;
    private Long createTime;
    private String fundAccount;
    private String mobilePhone;
    private String loginPwd;
    private String tradePwd;
    private String features;

}
