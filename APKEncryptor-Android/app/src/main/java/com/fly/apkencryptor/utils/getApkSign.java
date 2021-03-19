package com.fly.apkencryptor.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import maobyte.util.StreamUtil;
import maobyte.zip.ZipEntry;
import maobyte.zip.ZipFile;
import sun1.security.pkcs.PKCS7;

public class getApkSign {

    public static void main(String[] args) {

        String path = "E:\\MyData\\AndroidProjects\\FlyingReflect\\app\\build\\outputs\\apk\\debug\\app-debug.apk";//apk的路径

        String signatures = getSignatures(path);
        int sign=getSignCode(path);

        System.out.print("哈希签名："+signatures+"\n");
        System.out.print("数字签名："+sign);
        
    }




    public static int getSignCode(String path) {
        try {
            JarFile jarFile = new JarFile(path);
            byte[] readBuffer = new byte[8192];
            Certificate[] certs = null;
            Enumeration<?> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry je = (JarEntry) entries.nextElement();
                if (je.isDirectory()) {
                    continue;
                }
                if (je.getName().startsWith("META-INF/")) {
                    continue;
                }
                Certificate[] localCerts = loadCertificates(jarFile, je, readBuffer);
                if (certs == null) {
                    certs = localCerts;
                } else {
                    for (int i = 0; i < certs.length; i++) {
                        boolean found = false;
                        for (int j = 0; j < localCerts.length; j++) {
                            if (certs[i] != null && certs[i].equals(localCerts[j])) {
                                found = true;
                                break;
                            }
                        }
                        if (!found || certs.length != localCerts.length) {
                            jarFile.close();
                            return 0;
                        }
                    }
                }
            }
            return certs[0].hashCode();
        }catch (Exception e){
            return 0;
        }


    }



    public static String getSignatures(String path) {
        try {
            JarFile jarFile = new JarFile(path);
            byte[] readBuffer = new byte[8192];
            Certificate[] certs = null;
            Enumeration<?> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry je = (JarEntry) entries.nextElement();
                if (je.isDirectory()) {
                    continue;
                }
                if (je.getName().startsWith("META-INF/")) {
                    continue;
                }
                Certificate[] localCerts = loadCertificates(jarFile, je, readBuffer);
                if (certs == null) {
                    certs = localCerts;
                } else {
                    for (int i = 0; i < certs.length; i++) {
                        boolean found = false;
                        for (int j = 0; j < localCerts.length; j++) {
                            if (certs[i] != null && certs[i].equals(localCerts[j])) {
                                found = true;
                                break;
                            }
                        }
                        if (!found || certs.length != localCerts.length) {
                            jarFile.close();
                            return null;
                        }
                    }
                }
            }
            return new String(toCharsString(certs[0].getEncoded()));
        }catch (Exception e){
            return "";
        }


    }




    public static byte[] getApkSignatureData(String path) {

        try {

            ZipFile zipFile = new ZipFile(path);
            Enumeration<ZipEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipEntry ze = entries.nextElement();
                String name = ze.getName().toUpperCase();
                if (name.startsWith("META-INF/") && (name.endsWith(".RSA") || name.endsWith(".DSA"))) {
                    PKCS7 pkcs7 = new PKCS7(StreamUtil.readBytes(zipFile.getInputStream(ze)));
                    Certificate[] certs = pkcs7.getCertificates();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(baos);
                    dos.write(certs.length);
                    for (int i = 0; i < certs.length; i++) {
                        byte[] data = certs[i].getEncoded();
                        System.out.printf("  --SignatureHash[%d]: %08x\n", i, Arrays.hashCode(data));
                        dos.writeInt(data.length);
                        dos.write(data);
                    }
                    return baos.toByteArray();
                }
            }
            throw new Exception("META-INF/XXX.RSA (DSA) file not found.");

        }catch (Exception e){
            return null;
        }
    }





    public static String getApkSignature(String path) {
        try {
            return new String(getApkSignatureData(path),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }




    /**
     * 加载签名
     * 
     * @param jarFile
     * @param je
     * @param readBuffer
     * @return
     */
    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je,
            byte[] readBuffer) {
        try {
            InputStream is = jarFile.getInputStream(je);
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * 将签名转成转成可见字符串
     * 
     * @param sigBytes
     * @return
     */
    private static String toCharsString(byte[] sigBytes) {
        byte[] sig = sigBytes;
        final int N = sig.length;
        final int N2 = N * 2;
        char[] text = new char[N2];
        for (int j = 0; j < N; j++) {
            byte v = sig[j];
            int d = (v >> 4) & 0xf;
            text[j * 2] = (char) (d >= 10 ? ('a' + d - 10) : ('0' + d));
            d = v & 0xf;
            text[j * 2 + 1] = (char) (d >= 10 ? ('a' + d - 10) : ('0' + d));
        }
        return new String(text);
    }




}

