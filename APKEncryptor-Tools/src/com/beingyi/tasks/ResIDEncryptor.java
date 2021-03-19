package com.beingyi.tasks;

import com.beingyi.encrypt.DexStringEncryptor;
import com.beingyi.encrypt.utils.FileUtils;
import com.beingyi.encrypt.utils.IntDecoder;
import com.beingyi.tools.utils.DexUtils;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.smali.Smali;
import org.jf.smali.SmaliOptions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResIDEncryptor {
    ClassDef classDef;
    DexBuilder dexBuilder;
    String Decode_Type;

    public ResIDEncryptor(ClassDef classDef,String Decode_Type) throws Exception {
        this.classDef=classDef;
        this.dexBuilder=new DexBuilder(Opcodes.getDefault());
        this.dexBuilder.setIgnoreMethodAndFieldError(true);
        this.Decode_Type=Decode_Type;
        start();

    }

    private void start()throws Exception{
        StringBuilder result=new StringBuilder();
        StringBuilder code=new StringBuilder();

        String smali= DexUtils.getSmali(classDef);
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(smali.getBytes())));
        String line="";

        while ((line = br.readLine()) != null) {
            Matcher m = Pattern.compile("(\\.field\\spublic\\sstatic\\sfinal\\s(.*):I)\\s=\\s(.*)").matcher(line);
            if (m.find()) {
                System.out.println(m.group());
                String head=m.group(1);
                String field=m.group(2);
                String value=m.group(3);


                result.append(head).append("\n");

                code.append("    const v0, "+ IntDecoder.encode(value)+"\n");
                code.append("    invoke-static {v0}, "+Decode_Type+"->decode(I)I"+"\n");
                code.append("    move-result-object v0"+"\n");
                code.append("    sput v0, "+classDef.getType()+"->"+field+":I"+"\n");

                System.out.println("head:"+head);
                System.out.println("field:"+field);
                System.out.println("value:"+value);

            }else {
                result.append(line).append("\n");
            }
        }


        result.append("\n");
        result.append(".method static constructor <clinit>()V").append("\n");
        result.append("    .registers 1").append("\n");
        result.append(code.toString()).append("\n");
        result.append("    return-void").append("\n");
        result.append(".end method").append("\n");

        result.append("\n");

        //System.err.println(result.toString());
        this.classDef=Smali.assembleSmaliFile(result.toString(),dexBuilder,new SmaliOptions());

    }

    public ClassDef getClassDef(){

        return this.classDef;
    }


}
