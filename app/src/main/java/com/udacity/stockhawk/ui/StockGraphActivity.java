package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StockGraphActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.swipe_refresh_graph)
	SwipeRefreshLayout swipeRefreshLayout;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.chart)
	LineChart lineChart;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_graph);
		ButterKnife.bind(this);
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setRefreshing(true);
		String symbol = getIntent().getStringExtra(MainActivity.STOCK_SYMBOL);
		setTitle(getString(R.string.stock_graph_title, symbol));
		onRefresh();
	}

	@Override
	public void onRefresh() {
		String symbol = getIntent().getStringExtra(MainActivity.STOCK_SYMBOL);
		Timber.d("onCreate %s", symbol);
		Uri uri = Contract.Quote.URI.buildUpon().appendPath(symbol).build();
		String[] projection = Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{});
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		Timber.d("onCreate " + cursor.getCount());
		cursor.moveToFirst();
		String historyStr = cursor.getString(Contract.Quote.POSITION_HISTORY);
		List<Entry> entries = new ArrayList<Entry>();
		String[] historyLines = historyStr.split("\n");
		float cont = 0;
		for (String historyLine : historyLines) {
			String[] split = historyLine.split(", ");
			Float x = new Float(split[0]);
			Float y = new Float(split[1]);
			entries.add(new Entry(cont++, y));
		}

		LineDataSet dataSet = new LineDataSet(entries, getString(R.string.stock_graph_label));
		int colorWhite = ContextCompat.getColor(this, R.color.colorWhite);
		LineData lineData = new LineData(dataSet);
		lineData.setValueTextColor(colorWhite);
		lineChart.setData(lineData);
		Legend legend = lineChart.getLegend();
		legend.setTextColor(colorWhite);
		lineChart.invalidate();
		lineChart.getXAxis().setTextColor(colorWhite);
		lineChart.getDescription().setTextColor(colorWhite);
		lineChart.getDescription().setText(getString(R.string.historical_position));

		lineChart.getAxisLeft().setTextColor(colorWhite);
		lineChart.getAxisRight().setTextColor(colorWhite);
		swipeRefreshLayout.setRefreshing(false);
	}
}
