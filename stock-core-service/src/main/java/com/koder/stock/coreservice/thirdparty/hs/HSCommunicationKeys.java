package com.koder.stock.coreservice.thirdparty.hs;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class HSCommunicationKeys {

    @Value("${hs.domainUrl}")
    private String domainUrl;
    @Value("${hs.loginPath}")
    private String loginPath;
    @Value("${hs.getServerPath}")
    private String getServerPath;
    @Value("${hs.resource.path}")
    private String resourcePath;
    @Value("${hs.country.code}")
    private String countryCode;
    @Value("${hs.mobile}")
    private String mobile;
    @Value("${hs.password}")
    private String password;
    @Value("${hs.trade.pwd}")
    private String tradePwd;
    @Value("${hs.init.quotationApi}")
    private Boolean initQuotationApi;
    @Value("${hs.init.tradeApi}")
    private Boolean initTradeApi;

}
