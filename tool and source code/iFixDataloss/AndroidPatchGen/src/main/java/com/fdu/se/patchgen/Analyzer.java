package com.fdu.se.patchgen;

import com.fdu.se.patchgen.model.AppInfo;
import com.fdu.se.patchgen.model.SavedWidget;
import com.fdu.se.patchgen.utils.StringUtil;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.tagkit.Tag;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

import java.util.*;

public class Analyzer {
    private SetupApplication app;
    private String apkPath;
    private String packageName;
    private String sdkPath = "sdk/android.jar";
    private CallGraph cg;

    public void init(String apkPath){
        this.apkPath = apkPath;
        app = new SetupApplication(sdkPath, apkPath);
        System.out.println("begin to analyse app " + StringUtil.convertToLabel(apkPath));
        app.constructCallgraph();
        cg = Scene.v().getCallGraph();
    }

    public AppInfo getAppInfo() {
        AppInfo appInfo = new AppInfo();
        try {
            ProcessManifest processManifest = new ProcessManifest(apkPath);
            AXmlNode manifest = processManifest.getManifest();
            String pack = manifest.getAttribute("package").getValue().toString();
            System.out.println("package name: " + pack);
            packageName = pack;
            List<AXmlNode> activities = processManifest.getActivities();
            List<String> actNames = new ArrayList<>();
            for (AXmlNode act : activities) {
                String actName = act.getAttribute("name").getValue().toString();
                System.out.println(actName);
                actNames.add(actName);
            }
            appInfo.setPackName(pack);
            appInfo.setActivities(actNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appInfo;
    }

    public Map<String,List<SavedWidget>> getAllSavedWidgets(List<String> activities){
        removeDisEdges(cg);
        Map<String, List<SavedWidget>> savedMap = new HashMap<>();
        for(String act : activities){
            SootClass actClass = Scene.v().getSootClassUnsafe(act);
            List<SavedWidget> savedList = new ArrayList<>();
            boolean containsEdit = false;
            boolean containsButton = false;
            Set<Edge> idCalls = findIdCalls(cg,act);
            if(!idCalls.isEmpty()){
                for(SootField f : actClass.getFields()){
                    String type = f.getType().toString();
                    if(StringUtil.isEditable(type)){
                        containsEdit = true;
                        SavedWidget savedEdit = new SavedWidget();
                        savedEdit.setActName(act);
                        savedEdit.setClassType(type);
                        savedEdit.setFieldName(f.getName());
                        int idValue = getIdValue(f, idCalls);
                        savedEdit.setIdValue(idValue);
                        savedEdit.setResourceId(getIdString(Integer.toString(idValue)));
                        savedList.add(savedEdit);
                    }
                    if(StringUtil.isButton(type)){
                        containsButton = true;
                        SavedWidget savedButton = new SavedWidget();
                        savedButton.setActName(act);
                        savedButton.setClassType(type);
                        savedButton.setFieldName(f.getName());
                        int idValue = getIdValue(f,idCalls);
                        savedButton.setIdValue(idValue);
                        savedButton.setResourceId(getIdString(Integer.toString(idValue)));
                        savedList.add(savedButton);
                    }
                }
            }

            if(!/*(*/containsEdit /*&& containsButton)*/){
                savedList.clear();
            }
            savedMap.put(act,savedList);
        }
        return savedMap;
    }

    private void removeDisEdges(CallGraph cg) {
        List<Edge> delEdges = new ArrayList<>();
        Iterator<Edge> edges = cg.iterator();
        while (edges.hasNext()) {
            Edge e = edges.next();
            MethodOrMethodContext src = e.getSrc();
            MethodOrMethodContext tgt = e.getTgt();
            String srcName = src.method().getName();
            if (!src.toString().contains(packageName)) {
                delEdges.add(e);
                continue;
            }
            if (src.equals(tgt)) {
                delEdges.add(e);
                continue;
            }
            if (srcName.equals("<clinit>")) {
                delEdges.add(e);
                continue;
            }
            if (src.toString().contains("jacoco")) {
                delEdges.add(e);
                continue;
            }
            if (src.toString().equals("<java.lang.RuntimeException: void <init>(java.lang.String)>")) {
                delEdges.add(e);
                continue;
            }
            if (src.toString().equals("<java.lang.Exception: void <init>()>")) {
                delEdges.add(e);
                continue;
            }
        }
        for (Edge delEdge : delEdges) {
            cg.removeEdge(delEdge);
        }
    }

    public void printCallGraph(){
        SootClass sc = Scene.v().getSootClassUnsafe("com.fd.se.statelossdemo.MainActivity");
//        SootMethod method = sc.getMethod("void onCreate(android.os.Bundle)");
//        UnitGraph cfg = new BriefUnitGraph(method.getActiveBody());
//        Iterator<Unit> units = cfg.iterator();
//        while(units.hasNext()){
//            Stmt stmt = (Stmt) units.next();
//            if(stmt.containsFieldRef()){
//                System.out.println(stmt);
//                System.out.println(stmt.getFieldRef().getField());
//            }
//        }
      findIdCalls(cg,"com.fd.se.statelossdemo.MainActivity");
//        CallGraph cg = Scene.v().getCallGraph();
//        Iterator<Edge> edges = cg.iterator();
//        while(edges.hasNext()){
//            Edge e = edges.next();
//            if(e.getTgt().method().getName().equals("findViewById")){
//                System.out.println(e.srcStmt());
//            }
//        }
    }

    /**
     * find all calls of findViewById
     * @param cg call graph
     * @param declClass activity name
     * @return all findViewById method calls
     */
    private Set<Edge> findIdCalls(CallGraph cg, String declClass){
        Set<Edge> idCalls = new HashSet<>();
        Iterator<Edge> edges = cg.iterator();
        while(edges.hasNext()){
            Edge edge = edges.next();
            SootMethod tgtMethod = edge.getTgt().method();
            if(tgtMethod.getSubSignature().equals("android.view.View findViewById(int)")){
                // && tgtMethod.getDeclaringClass().getName().equals(declClass)
                if(edge.srcStmt().toString().contains(declClass))
                    idCalls.add(edge);
            }
        }
        return idCalls;
    }

    private int getIdValue(SootField widgetField,Set<Edge> idCalls){
        for(Edge e : idCalls){
            SootMethod src = e.getSrc().method();
            UnitGraph cfg = new BriefUnitGraph(src.getActiveBody());
            Stmt callStmt = e.srcStmt();
            InvokeExpr invokeExpr = callStmt.getInvokeExpr();
            Value idArg = invokeExpr.getArg(0);
            int idValue = Integer.parseInt(idArg.toString());
            Stmt curStmt = callStmt;
            Value curBase = ((AssignStmt)curStmt).getLeftOp();
            while(!cfg.getSuccsOf(curStmt).isEmpty()){
                List<Unit> sucStmts = cfg.getSuccsOf(curStmt);
                curStmt = (Stmt) sucStmts.get(0);
                if(curStmt instanceof AssignStmt){
                    AssignStmt curAssign = (AssignStmt) curStmt;
                    Value assignLeft = curAssign.getLeftOp();
                    Value assignRight = curAssign.getRightOp();
                    if(assignRight instanceof CastExpr){
                        Value castOp = ((CastExpr)assignRight).getOp();
                        if(castOp.equivTo(curBase)){
                            curBase = assignLeft;
                        }
                    }else{
                        if(assignRight.equivTo(curBase) && curAssign.containsFieldRef()){
                            SootField refField = curAssign.getFieldRef().getField();
                            if(refField.equals(widgetField)){
                                return idValue;
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    private String getIdString(String idValue){
        Iterator<SootClass> appClasses = Scene.v().getApplicationClasses().iterator();
        while (appClasses.hasNext()) {
            SootClass appClass = appClasses.next();
            if (appClass.getName().endsWith("R$id")) {
                SootClass idClass = appClass;
                Iterator<SootField> idFields = idClass.getFields().iterator();
                while (idFields.hasNext()) {
                    SootField idField = idFields.next();
                    if (idField.isFinal() && idField.isStatic()) {
                        String fieldName = idField.getName();
                        Tag fieldTag = idField.getTag("IntegerConstantValueTag");
                        if (fieldTag != null) {
                            String tagString = fieldTag.toString();
                            String fieldValue = tagString.split(" ")[1];
                            if (idValue.equals(fieldValue)) {
                                return fieldName;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
