package org.example.dao;


import org.example.entity.Preference;
import org.example.entity.User;
import org.example.util.DbException;
import org.springframework.security.core.parameters.P;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PreferenceDao {
    ConnectionProvider connectionProvider;

    public PreferenceDao(ConnectionProvider connectionProvider) {
        try {
            this.connectionProvider = connectionProvider.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getPreferences() {
        List<String> preferences = new ArrayList<>();

        try (Connection connection = connectionProvider.getConnection()) {
            String query = "SELECT \"preferenceName\" FROM \"Preference\"";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                preferences.add(resultSet.getString("preferenceName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preferences;
    }

    public Preference findById(Long id) {
        String sql = "SELECT * FROM \"Preference\" WHERE id = ?";
        Preference preference = null;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                preference = new Preference();
                preference.setId(resultSet.getLong("id"));
                preference.setPreferenceName(resultSet.getString("preferenceName"));


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preference;
    }

    public Preference findByPreferenceName(String preferenceName) {
        String sql = "SELECT * FROM \"Preference\" WHERE preferenceName = ?";
        Preference preference = null;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, preferenceName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                preference = new Preference();
                preference.setId(resultSet.getLong("id"));
                preference.setPreferenceName(resultSet.getString("preferenceName"));


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preference;
    }

    public Preference save(String preferenceName) {
        String sql = "INSERT INTO \"Preference\" (\"preferenceName\") VALUES (?) RETURNING id";
        Preference preference = null;

        try (Connection connection = connectionProvider.getConnection()) {
            boolean isExist = false;
            PreferenceDao preferenceDao = new PreferenceDao(connectionProvider);
            for (String prefName: preferenceDao.getPreferences()) {
                if (prefName == preferenceName) {
                    isExist = true;
                }
            }
            if (!isExist) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


                    preparedStatement.setString(1, preferenceName);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        preference = new Preference();
                        preference.setId(resultSet.getLong("id"));
                        preference.setPreferenceName(preferenceName);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preference;
    }

    //    public Preference update(Preference updatedPreference) {
//        try (Connection connection = connectionProvider.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE \"Preference\" SET \"preferenceName\" = ? WHERE id = ?")) {
//
//            preparedStatement.setString(1, updatedPreference.getPreferenceName());
//            preparedStatement.setLong(2, updatedPreference.getId());
//            int rowsUpdated = preparedStatement.executeUpdate();
//            if (rowsUpdated > 0) {
//                return updatedPreference;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    public void deletePreference(String preferenceName) throws DbException, SQLException {
        String sql = "DELETE FROM \"Preference\" WHERE \"preferenceName\" = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, preferenceName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DbException("Ошибка при удалении рецепта", e);
        } finally {
            connection.close();
            statement.close();
        }
    }

}

