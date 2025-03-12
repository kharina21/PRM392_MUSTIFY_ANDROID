package com.example.musicapplicationtemplate.sqlserver;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext {
    String classes = "net.sourceforge.jtds.jdbc.Driver";
    protected static String ip = "10.33.10.138";
    protected static String port = "1433";
    protected static String db = "MUSTIFY";
    protected static String user = "sa";
    protected static String pass = "123";
    protected Connection connection;

    public DBContext() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName(classes);
            String conUrl = "jdbc:jtds:sqlserver://" + ip + ":1433;databaseName=MUSTIFY;encrypt=false;trustServerCertificate=true";
            connection = DriverManager.getConnection(conUrl, user, pass);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean testConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return true;
            } else {
                Class.forName(classes);
                String conUrl = "jdbc:jtds:sqlserver://" + ip + ":1433;databaseName=MUSTIFY;encrypt=true;trustServerCertificate=true";
                connection = DriverManager.getConnection(conUrl, user, pass);
                return connection != null && !connection.isClosed(); // Kiểm tra lại sau khi tạo
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
