package com.home.modeler.activies;

import com.home.modeler.R;
import com.home.modeler.base.BaseFragment.kFragmentShowing;
import com.home.modeler.fragments.HomePhotosFragment;
import com.home.modeler.fragments.ItemPhotosFragment;
import com.home.modeler.fragments.ModelerFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class HomeModelerFragmentActivity extends FragmentActivity {
	private ViewPager _mViewPager;
	private ViewPagerAdapter _adapter;
	private HomePhotosFragment homePhotosFragment;
	private ItemPhotosFragment itemPhotosFragment;
	private ModelerFragment modelerFragment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		getActionBar().setHomeButtonEnabled(true);
		setUpView();
		setTab();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        View v = (View) menu.findItem(R.id.search).getActionView();
        EditText txtSearch = (EditText)v.findViewById(R.id.txt_search);
 
        txtSearch.setOnEditorActionListener(new OnEditorActionListener() {
 
            @Override
			public boolean onEditorAction(TextView v, int arg1, KeyEvent arg2) {
            	Toast.makeText(getBaseContext(), "Search : " + v.getText(), 
            			Toast.LENGTH_SHORT).show();
                return false;
			}
        });
 
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.home_photo:
			_mViewPager.setCurrentItem(0);
			break;
		case R.id.modeler:
			_mViewPager.setCurrentItem(1);
			break;
		case R.id.item_photo:
			_mViewPager.setCurrentItem(2);
			break;
		case R.id.close:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpView() {
		_mViewPager = (ViewPager) findViewById(R.id.viewPager);

		homePhotosFragment = new HomePhotosFragment();
		itemPhotosFragment = new ItemPhotosFragment();
		modelerFragment = new ModelerFragment();

		_adapter = new ViewPagerAdapter(getApplicationContext(),
				getSupportFragmentManager());
		_mViewPager.setAdapter(_adapter);
	}

	private void setTab() {
		_mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int position) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					findViewById(R.id.home_tab).setVisibility(View.VISIBLE);
					findViewById(R.id.modeler_tab)
							.setVisibility(View.INVISIBLE);
					findViewById(R.id.item_tab).setVisibility(View.INVISIBLE);
					break;

				case 1:
					findViewById(R.id.home_tab).setVisibility(View.INVISIBLE);
					findViewById(R.id.modeler_tab).setVisibility(View.VISIBLE);
					findViewById(R.id.item_tab).setVisibility(View.INVISIBLE);
					break;
				case 2:
					findViewById(R.id.home_tab).setVisibility(View.INVISIBLE);
					findViewById(R.id.modeler_tab)
							.setVisibility(View.INVISIBLE);
					findViewById(R.id.item_tab).setVisibility(View.VISIBLE);
					break;
				}
			}
		});

		setOnClickListenerForTab(R.id.home_text, 0);
		setOnClickListenerForTab(R.id.modeler_text, 1);
		setOnClickListenerForTab(R.id.item_text, 2);

		_mViewPager.setCurrentItem(1, true);
	}

	public class ViewPagerAdapter extends FragmentPagerAdapter {
		private Context _context;

		public ViewPagerAdapter(Context context, FragmentManager fm) {
			super(fm);
			_context = context;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				homePhotosFragment
						.didShowFragment(kFragmentShowing.kFragmentShowingIsHomePhotos);
				return homePhotosFragment;
			case 1:
				modelerFragment
						.didShowFragment(kFragmentShowing.kFragmentShowingIsModeler);
				return modelerFragment;
			case 2:
				itemPhotosFragment
						.didShowFragment(kFragmentShowing.kFragmentShowingIsItemPhotos);
				return itemPhotosFragment;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 3;
		}
	}

	public void setOnClickListenerForTab(int tabID, final int tabPosition) {
		findViewById(tabID).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				_mViewPager.setCurrentItem(tabPosition, true);
			}
		});
	}
}