package com.fdu.uiautomatortest;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;


import com.alibaba.fastjson.JSON;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.chaquo.python.android.PyApplication;
import com.fdu.uiautomatortest.dao.DynamicGraphDao;
import com.fdu.uiautomatortest.dao.TransitionGraphDao;
import com.fdu.uiautomatortest.dao.WidgetDao;
import com.fdu.uiautomatortest.model.DynamicEdge;
import com.fdu.uiautomatortest.model.DynamicGraph;
import com.fdu.uiautomatortest.model.DynamicWidget;
import com.fdu.uiautomatortest.model.DynamicWindow;
import com.fdu.uiautomatortest.model.MenuItem;
import com.fdu.uiautomatortest.model.SubMenu;
import com.fdu.uiautomatortest.model.TransitionGraph;
import com.fdu.uiautomatortest.model.TransitionType;
import com.fdu.uiautomatortest.model.Widget;
import com.fdu.uiautomatortest.model.WindowNode;
import com.fdu.uiautomatortest.model.WindowType;
import com.fdu.uiautomatortest.utils.DBUtil;
import com.fdu.uiautomatortest.utils.FileUtil;
import com.fdu.uiautomatortest.utils.StringUtil;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RunWith(AndroidJUnit4.class)
public class DynamicTest {
    private Instrumentation mInstrumentation;
    private UiDevice mUidevice;
    private String mPackage;
    private String mainActivity;
    private String mainActivityRaw;//for restart
    private UiObject2 dialogBtn;
    private Map<String, Set<Rect>> visitedElements = new HashMap<>();//<Activity, UIElementsPosition>
    private List<UiObject2> triggeredEles;//triggered elements in each page
    private Set<String> visitedACT = new HashSet<>();//visited activities
    private String fText = "abc";
    private String fNum = "123";
    private String windowLabel = "TapeMeasure";
    private final long clickDuration = 1500;
    private final long waitTime = 1000;//wait time after execute
    private final long rotateWait = 1000;//wait time after rotate
    private int width;
    private int height;
    private int length = 0;//scenario length
    private Map<UIElement, VisitInfo> visitMap = new HashMap<>();//<uielement,visitinfo>
    private List<Widget> staticWid = new ArrayList<>();//already visit widget from static
    private List<MenuItem> staticMenuItem = new ArrayList<>();//already visit menuitem from static
    private List<SubMenu> staticSubMenu = new ArrayList<>();//already visit submenu from static
    private long scenarioTimeout = 10000;
    private long testTimeout = 1000 * 60 * 4;
    private Integer frequencyCap = 4;
    private String hierarchyPath = "/data/data/com.fdu.uiautomatortest/hierarchy.xml";

    @Before
    public void setUp() throws IOException {
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        mUidevice = UiDevice.getInstance(mInstrumentation);
        mUidevice.setCompressedLayoutHeirarchy(true);
        mPackage = mUidevice.getCurrentPackageName();
        mainActivity = getRunningActivity();
        mainActivityRaw = getRunningActivityRaw();
        width = mUidevice.getDisplayWidth();
        height = mUidevice.getDisplayHeight();
    }

    private void initPython(){
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(InstrumentationRegistry.getTargetContext()));
        }
    }

    @Test
    public void test(){
        dumpWindow();
//        Document doc = getWinHierarchyfromFile(hierarchyPath);
//        Element root = doc.getRootElement();
//        Element first = root.elements().get(0);
//        Log.d("root attribute", first.attributeValue("package"));
        //Log.d("running activity", getRunningActivity());
//        try {
//            mUidevice.executeShellCommand("content insert --uri content://settings/system --bind name:s:user_rotation --bind value:i:1");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

//    @Test
//    public void dfsStatic2() {
//        boolean staticFlag = true;
//        String type = "ACT";
//        long startTime = System.currentTimeMillis();
//        long endTime = startTime;
//        while (endTime - startTime <= testTimeout) {
//            String curACT = getRunningActivity();
//            if (isMenu()) {
//                type = "Menu";
//            } else if (isDialog()) {
//                type = "Dialog";
//            }
//            TransitionEdgeDao edgeDao = new TransitionEdgeDao();
//            List<Map<String, Object>> staticResults = edgeDao.findTransitionResults(windowLabel, curACT, type);
//            if (staticResults.isEmpty() || !staticFlag) {
//                staticFlag = true;//dynamic successfully, set flag
//                List<UiObject2> clickableElements = mUidevice.findObjects(By.clickable(true));
//                if (clickableElements.isEmpty()) {
//                    back();
//                }
//                if (visitMap.isEmpty()) {
//                    UiObject2 firEle = clickableElements.get(0);
//                    String text = firEle.getText();
//                    String resourceId = firEle.getResourceName();
//                    String contentDesc = firEle.getContentDescription();
//                    String classType = firEle.getClassName();
//                    Rect visBounds = firEle.getVisibleBounds();
//                    String hostACT = curACT;
//                    UIElement firUIEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
//                    fill_data(firEle);
//                    executeEvent(firEle, "click");
//                    VisitInfo firVisit = new VisitInfo();
//                    if (isDialog()) {
//                        firVisit.setOpenType(1);
//                        firVisit.setVisit(false);
//                    } else if (isMenu()) {
//                        firVisit.setOpenType(2);
//                        firVisit.setVisit(false);
//                    }
//                    Integer nextFrequency = firVisit.getFrequency() + 1;
//                    firVisit.setFrequency(nextFrequency);
//                    visitMap.put(firUIEle, firVisit);
//                } else {
//                    if (gotoAnotherApp()) {
//                        back();
//                        String curPack = mUidevice.getCurrentPackageName();
//                        if (curPack.equals(mPackage)) {
//                            continue;
//                        } else {
//                            restartApp(mPackage, mainActivity);
//                            continue;
//                        }
//                    }
//                    for (UiObject2 cele : clickableElements) {
//                        String text = cele.getText();
//                        String resourceId = cele.getResourceName();
//                        String contentDesc = cele.getContentDescription();
//                        String classType = cele.getClassName();
//                        Rect visBounds = cele.getVisibleBounds();
//                        String hostACT = curACT;
//                        boolean isEditText = isEditText(cele);
//                        UIElement uiEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
//                        if (!visitMap.containsKey(uiEle)) {
//                            fill_data(cele);
//                            executeEvent(cele, "click");
//                            String dynamicInfo = "dynamic: "+hostACT + " " + visBounds.toString() + " id: " + resourceId;
//                            Log.i("TestInfo",dynamicInfo);
//                            VisitInfo subVisit = new VisitInfo();
//                            if (isDialog() && !isEditText) {
//                                subVisit.setOpenType(1);
//                                subVisit.setVisit(false);
//                            } else if (isMenu() && !isEditText) {
//                                subVisit.setOpenType(2);
//                                subVisit.setVisit(false);
//                            }
//                            Integer nextFrequency = subVisit.getFrequency() + 1;
//                            subVisit.setFrequency(nextFrequency);
//                            visitMap.put(uiEle, subVisit);
//                            break;
//                        } else {
//                            VisitInfo dmVisit = visitMap.get(uiEle);
//                            if (!dmVisit.getVisit()) {//to open menu or dialog
//                                Log.d("open type", dmVisit.getOpenType() + "");
//                                executeEvent(cele, "click");
//                                String dynamicInfo = "dynamic: "+hostACT + " " + visBounds.toString() + " id: " + resourceId;
//                                Log.i("TestInfo",dynamicInfo);
//                                Boolean dmFlag = true;
//                                List<UiObject2> dmElements = mUidevice.findObjects(By.clickable(true));//elements in menu or dialog
//                                for (UiObject2 dmEle : dmElements) {
//                                    String dmText = dmEle.getText();
//                                    String dmResourceId = dmEle.getResourceName();
//                                    String dmContentDesc = dmEle.getContentDescription();
//                                    String dmClassType = dmEle.getClassName();
//                                    Rect dmVisBounds = dmEle.getVisibleBounds();
//                                    String dmHostACT = getRunningActivity();
//                                    UIElement dm = new UIElement(dmText, dmResourceId, dmContentDesc, dmClassType, dmVisBounds, dmHostACT);
//                                    if (!visitMap.containsKey(dm)) {
//                                        dmFlag = false;
//                                        break;
//                                    } else {
//                                        dmFlag = dmFlag && visitMap.get(dm).getVisit();
//                                    }
//                                }
//                                Integer nextFrequency = dmVisit.getFrequency() + 1;
//                                dmVisit.setFrequency(nextFrequency);
//                                dmVisit.setVisit(dmFlag);
//                                if (dmVisit.getFrequency() > frequencyCap) {
//                                    dmVisit.setVisit(true);
//                                }
//                                break;
//                            }
//                        }
//                        if (clickableElements.indexOf(cele) == clickableElements.size() - 1) {
//                            String curAct = getRunningActivity();
//                            if (((mainActivity.equals(curAct))) && !isDialog() && !isMenu()) {
//                                UiObject2 optElement = findOptimalElement(clickableElements);
//                                String opt_text = optElement.getText();
//                                String opt_resourceId = optElement.getResourceName();
//                                String opt_contentDesc = optElement.getContentDescription();
//                                String opt_classType = optElement.getClassName();
//                                Rect opt_visBounds = optElement.getVisibleBounds();
//                                String opt_hostACT = curAct;
//                                UIElement opt_uiElement = new UIElement(opt_text, opt_resourceId, opt_contentDesc, opt_classType, opt_visBounds, opt_hostACT);
//                                VisitInfo opt_info = visitMap.get(opt_uiElement);
//                                executeEvent(optElement, "click");
//                                String dynamicInfo = "dynamic: "+ opt_hostACT + " " + opt_visBounds.toString() + " id: " + opt_resourceId;
//                                Log.i("TestInfo",dynamicInfo);
//                                opt_info.setFrequency(opt_info.getFrequency() + 1);
//                                visitMap.put(opt_uiElement, opt_info);
//                                break;
//                            } else {
//                                back();
//                                break;
//                            }
//                        }
//                    }
//                }
//            } else {
//                String event = "click";
//                String widgetId = "";
//                UiObject2 sWidget = null;
//                for (Map<String, Object> r : staticResults) {
//                    event = (String) r.get("event");
//                    widgetId = (String) r.get("widgetId");
//                    //widgetId = mPackage + ":id/" + widgetId;
//                    widgetId = "android:id/" + widgetId;
//                    if (mUidevice.hasObject(By.res(widgetId))) {
//                        List<UiObject2> sWidgets = mUidevice.findObjects(By.res(widgetId));
//                        sWidget = sWidgets.get(sWidgets.size() / 2);
//                        //sWidget = mUidevice.findObject(By.res(widgetId));
//                        String text = sWidget.getText();
//                        String resourceId = sWidget.getResourceName();
//                        String contentDesc = sWidget.getContentDescription();
//                        String classType = sWidget.getClassName();
//                        Rect visBounds = sWidget.getVisibleBounds();
//                        String hostACT = curACT;
//                        UIElement suie = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
//                        if (!visitMap.containsKey(suie)) {
//                            executeEvent(sWidget, event);
//                            String staticInfo = "static: "+hostACT + " " + visBounds.toString() + " id: " + resourceId;
//                            Log.i("TestInfo",staticInfo);
//                            VisitInfo s_visit = new VisitInfo();
//                            if (isDialog()) {
//                                s_visit.setOpenType(1);
//                                s_visit.setVisit(false);
//                            } else if (isMenu()) {
//                                s_visit.setOpenType(2);
//                                s_visit.setVisit(false);
//                            }
//                            Integer nextFrequency = s_visit.getFrequency() + 1;
//                            s_visit.setFrequency(nextFrequency);
//                            visitMap.put(suie, s_visit);
//                            staticFlag = true;//execute static successfully, set flag
//                            break;
//                        } else {
//                            VisitInfo sInfo = visitMap.get(suie);
//                            if((sInfo.getFrequency()==1 && sInfo.getOpenType()==0 && event.equals("long_click")) ||
//                                    (sInfo.getFrequency()==1 && sInfo.getOpenType()!=0 && event.equals("click"))){
//                                executeEvent(sWidget, event);
//                                String staticInfo = "static: "+hostACT + " " + visBounds.toString() + " id: " + resourceId;
//                                Log.i("TestInfo",staticInfo);
//                                VisitInfo s_visit = new VisitInfo();
//                                if (isDialog()) {
//                                    s_visit.setOpenType(1);
//                                    s_visit.setVisit(false);
//                                } else if (isMenu()) {
//                                    s_visit.setOpenType(2);
//                                    s_visit.setVisit(false);
//                                }
//                                Integer nextFrequency = s_visit.getFrequency() + 1;
//                                s_visit.setFrequency(nextFrequency);
//                                visitMap.put(suie, s_visit);
//                                staticFlag = true;//execute static successfully, set flag
//                                break;
//                            }else{
//                                staticFlag = false;
//                                continue;
//                            }
//                            //staticFlag = false;//does not execute static, set flag
//                            //continue;
//                        }
//                    } else {
//                        staticFlag = false;//does not execute static, set flag
//                        continue;
//                    }
//                }
//            }
//            endTime = System.currentTimeMillis();
//        }
//    }

//    @Test
//    public void dfsStatic1() {
//        boolean staticFlag = true;
//        String type = "ACT";
//        long startTime = System.currentTimeMillis();
//        long endTime = startTime;
//        while (endTime - startTime <= testTimeout) {
//            String curACT = getRunningActivity();
//            if (isMenu()) {
//                type = "MENU";
//            } else if (isDialog()) {
//                type = "DIALOG";
//            }
//            TransitionGraphDao graphDao = new TransitionGraphDao();
//            TransitionGraph g = graphDao.getTransitionGraph(windowLabel);
//            List<Map<String, String>> staticResults = graphDao.findTransitionResults(g, curACT, type, mPackage);
//            if (staticResults.isEmpty() || !staticFlag) {
//                staticFlag = true;//dynamic successfully, set flag
//                List<UiObject2> clickableElements = mUidevice.findObjects(By.clickable(true));
//                if (clickableElements.isEmpty()) {
//                    back();
//                }
//                if (visitMap.isEmpty()) {
//                    UiObject2 firEle = clickableElements.get(0);
//                    String text = firEle.getText();
//                    String resourceId = firEle.getResourceName();
//                    String contentDesc = firEle.getContentDescription();
//                    String classType = firEle.getClassName();
//                    Rect visBounds = firEle.getVisibleBounds();
//                    String hostACT = curACT;
//                    UIElement firUIEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
//                    fill_data(firEle);
//                    executeEvent(firEle, "click");
//                    VisitInfo firVisit = new VisitInfo();
//                    if (isDialog()) {
//                        firVisit.setOpenType(1);
//                        firVisit.setVisit(false);
//                    } else if (isMenu()) {
//                        firVisit.setOpenType(2);
//                        firVisit.setVisit(false);
//                    }
//                    Integer nextFrequency = firVisit.getFrequency() + 1;
//                    firVisit.setFrequency(nextFrequency);
//                    visitMap.put(firUIEle, firVisit);
//                } else {
//                    if (gotoAnotherApp()) {
//                        back();
//                        String curPack = mUidevice.getCurrentPackageName();
//                        if (curPack.equals(mPackage)) {
//                            continue;
//                        } else {
//                            restartApp(mPackage, mainActivity);
//                            continue;
//                        }
//                    }
//                    for (UiObject2 cele : clickableElements) {
//                        String text = cele.getText();
//                        String resourceId = cele.getResourceName();
//                        String contentDesc = cele.getContentDescription();
//                        String classType = cele.getClassName();
//                        Rect visBounds = cele.getVisibleBounds();
//                        String hostACT = curACT;
//                        boolean isEditText = isEditText(cele);
//                        UIElement uiEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
//                        if (!visitMap.containsKey(uiEle)) {
//                            fill_data(cele);
//                            executeEvent(cele, "click");
//                            String dynamicInfo = "dynamic: " + hostACT + " " + visBounds.toString() + " id: " + resourceId;
//                            Log.i("TestInfo", dynamicInfo);
//                            VisitInfo subVisit = new VisitInfo();
//                            if (isDialog() && !isEditText) {
//                                subVisit.setOpenType(1);
//                                subVisit.setVisit(false);
//                            } else if (isMenu() && !isEditText) {
//                                subVisit.setOpenType(2);
//                                subVisit.setVisit(false);
//                            }
//                            Integer nextFrequency = subVisit.getFrequency() + 1;
//                            subVisit.setFrequency(nextFrequency);
//                            visitMap.put(uiEle, subVisit);
//                            break;
//                        } else {
//                            VisitInfo dmVisit = visitMap.get(uiEle);
//                            if (!dmVisit.getVisit()) {//to open menu or dialog
//                                Log.d("open type", dmVisit.getOpenType() + "");
//                                executeEvent(cele, "click");
//                                String dynamicInfo = "dynamic: " + hostACT + " " + visBounds.toString() + " id: " + resourceId;
//                                Log.i("TestInfo", dynamicInfo);
//                                Boolean dmFlag = true;
//                                List<UiObject2> dmElements = mUidevice.findObjects(By.clickable(true));//elements in menu or dialog
//                                for (UiObject2 dmEle : dmElements) {
//                                    String dmText = dmEle.getText();
//                                    String dmResourceId = dmEle.getResourceName();
//                                    String dmContentDesc = dmEle.getContentDescription();
//                                    String dmClassType = dmEle.getClassName();
//                                    Rect dmVisBounds = dmEle.getVisibleBounds();
//                                    String dmHostACT = getRunningActivity();
//                                    UIElement dm = new UIElement(dmText, dmResourceId, dmContentDesc, dmClassType, dmVisBounds, dmHostACT);
//                                    if (!visitMap.containsKey(dm)) {
//                                        dmFlag = false;
//                                        break;
//                                    } else {
//                                        dmFlag = dmFlag && visitMap.get(dm).getVisit();
//                                    }
//                                }
//                                Integer nextFrequency = dmVisit.getFrequency() + 1;
//                                dmVisit.setFrequency(nextFrequency);
//                                dmVisit.setVisit(dmFlag);
//                                if (dmVisit.getFrequency() > frequencyCap) {
//                                    dmVisit.setVisit(true);
//                                }
//                                break;
//                            }
//                        }
//                        if (clickableElements.indexOf(cele) == clickableElements.size() - 1) {
//                            String curAct = getRunningActivity();
//                            if (((mainActivity.equals(curAct))) && !isDialog() && !isMenu()) {
//                                UiObject2 optElement = findOptimalElement(clickableElements);
//                                String opt_text = optElement.getText();
//                                String opt_resourceId = optElement.getResourceName();
//                                String opt_contentDesc = optElement.getContentDescription();
//                                String opt_classType = optElement.getClassName();
//                                Rect opt_visBounds = optElement.getVisibleBounds();
//                                String opt_hostACT = curAct;
//                                UIElement opt_uiElement = new UIElement(opt_text, opt_resourceId, opt_contentDesc, opt_classType, opt_visBounds, opt_hostACT);
//                                VisitInfo opt_info = visitMap.get(opt_uiElement);
//                                executeEvent(optElement, "click");
//                                String dynamicInfo = "dynamic: " + opt_hostACT + " " + opt_visBounds.toString() + " id: " + opt_resourceId;
//                                Log.i("TestInfo", dynamicInfo);
//                                opt_info.setFrequency(opt_info.getFrequency() + 1);
//                                visitMap.put(opt_uiElement, opt_info);
//                                break;
//                            } else {
//                                back();
//                                break;
//                            }
//                        }
//                    }
//                }
//            } else {
//                String event = "click";
//                String widgetId = "";
//                UiObject2 sWidget = null;
//                for (Map<String, String> r : staticResults) {
//                    event = r.get("event");
//                    widgetId = r.get("widgetId");
//                    widgetId = mPackage + ":id/" + widgetId;
//                    if (!mUidevice.hasObject(By.res(widgetId))) {
//                        widgetId = "android:id/" + widgetId;
//                    }
//                    if (mUidevice.hasObject(By.res(widgetId))) {
//                        List<UiObject2> sWidgets = mUidevice.findObjects(By.res(widgetId));
//                        sWidget = sWidgets.get(sWidgets.size() / 2);
//                        //sWidget = mUidevice.findObject(By.res(widgetId));
//                        String text = sWidget.getText();
//                        String resourceId = sWidget.getResourceName();
//                        String contentDesc = sWidget.getContentDescription();
//                        String classType = sWidget.getClassName();
//                        Rect visBounds = sWidget.getVisibleBounds();
//                        String hostACT = curACT;
//                        UIElement suie = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
//                        if (!visitMap.containsKey(suie)) {
//                            executeEvent(sWidget, event);
//                            String staticInfo = "static: " + hostACT + " " + visBounds.toString() + " id: " + resourceId;
//                            Log.i("TestInfo", staticInfo);
//                            VisitInfo s_visit = new VisitInfo();
//                            if (isDialog()) {
//                                s_visit.setOpenType(1);
//                                s_visit.setVisit(false);
//                            } else if (isMenu()) {
//                                s_visit.setOpenType(2);
//                                s_visit.setVisit(false);
//                            }
//                            Integer nextFrequency = s_visit.getFrequency() + 1;
//                            s_visit.setFrequency(nextFrequency);
//                            visitMap.put(suie, s_visit);
//                            staticFlag = true;//execute static successfully, set flag
//                            break;
//                        } else {
//                            VisitInfo sInfo = visitMap.get(suie);
//                            if ((sInfo.getFrequency() == 1 && sInfo.getOpenType() == 0 && event.equals("long_click")) ||
//                                    (sInfo.getFrequency() == 1 && sInfo.getOpenType() != 0 && event.equals("click"))) {
//                                executeEvent(sWidget, event);
//                                String staticInfo = "static: " + hostACT + " " + visBounds.toString() + " id: " + resourceId;
//                                Log.i("TestInfo", staticInfo);
//                                VisitInfo s_visit = new VisitInfo();
//                                if (isDialog()) {
//                                    s_visit.setOpenType(1);
//                                    s_visit.setVisit(false);
//                                } else if (isMenu()) {
//                                    s_visit.setOpenType(2);
//                                    s_visit.setVisit(false);
//                                }
//                                Integer nextFrequency = s_visit.getFrequency() + 1;
//                                s_visit.setFrequency(nextFrequency);
//                                visitMap.put(suie, s_visit);
//                                staticFlag = true;//execute static successfully, set flag
//                                break;
//                            } else {
//                                staticFlag = false;
//                                continue;
//                            }
//                            //staticFlag = false;//does not execute static, set flag
//                            //continue;
//                        }
//                    } else {
//                        staticFlag = false;//does not execute static, set flag
//                        continue;
//                    }
//                }
//            }
//            endTime = System.currentTimeMillis();
//        }
//    }
    

    @Test
    public void dfsStatic() {
        boolean staticFlag = true;
        long startTime = System.currentTimeMillis();
        long endTime = startTime;
        TransitionGraphDao graphDao = new TransitionGraphDao();
        TransitionGraph g = graphDao.getTransitionGraph(windowLabel);
        while (endTime - startTime <= testTimeout) {
            String type = "ACT";
            String curACT = getRunningActivity();
            if (isMenu()) {
                type = "MENU";
            } else if (isDialog()) {
                type = "DIALOG";
            }
            WindowNode node = graphDao.getNodeByCurWindow(g, curACT, type, mPackage);
            if (node == null || !staticFlag) {
                staticFlag = true;//dynamic successfully, set flag
                List<UiObject2> clickableElements = mUidevice.findObjects(By.clickable(true));
                if (clickableElements.isEmpty()) {
                    back();
                }else if (visitMap.isEmpty()) {
                    UiObject2 firEle = clickableElements.get(0);
                    String text = firEle.getText();
                    String resourceId = firEle.getResourceName();
                    String contentDesc = firEle.getContentDescription();
                    String classType = firEle.getClassName();
                    Rect visBounds = firEle.getVisibleBounds();
                    String hostACT = curACT;
                    UIElement firUIEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
                    fill_data(firEle);
                    executeEvent(firEle, "click");
                    VisitInfo firVisit = new VisitInfo();
                    if (isDialog()) {
                        firVisit.setOpenType(1);
                        firVisit.setVisit(false);
                    } else if (isMenu()) {
                        firVisit.setOpenType(2);
                        firVisit.setVisit(false);
                    }
                    Integer nextFrequency = firVisit.getFrequency() + 1;
                    firVisit.setFrequency(nextFrequency);
                    visitMap.put(firUIEle, firVisit);
                } else {
                    if (gotoAnotherApp()) {
                        back();
                        String curPack = mUidevice.getCurrentPackageName();
                        if (curPack.equals(mPackage)) {
                            continue;
                        } else {
                            restartApp(mPackage, mainActivity);
                            continue;
                        }
                    }
                    for (UiObject2 cele : clickableElements) {
                        String text = cele.getText();
                        String resourceId = cele.getResourceName();
                        String contentDesc = cele.getContentDescription();
                        String classType = cele.getClassName();
                        Rect visBounds = cele.getVisibleBounds();
                        String hostACT = curACT;
                        boolean isEditText = isEditText(cele);
                        UIElement uiEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
                        if (!visitMap.containsKey(uiEle)) {
                            fill_data(cele);
                            executeEvent(cele, "click");
                            String dynamicInfo = "dynamic: " + hostACT + " " + visBounds.toString() + " id: " + resourceId;
                            Log.i("TestInfo", dynamicInfo);
                            VisitInfo subVisit = new VisitInfo();
                            if (isDialog() && !isEditText) {
                                subVisit.setOpenType(1);
                                subVisit.setVisit(false);
                            } else if (isMenu() && !isEditText) {
                                subVisit.setOpenType(2);
                                subVisit.setVisit(false);
                            }
                            Integer nextFrequency = subVisit.getFrequency() + 1;
                            subVisit.setFrequency(nextFrequency);
                            visitMap.put(uiEle, subVisit);
                            break;
                        } else {
                            VisitInfo dmVisit = visitMap.get(uiEle);
                            if (!dmVisit.getVisit()) {//to open menu or dialog
                                Log.d("open type", dmVisit.getOpenType() + "");
                                executeEvent(cele, "click");
                                String dynamicInfo = "dynamic: " + hostACT + " " + visBounds.toString() + " id: " + resourceId;
                                Log.i("TestInfo", dynamicInfo);
                                Boolean dmFlag = true;
                                List<UiObject2> dmElements = mUidevice.findObjects(By.clickable(true));//elements in menu or dialog
                                for (UiObject2 dmEle : dmElements) {
                                    String dmText = dmEle.getText();
                                    String dmResourceId = dmEle.getResourceName();
                                    String dmContentDesc = dmEle.getContentDescription();
                                    String dmClassType = dmEle.getClassName();
                                    Rect dmVisBounds = dmEle.getVisibleBounds();
                                    String dmHostACT = getRunningActivity();
                                    UIElement dm = new UIElement(dmText, dmResourceId, dmContentDesc, dmClassType, dmVisBounds, dmHostACT);
                                    if (!visitMap.containsKey(dm)) {
                                        dmFlag = false;
                                        break;
                                    } else {
                                        dmFlag = dmFlag && visitMap.get(dm).getVisit();
                                    }
                                }
                                Integer nextFrequency = dmVisit.getFrequency() + 1;
                                dmVisit.setFrequency(nextFrequency);
                                dmVisit.setVisit(dmFlag);
                                if (dmVisit.getFrequency() > frequencyCap) {
                                    dmVisit.setVisit(true);
                                }
                                break;
                            }
                        }
                        if (clickableElements.indexOf(cele) == clickableElements.size() - 1) {
                            String curAct = getRunningActivity();
                            if (((mainActivity.equals(curAct))) && !isDialog() && !isMenu()) {
                                UiObject2 optElement = findOptimalElement(clickableElements);
                                String opt_text = optElement.getText();
                                String opt_resourceId = optElement.getResourceName();
                                String opt_contentDesc = optElement.getContentDescription();
                                String opt_classType = optElement.getClassName();
                                Rect opt_visBounds = optElement.getVisibleBounds();
                                String opt_hostACT = curAct;
                                UIElement opt_uiElement = new UIElement(opt_text, opt_resourceId, opt_contentDesc, opt_classType, opt_visBounds, opt_hostACT);
                                VisitInfo opt_info = visitMap.get(opt_uiElement);
                                executeEvent(optElement, "click");
                                String dynamicInfo = "dynamic: " + opt_hostACT + " " + opt_visBounds.toString() + " id: " + opt_resourceId;
                                Log.i("TestInfo", dynamicInfo);
                                opt_info.setFrequency(opt_info.getFrequency() + 1);
                                visitMap.put(opt_uiElement, opt_info);
                                break;
                            } else {
                                back();
                                break;
                            }
                        }
                    }
                }
            } else {
                if (1 == node.getHasOptionsMenu()) {
                    WindowNode menuNode = node.getOptionsMenuNode();
                    List<Widget> staticMenuWids = new ArrayList<>();
                    staticMenuWids.addAll(staticMenuItem);
                    staticMenuWids.addAll(staticSubMenu);
                    if (staticMenuWids.containsAll(menuNode.getWidgets())) {//all menu widgets are visited
                        List<Widget> widgets = node.getWidgets();
                        if(widgets.isEmpty()){
                            staticFlag = false;
                        }
                        for (Widget w : widgets) {
                            if (!staticWid.contains(w)) {
                                String res_id = w.getWidgetId();
                                if (res_id != null) {
                                    String res_id_d = mPackage + ":id/" + res_id;
                                    if (!mUidevice.hasObject(By.res(res_id_d))) {
                                        res_id_d = "android:id/" + res_id;
                                    }
                                    if (mUidevice.hasObject(By.res(res_id_d))) {
                                        UiObject2 w_d = mUidevice.findObject(By.res(res_id_d));
                                        String text = w_d.getText();
                                        String resId = w_d.getResourceName();
                                        String contentDesc = w_d.getContentDescription();
                                        String classType = w_d.getClassName();
                                        Rect visBounds = w_d.getVisibleBounds();
                                        String hostACT = getRunningActivity();
                                        executeEvent(w_d, w.getEvent());
                                        staticFlag = true;
                                        staticWid.add(w);
                                        Log.i("TestInfo", "static: widget " + w.getWidgetType() + " " + w.getEvent() + " " + node.getName());
                                        if(!isMenu() && !isDialog()){
                                            UIElement element = new UIElement(text,resId,contentDesc,classType,visBounds,hostACT);
                                            VisitInfo info = new VisitInfo();
                                            Integer frequency = info.getFrequency() + 1;
                                            info.setFrequency(frequency);
                                            visitMap.put(element,info);
                                        }
                                        //saveExecuteWidget(w_d);
                                        break;
                                    } else {
                                        staticWid.add(w);
                                        staticFlag = false;
                                    }
                                } else {
                                    staticWid.add(w);
                                    staticFlag = false;
                                }
                            } else {
                                staticFlag = false;
                            }
                        }
                    } else {
                        openMenu();
                        if(!isMenu()){
                            staticFlag = false;
                            continue;
                        }
                        String staticInfo = "static: menu " + menuNode.getName();
                        Log.i("TestInfo", staticInfo);
                        List<Widget> menuWidgets = menuNode.getWidgets();
                        for (Widget menuWidget : menuWidgets) {
                            if (menuWidget instanceof SubMenu) {
                                SubMenu sub = (SubMenu) menuWidget;
                                if (staticMenuItem.containsAll(sub.getItems())) {
                                    staticSubMenu.add(sub);
                                }
                                if (!staticSubMenu.contains(sub)) {
                                    String text = sub.getText();
                                    if (text != null) {
                                        if (mUidevice.hasObject(By.text(text))) {
                                            UiObject2 sub_d = mUidevice.findObject(By.text(text));
                                            executeEvent(sub_d, sub.getEvent());
                                            Log.i("TestInfo", "static: sub_menu " + text + " " + menuNode.getName());
                                            List<MenuItem> subItems = sub.getItems();
                                            for (MenuItem subItem : subItems) {
                                                if (!staticMenuItem.contains(subItem)) {
                                                    String subText = subItem.getText();
                                                    if (subText != null) {
                                                        if (mUidevice.hasObject(By.text(subText))) {
                                                            UiObject2 subItem_d = mUidevice.findObject(By.text(subText));
                                                            executeEvent(subItem_d, subItem.getEvent());
                                                            staticFlag = true;
                                                            staticMenuItem.add(subItem);
                                                            Log.i("TestInfo", "static: sub_menu_item " + subText + " " + menuNode.getName());
                                                            break;
                                                        } else {
                                                            staticMenuItem.add(subItem);
                                                            staticFlag = false;
                                                        }
                                                    } else {
                                                        staticMenuItem.add(subItem);
                                                        staticFlag = false;
                                                    }
                                                } else {
                                                    staticFlag = false;
                                                }
                                            }
                                        } else {
                                            staticSubMenu.add(sub);
                                            staticFlag = false;
                                        }
                                    } else {
                                        staticSubMenu.add(sub);
                                        staticFlag = false;
                                    }
                                } else {
                                    staticFlag = false;
                                }
                            }
                            if (menuWidget instanceof MenuItem) {
                                MenuItem item = (MenuItem) menuWidget;
                                if (!staticMenuItem.contains(item)) {
                                    String text = item.getText();
                                    if (text != null) {
                                        if (mUidevice.hasObject(By.text(text))) {
                                            UiObject2 item_d = mUidevice.findObject(By.text(text));
                                            executeEvent(item_d, item.getEvent());
                                            staticFlag = true;
                                            staticMenuItem.add(item);
                                            Log.i("TestInfo", "static: menu_item " + text + " " + menuNode.getName());
                                            break;
                                        } else {
                                            staticMenuItem.add(item);
                                            staticFlag = false;
                                        }
                                    } else {
                                        staticMenuItem.add(item);
                                        staticFlag = false;
                                    }
                                } else {
                                    staticFlag = false;
                                }
                            }
                        }
                    }
                } else {
                    List<Widget> widgets = node.getWidgets();
                    if(widgets.isEmpty()){
                        staticFlag = false;
                    }
                    for (Widget w : widgets) {
                        if (!staticWid.contains(w)) {
                            String res_id = w.getWidgetId();
                            if (res_id != null) {
                                String res_id_d = mPackage + ":id/" + res_id;
                                if (!mUidevice.hasObject(By.res(res_id_d))) {
                                    res_id_d = "android:id/" + res_id;
                                }
                                if (mUidevice.hasObject(By.res(res_id_d))) {
                                    UiObject2 w_d = mUidevice.findObject(By.res(res_id_d));
                                    String text = w_d.getText();
                                    String resId = w_d.getResourceName();
                                    String contentDesc = w_d.getContentDescription();
                                    String classType = w_d.getClassName();
                                    Rect visBounds = w_d.getVisibleBounds();
                                    String hostACT = getRunningActivity();
                                    executeEvent(w_d, w.getEvent());
                                    staticFlag = true;
                                    staticWid.add(w);
                                    Log.i("TestInfo", "static: widget " + w.getWidgetType() + " " + w.getEvent() + " " + node.getName());
                                    //saveExecuteWidget(w_d);
                                    if(!isMenu() && !isDialog()){
                                        UIElement element = new UIElement(text,resId,contentDesc,classType,visBounds,hostACT);
                                        VisitInfo info = new VisitInfo();
                                        Integer frequency = info.getFrequency() + 1;
                                        info.setFrequency(frequency);
                                        visitMap.put(element,info);
                                    }
                                    break;
                                } else {
                                    staticWid.add(w);
                                    staticFlag = false;
                                }
                            } else {
                                staticWid.add(w);
                                staticFlag = false;
                            }
                        } else {
                            staticFlag = false;
                        }
                    }
                }
            }
            endTime = System.currentTimeMillis();
        }
    }

    @Test
    public void dfsExplore() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime;
        while (endTime - startTime <= testTimeout) {
            List<UiObject2> clickableElements = mUidevice.findObjects(By.clickable(true));
            if (clickableElements.isEmpty()) {
                back();
            }
            if (visitMap.isEmpty()) {
                UiObject2 firEle = clickableElements.get(0);
                String text = firEle.getText();
                String resourceId = firEle.getResourceName();
                String contentDesc = firEle.getContentDescription();
                String classType = firEle.getClassName();
                Rect visBounds = firEle.getVisibleBounds();
                String hostACT = getRunningActivity();
                UIElement firUIEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
                fill_data(firEle);
                executeEvent(firEle, "click");
                VisitInfo firVisit = new VisitInfo();
                if (isDialog()) {
                    firVisit.setOpenType(1);
                    firVisit.setVisit(false);
                } else if (isMenu()) {
                    firVisit.setOpenType(2);
                    firVisit.setVisit(false);
                }
                Integer nextFrequency = firVisit.getFrequency() + 1;
                firVisit.setFrequency(nextFrequency);
                visitMap.put(firUIEle, firVisit);
            } else {
                if (gotoAnotherApp()) {
                    back();
                    String curPack = mUidevice.getCurrentPackageName();
                    if (curPack.equals(mPackage)) {
                        continue;
                    } else {
                        restartApp(mPackage, mainActivity);
                        continue;
                    }
                }
                for (UiObject2 cele : clickableElements) {
                    String text = cele.getText();
                    String resourceId = cele.getResourceName();
                    String contentDesc = cele.getContentDescription();
                    String classType = cele.getClassName();
                    Rect visBounds = cele.getVisibleBounds();
                    String hostACT = getRunningActivity();
                    boolean isEditText = isEditText(cele);
                    UIElement uiEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
                    if (!visitMap.containsKey(uiEle)) {
                        fill_data(cele);
                        executeEvent(cele, "click");
                        VisitInfo subVisit = new VisitInfo();
                        if (isDialog() && !isEditText) {
                            subVisit.setOpenType(1);
                            subVisit.setVisit(false);
                        } else if (isMenu() && !isEditText) {
                            subVisit.setOpenType(2);
                            subVisit.setVisit(false);
                        }
                        Integer nextFrequency = subVisit.getFrequency() + 1;
                        subVisit.setFrequency(nextFrequency);
                        visitMap.put(uiEle, subVisit);
                        break;
                    } else {
                        VisitInfo dmVisit = visitMap.get(uiEle);
                        if (!dmVisit.getVisit()) {
                            Log.d("open type", dmVisit.getOpenType() + "");
                            executeEvent(cele, "click");
                            Boolean dmFlag = true;//
                            List<UiObject2> dmElements = mUidevice.findObjects(By.clickable(true));
                            for (UiObject2 dmEle : dmElements) {
                                String dmText = dmEle.getText();
                                String dmResourceId = dmEle.getResourceName();
                                String dmContentDesc = dmEle.getContentDescription();
                                String dmClassType = dmEle.getClassName();
                                Rect dmVisBounds = dmEle.getVisibleBounds();
                                String dmHostACT = getRunningActivity();
                                UIElement dm = new UIElement(dmText, dmResourceId, dmContentDesc, dmClassType, dmVisBounds, dmHostACT);
                                if (!visitMap.containsKey(dm)) {
                                    dmFlag = false;
                                    break;
                                } else {
                                    dmFlag = dmFlag && visitMap.get(dm).getVisit();
                                }
                            }
                            Integer nextFrequency = dmVisit.getFrequency() + 1;
                            dmVisit.setFrequency(nextFrequency);
                            dmVisit.setVisit(dmFlag);
                            if (dmVisit.getFrequency() > frequencyCap) {
                                dmVisit.setVisit(true);
                            }
                            break;
                        }
                    }
                    if (clickableElements.indexOf(cele) == clickableElements.size() - 1) {
                        String curAct = getRunningActivity();
                        if (((mainActivity.equals(curAct))) && !isDialog() && !isMenu()) {
                            UiObject2 optElement = findOptimalElement(clickableElements);
                            String opt_text = optElement.getText();
                            String opt_resourceId = optElement.getResourceName();
                            String opt_contentDesc = optElement.getContentDescription();
                            String opt_classType = optElement.getClassName();
                            Rect opt_visBounds = optElement.getVisibleBounds();
                            String opt_hostACT = curAct;
                            UIElement opt_uiElement = new UIElement(opt_text, opt_resourceId, opt_contentDesc, opt_classType, opt_visBounds, opt_hostACT);
                            VisitInfo opt_info = visitMap.get(opt_uiElement);
                            executeEvent(optElement, "click");
                            opt_info.setFrequency(opt_info.getFrequency() + 1);
                            visitMap.put(opt_uiElement, opt_info);
                            break;
                        } else {
                            back();
                            break;
                        }
                    }
                }
            }
            endTime = System.currentTimeMillis();
        }
    }

    @Test
    public void testConn(){
        DBUtil.getConnection();
        //DBUtil.getSqliteConnection();
    }

    @Test
    public void getDynamicGraphWithStatic() {
        boolean staticFlag = true;
        long startTime = System.currentTimeMillis();
        long endTime = startTime;
        TransitionGraphDao graphDao = new TransitionGraphDao();
        TransitionGraph g = graphDao.getTransitionGraph(windowLabel);
        Set<DynamicWindow> dynamicWindows = new HashSet<>();
        List<DynamicEdge> dynamicEdges = new ArrayList<>();
        DynamicGraph dynamicGraph = new DynamicGraph();
        long dmWindowId = 0;
        long dmWidgetId = 0;
        long dmEdgeId = 0;
        while (endTime - startTime <= testTimeout) {
            String curPageName = getRunningActivity();
            dumpWindow();
            Document curDoc = getWinHierarchyfromFile(hierarchyPath);
            curDoc = removeSysUiNode(curDoc);
            DynamicWindow curWindow = containsWindow(dynamicWindows,curDoc);
            if(curWindow == null){
                curWindow = new DynamicWindow();
                curWindow.setId(++dmWindowId);
                curWindow.setLabel(windowLabel);
                curWindow.setName(curPageName);
                curWindow.setType(WindowType.ACT);
                curWindow.setWinHierarchy(curDoc);
                curWindow.setMaySaved(maySaved());
                dynamicWindows.add(curWindow);
            }
            String type = "ACT";
            //String curACT = getRunningActivity();
            if (isMenu()) {
                type = "MENU";
            } else if (isDialog()) {
                type = "DIALOG";
            }
            WindowNode node = graphDao.getNodeByCurWindow(g, curPageName, type, mPackage);

            DynamicWindow nextWindow = new DynamicWindow();
            nextWindow.setLabel(windowLabel);
            nextWindow.setType(WindowType.ACT);
            String nextPageName = curPageName;

            if (node == null || !staticFlag) {
                staticFlag = true;//dynamic successfully, set flag
                List<UiObject2> clickableElements = mUidevice.findObjects(By.clickable(true));
                filterElements(clickableElements);
                if (clickableElements.isEmpty()) {
                    back();
                }else if (visitMap.isEmpty()) {
                    UiObject2 firEle = clickableElements.get(0);
                    String text = firEle.getText();
                    String resourceId = firEle.getResourceName();
                    String contentDesc = firEle.getContentDescription();
                    String classType = firEle.getClassName();
                    Rect visBounds = firEle.getVisibleBounds();
                    String hostACT = curPageName;

                    boolean checkable = firEle.isCheckable();
                    boolean checked = firEle.isChecked();
                    boolean clickable = firEle.isClickable();
                    boolean enabled = firEle.isEnabled();
                    boolean focusable = firEle.isFocusable();
                    boolean focused = firEle.isFocused();
                    boolean scrollable = firEle.isScrollable();
                    boolean long_clickable = firEle.isLongClickable();
                    boolean selected = firEle.isSelected();
                    Point center = firEle.getVisibleCenter();
                    String appPack = firEle.getApplicationPackage();

                    UIElement firUIEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
                    fill_data(firEle);
                    executeEvent(firEle, "click");
                    nextPageName = getRunningActivity();
                    nextWindow.setName(nextPageName);
                    VisitInfo firVisit = new VisitInfo();
                    if (isDialog()) {
                        firVisit.setOpenType(1);
                        firVisit.setVisit(false);
                        nextWindow.setType(WindowType.DIALOG);
                    } else if (isMenu()) {
                        firVisit.setOpenType(2);
                        firVisit.setVisit(false);
                        nextWindow.setType((WindowType.OPTMENU));
                    }
                    Integer nextFrequency = firVisit.getFrequency() + 1;
                    firVisit.setFrequency(nextFrequency);
                    visitMap.put(firUIEle, firVisit);
                    dumpWindow();
                    Document nextDoc = getWinHierarchyfromFile(hierarchyPath);
                    nextDoc = removeSysUiNode(nextDoc);
                    nextWindow.setWinHierarchy(nextDoc);
                    DynamicWidget dmWidget = new DynamicWidget();
                    dmWidget.setResource_id(resourceId);
                    dmWidget.setText(text);
                    if(text != null){
                        dmWidget.setCompleteText(text);
                    }
                    dmWidget.setPack(appPack);
                    dmWidget.setWidget_class(classType);
                    dmWidget.setContent_desc(contentDesc);
                    dmWidget.setCheckable(checkable);
                    dmWidget.setChecked(checked);
                    dmWidget.setClickable(clickable);
                    dmWidget.setEnabled(enabled);
                    dmWidget.setFocusable(focusable);
                    dmWidget.setFocused(focused);
                    dmWidget.setScrollable(scrollable);
                    dmWidget.setLong_clickable(long_clickable);
                    dmWidget.setSelected(selected);
                    dmWidget.setBounds(visBounds);
                    dmWidget.setCenter(center);
                    if(!containsEdge(dynamicEdges,curWindow,nextWindow,dmWidget)){
                        DynamicWindow next_w = containsWindow(dynamicWindows,nextDoc);
                        if(next_w == null){
                            nextWindow.setId(++dmWindowId);
                            nextWindow.setMaySaved(maySaved());
                            dynamicWindows.add(nextWindow);
                        }else{
                            nextWindow = next_w;
                        }
                        DynamicEdge newEdge = new DynamicEdge();
                        newEdge.setId(++dmEdgeId);
                        newEdge.setLabel(windowLabel);
                        newEdge.setSource(curWindow);
                        newEdge.setTarget(nextWindow);
                        newEdge.setType(getTranType(curWindow));

                        dmWidget.setId(++dmWidgetId);

                        newEdge.setWidget(dmWidget);
                        dynamicEdges.add(newEdge);
                        printDynamicEdge(newEdge);
                    }
                } else {
                    if (gotoAnotherApp()) {
                        back();
                        String curPack = mUidevice.getCurrentPackageName();
                        if (curPack.equals(mPackage)) {
                            continue;
                        } else {
                            restartApp(mPackage, mainActivity);
                            continue;
                        }
                    }
                    for (UiObject2 cele : clickableElements) {
                        String text = cele.getText();
                        String resourceId = cele.getResourceName();
                        String contentDesc = cele.getContentDescription();
                        String classType = cele.getClassName();
                        Rect visBounds = cele.getVisibleBounds();
                        String hostACT = curPageName;

                        boolean checkable = cele.isCheckable();
                        boolean checked = cele.isChecked();
                        boolean clickable = cele.isClickable();
                        boolean enabled = cele.isEnabled();
                        boolean focusable = cele.isFocusable();
                        boolean focused = cele.isFocused();
                        boolean scrollable = cele.isScrollable();
                        boolean long_clickable = cele.isLongClickable();
                        boolean selected = cele.isSelected();
                        Point center = cele.getVisibleCenter();
                        String appPack = cele.getApplicationPackage();

                        boolean isEditText = isEditText(cele);
                        UIElement uiEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
                        if (!visitMap.containsKey(uiEle)) {
                            fill_data(cele);
                            executeEvent(cele, "click");
                            nextPageName = getRunningActivity();
                            nextWindow.setName(nextPageName);
                            String dynamicInfo = "dynamic: " + hostACT + " " + visBounds.toString() + " id: " + resourceId;
                            Log.i("TestInfo", dynamicInfo);
                            VisitInfo subVisit = new VisitInfo();
                            if (isDialog() && !isEditText) {
                                subVisit.setOpenType(1);
                                subVisit.setVisit(false);
                                nextWindow.setType(WindowType.DIALOG);
                            } else if (isMenu() && !isEditText) {
                                subVisit.setOpenType(2);
                                subVisit.setVisit(false);
                                nextWindow.setType(WindowType.OPTMENU);
                            }
                            Integer nextFrequency = subVisit.getFrequency() + 1;
                            subVisit.setFrequency(nextFrequency);
                            visitMap.put(uiEle, subVisit);
                            dumpWindow();
                            Document nextDoc = getWinHierarchyfromFile(hierarchyPath);
                            nextDoc = removeSysUiNode(nextDoc);
                            nextWindow.setWinHierarchy(nextDoc);
                            DynamicWidget dmWidget = new DynamicWidget();
                            dmWidget.setResource_id(resourceId);
                            dmWidget.setText(text);
                            if(text != null){
                                dmWidget.setCompleteText(text);
                            }
                            dmWidget.setPack(appPack);
                            dmWidget.setWidget_class(classType);
                            dmWidget.setContent_desc(contentDesc);
                            dmWidget.setCheckable(checkable);
                            dmWidget.setChecked(checked);
                            dmWidget.setClickable(clickable);
                            dmWidget.setEnabled(enabled);
                            dmWidget.setFocusable(focusable);
                            dmWidget.setFocused(focused);
                            dmWidget.setScrollable(scrollable);
                            dmWidget.setLong_clickable(long_clickable);
                            dmWidget.setSelected(selected);
                            dmWidget.setBounds(visBounds);
                            dmWidget.setCenter(center);
                            if(!containsEdge(dynamicEdges,curWindow,nextWindow,dmWidget)){
                                DynamicWindow next_w = containsWindow(dynamicWindows,nextDoc);
                                if(next_w == null){
                                    nextWindow.setId(++dmWindowId);
                                    nextWindow.setMaySaved(maySaved());
                                    dynamicWindows.add(nextWindow);
                                }else {
                                    nextWindow = next_w;
                                }
                                DynamicEdge newEdge = new DynamicEdge();
                                newEdge.setId(++dmEdgeId);
                                newEdge.setLabel(windowLabel);
                                newEdge.setSource(curWindow);
                                newEdge.setTarget(nextWindow);
                                newEdge.setType(getTranType(curWindow));

                                dmWidget.setId(++dmWidgetId);

                                newEdge.setWidget(dmWidget);
                                dynamicEdges.add(newEdge);
                                printDynamicEdge(newEdge);
                            }
                            break;
                        } else {
                            VisitInfo dmVisit = visitMap.get(uiEle);
                            if (!dmVisit.getVisit()) {//to open menu or dialog
                                Log.d("open type", dmVisit.getOpenType() + "");
                                executeEvent(cele, "click");
                                String dynamicInfo = "dynamic: " + hostACT + " " + visBounds.toString() + " id: " + resourceId;
                                Log.i("TestInfo", dynamicInfo);
                                Boolean dmFlag = true;
                                List<UiObject2> dmElements = mUidevice.findObjects(By.clickable(true));//elements in menu or dialog
                                for (UiObject2 dmEle : dmElements) {
                                    String dmText = dmEle.getText();
                                    String dmResourceId = dmEle.getResourceName();
                                    String dmContentDesc = dmEle.getContentDescription();
                                    String dmClassType = dmEle.getClassName();
                                    Rect dmVisBounds = dmEle.getVisibleBounds();
                                    String dmHostACT = getRunningActivity();
                                    UIElement dm = new UIElement(dmText, dmResourceId, dmContentDesc, dmClassType, dmVisBounds, dmHostACT);
                                    if (!visitMap.containsKey(dm)) {
                                        dmFlag = false;
                                        break;
                                    } else {
                                        dmFlag = dmFlag && visitMap.get(dm).getVisit();
                                    }
                                }
                                Integer nextFrequency = dmVisit.getFrequency() + 1;
                                dmVisit.setFrequency(nextFrequency);
                                dmVisit.setVisit(dmFlag);
                                if (dmVisit.getFrequency() > frequencyCap) {
                                    dmVisit.setVisit(true);
                                }
                                break;
                            }
                        }
                        if (clickableElements.indexOf(cele) == clickableElements.size() - 1) {
                            String curAct = getRunningActivity();
                            if (((mainActivity.equals(curAct))) && !isDialog() && !isMenu()) {
                                UiObject2 optElement = findOptimalElement(clickableElements);
                                String opt_text = optElement.getText();
                                String opt_resourceId = optElement.getResourceName();
                                String opt_contentDesc = optElement.getContentDescription();
                                String opt_classType = optElement.getClassName();
                                Rect opt_visBounds = optElement.getVisibleBounds();
                                String opt_hostACT = curAct;
                                UIElement opt_uiElement = new UIElement(opt_text, opt_resourceId, opt_contentDesc, opt_classType, opt_visBounds, opt_hostACT);
                                VisitInfo opt_info = visitMap.get(opt_uiElement);
                                executeEvent(optElement, "click");
                                String dynamicInfo = "dynamic: " + opt_hostACT + " " + opt_visBounds.toString() + " id: " + opt_resourceId;
                                Log.i("TestInfo", dynamicInfo);
                                opt_info.setFrequency(opt_info.getFrequency() + 1);
                                visitMap.put(opt_uiElement, opt_info);
                                break;
                            } else {
                                back();
                                break;
                            }
                        }
                    }
                }
            } else {
                if (1 == node.getHasOptionsMenu()) {
                    WindowNode menuNode = node.getOptionsMenuNode();
                    List<Widget> staticMenuWids = new ArrayList<>();
                    staticMenuWids.addAll(staticMenuItem);
                    staticMenuWids.addAll(staticSubMenu);
                    if (staticMenuWids.containsAll(menuNode.getWidgets())) {//all menu widgets are visited
                        List<Widget> widgets = node.getWidgets();
                        boolean executeFlag = false;
                        for (Widget w : widgets) {
                            if (!staticWid.contains(w)) {
                                String res_id = w.getWidgetId();
                                if (res_id != null) {
                                    String res_id_d = mPackage + ":id/" + res_id;
                                    if (!mUidevice.hasObject(By.res(res_id_d))) {
                                        res_id_d = "android:id/" + res_id;
                                    }
                                    if (mUidevice.hasObject(By.res(res_id_d))) {
                                        UiObject2 w_d = mUidevice.findObject(By.res(res_id_d));
                                        String text = w_d.getText();
                                        String resId = w_d.getResourceName();
                                        String contentDesc = w_d.getContentDescription();
                                        String classType = w_d.getClassName();
                                        Rect visBounds = w_d.getVisibleBounds();
                                        String hostACT = getRunningActivity();

                                        boolean checkable = w_d.isCheckable();
                                        boolean checked = w_d.isChecked();
                                        boolean clickable = w_d.isClickable();
                                        boolean enabled = w_d.isEnabled();
                                        boolean focusable = w_d.isFocusable();
                                        boolean focused = w_d.isFocused();
                                        boolean scrollable = w_d.isScrollable();
                                        boolean long_clickable = w_d.isLongClickable();
                                        boolean selected = w_d.isSelected();
                                        Point center = w_d.getVisibleCenter();
                                        String appPack = w_d.getApplicationPackage();

                                        executeEvent(w_d, w.getEvent());
                                        nextPageName = getRunningActivity();
                                        nextWindow.setName(nextPageName);
                                        executeFlag = true;
                                        staticWid.add(w);
                                        Log.i("TestInfo", "static: widget " + w.getWidgetType() + " " + w.getEvent() + " " + node.getName());

                                        UIElement element = new UIElement(text,resId,contentDesc,classType,visBounds,hostACT);
                                        VisitInfo info = new VisitInfo();
                                        if (isDialog()) {
                                            info.setOpenType(1);
                                            info.setVisit(false);
                                            nextWindow.setType(WindowType.DIALOG);
                                        } else if (isMenu()) {
                                            info.setOpenType(2);
                                            info.setVisit(false);
                                            nextWindow.setType((WindowType.OPTMENU));
                                        }
                                        Integer frequency = info.getFrequency() + 1;
                                        info.setFrequency(frequency);
                                        visitMap.put(element,info);
                                        //saveExecuteWidget(w_d);
                                        dumpWindow();
                                        Document nextDoc = getWinHierarchyfromFile(hierarchyPath);
                                        nextDoc = removeSysUiNode(nextDoc);
                                        nextWindow.setWinHierarchy(nextDoc);
                                        DynamicWidget dmWidget = new DynamicWidget();
                                        dmWidget.setResource_id(resId);
                                        dmWidget.setText(text);
                                        if(text != null){
                                            dmWidget.setCompleteText(text);
                                        }
                                        dmWidget.setPack(appPack);
                                        dmWidget.setWidget_class(classType);
                                        dmWidget.setContent_desc(contentDesc);
                                        dmWidget.setCheckable(checkable);
                                        dmWidget.setChecked(checked);
                                        dmWidget.setClickable(clickable);
                                        dmWidget.setEnabled(enabled);
                                        dmWidget.setFocusable(focusable);
                                        dmWidget.setFocused(focused);
                                        dmWidget.setScrollable(scrollable);
                                        dmWidget.setLong_clickable(long_clickable);
                                        dmWidget.setSelected(selected);
                                        dmWidget.setBounds(visBounds);
                                        dmWidget.setCenter(center);
                                        if(!containsEdge(dynamicEdges,curWindow,nextWindow,dmWidget)){
                                            DynamicWindow next_w = containsWindow(dynamicWindows,nextDoc);
                                            if(next_w == null){
                                                nextWindow.setId(++dmWindowId);
                                                nextWindow.setMaySaved(maySaved());
                                                dynamicWindows.add(nextWindow);
                                            }else{
                                                nextWindow = next_w;
                                            }
                                            DynamicEdge newEdge = new DynamicEdge();
                                            newEdge.setId(++dmEdgeId);
                                            newEdge.setLabel(windowLabel);
                                            newEdge.setSource(curWindow);
                                            newEdge.setTarget(nextWindow);
                                            newEdge.setType(getTranType(curWindow));

                                            dmWidget.setId(++dmWidgetId);

                                            newEdge.setWidget(dmWidget);
                                            dynamicEdges.add(newEdge);
                                            printDynamicEdge(newEdge);
                                        }
                                        break;
                                    } else {
                                        staticWid.add(w);
                                        executeFlag = false;
                                    }
                                } else {
                                    staticWid.add(w);
                                    executeFlag = false;
                                }
                            } else {
                                executeFlag = false;
                            }
                        }
                        staticFlag = executeFlag;
                    } else {
                        openMenu();
                        if(!isMenu()){
                            staticFlag = false;
                            continue;
                        }
                        nextPageName = getRunningActivity();
                        nextWindow.setName(nextPageName);
                        nextWindow.setType(WindowType.OPTMENU);
                        dumpWindow();
                        Document nextDoc = getWinHierarchyfromFile(hierarchyPath);
                        nextDoc = removeSysUiNode(nextDoc);
                        nextWindow.setWinHierarchy(nextDoc);
                        if(!containsEdge(dynamicEdges,curWindow,nextWindow,null)){
                            DynamicWindow next_w = containsWindow(dynamicWindows,nextDoc);
                            if(next_w == null){
                                nextWindow.setId(++dmWindowId);
                                nextWindow.setMaySaved(maySaved());
                                dynamicWindows.add(nextWindow);
                            }else{
                                nextWindow = next_w;
                            }
                            DynamicEdge newEdge = new DynamicEdge();
                            newEdge.setId(++dmEdgeId);
                            newEdge.setLabel(windowLabel);
                            newEdge.setSource(curWindow);
                            newEdge.setTarget(nextWindow);
                            newEdge.setType(getTranType(curWindow));
                            newEdge.setWidget(null);//no widget, press menu key
                            dynamicEdges.add(newEdge);
                            printDynamicEdge(newEdge);
                        }
                        //updateWindow(dynamicWindows,curWindow,nextWindow);//update curWindow
                        curWindow = containsWindow(dynamicWindows,nextDoc);
                        DynamicWindow nextWindow_menu = new DynamicWindow();
                        nextWindow_menu.setLabel(windowLabel);
                        nextWindow_menu.setType(WindowType.ACT);
                        String staticInfo = "static: menu " + menuNode.getName();
                        Log.i("TestInfo", staticInfo);
                        List<Widget> menuWidgets = menuNode.getWidgets();
                        boolean executeMenuFlag = false;
                        for (Widget menuWidget : menuWidgets) {
                            if (menuWidget instanceof SubMenu) {
                                SubMenu sub = (SubMenu) menuWidget;
                                if (staticMenuItem.containsAll(sub.getItems())) {
                                    staticSubMenu.add(sub);
                                }
                                if (!staticSubMenu.contains(sub)) {
                                    String text = sub.getText();
                                    if (text != null) {
                                        if (mUidevice.hasObject(By.text(text))) {
                                            UiObject2 sub_d = mUidevice.findObject(By.text(text));
                                            String resId = sub_d.getResourceName();
                                            String contentDesc = sub_d.getContentDescription();
                                            String classType = sub_d.getClassName();
                                            Rect visBounds = sub_d.getVisibleBounds();
                                            String hostACT = getRunningActivity();

                                            boolean checkable = sub_d.isCheckable();
                                            boolean checked = sub_d.isChecked();
                                            boolean clickable = sub_d.isClickable();
                                            boolean enabled = sub_d.isEnabled();
                                            boolean focusable = sub_d.isFocusable();
                                            boolean focused = sub_d.isFocused();
                                            boolean scrollable = sub_d.isScrollable();
                                            boolean long_clickable = sub_d.isLongClickable();
                                            boolean selected = sub_d.isSelected();
                                            Point center = sub_d.getVisibleCenter();
                                            String appPack = sub_d.getApplicationPackage();

                                            executeEvent(sub_d, sub.getEvent());
                                            Log.i("TestInfo", "static: sub_menu " + text + " " + menuNode.getName());
                                            String nextPageSub = getRunningActivity();
                                            nextWindow_menu.setName(nextPageSub);
                                            nextWindow_menu.setType(WindowType.OPTMENU);

                                            UIElement element = new UIElement(text,resId,contentDesc,classType,visBounds,hostACT);
                                            VisitInfo info = new VisitInfo();
//                                            if (isDialog()) {
//                                                info.setOpenType(1);
//                                                info.setVisit(false);
//                                                nextWindow.setType(WindowType.DIALOG);
//                                            } else if (isMenu()) {
//                                                info.setOpenType(2);
//                                                info.setVisit(false);
//                                                nextWindow.setType((WindowType.OPTMENU));
//                                            }
                                            Integer frequency = info.getFrequency() + 1;
                                            info.setFrequency(frequency);
                                            visitMap.put(element,info);
                                            dumpWindow();
                                            Document nextDoc_sub = getWinHierarchyfromFile(hierarchyPath);
                                            nextDoc_sub = removeSysUiNode(nextDoc_sub);
                                            nextWindow_menu.setWinHierarchy(nextDoc_sub);
                                            DynamicWidget dmWidget = new DynamicWidget();
                                            dmWidget.setResource_id(resId);
                                            dmWidget.setText(text);
                                            dmWidget.setPack(appPack);
                                            dmWidget.setWidget_class(classType);
                                            dmWidget.setContent_desc(contentDesc);
                                            dmWidget.setCheckable(checkable);
                                            dmWidget.setChecked(checked);
                                            dmWidget.setClickable(clickable);
                                            dmWidget.setEnabled(enabled);
                                            dmWidget.setFocusable(focusable);
                                            dmWidget.setFocused(focused);
                                            dmWidget.setScrollable(scrollable);
                                            dmWidget.setLong_clickable(long_clickable);
                                            dmWidget.setSelected(selected);
                                            dmWidget.setBounds(visBounds);
                                            dmWidget.setCenter(center);
                                            if(!containsEdge(dynamicEdges,curWindow,nextWindow_menu,dmWidget)){
                                                DynamicWindow next_w = containsWindow(dynamicWindows,nextDoc_sub);
                                                if(next_w == null){
                                                    nextWindow_menu.setId(++dmWindowId);
                                                    nextWindow_menu.setMaySaved(maySaved());
                                                    dynamicWindows.add(nextWindow_menu);
                                                }else{
                                                    nextWindow_menu = next_w;
                                                }
                                                DynamicEdge newEdge = new DynamicEdge();
                                                newEdge.setId(++dmEdgeId);
                                                newEdge.setLabel(windowLabel);
                                                newEdge.setSource(curWindow);
                                                newEdge.setTarget(nextWindow_menu);
                                                newEdge.setType(getTranType(curWindow));

                                                dmWidget.setId(++dmWidgetId);

                                                newEdge.setWidget(dmWidget);
                                                dynamicEdges.add(newEdge);
                                                printDynamicEdge(newEdge);
                                            }
                                            //updateWindow(dynamicWindows,curWindow,nextWindow);//update curWindow
                                            curWindow = containsWindow(dynamicWindows,nextDoc_sub);
                                            DynamicWindow nextWindow_item = new DynamicWindow();
                                            nextWindow_item.setLabel(windowLabel);
                                            nextWindow_item.setType(WindowType.ACT);
                                            List<MenuItem> subItems = sub.getItems();
                                            for (MenuItem subItem : subItems) {
                                                if (!staticMenuItem.contains(subItem)) {
                                                    String subText = subItem.getText();
                                                    if (subText != null) {
                                                        if (mUidevice.hasObject(By.text(subText))) {
                                                            UiObject2 subItem_d = mUidevice.findObject(By.text(subText));
                                                            String subItemId = subItem_d.getResourceName();
                                                            String subItemDesc = subItem_d.getContentDescription();
                                                            String subItemType = subItem_d.getClassName();
                                                            Rect subItemBounds = subItem_d.getVisibleBounds();
                                                            String subItemACT = getRunningActivity();

                                                            boolean subItemCheckable = subItem_d.isCheckable();
                                                            boolean subItemChecked = subItem_d.isChecked();
                                                            boolean subItemClickable = subItem_d.isClickable();
                                                            boolean subItemEnabled = subItem_d.isEnabled();
                                                            boolean subItemFocusable = subItem_d.isFocusable();
                                                            boolean subItemFocused = subItem_d.isFocused();
                                                            boolean subItemScrollable = subItem_d.isScrollable();
                                                            boolean subItemLong_clickable = subItem_d.isLongClickable();
                                                            boolean subItemSelected = subItem_d.isSelected();
                                                            Point subItemCenter = subItem_d.getVisibleCenter();
                                                            String subItemPack = subItem_d.getApplicationPackage();

                                                            executeEvent(subItem_d, subItem.getEvent());
                                                            executeMenuFlag = true;
                                                            staticMenuItem.add(subItem);
                                                            Log.i("TestInfo", "static: sub_menu_item " + subText + " " + menuNode.getName());

                                                            String nextPageItem = getRunningActivity();
                                                            nextWindow_item.setName(nextPageItem);

                                                            UIElement sub_element = new UIElement(subText,subItemId,subItemDesc,subItemType,subItemBounds,subItemACT);
                                                            VisitInfo sub_info = new VisitInfo();
                                                            if (isDialog()) {
                                                                sub_info.setOpenType(1);
                                                                sub_info.setVisit(false);
                                                                nextWindow_item.setType(WindowType.DIALOG);
                                                            } else if (isMenu()) {
                                                                sub_info.setOpenType(2);
                                                                sub_info.setVisit(false);
                                                                nextWindow_item.setType((WindowType.OPTMENU));
                                                            }
                                                            Integer sub_frequency = sub_info.getFrequency() + 1;
                                                            sub_info.setFrequency(sub_frequency);
                                                            visitMap.put(sub_element,sub_info);
                                                            dumpWindow();
                                                            Document nextDoc_item = getWinHierarchyfromFile(hierarchyPath);
                                                            nextDoc_item = removeSysUiNode(nextDoc_item);
                                                            nextWindow_item.setWinHierarchy(nextDoc_item);
                                                            DynamicWidget dmWidget_item = new DynamicWidget();
                                                            dmWidget_item.setResource_id(subItemId);
                                                            dmWidget_item.setText(subText);
                                                            if(subText != null){
                                                                dmWidget_item.setCompleteText(subText);
                                                            }
                                                            dmWidget_item.setPack(subItemPack);
                                                            dmWidget_item.setWidget_class(subItemType);
                                                            dmWidget_item.setContent_desc(subItemDesc);
                                                            dmWidget_item.setCheckable(subItemCheckable);
                                                            dmWidget_item.setChecked(subItemChecked);
                                                            dmWidget_item.setClickable(subItemClickable);
                                                            dmWidget_item.setEnabled(subItemEnabled);
                                                            dmWidget_item.setFocusable(subItemFocusable);
                                                            dmWidget_item.setFocused(subItemFocused);
                                                            dmWidget_item.setScrollable(subItemScrollable);
                                                            dmWidget_item.setLong_clickable(subItemLong_clickable);
                                                            dmWidget_item.setSelected(subItemSelected);
                                                            dmWidget_item.setBounds(subItemBounds);
                                                            dmWidget_item.setCenter(subItemCenter);
                                                            if(!containsEdge(dynamicEdges,curWindow,nextWindow_item,dmWidget_item)){
                                                                DynamicWindow next_w = containsWindow(dynamicWindows,nextDoc_item);
                                                                if(next_w == null){
                                                                    nextWindow_item.setId(++dmWindowId);
                                                                    nextWindow_item.setMaySaved(maySaved());
                                                                    dynamicWindows.add(nextWindow_item);
                                                                }else{
                                                                    nextWindow_item = next_w;
                                                                }
                                                                DynamicEdge newEdge = new DynamicEdge();
                                                                newEdge.setId(++dmEdgeId);
                                                                newEdge.setLabel(windowLabel);
                                                                newEdge.setSource(curWindow);
                                                                newEdge.setTarget(nextWindow_item);
                                                                newEdge.setType(getTranType(curWindow));

                                                                dmWidget_item.setId(++dmWidgetId);

                                                                newEdge.setWidget(dmWidget_item);
                                                                dynamicEdges.add(newEdge);
                                                                printDynamicEdge(newEdge);
                                                            }
                                                            break;
                                                        } else {
                                                            staticMenuItem.add(subItem);
                                                            executeMenuFlag = false;
                                                        }
                                                    } else {
                                                        staticMenuItem.add(subItem);
                                                        executeMenuFlag = false;
                                                    }
                                                } else {
                                                    executeMenuFlag = false;
                                                }
                                            }
                                            if(executeMenuFlag == true){
                                                break;
                                            }
                                        } else {
                                            staticSubMenu.add(sub);
                                            executeMenuFlag = false;
                                        }
                                    } else {
                                        staticSubMenu.add(sub);
                                        executeMenuFlag = false;
                                    }
                                } else {
                                    executeMenuFlag = false;
                                }
                            }
                            if (menuWidget instanceof MenuItem) {
                                MenuItem item = (MenuItem) menuWidget;
                                if (!staticMenuItem.contains(item)) {
                                    String text = item.getText();
                                    if (text != null) {
                                        if (mUidevice.hasObject(By.text(text))) {
                                            UiObject2 item_d = mUidevice.findObject(By.text(text));
                                            String resId = item_d.getResourceName();
                                            String contentDesc = item_d.getContentDescription();
                                            String classType = item_d.getClassName();
                                            Rect visBounds = item_d.getVisibleBounds();
                                            String hostACT = getRunningActivity();

                                            boolean checkable = item_d.isCheckable();
                                            boolean checked = item_d.isChecked();
                                            boolean clickable = item_d.isClickable();
                                            boolean enabled = item_d.isEnabled();
                                            boolean focusable = item_d.isFocusable();
                                            boolean focused = item_d.isFocused();
                                            boolean scrollable = item_d.isScrollable();
                                            boolean long_clickable = item_d.isLongClickable();
                                            boolean selected = item_d.isSelected();
                                            Point center = item_d.getVisibleCenter();
                                            String appPack = item_d.getApplicationPackage();

                                            executeEvent(item_d, item.getEvent());
                                            executeMenuFlag = true;
                                            staticMenuItem.add(item);
                                            Log.i("TestInfo", "static: menu_item " + text + " " + menuNode.getName());

                                            String nextPageItem = getRunningActivity();
                                            nextWindow_menu.setName(nextPageItem);

                                            UIElement element = new UIElement(text,resId,contentDesc,classType,visBounds,hostACT);
                                            VisitInfo info = new VisitInfo();
                                            if (isDialog()) {
                                                info.setOpenType(1);
                                                info.setVisit(false);
                                                nextWindow_menu.setType(WindowType.DIALOG);
                                            } else if (isMenu()) {
                                                info.setOpenType(2);
                                                info.setVisit(false);
                                                nextWindow_menu.setType((WindowType.OPTMENU));
                                            }
                                            Integer frequency = info.getFrequency() + 1;
                                            info.setFrequency(frequency);
                                            visitMap.put(element,info);
                                            dumpWindow();
                                            Document nextDoc_item = getWinHierarchyfromFile(hierarchyPath);
                                            nextDoc_item = removeSysUiNode(nextDoc_item);
                                            nextWindow_menu.setWinHierarchy(nextDoc_item);
                                            DynamicWidget dmWidget = new DynamicWidget();
                                            dmWidget.setResource_id(resId);
                                            dmWidget.setText(text);
                                            if(text != null){
                                                dmWidget.setCompleteText(text);
                                            }
                                            dmWidget.setPack(appPack);
                                            dmWidget.setWidget_class(classType);
                                            dmWidget.setContent_desc(contentDesc);
                                            dmWidget.setCheckable(checkable);
                                            dmWidget.setChecked(checked);
                                            dmWidget.setClickable(clickable);
                                            dmWidget.setEnabled(enabled);
                                            dmWidget.setFocusable(focusable);
                                            dmWidget.setFocused(focused);
                                            dmWidget.setScrollable(scrollable);
                                            dmWidget.setLong_clickable(long_clickable);
                                            dmWidget.setSelected(selected);
                                            dmWidget.setBounds(visBounds);
                                            dmWidget.setCenter(center);
                                            if(!containsEdge(dynamicEdges,curWindow,nextWindow_menu,dmWidget)){
                                                DynamicWindow next_w = containsWindow(dynamicWindows,nextDoc_item);
                                                if(next_w == null){
                                                    nextWindow_menu.setId(++dmWindowId);
                                                    nextWindow_menu.setMaySaved(maySaved());
                                                    dynamicWindows.add(nextWindow_menu);
                                                }else{
                                                    nextWindow_menu = next_w;
                                                }
                                                DynamicEdge newEdge = new DynamicEdge();
                                                newEdge.setId(++dmEdgeId);
                                                newEdge.setLabel(windowLabel);
                                                newEdge.setSource(curWindow);
                                                newEdge.setTarget(nextWindow_menu);
                                                newEdge.setType(getTranType(curWindow));

                                                dmWidget.setId(++dmWidgetId);

                                                newEdge.setWidget(dmWidget);
                                                dynamicEdges.add(newEdge);
                                                printDynamicEdge(newEdge);
                                            }
                                            break;
                                        } else {
                                            staticMenuItem.add(item);
                                            executeMenuFlag = false;
                                        }
                                    } else {
                                        staticMenuItem.add(item);
                                        executeMenuFlag = false;
                                    }
                                } else {
                                    executeMenuFlag = false;
                                }
                            }
                        }
                        staticFlag = executeMenuFlag;
                    }
                } else {
                    List<Widget> widgets = node.getWidgets();
                    boolean executeFlag = false;
                    for (Widget w : widgets) {
                        if (!staticWid.contains(w)) {
                            String res_id = w.getWidgetId();
                            if (res_id != null) {
                                String res_id_d = mPackage + ":id/" + res_id;
                                if (!mUidevice.hasObject(By.res(res_id_d))) {
                                    res_id_d = "android:id/" + res_id;
                                }
                                if (mUidevice.hasObject(By.res(res_id_d))) {
                                    UiObject2 w_d = mUidevice.findObject(By.res(res_id_d));
                                    String text = w_d.getText();
                                    String resId = w_d.getResourceName();
                                    String contentDesc = w_d.getContentDescription();
                                    String classType = w_d.getClassName();
                                    Rect visBounds = w_d.getVisibleBounds();
                                    String hostACT = getRunningActivity();

                                    boolean checkable = w_d.isCheckable();
                                    boolean checked = w_d.isChecked();
                                    boolean clickable = w_d.isClickable();
                                    boolean enabled = w_d.isEnabled();
                                    boolean focusable = w_d.isFocusable();
                                    boolean focused = w_d.isFocused();
                                    boolean scrollable = w_d.isScrollable();
                                    boolean long_clickable = w_d.isLongClickable();
                                    boolean selected = w_d.isSelected();
                                    Point center = w_d.getVisibleCenter();
                                    String appPack = w_d.getApplicationPackage();

                                    executeEvent(w_d, w.getEvent());
                                    nextPageName = getRunningActivity();
                                    nextWindow.setName(nextPageName);
                                    executeFlag = true;
                                    staticWid.add(w);
                                    Log.i("TestInfo", "static: widget " + w.getWidgetType() + " " + w.getEvent() + " " + node.getName());
                                    //saveExecuteWidget(w_d);

                                    UIElement element = new UIElement(text,resId,contentDesc,classType,visBounds,hostACT);
                                    VisitInfo info = new VisitInfo();
                                    if (isDialog()) {
                                        info.setOpenType(1);
                                        info.setVisit(false);
                                        nextWindow.setType(WindowType.DIALOG);
                                    } else if (isMenu()) {
                                        info.setOpenType(2);
                                        info.setVisit(false);
                                        nextWindow.setType((WindowType.OPTMENU));
                                    }
                                    Integer frequency = info.getFrequency() + 1;
                                    info.setFrequency(frequency);
                                    visitMap.put(element,info);
                                    dumpWindow();
                                    Document nextDoc = getWinHierarchyfromFile(hierarchyPath);
                                    nextDoc = removeSysUiNode(nextDoc);
                                    nextWindow.setWinHierarchy(nextDoc);
                                    DynamicWidget dmWidget = new DynamicWidget();
                                    dmWidget.setResource_id(resId);
                                    dmWidget.setText(text);
                                    if(text != null){
                                        dmWidget.setCompleteText(text);
                                    }
                                    dmWidget.setPack(appPack);
                                    dmWidget.setWidget_class(classType);
                                    dmWidget.setContent_desc(contentDesc);
                                    dmWidget.setCheckable(checkable);
                                    dmWidget.setChecked(checked);
                                    dmWidget.setClickable(clickable);
                                    dmWidget.setEnabled(enabled);
                                    dmWidget.setFocusable(focusable);
                                    dmWidget.setFocused(focused);
                                    dmWidget.setScrollable(scrollable);
                                    dmWidget.setLong_clickable(long_clickable);
                                    dmWidget.setSelected(selected);
                                    dmWidget.setBounds(visBounds);
                                    dmWidget.setCenter(center);
                                    if(!containsEdge(dynamicEdges,curWindow,nextWindow,dmWidget)){
                                        DynamicWindow next_w = containsWindow(dynamicWindows,nextDoc);
                                        if(next_w == null){
                                            nextWindow.setId(++dmWindowId);
                                            nextWindow.setMaySaved(maySaved());
                                            dynamicWindows.add(nextWindow);
                                        }else{
                                            nextWindow = next_w;
                                        }
                                        DynamicEdge newEdge = new DynamicEdge();
                                        newEdge.setId(++dmEdgeId);
                                        newEdge.setLabel(windowLabel);
                                        newEdge.setSource(curWindow);
                                        newEdge.setTarget(nextWindow);
                                        newEdge.setType(getTranType(curWindow));

                                        dmWidget.setId(++dmWidgetId);

                                        newEdge.setWidget(dmWidget);
                                        dynamicEdges.add(newEdge);
                                        printDynamicEdge(newEdge);
                                    }
                                    break;
                                } else {
                                    staticWid.add(w);
                                    executeFlag = false;
                                }
                            } else {
                                staticWid.add(w);
                                executeFlag = false;
                            }
                        } else {
                            executeFlag = false;
                        }
                    }
                    staticFlag = executeFlag;
                }
            }
            endTime = System.currentTimeMillis();
        }
        dynamicGraph.setNodes(dynamicWindows);
        dynamicGraph.setEdges(dynamicEdges);
        printDynamicGraph(dynamicGraph);
//        DynamicGraphDao graphDao1 = new DynamicGraphDao();
//        graphDao1.insertDynamicGraph(dynamicGraph);
    }

    @Test
    public void getDynamicGraph() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime;
        Set<DynamicWindow> dynamicWindows = new HashSet<>();
        List<DynamicEdge> dynamicEdges = new ArrayList<>();
        DynamicGraph dynamicGraph = new DynamicGraph();
        long dmWindowId = 0;
        long dmWidgetId = 0;
        long dmEdgeId = 0;
        while (endTime - startTime <= testTimeout) {
            String curPageName = getRunningActivity();
            dumpWindow();
            Document curDoc = getWinHierarchyfromFile(hierarchyPath);
            curDoc = removeSysUiNode(curDoc);
            DynamicWindow curWindow = containsWindow(dynamicWindows,curDoc);
            if(curWindow == null){
                curWindow = new DynamicWindow();
                curWindow.setId(++dmWindowId);
                curWindow.setLabel(windowLabel);
                curWindow.setName(curPageName);
                curWindow.setType(WindowType.ACT);
                curWindow.setWinHierarchy(curDoc);
                curWindow.setMaySaved(maySaved());
                dynamicWindows.add(curWindow);
            }
            List<UiObject2> clickableElements = mUidevice.findObjects(By.clickable(true));
            filterElements(clickableElements);
            if (clickableElements.isEmpty()) {
                back();
            }

            DynamicWindow nextWindow = new DynamicWindow();
            nextWindow.setLabel(windowLabel);
            nextWindow.setType(WindowType.ACT);
            String nextPageName = curPageName;

            if (visitMap.isEmpty()) {
                UiObject2 firEle = selectBestElement(clickableElements);
                String text = firEle.getText();
                String resourceId = firEle.getResourceName();
                String contentDesc = firEle.getContentDescription();
                String classType = firEle.getClassName();
                Rect visBounds = firEle.getVisibleBounds();
                String hostACT = getRunningActivity();

                boolean checkable = firEle.isCheckable();
                boolean checked = firEle.isChecked();
                boolean clickable = firEle.isClickable();
                boolean enabled = firEle.isEnabled();
                boolean focusable = firEle.isFocusable();
                boolean focused = firEle.isFocused();
                boolean scrollable = firEle.isScrollable();
                boolean long_clickable = firEle.isLongClickable();
                boolean selected = firEle.isSelected();
                Point center = firEle.getVisibleCenter();
                String appPack = firEle.getApplicationPackage();

                UIElement firUIEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
                fill_data(firEle);
                executeEvent(firEle, "click");
                nextPageName = getRunningActivity();
                nextWindow.setName(nextPageName);
                VisitInfo firVisit = new VisitInfo();
                if (isDialog()) {
                    firVisit.setOpenType(1);
                    firVisit.setVisit(false);
                    nextWindow.setType(WindowType.DIALOG);
                } else if (isMenu()) {
                    firVisit.setOpenType(2);
                    firVisit.setVisit(false);
                    nextWindow.setType(WindowType.OPTMENU);
                }
                Integer nextFrequency = firVisit.getFrequency() + 1;
                firVisit.setFrequency(nextFrequency);
                visitMap.put(firUIEle, firVisit);
                dumpWindow();
                Document nextDoc = getWinHierarchyfromFile(hierarchyPath);
                nextDoc = removeSysUiNode(nextDoc);
                nextWindow.setWinHierarchy(nextDoc);
                DynamicWidget dmWidget = new DynamicWidget();
                dmWidget.setResource_id(resourceId);
                dmWidget.setText(text);
                if(text != null){
                    dmWidget.setCompleteText(text);
                }
                dmWidget.setPack(appPack);
                dmWidget.setWidget_class(classType);
                dmWidget.setContent_desc(contentDesc);
                dmWidget.setCheckable(checkable);
                dmWidget.setChecked(checked);
                dmWidget.setClickable(clickable);
                dmWidget.setEnabled(enabled);
                dmWidget.setFocusable(focusable);
                dmWidget.setFocused(focused);
                dmWidget.setScrollable(scrollable);
                dmWidget.setLong_clickable(long_clickable);
                dmWidget.setSelected(selected);
                dmWidget.setBounds(visBounds);
                dmWidget.setCenter(center);
                if(!containsEdge(dynamicEdges,curWindow,nextWindow,dmWidget)){
                    DynamicWindow next_w = containsWindow(dynamicWindows,nextDoc);
                    if(next_w == null){
                        nextWindow.setId(++dmWindowId);
                        nextWindow.setMaySaved(maySaved());
                        dynamicWindows.add(nextWindow);
                    }else{
                        nextWindow = next_w;
                    }
                    DynamicEdge newEdge = new DynamicEdge();
                    newEdge.setId(++dmEdgeId);
                    newEdge.setLabel(windowLabel);
                    newEdge.setSource(curWindow);
                    newEdge.setTarget(nextWindow);
                    newEdge.setType(getTranType(curWindow));

                    dmWidget.setId(++dmWidgetId);

                    newEdge.setWidget(dmWidget);
                    dynamicEdges.add(newEdge);
                }
            } else {
                if (gotoAnotherApp()) {
                    back();
                    String curPack = mUidevice.getCurrentPackageName();
                    if (curPack.equals(mPackage)) {
                        continue;
                    } else {
                        restartApp(mPackage, mainActivity);
                        continue;
                    }
                }
                for (UiObject2 cele : clickableElements) {
                    String text = cele.getText();
                    String resourceId = cele.getResourceName();
                    String contentDesc = cele.getContentDescription();
                    String classType = cele.getClassName();
                    Rect visBounds = cele.getVisibleBounds();
                    String hostACT = getRunningActivity();

                    boolean checkable = cele.isCheckable();
                    boolean checked = cele.isChecked();
                    boolean clickable = cele.isClickable();
                    boolean enabled = cele.isEnabled();
                    boolean focusable = cele.isFocusable();
                    boolean focused = cele.isFocused();
                    boolean scrollable = cele.isScrollable();
                    boolean long_clickable = cele.isLongClickable();
                    boolean selected = cele.isSelected();
                    Point center = cele.getVisibleCenter();
                    String appPack = cele.getApplicationPackage();

                    boolean isEditText = isEditText(cele);
                    UIElement uiEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
                    if (!visitMap.containsKey(uiEle)) {
                        if(isBestElement(cele)){
                            fill_data(cele);
                            executeEvent(cele, "click");
                            nextPageName = getRunningActivity();
                            nextWindow.setName(nextPageName);
                            VisitInfo subVisit = new VisitInfo();
                            if (isDialog() && !isEditText) {
                                subVisit.setOpenType(1);
                                subVisit.setVisit(false);
                                nextWindow.setType(WindowType.DIALOG);
                            } else if (isMenu() && !isEditText) {
                                subVisit.setOpenType(2);
                                subVisit.setVisit(false);
                                nextWindow.setType(WindowType.OPTMENU);
                            }
                            Integer nextFrequency = subVisit.getFrequency() + 1;
                            subVisit.setFrequency(nextFrequency);
                            visitMap.put(uiEle, subVisit);
                            dumpWindow();
                            Document nextDoc = getWinHierarchyfromFile(hierarchyPath);
                            nextDoc = removeSysUiNode(nextDoc);
                            nextWindow.setWinHierarchy(nextDoc);
                            DynamicWidget dmWidget = new DynamicWidget();
                            dmWidget.setResource_id(resourceId);
                            dmWidget.setText(text);
                            if(text != null){
                                dmWidget.setCompleteText(text);
                            }
                            dmWidget.setPack(appPack);
                            dmWidget.setWidget_class(classType);
                            dmWidget.setContent_desc(contentDesc);
                            dmWidget.setCheckable(checkable);
                            dmWidget.setChecked(checked);
                            dmWidget.setClickable(clickable);
                            dmWidget.setEnabled(enabled);
                            dmWidget.setFocusable(focusable);
                            dmWidget.setFocused(focused);
                            dmWidget.setScrollable(scrollable);
                            dmWidget.setLong_clickable(long_clickable);
                            dmWidget.setSelected(selected);
                            dmWidget.setBounds(visBounds);
                            dmWidget.setCenter(center);
                            if(!containsEdge(dynamicEdges,curWindow,nextWindow,dmWidget)){
                                DynamicWindow next_w = containsWindow(dynamicWindows,nextDoc);
                                if(next_w == null){
                                    nextWindow.setId(++dmWindowId);
                                    nextWindow.setMaySaved(maySaved());
                                    dynamicWindows.add(nextWindow);
                                }else{
                                    nextWindow = next_w;
                                }
                                DynamicEdge newEdge = new DynamicEdge();
                                newEdge.setId(++dmEdgeId);
                                newEdge.setLabel(windowLabel);
                                newEdge.setSource(curWindow);
                                newEdge.setTarget(nextWindow);
                                newEdge.setType(getTranType(curWindow));

                                dmWidget.setId(++dmWidgetId);

                                newEdge.setWidget(dmWidget);
                                dynamicEdges.add(newEdge);
                            }
                            break;
                        }else{
                            continue;
                        }
                    } else {
                        VisitInfo dmVisit = visitMap.get(uiEle);
                        if (!dmVisit.getVisit()) {
                            Log.d("open type", dmVisit.getOpenType() + "");
                            executeEvent(cele, "click");
                            Boolean dmFlag = true;//
                            List<UiObject2> dmElements = mUidevice.findObjects(By.clickable(true));
                            for (UiObject2 dmEle : dmElements) {
                                String dmText = dmEle.getText();
                                String dmResourceId = dmEle.getResourceName();
                                String dmContentDesc = dmEle.getContentDescription();
                                String dmClassType = dmEle.getClassName();
                                Rect dmVisBounds = dmEle.getVisibleBounds();
                                String dmHostACT = getRunningActivity();

//                                boolean dmCheckable = dmEle.isCheckable();
//                                boolean dmChecked = dmEle.isChecked();
//                                boolean dmClickable = dmEle.isClickable();
//                                boolean dmEnabled = dmEle.isEnabled();
//                                boolean dmFocusable = dmEle.isFocusable();
//                                boolean dmFocused = dmEle.isFocused();
//                                boolean dmScrollable = dmEle.isScrollable();
//                                boolean dmLong_clickable = dmEle.isLongClickable();
//                                boolean dmSelected = dmEle.isSelected();
//                                Point dmCenter = dmEle.getVisibleCenter();
//                                String dmAppPack = dmEle.getApplicationPackage();

                                UIElement dm = new UIElement(dmText, dmResourceId, dmContentDesc, dmClassType, dmVisBounds, dmHostACT);
                                if (!visitMap.containsKey(dm)) {
                                    dmFlag = false;
                                    break;
                                } else {
                                    dmFlag = dmFlag && visitMap.get(dm).getVisit();
                                }
                            }
                            Integer nextFrequency = dmVisit.getFrequency() + 1;
                            dmVisit.setFrequency(nextFrequency);
                            dmVisit.setVisit(dmFlag);
                            if (dmVisit.getFrequency() > frequencyCap) {
                                dmVisit.setVisit(true);
                            }
                            break;
                        }
                    }
                    if (clickableElements.indexOf(cele) == clickableElements.size() - 1) {
                        UiObject2 notExecute = selectNotExecuteElement(clickableElements);
                        if(notExecute != null){
                            String ne_text = notExecute.getText();
                            String ne_resourceId = notExecute.getResourceName();
                            String ne_contentDesc = notExecute.getContentDescription();
                            String ne_classType = notExecute.getClassName();
                            Rect ne_visBounds = notExecute.getVisibleBounds();
                            String ne_hostACT = getRunningActivity();

                            boolean ne_checkable = notExecute.isCheckable();
                            boolean ne_checked = notExecute.isChecked();
                            boolean ne_clickable = notExecute.isClickable();
                            boolean ne_enabled = notExecute.isEnabled();
                            boolean ne_focusable = notExecute.isFocusable();
                            boolean ne_focused = notExecute.isFocused();
                            boolean ne_scrollable = notExecute.isScrollable();
                            boolean ne_long_clickable = notExecute.isLongClickable();
                            boolean ne_selected = notExecute.isSelected();
                            Point ne_center = notExecute.getVisibleCenter();
                            String ne_appPack = notExecute.getApplicationPackage();

                            boolean isEdit = isEditText(notExecute);
                            fill_data(notExecute);
                            executeEvent(notExecute, "click");
                            nextPageName = getRunningActivity();
                            nextWindow.setName(nextPageName);
                            VisitInfo subVisit = new VisitInfo();
                            if (isDialog() && !isEdit) {
                                subVisit.setOpenType(1);
                                subVisit.setVisit(false);
                                nextWindow.setType(WindowType.DIALOG);
                            } else if (isMenu() && !isEdit) {
                                subVisit.setOpenType(2);
                                subVisit.setVisit(false);
                                nextWindow.setType(WindowType.OPTMENU);
                            }
                            Integer nextFrequency = subVisit.getFrequency() + 1;
                            subVisit.setFrequency(nextFrequency);
                            visitMap.put(new UIElement(ne_text,ne_resourceId,ne_contentDesc,ne_classType,ne_hostACT), subVisit);
                            dumpWindow();
                            Document nextDoc = getWinHierarchyfromFile(hierarchyPath);
                            nextDoc = removeSysUiNode(nextDoc);
                            nextWindow.setWinHierarchy(nextDoc);
                            DynamicWidget dmWidget = new DynamicWidget();
                            dmWidget.setResource_id(ne_resourceId);
                            dmWidget.setText(ne_text);
                            if(ne_text != null){
                                dmWidget.setCompleteText(ne_text);
                            }
                            dmWidget.setPack(ne_appPack);
                            dmWidget.setWidget_class(ne_classType);
                            dmWidget.setContent_desc(ne_contentDesc);
                            dmWidget.setCheckable(ne_checkable);
                            dmWidget.setChecked(ne_checked);
                            dmWidget.setClickable(ne_clickable);
                            dmWidget.setEnabled(ne_enabled);
                            dmWidget.setFocusable(ne_focusable);
                            dmWidget.setFocused(ne_focused);
                            dmWidget.setScrollable(ne_scrollable);
                            dmWidget.setLong_clickable(ne_long_clickable);
                            dmWidget.setSelected(ne_selected);
                            dmWidget.setBounds(ne_visBounds);
                            dmWidget.setCenter(ne_center);
                            if(!containsEdge(dynamicEdges,curWindow,nextWindow,dmWidget)){
                                DynamicWindow next_w = containsWindow(dynamicWindows,nextDoc);
                                if(next_w == null){
                                    nextWindow.setId(++dmWindowId);
                                    nextWindow.setMaySaved(maySaved());
                                    dynamicWindows.add(nextWindow);
                                }else{
                                    nextWindow = next_w;
                                }
                                DynamicEdge newEdge = new DynamicEdge();
                                newEdge.setId(++dmEdgeId);
                                newEdge.setLabel(windowLabel);
                                newEdge.setSource(curWindow);
                                newEdge.setTarget(nextWindow);
                                newEdge.setType(getTranType(curWindow));

                                dmWidget.setId(++dmWidgetId);

                                newEdge.setWidget(dmWidget);
                                dynamicEdges.add(newEdge);
                            }
                            break;
                        }
                        String curAct = getRunningActivity();
                        if (((mainActivity.equals(curAct))) && !isDialog() && !isMenu()) {
                            UiObject2 optElement = findOptimalElement(clickableElements);
                            String opt_text = optElement.getText();
                            String opt_resourceId = optElement.getResourceName();
                            String opt_contentDesc = optElement.getContentDescription();
                            String opt_classType = optElement.getClassName();
                            Rect opt_visBounds = optElement.getVisibleBounds();
                            String opt_hostACT = curAct;
                            UIElement opt_uiElement = new UIElement(opt_text, opt_resourceId, opt_contentDesc, opt_classType, opt_visBounds, opt_hostACT);
                            VisitInfo opt_info = visitMap.get(opt_uiElement);
                            executeEvent(optElement, "click");
                            opt_info.setFrequency(opt_info.getFrequency() + 1);
                            visitMap.put(opt_uiElement, opt_info);
                            break;
                        } else {
                            back();
                            break;
                        }
                    }
                }
            }
            endTime = System.currentTimeMillis();
        }
        dynamicGraph.setNodes(dynamicWindows);
        dynamicGraph.setEdges(dynamicEdges);
        printDynamicGraph(dynamicGraph);
//        DynamicGraphDao graphDao = new DynamicGraphDao();
//        graphDao.insertDynamicGraph(dynamicGraph);
    }

    @Test
    public void getDynamicGraphPlain() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime;
        Set<DynamicWindow> dynamicWindows = new HashSet<>();
        List<DynamicEdge> dynamicEdges = new ArrayList<>();
        DynamicGraph dynamicGraph = new DynamicGraph();
        long dmWindowId = 0;
        long dmWidgetId = 0;
        long dmEdgeId = 0;
        UIElement preUI = null;
        while (endTime - startTime <= testTimeout) {
            String curPageName = getRunningActivity();
            dumpWindow();
            Document curDoc = getWinHierarchyfromFile(hierarchyPath);
            curDoc = removeSysUiNode(curDoc);
            DynamicWindow curWindow = containsWindow(dynamicWindows,curDoc);
            if(curWindow == null){
                curWindow = new DynamicWindow();
                curWindow.setId(++dmWindowId);
                curWindow.setLabel(windowLabel);
                curWindow.setName(curPageName);
                curWindow.setType(WindowType.ACT);
                curWindow.setWinHierarchy(curDoc);
                boolean maySaved = maySaved();
                curWindow.setMaySaved(maySaved);
                if(maySaved){
                    initEdits();
                    List<Map<String,String>> maySavedEdits = widgetsHasId(getMaySavedEdits());
                    rotateLeft();
                    List<Map<String,String>> savedRotate = getFinalSavedEdits(maySavedEdits);
                    rotateNatural();

                    initEdits();
                    List<Map<String,String>> maySavedEdits2 = widgetsHasId(getMaySavedEdits());
                    back();
                    List<Map<String,String>> savedBack = null;
                    if(mUidevice.getCurrentPackageName().equals(mUidevice.getLauncherPackageName())){
                        startApp(mainActivityRaw);
                        savedBack = getFinalSavedEdits(maySavedEdits2);
                    }else{
                        UiObject2 preObject = getUiObjectFrom(preUI);
                        if(preObject != null){
                            executeEvent(preObject,"click");
                            savedBack = getFinalSavedEdits(maySavedEdits2);
                        }else{
                            savedBack = maySavedEdits2;
                        }
                    }
                    curWindow.setSavedWidgets(unionSavedEdits(savedRotate,savedBack));
                }
                dynamicWindows.add(curWindow);
            }
            List<UiObject2> clickableElements = mUidevice.findObjects(By.clickable(true));
            filterElements(clickableElements);
            if (clickableElements.isEmpty()) {
                back();
            }

            DynamicWindow nextWindow = new DynamicWindow();
            nextWindow.setLabel(windowLabel);
            nextWindow.setType(WindowType.ACT);
            String nextPageName = curPageName;

            if (visitMap.isEmpty()) {
                UiObject2 firEle = clickableElements.get(0);
                String text = firEle.getText();
                String resourceId = firEle.getResourceName();
                String contentDesc = firEle.getContentDescription();
                String classType = firEle.getClassName();
                Rect visBounds = firEle.getVisibleBounds();
                String hostACT = getRunningActivity();

                boolean checkable = firEle.isCheckable();
                boolean checked = firEle.isChecked();
                boolean clickable = firEle.isClickable();
                boolean enabled = firEle.isEnabled();
                boolean focusable = firEle.isFocusable();
                boolean focused = firEle.isFocused();
                boolean scrollable = firEle.isScrollable();
                boolean long_clickable = firEle.isLongClickable();
                boolean selected = firEle.isSelected();
                Point center = firEle.getVisibleCenter();
                String appPack = firEle.getApplicationPackage();

                UIElement firUIEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
                fill_data(firEle);
                executeEvent(firEle, "click");
                preUI = firUIEle;
                nextPageName = getRunningActivity();
                nextWindow.setName(nextPageName);
                VisitInfo firVisit = new VisitInfo();
                if (isDialog()) {
                    firVisit.setOpenType(1);
                    firVisit.setVisit(false);
                    nextWindow.setType(WindowType.DIALOG);
                } else if (isMenu()) {
                    firVisit.setOpenType(2);
                    firVisit.setVisit(false);
                    nextWindow.setType(WindowType.OPTMENU);
                }
                Integer nextFrequency = firVisit.getFrequency() + 1;
                firVisit.setFrequency(nextFrequency);
                visitMap.put(firUIEle, firVisit);
                dumpWindow();
                Document nextDoc = getWinHierarchyfromFile(hierarchyPath);
                nextDoc = removeSysUiNode(nextDoc);
                nextWindow.setWinHierarchy(nextDoc);
                DynamicWidget dmWidget = new DynamicWidget();
                dmWidget.setResource_id(resourceId);
                dmWidget.setText(text);
                if(text != null){
                    dmWidget.setCompleteText(text);
                }
                dmWidget.setPack(appPack);
                dmWidget.setWidget_class(classType);
                dmWidget.setContent_desc(contentDesc);
                dmWidget.setCheckable(checkable);
                dmWidget.setChecked(checked);
                dmWidget.setClickable(clickable);
                dmWidget.setEnabled(enabled);
                dmWidget.setFocusable(focusable);
                dmWidget.setFocused(focused);
                dmWidget.setScrollable(scrollable);
                dmWidget.setLong_clickable(long_clickable);
                dmWidget.setSelected(selected);
                dmWidget.setBounds(visBounds);
                dmWidget.setCenter(center);
                if(!containsEdge(dynamicEdges,curWindow,nextWindow,dmWidget)){
                    DynamicWindow next_w = containsWindow(dynamicWindows,nextDoc);
                    if(next_w == null){
                        nextWindow.setId(++dmWindowId);
                        boolean maySaved = maySaved();
                        nextWindow.setMaySaved(maySaved);
                        if(maySaved){
                            initEdits();
                            List<Map<String,String>> maySavedEdits = widgetsHasId(getMaySavedEdits());
                            rotateLeft();
                            List<Map<String,String>> savedRotate = getFinalSavedEdits(maySavedEdits);
                            rotateNatural();

                            initEdits();
                            List<Map<String,String>> maySavedEdits2 = widgetsHasId(getMaySavedEdits());
                            back();
                            List<Map<String,String>> savedBack = null;
                            if(mUidevice.getCurrentPackageName().equals(mUidevice.getLauncherPackageName())){
                                startApp(mainActivityRaw);
                                savedBack = getFinalSavedEdits(maySavedEdits2);
                            }else{
                                UiObject2 preObject = getUiObjectFrom(preUI);
                                if(preObject != null){
                                    executeEvent(preObject,"click");
                                    savedBack = getFinalSavedEdits(maySavedEdits2);
                                }else{
                                    savedBack = maySavedEdits2;
                                }
                            }
                            nextWindow.setSavedWidgets(unionSavedEdits(savedRotate,savedBack));
                        }
                        dynamicWindows.add(nextWindow);
                    }else{
                        nextWindow = next_w;
                    }
                    DynamicEdge newEdge = new DynamicEdge();
                    newEdge.setId(++dmEdgeId);
                    newEdge.setLabel(windowLabel);
                    newEdge.setSource(curWindow);
                    newEdge.setTarget(nextWindow);
                    newEdge.setType(getTranType(curWindow));

                    dmWidget.setId(++dmWidgetId);

                    newEdge.setWidget(dmWidget);
                    dynamicEdges.add(newEdge);
                }
            } else {
                if (gotoAnotherApp()) {
                    back();
                    String curPack = mUidevice.getCurrentPackageName();
                    if (curPack.equals(mPackage)) {
                        continue;
                    } else {
                        restartApp(mainActivityRaw);
                        continue;
                    }
                }
                for (UiObject2 cele : clickableElements) {
                    String text = cele.getText();
                    String resourceId = cele.getResourceName();
                    String contentDesc = cele.getContentDescription();
                    String classType = cele.getClassName();
                    Rect visBounds = cele.getVisibleBounds();
                    String hostACT = getRunningActivity();

                    boolean checkable = cele.isCheckable();
                    boolean checked = cele.isChecked();
                    boolean clickable = cele.isClickable();
                    boolean enabled = cele.isEnabled();
                    boolean focusable = cele.isFocusable();
                    boolean focused = cele.isFocused();
                    boolean scrollable = cele.isScrollable();
                    boolean long_clickable = cele.isLongClickable();
                    boolean selected = cele.isSelected();
                    Point center = cele.getVisibleCenter();
                    String appPack = cele.getApplicationPackage();

                    boolean isEditText = isEditText(cele);
                    UIElement uiEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
                    if (!visitMap.containsKey(uiEle)) {
                        fill_data(cele);
                        executeEvent(cele, "click");
                        preUI = uiEle;
                        nextPageName = getRunningActivity();
                        nextWindow.setName(nextPageName);
                        VisitInfo subVisit = new VisitInfo();
                        if (isDialog() && !isEditText) {
                            subVisit.setOpenType(1);
                            subVisit.setVisit(false);
                            nextWindow.setType(WindowType.DIALOG);
                        } else if (isMenu() && !isEditText) {
                            subVisit.setOpenType(2);
                            subVisit.setVisit(false);
                            nextWindow.setType(WindowType.OPTMENU);
                        }
                        Integer nextFrequency = subVisit.getFrequency() + 1;
                        subVisit.setFrequency(nextFrequency);
                        visitMap.put(uiEle, subVisit);
                        dumpWindow();
                        Document nextDoc = getWinHierarchyfromFile(hierarchyPath);
                        nextDoc = removeSysUiNode(nextDoc);
                        nextWindow.setWinHierarchy(nextDoc);
                        DynamicWidget dmWidget = new DynamicWidget();
                        dmWidget.setResource_id(resourceId);
                        dmWidget.setText(text);
                        if(text != null){
                            dmWidget.setCompleteText(text);
                        }
                        dmWidget.setPack(appPack);
                        dmWidget.setWidget_class(classType);
                        dmWidget.setContent_desc(contentDesc);
                        dmWidget.setCheckable(checkable);
                        dmWidget.setChecked(checked);
                        dmWidget.setClickable(clickable);
                        dmWidget.setEnabled(enabled);
                        dmWidget.setFocusable(focusable);
                        dmWidget.setFocused(focused);
                        dmWidget.setScrollable(scrollable);
                        dmWidget.setLong_clickable(long_clickable);
                        dmWidget.setSelected(selected);
                        dmWidget.setBounds(visBounds);
                        dmWidget.setCenter(center);
                        if(!containsEdge(dynamicEdges,curWindow,nextWindow,dmWidget)){
                            DynamicWindow next_w = containsWindow(dynamicWindows,nextDoc);
                            if(next_w == null){
                                nextWindow.setId(++dmWindowId);
                                boolean maySaved = maySaved();
                                nextWindow.setMaySaved(maySaved);
                                if(maySaved){
                                    initEdits();
                                    List<Map<String,String>> maySavedEdits = widgetsHasId(getMaySavedEdits());
                                    rotateLeft();
                                    List<Map<String,String>> savedRotate = getFinalSavedEdits(maySavedEdits);
                                    rotateNatural();

                                    initEdits();
                                    List<Map<String,String>> maySavedEdits2 = widgetsHasId(getMaySavedEdits());
                                    back();
                                    List<Map<String,String>> savedBack = null;
                                    if(mUidevice.getCurrentPackageName().equals(mUidevice.getLauncherPackageName())){
                                        startApp(mainActivityRaw);
                                        savedBack = getFinalSavedEdits(maySavedEdits2);
                                    }else{
                                        UiObject2 preObject = getUiObjectFrom(preUI);
                                        if(preObject != null){
                                            executeEvent(preObject,"click");
                                            savedBack = getFinalSavedEdits(maySavedEdits2);
                                        }else{
                                            savedBack = maySavedEdits2;
                                        }
                                    }
                                    nextWindow.setSavedWidgets(unionSavedEdits(savedRotate,savedBack));
                                }
                                dynamicWindows.add(nextWindow);
                            }else{
                                nextWindow = next_w;
                            }
                            DynamicEdge newEdge = new DynamicEdge();
                            newEdge.setId(++dmEdgeId);
                            newEdge.setLabel(windowLabel);
                            newEdge.setSource(curWindow);
                            newEdge.setTarget(nextWindow);
                            newEdge.setType(getTranType(curWindow));

                            dmWidget.setId(++dmWidgetId);

                            newEdge.setWidget(dmWidget);
                            dynamicEdges.add(newEdge);
                        }
                        break;
                    } else {
                        VisitInfo dmVisit = visitMap.get(uiEle);
                        if (!dmVisit.getVisit()) {
                            Log.d("open type", dmVisit.getOpenType() + "");
                            executeEvent(cele, "click");
                            preUI = uiEle;
                            Boolean dmFlag = true;//
                            List<UiObject2> dmElements = mUidevice.findObjects(By.clickable(true));
                            for (UiObject2 dmEle : dmElements) {
                                String dmText = dmEle.getText();
                                String dmResourceId = dmEle.getResourceName();
                                String dmContentDesc = dmEle.getContentDescription();
                                String dmClassType = dmEle.getClassName();
                                Rect dmVisBounds = dmEle.getVisibleBounds();
                                String dmHostACT = getRunningActivity();

//                                boolean dmCheckable = dmEle.isCheckable();
//                                boolean dmChecked = dmEle.isChecked();
//                                boolean dmClickable = dmEle.isClickable();
//                                boolean dmEnabled = dmEle.isEnabled();
//                                boolean dmFocusable = dmEle.isFocusable();
//                                boolean dmFocused = dmEle.isFocused();
//                                boolean dmScrollable = dmEle.isScrollable();
//                                boolean dmLong_clickable = dmEle.isLongClickable();
//                                boolean dmSelected = dmEle.isSelected();
//                                Point dmCenter = dmEle.getVisibleCenter();
//                                String dmAppPack = dmEle.getApplicationPackage();

                                UIElement dm = new UIElement(dmText, dmResourceId, dmContentDesc, dmClassType, dmVisBounds, dmHostACT);
                                if (!visitMap.containsKey(dm)) {
                                    dmFlag = false;
                                    break;
                                } else {
                                    dmFlag = dmFlag && visitMap.get(dm).getVisit();
                                }
                            }
                            Integer nextFrequency = dmVisit.getFrequency() + 1;
                            dmVisit.setFrequency(nextFrequency);
                            dmVisit.setVisit(dmFlag);
                            if (dmVisit.getFrequency() > frequencyCap) {
                                dmVisit.setVisit(true);
                            }
                            break;
                        }
                    }
                    if (clickableElements.indexOf(cele) == clickableElements.size() - 1) {
                        String curAct = getRunningActivity();
                        if (((mainActivity.equals(curAct))) && !isDialog() && !isMenu()) {
                            UiObject2 optElement = findOptimalElement(clickableElements);
                            String opt_text = optElement.getText();
                            String opt_resourceId = optElement.getResourceName();
                            String opt_contentDesc = optElement.getContentDescription();
                            String opt_classType = optElement.getClassName();
                            Rect opt_visBounds = optElement.getVisibleBounds();
                            String opt_hostACT = curAct;
                            UIElement opt_uiElement = new UIElement(opt_text, opt_resourceId, opt_contentDesc, opt_classType, opt_visBounds, opt_hostACT);
                            VisitInfo opt_info = visitMap.get(opt_uiElement);
                            executeEvent(optElement, "click");
                            preUI = opt_uiElement;
                            opt_info.setFrequency(opt_info.getFrequency() + 1);
                            visitMap.put(opt_uiElement, opt_info);
                            break;
                        } else {
                            back();
                            break;
                        }
                    }
                }
            }
            endTime = System.currentTimeMillis();
        }
        dynamicGraph.setNodes(dynamicWindows);
        dynamicGraph.setEdges(dynamicEdges);
        printDynamicGraph(dynamicGraph);
//        DynamicGraphDao graphDao = new DynamicGraphDao();
//        graphDao.insertDynamicGraph(dynamicGraph);
    }

    @After
    public void tearDown() {
        //stopApp(mUidevice.getCurrentPackageName());//stop app after test
    }

    private UiObject2 getUiObjectFrom(UIElement uiElement){
        String resId = uiElement.getResourceId();
        String text = uiElement.getText();
        String contentDesc = uiElement.getContentDesc();
        //String classType = uiElement.getClassType();
        Rect visBounds = uiElement.getVisBounds();
        if(resId != null){
            return mUidevice.findObject(By.res(resId));
        }else if(text != null){
            return mUidevice.findObject(By.text(text));
        }else if(contentDesc != null){
            return mUidevice.findObject(By.desc(contentDesc));
        }else{
            return getUiObjectFromVis(visBounds);
        }
    }

    private UiObject2 getUiObjectFromVis(Rect visBounds){
        UiObject2 rootObject = getRootElement();
        return getUiObjectFromVis(rootObject,visBounds);
    }

    private UiObject2 getUiObjectFromVis(UiObject2 rootObject, Rect visBounds){
        if(rootObject.getVisibleBounds().equals(visBounds))
            return rootObject;
        for(UiObject2 childObject : rootObject.getChildren()){
            UiObject2 obj = getUiObjectFromVis(childObject,visBounds);
            if(obj != null)
                return obj;
        }
        return null;
    }

    private UiObject2 selectBestElement(List<UiObject2> elements){
        List<UiObject2> buttonObjects = new ArrayList<>();
        List<UiObject2> textviewObjects = new ArrayList<>();
        for(UiObject2 element : elements){
            String type = element.getClassName();
            if(type.equals("android.widget.Button") || type.equals("android.widget.ImageButton")){
                buttonObjects.add(element);
                if(StringUtil.isSubmitted(element.getText()) || StringUtil.isSubmitted(element.getContentDescription())){
                    return element;
                }
            }
            if(type.equals("android.widget.TextView")){
                textviewObjects.add(element);
                if(StringUtil.isSubmitted(element.getText()) || StringUtil.isSubmitted(element.getContentDescription())){
                    return element;
                }
            }
        }
        if(!buttonObjects.isEmpty())
            return buttonObjects.get(0);
        if(!textviewObjects.isEmpty())
            return textviewObjects.get(0);
        return elements.get(0);
    }

    private UiObject2 selectNotExecuteElement(List<UiObject2> elements){
        for(UiObject2 element : elements){
            String text = element.getText();
            String resourceId = element.getResourceName();
            String contentDesc = element.getContentDescription();
            String classType = element.getClassName();
            Rect visBounds = element.getVisibleBounds();
            String hostACT = getRunningActivity();
            UIElement uiEle = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
            if(!visitMap.containsKey(uiEle))
                return element;
        }
        return null;
    }

    private boolean isBestElement(UiObject2 element){
        String type = element.getClassName();
        if(type.equals("android.widget.Button") || type.equals("android.widget.ImageButton")){
            return true;
        }
        return false;
    }

    private String preProcessPhrase(String phrase){
        String[] words = phrase.split(" ");
        for(int i=0; i<words.length;i++){
            String word = words[i];
            if(!allCapital(word)){
                char[] chars = word.toCharArray();
                if(chars[0] >= 'A' && chars[0] <= 'Z'){
                    chars[0] += 32;
                }
                words[i] = String.valueOf(chars);
            }
        }
        return String.join(" ",words);
    }

    /**
     * if all characters are capital letter
     * @param word
     * @return
     */
    private boolean allCapital(String word){
        for(int i=0; i<word.length(); i++){
            char c = word.charAt(i);
            if(!(c >= 'A' && c <= 'Z')){
                return false;
            }
        }
        return true;
    }

    private String getCompletePhrase(String phrase){
        String p_pro = preProcessPhrase(phrase);//phrase after preprocessing
        String type = getPhraseType(p_pro);
        if(type.equals("only noun")){
            if(isEditablePage()){
                return "edit " + p_pro;
            }else{
                return "view " + p_pro;
            }
        }else if(type.equals("only verb")){
            return fillNoun(p_pro);
        }
        return p_pro;
    }

    private String fillNoun(String verb){
        String activity = getRunningActivity();
        if(activity.endsWith("Activity")){
            List<String> segs = StringUtil.segmentation(activity);
            segs.remove(segs.size() - 1);
            String new_words = String.join(" ",segs);
            String type = getPhraseType(new_words);
            if(type.equals("verb noun")){
                return new_words;
            }else if(type.equals("only noun")){
                return verb + " " + new_words;
            }
        }else{
            List<UiObject2> textViews = mUidevice.findObjects(By.clazz("android.widget.TextView"));
            for(UiObject2 textView : textViews){
                String text = textView.getText();
                String pack = textView.getApplicationPackage();
                if(text != null && pack.equals(mPackage)){
                    String pageLabel = preProcessPhrase(text);
                    String labelType = getPhraseType(pageLabel);
                    if(pageLabel.startsWith(verb) && labelType.equals("verb noun")){
                        return pageLabel;
                    }else if(labelType.equals("only noun")){
                        return verb + " " + pageLabel;
                    }
                }
            }
        }
        return verb;
    }

    private boolean isEditablePage(){
        List<UiObject2> editTexts = mUidevice.findObjects(By.clazz("android.widget.EditText"));
        List<UiObject2> spinners = mUidevice.findObjects(By.clazz("android.widget.Spinner"));
        List<UiObject2> radios = mUidevice.findObjects(By.clazz("android.widget.CheckedTextView"));
        List<UiObject2> checkBoxs = mUidevice.findObjects(By.clazz("android.widget.CheckBox"));
        List<UiObject2> ratingBars = mUidevice.findObjects(By.clazz("android.widget.RatingBar"));
        List<UiObject2> radioBtns = mUidevice.findObjects(By.clazz("android.widget.RadioButton"));
        List<UiObject2> switchBtns = mUidevice.findObjects(By.clazz("android.widget.Switch"));
        int num = editTexts.size() + spinners.size() + radios.size() + checkBoxs.size() + ratingBars.size() +
                radioBtns.size() + switchBtns.size();
        if(num > 0){
            return true;
        }
        return false;
    }

    private boolean hasEditWidgets(){
        List<UiObject2> editTexts = mUidevice.findObjects(By.clazz("android.widget.EditText"));
        int num = 0;
        for(UiObject2 editText : editTexts){
            UiObject2 parent = editText.getParent();
            if(!parent.getClassName().equals("android.widget.NumberPicker")){
                num ++;
            }
        }
        if(num > 0 || hasOtherEdits()){
            return true;
        }
        return false;
    }

    private boolean hasOtherEdits(){
        return mUidevice.hasObject(By.clazz("android.widget.Spinner")) || mUidevice.hasObject(By.clazz("android.widget.CheckBox")) ||
                mUidevice.hasObject(By.clazz("android.widget.RadioButton")) || mUidevice.hasObject(By.clazz("android.widget.CheckedTextView")) ||
                mUidevice.hasObject(By.clazz("android.widget.Switch")) || mUidevice.hasObject(By.clazz("android.widget.SeekBar")) ||
                mUidevice.hasObject(By.clazz("android.widget.RatingBar")) || mUidevice.hasObject(By.clazz("android.widget.AutoCompleteTextView"));
    }

    private boolean maySaved(){
        if(!hasEditWidgets()){
            return false;
        }
        List<UiObject2> buttons = mUidevice.findObjects(By.clazz("android.widget.Button"));
        List<UiObject2> imgBtns = mUidevice.findObjects(By.clazz("android.widget.ImageButton"));
        List<UiObject2> textViews = mUidevice.findObjects(By.clazz("android.widget.TextView"));
        int click_num = 0;
        for(UiObject2 textView : textViews){
            if(textView.isClickable()){
                click_num ++;
            }
        }
        return ((buttons.size() > 0) || (imgBtns.size() > 0) || (click_num > 0));
    }

    private List<Map<String,String>> getMaySavedEdits(){
        final String resourceKey = "resource_id";
        final String clazzKey = "clazz";
        final String textKey = "text";
        final String descKey = "content_desc";
        final String checkedKey = "checked";

        List<Map<String, String>> maySavedEdits = new ArrayList<>();
        String regex = "android\\.widget\\." + "((EditText)|(CheckBox)|(RadioButton)|(Switch)|(Spinner)|(CheckedTextView)|(AutoCompleteTextView))";
        Pattern p = Pattern.compile(regex);
        List<UiObject2> edits = mUidevice.findObjects(By.clazz(p));
        for(UiObject2 edit : edits){
            String clazz = edit.getClassName();
            if(clazz.equals("android.widget.EditText")){
                UiObject2 parent = edit.getParent();
                if(!parent.getClassName().equals("android.widget.NumberPicker")){
                    Map<String, String> editText = new HashMap<>();
                    editText.put(resourceKey,edit.getResourceName());
                    editText.put(clazzKey,clazz);
                    editText.put(textKey,edit.getText());
                    editText.put(descKey,edit.getContentDescription());
                    maySavedEdits.add(editText);
                }
            }else if(clazz.equals("android.widget.AutoCompleteTextView")){
                Map<String, String> autoText = new HashMap<>();
                autoText.put(resourceKey, edit.getResourceName());
                autoText.put(clazzKey, clazz);
                autoText.put(textKey, edit.getText());
                autoText.put(descKey, edit.getContentDescription());
                maySavedEdits.add(autoText);
            }else if(clazz.equals("android.widget.Spinner")){
                Map<String, String> spinner = new HashMap<>();
                spinner.put(resourceKey,edit.getResourceName());
                spinner.put(clazzKey,clazz);
                UiObject2 spinnerText = edit.getChildren().get(0);//get the Text Widget(TextView) of spinner
                spinner.put(textKey,spinnerText.getText());
                spinner.put(descKey,edit.getContentDescription());
                maySavedEdits.add(spinner);
            }else{
                Map<String, String> compoundBtn = new HashMap<>();
                compoundBtn.put(resourceKey,edit.getResourceName());
                compoundBtn.put(clazzKey,clazz);
                compoundBtn.put(checkedKey,Boolean.toString(edit.isChecked()));
                compoundBtn.put(descKey,edit.getContentDescription());
                maySavedEdits.add(compoundBtn);
            }
        }
        return maySavedEdits;
    }

    private void initEdits(){
        String regex = "android\\.widget\\." + "((EditText)|(CheckBox)|(RadioButton)|(Switch)|(Spinner)|(CheckedTextView)|(AutoCompleteTextView))";
        Pattern p = Pattern.compile(regex);
        List<UiObject2> edits = mUidevice.findObjects(By.clazz(p));
        for(UiObject2 edit : edits){
            String clazz = edit.getClassName();
            if(clazz.equals("android.widget.EditText")){
                UiObject2 parent = edit.getParent();
                if(!parent.getClassName().equals("android.widget.NumberPicker")){
                    edit.setText(fText);
                }
            }else if(clazz.equals("android.widget.AutoCompleteTextView")){
                edit.setText(fText);
            }else if(clazz.equals("android.widget.Spinner")){
                executeEvent(edit,"click");
                UiObject2 spinnerList = mUidevice.findObject(By.clazz("android.widget.ListView"));
                List<UiObject2> spinnerItems = spinnerList.getChildren();
                executeEvent(spinnerItems.get(spinnerItems.size() - 1),"click");
            }else if(clazz.equals("android.widget.CheckedTextView")){
                continue;
            }else{
                executeEvent(edit,"click");
            }
        }
    }

    private List<Map<String,String>> unionSavedEdits(List<Map<String,String>> saved1, List<Map<String,String>> saved2){
        saved1.removeAll(saved2);
        saved1.addAll(saved2);
        return saved1;
    }

    private List<Map<String,String>> widgetsHasId(List<Map<String,String>> widgets){
        List<Map<String,String>> id_widgets = new ArrayList<>();
        for(Map<String,String> widget : widgets){
            String resourceId = widget.get("resource_id");
            if(resourceId != null)
                id_widgets.add(widget);
        }
        return id_widgets;
    }

    private List<Map<String,String>> getFinalSavedEdits(List<Map<String,String>> maySavedEdits){
        List<Map<String, String>> finalSavedEdits = new ArrayList<>();
        for(Map<String,String> map : maySavedEdits){
            String resourceId = map.get("resource_id");
            String clazz = map.get("clazz");
            UiObject2 afterOpt = mUidevice.findObject(By.res(resourceId));//the same widget after kill, back or rotate
            if(afterOpt != null){
                if(clazz.equals("android.widget.EditText") || clazz.equals("android.widget.AutoCompleteTextView")){
                    String text = map.get("text");
                    String afterText = afterOpt.getText();
                    if(!isSameText(text,afterText))
                        finalSavedEdits.add(map);
                }else if(clazz.equals("android.widget.Spinner")){
                    String text = map.get("text");
                    String afterText = afterOpt.getChildren().get(0).getText();
                    if(!isSameText(text,afterText))
                        finalSavedEdits.add(map);
                }else{
                    String checked = map.get("checked");
                    String afterChecked = Boolean.toString(afterOpt.isChecked());
                    if(!checked.equals(afterChecked))
                        finalSavedEdits.add(map);
                }
            }else{
                finalSavedEdits.add(map);
            }
        }
        return finalSavedEdits;
    }

    private boolean isSameText(String str1, String str2){
        if((str1 != null) && (str2 != null))
            return str1.equals(str2);
        else if((str1 == null) && (str2 == null))
            return true;
        return false;
    }


//    public boolean containsWidget(UiObject2 obj,String curACT){
//        String text = obj.getText();
//        String resourceId = obj.getResourceName();
//        String contentDesc = obj.getContentDescription();
//        String classType = obj.getClassName();
//        Rect visBounds = obj.getVisibleBounds();
//        String hostACT = curACT;
//        UIElement uiElement = new UIElement(text,resourceId,contentDesc,classType,visBounds,hostACT);
//        return visitMap.containsKey(uiElement);
//    }

    public void printDynamicGraph(DynamicGraph g){
        List<DynamicEdge> edges = g.getEdges();
        for(DynamicEdge e : edges){
            DynamicWindow source = e.getSource();
            DynamicWindow target = e.getTarget();
            Log.d("DynamicGraph Info", "source: " + source.getName() + "\t" + source.getType());
            Log.d("DynamicGraph Info", "target: " + target.getName() + "\t" + target.getType());
            Log.d("DynamicGraph Info", "widget: " + (e.getWidget()==null));
            Log.d("DynamicGraph Info", "-----------------------------------------------------");
        }
    }

    public void printDynamicEdge(DynamicEdge e){
        DynamicWindow source = e.getSource();
        DynamicWindow target = e.getTarget();
        Log.d("DynamicEdge Info","source: "+source.getName() + "\t" + source.getType());
        Log.d("DynamicEdge Info", "target: " + target.getName() + "\t" + target.getType());
        Log.d("DynamicEdge Info", "widget: " + (e.getWidget()==null));
        Log.d("DynamicEdge Info", "*****************************************************");
    }

    public void updateWindow(Set<DynamicWindow> winSet,DynamicWindow oldWindow, DynamicWindow newWindow){
        oldWindow = containsWindow(winSet, newWindow.getWinHierarchy());
        newWindow = new DynamicWindow();
        newWindow.setLabel(windowLabel);
        newWindow.setType(WindowType.ACT);
    }

    public void filterElements(List<UiObject2> elements){
        Iterator<UiObject2> iterator = elements.iterator();
        while(iterator.hasNext()){
            UiObject2 element = iterator.next();
            if(isEditableElement(element) || isChildOfNum(element) || parentPicker(element) /*|| invalidElement(element)*/){
                if(isEditText(element)){
                    fill_data(element);
                }
                iterator.remove();
            }
        }
    }

    private boolean parentPicker(UiObject2 element){
        UiObject2 parent = element;
        while(parent != null){
            String clazz = parent.getClassName();
            if(clazz.equals("android.widget.DatePicker") || clazz.equals("android.widget.TimePicker"))
                return true;
            parent = parent.getParent();
        }
        return false;
    }

    private boolean invalidElement(UiObject2 element){
        String id = element.getResourceName();
        String desc = element.getContentDescription();
        if(id != null){
            if(id.equals("RelativeLayout1"))
                return true;
        }
        if(desc != null){
            if(desc.equals("Templates：向上导航")){
                return true;
            }
        }
        return false;
    }

    public boolean isEditableElement(UiObject2 element){
        String classType = element.getClassName();
        if(classType.equals("android.widget.Spinner") || classType.equals("android.widget.EditText") ||
                classType.equals("android.widget.CheckBox") || classType.equals("android.widget.CheckedTextView") ||
        classType.equals("android.widget.RadioButton") || classType.equals("android.widget.Switch") || classType.equals("android.widget.RatingBar") ||
        classType.equals("android.widget.AutoCompleteTextView")){
            return true;
        }
        return false;
    }

    public boolean isChildOfNum(UiObject2 element){
        UiObject2 parent = element.getParent();
        if(parent != null){
            String p_classType = parent.getClassName();
            if(p_classType.equals("android.widget.NumberPicker")){
                return true;
            }
        }
        return false;
    }

    public String getTranType(DynamicWindow source){
        String sourceType = source.getType();
        if(sourceType.equals(WindowType.OPTMENU) || sourceType.equals(WindowType.CTXMENU)){
            return TransitionType.MENU_TRAN;
        }else if(sourceType.equals(WindowType.DIALOG)){
            return TransitionType.DIALOG_TRAN;
        }
        return TransitionType.ACT_TRAN;
    }

    private String getPhraseType(String phrase){
        initPython();
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("nlp").callAttr("analysePhrase",phrase);
        String type = pyObject.toString();
        return type;
    }

    public void dumpWindow(){
        File file = new File(hierarchyPath);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
            mUidevice.dumpWindowHierarchy(file);
            Thread.sleep(1500);
                }catch(IOException e){
            e.printStackTrace();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    public void dumpWindow(String filePath){
        File file = new File(filePath);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
            mUidevice.dumpWindowHierarchy(file);
            Thread.sleep(1500);
        }catch(IOException e){
            e.printStackTrace();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    public DynamicWindow containsWindow(Set<DynamicWindow> winSet, Document doc){
        for(DynamicWindow win : winSet){
            if(sameHierarchy(win.getWinHierarchy(),doc)){
                return win;
            }
        }
        return null;
    }

    public boolean containsEdge(List<DynamicEdge> edgeList, DynamicWindow cur, DynamicWindow next,DynamicWidget w){
        if(w == null){
            for(DynamicEdge edge : edgeList){
                DynamicWindow src = edge.getSource();
                DynamicWindow tgt = edge.getTarget();
                if(sameHierarchy(src.getWinHierarchy(),cur.getWinHierarchy()) && sameHierarchy(tgt.getWinHierarchy(),
                        next.getWinHierarchy())){
                    return true;
                }
            }
            return false;
        }else{
            for(DynamicEdge edge : edgeList){
                DynamicWindow src = edge.getSource();
                DynamicWindow tgt = edge.getTarget();
                DynamicWidget widget = edge.getWidget();
                if(sameHierarchy(src.getWinHierarchy(),cur.getWinHierarchy()) && sameHierarchy(tgt.getWinHierarchy(),
                        next.getWinHierarchy()) && w.equals(widget)){
                    return true;
                }
            }
            return false;
        }
    }

//    public Document getWinHierarchyfromFile(String path){
//        File file = new File(path);
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        Document doc = null;
//        try {
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            doc = builder.parse(file);
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return doc;
//    }

    private Document getWinHierarchyfromFile(String path){
        File file = new File(path);
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return doc;
    }

    private Document removeSysUiNode(Document doc){
        Element root = doc.getRootElement();
        List<Element> elements = root.elements("node");
        for(Element e : elements){
            Attribute attr = e.attribute("package");
            String value = attr.getValue();
            if(!value.equals(mPackage)){//value.equals("com.android.systemui"
                e.getParent().remove(e);
            }
        }
        return doc;
    }

    private String getXMLText(Document doc){
        return removeSysUiNode(doc).asXML();
    }

    private String removeBlankLine(String str){
        String dest = "";
        if(str != null){
            Pattern p = Pattern.compile("\n\\s*\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("\n");
        }
        return dest;
    }

    private boolean sameHierarchy(Document doc1, Document doc2){
        List<Element> elements1 = getAllNodeElements(doc1.getRootElement());
        List<Element> elements2 = getAllNodeElements(doc2.getRootElement());
        int len1 = elements1.size();
        int len2 = elements2.size();
        if(len1 == len2){
            for(int i=0 ; i< len1; i++){
                Element doc1Node = elements1.get(i);
                Element doc2Node = elements2.get(i);
                List<String> comparedAttr1 = getComparedAttributes(doc1Node);
                List<String> comparedAttr2 = getComparedAttributes(doc2Node);
                if(!comparedAttr1.equals(comparedAttr2)){
                    return false;
                }
            }
            return true;
        }
        return false;
//        String xml1 = removeBlankLine(doc1.asXML());
//        String xml2 = removeBlankLine(doc2.asXML());
//        return xml1.equals(xml2);
    }

    private List<Element> getAllNodeElements(Element root){
        List<Element> allNodes = new ArrayList<>();
        allNodes.addAll(root.elements("node"));
        for(Element e : root.elements("node"))
            allNodes.addAll(getAllNodeElements(e));
        return allNodes;
    }

    private List<String> getComparedAttributes(Element element){
        List<String> attributes = new ArrayList<>();
        //attributes.add(element.getAttribute("text"));
        attributes.add(element.attribute("resource-id").getValue());
        attributes.add(element.attribute("class").getValue());
        attributes.add(element.attribute("package").getValue());
        attributes.add(element.attribute("content-desc").getValue());
        attributes.add(element.attribute("checkable").getValue());
        attributes.add(element.attribute("clickable").getValue());
        //attributes.add(element.attribute("enabled").getValue());
        //attributes.add(element.attribute("focusable").getValue());
        attributes.add(element.attribute("scrollable").getValue());
        attributes.add(element.attribute("long-clickable").getValue());
        //attributes.add(element.attribute("bounds").getValue());
        return attributes;
    }

    public void printHierarchy(String fileName, Document doc){
        List<Element> nodeList = doc.getRootElement().elements("node");
        for(int i = 0;i < nodeList.size();i++){
            Element node = nodeList.get(i);
            List<Attribute> attributes = node.attributes();
            for(int j = 0;j < attributes.size();j++){
                Attribute attr = attributes.get(j);
                String name = attr.getName();
                String value = attr.getValue();
                String printStr = name + ": " + value;
                FileUtil.filePrintln(fileName,printStr);
            }
            FileUtil.filePrintln(fileName,"*******************************************************");
        }
    }

    public void saveExecuteWidget(UiObject2 w_d){
        if(!isMenu() && !isDialog()){
            String text = w_d.getText();
            String resId = w_d.getResourceName();
            String contentDesc = w_d.getContentDescription();
            String classType = w_d.getClassName();
            Rect visBounds = w_d.getVisibleBounds();
            String hostACT = getRunningActivity();
            UIElement element = new UIElement(text,resId,contentDesc,classType,visBounds,hostACT);
            VisitInfo info = new VisitInfo();
            Integer frequency = info.getFrequency() + 1;
            info.setFrequency(frequency);
            visitMap.put(element,info);
        }
    }

    public void gotoMainPage() throws InterruptedException, IOException {
        //click back arrow in the page

        Rect rootRec = getRootRect();
        Rect refRct = new Rect(0, 0, width, height);
        if (getRunningActivity().equals(mainActivity) && !rootRec.equals(refRct)) {
            mUidevice.pressBack();
            Thread.sleep(waitTime);
            return;
        }
        while (!getRunningActivity().equals(mainActivity)) {
            mUidevice.pressBack();
            Thread.sleep(waitTime);
            if (isDialog()) {
                dialogBtn.click();
                Thread.sleep(waitTime);
            }
        }
    }

    private String getRunningActivity() {
        String cmd = "dumpsys activity activities";
        String result = null;
        try {
            result = mUidevice.executeShellCommand(cmd);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("run adb shell dumpsys activity error", e.getMessage());
        }
        String pattern = "mFocusedActivity: ActivityRecord.*\n";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(result);
        if (m.find()) {
            String group = m.group(0);
            String[] strs = group.split(" ");
            String currentActivity = strs[strs.length - 2];
            return currentActivity.replace("/", "");
//            String[] strs = group.split("/");
//            String currentActivity = strs[strs.length - 1].split(" ")[0];
//            return mPackage + currentActivity;
            //return currentActivity.substring(1);//remove "."
        }
        return "PseudoActivity";
    }

    private String getRunningActivityRaw(){
        String cmd = "dumpsys activity activities";
        String result = null;
        try {
            result = mUidevice.executeShellCommand(cmd);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("run adb shell dumpsys activity error", e.getMessage());
        }
        String pattern = "mFocusedActivity: ActivityRecord.*\n";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(result);
        if (m.find()) {
            String group = m.group(0);
            String[] strs = group.split(" ");
            String currentActivity = strs[strs.length - 2];
            return currentActivity;
//            String[] strs = group.split("/");
//            String currentActivity = strs[strs.length - 1].split(" ")[0];
//            return mPackage + currentActivity;
            //return currentActivity.substring(1);//remove "."
        }
        return "PseudoActivity";
    }

    public boolean isDialog() {
//        try {
//            Thread.sleep(800);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        boolean isDialog = false;
        UiObject2 root = getRootElement();
        Rect visRect = root.getVisibleBounds();
//        int visTop = visRect.top;
//        int visBottom = visRect.bottom;
//        Log.d("top-----bottom", visTop + "------" + visBottom);
//        Log.d("left-----right", visRect.left + "-----" + visRect.right);
//        if (visTop >= height / 4 && visTop <= height / 2 && visBottom >= height / 2 &&
//                visBottom <= height / 4 * 3) {
//            isDialog = true;
//        }
        int curWidth = visRect.right - visRect.left;
        int curHeight = visRect.bottom - visRect.top;
        if((curWidth >= width / 4 && curWidth <= width / 4 * 3) || (curHeight >= height / 4 &&
                curHeight <= height / 4 * 3)){
            isDialog = true;
        }
        Log.d("isDialog", isDialog + "");
        return isDialog;
    }

    public void addUIElement(String act, UiObject2 element) {
        if (visitedElements.containsKey(act)) {
            Set<Rect> eleSet = visitedElements.get(act);
            eleSet.add(element.getVisibleBounds());
        } else {
            Set<Rect> eleSet = new HashSet<>();
            eleSet.add(element.getVisibleBounds());
            visitedElements.put(act, eleSet);
        }
    }

    public boolean containUIElement(String act, UiObject2 element) {
        if (!visitedElements.containsKey(act)) {
            return false;
        }
        //String eleType = element.getClassName();
        Rect eleRect = element.getVisibleBounds();
        Set<Rect> eleSet = visitedElements.get(act);
        if (eleSet.contains(eleRect)) {
            return true;
        }
        return false;
    }

    public void executeEvent(UiObject2 element, String event) {
        //clearLog();
        if(element.getClassName().equals("android.widget.EditText")){
//            element.click();
//            element.setText(fText);
            fill_data(element);
        }else{
            if (event.equals("click") || event.equals("item_click")) {
                element.click();
            } else if (event.equals("long_click") || event.equals("item_long_click")) {
                element.click(clickDuration);
            }else if(event.equals("scroll")){
                element.scroll(Direction.UP,0.6f,500);
                try{
                    Thread.sleep(waitTime);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                element.scroll(Direction.DOWN,0.6f,500);
            }
        }
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        String log_info = checkCrashed();
//        if(log_info.contains("--------- beginning of crash") && log_info.contains("Process: " + mPackage)){
//            Log.e("crash info",log_info);
//            restartApp(mPackage,mainActivity);
//        }
    }

    public void openMenu() {
        mUidevice.pressMenu();
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public UiObject2 getRootElement() {
        List<UiObject2> frameLayouts = mUidevice.findObjects(By.clazz("android.widget.FrameLayout").depth(0));
        UiObject2 root = frameLayouts.get(0);
        for (UiObject2 f : frameLayouts) {
            if (getElementArea(f) > getElementArea(root)) {
                root = f;
            }
        }
        return root;
    }

    public int getElementArea(UiObject2 obj) {
        try {
            Rect rect = obj.getVisibleBounds();
            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;
            int area = width * height;
            return area;

        } catch (Exception e) {
            Log.d("StaleObjectException", "element doesn't exist");
            return 0;
        }
//        Rect rect = obj.getVisibleBounds();
//        int width = rect.right - rect.left;
//        int height = rect.bottom - rect.top;
//        int area = width * height;
//        return area;
    }

    public Rect getRootRect() {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UiObject2 root = getRootElement();
        Rect rRect = root.getVisibleBounds();
        Log.d("root location-left top right bottom", rRect.left + " " + rRect.top + " " + rRect.right
                + " " + rRect.bottom);
        return rRect;
    }

    public void dynamicExplore() throws IOException, InterruptedException {
        String curAct = getRunningActivity();
        visitedACT.add(curAct);
        if (length == 0) {
            List<UiObject2> clickableElements = mUidevice.findObjects(By.clickable(true));
            for (UiObject2 cele : clickableElements) {
                if (!containUIElement(curAct, cele) && !isBack(cele) && !isEditText(cele)) {
                    addUIElement(curAct, cele);
                    executeEvent(cele, "click");
                    visitedACT.add(getRunningActivity());
                    length++;
                    break;
                }
            }
        }
        Rect refRect = new Rect(0, 0, width, height);
        List<UiObject2> clickableElements = mUidevice.findObjects(By.clickable(true));
        boolean flag1 = visitedACT.contains(getRunningActivity()) && refRect.equals(getRootRect());
        boolean flag2 = clickableElements.isEmpty();
        boolean endFlag = flag1 || flag2 || containsWebView() || gotoAnotherApp();
        //String currentACT = "current";
        //String nextACT = "next";
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        while (!endFlag && length != 0 && (endTime - startTime <= scenarioTimeout)) {
            //currentACT = getRunningActivity();
            for (UiObject2 cele : clickableElements) {
                if (!containUIElement(getRunningActivity(), cele) && !isBack(cele) && !isEditText(cele)) {
                    addUIElement(getRunningActivity(), cele);
                    executeEvent(cele, "click");
                    visitedACT.add(getRunningActivity());
                    length++;
                    break;
                }
            }
            clickableElements = mUidevice.findObjects(By.clickable(true));
            flag1 = visitedACT.contains(getRunningActivity()) && refRect.equals(getRootRect());
            flag2 = clickableElements.isEmpty();
            endFlag = flag1 || flag2 || containsWebView() || gotoAnotherApp();
            //nextACT = getRunningActivity();
            endTime = System.currentTimeMillis();
        }
        gotoMainPage();
        length = 0;
    }

    public boolean isBack(UiObject2 uiObject2) {
        Rect rect = uiObject2.getVisibleBounds();
        String type = uiObject2.getClassName();
        if (rect.top >= 0 && rect.top <= height / 20 && rect.left >= 0 &&
                rect.left <= width / 25 && rect.right >= width / 20 && rect.right <= width / 5 * 3 &&
                rect.bottom >= height / 20 && rect.bottom <= height / 5 && (type.equals("android.widget.ImageButton")
                || type.equals("android.widget.ImageView") || type.equals("android.widget.LinearLayout"))) {
            return true;
        }
        return false;
    }

    public boolean isEditText(UiObject2 uiObject2) {
        String type = uiObject2.getClassName();
        if (type.equals("android.widget.EditText")) {
            return true;
        }
        return false;
    }

    public boolean containsWebView() {
        return mUidevice.hasObject(By.clazz("android.webkit.WebView"));
    }

    public boolean gotoAnotherApp() {
        String curPackage = mUidevice.getCurrentPackageName();
        Log.d("Test App Package", mPackage);
        if(curPackage != null){
            Log.d("Current App Package", curPackage);
        }else{
            Log.d("Current App Package", "null");
        }
        if (mPackage.equals(curPackage)) {
            Log.d("go to another app", "no");
            return false;
        }
        Log.d("go to another app", "yes");
        return true;
    }

    public void startApp(String pack, String act) {
        String start_cmd = "am start -W -n ";
        start_cmd += pack;
        start_cmd += "/.";
        start_cmd += act;
        try {
            mUidevice.executeShellCommand(start_cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startApp(String rawActivity){
        String start_cmd = "am start -W -n ";
        start_cmd += rawActivity;
        try {
            mUidevice.executeShellCommand(start_cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void startApp(String pack) {
//        Context context = mInstrumentation.getContext();
//        Intent intent = context.getPackageManager().getLaunchIntentForPackage(pack);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        context.startActivity(intent);
//    }

    public void stopApp(String pack) {
        String stop_cmd = "am force-stop ";
        stop_cmd += pack;
        try {
            mUidevice.executeShellCommand(stop_cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void restartApp(String pack) {
//        String curPack = mUidevice.getCurrentPackageName();
//        stopApp(curPack);
//        startApp(pack);
//    }

    public void restartApp(String pack, String act) {
//        String curPack = mUidevice.getCurrentPackageName();
//        stopApp(curPack);
//        startApp(pack, act);
        String start_cmd = "am start -S -n ";
        start_cmd += pack;
        start_cmd += "/.";
        start_cmd += act;
        try {
            mUidevice.executeShellCommand(start_cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restartApp(String rawActivity){
        String start_cmd = "am start -S -n ";
        start_cmd += rawActivity;
        try {
            mUidevice.executeShellCommand(start_cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String checkCrashed() {
        String logcat_filter = "AndroidRuntime:E CrashAnrDetector:D ActivityManager:E SQLiteDatabase:E WindowManager:E ActivityThread:E Parcel:E *:F *:S";
        String crash_cmd = "logcat -d " + logcat_filter;
        try {
            String crash_info = mUidevice.executeShellCommand(crash_cmd);
            return crash_info;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearLog() {
        String clear_cmd = "logcat -c";
        try {
            mUidevice.executeShellCommand(clear_cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResourceId(UiObject2 obj) {
        String res = obj.getResourceName();
        if (res == null) {
            return null;
        }
        return res.split("/")[1];
    }

    private void back() {
        String path = "/data/data/com.fdu.uiautomatortest/backComp.xml";
        dumpWindow(path);
        Document backBefore = removeSysUiNode(getWinHierarchyfromFile(path));
        mUidevice.pressBack();

        dumpWindow(path);
        Document backAfter = removeSysUiNode(getWinHierarchyfromFile(path));
        if(sameHierarchy(backBefore,backAfter)){
            mUidevice.pressBack();
        }
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void rotateLeft(){
        try {
            mUidevice.setOrientationLeft();
            Thread.sleep(rotateWait);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void rotateNatural(){
        try {
            mUidevice.setOrientationNatural();
            Thread.sleep(rotateWait);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void fill_data(UiObject2 obj) {
        String classType = obj.getClassName();
        if ("android.widget.EditText".equals(classType)) {
//            obj.click();
//            String oriText = obj.getText();
//            if(oriText != null){
//                obj.setText(oriText.split(" ")[0]);
//            }else{
//                obj.setText(fNum);
//                obj.setText(fText);
//                String afText = obj.getText();
//                if(afText == null){
//                    obj.setText(fNum);
//                }
//            }
//            if(isDialog()){
//                List<UiObject2> buttons = mUidevice.findObjects(By.clazz("android.widget.Button"));
//                boolean closeDialog = false;
//                for(UiObject2 button : buttons){
//                    if(StringUtil.isSubmitted(button.getText()) || StringUtil.isSubmitted(button.getContentDescription())){
//                        button.click();
//                        closeDialog = true;
//                        break;
//                    }
//                }
//                if(!closeDialog){
//                    back();
//                }
//            }else{
//                obj.setText(fNum);
//                String afText = obj.getText();
//                if(afText == null){
//                    obj.setText(fText);
//                }else{
//                    if(!fNum.equals(afText)){
//                        obj.setText(fText);
//                    }
//                }
//            }
            if(obj.getText() == null)
                obj.setText(fText);
//            Rect bounds = obj.getVisibleBounds();
//            List<UiObject2> editTexts = mUidevice.findObjects(By.clazz("android.widget.EditText"));
//            for(UiObject2 editText : editTexts){
//                if(bounds.equals(editText.getVisibleBounds())){
//                    String afText = editText.getText();
//                    if(afText == null){
//                        obj.setText(fText);
//                    }else{
//                        if(!fNum.equals(afText)){
//                            obj.setText(fText);
//                        }
//                    }
//                    break;
//                }
//            }
        }
    }

    public boolean isMenu() {
        boolean isMenu = false;
        UiObject2 root = getRootElement();
        Rect rootBounds = root.getVisibleBounds();
        int curWidth = rootBounds.right - rootBounds.left;
        int curHeight = rootBounds.bottom - rootBounds.top;
        Log.d("curWidth", curWidth + "");
        Log.d("curHeight", curHeight + "");
        boolean isOriSize = !((curHeight == height) && (curWidth == width));
        boolean hasListView = mUidevice.hasObject(By.clazz("android.widget.ListView"));
        boolean hasRecyclerView = mUidevice.hasObject(By.clazz("android.support.v7.widget.RecyclerView"));
        boolean hasList = hasListView || hasRecyclerView;
        if (isOriSize && hasList) {
            isMenu = true;
        }
        Log.d("isMenu", isMenu + "");
        return isMenu;
    }

    /**
     * find element that has min frequency
     *
     * @param elements
     * @return
     */
    public UiObject2 findOptimalElement(List<UiObject2> elements) {
        UiObject2 optElement = null;
        VisitInfo optInfo = null;
        int next = 0;//the initial index of optElement
        for (int i = 0; i < elements.size(); i++) {
            UiObject2 element = elements.get(i);
            String text = element.getText();
            String resourceId = element.getResourceName();
            String contentDesc = element.getContentDescription();
            String classType = element.getClassName();
            Rect visBounds = element.getVisibleBounds();
            String hostACT = getRunningActivity();
            UIElement uiElement = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
            VisitInfo vInfo = visitMap.get(uiElement);
            if (vInfo.getOpenType().intValue() == 0) {
                optInfo = vInfo;
                optElement = element;
                next = i + 1;
                break;
            }
        }
        for (int j = next; j < elements.size(); j++) {
            UiObject2 element = elements.get(j);
            String text = element.getText();
            String resourceId = element.getResourceName();
            String contentDesc = element.getContentDescription();
            String classType = element.getClassName();
            Rect visBounds = element.getVisibleBounds();
            String hostACT = getRunningActivity();
            UIElement uiElement = new UIElement(text, resourceId, contentDesc, classType, visBounds, hostACT);
            VisitInfo vInfo = visitMap.get(uiElement);
            if ((vInfo.getOpenType().intValue() == 0) && (vInfo.getFrequency() < optInfo.getFrequency())) {
                optInfo = vInfo;
                optElement = element;
            }
        }
        return optElement;
    }
}
