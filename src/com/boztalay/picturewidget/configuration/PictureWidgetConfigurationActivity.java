package com.boztalay.picturewidget.configuration;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.RemoteViews;

import com.boztalay.picturewidget.R;

public class PictureWidgetConfigurationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration);
		getActionBar().setTitle("Albums");
		
		int appWidgetId = getAppWidgetId();
		Intent configurationResult = createConfigurationResultIntent(appWidgetId);
		updateAppWidget(appWidgetId);
		setResult(RESULT_OK, configurationResult);
		
		//TODO make sure you update the widget before exiting!
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.configuration_menu, menu);
	    return true;
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
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.picture_widget);
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
}
