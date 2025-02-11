package org.example.dao;

import org.example.entity.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingDao {
    private ConnectionProvider connectionProvider;

    public RatingDao(ConnectionProvider connectionProvider) {
        try {
            this.connectionProvider = connectionProvider.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Rating save(Rating rating) {
        String sql = "INSERT INTO \"Rating\" (rating, recipe_id, user_id) VALUES (?, ?,?) RETURNING id";


        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setDouble(3, rating.getRating());
            preparedStatement.setLong(1, rating.getRecipe().getId());
            preparedStatement.setLong(2, rating.getUser().getId());


            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                rating.setId(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rating;
    }

    public void delete(Long ratingId) {
        String sql = "DELETE FROM \"Rating\" WHERE id = ?";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, ratingId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Rating findById(Long id) {
        Rating rating = null;
        String sql = "SELECT * FROM \"Rating\" WHERE id = ?";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {
                rating = new Rating();
                RecipeDao recipeDao = new RecipeDao(connectionProvider);
                UserDao userDao = new UserDao(connectionProvider);
                rating.setId(resultSet.getLong("id"));
                rating.setRecipe(recipeDao.findById(resultSet.getLong("recipe_id")));
                rating.setUser(userDao.findById(resultSet.getLong("user_id")));
                rating.setRating(resultSet.getDouble("rating"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rating;
    }

    public List<Rating> findByRecipeId(Long recipeId) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM \"Rating\" WHERE recipe_id = ?";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, recipeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                RecipeDao recipeDao = new RecipeDao(connectionProvider);
                UserDao userDao = new UserDao(connectionProvider);
                Rating rating = new Rating();
                rating.setId(resultSet.getLong("id"));
                rating.setRecipe(recipeDao.findById(resultSet.getLong("recipe_id")));
                rating.setUser(userDao.findById(resultSet.getLong("user_id")));
                rating.setRating(resultSet.getDouble("rating"));
                ratings.add(rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    public double calculateAverageRating(Long recipeId) {
        String sql = "SELECT AVG(rating) FROM \"Rating\" WHERE recipe_id = ?";
        double average = 0;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, recipeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                average = resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return average;
    }

    public double calculateUserAverageRating(Long userId) {
        String sql = "SELECT AVG(r.rating) FROM \"Rating\" r "
                + "JOIN \"Recipe\" rec ON r.recipe_id = rec.id "
                + "WHERE rec.user_id = ?";
        double average = 0;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                average = resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return average;
    }


    public void saveOrUpdateRating(Rating rating) {
        String selectSql = "SELECT id FROM \"Rating\" WHERE user_id = ? AND recipe_id = ?";
        String updateSql = "UPDATE \"Rating\" SET rating = ? WHERE user_id = ? AND recipe_id = ?";
        String insertSql = "INSERT INTO \"Rating\" (user_id, recipe_id, rating) VALUES (?, ?, ?)";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
            selectStatement.setLong(1, rating.getUser().getId());
            selectStatement.setLong(2, rating.getRecipe().getId());

            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setDouble(1, rating.getRating());
                    updateStatement.setLong(2, rating.getUser().getId());
                    updateStatement.setLong(3, rating.getRecipe().getId());
                    updateStatement.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                    insertStatement.setLong(1, rating.getUser().getId());
                    insertStatement.setLong(2, rating.getRecipe().getId());
                    insertStatement.setDouble(3, rating.getRating());
                    insertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


