package com.fly.apkencryptor.utils;

import org.jf.baksmali.Adaptors.ClassDefinition;
import org.jf.baksmali.BaksmaliOptions;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.MemoryDataStore;
import org.jf.util.IndentingWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DexUtils {



    public static String getPkgNameByType(String type) {

        return type.replaceFirst("L", "").replace("/", ".").replace(";", "");
    }


    public static String getTypeByPkg(String pkg) {
        StringBuilder sb=new StringBuilder();
        sb.append("L");
        sb.append(pkg.replace(".","/"));
        sb.append(";");
        return sb.toString();
    }


    public static byte[] getDexBuilderData(DexBuilder dexBuilder) throws IOException {

        MemoryDataStore memoryDataStore = new MemoryDataStore();
        dexBuilder.writeTo(memoryDataStore);
        return Arrays.copyOf(memoryDataStore.getBufferData(), memoryDataStore.getSize());

    }


    public static String getSmali(ClassDef classDef) {
        String code = null;
        try {
            StringWriter stringWriter = new StringWriter();
            IndentingWriter writer = new IndentingWriter(stringWriter);
            ClassDefinition classDefinition = new ClassDefinition(new BaksmaliOptions(), classDef);
            classDefinition.writeTo(writer);
            writer.close();
            code = stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return code;
    }



}
