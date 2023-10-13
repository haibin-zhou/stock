package com.koder.stock.web.filter;

import com.koder.stock.web.constant.UserLoginConstant;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "sessionFilter", urlPatterns = "/admin/*")
public class SessionFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpSession session = req.getSession();
        Object user = session.getAttribute(UserLoginConstant.SESSION_KEY);
        // 判断user是否为null
        if (user != null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            // 为null，说明用户未登录 （跳转到登录页面）
            HttpServletResponse response = (HttpServletResponse)servletResponse;
            response.sendRedirect("/index");
        }
    }

    @Override
    public void destroy() {

    }
}
