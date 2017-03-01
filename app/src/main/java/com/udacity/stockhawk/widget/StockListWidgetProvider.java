package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;

import timber.log.Timber;

/**
 * Created by toni on 01/03/2017.
 */

public class StockListWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Timber.d("onUpdate ");
		for (int appWidgetId : appWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_stocks);

			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			remoteViews.setOnClickPendingIntent(R.id.widget_list_header, pendingIntent);

			/* setting the adapter. */
			remoteViews.setRemoteAdapter(R.id.widget_stock_listview, new Intent(context, StockListWidgetRemoteViewsService.class));
			remoteViews.setEmptyView(
					R.id.widget_stock_listview, R.id.widget_detail_list_empty);

			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Timber.d("onReceive " + intent.getAction());
		if (QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())) {
			Timber.d("onReceive in");
			AppWidgetManager appWidgetManager =	AppWidgetManager.getInstance(context);
			int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_stock_listview);
		}
	}
}
