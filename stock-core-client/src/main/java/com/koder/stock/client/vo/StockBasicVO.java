package com.koder.stock.client.vo;

import lombok.*;

import java.util.HashMap;
import java.util.Map;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockBasicVO {
    private Long id;
    private String modifiedTime;
    private String name;
    private String code;
    private String market;
    private String status;
    private String quotationTime;


}
