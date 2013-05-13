package com.boztalay.picturewidget.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.boztalay.picturewidget.R;
import com.boztalay.picturewidget.configuration.PictureWidgetConfigurationActivity;

public class PictureWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		for(int i = 0; i < appWidgetIds.length; i++) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.picture_widget);
		    Intent configIntent = new Intent(context, PictureWidgetConfigurationActivity.class);
		    
		    Uri.withAppendedPath(Uri.parse("pw" + i + "://widget/id/"), String.valueOf(appWidgetIds[i]));
		    configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
		    
		    PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
	
		    remoteViews.setOnClickPendingIntent(R.id.picture_widget_parent, configPendingIntent);
		    appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		}
	}
}
