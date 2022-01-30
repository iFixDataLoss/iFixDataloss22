package com.fdu.uiautomatortest.model;

import org.dom4j.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DynamicWindow {
    private long id;
    private String name;//activity name
    private String label;//app name
    private String type;
    private Document winHierarchy;//the hierarchy of window
    private boolean maySaved;//the may saved flag
    private List<Map<String,String>> savedWidgets;
    private int patchType = -1;//-1 no patch, 0 kill, 1 back, 2 rotate
    private Set<DynamicEdge> dmOutEdges = new HashSet<>();
    private List<DynamicWidget> dmWidgets = new ArrayList<>();

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

    public Set<DynamicEdge> getDmOutEdges() {
        return dmOutEdges;
    }

    public void setDmOutEdges(Set<DynamicEdge> dmOutEdges) {
        this.dmOutEdges = dmOutEdges;
    }

    public List<DynamicWidget> getDmWidgets() {
        return dmWidgets;
    }

    public void setDmWidgets(List<DynamicWidget> dmWidgets) {
        this.dmWidgets = dmWidgets;
    }

    public Document getWinHierarchy() {
        return winHierarchy;
    }

    public void setWinHierarchy(Document winHierarchy) {
        this.winHierarchy = winHierarchy;
    }

    public boolean isMaySaved() {
        return maySaved;
    }

    public void setMaySaved(boolean maySaved) {
        this.maySaved = maySaved;
    }

    public List<Map<String, String>> getSavedWidgets() {
        return savedWidgets;
    }

    public void setSavedWidgets(List<Map<String, String>> savedWidgets) {
        this.savedWidgets = savedWidgets;
    }

    public int getPatchType() {
        return patchType;
    }

    public void setPatchType(int patchType) {
        this.patchType = patchType;
    }

    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        DynamicWindow that = (DynamicWindow) o;
//        return Objects.equals(name, that.name) &&
//                Objects.equals(label, that.label) &&
//                Objects.equals(type, that.type);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(name, label, type);
//    }
}
