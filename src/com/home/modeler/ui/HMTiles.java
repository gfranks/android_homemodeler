package com.home.modeler.ui;

import com.home.modeler.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class HMTiles extends LinearLayout {

	private HMHandler searchHandler;
	
	private Button btnAddPhoto;
	public Button btnEditPhotos;
	
	public HMTiles(Context context) {
		super(context);
		setupView(context);
	}
	public HMTiles(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupView(context);
	}

	
	private void setupView(Context context){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.photo_tiles, this);
		
		btnAddPhoto = (Button) findViewById(R.id.photo_tiles_add);
		btnAddPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HMTiles.this.searchHandler.onAddPhoto();
			}
		});
		
		btnEditPhotos = (Button) findViewById(R.id.photo_tiles_edit);
		btnEditPhotos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HMTiles.this.searchHandler.onEditPhotos();
			}
		});
	}
	
	
	public void setTileHandler(HMHandler searchHandler) {
		this.searchHandler = searchHandler;
	}
	
	public interface HMHandler {
		
		public void onAddPhoto();
		
		public void onEditPhotos();
	}
}
