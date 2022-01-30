package com.fdu.se.sootanalyze.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WindowNode {
    private long id;
    private String name; //window full name
    private String label;//app name
    private String type;
    private int hasOptionsMenu = 0;
    private WindowNode optionsMenuNode;
    private int hasSysEventCb = 0;//has onSaveInstanceState or onRestoreInstanceState
    private List<String> sysEventCbs = new ArrayList<>();
    Set<TransitionEdge> outEdges = new HashSet<>();
    List<Widget> widgets = new ArrayList<>();
    private List<String> windowWords = new ArrayList<>();//words of a window

    public WindowNode() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<TransitionEdge> getOutEdges() {
        return outEdges;
    }

    public void setOutEdges(Set<TransitionEdge> outEdges) {
        this.outEdges = outEdges;
    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<Widget> widgets) {
        this.widgets = widgets;
    }

    public int getHasOptionsMenu() {
        return hasOptionsMenu;
    }

    public void setHasOptionsMenu(int hasOptionsMenu) {
        this.hasOptionsMenu = hasOptionsMenu;
    }

    public WindowNode getOptionsMenuNode() {
        return optionsMenuNode;
    }

    public void setOptionsMenuNode(WindowNode optionsMenuNode) {
        this.optionsMenuNode = optionsMenuNode;
    }

    public int getHasSysEventCb() {
        return hasSysEventCb;
    }

    public void setHasSysEventCb(int hasSysEventCb) {
        this.hasSysEventCb = hasSysEventCb;
    }

    public List<String> getSysEventCbs() {
        return sysEventCbs;
    }

    public void setSysEventCbs(List<String> sysEventCbs) {
        this.sysEventCbs = sysEventCbs;
    }

    public List<String> getWindowWords() {
        return windowWords;
    }

    public void setWindowWords(List<String> windowWords) {
        this.windowWords = windowWords;
    }

    public void printOptMenuWidgets(){
        if(optionsMenuNode != null){
            System.out.println(optionsMenuNode.getName());
            List<Widget> menuWidgets = optionsMenuNode.getWidgets();
            for(Widget w:menuWidgets){
                System.out.println("id: "+w.getId());
                if(w instanceof MenuItem){
                    MenuItem item = (MenuItem)w;
                    System.out.println("menu_item: "+item.getItemId()+"\t"+item.getText());
                }
                if(w instanceof SubMenu){
                    SubMenu sub = (SubMenu)w;
                    System.out.println("sub_menu: "+sub.getSubMenuId()+"\t"+sub.getText());
                    for(MenuItem subItem:sub.getItems()){
                        System.out.println("sub_item id: "+subItem.getId());
                        System.out.println("sub_menu_item: "+subItem.getItemId()+"\t"+subItem.getText());
                    }
                }
            }
        }
    }
}
