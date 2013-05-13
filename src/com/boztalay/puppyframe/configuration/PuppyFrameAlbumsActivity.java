package com.boztalay.puppyframe.configuration;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.boztalay.puppyframe.R;
import com.boztalay.puppyframe.persistence.PuppyFramePersistenceManager;

public class PuppyFrameAlbumsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_albums);
		
		setUpViewsAndTitle();
		prepareAndUpdateWidget();
		
		//TODO make sure you update the widget before exiting!
	}
	
	private void setUpViewsAndTitle() {
		getActionBar().setTitle(getString(R.string.albums_title));
		
		PuppyFramePersistenceManager persistenceManager = new PuppyFramePersistenceManager(this);
		if(persistenceManager.getAlbumIds().size() == 0) {
			setUpViewsForNoAlbums();
		} else {
			setUpViewsForAlbums();
		}
	}
	
	private void setUpViewsForNoAlbums() {
		View currentAlbum = findViewById(R.id.current_album);
		
		ImageView currentAlbumThumbnail = (ImageView)currentAlbum.findViewById(R.id.album_thumbnail);
		currentAlbumThumbnail.setImageResource(R.drawable.missing_picture_default);
		
		TextView currentAlbumTitle = (TextView)currentAlbum.findViewById(R.id.album_title);
		currentAlbumTitle.setText("Couldn't find any albums!");
	}
	
	private void setUpViewsForAlbums() {
		
	}
	
	private void prepareAndUpdateWidget() {
		int appWidgetId = getAppWidgetId();
		Intent configurationResult = createConfigurationResultIntent(appWidgetId);
		updateAppWidget(appWidgetId);
		setResult(RESULT_OK, configurationResult);
	}
	
	private int getAppWidgetId() {
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if(extras == null) {
			throw new RuntimeException("Couldn't find the widget id!");
		}

		int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		if(appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			throw new RuntimeException("Couldn't find the widget id!");
		}
		
		return appWidgetId;
	}

	private Intent createConfigurationResultIntent(int appWidgetId) {
		Intent configurationResult = new Intent();
		configurationResult.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

		return configurationResult;
	}
	
	private void updateAppWidget(int appWidgetId) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.puppyframe_widget);
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.configuration_menu, menu);
	    return true;
	}
}
