package edu.smarteye.sensing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import edu.buffalo.cse.phonelab.lib.PeriodicTask;

public class Videoplanner extends PeriodicTask
{
	
	Context context;
	private boolean local_record_status = false;
	private boolean reply;
	private VideoRecorder recorder;
	private String hashedID;
	private String serviceName;
	private String URL_String = "ftp://ec2-50-17-179-124.compute-1.amazonaws.com";
	URL myURL;
	Application appl;
	public Videoplanner(Context ctxt, String logTag,Application app) throws NoSuchAlgorithmException 
	{
		super(ctxt, logTag);
		TelephonyManager telephonyManager = (TelephonyManager) ctxt.getSystemService(Context.TELEPHONY_SERVICE);
		MessageDigest digester = MessageDigest.getInstance("SHA-1");
		byte[] digest = digester.digest(telephonyManager.getDeviceId().getBytes());
		hashedID = (new BigInteger(1, digest)).toString(16);
		serviceName = hashedID;
		context = ctxt;
		if(app == null)
			Log.v("app "," is null const");
		else
			Log.v("app "," is not null const");
		appl = app;
		Log.v("VideoPlanner","Constructor");
	}
	
	
			
	public void upload_video()
	{
		try 
		{
			myURL = new URL(URL_String);
			new videoupload().execute(myURL);
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public boolean isLocalRecording()
	{
		return local_record_status;
	}
	
	
	
	public void record()
	{
		
		if(appl == null)
			Log.v("app "," is null");
		else
			Log.v("app "," is not null");
		Intent dialogIntent = new Intent(context, VideoRecorder.class);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		appl.startActivity(dialogIntent);
	}	
	
	
	
	protected void check()
	{
		
			if(!isLocalRecording())
			{
				File root = Environment.getExternalStorageDirectory();
				File f= new File(root.getAbsolutePath(), "status.txt");
				BufferedReader br;
				try 
				{
					br = new BufferedReader(new FileReader(f));
					String s = null;
					if ((s=br.readLine())!= null && (s = s.trim()).length() > 0)
					{
							Log.v(TAG,"status: "+s);
							serviceName = hashedID+s;
					}
					br.close();
					if (serviceName.contains("FLAG1") && !serviceName.equals(hashedID))
					{
						local_record_status = true;
						record();  
						Thread.sleep(12000);
						upload_video(); 
						
					}
				} 
				catch (FileNotFoundException e)
				{
					
					e.printStackTrace();
				} catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}				
			else
			{
				if(reply==false)
				{
					local_record_status = false;
					//delete all the videos
				}
				else
				{
					for(int i =0;i<10;i++)
						record();
				}
				return;
			}
		
			
			
	}
	
	
	
	
	
	
	
}