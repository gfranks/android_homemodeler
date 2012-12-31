package com.home.modeler.fragments;

import com.home.modeler.R;
import com.home.modeler.base.BaseFragment;
import com.home.modeler.utils.DrawableManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ModelerFragment extends BaseFragment {

	private Context mContext;
	private ImageView selectedHomePhoto, selectedItemPhoto;

	public ModelerFragment() {
	}

	public ModelerFragment(Context context) {
		this.mContext = context;
		loadManagers(mContext);
		broadcastManager.registerReceiver(mHomePhotoSelectedReceiver,
				new IntentFilter(HOME_INTENT_FILTER));
		broadcastManager.registerReceiver(mItemPhotoSelectedReceiver,
				new IntentFilter(ITEM_INTENT_FILTER));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.modeler, container, false);

		selectedHomePhoto = (ImageView) view
				.findViewById(R.id.selectedHomePhoto);
		selectedItemPhoto = (ImageView) view
				.findViewById(R.id.selectedItemPhoto);

		return view;
	}

	private BroadcastReceiver mHomePhotoSelectedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("Home Photo Receiver", "Got home photo");
			String drawablePath = intent.getStringExtra(Drawable_IntentKey);
			Drawable homePhoto = DrawableManager.getInstance().fetchDrawable(
					drawablePath);
			selectedHomePhoto.setImageDrawable(homePhoto);
		}
	};

	private BroadcastReceiver mItemPhotoSelectedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("Home Photo Receiver", "Got home photo");
			String drawablePath = intent.getStringExtra(Drawable_IntentKey);
			Drawable itemPhoto = DrawableManager.getInstance().fetchDrawable(
					drawablePath);
			selectedItemPhoto.setImageDrawable(itemPhoto);
		}
	};

	public void onDestroy() {
		broadcastManager.unregisterReceiver(mHomePhotoSelectedReceiver);
		broadcastManager.unregisterReceiver(mItemPhotoSelectedReceiver);
		super.onDestroy();
	};

	@Override
	public void loadPhotos() {
	}

	@Override
	public void checkIfPhotosExist() {
	}
}