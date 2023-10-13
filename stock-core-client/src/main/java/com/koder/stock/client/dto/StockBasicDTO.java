package com.koder.stock.client.dto;

import lombok.*;


@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockBasicDTO extends BaseDTO{

    private Long id;
    private Long modifiedTime;
    private Long createTime;
    private String name;
    private String code;
    private Integer market;
    private String exchange;
    private Integer status;

}
