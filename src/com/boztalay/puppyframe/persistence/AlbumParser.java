package com.boztalay.puppyframe.persistence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AlbumParser {
	private static final String ID_JSON_KEY = "id";
	private static final String	TITLE_JSON_KEY = "title";
	private static final String THUMBNAIL_PATH_JSON_KEY = "thumbnailPath";
    private static final String IMAGE_PATHS_JSON_KEY = "imagePaths";
    private static final String CACHED_IMAGE_PATHS_JSON_KEY = "cachedImagePaths";
    private static final String IMAGE_PATH_JSON_KEY = "imagePath";
    private static final String CACHED_IMAGE_PATH_JSON_KEY = "cachedImagePath";
	
	public static Album parseFromJsonRepresentation(String jsonRepresentation) {
		try {
			JSONObject albumJson = new JSONObject(jsonRepresentation);
			Album album = new Album(albumJson.getString(ID_JSON_KEY),
									albumJson.getString(TITLE_JSON_KEY),
									albumJson.getString(THUMBNAIL_PATH_JSON_KEY));
			
			if(albumJson.has(IMAGE_PATHS_JSON_KEY)) {
				JSONArray imagePathsJson = albumJson.getJSONArray(IMAGE_PATHS_JSON_KEY);
				for(int i = 0; i < imagePathsJson.length(); i++) {
					album.addImagePath(imagePathsJson.getString(i));
				}
			}

            if(albumJson.has(CACHED_IMAGE_PATHS_JSON_KEY)) {
                JSONArray cachedImagePathsJson = albumJson.getJSONArray(CACHED_IMAGE_PATHS_JSON_KEY);
                for(int i = 0; i < cachedImagePathsJson.length(); i++) {
                    JSONObject cachedImagePathJson = cachedImagePathsJson.getJSONObject(i);
                    album.cacheImagePath(cachedImagePathJson.getString(IMAGE_PATH_JSON_KEY),
                                         cachedImagePathJson.getString(CACHED_IMAGE_PATH_JSON_KEY));
                }
            }
			
			return album;
		} catch(JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("There was a problem parsing JSON for an album!");
		}
	}
	
	public static String makeJsonRepresentation(Album album) {
		JSONObject albumJson = new JSONObject();
		
		try {
			albumJson.accumulate(ID_JSON_KEY, album.getId());
			albumJson.accumulate(TITLE_JSON_KEY, album.getTitle());
			albumJson.accumulate(THUMBNAIL_PATH_JSON_KEY, album.getThumbnailPath());
			
			JSONArray imagePathsJson = new JSONArray(album.getImagePaths());
			albumJson.accumulate(IMAGE_PATHS_JSON_KEY, imagePathsJson);

            JSONArray cachedImagePathsJson = new JSONArray();
            Set<Entry<String, String>> cachedImagePathEntries = album.getCachedImagePaths().entrySet();
            for(Entry<String, String> cachedImagePathEntry : cachedImagePathEntries) {
                JSONObject cachedImagePathEntryJson = new JSONObject();
                cachedImagePathEntryJson.accumulate(IMAGE_PATH_JSON_KEY, cachedImagePathEntry.getKey());
                cachedImagePathEntryJson.accumulate(CACHED_IMAGE_PATH_JSON_KEY, cachedImagePathEntry.getValue());
                cachedImagePathsJson.put(cachedImagePathEntryJson);
            }
            albumJson.accumulate(CACHED_IMAGE_PATHS_JSON_KEY, cachedImagePathsJson);

		} catch(JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("There was a problem creating JSON for an album!");
		}
		
		return albumJson.toString();
	}
}
