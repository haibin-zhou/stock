package com.koder.stock.web.controller;

import com.koder.stock.client.dto.*;
import com.koder.stock.client.vo.StockBasicVO;
import com.koder.stock.coreservice.listenner.StockTradeOrderCreationMsgListener;
import com.koder.stock.coreservice.manager.StockTradeOrderManager;
import com.koder.stock.coreservice.manager.StockUserFundManager;
import com.koder.stock.coreservice.scheduled.StockQuotationScheduled;
import com.koder.stock.coreservice.service.StockBasicInformationService;
import com.koder.stock.coreservice.service.StockTradeOrderService;
import com.koder.stock.coreservice.thirdparty.hs.HsApiProxy;
import com.koder.stock.web.convert.StockBasicVOConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Controller
public class StockBasicController {

    @Autowired
    private StockBasicInformationService stockBasicInformationService;
    @Autowired
    private StockTradeOrderService stockTradeOrderService;
    @Autowired
    private StockTradeOrderManager stockTradeOrderManager;
    @Autowired
    private StockTradeOrderCreationMsgListener stockTradeOrderCreationMsgListener;
    @Autowired
    private StockQuotationScheduled stockQuotationScheduled;
    @Autowired
    private HsApiProxy hsApiProxy;
    @Autowired
    private StockUserFundManager stockUserFundManager;

    @RequestMapping(value = "/stock/basic/queryByCode/{code}", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO<StockBasicVO> queryStockBasicByCode(@PathVariable String code) {
        if (StringUtils.isEmpty(code)) {
            return ResultDTO.of("required.param.null", "code required");
        }
        StockBasicDTO dto = stockBasicInformationService.queryByCode(code);
        if (dto == null) {
            return ResultDTO.of("result.param.null", "code incorrectly");
        }
        StockBasicVO stockBasicVO = StockBasicVOConvertor.fromDTO(dto);
        return ResultDTO.of(stockBasicVO);
    }

    @RequestMapping(value = "/stock/createTradeOrder", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO<List<StockTradeOrderDTO>> createTradeOrder(HttpServletRequest request) {
        StockQuotationDTO stockQuotationDTO = StockQuotationDTO.builder()
                .quotationTime(Instant.now())
                .stockId(Long.valueOf(request.getParameter("stockId")))
                .code(request.getParameter("code"))
                .closingPrice(new BigDecimal(request.getParameter("closingPrice")))
                .highestPrice(new BigDecimal(request.getParameter("highestPrice")))
                .lowestPrice(new BigDecimal(request.getParameter("lowestPrice")))
                .openingPrice(new BigDecimal(request.getParameter("openingPrice")))
                .latestPrice(new BigDecimal(request.getParameter("latestPrice")))
                .lastClosingPrice(new BigDecimal(request.getParameter("lastClosingPrice"))).build();
        List<StockTradeOrderDTO> resultObject = this.stockTradeOrderService.createTradeOrders(stockQuotationDTO);
        return ResultDTO.of(resultObject);
    }


    @RequestMapping(value = "/stock/pushOrder", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO<StockTradeOrderDTO> pushTradeOrder(HttpServletRequest request) {
        StockTradeOrderDTO tradeOrderNo = stockTradeOrderManager.queryOrderByTradeNo(request.getParameter("tradeOrderNo"));
        stockTradeOrderCreationMsgListener.orderCreationMsg(tradeOrderNo);
        return ResultDTO.of(tradeOrderNo);
    }

    @RequestMapping(value = "/stock/getHistoryQuotation", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO<Boolean> getHistoryQuotation(HttpServletRequest request) {
        stockQuotationScheduled.historyQuotationGetting(request.getParameter("code"));
        return ResultDTO.of(true);
    }

    @RequestMapping(value = "/stock/balance", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO<StockUserFundDTO> getBalance(HttpServletRequest request) {
        StockUserFundDTO stockUserFundDTO = hsApiProxy.getUserFund(request.getParameter("exchangeType"));
        stockUserFundManager.add(stockUserFundDTO);
        return ResultDTO.of(stockUserFundDTO);
    }

    @RequestMapping(value = "/stock/holding", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO<Map<String,StockHoldingDTO>> getHolding(HttpServletRequest request) {
        Map<String,StockHoldingDTO> result  = hsApiProxy.queryHolding(request.getParameter("exchangeType"));
        return ResultDTO.of(result);
    }
    // 查询订阅列表，订阅生效的排在前面

    public PageDTO<StockBasicVO> queryStockBasicLists(PageDTO pageDTO){
        



        return pageDTO;
    }
}
