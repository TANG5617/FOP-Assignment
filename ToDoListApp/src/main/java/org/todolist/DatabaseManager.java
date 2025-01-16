package org.todolist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/taskdb"; //use your own database table
    private static final String USER = "root";
    private static final String PASSWORD = "YES"; //use your own password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            try {
                if (resource != null) resource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


