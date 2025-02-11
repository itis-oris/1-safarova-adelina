package org.example.dao;

import org.example.entity.ImageRecipe;
import org.example.entity.Recipe;
import org.example.entity.User;
import org.example.util.DbException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RecipeDao {
    private ConnectionProvider connectionProvider;

    public RecipeDao(ConnectionProvider connectionProvider) {
        try {
            this.connectionProvider = connectionProvider.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Recipe saveRecipe(Recipe recipe, User user, ImageRecipe coverImage, List<ImageRecipe> images) {
        String sql = "INSERT INTO \"Recipe\" (name, description, category, \"preparationTime\", servings, ingredients, steps, user_id, \"createdAt\",  \"coverImagePath\") VALUES (?, ?, ?, ?,?,?,?,?,?,?) RETURNING id";
        String sqlImage = "INSERT INTO \"ImageRecipe\" (path, recipe_id) VALUES (?, ?) RETURNING id";

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = today.format(formatter);

        try (Connection connection = connectionProvider.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


                preparedStatement.setString(1, recipe.getName());
                preparedStatement.setString(2, recipe.getDescription());
                preparedStatement.setString(3, recipe.getCategory());
                preparedStatement.setInt(4, recipe.getPreparationTime());
                preparedStatement.setInt(5, recipe.getServings());
                preparedStatement.setString(6, recipe.getIngredients());
                preparedStatement.setString(7, recipe.getSteps());
//            preparedStatement.setBytes(8, recipe.getImage());
                preparedStatement.setLong(8, user.getId());
                preparedStatement.setString(9, formattedDate);
                preparedStatement.setString(10, recipe.getCoverImagePath());


                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    recipe.setId(resultSet.getLong("id"));
                }
                for (ImageRecipe image : images) {
                    try (PreparedStatement preparedStatementImage = connection.prepareStatement(sqlImage)) {
                        preparedStatementImage.setString(1, image.getFilePath());
                        preparedStatementImage.setLong(2, image.getRecipe().getId());

                        preparedStatementImage.addBatch();
                        preparedStatementImage.executeBatch();
                    }
                }


                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recipe;
    }


    public List<Recipe> findCreatedRecipesByUserId(Long userId) {
        String sql = "SELECT * FROM \"Recipe\" WHERE user_id = ?";
        List<Recipe> recipes = new ArrayList<>();

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(resultSet.getLong("id"));
                recipe.setName(resultSet.getString("name"));
                recipe.setDescription(resultSet.getString("description"));
                recipe.setCategory(resultSet.getString("category"));
                recipe.setPreparationTime(resultSet.getInt("preparationTime"));
                recipe.setServings(resultSet.getInt("servings"));
                recipe.setIngredients(resultSet.getString("ingredients"));
                recipe.setSteps(resultSet.getString("steps"));
                recipe.setCoverImagePath(resultSet.getString("coverImagePath"));


                recipes.add(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recipes;
    }

    public List<Recipe> findFavoriteRecipesByUserId(Long userId) {
        String sql = "SELECT * FROM \"UserFavoriteRecipes\" WHERE user_id = ?";
        List<Recipe> recipes = new ArrayList<>();

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                RecipeDao recipeDao = new RecipeDao(connectionProvider);
                Recipe recipe = recipeDao.findById(resultSet.getLong("recipe_id"));


                recipes.add(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recipes;
    }

    public Recipe findById(Long id) {
        String sql = "SELECT * FROM \"Recipe\" WHERE id = ?";
        Recipe recipe = null;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                recipe = new Recipe();
                recipe = new Recipe();
                recipe.setId(resultSet.getLong("id"));
                recipe.setName(resultSet.getString("name"));
                recipe.setDescription(resultSet.getString("description"));
                recipe.setCategory(resultSet.getString("category"));
                recipe.setPreparationTime(resultSet.getInt("preparationTime"));
                recipe.setServings(resultSet.getInt("servings"));
                recipe.setIngredients(resultSet.getString("ingredients"));
                recipe.setSteps(resultSet.getString("steps"));
                recipe.setCoverImagePath(resultSet.getString("coverImagePath"));
//

                Long userId = resultSet.getLong("user_id");
                UserDao userDao = new UserDao(connectionProvider);
                User user = userDao.findById(userId);
                recipe.setUser(user);
                recipe.setCreatedAt(resultSet.getString("createdAt"));


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recipe;
    }

    public void deleteRecipe(Long recipeId) throws DbException, SQLException {
        String sql = "DELETE FROM \"Recipe\" WHERE id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, recipeId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DbException("Ошибка при удалении рецепта", e);
        } finally {
            connection.close();
            statement.close();
        }
    }

    public void updateRecipe(Recipe recipe, List<ImageRecipe> images) throws DbException {
        String updateSql = "UPDATE \"Recipe\" SET name = ?, description = ?, category = ?, \"preparationTime\" = ?," +
                " servings = ?, ingredients = ?, steps = ?, \"coverImagePath\" = ? WHERE id = ?";
//        String updateImagerecipe = "UPDATE \"ImageRecipe\" SET path = ? WHERE recipe_id = ?";
        String deleteImagerecipe = "DELETE FROM \"ImageRecipe\" WHERE recipe_id = ?";
        String insertImagerecipe = "INSERT INTO \"ImageRecipe\" (path, recipe_id) VALUES(?,?) RETURNING id";
        try (Connection connection = connectionProvider.getConnection()) {
            connection.setAutoCommit(false);
            try(PreparedStatement statement = connection.prepareStatement(updateSql)){

                statement.setString(1, recipe.getName());
                statement.setString(2, recipe.getDescription());
                statement.setString(3, recipe.getCategory());
                statement.setInt(4, recipe.getPreparationTime());
                statement.setInt(5, recipe.getServings());
                statement.setString(6, recipe.getIngredients());
                statement.setString(7, recipe.getSteps());
                statement.setString(8, recipe.getCoverImagePath());
                statement.setLong(9, recipe.getId());

                statement.executeUpdate();


            }
            try (PreparedStatement statement = connection.prepareStatement(deleteImagerecipe)){
                statement.setLong(1, recipe.getId());
                statement.executeUpdate();

            }
            for (ImageRecipe image : images) {
                try (PreparedStatement preparedStatementImage = connection.prepareStatement(insertImagerecipe)) {
                    preparedStatementImage.setString(1, image.getFilePath());
                    preparedStatementImage.setLong(2, recipe.getId());

                    preparedStatementImage.addBatch();
                    preparedStatementImage.executeBatch();
                }
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DbException("Ошибка при обновлении рецепта", e);
        }
    }

    public List<Recipe> search(String query, String category, String cookingTime) {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM \"Recipe\" WHERE 1=1 ";

        if (query != null && !query.isEmpty()) {
            sql += "AND name LIKE ? ";
        }
        if (category != null && !category.isEmpty()) {
            sql += "AND category = ? ";
        }
        if (cookingTime != null && !cookingTime.isEmpty()) {
            sql += "AND preparationTime <= ? ";
        }


        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            int paramIndex = 1;
            if (query != null && !query.isEmpty()) {
                preparedStatement.setString(paramIndex++, "%" + query + "%");
            }
            if (category != null && !category.isEmpty()) {
                preparedStatement.setString(paramIndex++, category);
            }
            if (cookingTime != null && !cookingTime.isEmpty()) {
                preparedStatement.setInt(paramIndex++, Integer.parseInt(cookingTime));
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(resultSet.getLong("id"));
                recipe.setName(resultSet.getString("name"));
                recipe.setDescription(resultSet.getString("description"));
                recipe.setCategory(resultSet.getString("category"));
                recipe.setPreparationTime(resultSet.getInt("preparationTime"));
                recipe.setServings(resultSet.getInt("servings"));
                recipe.setIngredients(resultSet.getString("ingredients"));
                recipe.setSteps(resultSet.getString("steps"));
                recipe.setCoverImagePath(resultSet.getString("coverImagePath"));


                recipes.add(recipe);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return recipes;
    }

    public List<Recipe> findAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM \"Recipe\"";


        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(resultSet.getLong("id"));
                recipe.setName(resultSet.getString("name"));
                recipe.setDescription(resultSet.getString("description"));
                recipe.setCategory(resultSet.getString("category"));
                recipe.setPreparationTime(resultSet.getInt("preparationTime"));
                recipe.setServings(resultSet.getInt("servings"));
                recipe.setIngredients(resultSet.getString("ingredients"));
                recipe.setSteps(resultSet.getString("steps"));
                recipe.setCoverImagePath(resultSet.getString("coverImagePath"));

                recipes.add(recipe);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return recipes;
    }

}

