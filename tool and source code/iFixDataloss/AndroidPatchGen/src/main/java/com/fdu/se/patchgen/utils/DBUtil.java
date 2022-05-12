package com.fdu.se.patchgen.utils;

import java.sql.*;

public class DBUtil {
    //private final static String SQLITEDRIVER = "org.sqlite.JDBC";
    private final static String SQLITEDRIVER = LoadProperties.get("SQLITEDRIVER");

    public static Connection getSqliteConnection(){
        Connection connection = null;
        try{
            Class.forName(SQLITEDRIVER);
            //String dbPath = "jdbc:sqlite:C:/Users/William/Nox_share/ImageShare/navgraph.db";
            String dbPath = "jdbc:sqlite:" + LoadProperties.get("SQLITEDBPATH");
            connection = DriverManager.getConnection(dbPath);
            System.out.println("connect successfully");
        }catch(Exception e){
            System.err.println("connect failed");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null)
                connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection = null;
    }
    public static void closePreparedStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null)
                preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        preparedStatement = null;
    }
    public static void closeStatement(Statement statement){
        try{
            if(statement != null){
                statement.close();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        statement = null;
    }
    public static void closeResultset(ResultSet resultSet){
        try{
            if(resultSet != null)
                resultSet.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        resultSet = null;
    }
}
