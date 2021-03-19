package cn.beingyi.sub.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class CheckUtils {



    public static class VPN {


        public static void main(Context context) {
            if (isVpnUsed()||isWifiProxy(context)) {
                System.exit(0);
            }
        }


        /**
         * 是否使用代理(WiFi状态下的,避免被抓包)
         */
        public static boolean isWifiProxy(Context context){
            final boolean is_ics_or_later = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
            String proxyAddress;
            int proxyPort;
            if (is_ics_or_later) {
                proxyAddress = System.getProperty("http.proxyHost");
                String portstr = System.getProperty("http.proxyPort");
                proxyPort = Integer.parseInt((portstr != null ? portstr : "-1"));
                System.out.println(proxyAddress + "~");
                System.out.println("port = " + proxyPort);
            }else {
                proxyAddress = android.net.Proxy.getHost(context);
                proxyPort = android.net.Proxy.getPort(context);
                Log.e("address = ", proxyAddress + "~");
                Log.e("port = ", proxyPort + "~");
            }
            return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
        }
        /**
         * 是否正在使用VPN
         */
        public static boolean isVpnUsed() {
            try {
                Enumeration niList = NetworkInterface.getNetworkInterfaces();
                if(niList != null) {
                    for (Object obj : Collections.list(niList)) {
                        NetworkInterface intf=(NetworkInterface)obj;
                        if(!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                            continue;
                        }
                        Log.d("-----", "isVpnUsed() NetworkInterface Name: " + intf.getName());
                        if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())){
                            return true; // The VPN is up
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return false;
        }


    }


        public static class Root{


        public static void main(Context context) {
            if(isRoot()||checkSuFile()||haveRoot()){
                System.exit(0);
            }
        }


        private final static String TAG = "RootUtil";


        static boolean HaveRoot=false;

        /**
         *   判断机器Android是否已经root，即是否获取root权限
         */
        public static boolean haveRoot() {
            if (!HaveRoot) {
                int ret = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
                if (ret != -1) {
                    Log.i(TAG, "have root!");
                    HaveRoot = true;
                } else {
                    Log.i(TAG, "not root!");
                }
            } else {
                Log.i(TAG, "mHaveRoot = true, have root!");
            }
            return HaveRoot;
        }


        public static boolean isRoot() {
            String binPath = "/system/bin/su";
            String xBinPath = "/system/xbin/su";
            if (new File(binPath).exists() && isExecutable(binPath))
                return true;
            if (new File(xBinPath).exists() && isExecutable(xBinPath))
                return true;
            return false;
        }

        private static boolean isExecutable(String filePath) {
            java.lang.Process p = null;
            try {
                p = Runtime.getRuntime().exec("ls -l " + filePath);
                // 获取返回内容
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String str = in.readLine();
                Log.i(TAG, str);
                if (str != null && str.length() >= 4) {
                    char flag = str.charAt(3);
                    if (flag == 's' || flag == 'x')
                        return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (p != null) {
                    p.destroy();
                }
            }
            return false;
        }


        public static boolean checkSuFile() {
            java.lang.Process process = null;
            try {
                //   /system/xbin/which 或者  /system/bin/which
                process = Runtime.getRuntime().exec(new String[]{"which", "su"});
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                if (in.readLine() != null) return true;
                return false;
            } catch (Throwable t) {
                return false;
            } finally {
                if (process != null) process.destroy();
            }
        }

        public static File checkRootFile() {
            File file = null;
            String[] paths = {"/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                    "/system/bin/failsafe/su", "/data/local/su"};
            for (String path : paths) {
                file = new File(path);
                if (file.exists()) return file;
            }
            return file;
        }



        // 执行命令但不关注结果输出
        public static int execRootCmdSilent(String cmd)
        {
            int result = -1;
            DataOutputStream dos = null;
            try
            {  java.lang.Process p = Runtime.getRuntime().exec("su");
                dos = new DataOutputStream(p.getOutputStream());

                dos.writeBytes(cmd + "\n");
                dos.flush();
                dos.writeBytes("exit\n");
                dos.flush();
                p.waitFor();
                result = p.exitValue();
            }
            catch (Exception e)
            {
                e.printStackTrace();  }
            finally
            {
                if (dos != null)
                {
                    try
                    {  dos.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();  }  }  }
            return result;
        }



    }

    public static class Xposed {


        public static void main(Context context) {
            if(checkPackage(context)||checkJarClass()||checkJarFile()||checkException()||checkMaps()){
                System.exit(0);
            }
        }

        private static boolean checkMaps() {
            String jarName = "XposedBridge.jar";
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/" + Process.myPid() + "/maps"));
                while (true) {
                    String str = bufferedReader.readLine();
                    if (str == null) {
                        break;
                    }
                    if (str.endsWith("jar")) {
                        if (str.contains(jarName)) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        private static boolean checkException() {
            try {
                throw new Exception("xppp");
            } catch (Exception e) {
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    if (stackTraceElement.getClassName().equals("de.robv.android.xposed.XposedBridge")) {       // stackTraceElement.getMethodName()
                        return true;
                    }
                }
            }
            return false;
        }

        private static boolean checkJarFile() {
            File f = new File("/system/framework/XposedBridge.jar");
            if (f.exists()) {
                return true;
            } else {
                return false;
            }
        }

        private static boolean checkJarClass() {
            try {
                ClassLoader cl = ClassLoader.getSystemClassLoader();
                Class clazz = cl.loadClass("de.robv.android.xposed.XposedBridge");

                if (clazz != null) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        private static boolean checkPackage(Context ctx) {
            PackageManager packageManager = ctx.getPackageManager();
            List<ApplicationInfo> applicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo applicationInfo : applicationInfoList) {
                if (applicationInfo.packageName.equals("de.robv.android.xposed.installer")) {
                    return true;
                }
            }
            return false;
        }

        public static void disXposed() {
            try {
                Class loadClass = ClassLoader.getSystemClassLoader().loadClass("de.robv.android.xposed.XposedBridge");
                Field declaredField = loadClass.getDeclaredField("disableHooks");
                Field declaredField2 = loadClass.getDeclaredField("runtime");
                declaredField.setAccessible(true);
                declaredField2.setAccessible(true);
                declaredField.setBoolean(null, true);
                declaredField2.setInt(null, 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Object object = ClassLoader.getSystemClassLoader().loadClass("de.robv.android.xposed.XposedHelpers").newInstance();
                if (object != null) {
                    System.exit(0);
                }
                return;
            } catch (Exception e) {
            }
        }


    }


    public static class Virtual {

        public static void main(Context context) {

            try {
                if (isVirtual() || hasAdbInEmulator() || hasKnownDeviceId(context)) {
                    System.exit(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        /**
         * 根据部分特征参数设备信息来判断是否为模拟器
         *
         * @return true 为模拟器
         */
        public static boolean isVirtual() {
            return Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.toLowerCase().contains("vbox")
                    || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                    || "google_sdk".equals(Build.PRODUCT);
        }


        public static boolean hasAdbInEmulator() throws IOException {
            boolean adbInEmulator = false;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/net/tcp")), 1000);
                String line;
                // Skip column names
                reader.readLine();

                ArrayList<tcp> tcpList = new ArrayList<tcp>();

                while ((line = reader.readLine()) != null) {
                    tcpList.add(tcp.create(line.split("\\W+")));
                }

                reader.close();

                // Adb is always bounce to 0.0.0.0 - though the port can change
                // real devices should be != 127.0.0.1
                int adbPort = -1;
                for (tcp tcpItem : tcpList) {
                    if (tcpItem.localIp == 0) {
                        adbPort = tcpItem.localPort;
                        break;
                    }
                }

                if (adbPort != -1) {
                    for (tcp tcpItem : tcpList) {
                        if ((tcpItem.localIp != 0) && (tcpItem.localPort == adbPort)) {
                            adbInEmulator = true;
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                reader.close();
            }

            return adbInEmulator;
        }


        public static class tcp {

            public int id;
            public long localIp;
            public int localPort;
            public int remoteIp;
            public int remotePort;

            static tcp create(String[] params) {
                return new tcp(params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8],
                        params[9], params[10], params[11], params[12], params[13], params[14]);
            }

            public tcp(String id, String localIp, String localPort, String remoteIp, String remotePort, String state,
                       String tx_queue, String rx_queue, String tr, String tm_when, String retrnsmt, String uid,
                       String timeout, String inode) {
                this.id = Integer.parseInt(id, 16);
                this.localIp = Long.parseLong(localIp, 16);
                this.localPort = Integer.parseInt(localPort, 16);
            }
        }

        private static String[] known_device_ids = {"000000000000000", // Default emulator id
                "e21833235b6eef10", // VirusTotal id
                "012345678912345"};

        public static boolean hasKnownDeviceId(Context context) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission")
            String deviceId = telephonyManager.getDeviceId();

            for (String known_deviceId : known_device_ids) {
                if (known_deviceId.equalsIgnoreCase(deviceId)) {
                    return true;
                }

            }
            return false;
        }


    }


}
