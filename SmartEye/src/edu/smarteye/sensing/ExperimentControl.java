package edu.smarteye.sensing;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
<<<<<<< HEAD

import android.app.Application;
=======
>>>>>>> 472be5bef43c73ccf5661946b52f447bed50e11d
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

	

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		
<<<<<<< HEAD
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
=======
		/*Intent dialogIntent = new Intent(this, VideoRecorder.class);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(dialogIntent);
		
		vrecorder = new VideoRecorder(getApplicationContext());
		vrecorder.startrecording();*/
		
>>>>>>> 472be5bef43c73ccf5661946b52f447bed50e11d
		motionCheck = new SenseMotion(getApplicationContext());
		motionCheck.startSense();
		
		try {
			proximityCheck = new ProximityTask(getApplicationContext(),"Neighbours",90000L);
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
