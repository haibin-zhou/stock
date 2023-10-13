package com.koder.stock.web.controller;

import com.koder.stock.client.dto.StockHoldingDTO;
import com.koder.stock.client.dto.StockUserFundDTO;
import com.koder.stock.client.vo.AjaxResultVO;
import com.koder.stock.coreservice.domain.constant.StockExchangeTypeConstant;
import com.koder.stock.coreservice.thirdparty.hs.HsApiProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.koder.stock.coreservice.domain.constant.StockExchangeTypeConstant.exchangeTypeMap;

@Controller
public class StockAdminController {
    @Autowired
    private HsApiProxy hsApiProxy;

    private static final BigDecimal HK_CURRENCY_RATE = new BigDecimal("7.8");

    @RequestMapping(path = "/admin/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("admin/index");
        BigDecimal[] hkTotally = new BigDecimal[1];
        hkTotally[0] = new BigDecimal(0L);
        exchangeTypeMap.forEach((k, v) -> {
            StockUserFundDTO fund = hsApiProxy.getUserFund(v);
            modelAndView.addObject(v + "fund", fund);
            if (v.equals(StockExchangeTypeConstant.US_EXCHANGE_TYPE)) {
                hkTotally[0] = hkTotally[0].add(fund.getAssetBalance().multiply(HK_CURRENCY_RATE));
            } else {
                hkTotally[0] = hkTotally[0].add(fund.getAssetBalance());
            }
        });
        modelAndView.addObject("hkTotally", hkTotally[0].toPlainString());
        return modelAndView;
    }

    @RequestMapping(path = "/admin/queryHolding")
    @ResponseBody
    public AjaxResultVO queryHolding(HttpServletRequest request, HttpServletResponse response) {
        List<StockHoldingDTO> holdingList = hsApiProxy.queryHoldList(request.getParameter("exchangeType"));
        AjaxResultVO result = AjaxResultVO.builder().data(holdingList).code("0").build();
        return result;
    }
}
