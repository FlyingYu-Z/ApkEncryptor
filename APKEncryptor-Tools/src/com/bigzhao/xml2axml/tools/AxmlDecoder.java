package com.bigzhao.xml2axml.tools;

import com.bigzhao.xml2axml.AXMLPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class AxmlDecoder {


    public static void decode(String inputPath, String outPutPath) throws Exception {

        AXMLPrinter.out=new PrintStream(new File(outPutPath));
        AXMLPrinter.main(new String[]{inputPath});
        AXMLPrinter.out.close();

    }




    public static String decode(File file) throws IOException {
        return decode(FileUtils.readFileToByteArray(file));
    }


    public static String decode(byte[] data) {
        try(InputStream is=new ByteArrayInputStream(data)) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            AXMLPrinter.out = new PrintStream(os);
            AXMLPrinter.decode(is);
            byte[] bs = os.toByteArray();
            IOUtils.closeQuietly(os);
            AXMLPrinter.out.close();
            return new String(bs, "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


}
