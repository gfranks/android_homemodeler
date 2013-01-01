package com.home.modeler.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;


public class DrawableManager {
    private final Map<String, Drawable> drawableMap;
    public static DrawableManager instance;
    private int targetDensity;

    private DrawableManager() {
        drawableMap = new HashMap<String, Drawable>();
    }
    
    public static DrawableManager getInstance(Context context) {
    	if (instance == null) {
    		instance = new DrawableManager();
    		instance.targetDensity = context.getResources().getDisplayMetrics().densityDpi;
    	}
    	
    	return instance;
    }

    public Drawable fetchDrawable(String path) {
        if (drawableMap.containsKey(path)) {
            return drawableMap.get(path);
        }

        Log.d(this.getClass().getSimpleName(), "image url:" + path);
        
//        Drawable drawable = Drawable.createFromPath(path);
        Bitmap bitmap = decodeFile(path);
        Drawable drawable = null;

        if (bitmap != null) {
            drawable = new BitmapDrawable(bitmap);
            drawableMap.put(path, drawable);
            Log.d(this.getClass().getSimpleName(), "got a thumbnail drawable: " + drawable.getBounds() + ", "
                    + drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", "
                    + drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());
        } else {
          Log.w(this.getClass().getSimpleName(), "could not get thumbnail");
        }

        return drawable;
    }
    
    public void addDrawableToManager(String path, Drawable drawable) {
    	drawableMap.put(path, drawable);
    }

    @SuppressLint("HandlerLeak") 
    public void fetchDrawableOnThread(final String urlString, final ImageView imageView) {
        if (drawableMap.containsKey(urlString)) {
            imageView.setImageDrawable(drawableMap.get(urlString));
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                imageView.setImageDrawable((Drawable) message.obj);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                //TODO : set imageView to a "pending" image
                Drawable drawable = fetchDrawable(urlString);
                Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
    
    public Drawable resizeBitmapForDrawable(Bitmap bitmap, int screenWidth, int screenHeight) {
    	int newWidth = bitmap.getWidth(), newHeight = bitmap.getHeight();
    	
//    	int bitmapWidth = bitmap.getWidth();
//    	int bitmapHeight = bitmap.getHeight();
//    	
//    	if (bitmapWidth > screenWidth) {
//    		int product = screenWidth/bitmapWidth;
//    		newWidth = screenWidth/2;
//    		newHeight = (bitmapHeight/product)/2;
//    	} else if (bitmapHeight > screenHeight) {
//    		int product = screenHeight/bitmapHeight;
//    		newHeight = screenHeight/2;
//    		newWidth = (bitmapWidth/product)/2;
//    	}
    	
        Bitmap bitmapOrig = Bitmap.createScaledBitmap(bitmap, newWidth*2, newHeight*2, false);
        return new BitmapDrawable(bitmapOrig);
    }
    
    private Bitmap decodeFile(String filePath){
    	File f = new File(filePath);
        Bitmap b = null;
        try {
            //Decode image size
            BitmapFactory.Options o1 = new BitmapFactory.Options();
            o1.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o1);
            fis.close();

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inPurgeable      = true;
            o2.inInputShareable = true;
            o2.inDensity        = targetDensity;
            o2.inTargetDensity  = targetDensity;
            
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
			e.printStackTrace();
		}
        return b;
    }
}