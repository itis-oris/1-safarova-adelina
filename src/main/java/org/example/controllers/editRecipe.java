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
import org.example.util.DbException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/recipe/edit/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
        maxFileSize = 1024 * 1024 * 50, // 50 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class editRecipe extends HttpServlet {
    private RecipeDao recipeDao;
    private PreferenceDao preferenceDao;
    private UserService userService;
    private UserPreferenceDao userPreferenceDao;
    private ImageRecipeDao imageRecipeDao;
    private String path;
    FileService fileService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        recipeDao = (RecipeDao) getServletContext().getAttribute("recipeDao");
        preferenceDao = (PreferenceDao) getServletContext().getAttribute("preferenceDao");
        userService = (UserService) getServletContext().getAttribute("userService");
        userPreferenceDao = (UserPreferenceDao) getServletContext().getAttribute("userPreferenceDao");
        imageRecipeDao = (ImageRecipeDao) getServletContext().getAttribute("imageRecipeDao");
        path = (String) getServletContext().getAttribute("path");
        fileService = (FileService) getServletContext().getAttribute("fileService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<String> preferences = preferenceDao.getPreferences();
        request.setAttribute("preferences", preferences);


        User user = userService.getUser(request, response);
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Параметр id отсутствует");
            return;
        }
        List<String> userPreferences;
        try {
            userPreferences = userPreferenceDao.getPreferencesByUserId(user.getId());
        } catch (DbException e) {
            throw new RuntimeException(e);
        }
        request.setAttribute("userPreferences", userPreferences);

        Long recipeId;
        try {
            recipeId = Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный формат id");
            return;
        }

        Recipe recipe = recipeDao.findById(recipeId);
        String createdAt = recipe.getCreatedAt();

        if (recipe != null && user.getId().equals(recipe.getUser().getId())) {
            String previousPage = request.getHeader("Referer");
            request.setAttribute("previousPage", previousPage);

            request.setAttribute("recipe", recipe);
            request.setAttribute("createdAt", createdAt);
            List<ImageRecipe> images = imageRecipeDao.findByRecipeId(recipeId);
            request.setAttribute("images", images);
            request.getRequestDispatcher("/WEB-INF/views/editRecipe.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Вы не можете редактировать этот рецепт.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        User user = userService.getUser(request, response);
        Long recipeId = Long.parseLong(request.getParameter("recipeId"));

        try {
            Recipe recipe = recipeDao.findById(recipeId);

            if (recipe != null && user.getId().equals(recipe.getUser().getId())) {

                String newName = request.getParameter("name");
                String newDescription = request.getParameter("description");
                String newCategory = request.getParameter("preferences");
                int newPreparationTime = Integer.parseInt(request.getParameter("preparationTime"));
                int newServings = Integer.parseInt(request.getParameter("servings"));
                String newIngridients = request.getParameter("ingridients");
                String newSteps = request.getParameter("steps");


                recipe.setName(newName);
                recipe.setDescription(newDescription);
                recipe.setCategory(newCategory);
                recipe.setPreparationTime(newPreparationTime);
                recipe.setServings(newServings);
                recipe.setIngredients(newIngridients);
                recipe.setSteps(newSteps);


                File uploadDir = new File(path);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }
                String oldCoverImage = recipe.getCoverImagePath();
                ImageRecipe coverImage = null;
                // Сохраняем обложку
                for (Part part : request.getParts()) {
                    if (part.getName().equals("cover") && part.getSize() > 0) {
                        fileService.deleteFile(oldCoverImage);
                        String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                        String filePath = path + File.separator + fileName;
                        part.write(filePath);
                        coverImage = new ImageRecipe();
                        recipe.setCoverImagePath(fileName);

                    }
                }


                List<ImageRecipe> images = imageRecipeDao.findByRecipeId(recipeId);
                if (request.getParts() != null){
                    for (ImageRecipe image : images) {
                        fileService.deleteFile(image.getFilePath());
                    }
                    images = null;

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
                }



                recipeDao.updateRecipe(recipe, images);
                response.sendRedirect(request.getContextPath() + "/recipe/" + recipeId);
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Вы не можете редактировать этот рецепт.");
            }
        } catch (DbException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при обновлении рецепта.");
        }
    }
}

