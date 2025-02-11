package org.example.dao;

import org.example.entity.Comment;

import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CommentDao {
    private ConnectionProvider connectionProvider;

    public CommentDao(ConnectionProvider connectionProvider) {
        try {
            this.connectionProvider = connectionProvider.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Comment save(Comment comment) {
        String sql = "INSERT INTO \"Comment\" (recipe_id, user_id, content, \"createdAt\") VALUES (?, ?, ?, ?) RETURNING id";
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = today.format(formatter);

        try (Connection connection = connectionProvider.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, comment.getRecipe().getId());
            preparedStatement.setLong(2, comment.getUser().getId());
            preparedStatement.setString(3, comment.getContent());
            preparedStatement.setString(4, formattedDate);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                comment.setId(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comment;
    }

    public void delete(Long commentId) {
        String sql = "DELETE FROM \"Comment\" WHERE id = ?";

        try (Connection connection = connectionProvider.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, commentId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Comment findById(Long id) {
        Comment comment = null;
        String sql = "SELECT * FROM \"Comment\" WHERE id = ?";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                comment = new Comment();
                RecipeDao recipeDao = new RecipeDao(connectionProvider);
                UserDao userDao = new UserDao(connectionProvider);
                comment.setId(resultSet.getLong("id"));
                comment.setRecipe(recipeDao.findById(resultSet.getLong("recipe_id")));
                comment.setUser(userDao.findById(resultSet.getLong("user_id")));
                comment.setContent(resultSet.getString("content"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comment;
    }

    public List<Comment> findByRecipeId(Long recipeId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM \"Comment\" WHERE recipe_id = ? ORDER BY \"createdAt\" DESC";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, recipeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                RecipeDao recipeDao = new RecipeDao(connectionProvider);
                UserDao userDao = new UserDao(connectionProvider);
                Comment comment = new Comment();
                comment.setId(resultSet.getLong("id"));
                comment.setRecipe(recipeDao.findById(resultSet.getLong("recipe_id")));
                comment.setUser(userDao.findById(resultSet.getLong("user_id")));
                comment.setContent(resultSet.getString("content"));
                comment.setCreatedAt(resultSet.getString("createdAt"));
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public List<Comment> findByUserId(Long userId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM \"Comment\" WHERE user_id = ? ORDER BY \"createdAt\" DESC";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                RecipeDao recipeDao = new RecipeDao(connectionProvider);
                UserDao userDao = new UserDao(connectionProvider);
                Comment comment = new Comment();
                comment.setId(resultSet.getLong("id"));
                comment.setRecipe(recipeDao.findById(resultSet.getLong("recipe_id")));
                comment.setUser(userDao.findById(resultSet.getLong("user_id")));
                comment.setContent(resultSet.getString("content"));
                comment.setCreatedAt(resultSet.getString("createdAt"));
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }
}


