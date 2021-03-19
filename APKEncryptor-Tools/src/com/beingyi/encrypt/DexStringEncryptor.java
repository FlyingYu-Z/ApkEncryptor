package com.beingyi.encrypt;

import com.beingyi.encrypt.utils.BES;
import com.beingyi.encrypt.utils.DexUtils;
import com.beingyi.encrypt.utils.FileUtils;
import com.beingyi.encrypt.utils.MD5;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.smali.Smali;
import org.jf.smali.SmaliOptions;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DexStringEncryptor {

    DexBuilder dexBuilder;
    List<String> ignoreList = new ArrayList<>();
    HashMap<String, String> StringPoolMap = new HashMap<>();
    int strCount = 0;


    static final String BYDecoder_ClassType_src="Lcom/beingyi/encrypt/BYDecoder;";
    static final String StringPool_ClassType_src="Lcom/beingyi/encrypt/StringPool;";
    static final String MethodPool_ClassType_src="Lcom/beingyi/encrypt/MethodPool;";

    static final String BYDecoder_DecodeMethod_src="#decode#";
    String BYDecoder_ClassType;
    String StringPool_ClassType;
    String MethodPool_ClassType;
    String BYDecoder_DecodeMethod;

    List<String> MethodPoolMap=new ArrayList<>();
    List<String> onlyEncryptClasses;


    private void init() {
        dexBuilder = new DexBuilder(Opcodes.getDefault());
        dexBuilder.setIgnoreMethodAndFieldError(true);

        this.BYDecoder_ClassType=getObfusedTypeName(BYDecoder_ClassType_src);
        this.StringPool_ClassType=getObfusedTypeName(StringPool_ClassType_src);
        this.MethodPool_ClassType=getObfusedTypeName(MethodPool_ClassType_src);

        this.BYDecoder_DecodeMethod=getRandomStr(10);


    }

    public DexStringEncryptor(byte[] in,List<String> onlyEncryptClasses) throws Exception {
        this.onlyEncryptClasses=onlyEncryptClasses;
        init();
        DexBackedDexFile dex = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new ByteArrayInputStream(in));

        List<ClassDef> classDefs = Lists.newArrayList(dex.getClasses());

        for (ClassDef classDef : classDefs) {
            String type = classDef.getType();
            String pkg = DexUtils.getPkgNameByType(type);

            if (pkg.equals("android.app.Application")) {
            }

            try {

                if(onlyEncryptClasses==null||onlyEncryptClasses.size()==0) {
                    dealClassDef(classDef);
                }else {
                    boolean encrypt=false;
                    for (String cla : onlyEncryptClasses) {
                        if (classDef.getType().startsWith(DexUtils.getTypeByPkg(cla))) {
                            encrypt = true;
                            break;
                        }
                    }
                    if (encrypt) {
                        dealClassDef(classDef);
                    } else {
                        dexBuilder.internClassDef(classDef);
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
                break;
            }


        }

        StringBuilder fieldContent=new StringBuilder();
        StringBuilder fieldValueContent=new StringBuilder();

        for(String key: StringPoolMap.keySet()){
            String field=key;
            String enStr= StringPoolMap.get(key);

            fieldContent.append("\n.field public static "+field+":Ljava/lang/String;\n");

            String input="const-string v0, \""+enStr+"\"\n" +
                    "    sput-object v0, Lcom/beingyi/encrypt/StringPool;->"+field+":Ljava/lang/String;";
            fieldValueContent.append("\n"+input+"\n");


        }

        String StringPoolSmali=FileUtils.readFile(new File("resource/StringPool.smali").getAbsolutePath());
        StringPoolSmali=StringPoolSmali.replace("#fields",fieldContent.toString());
        StringPoolSmali=StringPoolSmali.replace("#input",fieldValueContent.toString());
        StringPoolSmali=StringPoolSmali.replace(StringPool_ClassType_src,StringPool_ClassType);

        Smali.assembleSmaliFile(StringPoolSmali, dexBuilder, new SmaliOptions());
        Smali.assembleSmaliFile(FileUtils.readFile(new File("resource/BYDecoder.smali").getAbsolutePath()).replace(BYDecoder_ClassType_src,BYDecoder_ClassType).replace(BYDecoder_DecodeMethod_src,BYDecoder_DecodeMethod), dexBuilder, new SmaliOptions());


        String MethodPoolSmali=FileUtils.readFile(new File("resource/MethodPool.smali").getAbsolutePath());
        MethodPoolSmali=MethodPoolSmali.replace(MethodPool_ClassType_src,MethodPool_ClassType);
        for(String method:MethodPoolMap){
            MethodPoolSmali+=method;
        }

        Smali.assembleSmaliFile(MethodPoolSmali, dexBuilder, new SmaliOptions());


        System.out.println("finish!");
    }

    public byte[] getData() throws IOException {
        return DexUtils.getDexBuilderData(dexBuilder);
    }

    private void dealClassDef(ClassDef classDef) throws Exception {
        String smali = DexUtils.getSmali(classDef);
        smali = dealSmali(smali, classDef.getType());
        System.out.println(classDef.getType());
        Smali.assembleSmaliFile(smali, dexBuilder, new SmaliOptions());
    }

    private String dealSmali(String smali, String type) throws Exception {
        StringBuilder result = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(smali.getBytes())));
        String line = "";

        while ((line = br.readLine()) != null) {
            Matcher m = Pattern.compile("const-string ([vp]\\d{1,2}), \"(.*)\"").matcher(line);
            if (m.find()) {
                String tmp = m.group(2);
                if (tmp.equals("")) {
                    result.append(line).append("\n");
                    continue;
                }

                String register = m.group(1);
                tmp = StringEscapeUtils.unescapeJava(tmp);
                System.out.println(tmp);

                strCount++;

                String methodName= getRandomLitter(5)+getRandomStr(15)+ getRandomLitter(5);
                String pass= MD5.encode16(type);
                String fieldName= getRandomLitter(5)+getRandomStr(15)+ getRandomLitter(5);

                String enStr = BES.encode(tmp, pass);
                StringPoolMap.put(fieldName, enStr);

                String methodSmali=createMethod(methodName, BES.MD5.encode16(pass),fieldName);

                MethodPoolMap.add(methodSmali);

                String sign = "    const-string " + register + ", " + "\"\"";
                String dec = "";
                if (Integer.parseInt(register.substring(1)) > 15 && register.startsWith("v")) {
                    //dec = "    invoke-static/range {" + register + " .. " + register + "}, " + type + "->" + methodName + "(Ljava/lang/String;)Ljava/lang/String;";
                    dec = "    invoke-static {}, " + MethodPool_ClassType + "->" + methodName + "()Ljava/lang/String;";
                } else if (register.startsWith("v") || (register.startsWith("p") && Integer.parseInt(register.substring(1)) < 10)) {
                    dec = "    invoke-static {}, " + MethodPool_ClassType + "->" + methodName + "()Ljava/lang/String;";
                } else {
                    result.append(line).append("\n");
                    continue;
                }
                String mov = "    move-result-object " + register;
                result.append(sign).append("\n\n");
                result.append(dec).append("\n\n");
                result.append(mov).append("\n");
            } else {
                result.append(line).append("\n");
            }
        }
        br.close();

        return result.toString();
    }

    private String createMethod(String methodName,String pass,String fieldName){
        String smali= FileUtils.readFile(new File("resource/method.smali").getAbsolutePath());
        smali=smali.replaceFirst("#decode",methodName);
        smali=smali.replace("#pass",pass);
        smali=smali.replace("#field",fieldName);
        smali=smali.replace(BYDecoder_DecodeMethod_src,BYDecoder_DecodeMethod);
        smali=smali.replace(BYDecoder_ClassType_src,BYDecoder_ClassType);
        smali=smali.replace(MethodPool_ClassType_src,MethodPool_ClassType);
        smali=smali.replace(StringPool_ClassType_src,StringPool_ClassType);

        return smali;
    }


    private static String keys;

    static {
        try {
            keys = FileUtils.readFile(new File("resource/keys.txt").getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static String getObfusedTypeName(String type) {
        int length = 0;
        String[] types = type.split("/");
        if (types != null) {
            length = types.length;
        } else {
            length = 1;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("L");
        for (int i = 0; i < length; i++) {
            sb.append(getRandomStr(10) + "/");
        }
        sb.append(";");

        String result = sb.toString();


        return result.replace("/;", ";");
    }



    private static String getRandomStr(int length) {
        String[] keyArray = keys.split("\n");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        for (int i = 0; i < length; i++) {
            int num = r.nextInt(keyArray.length);
            sb.append(keyArray[num]);
        }
        return sb.toString().replace(".", "");
    }

    private static String getRandomLitter(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; ++i) {
            sb.append(str.charAt(random.nextInt(str.length())));
        }
        return sb.toString();
    }

}
