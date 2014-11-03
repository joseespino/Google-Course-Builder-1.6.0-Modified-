package com.android.adgcb;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.adgcb.database.DbAdapter;
import com.android.adgcb.viewad.MessageActivity;
import com.android.adgcb.viewnotification.NotificationArrayAdapter;
import com.google.android.gcm.GCMBaseIntentService;

@SuppressLint({ "SimpleDateFormat", "InlinedApi" })
/**
 * Class GCMIntentService
 * 
 * Receive and show notification
 * 
 * @author Jose Antonio Espino Paloamares
 *
 */
public class GCMIntentService extends GCMBaseIntentService {

	private DbAdapter db;
	public static final String DATE_TIME_FORMAT = "dd-MM-yyyy kk:mm:ss";
	
	@Override
	protected void onRegistered(Context context, String regId) {
		Intent intent = new Intent(Constants.ACTION_ON_REGISTERED);
		intent.putExtra(Constants.FIELD_REGISTRATION_ID, regId);
		context.sendBroadcast(intent);
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		Log.i(Constants.TAG, "onUnregistered: " + regId);

	}

	@Override
	protected void onMessage(Context context, Intent intent) {

		db = new DbAdapter(this);
		
		String msg = intent.getStringExtra(Constants.FIELD_MESSAGE);
		String name = intent.getStringExtra(Constants.FIELD_NAMECOURSE);
		String titulo = intent.getStringExtra(Constants.FIELD_TITLE);

		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		String reminderDateTime= dateTimeFormat.format(Calendar.getInstance().getTime());
		
		db.open();
		if(!db.checkNofitication(titulo,msg)){
			Log.i("Opcion1",titulo);
			db.newNotificacion(titulo, name, msg, 0, reminderDateTime);
	    	db.close();

            int notificationId = 001;
            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // Patron de vibracion: 1 segundo vibra, 0.5 segundos para, 1 segundo vibra
            long[] pattern = new long[]{1000,500,1000};


            Intent viewIntent = new Intent(this, MessageActivity.class);
            viewIntent.putExtra(Constants.FIELD_TITLE, titulo);
            viewIntent.putExtra(Constants.FIELD_MESSAGE, msg);
            viewIntent.putExtra(Constants.FIELD_NAMECOURSE, name);
            viewIntent.putExtra(Constants.FIELD_DATE, reminderDateTime);
            viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent viewPendingIntent =
                    PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create a WearableExtender to add functionality for wearables
            NotificationCompat.WearableExtender wearableExtender =
                    new NotificationCompat.WearableExtender()
                            .setHintHideIcon(true);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle(name)
                            .setContentText(titulo)
                            .setContentIntent(viewPendingIntent)
                            .setAutoCancel(true)
                            .setSound(defaultSound)
                            .extend(wearableExtender)
                            .setLights(Color.RED, 1, 0)
                            .setVibrate(pattern);

            NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
            secondPageStyle.setBigContentTitle("")
                    .bigText(NotificationArrayAdapter.reduceLengthString(80,msg));

            // Create second page notification
            Notification secondPageNotification =
                    new NotificationCompat.Builder(this)
                            .setStyle(secondPageStyle)
                            .build();

            // Add second page with wearable extender and extend the main notification
            Notification twoPageNotification =
                    new WearableExtender()
                            .addPage(secondPageNotification)
                            .extend(notificationBuilder)
                            .build();

            // Get an instance of the NotificationManager service
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(this);
            
            // Build the notification and issues it with notification manager.
            notificationManager.notify(notificationId, twoPageNotification);



		}
	}

	@Override
	protected void onError(Context context, String errorId) {
		Toast.makeText(context, errorId, Toast.LENGTH_LONG).show();
	}
}