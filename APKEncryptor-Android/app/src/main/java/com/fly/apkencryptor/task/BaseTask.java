package com.fly.apkencryptor.task;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import maobyte.xml.decode.AXmlDecoder;
import maobyte.xml.decode.XmlPullParser;
import net.fornwall.apksigner.CertCreator;
import net.fornwall.apksigner.KeyStoreFileManager;
import net.fornwall.apksigner.ZipSigner;

import com.android.apksig.ApkSigner;
import com.fly.apkencryptor.utils.EncryptorConfig;
import com.fly.apkencryptor.utils.FileUtils;
import com.fly.apkencryptor.utils.ManifestParse;
import com.fly.apkencryptor.utils.ThreadResult;
import com.fly.apkencryptor.utils.ZipOut;
import com.google.common.collect.ImmutableList;
import org.jf.baksmali.Adaptors.ClassDefinition;
import org.jf.baksmali.BaksmaliOptions;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.MemoryDataStore;
import org.jf.util.IndentingWriter;

import java.io.InputStream;
import java.security.cert.X509Certificate;


public class BaseTask {

	public String inputPath;
	public String outputPath;
	public ZipFile inputZipFile;
	public List<String> entries = new ArrayList<>();
	public List<String> dexEntries = new ArrayList<>();
	public List<String> resEntries = new ArrayList<>();
	public List<String> activities = new ArrayList<>();

	public boolean customApplication = false;
	public String customApplicationName = "android.app.Application";
	public String packageName;

	public ZipOut zipOut;
	public ThreadResult threadResult;

	public BaseTask(String inputPath, String outputPath) throws Exception {
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.inputZipFile = new ZipFile(inputPath);

		this.zipOut=new ZipOut(outputPath).setInput(inputZipFile);
		
		HashMap<String, byte[]> zipEnties = new HashMap<String, byte[]>();
		readZip(inputZipFile);

		/*
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread thread, Throwable e) {
						System.out.println("线程:" + thread.getName() + " 出现了异常：");
						e.printStackTrace();
					}
				});**/


		this.activities= ManifestParse.parseManifestActivity(getZipInputStream("AndroidManifest.xml"));
		initManifest(getZipInputStream("AndroidManifest.xml"));

	}


	public void addLib() throws Exception {
		List<String> libs=new ArrayList<>();
		libs.add("lib/armeabi-v7a/");
		libs.add("lib/x86/");
		libs.add("lib/x86_64/");
		libs.add("lib/arm64-v8a/");


		if(inputZipFile.getEntry("lib")==null){
			zipOut.addFile(EncryptorConfig.getARMEntry(), EncryptorConfig.getARMSO());
			zipOut.addFile(EncryptorConfig.getX86Entry(),EncryptorConfig.getX86SO());
		}else{

			for(String lib:libs){
				if(inputZipFile.getEntry(lib)!=null){
					zipOut.addFile(lib+"libFlySub.so", FileUtils.toByteArray(EncryptorConfig.getEntryInputStream(lib+"libFlySub.so")));
				}
			}



		}



	}


	public void setResultListener(ThreadResult threadResult){
		this.threadResult=threadResult;
	}


	public InputStream getZipInputStream(String entry) throws IOException {
		
		return new ByteArrayInputStream(FileUtils.toByteArray(inputZipFile.getInputStream(inputZipFile.getEntry(entry))));
	}

	private void initManifest(InputStream is) throws IOException {
		AXmlDecoder axml = AXmlDecoder.decode(is);
		maobyte.xml.decode.AXmlResourceParser parser = new maobyte.xml.decode.AXmlResourceParser();
		parser.open(new ByteArrayInputStream(axml.getData()), axml.mTableStrings);

		boolean success = false;
		int type;
		while ((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
			if (type != XmlPullParser.START_TAG)
				continue;
			if (parser.getName().equals("manifest")) {
				int size = parser.getAttributeCount();
				for (int i = 0; i < size; ++i) {
					if (parser.getAttributeName(i).equals("package")) {
						packageName = parser.getAttributeValue(i);
					}
				}
			} else if (parser.getName().equals("application")) {
				int size = parser.getAttributeCount();
				for (int i = 0; i < size; ++i) {
					if (parser.getAttributeNameResource(i) == 0x01010003) {
						customApplication = true;
						customApplicationName = parser.getAttributeValue(i);
						if (customApplicationName.startsWith(".")) {
							if (packageName == null)
								throw new NullPointerException("Package name is null.");
							customApplicationName = packageName + customApplicationName;
						}
						int index = axml.mTableStrings.getSize();
						byte[] data = axml.getData();
						int off = parser.currentAttributeStart + 20 * i;
						off += 8;
						ManifestParse.writeInt(data, off, index);
						off += 8;
						ManifestParse.writeInt(data, off, index);
					}
				}
				if (!customApplication) {
					int off = parser.currentAttributeStart;
					byte[] data = axml.getData();
					byte[] newData = new byte[data.length + 20];
					System.arraycopy(data, 0, newData, 0, off);
					System.arraycopy(data, off, newData, off + 20, data.length - off);

					// chunkSize
					int chunkSize = ManifestParse.readInt(newData, off - 32);
					ManifestParse.writeInt(newData, off - 32, chunkSize + 20);
					// attributeCount
					ManifestParse.writeInt(newData, off - 8, size + 1);

					int idIndex = parser.findResourceID(0x01010003);
					if (idIndex == -1)
						throw new IOException("idIndex == -1");

					boolean isMax = true;
					for (int i = 0; i < size; ++i) {
						int id = parser.getAttributeNameResource(i);
						if (id > 0x01010003) {
							isMax = false;
							if (i != 0) {
								System.arraycopy(newData, off + 20, newData, off, 20 * i);
								off += 20 * i;
							}
							break;
						}
					}
					if (isMax) {
						System.arraycopy(newData, off + 20, newData, off, 20 * size);
						off += 20 * size;
					}

					ManifestParse.writeInt(newData, off,
							axml.mTableStrings.find("http://schemas.android.com/apk/res/android"));
					ManifestParse.writeInt(newData, off + 4, idIndex);
					ManifestParse.writeInt(newData, off + 8, axml.mTableStrings.getSize());
					ManifestParse.writeInt(newData, off + 12, 0x03000008);
					ManifestParse.writeInt(newData, off + 16, axml.mTableStrings.getSize());
					axml.setData(newData);
				}
				success = true;
				break;
			}
		}


	}




	public static void createJKS() {

		String keystorePath = "E:\\MyData\\AndroidProjects\\test.jks";
		String alias = "test";
		char[] storePassword = "android".toCharArray();
		char[] keyPassword = "android".toCharArray();

		try {
			CertCreator.DistinguishedNameValues nameValues = new CertCreator.DistinguishedNameValues();
			nameValues.setCommonName("APKSigner");
			nameValues.setOrganization("Earth");
			nameValues.setOrganizationalUnit("Earth");
			CertCreator.createKeystoreAndKey(keystorePath, storePassword, "RSA", 2048, alias, keyPassword,
					"SHA1withRSA", 30, nameValues);
			System.out.println("证书创建成功");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public String getSmali(ClassDef classDef) {
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



	// 修改AndroidManifest Application Name属性
	public byte[] parseManifest(InputStream is, String Name) throws IOException {
		AXmlDecoder axml = AXmlDecoder.decode(is);
		maobyte.xml.decode.AXmlResourceParser parser = new maobyte.xml.decode.AXmlResourceParser();
		parser.open(new ByteArrayInputStream(axml.getData()), axml.mTableStrings);

		boolean success = false;
		int type;
		while ((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
			if (type != XmlPullParser.START_TAG)
				continue;
			if (parser.getName().equals("manifest")) {
				int size = parser.getAttributeCount();
				for (int i = 0; i < size; ++i) {
					if (parser.getAttributeName(i).equals("package")) {
						packageName = parser.getAttributeValue(i);
					}
				}
			} else if (parser.getName().equals("application")) {
				int size = parser.getAttributeCount();
				for (int i = 0; i < size; ++i) {
					if (parser.getAttributeNameResource(i) == 0x01010003) {
						customApplication = true;
						customApplicationName = parser.getAttributeValue(i);
						if (customApplicationName.startsWith(".")) {
							if (packageName == null)
								throw new NullPointerException("Package name is null.");
							customApplicationName = packageName + customApplicationName;
						}
						int index = axml.mTableStrings.getSize();
						byte[] data = axml.getData();
						int off = parser.currentAttributeStart + 20 * i;
						off += 8;
						ManifestParse.writeInt(data, off, index);
						off += 8;
						ManifestParse.writeInt(data, off, index);
					}
				}
				if (!customApplication) {
					int off = parser.currentAttributeStart;
					byte[] data = axml.getData();
					byte[] newData = new byte[data.length + 20];
					System.arraycopy(data, 0, newData, 0, off);
					System.arraycopy(data, off, newData, off + 20, data.length - off);

					// chunkSize
					int chunkSize = ManifestParse.readInt(newData, off - 32);
					ManifestParse.writeInt(newData, off - 32, chunkSize + 20);
					// attributeCount
					ManifestParse.writeInt(newData, off - 8, size + 1);

					int idIndex = parser.findResourceID(0x01010003);
					if (idIndex == -1)
						throw new IOException("idIndex == -1");

					boolean isMax = true;
					for (int i = 0; i < size; ++i) {
						int id = parser.getAttributeNameResource(i);
						if (id > 0x01010003) {
							isMax = false;
							if (i != 0) {
								System.arraycopy(newData, off + 20, newData, off, 20 * i);
								off += 20 * i;
							}
							break;
						}
					}
					if (isMax) {
						System.arraycopy(newData, off + 20, newData, off, 20 * size);
						off += 20 * size;
					}

					ManifestParse.writeInt(newData, off,
							axml.mTableStrings.find("http://schemas.android.com/apk/res/android"));
					ManifestParse.writeInt(newData, off + 4, idIndex);
					ManifestParse.writeInt(newData, off + 8, axml.mTableStrings.getSize());
					ManifestParse.writeInt(newData, off + 12, 0x03000008);
					ManifestParse.writeInt(newData, off + 16, axml.mTableStrings.getSize());
					axml.setData(newData);
				}
				success = true;
				break;
			}
		}
		if (!success)
			throw new IOException();
		ArrayList<String> list = new ArrayList<>(axml.mTableStrings.getSize());
		axml.mTableStrings.getStrings(list);
		list.add(Name);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		axml.write(list, baos);
		return baos.toByteArray();
	}

	private void readZip(ZipFile zip) throws Exception {
		Enumeration enums = zip.entries();
		while (enums.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) enums.nextElement();
			String entryName = entry.getName();
			// System.out.println(entryName);
			entries.add(entryName);

			if (entryName.startsWith("classes") && entryName.endsWith(".dex")) {
				dexEntries.add(entryName);
			}

			if (entryName.startsWith("res/layout/") && entryName.endsWith(".xml")) {
				resEntries.add(entryName);
			}

		}

	}



	public byte[] getDexBuilderData(DexBuilder dexBuilder) throws IOException {

		MemoryDataStore memoryDataStore = new MemoryDataStore();
		dexBuilder.writeTo(memoryDataStore);
		return Arrays.copyOf(memoryDataStore.getBufferData(), memoryDataStore.getSize());

	}


}
