package ru.khusyainov.gb.java3.hw2.server;

import ru.khusyainov.gb.java3.hw2.Client;

import java.sql.*;
import java.text.MessageFormat;

public class SQLiteAuthService implements AuthService {
    private Connection connection;
    private Statement stmt;

    @Override
    public void start() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException | SQLException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Client getClientByLoginPassword(String login, String password) {
        try {
            ResultSet rs = stmt.executeQuery(MessageFormat.format(
                    "SELECT * FROM users WHERE login = ''{0}'' AND password = ''{1}'';", login, password));
            if (rs.next()) {
                return new Client(rs.getString("login"), rs.getString("password"), rs.getString("nick"));
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
