package edu.smarteye.sensing;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.URL;

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
		// TODO Auto-generated method stub
		FTPClient ftp = new FTPClient();
		  try
		  {
		      Log.v( "Connecting "," To server" );
		      try
		      {
		    	  ftp.connect(InetAddress.getByName("ec2-50-17-179-124.compute-1.amazonaws.com"));
		      }
		      catch(Exception f)
		      {
		    	  f.printStackTrace();
		    	  Log.v("E:",f.toString());
		      }
		      Log.v("Reply code : ",Integer.toString(ftp.getReplyCode()));
		      if(!ftp.login("anonymous","anonymous"))
		      {
		          Log.v( "Login"," failed" );
		          ftp.logout();
		          
		      }
		      int reply = ftp.getReplyCode();
		      Log.v( "Connect returned: " , "" );
		      if (!FTPReply.isPositiveCompletion(reply)) 
		      {
		          ftp.disconnect();
		          Log.v( "Connection "," failed" );
		          
		      }
		      ftp.enterLocalPassiveMode(); 
		      FileInputStream in = new FileInputStream(VIDEO_PATH_NAME);
		      ftp.setFileType(ftp.BINARY_FILE_TYPE);
		      Log.v( "Uploading"," File" );
		      Log.v("Video ",VIDEO_PATH_NAME);
		      Log.v("Dir ",ftp.printWorkingDirectory());
		      ftp.changeWorkingDirectory("/uploads");
		      Log.v("New Dir : ",ftp.printWorkingDirectory());
		      if(in==null)
		    	  Log.v("InputStream "," is null");
		      else
		    	  Log.v("InputStream "," is not null");
		      boolean store = ftp.storeFile("/uploads",in); 
		      Log.v("Store ",String.valueOf(store));
		      in.close();
		      ftp.logout();
		      ftp.disconnect();
		  }
		  catch(Exception ex)
		  {
		      ex.printStackTrace();
		      Log.v("Error: ",ex.toString());
		  }
		return null;
	}
	
}
