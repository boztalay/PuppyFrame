package com.boztalay.picturewidget.persistence.album;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlbumParser {
	private static final String ID_JSON_KEY = "id";
	private static final String	TITLE_JSON_KEY = "title";
	private static final String THUMBNAIL_PATH_JSON_KEY = "thumbnailPath";
	private static final String IMAGE_PATHS_JSON_KEY = "imagePaths";
	
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
		} catch(JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("There was a problem creating JSON for an album!");
		}
		
		return albumJson.toString();
	}
}
