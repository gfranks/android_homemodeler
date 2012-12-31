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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.TextView;

public class HomePhotosFragment extends BaseFragment implements HMHandler {

	private HMTiles homePhotoTiles;
	private TextView homeNoPhotosTV;
	private ListView homeListView;
	private ArrayList<File> homePhotos;
	private PhotoListAdapter listAdapter;

	public HomePhotosFragment() {
	}

	public HomePhotosFragment(Context context) {
		loadManagers(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_photos, container, false);

		homePhotoTiles = (HMTiles) view.findViewById(R.id.home_photo_tiles);
		homePhotoTiles.setTileHandler(this);

		homeListView = (ListView) view.findViewById(R.id.homeListView);
		homeNoPhotosTV = (TextView) view.findViewById(R.id.home_nophotos);

		checkIfPhotosExist();
		listAdapter = new PhotoListAdapter(getActivity(), R.layout.photo_row,
				convertPhotoFileArrayToStringArray(homePhotos));
		homeListView.setAdapter(listAdapter);
		homeListView.setOnItemSelectedListener(homePhotoSelectedListener);

		return view;
	}

	@Override
	public void checkIfPhotosExist() {
		loadPhotos();
		if (homePhotos.size() > 0) {
			homeNoPhotosTV.setVisibility(View.INVISIBLE);
			homeListView.setVisibility(View.VISIBLE);
		} else {
			homeNoPhotosTV.setVisibility(View.VISIBLE);
			homeListView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void loadPhotos() {
		homePhotos = new ArrayList<File>();
		File[] photos = getActivity().getDir(HOME_FILE_DIR,
				Context.MODE_PRIVATE).listFiles();
		for (File f : photos) {
			homePhotos.add(f);
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
		if (homePhotoTiles.btnEditPhotos.getText().toString()
				.equals(getActivity().getString(R.string.photo_tiles_edit))) {
			if (homePhotos.size() > 0) {
				homePhotoTiles.btnEditPhotos.setText(getActivity().getString(
						R.string.photo_tiles_done));
				listAdapter.notifyDataSetChanged(true);
			}
		} else {

			// delete photos here
			ArrayList<Integer> itemsForDeletion = listAdapter
					.getItemsForDeletion();

			for (Integer i : itemsForDeletion) {
				File photo = homePhotos.get(i);
				homePhotos.remove(i);
				photo.delete();
				checkIfPhotosExist();
			}

			homePhotoTiles.btnEditPhotos.setText(getActivity().getString(
					R.string.photo_tiles_edit));
			listAdapter.notifyDataSetChanged(false);
		}
	}
	
	private void loadTakenPhoto(Intent data) {
		Bitmap photo = (Bitmap) data.getExtras().get("data");
		try {
			File dir = getActivity().getDir(HOME_FILE_DIR,
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

			Log.e(HomePhotosFragment.class.getName()
					+ ".onActivityResult()", "PHOTO ADDED");
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(HomePhotosFragment.class.getName()
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
        		File dir = getActivity().getDir(HOME_FILE_DIR,
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

				Log.e(HomePhotosFragment.class.getName()
						+ ".onActivityResult()", "PHOTO ADDED");
        	} catch(Exception e) {
        		e.printStackTrace();
    			Log.e(HomePhotosFragment.class.getName()
    					+ ".onActivityResult()", "CAUGHT EXCEPTION");
        	}
        }
	}

	private OnItemSelectedListener homePhotoSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> av, View v, int position,
				long id) {
			sendBroadcastWithDrawableAndFilter(homePhotos.get(position)
					.getAbsolutePath(), HOME_INTENT_FILTER);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};
}
