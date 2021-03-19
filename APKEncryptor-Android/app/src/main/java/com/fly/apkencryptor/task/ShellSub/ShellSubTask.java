package com.fly.apkencryptor.task.ShellSub;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.android.apksig.ApkSigner;
import com.fly.apkencryptor.R;
import com.fly.apkencryptor.activity.MainActivity;
import com.fly.apkencryptor.application.MyApp;
import com.fly.apkencryptor.fragment.AddShell;
import com.fly.apkencryptor.task.BaseTask;
import com.fly.apkencryptor.utils.APKUtils;
import com.fly.apkencryptor.utils.BYProtectUtils;
import com.fly.apkencryptor.utils.ByteEncoder;
import com.fly.apkencryptor.utils.Conf;
import com.fly.apkencryptor.utils.EncryptorConfig;
import com.fly.apkencryptor.utils.ExceptionUtils;
import com.fly.apkencryptor.utils.FileUtils;
import com.fly.apkencryptor.utils.ListUtil;
import com.fly.apkencryptor.utils.SPUtils;
import com.fly.apkencryptor.widget.StepLoading;
import com.google.common.collect.ImmutableList;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public class ShellSubTask extends BaseTask {

	Context context;
	MainActivity activity;
	AddShell fragment;
	Conf conf;
	public static boolean singleDEX = false;
	public boolean useKey = false;
	StepLoading stepLoading;

	String confEntry="src/"+BYProtectUtils.getAssetsName("application");
	List<String> enDexList=new ArrayList<>();

	public ShellSubTask(Context context,AddShell fragment,String inputPath, String outputPath, StepLoading mStepLoading) throws Exception {
		super(inputPath, outputPath);
		this.context=context;
		this.activity=(MainActivity) context;
		this.fragment=fragment;
		this.conf=new Conf(context);
		useKey=new Conf(context).getUseKey();
		stepLoading=mStepLoading;

		List<StepLoading.StepInfo> steps=new ArrayList<>();
		steps.add(new StepLoading.StepInfo(context.getString(R.string.parsing_apk),1));
		steps.add(new StepLoading.StepInfo(context.getString(R.string.processing_axml),2));
		steps.add(new StepLoading.StepInfo(context.getString(R.string.encrypting_dex),3));
		steps.add(new StepLoading.StepInfo(context.getString(R.string.saving_file),4));
		steps.add(new StepLoading.StepInfo(context.getString(R.string.signing_apk),5));
		steps.add(new StepLoading.StepInfo(context.getString(R.string.finished_encrypting),6));
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				stepLoading.iniSteps(steps);
			}
		});

	}


	public void start() {

		try {
			stepLoading.setStepStatus(1, StepLoading.Running);

            File apk=new File(MyApp.getContext().getFilesDir()+File.separator+"sub.apk");
            FileUtils.mkdir(apk.getParent());
            BYProtectUtils.copyAssetsFile("sub.apk",apk.getAbsolutePath());

            stepLoading.setStepStatus(1, StepLoading.Success);
		} catch (Exception e) {
			activity.showDialog(e.toString());
			stepLoading.setStepStatus(1, StepLoading.Failure);
			return;
		}


		try {
			stepLoading.setStepStatus(2, StepLoading.Running);

			byte[] manifest = parseManifest(getZipInputStream("AndroidManifest.xml"), new EncryptorConfig.Conf().getSubApplicationName());
			zipOut.addFile("AndroidManifest.xml", manifest);


			stepLoading.setStepStatus(2, StepLoading.Success);
		} catch (Exception e) {
			activity.showDialog(e.toString());
			stepLoading.setStepStatus(2, StepLoading.Failure);
			return;
		}



		try {
			stepLoading.setStepStatus(3, StepLoading.Running);

			byte[] dex = EncryptorConfig.getDex();
			zipOut.addFile("classes.dex", dex);


			if(singleDEX){
				String dexName="classes.dex";
				zipOut.addFile("src/" + BYProtectUtils.getAssetsName(dexName), xorEncode(FileUtils.toByteArray(getZipInputStream(dexName)),packageName));
				zipOut.removeFile(dexName);
				enDexList.add(BYProtectUtils.getAssetsName(dexName));
			}else {
				for (String dexName : dexEntries) {
					zipOut.addFile("src/" + BYProtectUtils.getAssetsName(dexName), xorEncode(FileUtils.toByteArray(getZipInputStream(dexName)),packageName));
					zipOut.removeFile(dexName);
					enDexList.add(BYProtectUtils.getAssetsName(dexName));
				}
			}

			stepLoading.setStepStatus(3, StepLoading.Success);
		} catch (Exception e) {
			activity.showDialog(e.toString());
			stepLoading.setStepStatus(3, StepLoading.Failure);
			return;
		}




		try {
			stepLoading.setStepStatus(4, StepLoading.Running);

			JSONObject jsonObject=new JSONObject();
			jsonObject.put("application",customApplicationName);
			jsonObject.put("sub",new EncryptorConfig.Conf().getSubApplicationName());
			jsonObject.put("dex", ListUtil.ListToString(enDexList));

			jsonObject.put("checkVirtual",fragment.cb_checkVirtual.isChecked());
			jsonObject.put("checkXposed",fragment.cb_checkXposed.isChecked());
			jsonObject.put("checkRoot",fragment.cb_checkRoot.isChecked());
			jsonObject.put("checkVPN",fragment.cb_checkVPN.isChecked());


			byte[] conf=ByteEncoder.Encrypt(jsonObject.toString().getBytes(),packageName);

			zipOut.addFile(confEntry, conf);

			zipOut.save();

			stepLoading.setStepStatus(4, StepLoading.Success);
		} catch (Exception e) {
			activity.showDialog(e.toString());
			stepLoading.setStepStatus(4, StepLoading.Failure);
			return;
		}



		try {
			stepLoading.setStepStatus(5, StepLoading.Running);

			File tmpFile = new File(outputPath + ".tmp");
			new File(outputPath).renameTo(tmpFile);

			if (useKey) {

				KeyStore keyStore = KeyStore.getInstance("JKS");
				keyStore.load(new FileInputStream(conf.getKeyStorePath()), conf.getKeyStorePw().toCharArray());

				String alias =conf.getCertAlias();

				PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, conf.getCertPw().toCharArray());
				X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
				ApkSigner.Builder builder = new ApkSigner.Builder(ImmutableList.of(new ApkSigner.SignerConfig.Builder("Fly", privateKey, ImmutableList.of(x509Certificate)).build()));
				builder.setInputApk(tmpFile);
				builder.setOutputApk(new File(outputPath));
				builder.setCreatedBy("Fly");
				builder.setMinSdkVersion(9);
				builder.setV1SigningEnabled(true);
				builder.setV2SigningEnabled(false);
				builder.build().sign();


			} else {


				KeyStore keyStore = KeyStore.getInstance("JKS");
				keyStore.load(BYProtectUtils.getStreamFromAssets("fly.jks"), "123456".toCharArray());

				String alias ="test";

				PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, "123456".toCharArray());
				X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
				ApkSigner.Builder builder = new ApkSigner.Builder(ImmutableList.of(new ApkSigner.SignerConfig.Builder("Fly", privateKey, ImmutableList.of(x509Certificate)).build()));
				builder.setInputApk(tmpFile);
				builder.setOutputApk(new File(outputPath));
				builder.setCreatedBy("Fly");
				builder.setMinSdkVersion(9);
				builder.setV1SigningEnabled(true);
				builder.setV2SigningEnabled(false);
				builder.build().sign();


			}

			tmpFile.delete();


			stepLoading.setStepStatus(6, StepLoading.Success);

			showFinish(context, new File(outputPath));


			stepLoading.setStepStatus(5, StepLoading.Success);
		} catch (Exception e) {
			activity.showDialog(e.toString());
			stepLoading.setStepStatus(5, StepLoading.Failure);
			return;
		}





	}



	public static void showFinish(Context context, final File signedFile) {

		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				SPUtils.putString("by","key","test");

				AlertDialog dialog = new AlertDialog.Builder(context)

						.setTitle(context.getString(R.string.encrypted_successfully))
						.setMessage(context.getString(R.string.the_output_file_is_saved_to_the_following_path) + signedFile.getAbsolutePath())
						.setCancelable(false)
						.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						})
						.setNegativeButton(context.getString(R.string.install), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								APKUtils.installAPK(context, signedFile.getAbsolutePath());

							}
						})
						.create();
				dialog.show();


			}
		});


	}


	//异或加密
	public static byte[] xorEncode(byte[] data,String key){
		byte[] keyBytes=key.getBytes();

		byte[] encryptBytes=new byte[data.length];
		for(int i=0; i<data.length; i++){
			encryptBytes[i]=(byte) (data[i]^keyBytes[i%keyBytes.length]);
		}
		return encryptBytes;
	}



}
