package com.fdu.uiautomatortest;

public class EdgeTest {
    private String sourceAct;
    private String targetAct;
    private String elementId;
    private String event = "click";

    public EdgeTest(){

    }

    public EdgeTest(String sourceAct, String targetAct, String elementId, String event) {
        this.sourceAct = sourceAct;
        this.targetAct = targetAct;
        this.elementId = elementId;
        this.event = event;
    }

    public EdgeTest(String sourceAct, String targetAct, String elementId) {
        this.sourceAct = sourceAct;
        this.targetAct = targetAct;
        this.elementId = elementId;
    }
}
