package org.example.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.dao.*;
import org.example.service.FileService;
import org.example.service.UserService;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.example.dao.ConnectionProvider;
import org.example.util.PropertyReader;

@WebListener
public class InitListener implements ServletContextListener {
    private ConnectionProvider connectionProvider;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            connectionProvider = ConnectionProvider.getInstance();
            sce.getServletContext().setAttribute("commentDao", new CommentDao(connectionProvider));
            sce.getServletContext().setAttribute("preferenceDao", new PreferenceDao(connectionProvider));
            sce.getServletContext().setAttribute("ratingDao", new RatingDao(connectionProvider));
            sce.getServletContext().setAttribute("recipeDao", new RecipeDao(connectionProvider));
            UserDao userDao = new UserDao(connectionProvider);
            sce.getServletContext().setAttribute("userDao", userDao);
            sce.getServletContext().setAttribute("userFavoriteRecipesDao", new UserFavoriteRecipesDao(connectionProvider));
            sce.getServletContext().setAttribute("userPreferenceDao", new UserPreferenceDao(connectionProvider));
            sce.getServletContext().setAttribute("imageRecipeDao", new ImageRecipeDao(connectionProvider));
            sce.getServletContext().setAttribute("userService", new UserService());
            String path = PropertyReader.getProperty("path");
            sce.getServletContext().setAttribute("path", path);
            sce.getServletContext().setAttribute("fileService", new FileService(userDao, path));

            Map<UUID, Long> userSessions = new HashMap<>();

            sce.getServletContext().setAttribute("USER_SESSIONS", userSessions);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            ConnectionProvider.getInstance().destroy();
            System.out.println("Соединение с БД закрыто.");
        } catch (SQLException e) {
            System.out.println("Соединение с БД не было установлено.");
            throw new RuntimeException(e);
        }
    }
}
