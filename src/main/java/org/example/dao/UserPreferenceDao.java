package org.example.dao;

import org.example.entity.Preference;
import org.example.entity.User;
import org.example.entity.UserPreference;
import org.example.util.DbException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserPreferenceDao {
    ConnectionProvider connectionProvider;

    public UserPreferenceDao(ConnectionProvider connectionProvider) {
        try {
            this.connectionProvider = connectionProvider.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserPreference getUserPreference(Long user_id) {
        String sql = "SELECT * FROM \"UserPreference\" WHERE user_id = ?";
        UserPreference userPreference = null;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, user_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                userPreference = new UserPreference();
                UserDao userDao = new UserDao(connectionProvider);
                User user = userDao.findById(user_id);
                PreferenceDao preferenceDao = new PreferenceDao(connectionProvider);
                Preference preference = preferenceDao.findById(resultSet.getLong("preference_id"));

                userPreference.setId(resultSet.getLong("id"));
                userPreference.setUser(user);
                userPreference.setPreference(preference);



            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userPreference;
    }

    public UserPreference updateUserPreference(UserPreference updatedUserPreference) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE \"UserPreference\" SET user_id = ?, preference_id = ? WHERE id = ?")) {
//            preparedStatement.setBlob(1, updatedUser.getAvatar());
            preparedStatement.setLong(1, updatedUserPreference.getUser().getId());
            preparedStatement.setLong(2, updatedUserPreference.getPreference().getId());
            preparedStatement.setLong(3, updatedUserPreference.getId());
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                return updatedUserPreference;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<String> getPreferencesByUserId(Long userId) throws DbException {
        List<String> preferences = new ArrayList<>();
        String sql = "SELECT preference_id FROM \"UserPreference\" WHERE user_id = ?";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PreferenceDao preferenceDao = new PreferenceDao(connectionProvider);
                    Preference preference = preferenceDao.findById(resultSet.getLong("preference_id"));
                    preferences.add(preference.getPreferenceName());
                }
            }
        } catch (SQLException e) {
            throw new DbException("Ошибка при выполнении запроса getPreferencesByUserId", e);
        }
        return preferences;
    }

    public void deletePreferences(Long userId) throws DbException {
        try (Connection connection = connectionProvider.getConnection()) {
            // Удаление существующих Preferences
            PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM \"UserPreference\" WHERE user_id = ?");
            deleteStatement.setLong(1, userId);
            deleteStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DbException("Ошибка при выполнении запроса setPreferences", e);
        }
    }
}
