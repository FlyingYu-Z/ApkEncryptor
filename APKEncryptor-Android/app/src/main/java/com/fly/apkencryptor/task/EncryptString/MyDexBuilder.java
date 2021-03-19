package com.fly.apkencryptor.task.EncryptString;

import android.support.annotation.NonNull;

import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.writer.builder.DexBuilder;

public class MyDexBuilder extends DexBuilder {

    String tag="";

    public MyDexBuilder(@NonNull Opcodes opcodes) {
        super(opcodes);
    }

    public void setTag(String str){
        this.tag=str;
    }

    public String getTag(){
        return this.tag;
    }
}
