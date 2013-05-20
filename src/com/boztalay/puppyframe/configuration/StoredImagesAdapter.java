package com.boztalay.puppyframe.configuration;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.boztalay.puppyframe.R;
import com.boztalay.puppyframe.persistence.Album;
import com.nostra13.universalimageloader.core.ImageLoader;

public class StoredImagesAdapter extends BaseAdapter {
	private static final String FILE_PATH_PREFIX = "file://";

    private Album album;

	private Cursor cursor;
	private int pathColumnIndex;

	private LayoutInflater layoutInflater;

    public class PuppyFrameImageLoadingException extends Exception { }

	public StoredImagesAdapter(Context context, Album album) throws PuppyFrameImageLoadingException {
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.album = album;

	    setUpCursor(context);
	}

    private void setUpCursor(Context context) throws PuppyFrameImageLoadingException {
        ContentResolver cr = context.getContentResolver();

        String[] columns = new String[] { ImageColumns.DATA };
        cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
        if(cursor == null) {
            throw new PuppyFrameImageLoadingException();
        }

        pathColumnIndex = cursor.getColumnIndex(ImageColumns.DATA);
    }

	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		cursor.moveToPosition(position);
        String pathWithFilePrefix = FILE_PATH_PREFIX + cursor.getString(pathColumnIndex);
		return pathWithFilePrefix;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.simple_image_view, parent, false);
		} else {
			ImageLoader.getInstance().cancelDisplayTask((SelectableImageView)convertView);
			((SelectableImageView)convertView).setImageDrawable(null);
		}

		String imagePath = (String) getItem(position);
		ImageLoader.getInstance().displayImage(imagePath, (SelectableImageView)convertView);

        ((SelectableImageView)convertView).setChecked(album.getImagePaths().contains(imagePath));

		return convertView;
	}
}
