package org.example.dao;


import org.example.entity.*;
import org.example.util.DbException;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private ConnectionProvider connectionProvider;


    public UserDao(ConnectionProvider connectionProvider) {
        try {
            this.connectionProvider = connectionProvider.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User save(User user) {
        String sql = "INSERT INTO \"User\" (username, email, password, avatar, \"createdAt\", \"isAdmin\") VALUES (?, ?, ?, ?, ?,?) RETURNING id";
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = today.format(formatter);

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, "default-avatar.jpeg");
            preparedStatement.setString(5, formattedDate);
            preparedStatement.setBoolean(6, false);


            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public void addPreferenceToUser(User user, String preferenceName) throws DbException {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO \"UserPreference\" (user_id, preference_id) VALUES (?, ?) RETURNING id")) {
//            Long userId = getUserIdByName(userName);
            Preference preference = getPreferenceByName(preferenceName);

            Long userId = user.getId();
            Long preferenceId = preference.getId();
            statement.setLong(1, userId);
            statement.setLong(2, preferenceId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long generatedId = resultSet.getLong("id");
                UserPreference userPreference = new UserPreference();
                userPreference.setUser(user);
                userPreference.setPreference(preference);
                userPreference.setId(generatedId);

            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }


    private Preference getPreferenceByName(String preferenceName) throws DbException {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT \"id\" FROM \"Preference\" WHERE \"preferenceName\" = ?")) {
            statement.setString(1, preferenceName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Preference preference = new Preference();
                Long id = resultSet.getLong("id");
                preference.setId(id);
                preference.setPreferenceName(preferenceName);

                return preference;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DbException(e.getMessage());
        }
        return null;
    }


    public boolean isUsernameExists(String username) throws DbException {
        String sql = "SELECT COUNT(*) FROM \"User\" WHERE username = ?";
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return false;
    }

    public boolean isEmailExists(String email) throws DbException {
        String sql = "SELECT COUNT(*) FROM \"User\" WHERE email = ?";
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return false;
    }

    public User findByEmail(String email) throws DbException {
        User user = null;
        String sql = "SELECT * FROM \"User\" WHERE email = ?";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getLong("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPassword(resultSet.getString("password"));
                    user.setAvatar(resultSet.getString("avatar"));
                    user.setCreatedAt(resultSet.getString("createdAt"));
                    user.setIsAdmin(resultSet.getBoolean("isAdmin"));

                }
            }
        } catch (SQLException e) {
            throw new DbException("Ошибка при выполнении запроса findByEmail", e);
        }
        return user;
    }

    public User findById(Long id) {
        String sql = "SELECT * FROM \"User\" WHERE id = ?";
        User user = null;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                user = new User();
                user.setId(resultSet.getLong("id"));
                user.setUsername(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setAvatar(resultSet.getString("avatar"));
                user.setCreatedAt(resultSet.getString("createdAt"));
                user.setIsAdmin(resultSet.getBoolean("isAdmin"));



            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public User updateUser(User updatedUser) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE \"User\" SET username = ?, email = ?, avatar = ? WHERE id = ?")) {

            preparedStatement.setString(1, updatedUser.getUsername());
            preparedStatement.setString(2, updatedUser.getEmail());
            preparedStatement.setString(3, updatedUser.getAvatar());
            preparedStatement.setLong(4, updatedUser.getId());
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                return updatedUser;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        try (Connection connection = connectionProvider.getConnection()) {
            String query = "SELECT * FROM \"User\"";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setUsername(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setAvatar(resultSet.getString("avatar"));
                user.setCreatedAt(resultSet.getString("createdAt"));
                user.setIsAdmin(resultSet.getBoolean("isAdmin"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User revokeGrantRights(User user){
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE \"User\" SET \"isAdmin\" = ? WHERE id = ?")) {

            preparedStatement.setBoolean(1, !user.getIsAdmin());
            preparedStatement.setLong(2, user.getId());
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<User> search(String query) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM \"User\" WHERE 1=1 ";

        if (query != null && !query.isEmpty()) {
            sql += "AND username LIKE ? ";
        }

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            int paramIndex = 1;
            if (query != null && !query.isEmpty()) {
                preparedStatement.setString(paramIndex++, "%" + query + "%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setUsername(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setAvatar(resultSet.getString("avatar"));
                user.setCreatedAt(resultSet.getString("createdAt"));
                user.setIsAdmin(resultSet.getBoolean("isAdmin"));
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return users;
    }

    public void deleteUser(Long userId) throws DbException, SQLException {
        String sql = "DELETE FROM \"User\" WHERE id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DbException("Ошибка при удалении аккаунта", e);
        } finally {
            connection.close();
            statement.close();
        }
    }

}

