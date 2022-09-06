package com.fdu.uiautomatortest.dao;

import android.util.Log;

import com.fdu.uiautomatortest.model.DynamicEdge;
import com.fdu.uiautomatortest.model.DynamicWidget;
import com.fdu.uiautomatortest.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class DynamicEdgeDao {
    public int insertDynamicEdge(DynamicEdge edge){
        int insertId = (int)edge.getId();
        try{
            Connection sqlite_conn = DBUtil.getSqliteConnection();
            if(!DBUtil.isExists("dynamic_edge",sqlite_conn)){
                createEdgeTable(sqlite_conn);
            }
//            String sql = "insert into dynamic_edge(_id,label,type,swindow_id,twindow_id,widget_id) values" +
//                    "(?,?,?,?,?,?)";
            String sql = "insert into dynamic_edge(label,type,swindow_id,twindow_id,widget_id) values" +
                    "(?,?,?,?,?)";
            //PreparedStatement preparedStatement = sqlite_conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement preparedStatement = sqlite_conn.prepareStatement(sql);
            //preparedStatement.setInt(1,(int)edge.getId());
            preparedStatement.setString(1,edge.getLabel());
            preparedStatement.setString(2,edge.getType());
//            preparedStatement.setInt(3,idMap.get(edge.getSource().getId()));
//            preparedStatement.setInt(4,idMap.get(edge.getTarget().getId()));
            preparedStatement.setInt(3,(int)edge.getSource().getId());
            preparedStatement.setInt(4,(int)edge.getTarget().getId());
            DynamicWidget dynamicWidget = edge.getWidget();
            if(dynamicWidget != null){
                preparedStatement.setInt(5,(int)dynamicWidget.getId());
            }else{
                preparedStatement.setInt(5,-1);
            }
            int changeRows = preparedStatement.executeUpdate();
            if(changeRows > 0){
                Log.d("sqlite insert info", "insert DynamicEdge " + edge.getId() + " successfully");
                Statement statement = sqlite_conn.createStatement();
                ResultSet rs = statement.executeQuery("select max(_id) from dynamic_edge");
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

    private void createEdgeTable(Connection conn){
        String sql = "CREATE TABLE \"dynamic_edge\" (\n" +
                "  \"_id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "  \"label\" text,\n" +
                "  \"type\" text,\n" +
                "  \"swindow_id\" integer,\n" +
                "  \"twindow_id\" integer,\n" +
                "  \"widget_id\" integer\n" +
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
}
