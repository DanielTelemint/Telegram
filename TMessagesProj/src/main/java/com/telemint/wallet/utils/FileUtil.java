package com.telemint.wallet.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

public class FileUtil {

    private final static String TAG = "FileUtil";

    public static boolean isFileExist(String path, String fileName){
        File file = new File(path, fileName);
        return file.exists();
    }

    public static boolean setExecutable(File file){
        file.setReadable(true, false);
        file.setWritable(true, false);
        if(!file.setExecutable(true, false)){
            return false;
        }
        return true;
    }

    public static boolean copy(Context context, String path, String fileName){

        boolean isExist = false;

        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("gaia");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
            return false;
        }

        for(String file : files) {
            if(file.equals(fileName)){

                Log.w(FileUtil.TAG, "setupGaia : "+file);

                isExist = true;

                InputStream in = null;
                OutputStream out = null;
                File outFile = null;
                try {
                    in = assetManager.open(file);
                    outFile = new File(path, file);
                    out = new FileOutputStream(outFile);
                    writeFile(in, out);

                } catch(IOException e) {
                    Log.e(FileUtil.TAG, "Failed to copy asset file: " + file, e);
                    return false;
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                            if(!FileUtil.setExecutable(outFile)) return false;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
            }
        }
        return isExist;
    }

    private static void writeFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static String getChecksumMD5(String path){
        String returnVal = "";
        try
        {
            InputStream   input   = new FileInputStream(path);
            byte[]        buffer  = new byte[1024];
            MessageDigest md5Hash = MessageDigest.getInstance("MD5");
            int           numRead = 0;
            while (numRead != -1)
            {
                numRead = input.read(buffer);
                if (numRead > 0)
                {
                    md5Hash.update(buffer, 0, numRead);
                }
            }
            input.close();

            byte [] md5Bytes = md5Hash.digest();
            for (int i=0; i < md5Bytes.length; i++)
            {
                returnVal += Integer.toString( ( md5Bytes[i] & 0xff ) + 0x100, 16).substring( 1 );
            }
        }
        catch(Throwable t) {t.printStackTrace();}
        return returnVal.toUpperCase();
    }
}
