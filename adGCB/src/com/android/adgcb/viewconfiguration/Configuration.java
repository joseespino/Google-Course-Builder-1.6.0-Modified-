package com.android.adgcb.viewconfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.adgcb.Constants;
import com.android.adgcb.R;
import com.android.adgcb.courses.CoursesList;
import com.android.adgcb.database.DbAdapter;
import com.google.android.gcm.GCMRegistrar;

/**
 * Class configuration
 * 
 * Show different courses registered
 * @author Jose Antonio Espino Palomares
 *
 */
public class Configuration extends Activity {

	private ListView lv;
	private DbAdapter db;
	private ArrayList<String> resultados = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private Builder alertDialogBuilder;
	private int position;
	private static ProgressDialog pd = null;
	private String identificador;
	private String direccion;
	private Toast toast3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		lv = (ListView) findViewById(R.id.listView1);

		db = new DbAdapter(this);

		db.open();
		resultados = db.loadCourses();
		if (resultados.isEmpty()) {
			toast3 = Toast.makeText(getApplicationContext(),
					R.string.nocourses,
					Toast.LENGTH_SHORT);
			toast3.show();
		} else {

			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, android.R.id.text1,
					resultados);
			lv.setAdapter(adapter);
		}

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int index, long arg3) {
				// TODO Auto-generated method stub
				position = index;
				alertDialogBuilder = new AlertDialog.Builder(Configuration.this);
				alertDialogBuilder.setTitle(R.string.deleteconfiguration);
				alertDialogBuilder.setMessage(R.string.confirmation);
				alertDialogBuilder.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								Cursor cursor = db.getCourse(adapter
										.getItem(position));

								direccion = cursor.getString(0);
								identificador = cursor.getString(1);

								pd = ProgressDialog.show(Configuration.this,
										getString(R.string.processing),
										getString(R.string.wait), true, false);

								final String regId = GCMRegistrar
										.getRegistrationId(Configuration.this);

								if (!regId.equals("")) {
									sendIdToServer(regId);
								} else {
									GCMRegistrar.register(Configuration.this,
											identificador);
								}
							}
						});
				alertDialogBuilder.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				return true;
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_new:
			return true;

		case R.id.SubMnuOpc1:
			Intent intent = new Intent(Configuration.this, CoursesList.class);
			startActivity(intent);
			finish();
			return true;	
		/*	
		case R.id.SubMnuOpc2:
			Intent intent = new Intent(Configuration.this, Register.class);
			startActivity(intent);
			finish();
			return true;
		*/
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void sendIdToServer(String regId) {
		(new SendRegistrationIdTask(regId)).execute();
	}

	@SuppressWarnings("unused")
	private class GCMReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String regId = intent
					.getStringExtra(Constants.FIELD_REGISTRATION_ID);
			sendIdToServer(regId);
		}
	}

	private final class SendRegistrationIdTask extends
			AsyncTask<String, Void, HttpResponse> {
		private String mRegId;

		public SendRegistrationIdTask(String regId) {
			mRegId = regId;
		}

		@Override
		protected HttpResponse doInBackground(String... regIds) {
			String url = direccion + "/unregister";
			Log.i("Direccion", "URL: " + url);
			HttpPost httppost = new HttpPost(url);
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 10000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			int timeoutSocket = 10000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);
				nameValuePairs.add(new BasicNameValuePair("reg_id", mRegId));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
				return httpclient.execute(httppost);
			} catch (ClientProtocolException e) {
				Log.e(Constants.TAG, e.getMessage(), e);
			} catch (IOException e) {
				Log.e(Constants.TAG, e.getMessage(), e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(HttpResponse response) {
			if (response == null) {
				Log.e(Constants.TAG, "HttpResponse is null");
				pd.dismiss();
				Toast toast1 = Toast.makeText(getApplicationContext(),
						R.string.impossible,
						Toast.LENGTH_SHORT);
				toast1.show();
				return;
			}
			else{
				if (pd != null) {
					pd.dismiss();
				}
				StatusLine httpStatus = response.getStatusLine();
				Log.i(Constants.TAG, "Status: " + httpStatus.getStatusCode());

				if (httpStatus.getStatusCode() != 202) {
					Log.e(Constants.TAG, "Status: " + httpStatus.getStatusCode());
					Toast toast2 = Toast.makeText(getApplicationContext(),
							R.string.impossibleserver
									+ httpStatus.getStatusCode(),
							Toast.LENGTH_SHORT);
					toast2.show();
					return;

				} else {
					db.deleteConfiguracion(adapter.getItem(position));
					adapter.remove(adapter.getItem(position));
					adapter.notifyDataSetChanged();
				}
				
			}
	
			
		}
	}

}
