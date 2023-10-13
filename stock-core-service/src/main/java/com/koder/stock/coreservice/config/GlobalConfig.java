package com.koder.stock.coreservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class GlobalConfig {

    @Value("${system.default.userId}")
    public Long systemDefaultUserId;
    @Value("${system.get.quotation.history}")
    public Boolean getQuotationHistory;
    @Value("${system.quotation.history.startDate}")
    public Long quotationStartDate;
    @Value("${system.login.verifyCode}")
    private String verifyCode;

}
