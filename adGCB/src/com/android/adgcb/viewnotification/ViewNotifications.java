package com.android.adgcb.viewnotification;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.android.adgcb.Constants;
import com.android.adgcb.R;
import com.android.adgcb.database.DbAdapter;
import com.android.adgcb.viewad.MessageActivity;
import com.android.adgcb.viewconfiguration.Configuration;

/**
 * Class ViewNotifications
 * 
 * Show notifications
 * 
 * @author Jose Antonio Espino Palomares
 * 
 */
public class ViewNotifications extends Activity implements
		SwipeRefreshLayout.OnRefreshListener {

	private List<Notification> notificationList = new ArrayList<Notification>();
	private DbAdapter db;
	private Builder alertDialogBuilder;
	public NotificationArrayAdapter adapter;
	private SwipeRefreshLayout swipeLayout;
	private ListView lv;
	private EditText inputSearch;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the View layer
		setContentView(R.layout.notificationlistview);

		inputSearch = (EditText) findViewById(R.id.inputSearch);

		

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		db = new DbAdapter(this);
		db.open();
		notificationList = db.loadNotification();

		// Create a customized ArrayAdapter
		adapter = new NotificationArrayAdapter(getApplicationContext(),
				R.layout.notification_listitem, notificationList);

		// Get reference to ListView holder
		lv = (ListView) this.findViewById(R.id.countryLV);

		// Set the ListView adapter
		lv.setAdapter(adapter);

		lv.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int filaSuperior = (lv == null// Si la lista esta vacía ó
				|| lv.getChildCount() == 0) ? 0 : lv.getChildAt(0).getTop();// Estamos
																			// en
																			// el
																			// elemento
																			// superior
				swipeLayout.setEnabled(filaSuperior >= 0); // Activamos o
															// desactivamos el
															// swipe layout
															// segun corresponda
			}

		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int index, long arg3) {
				// TODO Auto-generated method stub

				final int position = index;

				alertDialogBuilder = new AlertDialog.Builder(
						ViewNotifications.this);
				alertDialogBuilder.setTitle(R.string.deletead);
				alertDialogBuilder.setMessage(R.string.confirmation);
				alertDialogBuilder.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								db.deleteNotificacion(adapter.getItem(position).title);
								adapter.remove(adapter.getItem(position));
								adapter.notifyDataSetChanged();

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

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {

				Intent intent = new Intent(getApplicationContext(),
						MessageActivity.class);
				intent.putExtra(Constants.FIELD_TITLE,
						adapter.getItem(position).title);
				intent.putExtra(Constants.FIELD_MESSAGE,
						adapter.getItem(position).message);
				intent.putExtra(Constants.FIELD_NAMECOURSE,
						adapter.getItem(position).nameCourse);
				intent.putExtra(Constants.FIELD_DATE,
						adapter.getItem(position).date);

				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);

			}
		});

		/* Activando el filtro de busqueda */
		inputSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

				if (arg0 == null) {
					adapter.clear();
					adapter.addAll(getNotifications());
				} else {
					adapter.clear();
					adapter.addAll(db.searchNotifications(arg0.toString()));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * Refresh adapter
	 */
	public void onRefresh() {

		swipeLayout.setRefreshing(true);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				adapter.clear();
				adapter.addAll(getNotifications());
				swipeLayout.setRefreshing(false);

			}
		}, 3000);

	}

	/**
	 * Get list of notifications from database
	 * 
	 * @return list of notifications
	 */
	private List<Notification> getNotifications() {
		List<Notification> notificationList = new ArrayList<Notification>();
		notificationList = db.loadNotification();
		return notificationList;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menuconfiguration, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_new:
			Intent intent = new Intent(ViewNotifications.this,
					Configuration.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}