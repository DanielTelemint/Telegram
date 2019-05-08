package com.lunamint.lunagram.secure;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.provider.Settings;
import android.util.Base64;

import com.lunamint.lunagram.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

public class Secure {

    private final static String DEBUG_SIGNATURE = "vx0O7E87XhRclmzJVsB2IVNUua0=";
    private final static String RELEASE_SIGNATURE = "5fIH753Ipe+a2XyDh8jvtit1LiE=";

    public static boolean isRooted() {

        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        try {
            String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                    "/system/bin/failsafe/su","/data/local/su", "/su/bin/su","/system/sbin/su","/system/xbin/su","/system/xbin/mu","/system/bin/.ext/.su","/system/usr/su-backup","/data/data/com.noshufou.android.su","/system/app/su.apk","/system/bin/.ext","/system/xbin/.ext"};
            for (String path : paths) {
                if (new File(path).exists()) {
                    return true;
                }
            }
        } catch (Exception e1) {
        }

        boolean canExecute = isRootAvailable();

        return canExecute;
    }

    public static boolean isAdbEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0) == 1;
    }

    public static boolean isValidSignature(Context context) {
        String correctSignature = BuildConfig.DEBUG ? DEBUG_SIGNATURE : RELEASE_SIGNATURE;
        String sig = "";
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : pi.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());
                sig = new String(Base64.encode(md.digest(), 0));
            }

            return sig.contains(correctSignature) ? true : false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isRootAvailable() {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(new String[]{"su"});
            writeCommandToConsole(p, "exit 0");
            int result = p.waitFor();
            if (result != 0)
                throw new Exception("err " + result);
            return true;
        } catch (IOException e) {
        } catch (Exception e) {
        } finally {
            if (p != null)
                p.destroy();
        }
        return false;
    }

    private static String writeCommandToConsole(Process proc, String command) throws Exception {
        byte[] tmpArray = new byte[1024];
        proc.getOutputStream().write((command + "\n").getBytes());
        proc.getOutputStream().flush();
        int bytesRead = 0;
        if (proc.getErrorStream().available() > 0) {
            if ((bytesRead = proc.getErrorStream().read(tmpArray)) > 1) {
                throw new Exception(new String(tmpArray, 0, bytesRead));

            }
        }
        if (proc.getInputStream().available() > 0) {
            bytesRead = proc.getInputStream().read(tmpArray);
        }
        return new String(tmpArray);
    }

    //Todo: need to implement version checking.
    public static boolean checkAppVersion() {
        if (true) {
            return true;
        } else {
            return false;
        }

    }
}
