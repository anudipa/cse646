package edu.smarteye.sensing;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;



public class ExperimentControl extends Service 
{

	// Proximity
	ProximityTask proximityCheck;
	SenseMotion motionCheck;
	int lastStatus = 0;
	//
	public ExperimentControl() {
		Log.i("EXPERIMENT","ExperimentControl started!!!");
	}

	
	public void onCreate()
	{
		try {
			motionCheck = new SenseMotion(getApplicationContext());
			motionCheck.startSense();
			proximityCheck = new ProximityTask(getApplicationContext(),"Neighbors",60000L);
			proximityCheck.start();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		return START_STICKY;
	}
	
	public void onDestroy()
	{
		motionCheck.stopSense();
		proximityCheck.stop();
	}

}
