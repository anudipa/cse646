package edu.smarteye.sensing;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;



public class ExperimentControl extends Service 
{

	ProximityTask proximityCheck;
	SenseMotion motionCheck;
	Videoplanner vrecorder;
	Application app;
	
	int lastStatus = 0;
	
	public ExperimentControl() {
		Log.i("EXPERIMENT","ExperimentControl started!!!");
	}

	
	/*public void onCreate()
	{
		try {
			motionCheck = new SenseMotion(getApplicationContext());
			motionCheck.startSense();
			proximityCheck = new ProximityTask(getApplicationContext(),"Neighbors",100000L);
			proximityCheck.start();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		
		try 
		{
			vrecorder = new Videoplanner(getApplicationContext(),"abcd",app);
		} 
		catch (NoSuchAlgorithmException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		vrecorder.check();
		motionCheck = new SenseMotion(getApplicationContext());
		motionCheck.startSense();
		
		try {
			proximityCheck = new ProximityTask(getApplicationContext(),"Neighbours",60000L);
			proximityCheck.start();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return START_STICKY;
	}
	
	public void onDestroy()
	{
		motionCheck.stopSense();
		proximityCheck.stop();
		//vrecorder.shutdown();
	}

}
