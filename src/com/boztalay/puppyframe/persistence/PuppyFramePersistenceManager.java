package com.boztalay.puppyframe.persistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

import com.boztalay.puppyframe.R;

public class PuppyFramePersistenceManager implements SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String ALBUM_IDS_KEY = "albumIds";
	private SharedPreferences sharedPrefs;

	private Set<String> albumIds;
	private Map<String, Album> albums;

	public PuppyFramePersistenceManager(Context context) {
		this.sharedPrefs = context.getSharedPreferences(context.getString(R.string.shared_prefs_name), Context.MODE_PRIVATE);
		this.sharedPrefs.registerOnSharedPreferenceChangeListener(this);
	}

	public Set<String> getAlbumIds() {
		loadAlbumIdsFromSharedPrefsIfNeeded();

		return albumIds;
	}

	private void loadAlbumIdsFromSharedPrefsIfNeeded() {
		if(albumIds == null) {
			forceLoadAlbumIdsFromSharedPrefs();
		}

		if(albums == null) {
			albums = new HashMap<String, Album>();
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

    public Album getDefaultAlbum() {
        loadAlbumIdsFromSharedPrefsIfNeeded();

        String defaultAlbumId = (String)albumIds.toArray()[0];
        return loadAlbumWithIdFromSharedPrefsAndUpdateWorkingCopy(defaultAlbumId);
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
	
	public Album createNewAlbum(String albumTitle) {
		return new Album(generateAlbumId(), albumTitle, "");
	}

	public Album createNewAlbumAndSave(String albumTitle) {
		Album newAlbum = createNewAlbum(albumTitle);

		saveAlbum(newAlbum);

		return newAlbum;
	}

	private String generateAlbumId() {
		Random rand = new Random();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(SystemClock.elapsedRealtime());
		stringBuilder.append('-');
		stringBuilder.append(rand.nextLong());

		return stringBuilder.toString();
	}

	public void deleteAlbum(Album album) {
		loadAlbumIdsFromSharedPrefsIfNeeded();

		albumIds.remove(album.getId());
		albums.remove(album);

		sharedPrefs.edit().remove(album.getId()).putStringSet(ALBUM_IDS_KEY, albumIds).commit();
	}

	public void setCurrentAlbumForAppWidgetId(Album album, int appWidgetId) {
		sharedPrefs.edit().putString(String.valueOf(appWidgetId), album.getId()).commit();
	}

	public String getCurrentAlbumIdForAppWidgetId(int appWidgetId) {
		return sharedPrefs.getString(String.valueOf(appWidgetId), null);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadAlbumIdsFromSharedPrefsIfNeeded();

		if(key.equals(ALBUM_IDS_KEY)) {
			forceLoadAlbumIdsFromSharedPrefs();
		} else if(albumIds.contains(key)) {
			loadAlbumWithIdFromSharedPrefsAndUpdateWorkingCopy(key);
		}
	}
}
