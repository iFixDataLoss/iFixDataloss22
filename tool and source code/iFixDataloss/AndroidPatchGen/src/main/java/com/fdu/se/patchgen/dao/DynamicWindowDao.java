package com.fdu.se.patchgen.dao;

import com.fdu.se.patchgen.model.DynamicWindow;
import com.fdu.se.patchgen.utils.DBUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DynamicWindowDao {
    public List<DynamicWindow> getWindows(String label){
        List<DynamicWindow> windows = new ArrayList<>();
        try{
            Connection conn = DBUtil.getSqliteConnection();
            String sql = "select * from dynamic_window where label = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,label);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                DynamicWindow window = new DynamicWindow();
                window.setId(resultSet.getInt("_id"));
                window.setName(resultSet.getString("name"));
                window.setLabel(resultSet.getString("label"));
                window.setType(resultSet.getString("type"));
                Document doc = DocumentHelper.parseText(resultSet.getString("hierarchy"));
                window.setWinHierarchy(doc);
                window.setMaySaved(resultSet.getInt("maySaved") == 0 ? false : true);
                windows.add(window);
            }
            DBUtil.closeResultset(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(conn);
        }catch(Exception e){
            e.printStackTrace();
        }
        return windows;
    }
}
