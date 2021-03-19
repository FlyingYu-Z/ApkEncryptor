package com.bigzhao.xml2axml.tools;

import android.content.Context;
import com.bigzhao.xml2axml.Encoder;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class AxmlEncoder {


    public static void encode(String inputPath, String outPutPath) throws Exception {

        Encoder e = new Encoder();
        byte[] bs = e.encodeFile(new Context(), inputPath);
        FileUtils.writeByteArrayToFile(new File(outPutPath), bs);

    }



    public static byte[] encode(String xml){
        try {
            Encoder encoder = new Encoder();
            return encoder.encodeString(null, xml);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static byte[] encode(File file){
        try {
            Encoder encoder = new Encoder();
            return encoder.encodeFile(null, file.getAbsolutePath());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }





}
