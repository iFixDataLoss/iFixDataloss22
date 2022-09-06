package com.fdu.uiautomatortest.dao;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.fdu.uiautomatortest.model.DynamicWindow;
import com.fdu.uiautomatortest.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DynamicWindowDao {
    public int insertDynamicWindow(DynamicWindow dw){
        int insertId = (int)dw.getId();
        try{
            Connection sqlite_conn = DBUtil.getSqliteConnection();
            if(!DBUtil.isExists("dynamic_window",sqlite_conn)){
                createWindowTable(sqlite_conn);
            }
            //String sql = "insert into dynamic_window(_id,name,label,type) values (?,?,?,?)";
            String sql = "insert into dynamic_window(name,label,type,hierarchy,maySaved,savedWidgets) values (?,?,?,?,?,?)";
            //PreparedStatement preparedStatement = sqlite_conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement preparedStatement = sqlite_conn.prepareStatement(sql);
            //preparedStatement.setInt(1,(int)dw.getId());
            preparedStatement.setString(1,dw.getName());
            preparedStatement.setString(2, dw.getLabel());
            preparedStatement.setString(3, dw.getType());
            preparedStatement.setString(4,dw.getWinHierarchy().asXML());
            preparedStatement.setInt(5,connvertToInt(dw.isMaySaved()));
            preparedStatement.setString(6, JSON.toJSONString(dw.getSavedWidgets()));
            int changeRows = preparedStatement.executeUpdate();
            if(changeRows > 0){
                Log.d("sqlite insert info", "insert DynamicWindow " + dw.getId() + " successfully");
                Statement statement = sqlite_conn.createStatement();
                ResultSet rs = statement.executeQuery("select max(_id) from dynamic_window");
                if(rs.next()){
                    insertId = rs.getInt(1);
                }
                DBUtil.closeResultset(rs);
                DBUtil.closeStatement(statement);
            }
//            ResultSet rs = preparedStatement.getGeneratedKeys();
//            if(rs.next()){
//                insertId = rs.getInt(1);
//            }
//            DBUtil.closeResultset(rs);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(sqlite_conn);
        }catch(Exception e){
            e.printStackTrace();
        }
        return insertId;
    }

    private void createWindowTable(Connection conn){
        String sql = "CREATE TABLE \"dynamic_window\" (\n" +
                "  \"_id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "  \"name\" text,\n" +
                "  \"label\" text,\n" +
                "  \"type\" text,\n" +
                "  \"hierarchy\" text,\n" +
                "  \"maySaved\" integer,\n" +
                "  \"savedWidgets\" text\n" +
                ")";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(conn);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private int connvertToInt(Boolean b){
        if(b.booleanValue() == true){
            return 1;
        }
        return 0;
    }
}
