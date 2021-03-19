package com.fly.apkencryptor.utils;

import maobyte.xml.decode.AXmlDecoder;
import maobyte.xml.decode.AXmlResourceParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ManifestParse {

    public static List<String> parseManifestActivity(InputStream inputStream) throws IOException {
        String str = (String) null;
        List<String> arrayList = new ArrayList();
        AXmlDecoder decode = AXmlDecoder.decode(inputStream);
        AXmlResourceParser aXmlResourceParser = new AXmlResourceParser();
        aXmlResourceParser.open(new ByteArrayInputStream(decode.getData()), decode.mTableStrings);
        while (true) {
            int next = aXmlResourceParser.next();
            if (next == 1) {
                return arrayList;
            }
            if (next == 2) {
                int attributeCount;
                if (aXmlResourceParser.getName().equals("manifest")) {
                    attributeCount = aXmlResourceParser.getAttributeCount();
                    for (next = 0; next < attributeCount; next++) {
                        if (aXmlResourceParser.getAttributeName(next).equals("package")) {
                            str = aXmlResourceParser.getAttributeValue(next);
                        }
                    }
                } else if (aXmlResourceParser.getName().equals("activity")) {
                    int attributeCount2 = aXmlResourceParser.getAttributeCount();
                    for (attributeCount = 0; attributeCount < attributeCount2; attributeCount++) {
                        if (aXmlResourceParser.getAttributeNameResource(attributeCount) == 16842755) {
                            String attributeValue = aXmlResourceParser.getAttributeValue(attributeCount);
                            if (attributeValue.startsWith(".")) {
                                attributeValue = new StringBuffer().append(str).append(attributeValue).toString();
                            }
                            arrayList.add(attributeValue);
                        }
                    }
                }
            }
        }
    }

    public static int readInt(byte[] bArr, int i) {
        return (((bArr[i + 3] << 24) | ((bArr[i + 2] & 255) << 16)) | ((bArr[i + 1] & 255) << 8)) | (bArr[i] & 255);
    }

    public static void writeInt(byte[] bArr, int i, int i2) {
        int i3 = i + 1;
        bArr[i] = (byte) (i2 & 255);
        int i4 = i3 + 1;
        bArr[i3] = (byte) ((i2 >>> 8) & 255);
        i3 = i4 + 1;
        bArr[i4] = (byte) ((i2 >>> 16) & 255);
        bArr[i3] = (byte) ((i2 >>> 24) & 255);
    }
}