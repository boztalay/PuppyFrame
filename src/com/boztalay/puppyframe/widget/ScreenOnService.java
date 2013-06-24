package com.boztalay.puppyframe.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import com.boztalay.puppyframe.R;
import com.boztalay.puppyframe.configuration.albums.AlbumsActivity;
import com.boztalay.puppyframe.persistence.Album;
import com.boztalay.puppyframe.persistence.PuppyFramePersistenceManager;

import java.util.Random;

public class ScreenOnService extends Service {
    private AppWidgetManager appWidgetManager;
    private PuppyFramePersistenceManager persistenceManager;

    private Random rand;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("PuppyFrame", "ScreenOnService: onCreate called, creating the intent listener");

        persistenceManager = new PuppyFramePersistenceManager(this);
        appWidgetManager = AppWidgetManager.getInstance(this);

        rand = new Random();

        IntentFilter screenOnFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    Log.d("PuppyFrame", "ScreenOnService: Screen on intent received, updating widgets");
                    updateAllWidgets();
                }
            }
        }, screenOnFilter);
    }

    private void updateAllWidgets() {
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, PuppyFrameWidgetProvider.class));
        for(int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            Log.d("PuppyFrame", "Updating widget with id: " + appWidgetId);

            String currentAlbumId = persistenceManager.getCurrentAlbumIdForAppWidgetId(appWidgetId);
            Log.d("PuppyFrame", "Widget has album id: " + currentAlbumId);

            if(currentAlbumId != null) {
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.puppyframe_widget);
                Intent configIntent = new Intent(this, AlbumsActivity.class);

                Uri.withAppendedPath(Uri.parse("pw" + i + "://widget/id/"), String.valueOf(appWidgetId));
                configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

                PendingIntent configPendingIntent = PendingIntent.getActivity(this, 0, configIntent, 0);
                remoteViews.setOnClickPendingIntent(R.id.picture_widget_parent, configPendingIntent);

                Log.d("PuppyFrame", "Album id wasn't null");
                Album currentAlbum = persistenceManager.getAlbumWithId(currentAlbumId);
                int randomIndex = rand.nextInt(currentAlbum.getImagePaths().size());
                Log.d("PuppyFrame", "Getting image at index " + randomIndex);
                Uri imageUri = Uri.parse(currentAlbum.getImagePaths().get(randomIndex));
                Log.d("PuppyFrame", "Widget imageUri: " + imageUri.toString());
                remoteViews.setImageViewUri(R.id.the_picture, imageUri);

                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
