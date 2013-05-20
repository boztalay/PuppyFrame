package com.boztalay.puppyframe.configuration;

import android.app.ActionBar;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.boztalay.puppyframe.R;
import com.boztalay.puppyframe.persistence.Album;
import com.boztalay.puppyframe.persistence.PuppyFramePersistenceManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class AlbumsActivity extends Activity implements AdapterView.OnItemClickListener {
	private static final int EDIT_ALBUM_ACTIVITY_REQUEST_CODE = 1;
	private static final int FADE_DURATION_MILLIS = 75;
	
	private PuppyFramePersistenceManager persistenceManager;
	private Album currentAlbum;

    private AlbumsAdapter albumsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_albums);

		persistenceManager = new PuppyFramePersistenceManager(this);

        initializeUniversalImageLoader();
        setUpViewsAndTitle();
		prepareAndUpdateWidget();
		
		//TODO make sure you update the widget before exiting!
	}
	
	private void setUpViewsAndTitle() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setTitle(getString(R.string.albums_title));
        }

        View currentAlbumView = findViewById(R.id.current_album);

		if(persistenceManager.getAlbumIds().size() == 0) {
			setUpViewsForNoAlbums(currentAlbumView);
		} else {
			setUpViewsForAlbums(currentAlbumView);
		}

        currentAlbumView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditAlbumActivity();
            }
        });

        GridView albumsGrid = (GridView) findViewById(R.id.albums_grid);
        albumsAdapter = new AlbumsAdapter(this);
        albumsGrid.setAdapter(albumsAdapter);
        albumsGrid.setOnItemClickListener(this);
	}
	
	private void setUpViewsForNoAlbums(View currentAlbumView) {
		currentAlbum = null;
		
		ImageView currentAlbumThumbnail = (ImageView)currentAlbumView.findViewById(R.id.album_thumbnail);
		currentAlbumThumbnail.setImageResource(R.drawable.missing_picture_default);
		
		TextView currentAlbumTitle = (TextView)currentAlbumView.findViewById(R.id.album_title);
		currentAlbumTitle.setText("Couldn't find any albums!");
	}
	
	private void startEditAlbumActivity() {
		Intent editAlbumIntent = new Intent(AlbumsActivity.this, EditAlbumActivity.class);
		if(currentAlbum != null) {
			editAlbumIntent.putExtra(EditAlbumActivity.ALBUM_ID_KEY, currentAlbum.getId());
		}
		
        startActivityForResult(editAlbumIntent, EDIT_ALBUM_ACTIVITY_REQUEST_CODE);
	}

    private void startEditAlbumActivityForNewAlbum() {
        Intent editAlbumIntent = new Intent(AlbumsActivity.this, EditAlbumActivity.class);
        startActivityForResult(editAlbumIntent, EDIT_ALBUM_ACTIVITY_REQUEST_CODE);
    }

    private void setUpViewsForAlbums() {
        View currentAlbumView = findViewById(R.id.current_album);
        setUpViewsForAlbums(currentAlbumView);
    }
	
	private void setUpViewsForAlbums(View currentAlbumView) {
		String currentAlbumId = persistenceManager.getCurrentAlbumId();
        currentAlbum = persistenceManager.getAlbumWithId(currentAlbumId);

        ImageView currentAlbumThumbnail = (ImageView)currentAlbumView.findViewById(R.id.album_thumbnail);
        ImageLoader.getInstance().displayImage(currentAlbum.getThumbnailPath(), currentAlbumThumbnail);

        TextView currentAlbumTitle = (TextView)currentAlbumView.findViewById(R.id.album_title);
        currentAlbumTitle.setText(currentAlbum.getTitle());
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
        if(appWidgetManager != null) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.puppyframe_widget);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
	}
	
	private void initializeUniversalImageLoader() {
		DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
																	.cacheInMemory()
																	.displayer(new FadeInBitmapDisplayer(FADE_DURATION_MILLIS))
                                                                    .showImageForEmptyUri(R.drawable.missing_picture_default)
																	.showImageOnFail(R.drawable.missing_picture_default)
																	.build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
																	  .defaultDisplayImageOptions(displayOptions)
																	  .build();
		
		ImageLoader.getInstance().init(config);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.albums_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.add_new_album_menu_action:
		    	startEditAlbumActivityForNewAlbum();
		        return true;
		    case R.id.settings_menu_action:
		    	return true;
		}
	    return super.onOptionsItemSelected(item);
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == EDIT_ALBUM_ACTIVITY_REQUEST_CODE) {
			if(resultCode == RESULT_OK) {
                setUpViewsForAlbums();
                albumsAdapter.refreshAlbums();
			}
		}
	}
}
