package com.fdu.se.sootanalyze;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.fdu.se.sootanalyze.model.WindowNode;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class TopicExtractor {

    private Set<String> dictionary = new HashSet<>();
    private int LSD = 2;//dimension

    public List<String> segmentation(String identifier){
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

    public char[] toCharArray(List<Character> charList){
        int size = charList.size();
        char[] charArray = new char[size];
        for(int i = 0;i < size;i++){
            charArray[i] = charList.get(i).charValue();
        }
        return charArray;
    }

    public static void main(String[] args){
        TopicExtractor topicExtractor = new TopicExtractor();
        List<String> words = topicExtractor.segmentation("vFavorite");
        for(String word:words){
            System.out.println(word);
        }
        System.out.println(topicExtractor.getLemma("cs"));
    }

    public boolean isCapital(char c){
        return c >= 'A' && c <= 'Z';
    }

    public boolean isNumber(char c){
        return c >= '0' && c <= '9';
    }

    public String removeNum(String word){
        char lastChar = word.charAt(word.length() - 1);
        if(isNumber(lastChar)){
            return word.substring(0, word.length() - 1);
        }
        return word;
    }

    public String getLemma(String word){
        String lema = word;
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(word);
        pipeline.annotate(document);
        List<CoreMap> words = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap word_temp: words) {
            for (CoreLabel token: word_temp.get(CoreAnnotations.TokensAnnotation.class)) {
                lema = token.get(CoreAnnotations.LemmaAnnotation.class);
                if(1 == lema.length()){
                    return word;
                }
                //return lema;
            }
        }
        return lema;
    }

    public List<String> readStopwords(){
        List<String> stopwords = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream("stopwords.txt"), "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                stopwords.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopwords;
    }

    public void removeStopwords(List<String> words,List<String> stopwords){
        //words.removeAll(stopwords);
        Iterator<String> wordsItr = words.iterator();
        while(wordsItr.hasNext()){
            String word = wordsItr.next();
            if(stopwords.contains(word)){
                wordsItr.remove();
            }
        }
    }

    public void filterWords(List<String> words){
        Iterator<String> wordsItr = words.iterator();
        while(wordsItr.hasNext()){
            String word = wordsItr.next();
            if(word.length() <= 2){
                wordsItr.remove();
            }
        }
    }

    public double computeTF(String word, WindowNode window){
        int count = 0;
        List<String> winWords = window.getWindowWords();
        for(String winWord : winWords){
            if(winWord.equals(word)){
                count ++;
            }
        }
        return (double) count / winWords.size();
    }

    public double computeIDF(String word, List<WindowNode> windows){
        int count = 0;
        for(WindowNode window : windows){
            List<String> winWords = window.getWindowWords();
            if(winWords.contains(word)){
                count ++;
            }
        }
        //return Math.log10((double) windows.size() / (count + 1));
        return (double) windows.size() / count;
    }

    public double computeTFIDF(String word, WindowNode window, List<WindowNode> windows){
        double tf = computeTF(word, window);
        double idf = computeIDF(word, windows);
        return tf * idf;
    }

    public void initDictionary(List<WindowNode> windows){
        for(WindowNode window : windows){
            List<String> winWords = window.getWindowWords();
            for(String winWord : winWords){
                dictionary.add(winWord);
            }
        }
    }

    public Matrix createMatrix(List<WindowNode> windows){
        double[][] array = new double[dictionary.size()][windows.size()];
        Matrix matrix = new Matrix(array);
        List<String> dicList = new ArrayList<>(dictionary);
        for(int i=0; i< dictionary.size();i++){
            for(int j=0;j<windows.size();j++){
                String word = dicList.get(i);
                double tf_idf = computeTFIDF(word, windows.get(j), windows);
                matrix.set(i,j,tf_idf);
            }
        }
        return matrix;
    }

    public Matrix SVD(Matrix matrix) {
        SingularValueDecomposition svd = matrix.svd();


        Matrix v = svd.getV().transpose();
        for (int i = LSD; i < v.getRowDimension(); i++) {
            for (int j = 0; j < v.getColumnDimension(); j++) {
                v.set(i, j, 0.0);
            }
        }
        return v;
    }

    public void printMatrix(Matrix matrix) {
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                System.out.printf("m(%d,%d) = %g\t", i, j, matrix.get(i, j));
            }
            System.out.printf("\n");
        }
    }

    public List<Double> getVector(Matrix matrix, int row,int dim){
        List<Double> vector = new ArrayList<>();
        for(int j=0;j<dim;j++){
            vector.add(matrix.get(row, j));
        }
        return vector;
    }

    public double cos(List<Double> v1, List<Double> v2, int dim) {
        // Cos(theta) = A(dot)B / |A||B|
        double a_dot_b = 0;
        for (int i = 0; i < dim; i++) {
            a_dot_b += v1.get(i) * v2.get(i);
        }
        double A = 0;
        for (int j = 0; j < dim; j++) {
            A += v1.get(j) * v1.get(j);
        }
        A = Math.sqrt(A);
        double B = 0;
        for (int k = 0; k < dim; k++) {
            B += v2.get(k) * v2.get(k);
        }
        B = Math.sqrt(B);
        return a_dot_b / (A * B);
    }

    /**
     * count the number of the word in a query
     * @param word
     * @param query
     * @return
     */
    public double computeCount(String word,List<String> query){
        int count = 0;
        for(String qw : query){
            if(qw.equals(word)){
                count ++;
            }
        }
        return count;
    }

    /**
     * get the word frequency vector
     * @param query
     * @return
     */
    public double[][] getInitQVector(List<String> query){
        List<Double> countList = new ArrayList<>();
        for(String dicWord : dictionary){
            countList.add(computeCount(dicWord, query));
        }
        double[][] qVec = new double[1][dictionary.size()];
        for(int i=0;i<countList.size();i++){
            qVec[0][i] = countList.get(i);
        }
        return qVec;
    }

    public Matrix getSubMatrix(Matrix matrix, int row, int col){
        double[][] subMatrixArr = new double[row][col];
        for(int i=0;i<row;i++){
            for(int j=0;j<col;j++){
                subMatrixArr[i][j] = matrix.get(i,j);
            }
        }
        return new Matrix(subMatrixArr);
    }
}
