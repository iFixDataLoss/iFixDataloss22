package com.fdu.se.sootanalyze.dao;

import com.fdu.se.sootanalyze.model.Widget;
import com.fdu.se.sootanalyze.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class WidgetDao {
    public void insertWidget(Widget w,long actId){
        try{
            Connection conn = DBUtil.getConnection();
            String sql = "insert into widget(id,widget_type,widget_id_name,event,listener_name," +
                    "event_method,layout_register,act_id,dep_widgets,widget_text) values (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setLong(1,w.getId());
            preparedStatement.setString(2,w.getWidgetType());
            preparedStatement.setString(3, w.getWidgetId());
            preparedStatement.setString(4,w.getEvent());
            preparedStatement.setString(5, w.getListenerName());
            preparedStatement.setString(6, w.getEventMethod());
            preparedStatement.setInt(7,w.getLayoutRegister());
            preparedStatement.setLong(8,actId);
            List<Widget> dWidgets = w.getdWidgets();
            preparedStatement.setString(9,convertToString(dWidgets));
            preparedStatement.setString(10, w.getWidgetText());
            int changeRows = preparedStatement.executeUpdate();
            if(changeRows > 0){
                System.out.println("insert widget " + w.getId() + " successfully");
            }
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(conn);
            if(!dWidgets.isEmpty()){
                for(Widget dWidget:dWidgets){
                    insertDepWidget(dWidget);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * get max id of widget, menu_item, sub_menu, dep_widget
     * @return
     */
    public static long getMaxId(){
        long maxId = 0;
        long id1 = 0;
        long id2 = 0;
        long id3 = 0;
        long id4 = 0;
        try{
            Connection conn = DBUtil.getConnection();
            String sql1 = "select max(id) from widget";
            String sql2 = "select max(id) from menu_item";
            String sql3 = "select max(id) from sub_menu";
            String sql4 = "select max(id) from dep_widget";
            Statement statement = conn.createStatement();
            ResultSet rs1 = statement.executeQuery(sql1);
            if(rs1.next()){
                id1 = rs1.getLong(1);
            }
            ResultSet rs2 = statement.executeQuery(sql2);
            if(rs2.next()){
                id2 = rs2.getLong(1);
            }
            ResultSet rs3 = statement.executeQuery(sql3);
            if(rs3.next()){
                id3 = rs3.getLong(1);
            }
            ResultSet rs4 = statement.executeQuery(sql4);
            if(rs4.next()){
                id4 = rs4.getLong(1);
            }
            maxId = Math.max(Math.max(id1,id2),Math.max(id3,id4));
            DBUtil.closeResultset(rs4);
            DBUtil.closeResultset(rs3);
            DBUtil.closeResultset(rs2);
            DBUtil.closeResultset(rs1);
            DBUtil.closeStatement(statement);
            DBUtil.closeConnection(conn);
        }catch(Exception e){
            e.printStackTrace();
        }
        return maxId;
    }

    private void insertDepWidget(Widget w){
        try{
            Connection conn = DBUtil.getConnection();
            String sql = "insert into dep_widget(id,widget_type,widget_id_name) values (?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setLong(1,w.getId());
            preparedStatement.setString(2,w.getWidgetType());
            preparedStatement.setString(3, w.getWidgetId());
            int changeRows = preparedStatement.executeUpdate();
            if(changeRows > 0){
                System.out.println("insert dep_widget " + w.getId() + " successfully");
            }
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(conn);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String convertToString(List<Widget> depWidgets){
        if(!depWidgets.isEmpty()){
            StringBuilder builder = new StringBuilder();
            for(Widget depWidget:depWidgets){
                long id = depWidget.getId();
                builder.append(id);
                builder.append(',');
            }
            String str = builder.toString();
            return str.substring(0,str.length()-1);
        }
        return null;
    }
}
