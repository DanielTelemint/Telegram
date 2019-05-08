package com.lunamint.wallet.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

public class FileUtil {

    private final static String txFileName = "tx_";

    public static final boolean isFileExist(String path, String fileName) {
        File file = new File(path, fileName);
        return file.exists();
    }

    public static final boolean setExecutable(File file) {
        file.setReadable(true, false);
        file.setWritable(true, false);
        if (!file.setExecutable(true, false)) {
            return false;
        }
        return true;
    }

    public static final boolean copy(Context context, String path, String fileName) {

        boolean isExist = false;

        AssetManager assetManager = context.getAssets();
        String[] files;
        try {
            files = assetManager.list("gaia");

            for (String file : files) {
                if (file.equals(fileName)) {
                    isExist = true;

                    InputStream in = null;
                    OutputStream out = null;
                    File outFile = null;
                    try {
                        in = assetManager.open("gaia/" + file);
                        outFile = new File(path, file);
                        out = new FileOutputStream(outFile);
                        writeFile(in, out);

                    } catch (IOException e) {
                        return false;
                    } finally {
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
                                if (!FileUtil.setExecutable(outFile)) return false;
                            } catch (IOException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }

        return isExist;
    }

    public static final void deleteFile(String path, String fileName) {
        File outFile = new File(path, fileName);

        if (outFile.exists()) {
            outFile.delete();
        }
    }

    public static final void deleteTx(Context context, long timeMillis) {

        String fileName = txFileName + timeMillis;

        File outFile = new File(context.getFilesDir(), fileName);

        if (outFile.exists()) {
            outFile.delete();
        }
    }

    public static final boolean writeTx(Context context, long timeMillis, String msg) {
        try {
            File gpxfile = new File(context.getFilesDir(), FileUtil.txFileName + timeMillis);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(msg);
            writer.flush();
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static final void writeFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static final String getTxFileName(long timeMillis) {
        return FileUtil.txFileName + timeMillis;
    }

    public static final String getChecksumMD5(String path) {
        String returnVal = "";
        try {
            InputStream input = new FileInputStream(path);
            byte[] buffer = new byte[1024];
            MessageDigest md5Hash = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = input.read(buffer);
                if (numRead > 0) {
                    md5Hash.update(buffer, 0, numRead);
                }
            }
            input.close();

            byte[] md5Bytes = md5Hash.digest();
            for (int i = 0; i < md5Bytes.length; i++) {
                returnVal += Integer.toString((md5Bytes[i] & 0xff) + 0x100, 16).substring(1);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return returnVal.toUpperCase();
    }
}
