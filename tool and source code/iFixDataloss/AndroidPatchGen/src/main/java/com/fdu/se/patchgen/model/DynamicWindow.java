package com.fdu.se.patchgen.model;

import org.dom4j.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DynamicWindow {
    private long id;
    private String name;//activity name
    private String label;//app name
    private String type;
    private Document winHierarchy;//the hierarchy of window
    private boolean maySaved;//the may saved flag
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
