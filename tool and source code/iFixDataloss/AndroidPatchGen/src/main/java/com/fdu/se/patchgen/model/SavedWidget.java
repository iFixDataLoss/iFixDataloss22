package com.fdu.se.patchgen.model;

public class SavedWidget {
    private String ActName;
    private String fieldName;
    private String classType;
    private int idValue = -1;
    private String resourceId;

    public String getActName() {
        return ActName;
    }

    public void setActName(String actName) {
        ActName = actName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public int getIdValue() {
        return idValue;
    }

    public void setIdValue(int idValue) {
        this.idValue = idValue;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String toString() {
        return "SavedWidget{" +
                "ActName='" + ActName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", classType='" + classType + '\'' +
                ", idValue=" + idValue +
                ", resourceId='" + resourceId + '\'' +
                '}';
    }
}
