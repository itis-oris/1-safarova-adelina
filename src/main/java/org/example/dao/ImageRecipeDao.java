package org.example.dao;

import org.example.entity.Comment;
import org.example.entity.ImageRecipe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImageRecipeDao {
    private ConnectionProvider connectionProvider;

    public ImageRecipeDao(ConnectionProvider connectionProvider) {
        try {
            this.connectionProvider = connectionProvider.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<ImageRecipe> findByRecipeId(Long recipeId) {
        List<ImageRecipe> images = new ArrayList<>();
        String sql = "SELECT * FROM \"ImageRecipe\" WHERE recipe_id = ? ";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, recipeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                RecipeDao recipeDao = new RecipeDao(connectionProvider);
                ImageRecipe image = new ImageRecipe();
                image.setId(resultSet.getLong("id"));
                image.setFilePath(resultSet.getString("path"));
                image.setRecipe(recipeDao.findById(resultSet.getLong("recipe_id")));
                images.add(image);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return images;
    }
}
