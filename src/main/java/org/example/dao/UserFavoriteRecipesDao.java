package org.example.dao;

import org.example.entity.Recipe;
import org.example.entity.User;
import org.example.entity.UserFavoriteRecipes;
import org.example.util.DbException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserFavoriteRecipesDao {
    private ConnectionProvider connectionProvider;


    public UserFavoriteRecipesDao(ConnectionProvider connectionProvider) {
        try {
            this.connectionProvider = connectionProvider.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addRecipeToFavorites(User user, Long recipeId) throws DbException {
        String checkSql = "SELECT COUNT(*) FROM \"UserFavoriteRecipes\" WHERE user_id = ? AND recipe_id = ?";
        String insertSql = "INSERT INTO \"UserFavoriteRecipes\" (user_id, recipe_id) VALUES (?, ?) RETURNING id";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkSql);
             PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {


            checkStatement.setLong(1, user.getId());
            checkStatement.setLong(2, recipeId);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {

                return;
            }

            RecipeDao recipeDao = new RecipeDao(connectionProvider);
            Recipe recipe = recipeDao.findById(recipeId);
            // Выполняем вставку
            insertStatement.setLong(1, user.getId());
            insertStatement.setLong(2, recipeId);
            ResultSet insertResultSet = insertStatement.executeQuery();
            if (insertResultSet.next()) {
                UserFavoriteRecipes userFavoriteRecipes = new UserFavoriteRecipes();
                userFavoriteRecipes.setId(insertResultSet.getLong("id"));
                userFavoriteRecipes.setUser(user);
                userFavoriteRecipes.setRecipe(recipe);

            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DbException("Ошибка при добавлении рецепта в избранное", e);
        }
    }

    public void removeRecipeFromFavorites(User user, Long recipeId) throws DbException {
        String deleteSql = "DELETE FROM \"UserFavoriteRecipes\" WHERE user_id = ? AND recipe_id = ?";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {

            // Выполняем удаление
            deleteStatement.setLong(1, user.getId());
            deleteStatement.setLong(2, recipeId);
            int rowsAffected = deleteStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Рецепт успешно удален из избранного.");
            } else {
                System.out.println("Рецепт не найден в избранном.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DbException("Ошибка при удалении рецепта из избранного", e);
        }
    }

}
