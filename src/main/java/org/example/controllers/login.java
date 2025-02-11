package org.example.controllers;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.*;
import org.example.entity.User;

import org.example.util.DbException;
import org.springframework.security.crypto.bcrypt.BCrypt;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class login extends HttpServlet {
    private UserDao userDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userDao = (UserDao) getServletContext().getAttribute("userDao");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Map<String, String> errors = new HashMap<>();

        if (email == null || email.isEmpty()) {
            errors.put("email", "Пожалуйста, введите email.");
        }
        if (password == null || password.isEmpty()) {
            errors.put("password", "Пожалуйста, введите пароль.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        try {
            User user = userDao.findByEmail(email);
            if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
                errors.put("login", "Неверный email или пароль.");
                request.setAttribute("errors", errors);
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            request.getSession().setAttribute("user", user);

            String returnUrl = request.getParameter("returnUrl");
            if (returnUrl != null && !returnUrl.isEmpty() && !returnUrl.equals(request.getContextPath() + "/profile/")) {
                response.sendRedirect(returnUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/profile/" + user.getId());
            }


        } catch (DbException e) {
            e.printStackTrace();
            request.getRequestDispatcher("/WEB-INF/views/loginFailed.jsp").forward(request, response);
        }
    }

}
