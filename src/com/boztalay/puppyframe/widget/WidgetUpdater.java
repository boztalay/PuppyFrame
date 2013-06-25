package com.boztalay.puppyframe.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import com.boztalay.puppyframe.R;
import com.boztalay.puppyframe.configuration.albums.AlbumsActivity;
import com.boztalay.puppyframe.persistence.Album;
import com.boztalay.puppyframe.persistence.PuppyFramePersistenceManager;

import java.util.Random;

public class WidgetUpdater {

    public static void updateAllWidgets(Context context) {
        Random rand = new Random();

        PuppyFramePersistenceManager persistenceManager = new PuppyFramePersistenceManager(context);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, PuppyFrameWidgetProvider.class));

        for(int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            Log.d("PuppyFrame", "WidgetUpdater: Updating widget with id: " + appWidgetId);

            String currentAlbumId = persistenceManager.getCurrentAlbumIdForAppWidgetId(appWidgetId);
            Log.d("PuppyFrame", "WidgetUpdater: Widget has album id: " + currentAlbumId);

            if(currentAlbumId != null) {
                Log.d("PuppyFrame", "WidgetUpdater: Album id wasn't null, choosing a new image and updating the widget");

                Intent configIntent = new Intent(context, AlbumsActivity.class);
                configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                configIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                configIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                configIntent.setData(Uri.parse(configIntent.toUri(Intent.URI_INTENT_SCHEME)));

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.puppyframe_widget);
                PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.picture_widget_parent, configPendingIntent);

                Album currentAlbum = persistenceManager.getAlbumWithId(currentAlbumId);
                int randomIndex = rand.nextInt(currentAlbum.getImagePaths().size());
                Log.d("PuppyFrame", "WidgetUpdater: Getting image at index " + randomIndex);

                Uri imageUri = Uri.parse(currentAlbum.getImagePaths().get(randomIndex));
                Log.d("PuppyFrame", "WidgetUpdater: Widget imageUri: " + imageUri.toString());
                remoteViews.setImageViewUri(R.id.the_picture, imageUri);

                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        }
    }
}
