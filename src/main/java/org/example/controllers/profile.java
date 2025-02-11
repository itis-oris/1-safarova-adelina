package org.example.controllers;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.*;
import org.example.entity.*;
import org.example.service.UserService;

import java.io.IOException;
import java.util.List;

@WebServlet("/profile/*")
public class profile extends HttpServlet {
    private UserDao userDao;
    private RecipeDao recipeDao;
    private UserPreferenceDao userPreferenceDao;
    private CommentDao commentDao;
    private RatingDao ratingDao;
    private UserService userService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userDao = (UserDao) getServletContext().getAttribute("userDao");
        recipeDao = (RecipeDao) getServletContext().getAttribute("recipeDao");
        userPreferenceDao = (UserPreferenceDao) getServletContext().getAttribute("userPreferenceDao");
        commentDao = (CommentDao) getServletContext().getAttribute("commentDao");
        ratingDao = (RatingDao) getServletContext().getAttribute("ratingDao");
        userService = (UserService) getServletContext().getAttribute("userService");


    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.matches("^/\\d+$")) {
            String profileIdStr = pathInfo.substring(1);

            Long profileId = Long.parseLong(profileIdStr);
            User currentUser = userService.getUser(request, response);

            User profileUser = userDao.findById(profileId);
            if (profileUser == null) {
                response.sendRedirect(request.getContextPath() + "/main");
                return;
            }
            try {

                List<Recipe> createdRecipes = recipeDao.findCreatedRecipesByUserId(profileUser.getId());
                List<Recipe> favoriteRecipes = recipeDao.findFavoriteRecipesByUserId(profileUser.getId());
                List<Comment> userComments = commentDao.findByUserId(profileUser.getId());
                List<String> userPreference = userPreferenceDao.getPreferencesByUserId(profileUser.getId());
                double userRating = ratingDao.calculateUserAverageRating(profileUser.getId());
                String createdAt = profileUser.getCreatedAt();

                request.setAttribute("user", currentUser);
                request.setAttribute("profileUser", profileUser);
                request.setAttribute("userPreference", userPreference);
                request.setAttribute("createdRecipes", createdRecipes);
                request.setAttribute("favoriteRecipes", favoriteRecipes);
                request.setAttribute("comments", userComments);
                request.setAttribute("userRating", userRating);
                request.setAttribute("createdAt", createdAt);


            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Не удалось загрузить профиль пользователя.");
//            getServletContext().getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        User user = userService.getUser(request, response);
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            try {
                userDao.deleteUser(user.getId());
                response.sendRedirect(request.getContextPath() + "/logout");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при удалении аккаунта");
            }
        }
    }
}

