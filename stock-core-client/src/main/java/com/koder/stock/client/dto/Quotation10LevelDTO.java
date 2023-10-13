package com.koder.stock.client.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Quotation10LevelDTO {

    private Integer level;
    private BigDecimal price;
    private Long volume;
    private Integer ordersCount;
    private String brokeId;
    // buyIn 1 ; sell out 2;
    private String entrustBs;

}
