package com.home.modeler.activies;

import java.io.File;

import com.home.modeler.R;
import com.home.modeler.utils.DrawableManager;
import com.home.modeler.utils.HMConstants;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;

public class SplashActivity extends Activity {
	
	private DrawableManager drawableManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        
        new Thread(doBackgroundWork).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }
    
    Runnable doBackgroundWork = new Runnable() {
    	public void run() {
			drawableManager = DrawableManager.getInstance(SplashActivity.this);
			
			File homePhotosDir = getDir(HMConstants.HOME_FILE_DIR, Context.MODE_PRIVATE);
			File itemPhotosDir = getDir(HMConstants.ITEM_FILE_DIR, Context.MODE_PRIVATE);
			
			if (!homePhotosDir.isDirectory()) {
				homePhotosDir.mkdir();
			}
			
			if (!itemPhotosDir.isDirectory()) {
				itemPhotosDir.mkdir();
			}
			
			loadPhotosToDrawableManager(homePhotosDir);
			loadPhotosToDrawableManager(itemPhotosDir);
			
			runOnUiThread(transitionRunnable);
    	}
    };
    
    public void loadPhotosToDrawableManager(File dir) {
		File[] photos = dir.listFiles();
		for (File f : photos) {
			drawableManager.fetchDrawable(f.getAbsolutePath());
		}
	}
    
    Runnable transitionRunnable = new Runnable() {
    	public void run() {
    		Intent mainIntent = new Intent(SplashActivity.this, HomeModelerFragmentActivity.class);
    		finish();
    		startActivity(mainIntent);
    	}
    };
}
