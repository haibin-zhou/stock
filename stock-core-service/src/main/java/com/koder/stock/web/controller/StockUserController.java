package com.koder.stock.web.controller;

import com.koder.stock.client.dto.ResultDTO;
import com.koder.stock.client.dto.StockUserDTO;
import com.koder.stock.coreservice.config.GlobalConfig;
import com.koder.stock.coreservice.service.StockUserService;
import com.koder.stock.web.constant.UserLoginConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class StockUserController {

    @Autowired
    private GlobalConfig globalConfig;
    @Autowired
    private StockUserService stockUserService;

    @RequestMapping(path = "/login")
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String mobilePhone = request.getParameter("mobilePhone");
        String password = request.getParameter("password");
        String verifyCode = request.getParameter("verifyCode");

        ModelAndView modelAndView = new ModelAndView();

        if (StringUtils.isBlank(verifyCode) || StringUtils.isBlank(mobilePhone) || StringUtils.isBlank(password)) {
            modelAndView.setViewName("login");
            return modelAndView.addObject("errorMsg", "用户名或者密码不正确");
        }

        if (!verifyCode.equals(this.globalConfig.getVerifyCode())) {
            modelAndView.setViewName("login");
            return modelAndView.addObject("errorMsg", "用户名或者密码不正确");
        }

        ResultDTO<StockUserDTO> stockUserResult = this.stockUserService.queryByUP(mobilePhone, password);

        if (stockUserResult.getResult() == null) {
            modelAndView.setViewName("login");
            return modelAndView.addObject("errorMsg", "用户名或者密码不正确");
        }
        request.getSession().setAttribute(UserLoginConstant.SESSION_KEY, stockUserResult.getResult());
        response.sendRedirect("/admin/index");
        return modelAndView;
    }

    @RequestMapping(path = "/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }
}
