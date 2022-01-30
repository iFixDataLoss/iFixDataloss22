package com.fdu.uiautomatortest.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    public static void filePrintln(String fileName , String content){
        try{
            String path = Environment.getExternalStorageDirectory().getPath() + "/GesdaUpgrade/" + fileName;
            //Log.d("filePath", path);
            File file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file,true);
            BufferedWriter out=new BufferedWriter(writer);
            out.write(content);
            out.newLine();
            out.flush();
            out.close();
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
