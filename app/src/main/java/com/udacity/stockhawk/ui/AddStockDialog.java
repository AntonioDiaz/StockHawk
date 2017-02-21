package com.udacity.stockhawk.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class AddStockDialog extends DialogFragment {

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.dialog_stock)
	EditText stock;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		@SuppressLint("InflateParams") View custom = inflater.inflate(R.layout.add_stock_dialog, null);

		ButterKnife.bind(this, custom);


		stock.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				addStock();
				return true;
			}
		});
		builder.setView(custom);
		builder.setMessage(getString(R.string.dialog_title));
		builder.setPositiveButton(getString(R.string.dialog_add),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						addStock();
					}
				});
		builder.setNegativeButton(getString(R.string.dialog_cancel), null);
		Dialog dialog = builder.create();
		Window window = dialog.getWindow();
		if (window != null) {
			window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}

		return dialog;
	}

	private void addStock() {
		new CheckStockAsyncTask(getActivity()).execute(stock.getText().toString());

	}

	public class CheckStockAsyncTask extends AsyncTask<String, Void, Boolean> {

		Context mContext;

		public CheckStockAsyncTask(Context mContext) {
			this.mContext = mContext;
		}

		@Override
		protected Boolean doInBackground(String... strings) {
			Boolean existsStock = false;
			try {
				if ((strings != null && strings.length > 0)) {
					Stock newStock = YahooFinance.get(strings[0].toString());
					Timber.d("stock " + newStock);
					if (newStock != null && newStock.getName()!=null && newStock.getQuote().getPrice() != null) {
						existsStock = true;
					}
				}
			} catch (IOException e) {
				Timber.e(e, "Error fetching stock quotes");
			}
			return existsStock;
		}

		@Override
		protected void onPostExecute(Boolean exists) {
			if (exists && mContext instanceof MainActivity) {
				((MainActivity) mContext).addStock(stock.getText().toString());
			} else {
				Toast.makeText(mContext, mContext.getString(R.string.toast_stock_name_not_found), Toast.LENGTH_SHORT).show();
			}
			dismissAllowingStateLoss();
		}
	}
}