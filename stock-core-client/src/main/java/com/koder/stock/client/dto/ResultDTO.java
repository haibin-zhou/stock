package com.koder.stock.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultDTO<T> {

    private boolean success;
    private String errorMsg;
    private String errorCode;
    private T result;

    public static <T> ResultDTO<T> of(String errorCode, String errorMsg) {
        ResultDTO resultVO = new ResultDTO();
        resultVO.errorCode = errorCode;
        resultVO.errorMsg = errorMsg;
        resultVO.success = false;
        return resultVO;
    }

    public static <T> ResultDTO<T> of(T result) {
        ResultDTO resultVO = new ResultDTO();
        resultVO.setResult(result);
        resultVO.success = true;
        return resultVO;
    }
}


