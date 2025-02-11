package org.example.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.api.FlywayException;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionProvider {
    private static ConnectionProvider _instance;


    private static HikariDataSource dataSource;


    public synchronized static ConnectionProvider getInstance() throws SQLException {
        if (_instance == null) {
            synchronized (ConnectionProvider.class) {
                if (_instance == null) {
                    _instance = new ConnectionProvider();
                }
            }
        }
        return _instance;
    }

    private ConnectionProvider() {
        try {
            Class.forName("org.postgresql.Driver");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/culinaryExchange");
            config.setUsername("postgres");
            config.setPassword("123");
            config.setConnectionTimeout(50000);
            config.setMaximumPoolSize(10);
            dataSource = new HikariDataSource(config);


        } catch (ClassNotFoundException | FlywayException e) {
            e.printStackTrace();
        }
    }


    public synchronized Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        return connection;
    }

    public synchronized void releaseConnection(Connection connection) throws SQLException {
        connection.close();

    }

    public void destroy() {
        dataSource.close();

    }
}
