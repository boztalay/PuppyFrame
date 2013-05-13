package com.boztalay.picturewidget.persistence.album;

import java.util.ArrayList;

public class Album {
	private String id;
	private String title;
	private String thumbnailPath;
	private ArrayList<String> imagePaths;
	
	public Album(String id, String title, String thumbnailPath) {
		this.id = id;
		this.title = title;
		this.thumbnailPath = thumbnailPath;
		
		this.imagePaths = new ArrayList<String>();
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
		imagePaths.remove(imagePath);
	}
}
