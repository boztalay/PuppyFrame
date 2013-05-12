package com.boztalay.picturewidget.persistence;

public class PictureWidgetPersistenceManager {
	private static PictureWidgetPersistenceManager sharedInstance;
	
	public static PictureWidgetPersistenceManager sharedInstance() {
		if(sharedInstance == null) {
			sharedInstance = new PictureWidgetPersistenceManager();
		}
		return sharedInstance;
	}
	
	public PictureWidgetPersistenceManager() {
		
	}
}
