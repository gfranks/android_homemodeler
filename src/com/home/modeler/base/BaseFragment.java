package com.home.modeler.base;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import com.home.modeler.R;
import com.home.modeler.utils.DrawableManager;
import com.home.modeler.utils.HMConstants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
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

	private Context mContext;
	private int MAX_FILENAME_LENGTH = 100;
	public DrawableManager drawableManager;
	public LocalBroadcastManager broadcastManager;

	public void loadManagers(Context context) {
		this.mContext = context;
		drawableManager = DrawableManager.getInstance(mContext);
		broadcastManager = LocalBroadcastManager
				.getInstance(mContext);
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

	public void takePic() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Select Method");
		builder.setMessage("Would you like to take a new photo or select one?");
		builder.setCancelable(true);
		builder.setNegativeButton("Take New", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(
							DialogInterface dialog, int pos) {
						Intent cameraIntent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(cameraIntent, HMConstants.PHOTOTAKEN_RESULT_CODE);
					}
				});
		builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(
					DialogInterface dialog, int pos) {
				Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), HMConstants.PHOTOSELECTED_RESULT_CODE);
			}
		});
		builder.show();
	}
	
	public String getPathFromSelectedPhoto(Uri uri) {
		String selectedPhotoPath = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            selectedPhotoPath = cursor.getString(column_index);
        }
        
        return selectedPhotoPath;
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
		intent.putExtra(HMConstants.Drawable_IntentKey, drawableSource);
		broadcastManager.sendBroadcast(intent);
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
				Drawable image = drawableManager
						.fetchDrawable(path);
				
				if (image != null) {
					photoImageView.setImageDrawable(image);
				} else {
					photoImageView.setImageResource(R.drawable.no_photo_available);
				}

				if (isEditing) {
					CheckBox edit = (CheckBox) v
							.findViewById(R.id.photoImageEdit);
					edit.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton cv,
								boolean isChecked) {
							if (isChecked) {
								if (!itemsForDeletion.contains(position)) {
									itemsForDeletion.add(position);
								}
							} else {
								if (itemsForDeletion.contains(position)) {
									itemsForDeletion.remove(position);
								}
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