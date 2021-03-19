package com.beingyi.tasks;

import com.beingyi.encrypt.DexStringEncryptor;
import com.beingyi.encrypt.utils.FileUtils;
import org.antlr.runtime.RecognitionException;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.smali.Smali;
import org.jf.smali.SmaliOptions;

import java.io.File;

public class IntDecoderSmail {

    static final String IntDecoder_ClassType_src="Lcom/beingyi/encrypt/utils/IntDecoder;";
    public String IntDecoder_ClassType;
    ClassDef classDef;
    DexBuilder dexBuilder;
    public IntDecoderSmail() throws RecognitionException {
        this.dexBuilder=new DexBuilder(Opcodes.getDefault());
        this.dexBuilder.setIgnoreMethodAndFieldError(true);
        //this.IntDecoder_ClassType= DexStringEncryptor.getObfusedTypeName(IntDecoder_ClassType_src);
        this.IntDecoder_ClassType= IntDecoder_ClassType_src;
        this.classDef=Smali.assembleSmaliFile(FileUtils.readFile(new File("resource/IntDecoder.smali").getAbsolutePath()).replace(IntDecoder_ClassType_src,IntDecoder_ClassType), dexBuilder, new SmaliOptions());
    }

    public ClassDef getClassDef(){
        return this.classDef;
    }

}
