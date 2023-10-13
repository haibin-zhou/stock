package com.koder.stock.coreservice.domain.dataobject;

import com.alibaba.fastjson.JSON;
import lombok.*;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockBasicDO implements Serializable {

    private Long id;
    private Long modifiedTime;
    private Long createTime;
    private String name;
    private String code;
    private Integer market;
    private String exchange;
    private String features;
    private Integer status;

}
