package com.fdu.se.patchgen;

import com.fdu.se.patchgen.dao.DynamicGraphDao;
import com.fdu.se.patchgen.model.DynamicGraph;
import com.fdu.se.patchgen.model.DynamicWindow;
import com.fdu.se.patchgen.model.WindowType;
import com.fdu.se.patchgen.utils.ASTUtil;
import com.fdu.se.patchgen.utils.FileUtil;
import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    private static String graphLabel = "CycleStreets";
    private static String srcPath = "E:\\android\\app\\src";

    public static void main(String[] args) {
        DynamicGraphDao graphDao = new DynamicGraphDao();
        DynamicGraph graph = graphDao.getDynamicGraph(graphLabel);
        Map<String, Map<String, List<String>>> map = new HashMap<>();
        for(DynamicWindow win : graph.getNodes()){
            if(win.isMaySaved() && win.getType().equals(WindowType.ACT) && !map.containsKey(win.getName())){
                Map<String,List<String>> savedMap = graphDao.getSavedWidget(win);
                map.put(win.getName(),savedMap);
            }
        }
        System.out.println(map.size());
        System.out.println(map);
        File file = new File(srcPath);
        List<File> files = FileUtil.getActivityFiles(file,graph.getActivityNames());
        for(File f:files){
            String filename = f.getName();
            String act_short = filename.substring(0,filename.length() - 5);
            Map<String, List<String>> savedIds = getSavedIds(map, act_short);
            if(savedIds != null){
                try{
                    CompilationUnit cu = StaticJavaParser.parse(f);
                    List<ClassOrInterfaceDeclaration> classDeclares = cu.findAll(ClassOrInterfaceDeclaration.class);
                    ClassOrInterfaceDeclaration classDecl = classDeclares.get(0);
                    ASTUtil.addImport(cu);
                    ASTUtil.insertCode(classDecl,savedIds);
                    System.out.println(cu.toString());

                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private static Map<String,List<String>> getSavedIds(Map<String,Map<String,List<String>>> map, String act){
        for(String act_full : map.keySet()){
            if(act_full.endsWith(act)){
                return map.get(act_full);
            }
        }
        return null;
    }

    private static class ClassDeclareVisitor extends VoidVisitorAdapter<Void>{
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            super.visit(n, arg);
            System.out.println(n.getName());
        }
    }
}
