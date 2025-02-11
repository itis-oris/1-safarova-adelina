package org.example.controllers;

import org.example.dao.*;
import org.example.entity.*;
import org.example.service.UserService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

@WebServlet("/recipe/*")
public class showRecipe extends HttpServlet {

    private RecipeDao recipeDao;

    private CommentDao commentDao;
    private RatingDao ratingDao;
    private UserService userService;
    private ImageRecipeDao imageRecipeDao;
    private String path;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        recipeDao = (RecipeDao) getServletContext().getAttribute("recipeDao");
        commentDao = (CommentDao) getServletContext().getAttribute("commentDao");
        ratingDao = (RatingDao) getServletContext().getAttribute("ratingDao");
        imageRecipeDao = (ImageRecipeDao) getServletContext().getAttribute("imageRecipeDao");
        userService = (UserService) getServletContext().getAttribute("userService");
        path = (String) getServletContext().getAttribute("path");


    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.matches("^/\\d+$")) {
            String recipeIdStr = pathInfo.substring(1);
            try {
                Long recipeId = Long.parseLong(recipeIdStr);
                Recipe recipe = recipeDao.findById(recipeId);

                if (recipe != null) {
                    request.setAttribute("recipe", recipe);
                    String ing = recipe.getIngredients();
                    System.out.println("что по запятым " + ing);


                    List<Comment> comments = commentDao.findByRecipeId(recipeId);
                    request.setAttribute("comments", comments);

                    double averageRating = ratingDao.calculateAverageRating(recipeId);
                    request.setAttribute("averageRating", averageRating);

                    List<ImageRecipe> images = imageRecipeDao.findByRecipeId(recipeId);
                    request.setAttribute("images", images);

                    User currentUser = userService.getUser(request, response);
                    if (currentUser != null && recipe.getUser().getId().equals(currentUser.getId())) {
                        request.setAttribute("user", currentUser);
                        request.setAttribute("isAuthor", true);
                    } else {
                        request.setAttribute("isAuthor", false);
                    }
                    User author = recipe.getUser();
                    String authorName = author.getUsername();
                    Long authorId = author.getId();
//                    request.setAttribute("author", author);
                    request.setAttribute("authorName", authorName);
                    request.setAttribute("authorId", authorId);




                    request.getRequestDispatcher("/WEB-INF/views/showRecipe.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Рецепт не найден");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный идентификатор рецепта");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Рецепт не найден");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");



        User currentUser = userService.getUser(request, response);

        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.matches("^/\\d+$")) {
            Long recipeId = Long.parseLong(pathInfo.substring(1));
            Recipe recipe = recipeDao.findById(recipeId);

            if (recipe != null && currentUser != null) {
                String action = request.getParameter("action");

                if ("delete".equals(action) && recipe.getUser().getId().equals(currentUser.getId())) {
                    try {
                        recipeDao.deleteRecipe(recipeId);
                        response.sendRedirect(request.getContextPath() + "/cookbook");
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при удалении рецепта");
                    }
                } else if ("rate".equals(action)) {
                    double ratingValue = Integer.parseInt(request.getParameter("rating"));
                    Rating rating = new Rating();
                    rating.setRecipe(recipe);
                    rating.setUser(currentUser);
                    rating.setRating(ratingValue);

                    ratingDao.saveOrUpdateRating(rating);
                    response.sendRedirect(request.getContextPath() + "/recipe/" + recipeId);
                } else if ("addComment".equals(action)) {
                    String commentText = request.getParameter("commentText");
                    Comment comment = new Comment();
                    comment.setRecipe(recipe);
                    comment.setUser(currentUser);
                    comment.setContent(commentText);

                    commentDao.save(comment);
                    response.sendRedirect(request.getContextPath() + "/recipe/" + recipeId);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Неверное действие");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный идентификатор рецепта");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный идентификатор рецепта");
        }
    }
}
