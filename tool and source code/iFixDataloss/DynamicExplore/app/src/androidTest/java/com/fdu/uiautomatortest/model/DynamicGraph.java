package com.fdu.uiautomatortest.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DynamicGraph {
    private Set<DynamicWindow> nodes = new HashSet<>();
    private List<DynamicEdge> edges = new ArrayList<>();

    public Set<DynamicWindow> getNodes() {
        return nodes;
    }

    public void setNodes(Set<DynamicWindow> nodes) {
        this.nodes = nodes;
    }

    public List<DynamicEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<DynamicEdge> edges) {
        this.edges = edges;
    }
}
