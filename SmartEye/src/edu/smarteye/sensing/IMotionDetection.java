package edu.smarteye.sensing;

public interface IMotionDetection {
	public int[] getPrevious();
    public boolean detect(int[] data, int width, int height);
}

