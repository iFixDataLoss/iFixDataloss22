package com.fdu.se.patchgen.dao;

import com.fdu.se.patchgen.model.DynamicGraph;
import com.fdu.se.patchgen.model.DynamicWindow;
import com.fdu.se.patchgen.utils.StringUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicGraphDao {
    private DynamicEdgeDao edgeDao = new DynamicEdgeDao();
    public static final String submitKey = "submit widget";

    public DynamicGraph getDynamicGraph(String label){
        DynamicGraph graph = new DynamicGraph();
        graph.setEdges(edgeDao.getEdges(label));
        graph.setNodes(edgeDao.getAllWindows());
        return graph;
    }

    public Map<String,List<String>> getSavedWidget(DynamicWindow window){
        Document doc = window.getWinHierarchy();
        Element root = doc.getRootElement();
        Map<String, List<String>> savedElements = new HashMap<>();
        getSavedElements(root,savedElements);
        return savedElements;
    }

    private void getSavedElements(Element root,Map<String, List<String>> savedElements){
        if(root.getName().equals("node")){
            String clazz = root.attributeValue("class");
//            if(clazz.equals("android.widget.EditText")){
//                String res_id = processId(root.attributeValue("resource-id"));
//                if(!savedElements.containsKey("android.widget.EditText")){
//                    List<String> idList = new ArrayList<>();
//                    idList.add(res_id);
//                    savedElements.put("android.widget.EditText",idList);
//                }else{
//                    List<String> idList = savedElements.get("android.widget.EditText");
//                    idList.add(res_id);
//                }
//            }
            if(clazz.equals("android.widget.EditText") || clazz.equals("android.widget.Spinner") || clazz.equals("android.widget.CheckBox") ||
            clazz.equals("android.widget.RadioButton") || clazz.equals("android.widget.CheckedTextView") ||
            clazz.equals("android.widget.Switch") || clazz.equals("android.widget.SeekBar") || clazz.equals("android.widget.RatingBar") ||
            clazz.equals("android.widget.AutoCompleteTextView")){
                String res_id = processId(root.attributeValue("resource-id"));
                if(!savedElements.containsKey(clazz)){
                    List<String> idList = new ArrayList<>();
                    idList.add(res_id);
                    savedElements.put(clazz,idList);
                }else{
                    List<String> idList = savedElements.get(clazz);
                    idList.add(res_id);
                }
            }
//            if(clazz.equals("android.widget.Button")){
//                String res_id = processId(root.attributeValue("resource-id"));
//                if(!savedElements.containsKey("android.widget.Button")){
//                    List<String> idList = new ArrayList<>();
//                    idList.add(res_id);
//                    savedElements.put("android.widget.Button",idList);
//                }else{
//                    List<String> idList = savedElements.get("android.widget.Button");
//                    idList.add(res_id);
//                }
//            }
            if(clazz.equals("android.widget.Button") || clazz.equals("android.widget.ImageButton")){
                String res_id = processId(root.attributeValue("resource-id"));
                String text = root.attributeValue("text");
                String content_desc = root.attributeValue("content-desc");
                if(!"".equals(res_id) && (StringUtil.isSubmitted(text) || StringUtil.isSubmitted(content_desc))){
                    if(!savedElements.containsKey(submitKey)){
                        List<String> idList = new ArrayList<>();
                        idList.add(res_id);
                        savedElements.put(submitKey,idList);
                    }else{
                        List<String> idList = savedElements.get(submitKey);
                        idList.add(res_id);
                    }
                }
            }
            if(clazz.equals("android.widget.TextView")){
                String res_id = processId(root.attributeValue("resource-id"));
                String clickable = root.attributeValue("clickable");
                String text = root.attributeValue("text");
                String content_desc = root.attributeValue("content-desc");
                if(!"".equals(res_id) && clickable.equals("true") && (StringUtil.isSubmitted(text) || StringUtil.isSubmitted(content_desc))){
                    if(!savedElements.containsKey(submitKey)){
                        List<String> idList = new ArrayList<>();
                        idList.add(res_id);
                        savedElements.put(submitKey,idList);
                    }else{
                        List<String> idList = savedElements.get(submitKey);
                        idList.add(res_id);
                    }
                }
            }
        }
        for(Element e : root.elements()){
            getSavedElements(e,savedElements);
        }
    }

    private String processId(String res_id){
        if(!res_id.equals("")){
            String[] arrays = res_id.split("id/");
            return arrays[1];
        }
        return res_id;
    }
}
