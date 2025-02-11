package org.example.controllers;

import org.example.dao.RecipeDao;
import org.example.dao.UserDao;
import org.example.dao.UserFavoriteRecipesDao;
import org.example.service.UserService;
import org.example.entity.User;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/saveToFavorites")
public class saveToFavorites extends HttpServlet {
    private UserService userService;
    private UserFavoriteRecipesDao userFavoriteRecipesDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = (UserService) getServletContext().getAttribute("userService");
        userFavoriteRecipesDao = (UserFavoriteRecipesDao) getServletContext().getAttribute("userFavoriteRecipesDao");

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String recipeIdParam = request.getParameter("recipeId");
        if (recipeIdParam == null || recipeIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Идентификатор рецепта не указан");
            return;
        }

        Long recipeId;
        try {
            recipeId = Long.parseLong(recipeIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный формат идентификатора рецепта");
            return;
        }


        User currentUser = userService.getUser(request, response);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            userFavoriteRecipesDao.addRecipeToFavorites(currentUser, recipeId);
            response.sendRedirect(request.getContextPath() + "/favoriteRecipes");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при обработке запроса");
        }
    }

}
