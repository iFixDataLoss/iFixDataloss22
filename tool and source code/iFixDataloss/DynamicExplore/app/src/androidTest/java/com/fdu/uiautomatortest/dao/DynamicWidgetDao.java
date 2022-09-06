package com.fdu.uiautomatortest.dao;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.fdu.uiautomatortest.model.DynamicWidget;
import com.fdu.uiautomatortest.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DynamicWidgetDao {
    public int insertDynamicWidget(DynamicWidget widget){
        int insertId = (int)widget.getId();
        try{
            Connection sqlite_conn = DBUtil.getSqliteConnection();
            if(!DBUtil.isExists("dynamic_widget",sqlite_conn)){
                createWidgetTable(sqlite_conn);
            }
//            String sql = "insert into dynamic_widget(_id,res_id,text,pack,widget_class,content_desc,checkable," +
//                    "checked,clickable,enabled,focusable,focused,scrollable,long_clickable,selected,bounds,center,complete_text) values" +
//                    "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            String sql = "insert into dynamic_widget(res_id,text,pack,widget_class,content_desc,checkable," +
                    "checked,clickable,enabled,focusable,focused,scrollable,long_clickable,selected,bounds,center,complete_text) values" +
                    "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //PreparedStatement preparedStatement = sqlite_conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement preparedStatement = sqlite_conn.prepareStatement(sql);
            //preparedStatement.setInt(1,(int)widget.getId());
            preparedStatement.setString(1, widget.getResource_id());
            preparedStatement.setString(2, widget.getText());
            preparedStatement.setString(3, widget.getPack());
            preparedStatement.setString(4, widget.getWidget_class());
            preparedStatement.setString(5, widget.getContent_desc());
            preparedStatement.setInt(6,connvertToInt(widget.getCheckable()));
            preparedStatement.setInt(7,connvertToInt(widget.getChecked()));
            preparedStatement.setInt(8,connvertToInt(widget.getClickable()));
            preparedStatement.setInt(9,connvertToInt(widget.getEnabled()));
            preparedStatement.setInt(10,connvertToInt(widget.getFocusable()));
            preparedStatement.setInt(11,connvertToInt(widget.getFocused()));
            preparedStatement.setInt(12,connvertToInt(widget.getScrollable()));
            preparedStatement.setInt(13,connvertToInt(widget.getLong_clickable()));
            preparedStatement.setInt(14,connvertToInt(widget.getSelected()));
            Rect bounds = widget.getBounds();
            String bounds_str = bounds.left + "," + bounds.top + "," + bounds.right + "," + bounds.bottom;
            preparedStatement.setString(15,bounds_str);
            Point center = widget.getCenter();
            String center_str = center.x + "," + center.y;
            preparedStatement.setString(16,center_str);
            preparedStatement.setString(17,widget.getCompleteText());
            int changeRows = preparedStatement.executeUpdate();
            if(changeRows > 0){
                Log.d("sqlite insert info", "insert DynamicWidget " + widget.getId() + " successfully");
                Statement statement = sqlite_conn.createStatement();
                ResultSet rs = statement.executeQuery("select max(_id) from dynamic_widget");
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

    private void createWidgetTable(Connection conn){
        String sql = "CREATE TABLE \"dynamic_widget\" (\n" +
                "  \"_id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "  \"res_id\" text,\n" +
                "  \"text\" text,\n" +
                "  \"pack\" text,\n" +
                "  \"widget_class\" text,\n" +
                "  \"content_desc\" text,\n" +
                "  \"checkable\" integer,\n" +
                "  \"checked\" integer,\n" +
                "  \"clickable\" integer,\n" +
                "  \"enabled\" integer,\n" +
                "  \"focusable\" integer,\n" +
                "  \"focused\" integer,\n" +
                "  \"scrollable\" integer,\n" +
                "  \"long_clickable\" integer,\n" +
                "  \"selected\" integer,\n" +
                "  \"bounds\" text,\n" +
                "  \"center\" text,\n" +
                "  \"complete_text\" text\n" +
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
