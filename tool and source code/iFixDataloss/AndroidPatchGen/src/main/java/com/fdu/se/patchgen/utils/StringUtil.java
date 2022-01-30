package com.fdu.se.patchgen.utils;

public class StringUtil {
    /**
     * convert initial target activity, for example class "Lorg/openintents/notepad/PreferenceActivity;"
     * @param classStr
     * @return
     */
    public static String convertToAct(String classStr){
        int len = classStr.length();
        String str = classStr.substring(8, len - 2);
        return str.replace('/', '.');
    }

    public static String convertToLabel(String apkPath){
        String[] nameArray = null;
        if(apkPath.contains("/")){
            nameArray = apkPath.split("/");
        }else{
            nameArray = apkPath.split("\\\\");
        }
        String appFullName = nameArray[nameArray.length - 1];
        int length = appFullName.length();
        return appFullName.substring(0, length - 4);
    }

    public static boolean isEditable(String widgetType){
        if(widgetType.equals("android.widget.EditText") || widgetType.equals("android.widget.CheckBox") ||
        widgetType.equals("android.widget.RadioButton") || widgetType.equals("android.widget.Spinner") ||
        widgetType.equals("android.widget.Switch") || widgetType.equals("android.widget.SeekBar") ||
        widgetType.equals("android.widget.RatingBar") || widgetType.equals("android.widget.CheckedTextView")){
            return true;
        }
        return false;
    }

    public static boolean isButton(String widgetType){
        if(widgetType.equals("android.widget.Button") || widgetType.equals("android.widget.ImageButton"))
            return true;
        return false;
    }

    public static boolean isSubmitted(String text){
        if(text.equals(""))
            return false;

        if(text == null)
            return false;
        else{
            if(text.startsWith("保存") || text.startsWith("提交"))
                return true;
            String lower = text.toLowerCase();
            if(lower.startsWith("ok") || lower.startsWith("submit") || lower.startsWith("save") || lower.startsWith("done") ||
                    lower.startsWith("add") || lower.startsWith("update") || lower.startsWith("create") || lower.startsWith("register") ||
                    lower.startsWith("log in") || lower.startsWith("login"))
                return true;
        }
        return false;
    }
}
