package edu.smarteye.sensing;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.hardware.SensorEventListener;


public class SenseMotion implements SensorEventListener{
	
	int STATUS = 0;
	private double mLastA = 0;
	String TAG = "SenseMotion";
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mGyroscope;
	
	private boolean AcceFLAG = true;
	private boolean GyroFLAG = true;
	private boolean SoundFLAG = true;
	
	private long lastUpdate = 0;
	private long lastStatusUpd = 0;
	private int lastStatus = -1;
	private int flag = 0;
	private boolean mStartRecording = false;
	private double thresholdUp  = 0.0;
	private double thresholdDown = 0.0;
	private int thresholdGyro = 0;
	//private boolean motion = false;
	/*File logFile;
	File f;
	FileWriter fwriter;
	BufferedWriter o;*/
	//Sampling
	float[] sample = new float[20];
	int i;
	float sum;
	float totalTime;
	//gyro
	long last = 0;
	private float mLastRotAngle;
	private final static double EPSILON = 0.00000001;
	//final float currentRotVector[] =  { 1, 0, 0, 0 };
	private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
	//audio sense
	Timer recordTask;
	int interval = 1000;
	private MediaRecorder mRecorder = null;
	private int soundLevel = -1;
  

    Handler handler = new Handler();
	
	public SenseMotion(Context context) 
	{
		try
		{
			File config = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"config.txt");
			BufferedReader br = new BufferedReader(new FileReader(config));
			String s = null;
			while((s=br.readLine()) != null)
			{
				Log.i(TAG,s);
				if((s = s.trim()).length() > 0)
				{
					if (s.contains("A:false"))
						AcceFLAG = false;
					else if(s.contains("G:false"))
						GyroFLAG = false;
					else if(s.contains("S:false"))
						SoundFLAG = false;
				}
			}
			br.close();
			Log.d(TAG,"Accel = "+AcceFLAG+" Gyro = "+GyroFLAG+" Sound = "+SoundFLAG);
			
		}catch(Exception e)
		{
			Log.e(TAG, "Error at Config file "+e.getMessage());
		}
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        
        lastUpdate = System.currentTimeMillis();
		
	}

	

	
	public void startSense()
	{
		try {
			if (flag == 1)
			{
				mSensorManager.unregisterListener(this, mAccelerometer);
				mSensorManager.unregisterListener(this, mGyroscope);
				thresholdUp  = 0.0;
				thresholdDown = 0.0;
				thresholdGyro = 0;
				flag = 0;
				
			}
			
            flag = 1;
            if(AcceFLAG == true)
            	mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            if(GyroFLAG == true)
            	mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            mStartRecording = false;
            if(SoundFLAG == true)
            {
	            recordTask = new Timer();
				recordTask.scheduleAtFixedRate(new TimerTask() 
				{
					 public void run()
					 {
						 if (mRecorder == null) 
							{
								mRecorder = new MediaRecorder();
								mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
								mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
								mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
								mRecorder.setOutputFile("/dev/null"); 
								if(mRecorder == null)
				            		{
				            			Log.i(TAG, "Media obj is null");
				            		}
				            	//	Log.i(TAG,"About to start media");
				            	if(!mStartRecording)
				            	{
				            		try
				            		{
				            			mRecorder.prepare();
				            			mRecorder.start();
				            			mStartRecording = true;
				            			//Log.d(TAG,"Started Recording");
				            			Thread.sleep(500);
				            		}catch(IllegalStateException e1)
				            		{
				            			Log.e(TAG, "Error inside---"+e1.getMessage());
				            			e1.printStackTrace();
				            		}catch (IOException e2)
				            		{
				            			Log.e(TAG, "Error inside---"+e2.getMessage());
				            			e2.printStackTrace();
				            		} catch (InterruptedException e) {
				            			e.printStackTrace();
									}
				            	}	
							} 
						 
							
							if (mRecorder != null && mStartRecording == true)
							{
							//	Log.d(TAG,"Stopping Recording");
								mRecorder.getMaxAmplitude();
								mRecorder.stop();
								soundLevel = mRecorder.getMaxAmplitude();
								if (SoundFLAG == true && AcceFLAG == false)
								{
									if(soundLevel > 1000)
										STATUS = 1;
									else
										STATUS = 0;
									long thisTime = System.currentTimeMillis();
							        if (!(lastStatus == 1 && (thisTime - lastStatusUpd) < 60000) && (lastStatus != STATUS || (thisTime - lastStatusUpd) > 50000))
							          {
							        	 
							        	  try
							        	  {
							        		  File root = Environment.getExternalStorageDirectory();
							        		  File f= new File(root.getAbsolutePath(), "status.txt");  
							        		  FileWriter filewriter = new FileWriter(f);  
							        		  BufferedWriter out = new BufferedWriter(filewriter);
							        		  out.write("FLAG"+STATUS);
							        		  out.close();
							        		  Log.i(TAG,"In Sound Changing Status: "+STATUS+" sound level = "+soundLevel);
							        		  lastStatusUpd = System.currentTimeMillis();
							        		  lastStatus = STATUS;
							        		  
							        	  }
							        	  catch(Exception e)
							        	  {
							        		  Log.e(TAG,"In Gyroscope "+e.getMessage());
							        	  }
							          }
									
								}
									
							//	Log.i(TAG, "Amplitude = "+soundLevel);
								
								mRecorder.reset();
								
								mRecorder.release();
								
								mRecorder = null;
								mStartRecording = false;
								
							}
					 }
				 }, 0, interval);
				
            }
		} catch (Exception e) 
		{
			Log.e(TAG, e.getMessage()); 
		}
		
	}
	
	public void stopSense()
	{
		
		if(SoundFLAG == true && recordTask != null)
		{
			recordTask.cancel();
		}
		
		try
		 {
			 File root = Environment.getExternalStorageDirectory();
			 File f= new File(root.getAbsolutePath(), "status.txt");  
			 FileWriter filewriter = new FileWriter(f);  
			 BufferedWriter out = new BufferedWriter(filewriter);
			 out.write("FLAG0");
			 out.close();
		 }catch (Exception e)
		 {
			 Log.e(TAG, "In findMotion() "+e.getMessage());
		 }
		Log.d(TAG,"Stop Sensing");
		
		
		if(flag == 1)
			{
				if(AcceFLAG == true)
					mSensorManager.unregisterListener(this, mAccelerometer);
				if(GyroFLAG == true)
					mSensorManager.unregisterListener(this, mGyroscope);
				thresholdUp  = 0.0;
				thresholdDown = 0.0;
				thresholdGyro = 0;
				flag = 0;
			}
			
		
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		float[] value = event.values;
		long actualTime = System.currentTimeMillis();
		
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			float x = value[0];
	//		float y = value[1];
			float z = value[2];
			double sampleAcc = 0.0;
			double a = Math.sqrt(x*x + z*z);
			
			if(lastUpdate == 0)
			{
				lastUpdate = actualTime;
			}
			if (actualTime - lastUpdate > 0 && actualTime - lastUpdate < 300 && i < 20)
			{
				sample[i] = (float) a;
				i++;
			}
				
			
			if (i == 20 || actualTime - lastUpdate > 300)
			{
				totalTime = actualTime - lastUpdate;
				lastUpdate = actualTime;
				sum = 0;
				for(int j = 0;j < i; j++)
				{
					sum += sample[j];
				}
				sampleAcc = sum/i;
				i=0;
				
				if (flag == 1)			
					findMotion(sampleAcc, mLastA);			
				mLastA = sampleAcc;
		
				
			}
			
					
		}
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
		{
			final float currentRotVector[] =  { 1, 0, 0, 0 };
			if (timestamp != 0) 
			{
	              final float dT = (event.timestamp - timestamp) * NS2S;
	              // Axis of the rotation sample, not normalized yet.
	              float axisX = event.values[0];
	              float axisY = event.values[1];
	              float axisZ = event.values[2];

	              // Calculate the angular speed of the sample
	              float omegaMagnitude = (float) Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

	              // Normalize the rotation vector if it's big enough to get the axis
	              if (omegaMagnitude > EPSILON) {
	                  axisX /= omegaMagnitude;
	                  axisY /= omegaMagnitude;
	                  axisZ /= omegaMagnitude;
	              }

	              // Integrate around this axis with the angular speed by the timestep
	              // in order to get a delta rotation from this sample over the timestep
	              // We will convert this axis-angle representation of the delta rotation
	              // into a quaternion before turning it into the rotation matrix.
	              float thetaOverTwo = omegaMagnitude * dT / 2.0f;
	              float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
	              float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
	              deltaRotationVector[0] = sinThetaOverTwo * axisX;
	              deltaRotationVector[1] = sinThetaOverTwo * axisY;
	              deltaRotationVector[2] = sinThetaOverTwo * axisZ;
	              deltaRotationVector[3] = cosThetaOverTwo;
	              
	              /* quaternion multiplication 
	              Reference: http://www.cprogramming.com/tutorial/3d/quaternions.html
	          */

	          currentRotVector[0] = deltaRotationVector[0] * currentRotVector[0] - 
	                                deltaRotationVector[1] * currentRotVector[1] - 
	                                deltaRotationVector[2] * currentRotVector[2] - 
	                                deltaRotationVector[3] * currentRotVector[3];

	          currentRotVector[1] = deltaRotationVector[0] * currentRotVector[1] + 
	                                deltaRotationVector[1] * currentRotVector[0] + 
	                                deltaRotationVector[2] * currentRotVector[3] - 
	                                deltaRotationVector[3] * currentRotVector[2];

	          currentRotVector[2] = deltaRotationVector[0] * currentRotVector[2] - 
	                                deltaRotationVector[1] * currentRotVector[3] + 
	                                deltaRotationVector[2] * currentRotVector[0] + 
	                                deltaRotationVector[3] * currentRotVector[1];

	          currentRotVector[3] = deltaRotationVector[0] * currentRotVector[3] + 
	                                deltaRotationVector[1] * currentRotVector[2] - 
	                                deltaRotationVector[2] * currentRotVector[1] + 
	                                deltaRotationVector[3] * currentRotVector[0];
	          final float rad2deg = (float) (180.0f / Math.PI);
	          float RotAngle = currentRotVector[0] * rad2deg;
	          axisX = currentRotVector[1];
	          axisY = currentRotVector[2];
	          axisZ = currentRotVector[3];
	          if (Math.abs(RotAngle) != Math.abs(mLastRotAngle) && Math.abs(Math.abs(RotAngle)- Math.abs(mLastRotAngle)) > 0.0033)
	          {
	        	
	        	 //mLastRotAngle = RotAngle;
	        	 thresholdGyro = thresholdGyro+2;
	        	 
	        	 if(thresholdGyro > 5)
		          {
		        	  STATUS = 1;
		        	  
		        	  Log.i(TAG,"Sensor Orientation GyroScope Threshold: "+ thresholdGyro+ " axisX: " + axisX + //
		    	              " axisY: " + axisY + //
		    	                          " axisZ: " + axisZ + //
		    	              " RotAngle: " + RotAngle + " Last RotAngle: " + mLastRotAngle + " Difference: " + Math.abs(Math.abs(RotAngle) - Math.abs(mLastRotAngle)));
		        	 
		        	 
		          }
	        	 
	        	 mLastRotAngle = RotAngle;
	        	
	        	 
	          }
	          else if (thresholdGyro > 0)
	          {
	        	  thresholdGyro --;
	        	  
	        	  if (thresholdGyro < 2)
	        		  STATUS = 0;
	        	 
	        	 
	          }
	          
	          long thisTime = System.currentTimeMillis();
	          if (!(lastStatus == 1 && (thisTime - lastStatusUpd) < 60000) && (lastStatus != STATUS || (thisTime - lastStatusUpd) > 50000))
	          {
	        	 
	        	  try
	        	  {
	        		  File root = Environment.getExternalStorageDirectory();
	        		  File f= new File(root.getAbsolutePath(), "status.txt");  
	        		  FileWriter filewriter = new FileWriter(f);  
	        		  BufferedWriter out = new BufferedWriter(filewriter);
	        		  out.write("FLAG"+STATUS);
	        		  out.close();
	        		  Log.i(TAG,"In Gyro Changing Status: "+STATUS+" threshold = "+thresholdGyro);
	        		  thresholdGyro = 0;
	        		  lastStatusUpd = System.currentTimeMillis();
	        		  lastStatus = STATUS;
	        		  
	        	  }
	        	  catch(Exception e)
	        	  {
	        		  Log.e(TAG,"In Gyroscope "+e.getMessage());
	        	  }
	          }
	               
	          }
				
			 timestamp =  event.timestamp;
	       
		}
			
		
		
	}
	
	
	protected void findMotion( double nowA, double lastA)
	{
		 		 
		 //motion = false;
		 if (Math.abs(nowA - lastA) > 5)
		 {
			 thresholdUp = 0;
			 thresholdDown = 0;
					
		 }
		 else if(nowA - lastA > 0.15)
		 {
             thresholdUp = thresholdUp + 4;
             thresholdDown = 0;
		 }
		 else if(lastA - nowA > 0.15)
		 {
             thresholdDown = thresholdDown + 4;
             thresholdUp = 0;
		 }
		 else if(nowA - lastA > 0.05)
		 {
			 thresholdUp  = thresholdUp + 0.5;
		     if(thresholdDown >= 0.1)
		    	  thresholdDown = thresholdDown - 0.1;
		 }
		 else if(lastA - nowA > 0.05)
		 {
			 thresholdDown = thresholdDown + 0.5;
			 if (thresholdUp >= 0.1)
                 thresholdUp = thresholdUp - 0.1;
		 }
         else
         {
             if(thresholdUp >= 0.2)
                     thresholdUp = thresholdUp - 0.2;
             if(thresholdDown >= 0.2)
                    thresholdDown = thresholdDown - 0.2;
         }
		 
		 //String msg = String.format("threshold: UP = %.2f DOWN = %.2f", thresholdUp, thresholdDown);
		 if (STATUS == 1 && (thresholdUp < 0.6 || thresholdDown < 0.6))
		 {
			 STATUS = 0;
		 }
		 if ((thresholdUp > 0.5 && thresholdUp < 4) || (thresholdDown > 0.5 && thresholdDown < 4))
		 {
			 if(SoundFLAG == true && soundLevel > 250 && STATUS == 0)
			 {
				// motion = true;
				 STATUS = 1;
				 Log.i(TAG,"Moving Now: "+nowA+" Before: "+ lastA+" ampl = "+soundLevel);
			 }
		 }
		 else if(STATUS == 0 && (thresholdUp > 3 || thresholdDown > 3 || (SoundFLAG == true && soundLevel > 1000)))
		 {
			// motion = true;
			 STATUS = 1;
			 Log.i(TAG,"Moving Now: "+nowA+" Before: "+ lastA+" ampl = "+soundLevel);
			 thresholdUp = 1.0;
			 thresholdDown = 1.0;
		 }
		 
		 

			 long thisTime = System.currentTimeMillis(); 
			 if (lastStatus != STATUS || (thisTime - lastStatusUpd) > 60000 || lastStatusUpd == 0)
			 {
				 if(!(lastStatus == 1 && (thisTime - lastStatusUpd) < 50000))
				 {
				
					 try
					 {
						 File root = Environment.getExternalStorageDirectory();
						 File f= new File(root.getAbsolutePath(), "status.txt");  
						 FileWriter filewriter = new FileWriter(f);  
						 BufferedWriter out = new BufferedWriter(filewriter);
						 out.write("FLAG"+STATUS);
						 out.close();
						 Log.i(TAG,"In Accel Changing Status: "+STATUS);
					 }catch (Exception e)
					 {
						 Log.e(TAG, "In findMotion() "+e.getMessage());
					 }
					 lastStatusUpd = thisTime;
					 lastStatus = STATUS;
				}
			 }
	}


}
