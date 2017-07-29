package com.edu.claf.Utils;

import android.os.Environment;

import com.edu.claf.bean.FoundInfo;
import com.edu.claf.bean.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {
    public static final String CACHE="cache";
    public static final String IMAGE = "image";
    public static final String ROOT="LostAndFound";
    public static final String ICON = "icon";
    private static File file;

    public static File  getDir(String str){
        StringBuilder path = new StringBuilder();
        if (isSdAvaiable()) {
            path.append(Environment.getExternalStorageDirectory().getAbsolutePath());
            path.append(File.separator);
            path.append(ROOT);
            path.append(File.separator);
            path.append(str);

        }else{
            File cacheDir = UIUtils.getContext().getCacheDir();
            path.append(cacheDir.getAbsolutePath());
            path.append(File.separator);
            path.append(str);
        }
        file = new File(path.toString());
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return  file;
    }

    private static boolean isSdAvaiable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File getCacheDir(User user){
        return  getDir(user.getMobilePhoneNumber()+File.separator+CACHE);
    }

    public static  File getIconDir(String phoneNumber){
        return  getDir(phoneNumber+File.separator+ICON);
    }

    public static File getImageDir(){
        return  getDir(IMAGE);
    }

    public static File getUserDir(User user){
        if (user!=null){
            return getDir(user.getMobilePhoneNumber());
        }
        return  null;
    }

    public static String readJsonData(String path){
        BufferedReader reader = null;
        String str = "";
        try {
            FileInputStream fis = new FileInputStream(path);
            InputStreamReader isReader = new InputStreamReader(fis);
            reader = new BufferedReader(isReader);
            String result = null;
            while((result = reader.readLine()) != null){
                str += result;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }
}
