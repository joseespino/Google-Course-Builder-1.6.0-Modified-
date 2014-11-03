package com.android.adgcb.viewad;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.android.adgcb.Constants;
import com.android.adgcb.R;
import com.android.adgcb.database.DbAdapter;


/**
 * Class MessageActivity
 * 
 * Show the message
 * 
 * @author Jose Antonio Espino Palomares
 *
 */
public class MessageActivity extends Activity {
	
	private TextView messageView;
	private TextView titleView;
	private TextView nameView;
	private TextView dateView;
	private DbAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		titleView = (TextView) findViewById(R.id.textView1);
		messageView = (TextView) findViewById(R.id.textView2);
		nameView = (TextView) findViewById(R.id.textView3);
		dateView = (TextView) findViewById(R.id.textView4);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		String titulo = getIntent().getStringExtra(Constants.FIELD_TITLE);
		String msg = getIntent().getStringExtra(Constants.FIELD_MESSAGE);
		String name = getIntent().getStringExtra(Constants.FIELD_NAMECOURSE);
		String date = getIntent().getStringExtra(Constants.FIELD_DATE);
		
		db = new DbAdapter(this);
		db.open();
		db.updateState(titulo,msg);
		db.close();
		titleView.setText(titulo);
		messageView.setMovementMethod(new ScrollingMovementMethod() );

		messageView.setText(msg);	
		nameView.setText(name);
		dateView.setText(date);

	}
}