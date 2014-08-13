package com.mtv.gicula;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mtv.gicula.utils.NetworkUtils;

public class MainActivity extends ActionBarActivity {
	private static final String TEL_PREFIX = "tel:";

	private static final String HTML = "http://www.gicula.com";

	private WebView wv;

	private String mimeType = "text/html";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.fragment_main);
		loadWebsite();
	}

	private void loadWebsite() {
		wv = (WebView) findViewById(R.id.webview);
		wv.setWebViewClient(new CustomWebViewClient());
		wv.getSettings().setLoadsImagesAutomatically(true);
		wv.getSettings().setJavaScriptEnabled(true);
		// wv.loadData(HTML, mimeType, Encoding.UTF_8.toString());
		wv.loadUrl(HTML);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			showAboutUs();
			return true;
		}

		if (id == R.id.action_refresh) {
			loadWebsite();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		checkNetWork();
		super.onResume();
	}

	private AlertDialog.Builder builder;

	/**
	 * Method used to show about us dialog.
	 */
	private void showAboutUs() {
		if (builder == null) {
			builder = new Builder(this);
		}
		builder.setTitle(getString(R.string.gioithieu));
		builder.setMessage(getString(R.string.about_us));
		builder.setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		Dialog dialog = builder.create();
		dialog.show();
	}

	private void checkNetWork() {
		if (NetworkUtils.getInstance(this).hasConnection()) {
			return;
		} else {
			builder = new Builder(this);
			builder.setTitle(getString(R.string.thong_bao));
			builder.setMessage(getString(R.string.check_internet_msg));
			builder.setPositiveButton(getString(R.string.ok),
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			Dialog dialog = builder.create();
			dialog.show();
		}
	}

	private class CustomWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView wv, String url) {
			if (url.startsWith(TEL_PREFIX)) {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse(url));
				startActivity(intent);
				return true;
			}
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			setProgressBarIndeterminateVisibility(false);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			setProgressBarIndeterminateVisibility(true);
		}
	}

	@Override
	public void onBackPressed() {
		if (wv != null && wv.canGoBack()) {
			wv.goBack();
		} else {
			super.onBackPressed();
		}

	}

	@Override
	public void onDestroy() {
		builder = null;
		if (wv != null) {
			wv.stopLoading();
			wv = null;
		}
		super.onDestroy();
	}

}
