package com.sparta.msa_exam.order.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class GatewayAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {

            String gatewayHeader = httpRequest.getHeader("X-Gateway-Auth");

            if (!"my-secret-key".equals(gatewayHeader)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "잘못된 접근입니다.");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}