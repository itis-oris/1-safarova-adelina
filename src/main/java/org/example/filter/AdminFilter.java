package org.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.UserService;

import java.io.IOException;
import java.net.URLEncoder;

@WebFilter("/admin")
public class AdminFilter extends HttpFilter {
    private UserService userService;

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        userService = new UserService();
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (!userService.isNonAnonymous(req, res) || !userService.getUser(req, res).getIsAdmin()) {
            res.sendRedirect(req.getContextPath() + "/main" );
            req.getSession().setAttribute("notAdmin", "Вы не обладаете достаточными правами, для посещения страницы администратора!");
            return;
        } else {
            if (userService.isNonAnonymous(req, res)) {
                req.setAttribute("user", userService.getUser(req, res));
            }
            chain.doFilter(req, res);
        }
    }
}

