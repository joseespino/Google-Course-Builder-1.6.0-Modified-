package com.android.adgcb.viewnotification;

import java.util.ArrayList;
import java.util.List;

import com.android.adgcb.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** Class NotificationArrayAdapter
 * 
 * 	Arrays of notifications
 * 
 * @author Jose Antonio Espino Palomares
 *
 */
public class NotificationArrayAdapter extends ArrayAdapter<Notification> {


	private int maxsizemessage = 90;
	private int maxsizetitle = 30;
	private int maxsizenamecourse = 40;


	private ImageView notificationIcon;
	private TextView title;
	private TextView nameCourse;
	private TextView message;
	private TextView date;

	private List<Notification> notifications = new ArrayList<Notification>();

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public NotificationArrayAdapter(Context context, int textViewResourceId,
			List<Notification> objects) {
		super(context, textViewResourceId, objects);
		this.notifications = objects;
	}

	/**
	 * Get number of notifications
	 * 
	 * @return Notifications arrays's size
	 */
	public int getCount() {
		return this.notifications.size();
	}

	/**
	 * Get item from notification
	 */
	public Notification getItem(int index) {
		return this.notifications.get(index);
	}

	/**
	 * Get view to show
	 */
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		if (row == null) {
			// ROW INFLATION
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.notification_listitem, parent,
					false);
		}

		// Get item
		Notification notification = getItem(position);

		// Get reference to ImageView
		notificationIcon = (ImageView) row.findViewById(R.id.imageView1);

		// Get reference to TextViews
		title = (TextView) row.findViewById(R.id.titulo);
		nameCourse = (TextView) row.findViewById(R.id.nombre);
		message = (TextView) row.findViewById(R.id.mensaje);
		date = (TextView) row.findViewById(R.id.fecha);

		// Set notification
		title.setText(reduceLengthString(maxsizetitle,notification.title));
		nameCourse.setText(reduceLengthString(maxsizenamecourse,notification.nameCourse));
		message.setText(reduceLengthString(maxsizemessage,notification.message));
		date.setText(notification.date);

		if (notification.state.equals("0")) {
			notificationIcon.setImageResource(R.drawable.ic_message);
			row.setBackgroundColor(Color.rgb(238, 233, 233));
		} else {
			notificationIcon.setImageResource(R.drawable.ic_unmessage);
			row.setBackgroundColor(Color.rgb(255, 255, 255));

		}

		return row;
	}
	
	/**
	 * Reduce string size to show
	 * 
	 * @param maxsize 
	 * @param string
	 * @return reduce string
	 */
	public static String reduceLengthString(int maxsize, String string) {
		if (string.length() > maxsize) {
			String aux = string.substring(0, maxsize);
			aux = aux + "...";
			return aux;
		} else {
			return string;
		}
	}
}