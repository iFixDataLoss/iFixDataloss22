package com.fdu.uiautomatortest.dao;

import android.util.Log;

import com.fdu.uiautomatortest.model.DynamicEdge;
import com.fdu.uiautomatortest.model.DynamicGraph;
import com.fdu.uiautomatortest.model.DynamicWidget;
import com.fdu.uiautomatortest.model.DynamicWindow;

import java.util.List;
import java.util.Set;

public class DynamicGraphDao {
    private DynamicWindowDao windowDao = new DynamicWindowDao();
    private DynamicEdgeDao edgeDao = new DynamicEdgeDao();
    private DynamicWidgetDao widgetDao = new DynamicWidgetDao();
    public void insertDynamicGraph(DynamicGraph g){
        List<DynamicEdge> edges = g.getEdges();
        Set<DynamicWindow>  windows = g.getNodes();
//        Map<Long, Integer> idMap = new HashMap<>();//DynamicWindow id: <initial id, id after insert>
        for(DynamicWindow dw : windows){
//            long id = dw.getId();
            int windowId = windowDao.insertDynamicWindow(dw);
//            idMap.put(id,windowId);
            dw.setId(windowId);
        }
        for(DynamicEdge e : edges){
            DynamicWidget widget = e.getWidget();
            if(widget != null){
                int widgetId = widgetDao.insertDynamicWidget(widget);
                widget.setId(widgetId);
            }
            int edgeId = edgeDao.insertDynamicEdge(e);
            e.setId(edgeId);
        }
    }
}
