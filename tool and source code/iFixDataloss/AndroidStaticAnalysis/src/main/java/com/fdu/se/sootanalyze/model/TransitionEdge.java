package com.fdu.se.sootanalyze.model;

public class TransitionEdge {
    private String label;
    private long id;
    private Widget widget;
    private WindowNode source;
    private WindowNode target;
    private String type = TransitionType.ACT_TRAN;//the transition from

    public TransitionEdge() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    public WindowNode getSource() {
        return source;
    }

    public void setSource(WindowNode source) {
        this.source = source;
    }

    public WindowNode getTarget() {
        return target;
    }

    public void setTarget(WindowNode target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
