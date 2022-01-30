package com.fdu.uiautomatortest.model;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.Objects;

public class DynamicWidget {
    private long id;
    private String resource_id;
    private String text;
    private String pack;//package name
    private String widget_class;
    private String content_desc;
    private Boolean checkable;
    private Boolean checked;
    private Boolean clickable;
    private Boolean enabled;
    private Boolean focusable;
    private Boolean focused;
    private Boolean scrollable;
    private Boolean long_clickable;
    //private Boolean password;
    private Boolean selected;
    private Rect bounds;
    private Point center;
    private String completeText;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getResource_id() {
        return resource_id;
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getWidget_class() {
        return widget_class;
    }

    public void setWidget_class(String widget_class) {
        this.widget_class = widget_class;
    }

    public String getContent_desc() {
        return content_desc;
    }

    public void setContent_desc(String content_desc) {
        this.content_desc = content_desc;
    }

    public Boolean getCheckable() {
        return checkable;
    }

    public void setCheckable(Boolean checkable) {
        this.checkable = checkable;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getClickable() {
        return clickable;
    }

    public void setClickable(Boolean clickable) {
        this.clickable = clickable;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getFocusable() {
        return focusable;
    }

    public void setFocusable(Boolean focusable) {
        this.focusable = focusable;
    }

    public Boolean getFocused() {
        return focused;
    }

    public void setFocused(Boolean focused) {
        this.focused = focused;
    }

    public Boolean getScrollable() {
        return scrollable;
    }

    public void setScrollable(Boolean scrollable) {
        this.scrollable = scrollable;
    }

    public Boolean getLong_clickable() {
        return long_clickable;
    }

    public void setLong_clickable(Boolean long_clickable) {
        this.long_clickable = long_clickable;
    }

//    public Boolean getPassword() {
//        return password;
//    }
//
//    public void setPassword(Boolean password) {
//        this.password = password;
//    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Rect getBounds() {
        return bounds;
    }

    public void setBounds(Rect bounds) {
        this.bounds = bounds;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public String getCompleteText() {
        return completeText;
    }

    public void setCompleteText(String completeText) {
        this.completeText = completeText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicWidget that = (DynamicWidget) o;
        return Objects.equals(resource_id, that.resource_id) &&
                Objects.equals(text, that.text) &&
                Objects.equals(pack, that.pack) &&
                Objects.equals(widget_class, that.widget_class) &&
                Objects.equals(content_desc, that.content_desc) &&
                Objects.equals(checkable, that.checkable) &&
                Objects.equals(checked, that.checked) &&
                Objects.equals(clickable, that.clickable) &&
                Objects.equals(enabled, that.enabled) &&
                Objects.equals(focusable, that.focusable) &&
                Objects.equals(focused, that.focused) &&
                Objects.equals(scrollable, that.scrollable) &&
                Objects.equals(long_clickable, that.long_clickable) &&
                Objects.equals(selected, that.selected) &&
                Objects.equals(bounds, that.bounds) &&
                Objects.equals(center, that.center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource_id, text, pack, widget_class, content_desc, checkable, checked, clickable, enabled, focusable, focused, scrollable, long_clickable, selected, bounds, center);
    }
}
