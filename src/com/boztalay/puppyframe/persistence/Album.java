package com.boztalay.puppyframe.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Album {
	private String id;
	private String title;
	private String thumbnailPath;
    private ArrayList<String> imagePaths;
    private Map<String, String> cachedImagePaths;
	
	public Album(String id, String title, String thumbnailPath) {
		this.id = id;
		this.title = title;
		this.thumbnailPath = thumbnailPath;
		
		this.imagePaths = new ArrayList<String>();
        this.cachedImagePaths = new HashMap<String, String>();
	}
	
	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	
	public ArrayList<String> getImagePaths() {
		return imagePaths;
	}
	
	public void addImagePath(String imagePath) {
		imagePaths.add(imagePath);
	}
	
	public void removeImagePath(String imagePath) {
        if(isImageCached(imagePath)) {
            String cachedImagePath = cachedImagePaths.get(imagePath);
            cachedImagePaths.remove(imagePath);
            imagePaths.remove(cachedImagePath);
        } else {
            imagePaths.remove(imagePath);
        }
	}

    public Map<String, String> getCachedImagePaths() {
        return cachedImagePaths;
    }

    public void cacheImagePath(String imagePath, String cachedImagePath) {
        if(!cachedImagePaths.containsValue(cachedImagePath)) {
            cachedImagePaths.put(imagePath, cachedImagePath);
        }
    }

    public boolean isImageCached(String imagePath) {
        return cachedImagePaths.containsKey(imagePath);
    }
}
