package org.example.controllers;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.*;
import org.example.entity.Recipe;
import org.example.entity.User;
import org.example.service.FileService;
import org.example.service.UserService;
import org.example.util.DbException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/cookbook")
@MultipartConfig
public class cookbook extends HttpServlet {

    private RecipeDao recipeDao;

    private PreferenceDao preferenceDao;
    private UserService userService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        recipeDao = (RecipeDao) getServletContext().getAttribute("recipeDao");
        preferenceDao = (PreferenceDao) getServletContext().getAttribute("preferenceDao");
        userService = (UserService) getServletContext().getAttribute("userService");


    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<String> preferences = preferenceDao.getPreferences();
        request.setAttribute("preferences", preferences);

        User user = userService.getUser(request, response);

        if (user != null) {

            List<Recipe> savedRecipes = recipeDao.findCreatedRecipesByUserId(user.getId());
            user.setCreatedRecipes(savedRecipes);
            request.setAttribute("user", user);
        } else {
            response.sendRedirect(request.getContextPath()+"/login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/cookbook.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        User user = userService.getUser(request, response);
        Long recipeId = Long.parseLong(request.getParameter("recipeId"));

        Recipe recipe = recipeDao.findById(recipeId);


        try {
            if (user.getId().equals(recipe.getUser().getId())) {
                recipeDao.deleteRecipe(recipeId);
                response.sendRedirect(request.getContextPath() + "/cookbook");
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Вы не можете удалить этот рецепт.");
            }
        } catch (DbException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при удалении рецепта.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

