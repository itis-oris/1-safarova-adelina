package org.example.controllers;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.dao.*;
import org.example.entity.User;
import org.example.service.FileService;
import org.example.util.DbException;
import org.springframework.security.crypto.bcrypt.BCrypt;


@WebServlet("/register")
public class register extends HttpServlet {
    private UserDao userDao;
    private PreferenceDao preferenceDao;
    private String path;
    FileService fileService;
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userDao = (UserDao) getServletContext().getAttribute("userDao");
        preferenceDao = (PreferenceDao) getServletContext().getAttribute("preferenceDao");
        path = (String) getServletContext().getAttribute("path");
        fileService = (FileService) getServletContext().getAttribute("fileService");
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> preferences = preferenceDao.getPreferences();
        request.setAttribute("preferences", preferences);
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String passwordConfirm = request.getParameter("passwordConfirm");
        String[] preferences = request.getParameterValues("preferences");

        Map<String, String> errors = new HashMap<>();

        if (name == null || name.isEmpty()) {
            errors.put("name", "Пожалуйста, введите имя.");
        }
        if (email == null || email.isEmpty()) {
            errors.put("email", "Пожалуйста, введите email.");
        }
        if (password == null || password.isEmpty() || passwordConfirm == null || passwordConfirm.isEmpty()) {
            errors.put("password", "Пожалуйста, введите пароль и его подтверждение.");
        }
        if (!password.equals(passwordConfirm)) {
            errors.put("passwordConfirm", "Пароли не совпадают.");
        }

        try {
            if (userDao.isUsernameExists(name)) {
                errors.put("name", "Пользователь с таким именем уже существует.");
            }
            if (userDao.isEmailExists(email)) {
                errors.put("email", "Пользователь с таким email уже существует.");
            }

            if (!errors.isEmpty()) {

                List<String> preferencesList = preferenceDao.getPreferences();
                request.setAttribute("preferences", preferencesList);

                request.setAttribute("errors", errors);
                request.setAttribute("name", name);
                request.setAttribute("email", email);
                getServletContext().getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                return;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            User user = new User();
            user.setUsername(name);
            user.setEmail(email);
            user.setPassword(hashedPassword);

            userDao.save(user);

            if (preferences != null && preferences.length > 0) {
                for (String preference : preferences) {
                    userDao.addPreferenceToUser(user, preference);
                }
            }
            response.sendRedirect(request.getContextPath()+"/login");
        } catch (DbException e) {
            e.printStackTrace();
            request.getRequestDispatcher("/WEB-INF/views/registerFailed.jsp")
                    .forward(request, response);
        }
    }

}

