package cn.beingyi.apkenceyptor.utils;


import java.io.*;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.Formatter;

public class FileUtils
{




    public static String getMd5ByFile(String path)
    {
        String value = null;
        File file=new File(path);
		FileInputStream in=null;
		try
        {
		    in = new FileInputStream(file);

            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
			byte[] md5_byte=md5.digest();
			
			
            BigInteger bi = new BigInteger(1, md5_byte);
            value = bi.toString(16);
			value=toHexString(md5_byte);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != in)
            {
                try
                {
                    in.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
		
		

		
		
        return value;
    }

	private static String toHexString(byte[] bytes) {       
        Formatter formatter = new Formatter();              
        for (byte b : bytes) {          
            formatter.format("%02x", b);
        }

        String res = formatter.toString();
        formatter.close();
        return res;
	}




    public static void mkdir(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {

        }
    }


    public static void copyFile(String oldPath, String newPath)
    {  
        try
        {  
            int bytesum = 0;  
            int byteread = 0;  
            File oldfile = new File(oldPath);  

            if (!new File(newPath).getParentFile().exists())
            {
                new File(newPath).getParentFile().mkdirs();
            }

            if (oldfile.exists())
            {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);  
                byte[] buffer = new byte[1444];  
                while ((byteread = inStream.read(buffer)) != -1)
                {  
                    bytesum += byteread;
                    System.out.println(bytesum);  
                    fs.write(buffer, 0, byteread);  
                }  
                inStream.close();  
                fs.close();  
            }  
        }  
        catch (Exception e)
        {  
            e.printStackTrace();  
        }  
    }  


    public static void delSingleFile(String path)
    {
        try
        {
            File file = new File(path);
            if(file.exists()) {
                file.delete();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(); 
        }
    }


    public static void delFolder(String folderPath)
    {
        try
        {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        }
        catch (Exception e)
        {
            e.printStackTrace(); 
        }
    }

    
    private static boolean delAllFile(String path)
    {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists())
        {
            return flag;
        }
        if (!file.isDirectory())
        {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++)
        {
            if (path.endsWith(File.separator))
            {
                temp = new File(path + tempList[i]);
            }
            else
            {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile())
            {
                temp.delete();
            }
            if (temp.isDirectory())
            {
                delAllFile(path + File.separator + tempList[i]);
                delFolder(path +File.separator + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }







    public static void writeFile(String path, String content) throws IOException
    {

        File saveFile = new File(path);
        if (!saveFile.getParentFile().exists())
        {
            saveFile.getParentFile().mkdirs();
        }

        if (!saveFile.exists())
        {
            saveFile.createNewFile();
        }
        final FileOutputStream outStream = new FileOutputStream(saveFile);
        try
        {
            outStream.write(content.getBytes());
            outStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }



	
	
	




    public static String getSuffix(String path){
        String result="";
        if(new File(path).isDirectory()){
            return "";
        }
        result=path.subSequence(path.lastIndexOf(".")+1,path.length()).toString();
        return result.toLowerCase();
    }

    public static byte[] getByte(String filePath) {

        InputStream in = null;
        byte[] data=null;
        try {
            in = new FileInputStream(filePath);

            data = toByteArray(in);
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();

    }




}
