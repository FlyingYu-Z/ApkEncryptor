package com.fly.apkencryptor.utils;

import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;

import java.util.List;

public class DexBuilderUtil {



    public static void MergeDex (DexBuilder builder, List<DexBackedClassDef> ListDexClassDef, List<ClassDef> mClassDef, MergeDexCallback callback)
    {
        lable1: for ( int i=0;i < ListDexClassDef.size ();i++ )
        {
            DexBackedClassDef cl=ListDexClassDef.get (i);
            for ( ClassDef def:mClassDef )
            {
                if ( def.getType().equals (cl.getType ()))
                {
                    continue lable1;
                }
            }
            builder.internClassDef (cl);
            callback.onProgress (i);
        }
    }



    public interface MergeDexCallback
    {
        void onProgress(int progress);
    }




}
