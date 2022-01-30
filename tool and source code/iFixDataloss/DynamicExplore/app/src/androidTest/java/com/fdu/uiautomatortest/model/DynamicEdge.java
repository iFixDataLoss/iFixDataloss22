package com.fdu.uiautomatortest.model;

public class DynamicEdge {
    private long id;
    private String label;
    private DynamicWidget widget;
    private DynamicWindow source;
    private DynamicWindow target;
    private String type = TransitionType.ACT_TRAN;//the transition from

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DynamicWidget getWidget() {
        return widget;
    }

    public void setWidget(DynamicWidget widget) {
        this.widget = widget;
    }

    public DynamicWindow getSource() {
        return source;
    }

    public void setSource(DynamicWindow source) {
        this.source = source;
    }

    public DynamicWindow getTarget() {
        return target;
    }

    public void setTarget(DynamicWindow target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
