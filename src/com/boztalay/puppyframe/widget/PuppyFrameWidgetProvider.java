package com.boztalay.puppyframe.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.boztalay.puppyframe.R;
import com.boztalay.puppyframe.configuration.albums.AlbumsActivity;
import com.boztalay.puppyframe.persistence.PuppyFramePersistenceManager;

public class PuppyFrameWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

        updateAllWidgets(context, appWidgetManager, appWidgetIds);
	}

    private void updateAllWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        PuppyFramePersistenceManager persistenceManager = new PuppyFramePersistenceManager(context);

        for(int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.puppyframe_widget);
            Intent configIntent = new Intent(context, AlbumsActivity.class);

            Uri.withAppendedPath(Uri.parse("pw" + i + "://widget/id/"), String.valueOf(appWidgetId));
            configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.picture_widget_parent, configPendingIntent);

            String currentAlbumId = persistenceManager.getCurrentAlbumIdForAppWidgetId(appWidgetId);
            if(currentAlbumId != null) {
                Uri imageUri = Uri.parse(persistenceManager.getAlbumWithId(currentAlbumId).getImagePaths().get(0));
                remoteViews.setImageViewUri(R.id.the_picture, imageUri);
            }

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
