package com.boztalay.puppyframeuid.configuration.albums;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.boztalay.puppyframeuid.R;
import com.boztalay.puppyframeuid.persistence.Album;
import com.boztalay.puppyframeuid.persistence.PuppyFramePersistenceManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class AlbumsAdapter extends BaseAdapter {
    private PuppyFramePersistenceManager persistenceManager;
    private ArrayList<Album> albumsToDisplay;
    private int appWidgetId;

    private LayoutInflater layoutInflater;

    public AlbumsAdapter(Context context, int appWidgetId) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.appWidgetId = appWidgetId;
        persistenceManager = new PuppyFramePersistenceManager(context);
        refreshAlbums();
    }

    public void refreshAlbums() {
        albumsToDisplay = new ArrayList<Album>();
        for(String albumId : persistenceManager.getAlbumIds()) {
            if(!albumId.equals(persistenceManager.getCurrentAlbumIdForAppWidgetId(appWidgetId))) {
                Album albumToDisplay = persistenceManager.getAlbumWithId(albumId);
                albumsToDisplay.add(albumToDisplay);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return albumsToDisplay.size();
    }

    @Override
    public Object getItem(int position) {
        return albumsToDisplay.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.album_preview, parent, false);
        }

        Album album = albumsToDisplay.get(position);
        ImageView albumThumbnail = (ImageView) convertView.findViewById(R.id.album_thumbnail);
        TextView albumTitle = (TextView) convertView.findViewById(R.id.album_title);

        albumThumbnail.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(album.getThumbnailPath(), albumThumbnail);
        albumTitle.setText(album.getTitle());

        return convertView;
    }
}
