package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by toni on 01/03/2017.
 */

public class StockListWidgetRemoteViewsService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return  new RemoteViewsFactory() {

			private Cursor data = null;

			@Override
			public void onCreate() {
				Timber.d("onCreate");
			}

			@Override
			public void onDataSetChanged() {
				if (data != null) {
					data.close();
				}
				final long identityToken = Binder.clearCallingIdentity();
				data = getContentResolver().query(
						Contract.Quote.URI, Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}), null, null, Contract.Quote.COLUMN_SYMBOL);
				Binder.restoreCallingIdentity(identityToken);
			}

			@Override
			public void onDestroy() {
				if (data!=null) {
					data.close();
					data = null;
				}
			}

			@Override
			public int getCount() {
				return data == null ? 0 : data.getCount();
			}

			@Override
			public RemoteViews getViewAt(int position) {
				if (position== AdapterView.INVALID_POSITION || data==null || !data.moveToPosition(position)) {
					return null;
				}
				RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_stocks_list_item);
				String stockName = data.getString(Contract.Quote.POSITION_NAME);
				DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

				String strPrice = dollarFormat.format(data.getFloat(Contract.Quote.POSITION_PRICE));

				remoteViews.setTextViewText(R.id.widget_list_item_title, stockName);
				remoteViews.setTextViewText(R.id.widget_list_item_price, strPrice);
				return remoteViews;
			}

			@Override
			public RemoteViews getLoadingView() {
				return new RemoteViews(getPackageName(), R.layout.widget_stocks_list_item);
			}

			@Override
			public int getViewTypeCount() {
				return 1;
			}

			@Override
			public long getItemId(int position) {
				if (data.moveToPosition(position))
					return data.getLong(Contract.Quote.POSITION_ID);
				return position;
			}

			@Override
			public boolean hasStableIds() {
				return true;
			}
		};
	}
}
