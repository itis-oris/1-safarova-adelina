package org.example.controllers;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.example.dao.*;
import org.example.entity.Preference;
import org.example.entity.Recipe;
import org.example.entity.User;
import org.example.entity.UserPreference;
import org.example.service.FileService;
import org.example.service.UserService;
import org.example.util.DbException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;


@WebServlet("/profile/edit")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
        maxFileSize = 1024 * 1024 * 50, // 50 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class editProfile extends HttpServlet {
    private UserDao userDao;
    private RatingDao ratingDao;
    private PreferenceDao preferenceDao;
    private UserService userService;
    private UserPreferenceDao userPreferenceDao;
    private String path;
    FileService fileService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userDao = (UserDao) getServletContext().getAttribute("userDao");
        ratingDao = (RatingDao) getServletContext().getAttribute("ratingDao");
        preferenceDao = (PreferenceDao) getServletContext().getAttribute("preferenceDao");
        userPreferenceDao = (UserPreferenceDao) getServletContext().getAttribute("userPreferenceDao");
        userService = (UserService) getServletContext().getAttribute("userService");
        path = (String) getServletContext().getAttribute("path");
        fileService = (FileService) getServletContext().getAttribute("fileService");

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> preferences = preferenceDao.getPreferences();
        request.setAttribute("preferences", preferences);


        User currentUser = userService.getUser(request, response);

        User user = userDao.findById(currentUser.getId());

        if (user != null) {
            String previousPage = request.getHeader("Referer");
            if (previousPage != null) {
                request.getSession().setAttribute("previousPage", previousPage);
            }

            String createdAt = user.getCreatedAt();

            double userRating = ratingDao.calculateUserAverageRating(user.getId());


            List<String> category = null;
            try {
                category = userPreferenceDao.getPreferencesByUserId(user.getId());
            } catch (DbException e) {
                throw new RuntimeException(e);
            }


            request.setAttribute("user", user);
            request.setAttribute("createdAt", createdAt);
            request.setAttribute("userRating", userRating);
            request.setAttribute("category", category);
            request.getRequestDispatcher("/WEB-INF/views/editProfile.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Вы не можете редактировать этот рецепт.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        try {

            String userId = request.getParameter("userId");
            String username = request.getParameter("name");
            String email = request.getParameter("email");
            String[] preferences = request.getParameterValues("preferences");
            // Обработка загруженного файла


            User user = userDao.findById(Long.parseLong(userId));
            if (user == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Пользователь не найден.");
                return;
            }
            // Обновление аватара
            Part filePart = request.getPart("avatar");
            String oldAvatar = user.getAvatar();

            if (filePart != null && filePart.getSize() > 0) {
//                String uploadPath = getServletContext().getRealPath("")  +
                if (!Objects.equals(oldAvatar, "default-avatar.jpeg")) {
                    fileService.deleteFile(oldAvatar);
                }

                File uploadDir = new File(path);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                String fileName = userId + "_" + FilenameUtils.getName(filePart.getSubmittedFileName());
                String filePath = path + File.separator + fileName;
                filePart.write(filePath);

                user.setAvatar(fileName);

            }else {
                if (!Objects.equals(oldAvatar, "default-avatar.jpeg")) {
                    fileService.deleteFile(oldAvatar);
                }
                user.setAvatar("default-avatar.jpeg");
            }


            // Обновление данных пользователя
            user.setUsername(username);
            user.setEmail(email);
            userDao.updateUser(user);

            // Обновление предпочтений
            try {
                userPreferenceDao.deletePreferences(user.getId());
            } catch (DbException ex) {
                throw new RuntimeException(ex);
            }
            if (preferences != null) {
                for (String preference : preferences) {
                    userDao.addPreferenceToUser(user, preference);
                }
            }
            response.sendRedirect(request.getContextPath() + "/profile/" + userId);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при обработке данных.");

        }


    }
}
