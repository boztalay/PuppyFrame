package com.boztalay.puppyframe.persistence;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

import com.boztalay.puppyframe.R;

public class PuppyFramePersistenceManager implements SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String ALBUM_IDS_KEY = "albumIds";
	private static final String CURRENT_ALBUM_KEY = "currentAlbum";

	private SharedPreferences sharedPrefs;

	private Set<String> albumIds;
	private Map<String, Album> albums;
	private String currentAlbum;

	public PuppyFramePersistenceManager(Context context) {
		this.sharedPrefs = context.getSharedPreferences(context.getString(R.string.shared_prefs_name), Context.MODE_PRIVATE);
	}

	public Set<String> getAlbumIds() {
		loadAlbumIdsFromSharedPrefsIfNeeded();

		return albumIds;
	}
	
	private void loadAlbumIdsFromSharedPrefsIfNeeded() {
		if(albumIds == null) {
			forceLoadAlbumIdsFromSharedPrefs();
		}
	}
	
	private void forceLoadAlbumIdsFromSharedPrefs() {
		albumIds = sharedPrefs.getStringSet(ALBUM_IDS_KEY, null);
		if(albumIds == null) {
			albumIds = new HashSet<String>();
		}
	}

	public Album getAlbumWithId(String albumId) {
		loadAlbumIdsFromSharedPrefsIfNeeded();

		if(albums.containsKey(albumId)) {
			return albums.get(albumId);
		} else {
			return loadAlbumWithIdFromSharedPrefsAndUpdateWorkingCopy(albumId);
		}
	}
	
	private Album loadAlbumWithIdFromSharedPrefsAndUpdateWorkingCopy(String albumId) {
		String albumJson = sharedPrefs.getString(albumId, null);
		if(albumJson == null) {
			throw new RuntimeException("Couldn't find JSON stored for album " + albumId);
		}

		Album album = AlbumParser.parseFromJsonRepresentation(albumJson);
		albums.put(albumId, album);
		
		return album;
	}
	
	public void saveAlbum(Album album) {
		loadAlbumIdsFromSharedPrefsIfNeeded();
		
		if(!albumIds.contains(album.getId())) {
			albumIds.add(album.getId());
		}
		albums.put(album.getId(), album);
		
		sharedPrefs.edit().putString(album.getId(), AlbumParser.makeJsonRepresentation(album))
						  .putStringSet(ALBUM_IDS_KEY, albumIds)
						  .commit();
	}
	
	public void setCurrentAlbum(Album album) {
		sharedPrefs.edit().putString(CURRENT_ALBUM_KEY, album.getId()).commit();
	}
	
	public String getCurrentAlbumId() {
		if(currentAlbum == null) {
			forceLoadCurrentAlbumFromSharedPrefs();
		}
		
		return currentAlbum;
	}
	
	private void forceLoadCurrentAlbumFromSharedPrefs() {
		currentAlbum = sharedPrefs.getString(CURRENT_ALBUM_KEY, null);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals(ALBUM_IDS_KEY)) {
			forceLoadAlbumIdsFromSharedPrefs();
		} else if(key.equals(CURRENT_ALBUM_KEY)) {
			forceLoadCurrentAlbumFromSharedPrefs();
		} else if(albumIds.contains(key)) {
			loadAlbumWithIdFromSharedPrefsAndUpdateWorkingCopy(key);
		}
	}
}
