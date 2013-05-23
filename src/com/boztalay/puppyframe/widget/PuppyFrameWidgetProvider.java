package com.boztalay.puppyframe.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.boztalay.puppyframe.R;
import com.boztalay.puppyframe.configuration.albums.AlbumsActivity;

public class PuppyFrameWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent serviceIntent = new Intent(context, ScreenOnService.class);
        context.startService(serviceIntent);
	}
}
