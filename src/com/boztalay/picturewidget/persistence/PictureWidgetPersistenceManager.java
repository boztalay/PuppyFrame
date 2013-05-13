package com.boztalay.picturewidget.persistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

import com.boztalay.picturewidget.R;
import com.boztalay.picturewidget.persistence.album.PictureWidgetAlbum;
import com.boztalay.picturewidget.persistence.album.PictureWidgetAlbumParser;

public class PictureWidgetPersistenceManager {
	private static final String ALBUM_IDS_KEY = "albumIds";

	private SharedPreferences sharedPrefs;

	private Set<String> albumIds;
	private Map<String, PictureWidgetAlbum> albums;

	public PictureWidgetPersistenceManager(Context context) {
		this.sharedPrefs = context.getSharedPreferences(context.getString(R.string.shared_prefs_name), Context.MODE_PRIVATE);
	}

	public Set<String> getAlbumIds() {
		loadAlbumIdsFromSharedPrefsIfNeeded();

		return albumIds;
	}
	
	private void loadAlbumIdsFromSharedPrefsIfNeeded() {
		if(albumIds == null) {
			albumIds = sharedPrefs.getStringSet(ALBUM_IDS_KEY, null);
			if(albumIds == null) {
				albumIds = new HashSet<String>();
			}
		}
	}

	public PictureWidgetAlbum getAlbumWithId(String albumId) {
		if(albums == null) {
			albums = new HashMap<String, PictureWidgetAlbum>();
		}

		if(albums.containsKey(albumId)) {
			return albums.get(albumId);
		} else {
			String albumJson = sharedPrefs.getString(albumId, null);
			if(albumJson == null) {
				throw new RuntimeException("Couldn't find JSON stored for album " + albumId);
			}

			PictureWidgetAlbum album = PictureWidgetAlbumParser.parseFromJsonRepresentation(albumJson);
			albums.put(albumId, album);
			
			return album;
		}
	}
	
	public void saveAlbum(PictureWidgetAlbum album) {
		loadAlbumIdsFromSharedPrefsIfNeeded();
		
		if(!albumIds.contains(album.getId())) {
			albumIds.add(album.getId());
		}
		albums.put(album.getId(), album);
		
		sharedPrefs.edit().putString(album.getId(), PictureWidgetAlbumParser.makeJsonRepresentation(album))
						  .putStringSet(ALBUM_IDS_KEY, albumIds)
						  .commit();
	}
}
