package com.fdu.se.patchgen.model;

import java.util.*;

public class DynamicGraph {
    private List<DynamicWindow> nodes = new ArrayList<>();
    private List<DynamicEdge> edges = new ArrayList<>();

    public List<DynamicWindow> getNodes() {
        return nodes;
    }

    public void setNodes(List<DynamicWindow> nodes) {
        this.nodes = nodes;
    }

    public List<DynamicEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<DynamicEdge> edges) {
        this.edges = edges;
    }

    public Set<String> getActivityNames(){
        Set<String> names = new HashSet<>();
        for(DynamicWindow node : nodes){
            if(node.getType().equals(WindowType.ACT)){
                names.add(node.getName());
            }
        }
        processActNames(names);
        return names;
    }

    /**
     * get the simeple name of each activity
     * @param names full name activities
     */
    private void processActNames(Collection<String> names){
        Iterator<String> iterator = names.iterator();
        Set<String> templeSet = new HashSet<>();
        while(iterator.hasNext()){
            String name = iterator.next();
            String[] strArray = name.split("\\.");
            String simpleName = strArray[strArray.length - 1];
            iterator.remove();
            templeSet.add(simpleName);
        }
        names.addAll(templeSet);
    }
}
