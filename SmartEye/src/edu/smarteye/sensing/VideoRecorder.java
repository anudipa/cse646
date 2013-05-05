package edu.smarteye.sensing;

//import com.example.project.R;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoRecorder implements SurfaceHolder.Callback 
{
	String videofolder = android.os.Environment.getExternalStorageDirectory()+"/Record/";
	private final String VIDEO_PATH_NAME = videofolder+"test.mp4";
	private MediaRecorder mMediaRecorder;
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mHolder;
	private boolean mInitSuccesful;
		
	void startrecording(Context context)
	{
		mSurfaceView = new SurfaceView(context);
	    mHolder = mSurfaceView.getHolder();
	    mHolder.addCallback(this);
	    mMediaRecorder.start();
        try 
        {
            Thread.sleep(10 * 1000);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
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

	private void shutdown() 
	{
	    // Release MediaRecorder and especially the Camera as it's a shared
	    // object that can be used by other applications
		mMediaRecorder.stop();
	    mMediaRecorder.reset();
	    mMediaRecorder.release();
	    mCamera.release();

	    // once the objects have been released they can't be reused
	    mMediaRecorder = null;
	    mCamera = null;
	}

}
