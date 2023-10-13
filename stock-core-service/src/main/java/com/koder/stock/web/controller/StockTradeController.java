package com.koder.stock.web.controller;

import com.koder.stock.client.dto.ResultDTO;
import com.koder.stock.client.dto.StockTradeOrderDTO;
import com.koder.stock.coreservice.service.StockTradeOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@Controller
public class StockTradeController {
    @Autowired
    private StockTradeOrderService stockTradeOrderService;

    @RequestMapping(value = "/trade/entrust", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO<StockTradeOrderDTO> entrust(HttpServletRequest request, HttpServletResponse response) {
        String entrustBs = request.getParameter("entrustBs");
        if (StringUtils.isBlank(entrustBs)) {
            return ResultDTO.of("entrustBs.required", "购买/出售 不能为空");
        }
        String entrustPrice = request.getParameter("entrustPrice");
        String stockCode = request.getParameter("stockCode");
        String entrustCount = request.getParameter("entrustCount");

        if (StringUtils.isBlank(entrustPrice) || StringUtils.isBlank(stockCode)
                || StringUtils.isBlank(entrustCount)) {
            return ResultDTO.of("entrust.keyElements.required", "交易关键参数不能为空");
        }
        StockTradeOrderDTO stockTradeOrderDTO = StockTradeOrderDTO.builder()
                .entrustPrice(new BigDecimal(entrustPrice))
                .stockCode(stockCode)
                .entrustVolume(Integer.valueOf(entrustCount))
                .entrustBs(entrustBs).build();
        return stockTradeOrderService.createTradeOrder(stockTradeOrderDTO);
    }







}
