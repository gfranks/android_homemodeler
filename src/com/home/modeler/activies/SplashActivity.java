package com.home.modeler.activies;

import java.io.File;

import com.home.modeler.R;
import com.home.modeler.base.BaseFragment;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;

public class SplashActivity extends Activity {

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
    		try {
    			// do work here
    			File dir1 = getDir(BaseFragment.HOME_FILE_DIR, Context.MODE_PRIVATE);
    			File dir2 = getDir(BaseFragment.ITEM_FILE_DIR, Context.MODE_PRIVATE);
    			if (!dir1.isDirectory()) {
    				dir1.mkdir();
    			}
    			if (!dir2.isDirectory()) {
    				dir2.mkdir();
    			}
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				runOnUiThread(transitionRUnnable);
			}
    	}
    };
    
    Runnable transitionRUnnable = new Runnable() {
    	public void run() {
    		Intent mainIntent = new Intent(SplashActivity.this, HomeModelerFragmentActivity.class);
    		finish();
    		startActivity(mainIntent);
    	}
    };
}
