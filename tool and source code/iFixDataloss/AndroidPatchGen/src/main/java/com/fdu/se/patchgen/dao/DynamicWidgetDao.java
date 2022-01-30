package com.fdu.se.patchgen.dao;

import com.fdu.se.patchgen.model.DynamicWidget;
import com.fdu.se.patchgen.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DynamicWidgetDao {
    public List<DynamicWidget> getWidgets(){
        List<DynamicWidget> widgets = new ArrayList<>();
        try{
            Connection conn = DBUtil.getSqliteConnection();
            String sql = "select * from dynamic_widget";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                DynamicWidget widget = new DynamicWidget();
                widget.setId(resultSet.getInt("_id"));
                widget.setResource_id(resultSet.getString("res_id"));
                widget.setText(resultSet.getString("text"));
                widget.setPack(resultSet.getString("pack"));
                widget.setWidget_class(resultSet.getString("widget_class"));
                widget.setContent_desc(resultSet.getString("content_desc"));
                widget.setCheckable(intToBool(resultSet.getInt("checkable")));
                widget.setChecked(intToBool(resultSet.getInt("checked")));
                widget.setClickable(intToBool(resultSet.getInt("clickable")));
                widget.setEnabled(intToBool(resultSet.getInt("enabled")));
                widget.setFocusable(intToBool(resultSet.getInt("focusable")));
                widget.setFocused(intToBool(resultSet.getInt("focused")));
                widget.setScrollable(intToBool(resultSet.getInt("scrollable")));
                widget.setLong_clickable(intToBool(resultSet.getInt("long_clickable")));
                widget.setSelected(intToBool(resultSet.getInt("selected")));
                widget.setBounds(resultSet.getString("bounds"));
                widget.setCenter(resultSet.getString("center"));
                widgets.add(widget);
            }
            DBUtil.closeResultset(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(conn);
        }catch(Exception e){
            e.printStackTrace();
        }
        return widgets;
    }

    private boolean intToBool(int i){
        return i == 0 ? false : true;
    }
}
