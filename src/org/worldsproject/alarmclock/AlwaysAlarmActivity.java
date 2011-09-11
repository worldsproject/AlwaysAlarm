package org.worldsproject.alarmclock;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AlwaysAlarmActivity extends Activity 
{
	public static ArrayList<Alarm> alarms = new ArrayList<Alarm>();
	private SharedPreferences pref;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		Intent intent = getIntent();
		String mode = intent.getStringExtra("mode");
		
		LinearLayout root = (LinearLayout)findViewById(R.id.alarms);
		root.removeAllViews();
		for(Alarm v:alarms)
		{
			root.addView(v);
		}

		if(mode != null)
		{
			if(mode.equals("new_alarm"))
			{
				String unit = pref.getString("alarm_units", "false");
				Alarm alarm = new Alarm(this,
						intent.getIntExtra("hour", 0),
						intent.getIntExtra("minute", 0),
						intent.getIntExtra("steps", 0),
						intent.getBooleanExtra("monday", false),
						intent.getBooleanExtra("tuesday", false),
						intent.getBooleanExtra("wednesday", false),
						intent.getBooleanExtra("thursday", false),
						intent.getBooleanExtra("friday", false),
						intent.getBooleanExtra("saturday", false),
						intent.getBooleanExtra("sunday", false), 
						(unit.equalsIgnoreCase("false") ? false : true));
				
				Calendar cal = alarm.nextAlarmEvent();
				
				if(cal == null)
				{
					Toast.makeText(getBaseContext(), "Alarm exists in past.\n  No alarm added", Toast.LENGTH_LONG).show();
					return;
				}
				root.addView(alarm);
				alarms.add(alarm);
				
				
				Calendar cur = Calendar.getInstance();
				cur.setTimeInMillis(System.currentTimeMillis());
				long diff = cal.getTimeInMillis() - cur.getTimeInMillis();
				Toast.makeText(getBaseContext(), "" + (diff/1000), Toast.LENGTH_LONG).show();
				
		    	Intent alarmIntent = new Intent(AlwaysAlarmActivity.this, AlarmReceiver.class);
		    	alarmIntent.putExtra("steps", intent.getIntExtra("steps", 0));
		    	PendingIntent sender = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		    	
		    	AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		    	am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
			}
		}
	}
	
	public void removeAlarm(View v)
	{
		LinearLayout root = (LinearLayout)findViewById(R.id.alarms);
		root.removeView((Alarm)v.getParent().getParent().getParent());//TODO This feels dirty...
		alarms.remove((Alarm)v.getParent().getParent().getParent());
	}

	public void addAlarm(View v)
	{
		Intent myIntent = new Intent(AlwaysAlarmActivity.this, AddAlarmActivity.class);
		AlwaysAlarmActivity.this.startActivity(myIntent);
	}

	public void showPreferences(View v)
	{
		Intent myIntent = new Intent(AlwaysAlarmActivity.this, AlarmPreferences.class);
		AlwaysAlarmActivity.this.startActivity(myIntent);
	}
}