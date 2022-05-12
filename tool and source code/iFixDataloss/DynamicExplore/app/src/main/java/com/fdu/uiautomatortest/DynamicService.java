package com.fdu.uiautomatortest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

//import com.chaquo.python.PyObject;
//import com.chaquo.python.Python;
//import com.chaquo.python.android.AndroidPlatform;

public class DynamicService extends Service {
    private static DynamicService dynamicService;

    public DynamicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //initPython();
        dynamicService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

//    public void initPython(){
//        if (! Python.isStarted()) {
//            Python.start(new AndroidPlatform(this));
//        }
//    }

    public static DynamicService getDynamicService(){
        return dynamicService;
    }

}