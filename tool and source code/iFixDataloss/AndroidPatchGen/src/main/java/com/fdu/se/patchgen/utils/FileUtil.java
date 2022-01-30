package com.fdu.se.patchgen.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileUtil {
//    public static List<File> getJavaFiles(File file){
//        List<File> javaFiles = new ArrayList<>();
//        if (!file.exists())
//            return new ArrayList<>();
//        if (!file.isDirectory()) {
//            javaFiles.add(file);
//        } else {
//            File[] files = file.listFiles();
//            if (files != null) {
//                for (File f : files) {
//                    if (f.isDirectory()) {
//                        javaFiles.addAll(getJavaFiles(f));
//                    } else if (f.getName().length() > 5 && f.getName().substring(f.getName().length() - 5).equals(".java")
//                            && except(f.getName())) {
//                        javaFiles.add(f);
//                    }
//                }
//            }
//        }
//        return javaFiles;
//    }

    public static List<File> getActivityFiles(File projectFile, Collection<String> actNames){
        List<File> actFiles = new ArrayList<>();
        if(!projectFile.exists())
            return new ArrayList<>();
        if(!projectFile.isDirectory()){
            actFiles.add(projectFile);
        }else{
            File[] files = projectFile.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        actFiles.addAll(getActivityFiles(f,actNames));
                    } else if (f.getName().length() > 5 && f.getName().substring(f.getName().length() - 5).equals(".java")
                            && except(f.getName()) && actNames.contains(f.getName().substring(0,f.getName().length() - 5))) {
                        actFiles.add(f);
                    }
                }
            }
        }
        return actFiles;
    }

    private static boolean except(String fileName) {
        return !fileName.equals("R.java") && !fileName.equals("BuildConfig.java") && !fileName.startsWith(".")
                && !fileName.startsWith("_") && !fileName.startsWith("Test") && !fileName.endsWith("Test.java") && !fileName.startsWith("Enum");
    }
}
