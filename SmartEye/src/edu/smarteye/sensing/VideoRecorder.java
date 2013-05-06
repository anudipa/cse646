package edu.smarteye.sensing;

import java.io.IOException;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoRecorder implements SurfaceHolder.Callback 
{
	String TAG ="VideoRecorder";
	String videofolder = android.os.Environment.getExternalStorageDirectory()+"/Record/";
	private final String VIDEO_PATH_NAME = videofolder+"test.mp4";
	private MediaRecorder mMediaRecorder;
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mHolder;
	private boolean mInitSuccesful;
	
	public VideoRecorder(Context context)
	{
		mSurfaceView = new SurfaceView(context);
	    mHolder = mSurfaceView.getHolder();
	    mHolder.addCallback(this);
	    mMediaRecorder = new MediaRecorder();
	    Log.d(TAG, "Created instance");
	}
		
	void startrecording()
	{
		//mSurfaceView = new SurfaceView(context);
	    //mHolder = mSurfaceView.getHolder();
	   //Holder.addCallback(this);
	    mMediaRecorder.start();
        try 
        {
            Thread.sleep(10 * 1000);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            Log.e(TAG, "Error while starting "+e.getMessage());
        }
        //finish();
        
	}

	private void initRecorder(Surface surface) throws IOException 
	{
	    
	    if(mCamera == null) 
	    {
	        mCamera = Camera.open();
	        mCamera.unlock();
	    }
	    if(mMediaRecorder == null) 
	    	mMediaRecorder = new MediaRecorder();
	    mMediaRecorder.setPreviewDisplay(surface);
	    mMediaRecorder.setCamera(mCamera);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
	    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	    mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
	    mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
	    mMediaRecorder.setVideoFrameRate(30);
	    mMediaRecorder.setVideoSize(640, 480);
	    mMediaRecorder.setOutputFile(VIDEO_PATH_NAME);

	    try 
	    {
	        mMediaRecorder.prepare();
	    } 
	    catch (IllegalStateException e)
	    {
	        e.printStackTrace();
	        Log.e(TAG, "Error at Init: "+e.getMessage());
	    }

	    mInitSuccesful = true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
	    try 
	    {
	        if(!mInitSuccesful)
	            initRecorder(mHolder.getSurface());
	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	    }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
	    shutdown();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

	public void shutdown() 
	{
	    mMediaRecorder.stop();
	    mMediaRecorder.reset();
	    mMediaRecorder.release();
	    mCamera.release();
	    mMediaRecorder = null;
	    mCamera = null;
	}

}
