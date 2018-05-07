package com.hireboss.android.roachlabs;
/*
 *Author:Gurdev Singh
 *date  :8 jan 2016
 *version:1.0
 */
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.hireboss.android.roachlabs.Util.DialogMediaUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class DialogMediaActivity extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		getWindow().setDimAmount(1);
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));


		DialogMediaUtils dialog = new DialogMediaUtils(this);
		dialog.showDialog(DialogMediaActivity.this);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
