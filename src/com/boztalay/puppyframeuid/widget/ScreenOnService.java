package com.boztalay.puppyframeuid.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.os.IBinder;
import android.util.Log;
import com.boztalay.puppyframeuid.persistence.PuppyFramePersistenceManager;

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
                    WidgetUpdater.updateAllWidgets(context);
                }
            }
        }, screenOnFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
