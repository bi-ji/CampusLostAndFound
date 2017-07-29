package com.edu.claf.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    public static String encode(String password){
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(password.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b: digest) {
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2){
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            return  sb.toString();
        } catch (NoSuchAlgorithmException e) {
            
            e.printStackTrace();
        }
        return  null;
    }

 
    public static String getFileMD5(String sourceDir) {
        File file = new File(sourceDir);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len = -1;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            while ((len = fis.read(bytes))  != -1 ){
                md5.update(bytes,0,len);
            }
            byte[] result = md5.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b: result) {
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2){
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            return  sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
