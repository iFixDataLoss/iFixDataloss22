package com.fdu.se.patchgen.utils;

import com.fdu.se.patchgen.dao.DynamicGraphDao;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;

import java.util.List;
import java.util.Map;

public class ASTUtil {
    private static String spField = "sp_gen";
    private static String boolField = "isSubmit_gen";
    private static String spEditorVar = "sp_gen_editor";

    private final static String editFullType = "android.widget.EditText";
    private final static String editShortType = "EditText";
    private final static String spinnerFullType = "android.widget.Spinner";
    private final static String spinnerShortType = "Spinner";
    private final static String seekbarFullType = "android.widget.SeekBar";
    private final static String seekbarShortType = "SeekBar";
    private final static String switchFullType = "android.widget.Switch";
    private final static String switchShortType = "Switch";
    private final static String checkboxFullType = "android.widget.CheckBox";
    private final static String checkboxShortType = "CheckBox";
    private final static String radioFullType = "android.widget.RadioButton";
    private final static String radioShortType = "RadioButton";
    private final static String ratingbarFullType = "android.widget.RatingBar";
    private final static String ratingbarShortType = "RatingBar";
    private final static String checkedtextFullType = "android.widget.CheckedTextView";
    private final static String checkedtextShortType = "CheckedTextView";
    private final static String autotextFullType = "android.widget.AutoCompleteTextView";
    private final static String autotextShortType = "AutoCompleteTextView";
    private final static String buttonFullType = "android.widget.Button";
    private final static String imgbuttonFullType = "android.widget.ImageButton";
    private final static String textviewFullType = "android.widget.TextView";

    public static void addImport(CompilationUnit cu){
        if(!containsImport(cu)){
            cu.addImport("android.content.SharedPreferences");
        }
    }

    public static void insertCode(ClassOrInterfaceDeclaration classDecl,Map<String,List<String>> savedWidgets){
        classDecl.addField("SharedPreferences",spField, Modifier.Keyword.PRIVATE);
        classDecl.addField("boolean",boolField, Modifier.Keyword.PRIVATE);
        insertSubmitChange(classDecl,savedWidgets);
        BlockStmt onPauseBody = hasMethod(classDecl,"onPause");
        if(onPauseBody == null){
            insertOnPause(classDecl, savedWidgets);
        }else{
            modifyOnPause(onPauseBody,savedWidgets);
        }
        BlockStmt onResumeBody = hasMethod(classDecl, "onResume");
        if(onResumeBody == null){
            insertOnResume(classDecl, savedWidgets);
        }else{
            modifyOnResume(classDecl,onResumeBody,savedWidgets);
        }
    }

    private static BlockStmt hasMethod(ClassOrInterfaceDeclaration classDecl,String methodName){
        List<MethodDeclaration> methods = classDecl.getMethodsByName(methodName);
        for(MethodDeclaration method : methods){
            if(method.getType().isVoidType() && method.hasModifier(Modifier.Keyword.PROTECTED)){
                return method.getBody().get();
            }
        }
        return null;
    }

    private static void modifyOnPause(BlockStmt body, Map<String,List<String>> savedWidgets){
        List<String> edit_list = savedWidgets.get(editFullType);
        List<String> spinner_list = savedWidgets.get(spinnerFullType);
        List<String> seekbar_list = savedWidgets.get(seekbarFullType);
        List<String> switch_list = savedWidgets.get(switchFullType);
        List<String> checkbox_list = savedWidgets.get(checkboxFullType);
        List<String> radio_list = savedWidgets.get(radioFullType);
        List<String> ratingbar_list = savedWidgets.get(ratingbarFullType);
        List<String> checkedtext_list = savedWidgets.get(checkedtextFullType);
        List<String> autotext_list = savedWidgets.get(autotextFullType);

        insertOnPauseInit(editShortType,body,edit_list);
        insertOnPauseInit(spinnerShortType,body,spinner_list);
        insertOnPauseInit(seekbarShortType,body,seekbar_list);
        insertOnPauseInit(switchShortType,body,switch_list);
        insertOnPauseInit(checkboxShortType,body,checkbox_list);
        insertOnPauseInit(radioShortType,body,radio_list);
        insertOnPauseInit(ratingbarShortType,body,ratingbar_list);
        insertOnPauseInit(checkedtextShortType,body,checkedtext_list);
        insertOnPauseInit(autotextShortType,body,autotext_list);

        VariableDeclarator spDeclarator = new VariableDeclarator();
        spDeclarator.setType("SharedPreferences.Editor");
        spDeclarator.setName(spEditorVar);
        spDeclarator.setInitializer(spField + ".edit()");
        VariableDeclarationExpr spDeclExpr = new VariableDeclarationExpr();
        spDeclExpr.setVariables(new NodeList<>(spDeclarator));
        body.addStatement(spDeclExpr);

        IfStmt ifStmt = new IfStmt();
        ifStmt.setCondition(new NameExpr(boolField));
        BlockStmt thenBlock = new BlockStmt();
        insertOnPauseSaveThen(editShortType,thenBlock,edit_list);
        insertOnPauseSaveThen(spinnerShortType,thenBlock,spinner_list);
        insertOnPauseSaveThen(seekbarShortType,thenBlock,seekbar_list);
        insertOnPauseSaveThen(switchShortType,thenBlock,switch_list);
        insertOnPauseSaveThen(checkboxShortType,thenBlock,checkbox_list);
        insertOnPauseSaveThen(radioShortType,thenBlock,radio_list);
        insertOnPauseSaveThen(ratingbarShortType,thenBlock,ratingbar_list);
        insertOnPauseSaveThen(checkedtextShortType,thenBlock,checkedtext_list);
        insertOnPauseSaveThen(autotextShortType,thenBlock,autotext_list);

        ifStmt.setThenStmt(thenBlock);
        BlockStmt elseBlock = new BlockStmt();
        insertOnPauseSaveElse(editShortType,elseBlock,edit_list);
        insertOnPauseSaveElse(spinnerShortType,elseBlock,spinner_list);
        insertOnPauseSaveElse(seekbarShortType,elseBlock,seekbar_list);
        insertOnPauseSaveElse(switchShortType,elseBlock,switch_list);
        insertOnPauseSaveElse(checkboxShortType,elseBlock,checkbox_list);
        insertOnPauseSaveElse(radioShortType,elseBlock,radio_list);
        insertOnPauseSaveElse(ratingbarShortType,elseBlock,ratingbar_list);
        insertOnPauseSaveElse(checkedtextShortType,elseBlock,checkedtext_list);
        insertOnPauseSaveElse(autotextShortType,elseBlock,autotext_list);

        ifStmt.setElseStmt(elseBlock);
        body.addStatement(ifStmt);
        MethodCallExpr commitCall = new MethodCallExpr();
        commitCall.setScope(new NameExpr(spEditorVar));
        commitCall.setName("commit");
        body.addStatement(commitCall);
    }

    private static void modifyOnResume(ClassOrInterfaceDeclaration classDecl, BlockStmt body, Map<String,List<String>> savedWidgets){
        AssignExpr spAssign = new AssignExpr();
        spAssign.setTarget(new NameExpr(spField));
        MethodCallExpr right = new MethodCallExpr();
        right.setName("getSharedPreferences");
        right.setArguments(new NodeList<>(new StringLiteralExpr(classDecl.getNameAsString()),new NameExpr("MODE_PRIVATE")));
        spAssign.setValue(right);
        body.addStatement(spAssign);

        List<String> edit_list = savedWidgets.get(editFullType);
        List<String> spinner_list = savedWidgets.get(spinnerFullType);
        List<String> seekbar_list = savedWidgets.get(seekbarFullType);
        List<String> switch_list = savedWidgets.get(switchFullType);
        List<String> checkbox_list = savedWidgets.get(checkboxFullType);
        List<String> radio_list = savedWidgets.get(radioFullType);
        List<String> ratingbar_list = savedWidgets.get(ratingbarFullType);
        List<String> checkedtext_list = savedWidgets.get(checkedtextFullType);
        List<String> autotext_list = savedWidgets.get(autotextFullType);
        insertOnResumeCore(editShortType,body,edit_list);
        insertOnResumeCore(spinnerShortType,body,spinner_list);
        insertOnResumeCore(seekbarShortType,body,seekbar_list);
        insertOnResumeCore(switchShortType,body,switch_list);
        insertOnResumeCore(checkboxShortType,body,checkbox_list);
        insertOnResumeCore(radioShortType,body,radio_list);
        insertOnResumeCore(ratingbarShortType,body,ratingbar_list);
        insertOnResumeCore(checkedtextShortType,body,checkedtext_list);
        insertOnResumeCore(autotextShortType,body,autotext_list);

        AssignExpr assignExpr = new AssignExpr();
        assignExpr.setTarget(new NameExpr(boolField));
        assignExpr.setValue(new NameExpr("false"));
        ExpressionStmt assignStmt = new ExpressionStmt(assignExpr);
        body.addStatement(assignStmt);
    }

    private static void insertSubmitChange(ClassOrInterfaceDeclaration classDecl,Map<String,List<String>> savedWidgets){
        List<String> submitWidgets = savedWidgets.get(DynamicGraphDao.submitKey);
        if(submitWidgets != null && !submitWidgets.isEmpty()){
            String submit_id = "R.id." + getSubmitId(submitWidgets);
            List<AssignExpr> assignExprs = classDecl.findAll(AssignExpr.class);
            Expression submitBtn = null;
            for(AssignExpr assignExpr : assignExprs){
                Expression value = assignExpr.getValue();
                if(value instanceof MethodCallExpr){
                    MethodCallExpr methodCallExpr = (MethodCallExpr) value;
                    String methodName = methodCallExpr.getNameAsString();
                    if(methodName.equals("findViewById")){
                        Expression argExpression = methodCallExpr.getArgument(0);
                        String arg = argExpression.toString();
                        if(arg.equals(submit_id)){
                            submitBtn = assignExpr.getTarget();
                            break;
                        }
                    }
                }
            }
            if(submitBtn != null){
                List<MethodCallExpr> methodCallExprs = classDecl.findAll(MethodCallExpr.class);
                for(MethodCallExpr methodCallExpr : methodCallExprs){
                    String callName = methodCallExpr.getNameAsString();
                    if(callName.equals("setOnClickListener") && methodCallExpr.hasScope()){
                        Expression scopeExpression = methodCallExpr.getScope().get();
                        if(submitBtn.equals(scopeExpression)){
                            if(methodCallExpr.getArgument(0) instanceof ObjectCreationExpr){
                                ObjectCreationExpr objCreExpr = (ObjectCreationExpr) methodCallExpr.getArgument(0);
                                NodeList<BodyDeclaration<?>> bodys = objCreExpr.getAnonymousClassBody().get();
                                MethodDeclaration methodDeclaration = getOnClick(bodys);
                                if(methodDeclaration != null){
                                    BlockStmt bStmt = methodDeclaration.getBody().get();
                                    AssignExpr assignExpr = new AssignExpr();
                                    assignExpr.setTarget(new NameExpr(boolField));
                                    assignExpr.setValue(new NameExpr("true"));
                                    bStmt.addStatement(assignExpr);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private static String getSubmitId(List<String> buttons){
        return buttons.get(0);
    }

    private static MethodDeclaration getOnClick(NodeList<BodyDeclaration<?>> bodys){
        for(BodyDeclaration<?> body : bodys){
            if(body instanceof MethodDeclaration){
                MethodDeclaration methodDeclaration = (MethodDeclaration) body;
                if(methodDeclaration.getNameAsString().equals("onClick") && methodDeclaration.hasParametersOfType("View")){
                    return methodDeclaration;
                }
            }
        }
        return null;
    }

    private static boolean containsImport(CompilationUnit cu){
        for(ImportDeclaration im : cu.getImports()){
            String name = im.getNameAsString();
            if("android.content.SharedPreferences".equals(name) || "android.content.*".equals(name))
                return true;
        }
        return false;
    }

    private static void insertOnPause(ClassOrInterfaceDeclaration classDecl, Map<String, List<String>> savedWidgets){
        MethodDeclaration methodPause = classDecl.addMethod("onPause", Modifier.Keyword.PROTECTED);
        methodPause.setType("void");
        methodPause.addAnnotation(new MarkerAnnotationExpr("Override"));
        BlockStmt blockStmt = new BlockStmt();
        MethodCallExpr methodCallExpr = new MethodCallExpr();
        methodCallExpr.setScope(new SuperExpr());
        methodCallExpr.setName("onPause");
        blockStmt.addStatement(methodCallExpr);
        List<String> edit_list = savedWidgets.get(editFullType);
        List<String> spinner_list = savedWidgets.get(spinnerFullType);
        List<String> seekbar_list = savedWidgets.get(seekbarFullType);
        List<String> switch_list = savedWidgets.get(switchFullType);
        List<String> checkbox_list = savedWidgets.get(checkboxFullType);
        List<String> radio_list = savedWidgets.get(radioFullType);
        List<String> ratingbar_list = savedWidgets.get(ratingbarFullType);
        List<String> checkedtext_list = savedWidgets.get(checkedtextFullType);
        List<String> autotext_list = savedWidgets.get(autotextFullType);

        insertOnPauseInit(editShortType,blockStmt,edit_list);
        insertOnPauseInit(spinnerShortType,blockStmt,spinner_list);
        insertOnPauseInit(seekbarShortType,blockStmt,seekbar_list);
        insertOnPauseInit(switchShortType,blockStmt,switch_list);
        insertOnPauseInit(checkboxShortType,blockStmt,checkbox_list);
        insertOnPauseInit(radioShortType,blockStmt,radio_list);
        insertOnPauseInit(ratingbarShortType,blockStmt,ratingbar_list);
        insertOnPauseInit(checkedtextShortType,blockStmt,checkedtext_list);
        insertOnPauseInit(autotextShortType,blockStmt,autotext_list);
//        for(String edit : edit_list){
//            if(!"".equals(edit)){
//                VariableDeclarator variableDeclarator = new VariableDeclarator();
//                variableDeclarator.setType("EditText");
//                variableDeclarator.setName("edit_" + edit);
//                variableDeclarator.setInitializer("findViewById(R.id." + edit + ")");
//                VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
//                variableDeclarationExpr.setVariables(new NodeList<>(variableDeclarator));
//                blockStmt.addStatement(variableDeclarationExpr);
//            }
//        }
        VariableDeclarator spDeclarator = new VariableDeclarator();
        spDeclarator.setType("SharedPreferences.Editor");
        spDeclarator.setName(spEditorVar);
        spDeclarator.setInitializer(spField + ".edit()");
        VariableDeclarationExpr spDeclExpr = new VariableDeclarationExpr();
        spDeclExpr.setVariables(new NodeList<>(spDeclarator));
        blockStmt.addStatement(spDeclExpr);

        IfStmt ifStmt = new IfStmt();
        ifStmt.setCondition(new NameExpr(boolField));
        BlockStmt thenBlock = new BlockStmt();
        insertOnPauseSaveThen(editShortType,thenBlock,edit_list);
        insertOnPauseSaveThen(spinnerShortType,thenBlock,spinner_list);
        insertOnPauseSaveThen(seekbarShortType,thenBlock,seekbar_list);
        insertOnPauseSaveThen(switchShortType,thenBlock,switch_list);
        insertOnPauseSaveThen(checkboxShortType,thenBlock,checkbox_list);
        insertOnPauseSaveThen(radioShortType,thenBlock,radio_list);
        insertOnPauseSaveThen(ratingbarShortType,thenBlock,ratingbar_list);
        insertOnPauseSaveThen(checkedtextShortType,thenBlock,checkedtext_list);
        insertOnPauseSaveThen(autotextShortType,thenBlock,autotext_list);
//        for(String edit : edit_list){
//            if(!"".equals(edit)){
//                MethodCallExpr putStringCall = new MethodCallExpr();
//                putStringCall.setScope(new NameExpr(spEditorVar));
//                putStringCall.setName("putString");
//                putStringCall.setArguments(new NodeList<>(new StringLiteralExpr("edit_" + edit),new StringLiteralExpr("")));
//                thenBlock.addStatement(putStringCall);
//            }
//        }
        ifStmt.setThenStmt(thenBlock);
        BlockStmt elseBlock = new BlockStmt();
        insertOnPauseSaveElse(editShortType,elseBlock,edit_list);
        insertOnPauseSaveElse(spinnerShortType,elseBlock,spinner_list);
        insertOnPauseSaveElse(seekbarShortType,elseBlock,seekbar_list);
        insertOnPauseSaveElse(switchShortType,elseBlock,switch_list);
        insertOnPauseSaveElse(checkboxShortType,elseBlock,checkbox_list);
        insertOnPauseSaveElse(radioShortType,elseBlock,radio_list);
        insertOnPauseSaveElse(ratingbarShortType,elseBlock,ratingbar_list);
        insertOnPauseSaveElse(checkedtextShortType,elseBlock,checkedtext_list);
        insertOnPauseSaveElse(autotextShortType,elseBlock,autotext_list);
//        for(String edit : edit_list){
//            if(!"".equals(edit)){
//                MethodCallExpr putStringCall = new MethodCallExpr();
//                putStringCall.setScope(new NameExpr(spEditorVar));
//                putStringCall.setName("putString");
//                putStringCall.setArguments(new NodeList<>(new StringLiteralExpr("edit_" + edit),new NameExpr("edit_" + edit + ".getText().toString()")));
//                elseBlock.addStatement(putStringCall);
//            }
//        }
        ifStmt.setElseStmt(elseBlock);
        blockStmt.addStatement(ifStmt);
        MethodCallExpr commitCall = new MethodCallExpr();
        commitCall.setScope(new NameExpr(spEditorVar));
        commitCall.setName("commit");
        blockStmt.addStatement(commitCall);
        methodPause.setBody(blockStmt);
    }

    private static void insertOnResume(ClassOrInterfaceDeclaration classDecl,Map<String,List<String>> savedWidgets){
        MethodDeclaration methodResume = classDecl.addMethod("onResume", Modifier.Keyword.PROTECTED);
        methodResume.setType("void");
        methodResume.addAnnotation(new MarkerAnnotationExpr("Override"));
        BlockStmt blockStmt = new BlockStmt();
        MethodCallExpr methodCallExpr = new MethodCallExpr();
        methodCallExpr.setScope(new SuperExpr());
        methodCallExpr.setName("onResume");
        blockStmt.addStatement(methodCallExpr);
        AssignExpr spAssign = new AssignExpr();
        spAssign.setTarget(new NameExpr(spField));
        MethodCallExpr right = new MethodCallExpr();
        right.setName("getSharedPreferences");
        right.setArguments(new NodeList<>(new StringLiteralExpr(classDecl.getNameAsString()),new NameExpr("MODE_PRIVATE")));
        spAssign.setValue(right);
        blockStmt.addStatement(spAssign);

        List<String> edit_list = savedWidgets.get(editFullType);
        List<String> spinner_list = savedWidgets.get(spinnerFullType);
        List<String> seekbar_list = savedWidgets.get(seekbarFullType);
        List<String> switch_list = savedWidgets.get(switchFullType);
        List<String> checkbox_list = savedWidgets.get(checkboxFullType);
        List<String> radio_list = savedWidgets.get(radioFullType);
        List<String> ratingbar_list = savedWidgets.get(ratingbarFullType);
        List<String> checkedtext_list = savedWidgets.get(checkedtextFullType);
        List<String> autotext_list = savedWidgets.get(autotextFullType);
        insertOnResumeCore(editShortType,blockStmt,edit_list);
        insertOnResumeCore(spinnerShortType,blockStmt,spinner_list);
        insertOnResumeCore(seekbarShortType,blockStmt,seekbar_list);
        insertOnResumeCore(switchShortType,blockStmt,switch_list);
        insertOnResumeCore(checkboxShortType,blockStmt,checkbox_list);
        insertOnResumeCore(radioShortType,blockStmt,radio_list);
        insertOnResumeCore(ratingbarShortType,blockStmt,ratingbar_list);
        insertOnResumeCore(checkedtextShortType,blockStmt,checkedtext_list);
        insertOnResumeCore(autotextShortType,blockStmt,autotext_list);
//        for(String edit : edit_list){
//            if(!"".equals(edit)){
//                VariableDeclarator variableDeclarator = new VariableDeclarator();
//                variableDeclarator.setType("EditText");
//                String editName = "edit_" + edit;
//                variableDeclarator.setName(editName);
//                variableDeclarator.setInitializer("findViewById(R.id." + edit + ")");
//                VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
//                variableDeclarationExpr.setVariables(new NodeList<>(variableDeclarator));
//                blockStmt.addStatement(variableDeclarationExpr);
//                MethodCallExpr setTextCall = new MethodCallExpr();
//                setTextCall.setScope(new NameExpr(editName));
//                setTextCall.setName("setText");
//                MethodCallExpr arg2Call = new MethodCallExpr();
//                arg2Call.setScope(new NameExpr(spField));
//                arg2Call.setName("getString");
//                arg2Call.setArguments(new NodeList<>(new StringLiteralExpr(editName), new StringLiteralExpr("")));
//                setTextCall.setArguments(new NodeList<>(arg2Call));
//                blockStmt.addStatement(setTextCall);
//            }
//        }
        AssignExpr assignExpr = new AssignExpr();
        assignExpr.setTarget(new NameExpr(boolField));
        assignExpr.setValue(new NameExpr("false"));
        ExpressionStmt assignStmt = new ExpressionStmt(assignExpr);
        blockStmt.addStatement(assignStmt);
        methodResume.setBody(blockStmt);
    }

    /**
     * init each widget by findViewById
     * @param type widget short type
     * @param blockStmt onPause method block
     * @param id_list widget id list
     */
    private static void insertOnPauseInit(String type, BlockStmt blockStmt, List<String> id_list){
        if(id_list != null){
            for(String id : id_list){
                if(!"".equals(id)){
                    VariableDeclarator variableDeclarator = new VariableDeclarator();
                    variableDeclarator.setType(type);
                    variableDeclarator.setName(type + "_" + id);
                    variableDeclarator.setInitializer("findViewById(R.id." + id + ")");
                    VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
                    variableDeclarationExpr.setVariables(new NodeList<>(variableDeclarator));
                    blockStmt.addStatement(variableDeclarationExpr);
                }
            }
        }
    }

    private static void insertOnPauseSaveThen(String type, BlockStmt thenBlock, List<String> id_list){
        if(id_list != null){
            for(String id : id_list){
                if(!"".equals(id)){
                    MethodCallExpr putCall = new MethodCallExpr();
                    putCall.setScope(new NameExpr(spEditorVar));
                    putCall.setName(getPutMethod(type));
                    putCall.setArguments(new NodeList<>(new StringLiteralExpr(type + "_" + id),new StringLiteralExpr("")));
                    thenBlock.addStatement(putCall);
                }
            }
        }
    }

    private static void insertOnPauseSaveElse(String type, BlockStmt elseBlock, List<String> id_list){
        if(id_list != null){
            for(String id : id_list){
                if(!"".equals(id)){
                    MethodCallExpr putCall = new MethodCallExpr();
                    putCall.setScope(new NameExpr(spEditorVar));
                    putCall.setName(getPutMethod(type));
                    String firstArg = type + "_" + id;
                    putCall.setArguments(new NodeList<>(new StringLiteralExpr(firstArg),new NameExpr(firstArg + getterMethod(type))));
                    elseBlock.addStatement(putCall);
                }
            }
        }
    }

    private static String getPutMethod(String type){
        String putMethod = "putObject";
        switch(type){
            case editShortType:
            case autotextShortType:
                putMethod = "putString";
                break;
            case spinnerShortType:
            case seekbarShortType:
                putMethod = "putInt";
                break;
            case switchShortType:
            case checkboxShortType:
            case radioShortType:
            case checkedtextShortType:
                putMethod = "putBoolean";
                break;
            case ratingbarShortType:
                putMethod = "putFloat";
                break;
            default:
                putMethod = "putObject";
        }
        return putMethod;
    }

    private static MethodCallExpr getGetMethod(String type){
        MethodCallExpr getMethod = new MethodCallExpr();
        switch(type){
            case editShortType:
            case autotextShortType:
                getMethod.setName("getString");
                getMethod.addArgument(new StringLiteralExpr());
                getMethod.addArgument(new StringLiteralExpr(""));
                break;
            case spinnerShortType:
            case seekbarShortType:
                getMethod.setName("getInt");
                getMethod.addArgument(new StringLiteralExpr());
                getMethod.addArgument(new NameExpr("0"));
                break;
            case switchShortType:
            case radioShortType:
            case checkboxShortType:
            case checkedtextShortType:
                getMethod.setName("getBoolean");
                getMethod.addArgument(new StringLiteralExpr());
                getMethod.addArgument(new NameExpr("false"));
                break;
            case ratingbarShortType:
                getMethod.setName("getFloat");
                getMethod.addArgument(new StringLiteralExpr());
                getMethod.addArgument(new NameExpr("0"));
                break;
            default:
                getMethod.setName("getObject");
        }
        return getMethod;
    }

    private static String getterMethod(String type){
        String getter = ".getObject()";
        switch(type){
            case editShortType:
            case autotextShortType:
                getter = ".getText().toString()";
                break;
            case spinnerShortType:
                getter = ".getSelectedItemPosition()";
                break;
            case seekbarShortType:
                getter = ".getProgress()";
                break;
            case switchShortType:
            case radioShortType:
            case checkboxShortType:
            case checkedtextShortType:
                getter = ".isChecked()";
                break;
            case ratingbarShortType:
                getter = ".getRating()";
                break;
            default:
                getter = ".getObject()";
        }
        return getter;
    }

    private static MethodCallExpr setterMethod(String type){
        MethodCallExpr setter = new MethodCallExpr();
        switch(type){
            case editShortType:
                setter.setName("setText");
                setter.addArgument(new MethodCallExpr());
                break;
            case spinnerShortType:
                setter.setName("setSelection");
                setter.addArgument(new MethodCallExpr());
                break;
            case seekbarShortType:
                setter.setName("setProgress");
                setter.addArgument(new MethodCallExpr());
                break;
            case switchShortType:
            case radioShortType:
            case checkboxShortType:
            case checkedtextShortType:
                setter.setName("setChecked");
                setter.addArgument(new MethodCallExpr());
                break;
            case ratingbarShortType:
                setter.setName("setRating");
                setter.addArgument(new MethodCallExpr());
                break;
            case autotextShortType:
                setter.setName("setText");
                setter.addArgument(new MethodCallExpr());
                setter.addArgument(new NameExpr("true"));
                break;
            default:
                setter.setName("setObject");
        }
        return setter;
    }

    private static void insertOnResumeCore(String type, BlockStmt blockStmt, List<String> id_list){
        if(id_list != null){
            for(String id : id_list){
                if(!"".equals(id)){
                    VariableDeclarator variableDeclarator = new VariableDeclarator();
                    variableDeclarator.setType(type);
                    String widgetName = type + "_" + id;
                    variableDeclarator.setName(widgetName);
                    variableDeclarator.setInitializer("findViewById(R.id." + id + ")");
                    VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
                    variableDeclarationExpr.setVariables(new NodeList<>(variableDeclarator));
                    blockStmt.addStatement(variableDeclarationExpr);
                    MethodCallExpr setCall = setterMethod(type);
                    setCall.setScope(new NameExpr(widgetName));
                    MethodCallExpr arg2Call = getGetMethod(type);
                    arg2Call.setScope(new NameExpr(spField));
                    arg2Call.setArgument(0, new StringLiteralExpr(widgetName));
                    setCall.setArgument(0,arg2Call);
                    blockStmt.addStatement(setCall);
                }
            }
        }
    }
}
