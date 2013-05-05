package edu.smarteye.sensing;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;



public class ExperimentControl extends Service 
{

	ProximityTask proximityCheck;
	SenseMotion motionCheck;
	VideoRecorder vrecorder;
	
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
		
		/*Intent dialogIntent = new Intent(this, VideoRecorder.class);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(dialogIntent);
		
		vrecorder = new VideoRecorder(getApplicationContext());
		vrecorder.startrecording();*/
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
