package com.android.adgcb.courses;

import java.io.IOException;
import java.io.InputStream;
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
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.adgcb.Constants;
import com.android.adgcb.R;
import com.android.adgcb.database.DbAdapter;
import com.android.adgcb.viewconfiguration.Configuration;
import com.google.android.gcm.GCMRegistrar;

/**
 * Class CoursesList
 * 
 * Show list of courses registred
 * 
 * @author Jose Antonio Espino Palomares
 * 
 */
public class CoursesList extends Activity {

	private List<Course> coursesList;
	private static ProgressDialog pd = null;
	private String textdireccion;
	private String textnombre;
	private String textidentificador;
	private DbAdapter db;
	private GCMReceiver mGCMReceiver;
	private IntentFilter mOnRegisteredFilter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the View layer
		setContentView(R.layout.listview);

		mGCMReceiver = new GCMReceiver();
		mOnRegisteredFilter = new IntentFilter();
		mOnRegisteredFilter.addAction(Constants.ACTION_ON_REGISTERED);

		db = new DbAdapter(this);
		db.open();

		// Create Parser for raw/countries.xml
		CourseParser courseParser = new CourseParser();
		InputStream inputStream = getResources().openRawResource(R.raw.courses);

		// Parse the inputstream
		courseParser.parse(inputStream);

		// Get Countries
		coursesList = courseParser.getList();

		// Create a customized ArrayAdapter
		CourseArrayAdapter adapter = new CourseArrayAdapter(
				getApplicationContext(), R.layout.course_listitem, coursesList);

		// Get reference to ListView holder
		ListView lv = (ListView) this.findViewById(R.id.countryLV);

		// Set the ListView adapter
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				textnombre = coursesList.get(position).getName();
				textdireccion = coursesList.get(position).getAddress();
				textidentificador = coursesList.get(position).getId();

				GCMRegistrar.checkDevice(CoursesList.this);
				GCMRegistrar.checkManifest(CoursesList.this);

				if (!db.checkCourse(textnombre)) {

					pd = ProgressDialog.show(CoursesList.this,
							getString(R.string.processing),
							getString(R.string.wait), true, false);

					final String regId = GCMRegistrar
							.getRegistrationId(CoursesList.this);

					if (!regId.isEmpty()) {
						sendIdToServer(regId);
					} else {

						GCMRegistrar.register(CoursesList.this, coursesList
								.get(position).getId());
					}
				} else {
					Toast toast2 = Toast.makeText(getApplicationContext(),
							R.string.courseadd, Toast.LENGTH_SHORT);
					toast2.show();
				}

			}
		});
	}

	/**
	 * Class sendIdToServer
	 * 
	 * Send ID to server
	 * 
	 * @param regId
	 */
	private void sendIdToServer(String regId) {
		(new SendRegistrationIdTask(regId)).execute();
	}

	private class GCMReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String regId = intent
					.getStringExtra(Constants.FIELD_REGISTRATION_ID);
			sendIdToServer(regId);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(mGCMReceiver, mOnRegisteredFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(mGCMReceiver);
	}

	/**
	 * Class SendRegistrationIdTask
	 * 
	 * Task to send id to server
	 * 
	 * @author Jose Antonio Espino Palomares
	 * 
	 */
	private final class SendRegistrationIdTask extends
			AsyncTask<String, Void, HttpResponse> {
		private String mRegId;

		/**
		 * Constructor
		 * 
		 * @param regId
		 */
		public SendRegistrationIdTask(String regId) {
			mRegId = regId;
		}

		/**
		 * Task to send ID to server
		 */
		protected HttpResponse doInBackground(String... regIds) {
			String url = textdireccion + "/register";
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
				DefaultHttpClient httpclient = new DefaultHttpClient(
						httpParameters);
				return httpclient.execute(httppost);
			} catch (ClientProtocolException e) {
				Log.e(Constants.TAG, e.getMessage(), e);
			} catch (IOException e) {
				Log.e(Constants.TAG, e.getMessage(), e);
			}

			return null;
		}

		/**
		 * Get result of doInBackground
		 */
		protected void onPostExecute(HttpResponse response) {
			if (response == null) {
				Log.e(Constants.TAG, "HttpResponse is null");
				pd.dismiss();
				Toast toast1 = Toast.makeText(getApplicationContext(),
						R.string.impossiblecourse, Toast.LENGTH_SHORT);
				toast1.show();
				return;
			} else {
				if (pd != null) {
					pd.dismiss();
				}
				StatusLine httpStatus = response.getStatusLine();
				Log.i(Constants.TAG, "Status: " + httpStatus.getStatusCode());

				if (httpStatus.getStatusCode() != 202) {
					Log.e(Constants.TAG,
							"Status: " + httpStatus.getStatusCode());
					Toast toast2 = Toast.makeText(getApplicationContext(),
							R.string.impossiblecourse, Toast.LENGTH_SHORT);
					toast2.show();
					return;

				} else {

					db.open();
					db.newCourse(textnombre, textdireccion, textidentificador);
					db.close();
					Intent intent = new Intent(CoursesList.this,
							Configuration.class);
					startActivity(intent);
					finish();
				}
			}
		}
	}

}