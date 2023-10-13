package com.koder.stock.client.vo;

import com.koder.stock.client.dto.StockHoldingDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AjaxResultVO<T> {
    String code;
    T data;
    String msg="";
    Long count =0L;
}
