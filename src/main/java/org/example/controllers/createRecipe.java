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
import org.example.entity.ImageRecipe;
import org.example.entity.Recipe;
import org.example.entity.User;
import org.example.service.FileService;
import org.example.service.UserService;


import java.io.File;
import java.io.IOException;


import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/recipe/create")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
        maxFileSize = 1024 * 1024 * 50, // 50 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)

public class createRecipe extends HttpServlet {
    private RecipeDao recipeDao;
    private PreferenceDao preferenceDao;
    private UserService userService;
    private String path;
    FileService fileService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        recipeDao = (RecipeDao) getServletContext().getAttribute("recipeDao");
        preferenceDao = (PreferenceDao) getServletContext().getAttribute("preferenceDao");
        userService = (UserService) getServletContext().getAttribute("userService");
        path = (String) getServletContext().getAttribute("path");
        fileService = (FileService) getServletContext().getAttribute("fileService");

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> preferences = preferenceDao.getPreferences();
        request.setAttribute("preferences", preferences);


        getServletContext().getRequestDispatcher("/WEB-INF/views/createRecipe.jsp").forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");


        User user = userService.getUser(request, response);

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String ingredients = request.getParameter("ingredients");
        String steps = request.getParameter("steps");
        int cookingTime = Integer.parseInt(request.getParameter("cookingTime"));
        int servings = Integer.parseInt(request.getParameter("servings"));
        String preference = request.getParameter("preference");


        try {
            Recipe recipe = new Recipe();
            recipe.setName(title);
            recipe.setDescription(description);
            recipe.setCategory(preference);
            recipe.setPreparationTime(cookingTime);
            recipe.setServings(servings);
            recipe.setIngredients(ingredients);
            recipe.setSteps(steps);


            File uploadDir = new File(path);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            ImageRecipe coverImage = null;
            // Сохраняем обложку
            for (Part part : request.getParts()) {
                if (part.getName().equals("cover") && part.getSize() > 0) {
                    String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                    String filePath = path + File.separator + fileName;
                    part.write(filePath);
                    coverImage = new ImageRecipe();
                    recipe.setCoverImagePath(fileName);
                }
            }

            List<ImageRecipe> images = new ArrayList<>();
            for (Part part : request.getParts()) {
                if (part.getName().equals("images") && part.getSize() > 0) {
                    String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();

                    String filePath = path + File.separator + fileName;
                    part.write(filePath);

                    ImageRecipe image = new ImageRecipe();
                    image.setFilePath(fileName);
                    recipe.addImage(image);
                    images.add(image);


                }
            }
            recipeDao.saveRecipe(recipe, user, coverImage, images);

            response.sendRedirect(getServletContext().getContextPath()+"/cookbook");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Ошибка при сохранении рецепта: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/createRecipe.jsp").forward(request, response);
        }


    }

}
