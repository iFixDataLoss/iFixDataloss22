package com.fdu.se.sootanalyze.dao;

import com.fdu.se.sootanalyze.model.MenuItem;
import com.fdu.se.sootanalyze.model.SubMenu;
import com.fdu.se.sootanalyze.model.Widget;
import com.fdu.se.sootanalyze.model.WindowNode;
import com.fdu.se.sootanalyze.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class WindowNodeDao {
    private MenuItemDao menuItemDao = new MenuItemDao();
    private SubMenuDao subMenuDao = new SubMenuDao();

    public void insertWindowNode(WindowNode node){
        try{
            Connection conn = DBUtil.getConnection();
            String sql = "insert into window_node(id,window_name,window_label,window_type,has_opt_menu,opt_menu_id) values " +
                    "(?,?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setLong(1,node.getId());
            preparedStatement.setString(2,node.getName());
            preparedStatement.setString(3, node.getLabel());
            preparedStatement.setString(4,node.getType());
            preparedStatement.setInt(5, node.getHasOptionsMenu());
            WindowNode optMenuNode = node.getOptionsMenuNode();
            if(optMenuNode != null){
                insertWindowNode(optMenuNode);//insert optionsMenuNode
                long menuId = optMenuNode.getId();
                preparedStatement.setLong(6,menuId);
                List<Widget> menuWidgets = optMenuNode.getWidgets();
                for(Widget menuWidget:menuWidgets){
                    if(menuWidget instanceof MenuItem){
                        MenuItem item = (MenuItem)menuWidget;
                        menuItemDao.insertMenuItem(item,-1,menuId);
                    }
                    if(menuWidget instanceof SubMenu){
                        SubMenu sub = (SubMenu)menuWidget;
                        long subId = sub.getId();
                        List<MenuItem> subItems = sub.getItems();
                        subMenuDao.insertSubMenu(sub,menuId);
                        for(MenuItem subItem:subItems){
                            menuItemDao.insertMenuItem(subItem,subId,menuId);
                        }
                    }
                }
            }else{
                preparedStatement.setLong(6,-1);
            }
            int changeRows = preparedStatement.executeUpdate();
            if(changeRows > 0){
                System.out.println("insert WindowNode "+node.getId()+" successfully");
            }
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(conn);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * get max id of window_node
     * @return
     */
    public static long getMaxId(){
        long maxId = 0;
        try{
            Connection conn = DBUtil.getConnection();
            String sql = "select max(id) from window_node";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()){
                maxId = rs.getLong(1);
            }
            DBUtil.closeResultset(rs);
            DBUtil.closeStatement(statement);
            DBUtil.closeConnection(conn);
        }catch (Exception e){
            e.printStackTrace();
        }
        return maxId;
    }

    private String convertToString(List<String> words){
        if(!words.isEmpty()){
            StringBuilder builder = new StringBuilder();
            for(String word : words){
                builder.append(word);
                builder.append(",");
            }
            String str = builder.toString();
            return str.substring(0,str.length()-1);
        }
        return null;
    }
}
