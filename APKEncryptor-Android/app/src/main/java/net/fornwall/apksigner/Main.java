package net.fornwall.apksigner;

import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;


public class Main
{


	public static void main(String[] args) throws Exception
    {


		String keystorePath = "/storage/emulated/0/test.jks";
		String inputFile = "/storage/emulated/0/版权虎3.4.apk";
		String outputFile = "/storage/emulated/0/版权虎3.4_signed.apk";

		char[] storePassword = "android".toCharArray();
        char[] keyPassword = "android".toCharArray();

		File keystoreFile = new File(keystorePath);

        
		if (!keystoreFile.exists())
        {
            createJKS();
		}


        try
        {
            KeyStore keyStore = KeyStoreFileManager.loadKeyStore(keystorePath,null);
            String alias = keyStore.aliases().nextElement();
            X509Certificate publicKey = (X509Certificate) keyStore.getCertificate(alias);
			PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keyPassword);
			ZipSigner.signZip(publicKey, privateKey, "SHA1withRSA", inputFile, outputFile);

            System.out.println("签名成功");
		}
        catch (Exception e)
        {
            e.printStackTrace();
		}



	}



    public static void createJKS()
    {


		String keystorePath = "/storage/emulated/0/test.jks";
        String alias = "test";
        char[] storePassword = "android".toCharArray();
        char[] keyPassword = "android".toCharArray();

        try
        {
            CertCreator.DistinguishedNameValues nameValues = new CertCreator.DistinguishedNameValues();
            nameValues.setCommonName("APKSigner");
            nameValues.setOrganization("Earth");
            nameValues.setOrganizationalUnit("Earth");
            CertCreator.createKeystoreAndKey(keystorePath, storePassword, "RSA", 2048, alias, keyPassword, "SHA1withRSA",
                                             30, nameValues);
            System.out.println("证书创建成功");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

}
