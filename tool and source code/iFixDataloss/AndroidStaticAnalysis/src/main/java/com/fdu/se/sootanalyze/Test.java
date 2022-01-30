package com.fdu.se.sootanalyze;

import com.fdu.se.sootanalyze.model.*;
import com.fdu.se.sootanalyze.utils.FileUtil;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.util.Iterator;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        SootAnalyze sootAnalyze = new SootAnalyze();
        List<String> apkPaths = FileUtil.getApkPaths("E:\\E_backup\\AndroidTesting\\test app\\accessibility");
        for(String apkPath:apkPaths){
            sootAnalyze.init(apkPath);
            List<WindowNode> nodes = sootAnalyze.analyse();
//            sootAnalyze.analyseDependencies(nodes);
//            for(WindowNode n : nodes){
//                System.out.println(n.getName()+":");
//                System.out.println(n.getWindowWords());
//                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//            }
//            TransitionGraph g = sootAnalyze.generateTransitionGraph(nodes);
//            for(TransitionEdge e : g.getEdges()){
//                System.out.println("type: " + e.getType());
//                System.out.println("source: " + e.getSource().getName());
//                System.out.println("target: " + e.getTarget().getName());
//                if(e.getType().equals(TransitionType.MENU_TRAN)){
//                    System.out.println("menu_item text: " + ((MenuItem)e.getWidget()).getText());
//                    System.out.println("menu_item id: " + ((MenuItem)e.getWidget()).getItemId());
//                }else{
//                    System.out.println("widget type: " + e.getWidget().getWidgetType());
//                    System.out.println("widget id: " + e.getWidget().getWidgetId());
//                }
//                System.out.println("---------------------------------------------------");
//            }
            //String className = "org.openintents.notepad.noteslist.NotesList";
            //String className = "com.commonsware.android.arXiv.arXiv";
            //String eventMethod = "onOptionsItemSelected";
//            CallGraph cg = Scene.v().getCallGraph();
//            Iterator<Edge> edges = cg.iterator();
//            while(edges.hasNext()){
//                Edge e = edges.next();
//                SootMethod src = e.getSrc().method();
//                if(src.getName().equals("createIntent") && src.getDeclaringClass().getName().equals("com.chanapps.four.activity.BoardActivity")){
//                    System.out.println(src.getActiveBody());
//                    break;
//                }
//            }
            //sootAnalyze.getTranFromOptMenu(cg, className, eventMethod);
        }
    }
}
