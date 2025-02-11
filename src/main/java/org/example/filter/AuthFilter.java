package org.example.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

import org.example.service.UserService;



@WebFilter("/*")
public class AuthFilter extends HttpFilter {
    private static final String[] securedPaths = new String[]{"/recipe/create", "/cookbook", "/favoriteRecipes"};
    private UserService userService;

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        userService = new UserService();
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        boolean isProtected = false;
        for (String path : securedPaths) {
            if (req.getRequestURI().substring(req.getContextPath().length()).startsWith(path)) {
                isProtected = true;
                break;
            }
        }
        if (isProtected && !userService.isNonAnonymous(req, res)) {
            String returnUrl = req.getRequestURI();
            String queryString = req.getQueryString();
            if (queryString != null) {
                returnUrl += "?" + queryString;
            }
            res.sendRedirect(req.getContextPath() + "/login?returnUrl=" + URLEncoder.encode(returnUrl, "UTF-8"));
        } else {
            if (userService.isNonAnonymous(req, res)) {
                req.setAttribute("user", userService.getUser(req, res));
            }
            chain.doFilter(req, res);
        }
    }
}
