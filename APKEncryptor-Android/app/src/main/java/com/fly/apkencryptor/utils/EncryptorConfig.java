package com.fly.apkencryptor.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.fly.apkencryptor.application.MyApp;
import com.fly.apkencryptor.widget.ToastUtils;
import com.google.common.collect.Lists;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.MemoryDataStore;
import org.json.JSONException;
import org.json.JSONObject;

public class EncryptorConfig {


	public static byte[] getDex() throws IOException {
		DexBuilder dexBuilder=new DexBuilder(Opcodes.getDefault());
		dexBuilder.setIgnoreMethodAndFieldError(true);

		InputStream in=getEntryInputStream("classes.dex");

		List<String> remove=new ArrayList<>();
		remove.add("com.test.MainActivity");

		DexBackedDexFile dex = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new ByteArrayInputStream(FileUtils.toByteArray(in)));

		List<DexBackedClassDef> classDefList= Lists.newArrayList(dex.getClasses());

		for(DexBackedClassDef classDef : classDefList){
			String type=classDef.getType();
			String pkg= DexUtils.getPkgNameByType(type);

			if(!remove.contains(pkg)){
				dexBuilder.internClassDef(classDef);
			}


		}



		MemoryDataStore memoryDataStore = new MemoryDataStore();
		dexBuilder.writeTo(memoryDataStore);

		return Arrays.copyOf(memoryDataStore.getBufferData(), memoryDataStore.getSize());
	}


	public static byte[] getX86SO() throws IOException {
		InputStream in=getEntryInputStream("lib/x86/libFlySub.so");
		return FileUtils.toByteArray(in);
	}

	public static byte[] getARMSO() throws IOException {
		InputStream in=getEntryInputStream("lib/armeabi-v7a/libFlySub.so");
		return FileUtils.toByteArray(in);
	}


	public static String getX86Entry(){
		return ("lib/x86/libFlySub.so");
	}

	public static String getARMEntry(){
		return ("lib/armeabi-v7a/libFlySub.so");
	}


	public static InputStream getEntryInputStream(String entry) throws IOException {
		File apk=new File(MyApp.getContext().getFilesDir()+File.separator+"sub.apk");
		ZipFile zipFile=new ZipFile(apk);

		ZipEntry zipEntry=zipFile.getEntry(entry);

		return zipFile.getInputStream(zipEntry);
	}


	public static class Conf{

		JSONObject jsonObject;
		public Conf() throws JSONException {
			jsonObject=new JSONObject(BYProtectUtils.readAssetsTxt("conf.json"));
		}

		public  String getSubApplicationName() throws JSONException {
			
			return jsonObject.getString("SubApplication");
		}
		
		
		
	}




}
