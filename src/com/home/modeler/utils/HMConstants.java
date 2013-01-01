package com.home.modeler.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class HMConstants {
	
	public static final int PHOTOTAKEN_RESULT_CODE    = 101;
	public static final int PHOTOSELECTED_RESULT_CODE = 102;
	
	public static final String HOME_FILE_DIR      = "HomePhotos";
	public static final String ITEM_FILE_DIR      = "ItemPhotos";
	public static final String HOME_INTENT_FILTER = "HomePhotoSelected";
	public static final String ITEM_INTENT_FILTER = "ItemPhotoSelected";
	public static final String Drawable_IntentKey = "drawable_source";

    public static float getWidthInInches(Activity activity) {
    	DisplayMetrics metrics = new DisplayMetrics();
    	activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return metrics.widthPixels / metrics.xdpi;
    }
    
	public static float getHeightInInches(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		return metrics.heightPixels / metrics.ydpi;
	}
	
	public static double getSizeInInches(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		float widthInInches = metrics.widthPixels / metrics.xdpi;
		float heightInInches = metrics.heightPixels / metrics.ydpi;
		
		return Math.sqrt(Math.pow(widthInInches, 2) + Math.pow(heightInInches, 2));
	}
	
	public static boolean isDefaultDevice(Activity activity) {
		return getSizeInInches(activity) < 6.5;
	}
	
	public static boolean is7inchTablet(Activity activity) {
		return getSizeInInches(activity) < 6.5;
	}
	
	public static boolean is8inchTablet(Activity activity) {
		return getSizeInInches(activity) < 6.5;
	}
}
