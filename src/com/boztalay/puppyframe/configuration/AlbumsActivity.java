package com.boztalay.puppyframe.configuration;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.boztalay.puppyframe.R;
import com.boztalay.puppyframe.persistence.Album;
import com.boztalay.puppyframe.persistence.PuppyFramePersistenceManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class AlbumsActivity extends Activity {
	private static final int EDIT_ALBUM_ACTIVITY_REQUEST_CODE = 1;
	private static final int FADE_DURATION_MILLIS = 100;
	
	private PuppyFramePersistenceManager persistenceManager;
	private Album currentAlbum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_albums);
		getActionBar().setTitle(getString(R.string.albums_title));
		
		persistenceManager = new PuppyFramePersistenceManager(this);
		
		setUpViews();
		prepareAndUpdateWidget();
		
		initializeUniversalImageLoader();
		
		//TODO make sure you update the widget before exiting!
	}
	
	private void setUpViews() {
		if(persistenceManager.getAlbumIds().size() == 0) {
			setUpViewsForNoAlbums();
		} else {
			setUpViewsForAlbums();
		}
	}
	
	private void setUpViewsForNoAlbums() {
		currentAlbum = null;
		
		View currentAlbum = findViewById(R.id.current_album);
		
		ImageView currentAlbumThumbnail = (ImageView)currentAlbum.findViewById(R.id.album_thumbnail);
		currentAlbumThumbnail.setImageResource(R.drawable.missing_picture_default);
		
		TextView currentAlbumTitle = (TextView)currentAlbum.findViewById(R.id.album_title);
		currentAlbumTitle.setText("Couldn't find any albums!");
		
		currentAlbum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startEditAlbumActivity();
			}
		});
	}
	
	private void startEditAlbumActivity() {
		Intent editAlbumIntent = new Intent(AlbumsActivity.this, EditAlbumActivity.class);
		if(currentAlbum != null) {
			editAlbumIntent.putExtra(EditAlbumActivity.ALBUM_ID_KEY, currentAlbum.getId());
		}
		
        startActivityForResult(editAlbumIntent, EDIT_ALBUM_ACTIVITY_REQUEST_CODE);
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
	
	private void initializeUniversalImageLoader() {
		DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
																	.cacheInMemory()
																	.displayer(new FadeInBitmapDisplayer(FADE_DURATION_MILLIS))
																	.showImageOnFail(R.drawable.missing_picture_default)
																	.build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
																	  .defaultDisplayImageOptions(displayOptions)
																	  .build();
		
		ImageLoader.getInstance().init(config);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.albums_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.add_new_album_menu_action:
		    	startEditAlbumActivity();
		        return true;
		    case R.id.settings_menu_action:
		    	return true;
		}
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == RESULT_OK) {
			if(resultCode == EDIT_ALBUM_ACTIVITY_REQUEST_CODE) {
				//TODO refresh the current album and gridview
			}
		}
	}
}
