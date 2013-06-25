package com.boztalay.puppyframeuid.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PuppyFrameWidgetProvider extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        Log.d("PuppyFrame", "onEnabled called, starting ScreenOnService");

        Intent serviceIntent = new Intent(context, ScreenOnService.class);
        context.startService(serviceIntent);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d("PuppyFrame", "onDisabled called, stopping ScreenOnService");

        Intent serviceIntent = new Intent(context, ScreenOnService.class);
        context.stopService(serviceIntent);
    }

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.d("PuppyFrame", "onUpdate called");
	}
}
