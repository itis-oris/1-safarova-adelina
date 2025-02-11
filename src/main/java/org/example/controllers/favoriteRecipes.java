package org.example.controllers;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.*;
import org.example.entity.Recipe;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.util.DbException;


import java.io.IOException;
import java.util.List;

@WebServlet("/favoriteRecipes")
public class favoriteRecipes extends HttpServlet {

    private RecipeDao recipeDao;
    private PreferenceDao preferenceDao;
    private UserFavoriteRecipesDao userFavoriteRecipesDao;
    private UserService userService;
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        recipeDao = (RecipeDao) getServletContext().getAttribute("recipeDao");
        preferenceDao = (PreferenceDao) getServletContext().getAttribute("preferenceDao");
        userFavoriteRecipesDao = (UserFavoriteRecipesDao) getServletContext().getAttribute("userFavoriteRecipesDao");
        userService = (UserService) getServletContext().getAttribute("userService");

    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<String> preferences = preferenceDao.getPreferences();
        request.setAttribute("preferences", preferences);


        User user = userService.getUser(request, response);

        if (user != null) {


            List<Recipe> favoriteRecipes = recipeDao.findFavoriteRecipesByUserId(user.getId());
            user.setFavoriteRecipes(favoriteRecipes);
            request.setAttribute("user", user);
        } else {
            response.sendRedirect(request.getContextPath()+"/login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/favoriteRecipes.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        User user = userService.getUser(request, response);

        if ("remove".equals(action)) {
            try {
                Long recipeId = Long.parseLong(request.getParameter("recipeId"));
                userFavoriteRecipesDao.removeRecipeFromFavorites(user, recipeId);
                response.sendRedirect(request.getContextPath() + "/favoriteRecipes");
            } catch (DbException | NumberFormatException e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Ошибка при удалении рецепта из избранного.");
                request.getRequestDispatcher("/favoriteRecipes.jsp").forward(request, response);
            }
        }
    }

}
