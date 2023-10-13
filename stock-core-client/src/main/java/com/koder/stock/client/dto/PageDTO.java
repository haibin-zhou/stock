package com.koder.stock.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDTO<T> extends ResultDTO<List<T>>{

    private Integer pageIndex = 20;
    private Integer pageSize = 10;
    private Integer rowCount = 0;

    public Integer getPages(){
        return ((rowCount + pageSize-1) / pageSize) + 1;
    }
}
