package com.boztalay.puppyframe.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;
import com.boztalay.puppyframe.R;
import com.boztalay.puppyframe.configuration.albums.AlbumsActivity;
import com.boztalay.puppyframe.persistence.PuppyFramePersistenceManager;

public class ScreenOnService extends Service {
    private AppWidgetManager appWidgetManager;
    private PuppyFramePersistenceManager persistenceManager;

    @Override
    public void onCreate() {
        super.onCreate();

        persistenceManager = new PuppyFramePersistenceManager(this);
        appWidgetManager = AppWidgetManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateAllWidgets();

        IntentFilter screenOnFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    updateAllWidgets();
                }
            }
        }, screenOnFilter);

        return START_STICKY;
    }

    private void updateAllWidgets() {
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, PuppyFrameWidgetProvider.class));
        for(int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.puppyframe_widget);
            Intent configIntent = new Intent(this, AlbumsActivity.class);

            Uri.withAppendedPath(Uri.parse("pw" + i + "://widget/id/"), String.valueOf(appWidgetId));
            configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            PendingIntent configPendingIntent = PendingIntent.getActivity(this, 0, configIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.picture_widget_parent, configPendingIntent);

            String currentAlbumId = persistenceManager.getCurrentAlbumIdForAppWidgetId(appWidgetId);
            if(currentAlbumId != null) {
                Uri imageUri = Uri.parse(persistenceManager.getAlbumWithId(currentAlbumId).getImagePaths().get(0));
                remoteViews.setImageViewUri(R.id.the_picture, imageUri);
            }

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
