package org.example.controllers;

import org.example.dao.*;
import org.example.entity.Recipe;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/search")
public class search extends HttpServlet {
    private RecipeDao recipeDao;
    private PreferenceDao preferenceDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        recipeDao = (RecipeDao) getServletContext().getAttribute("recipeDao");
        preferenceDao = (PreferenceDao) getServletContext().getAttribute("preferenceDao");

    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        String category = request.getParameter("category");
        String cookingTime = request.getParameter("cookingTime");

        List<String> preferences = preferenceDao.getPreferences();
        request.setAttribute("preferences", preferences);


        List<Recipe> recipes = recipeDao.search(query, category, cookingTime);

        request.setAttribute("recipes", recipes);
        request.getRequestDispatcher("/WEB-INF/views/search.jsp").forward(request, response);
    }
}
