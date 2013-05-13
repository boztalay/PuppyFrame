package com.boztalay.picturewidget.configuration;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.boztalay.picturewidget.R;

public class PictureWidgetConfigurationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration);
		
		int appWidgetId = getAppWidgetId();
		Intent configurationResult = createConfigurationResultIntent(appWidgetId);
		
		updateAppWidget(appWidgetId);
		
		setResult(RESULT_OK, configurationResult);
		finish();
	}
	
	private int getAppWidgetId() {
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if(extras == null) {
			throw new RuntimeException("Couldn't find the widget id!");
		}

		int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
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
