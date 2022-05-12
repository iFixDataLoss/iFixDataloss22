package com.fdu.se.sootanalyze;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.fdu.se.sootanalyze.dao.EdgeDao;
import com.fdu.se.sootanalyze.dao.TransitionGraphDao;
import com.fdu.se.sootanalyze.dao.WidgetDao;
import com.fdu.se.sootanalyze.dao.WindowNodeDao;
import com.fdu.se.sootanalyze.model.*;
import com.fdu.se.sootanalyze.utils.DBUtil;
import com.fdu.se.sootanalyze.utils.FileUtil;
import com.fdu.se.sootanalyze.utils.LoadProperties;
import soot.SootClass;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SootAnalyze sootAnalyze = new SootAnalyze();
        String inputApkPaths = LoadProperties.get("INPUTAPKPATH");
        List<String> apkPaths = FileUtil.getApkPaths(inputApkPaths);
        for(String apkPath:apkPaths){
            long startTime = System.currentTimeMillis();
            sootAnalyze.init(apkPath);
            List<WindowNode> nodes = sootAnalyze.analyse();
            sootAnalyze.analyseDependencies(nodes);
            TransitionGraph g = sootAnalyze.generateTransitionGraph(nodes);
            TransitionGraphDao graphDao = new TransitionGraphDao();
            graphDao.insertGraph(g);
            long endTime = System.currentTimeMillis();
            long executeTime = endTime - startTime;
            FileUtil.filePrintln("time.txt",apkPath+"\t"+executeTime+"");
        }

//        List<String> apkPaths = FileUtil.getApkPaths("E:\\E_backup\\AndroidTesting\\test app\\accessibility");
//        List<WindowNode> nodes = new ArrayList<>();
//        for(String apkPath:apkPaths){
//            sootAnalyze.init(apkPath);
//            List<SootClass> acts = sootAnalyze.getActivities();
//            for(SootClass act:acts){
//                List<String> words = sootAnalyze.getWindowWords(act);
//                WindowNode node = new WindowNode();
//                node.setWindowWords(words);
//                node.setName(act.getShortName());
//                nodes.add(node);
//                System.out.println("act name: " + act.getName());
//                for(int i=0;i<words.size();i++){
//                    System.out.print(words.get(i) + " ");
//                    if(i%6==0){
//                        System.out.println();
//                    }
//                }
//                System.out.println();
//            }
//
//            List<String> query = new ArrayList<>();
//            query.add("search");
//            query.add("title");
//            query.add("article");
//            TopicExtractor extractor = new TopicExtractor();
//            extractor.initDictionary(nodes);
//            Matrix matrix = extractor.createMatrix(nodes);
//            extractor.printMatrix(matrix);
//            System.out.println("*************************");
//            SingularValueDecomposition decomposition = matrix.svd();
//            Matrix u = decomposition.getU();
//            Matrix s = decomposition.getS();
//            Matrix v = decomposition.getV();
//            extractor.printMatrix(v);
//            Matrix queryMatrix = new Matrix(extractor.getInitQVector(query));
//            int dim = 9;
//            Matrix u_k = extractor.getSubMatrix(u,u.getRowDimension(),dim);
//            Matrix s_k = extractor.getSubMatrix(s,dim,dim);
//            Matrix queryVector = queryMatrix.times(u_k).times(s_k.inverse());
//            List<Double> v1 = extractor.getVector(queryVector,0,dim);
//            for(int i=0;i<9;i++){
//                List<Double> v_d = extractor.getVector(v,i,dim);
//                double sim = extractor.cos(v1,v_d,dim);
//                System.out.println(sim);
//            }
//        }
    }
}
