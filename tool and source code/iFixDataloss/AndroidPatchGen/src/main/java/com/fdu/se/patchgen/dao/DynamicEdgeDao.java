package com.fdu.se.patchgen.dao;

import com.fdu.se.patchgen.model.DynamicEdge;
import com.fdu.se.patchgen.model.DynamicWidget;
import com.fdu.se.patchgen.model.DynamicWindow;
import com.fdu.se.patchgen.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DynamicEdgeDao {
    private DynamicWindowDao windowDao = new DynamicWindowDao();
    private DynamicWidgetDao widgetDao = new DynamicWidgetDao();
    private List<DynamicWindow> windows;

    public List<DynamicEdge> getEdges(String label){
        List<DynamicEdge> edges = new ArrayList<>();
        windows = windowDao.getWindows(label);
        List<DynamicWidget> widgets = widgetDao.getWidgets();
        try{
            Connection conn = DBUtil.getSqliteConnection();
            String sql = "select * from dynamic_edge where label = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,label);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                DynamicEdge edge = new DynamicEdge();
                edge.setId(resultSet.getInt("_id"));
                edge.setLabel(resultSet.getString("label"));
                edge.setType(resultSet.getString("type"));
                DynamicWindow source = findWindowById(windows, resultSet.getInt("swindow_id"));
                edge.setSource(source);
                DynamicWindow target = findWindowById(windows, resultSet.getInt("twindow_id"));
                edge.setTarget(target);
                DynamicWidget widget = findWidgetById(widgets, resultSet.getInt("widget_id"));
                edge.setWidget(widget);
                edges.add(edge);
            }
            DBUtil.closeResultset(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(conn);
        }catch(Exception e){
            e.printStackTrace();
        }
        return edges;
    }

    private DynamicWidget findWidgetById(List<DynamicWidget> widgets, int widget_id) {
        if(widget_id == -1){
            return null;
        }else{
            for(DynamicWidget w : widgets){
                int id = (int)w.getId();
                if(id == widget_id){
                    return w;
                }
            }
        }
        return null;
    }

    private DynamicWindow findWindowById(List<DynamicWindow> windows, int swindow_id) {
        for(DynamicWindow window : windows){
            int id = (int)window.getId();
            if(id == swindow_id){
                return window;
            }
        }
        return null;
    }

    public List<DynamicWindow> getAllWindows(){
        return windows;
    }
}
