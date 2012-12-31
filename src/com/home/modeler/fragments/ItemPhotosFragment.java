package com.home.modeler.fragments;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.home.modeler.R;
import com.home.modeler.base.BaseFragment;
import com.home.modeler.ui.HMTiles;
import com.home.modeler.ui.HMTiles.HMHandler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ItemPhotosFragment extends BaseFragment implements HMHandler {

	private HMTiles itemPhotoTiles;
	private TextView itemNoPhotosTV;
	private ListView itemListView;
	private ArrayList<File> itemPhotos;
	private PhotoListAdapter listAdapter;

	public ItemPhotosFragment() {
	}

	public ItemPhotosFragment(Context context) {
		loadManagers(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.item_photos, container, false);

		itemPhotoTiles = (HMTiles) view.findViewById(R.id.item_photo_tiles);
		itemPhotoTiles.setTileHandler(this);

		itemListView = (ListView) view.findViewById(R.id.itemListView);
		itemNoPhotosTV = (TextView) view.findViewById(R.id.item_nophotos);

		checkIfPhotosExist();
		listAdapter = new PhotoListAdapter(getActivity(), R.layout.photo_row,
				convertPhotoFileArrayToStringArray(itemPhotos));
		itemListView.setAdapter(listAdapter);
		itemListView.setOnItemSelectedListener(itemPhotoSelectedListener);

		return view;
	}

	@Override
	public void checkIfPhotosExist() {
		loadPhotos();
		if (itemPhotos.size() > 0) {
			itemNoPhotosTV.setVisibility(View.INVISIBLE);
			itemListView.setVisibility(View.VISIBLE);
		} else {
			itemNoPhotosTV.setVisibility(View.VISIBLE);
			itemListView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void loadPhotos() {
		itemPhotos = new ArrayList<File>();
		File[] photos = getActivity().getDir(ITEM_FILE_DIR,
				Context.MODE_PRIVATE).listFiles();
		for (File f : photos) {
			itemPhotos.add(f);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTOTAKEN_RESULT_CODE) {
			loadTakenPhoto(data);
		}
		
		if (requestCode == PHOTOSELECTED_RESULT_CODE) {
			loadSelectedPhoto(data);
		}
	}

	@Override
	public void onAddPhoto() {
		takePic();
	}

	@Override
	public void onEditPhotos() {
		if (itemPhotoTiles.btnEditPhotos.getText().toString()
				.equals(getActivity().getString(R.string.photo_tiles_edit))) {
			if (itemPhotos.size() > 0) {
				itemPhotoTiles.btnEditPhotos.setText(getActivity().getString(
						R.string.photo_tiles_done));
				listAdapter.notifyDataSetChanged(true);
			}
		} else {

			// delete photos here
			ArrayList<Integer> itemsForDeletion = listAdapter
					.getItemsForDeletion();

			for (Integer i : itemsForDeletion) {
				File photo = itemPhotos.get(i);
				itemPhotos.remove(i);
				photo.delete();
				checkIfPhotosExist();
			}

			itemPhotoTiles.btnEditPhotos.setText(getActivity().getString(
					R.string.photo_tiles_edit));
			listAdapter.notifyDataSetChanged(false);
		}
	}
	
	private void loadTakenPhoto(Intent data) {
		Bitmap photo = (Bitmap) data.getExtras().get("data");
		try {
			File dir = getActivity().getDir(ITEM_FILE_DIR,
					Context.MODE_PRIVATE);
			File newPhoto = new File(dir.getAbsolutePath() + File.separator
					+ generateRandomFilename() + ".png");
			FileOutputStream out = new FileOutputStream(newPhoto);
			photo.compress(Bitmap.CompressFormat.PNG, 90, out);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(newPhoto), 16 * 1024);
			bos.close();

			checkIfPhotosExist();

			Display display = getActivity().getWindowManager()
					.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);

			drawableManager
					.addDrawableToManager(newPhoto.getAbsolutePath(),
							drawableManager.resizeBitmapForDrawable(photo,
									size.x, size.y));

			// reload listview
			listAdapter.notifyDataSetChanged();

			Log.e(ItemPhotosFragment.class.getName()
					+ ".onActivityResult()", "PHOTO ADDED");
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(ItemPhotosFragment.class.getName()
					+ ".onActivityResult()", "CAUGHT EXCEPTION");
		}
	}
	
	private void loadSelectedPhoto(Intent data) {
		String imagePath = null;
		Uri selectedImageUri = data.getData();

        String filemanagerPhotoPath = selectedImageUri.getPath();
        String selectedImagePath = null;

        if(filemanagerPhotoPath!=null) {
            imagePath = filemanagerPhotoPath;
        } else {
            selectedImagePath = getPathFromSelectedPhoto(selectedImageUri);
        }
        
        if(selectedImagePath!=null) {
            imagePath = selectedImagePath;
        }
        
        if (imagePath != null) {
        	try {
        		File src = new File(imagePath);
        		File dir = getActivity().getDir(ITEM_FILE_DIR,
    					Context.MODE_PRIVATE);
    			File newPhoto = new File(dir.getAbsolutePath() + File.separator
    					+ generateRandomFilename() + ".png");
	        	InputStream is = new FileInputStream(src);
	            OutputStream os = new FileOutputStream(newPhoto);
	            byte[] buff=new byte[1024];
	            int len;
	            while((len=is.read(buff))>0){
	                os.write(buff,0,len);
	            }
	            is.close();
	            os.close();
	            
	            checkIfPhotosExist();

				Display display = getActivity().getWindowManager()
						.getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);

				drawableManager
						.addDrawableToManager(newPhoto.getAbsolutePath(),
								drawableManager.resizeBitmapForDrawable(
										((BitmapDrawable)Drawable.createFromPath(newPhoto.getAbsolutePath())).getBitmap(),
										size.x, size.y));

				// reload listview
				listAdapter.notifyDataSetChanged();

				Log.e(ItemPhotosFragment.class.getName()
						+ ".onActivityResult()", "PHOTO ADDED");
        	} catch(Exception e) {
        		e.printStackTrace();
    			Log.e(ItemPhotosFragment.class.getName()
    					+ ".onActivityResult()", "CAUGHT EXCEPTION");
        	}
        }
	}

	private OnItemSelectedListener itemPhotoSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> av, View v, int position,
				long id) {
			sendBroadcastWithDrawableAndFilter(itemPhotos.get(position)
					.getAbsolutePath(), ITEM_INTENT_FILTER);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};
}