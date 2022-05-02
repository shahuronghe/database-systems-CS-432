package com.lovlalsha.studentsystem.helpers;

import android.content.Context;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseConnection {
    private static final String URL = "jdbc:oracle:thin:@castor.cc.binghamton.edu:1521:ACAD111";
    private static final String DEFAULT_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String USER = "sronghe1";
    private static final String PASSWORD = "tahiti80";
    private static Connection connection = null;

    public static synchronized Connection getInstance(Context context) {

        if (connection == null) {
            try {
                connection = createConnection();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            Toast.makeText(context, "Database Connected!", Toast.LENGTH_SHORT).show();
        }
        return connection;
    }

    public static synchronized void disconnect(Context context) {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            Toast.makeText(context, "Database Connected!", Toast.LENGTH_SHORT).show();
        }
    }

    /*public Connection connect() {
        try {
            this.connection = createConnection();

        } catch (Exception e) {
            Log.e("DatabaseConnection", "Error Connecting to Database", e);
            Toast.makeText(mContext.getApplicationContext(), "Error Connecting to Database", Toast.LENGTH_LONG).show();
        }

        // Toast.makeText(mContext.getApplicationContext(), "Connected to Database!", Toast.LENGTH_SHORT).show();
        return connection;
    }

    public Connection getCursor() {
        return connection;
    }

    public void disconnect() throws SQLException {
        connection.close();
    }*/

    public static Connection createConnection(String driver, String url, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static Connection createConnection() throws ClassNotFoundException, SQLException {
        return createConnection(DEFAULT_DRIVER, URL, USER, PASSWORD);
    }


    public static ArrayList<HashMap<String, String>> getResultMap(Object object) throws SQLException {

        ArrayList<HashMap<String, String>> map3 = new ArrayList<>();

        ResultSet resultSet = (ResultSet) object;

        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (resultSet.next()) {
            HashMap<String, String> map = new HashMap<>();
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));
                String columnName = rsmd.getColumnName(i);
                map.put(columnName, columnValue);
            }
            map3.add(map);
            System.out.println();
        }

        return map3;
    }
}
