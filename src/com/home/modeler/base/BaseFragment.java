package com.home.modeler.base;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import com.home.modeler.R;
import com.home.modeler.utils.DrawableManager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public abstract class BaseFragment extends Fragment {

	private int MAX_FILENAME_LENGTH = 100;
	public final int HOME_PHOTO_RESULT_CODE = 101;
	public final int ITEM_PHOTO_RESULT_CODE = 102;
	public static final String HOME_FILE_DIR = "HomePhotos";
	public static final String ITEM_FILE_DIR = "ItemPhotos";
	public static final String HOME_INTENT_FILTER = "HomePhotoSelected";
	public static final String ITEM_INTENT_FILTER = "ItemPhotoSelected";
	public static final String Drawable_IntentKey = "drawable_source";
	public DrawableManager drawableManager;

	public void loadDrawableManager() {
		drawableManager = DrawableManager.getInstance();
	}

	public static enum kFragmentShowing {
		kFragmentShowingIsHomePhotos, kFragmentShowingIsModeler, kFragmentShowingIsItemPhotos;
	}

	public void didShowFragment(kFragmentShowing fragmentShowing) {
		switch (fragmentShowing) {
		case kFragmentShowingIsHomePhotos:
			// home photos fragment showing

			break;
		case kFragmentShowingIsModeler:
			// modeler fragment showing

			break;
		case kFragmentShowingIsItemPhotos:
			// item fragment showing

			break;
		default:
			break;
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.splash, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public void takePic(int resultCode) {
		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, resultCode);
	}

	public String generateRandomFilename() {
		Random generator = new Random();
		StringBuilder randomStringBuilder = new StringBuilder();
		int randomLength = generator.nextInt(MAX_FILENAME_LENGTH);
		char tempChar;
		for (int i = 0; i < randomLength; i++) {
			int rnd = (int) (Math.random() * 52);
			tempChar = (rnd < 26) ? 'A' : 'a';
			randomStringBuilder.append((char) (tempChar + rnd % 26));
		}
		return randomStringBuilder.toString();
	}
	
	public ArrayList<String> convertPhotoFileArrayToStringArray(
			ArrayList<File> files) {
		ArrayList<String> items = new ArrayList<String>();
		for (File f : files) {
			items.add(f.getAbsolutePath());
		}

		return items;
	}

	public void sendBroadcastWithDrawableAndFilter(String drawableSource,
			String filterType) {
		Intent intent = new Intent(filterType);
		intent.putExtra(Drawable_IntentKey, drawableSource);
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
	}

	public class PhotoListAdapter extends ArrayAdapter<String> {

		private ArrayList<String> items;
		private Context mContext;
		private ArrayList<Integer> itemsForDeletion;
		private boolean isEditing;

		public PhotoListAdapter(Context context, int textViewResourceId,
				ArrayList<String> items) {
			super(context, textViewResourceId, items);
			this.items = items;
			this.mContext = context;
			itemsForDeletion = new ArrayList<Integer>();
			isEditing = false;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.photo_row, null);
			}
			String path = items.get(position);
			if (path != null) {
				ImageView photoImageView = (ImageView) v
						.findViewById(R.id.photoImageView);
				photoImageView.setImageDrawable(drawableManager
						.fetchDrawable(path));

				if (isEditing) {
					CheckBox edit = (CheckBox) v
							.findViewById(R.id.photoImageEdit);
					edit.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton cv,
								boolean isChecked) {
							if (isChecked) {
								if (!itemsForDeletion.contains(position))
									itemsForDeletion.add(position);
								// if (deleteButton != null) {
								// deleteButton.setEnabled(true);
								// }
							} else {
								if (itemsForDeletion.contains(position))
									itemsForDeletion.remove(position);
								// if (deleteButton != null &&
								// mListingsForDeletion.isEmpty()) {
								// deleteButton.setEnabled(false);
								// }
							}
						}
					});
					if (isEditing) {
						edit.setVisibility(View.VISIBLE);
					} else {
						edit.setVisibility(View.GONE);
					}
				}
			}
			return v;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		public void notifyDataSetChanged(boolean isEditing) {
			this.isEditing = isEditing;
			notifyDataSetChanged();
		}

		public ArrayList<Integer> getItemsForDeletion() {
			return itemsForDeletion;
		}
	}

	public abstract void loadPhotos();

	public abstract void checkIfPhotosExist();
}