package edu.smarteye.sensing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView txtView;
	Button start;
	Button stop;
	String TAG = "SmartEye";
	Timer readTask = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        txtView = (TextView) findViewById(R.id.textView1);
        start = (Button) findViewById(R.id.startBtn);
        stop = (Button) findViewById(R.id.stopBtn);
        
        final Intent serviceIntent = new Intent(getApplicationContext(),ExperimentControl.class );
    	
        
        start.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startService(serviceIntent);
				readTask = new Timer();
		        readTask.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() 
					{
						Log.i(TAG,"Inside Timer");
						
						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								try {
								File root = Environment.getExternalStorageDirectory();
								File f= new File(root.getAbsolutePath(), "status.txt");
								BufferedReader br;
								br = new BufferedReader(new FileReader(f));
								
								String s = null;
								if ((s=br.readLine())!= null && (s = s.trim()).length() > 0)
									{
										Log.v(TAG,"status: "+s);
										if(s.contains("FLAG1"))
										{
											txtView.setText("Status: Moving");
										}
										else if(s.contains("FLAG0"))
										{
											txtView.setText("Status: NotMoving");
										}
										
									}
								br.close();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									Log.e(TAG, "Error while reading "+e.getMessage());
								}
								
							}
							
							
						});
						
						
					}
		        	
		        }, 0, 3000L);
				
			}
        	
        	
        });
        
        stop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				stopService(serviceIntent);
				if (readTask != null)
					readTask.cancel();
				txtView.setText("Status: ");
			}
        	
        	
        });
        
	}

}
