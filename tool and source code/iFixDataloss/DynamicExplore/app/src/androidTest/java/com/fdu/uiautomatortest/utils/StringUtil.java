package com.fdu.uiautomatortest.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public static List<String> segmentation(String identifier){
        List<String> words = new ArrayList<>();
        if(identifier.contains("_")){
            String[] segResults = identifier.split("_");
            for(String seg : segResults){
                if(seg.length() > 1){
                    words.add(removeNum(seg.toLowerCase()));
                }
            }
            return words;
        }
        int len = identifier.length();
        List<Character> temp = new ArrayList<>();
        for(int i = 0;i < len; i++){
            char c = identifier.charAt(i);
            if(isCapital(c) && i != 0 && i != len -1){
                if(isCapital(temp.get(temp.size() - 1).charValue())){
                    temp.add(c);
                }else{
                    String word = new String(toCharArray(temp));
                    if(word.length() > 1){
                        words.add(removeNum(word.toLowerCase()));
                    }
                    temp.clear();
                    temp.add(c);
                }
            }else{
                temp.add(c);
                if(i == len - 1){
                    String lWord = new String(toCharArray(temp));
                    if(lWord.length() > 1){
                        words.add(removeNum(lWord.toLowerCase()));
                    }
                }
            }
        }
        return words;
    }

    public static boolean isSubmitted(String text){
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

    private static char[] toCharArray(List<Character> charList){
        int size = charList.size();
        char[] charArray = new char[size];
        for(int i = 0;i < size;i++){
            charArray[i] = charList.get(i).charValue();
        }
        return charArray;
    }

    private static boolean isCapital(char c){
        return c >= 'A' && c <= 'Z';
    }

    private static boolean isNumber(char c){
        return c >= '0' && c <= '9';
    }

    private static String removeNum(String word){
        char lastChar = word.charAt(word.length() - 1);
        if(isNumber(lastChar)){
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
}
