package com.udacity.stockhawk.sync;

import android.content.Intent;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import timber.log.Timber;

/**
 * Created by toni on 01/03/2017.
 */

public class QuoteIntentServiceFirebase extends JobService {

	@Override
	public boolean onStartJob(JobParameters job) {
		Timber.d("Intent handled");
		Intent nowIntent = new Intent(getApplicationContext(), QuoteIntentService.class);
		getApplicationContext().startService(nowIntent);
		return true;
	}

	@Override
	public boolean onStopJob(JobParameters job) {
		return false;
	}
}
