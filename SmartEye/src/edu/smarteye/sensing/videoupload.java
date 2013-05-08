package edu.smarteye.sensing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import android.os.AsyncTask;
import android.util.Log;

public class videoupload extends AsyncTask<URL,Integer,Long>
{
	String videofolder = android.os.Environment.getExternalStorageDirectory()+"/Record/";
	private final String VIDEO_PATH_NAME = videofolder+"test.mp4";

	@Override
	protected Long doInBackground(URL... url) 
	{
		   String location = VIDEO_PATH_NAME;
		   String hostName = "ec2-54-224-254-71.compute-1.amazonaws.com";
		   String username = "testuser";
		   String password = "testuser";
		   FTPClient ftp = null;

	        InputStream in = null;
	        try 
	        {
	        	
	            ftp = new FTPClient();
	            ftp.connect(hostName);
	            ftp.enterLocalPassiveMode();
	            ftp.login(username, password);
	            ftp.setFileType(FTP.BINARY_FILE_TYPE);
	            ftp.changeWorkingDirectory("/uploads");
	            int reply = ftp.getReplyCode();
	            Log.v("Received Reply from FTP Connection:" ,Integer.toString(reply));
	            if (FTPReply.isPositiveCompletion(reply)) 
	            {
	                System.out.println("Connected Success");
	            }

	            File f1 = new File(location);
	            in = new FileInputStream(f1);
	            ftp.storeFile(VIDEO_PATH_NAME, in);
	            ftp.logout();
	            ftp.disconnect();
	        }
			catch(Exception ex)
			{
			     ex.printStackTrace();
			}
			return null;
			
	}

}