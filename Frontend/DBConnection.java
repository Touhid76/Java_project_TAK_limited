package com.template;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/taklmt";
    private static final String USER = "root";
    private static final String PASS = "iwbac bic sw nimab"; // CHANGE TO YOUR DB PASSWORD

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}