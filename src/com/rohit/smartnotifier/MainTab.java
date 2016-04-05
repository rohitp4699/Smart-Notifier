package com.rohit.smartnotifier;

import com.reminderbyrohit.normalfiles.MainActivity;
import com.rohit.smartnotifier.R;
import com.rohitutils.scheduler.SchedulerActivity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainTab extends TabActivity {

	TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maintab);

		tabHost= getTabHost();

		TabSpec first = tabHost.newTabSpec("Tab1");
		TabSpec sec = tabHost.newTabSpec("Tab2");
		TabSpec third = tabHost.newTabSpec("Tab3");

		first.setIndicator("Scheduler");
		first.setContent(new Intent(getApplicationContext(), SchedulerActivity.class));


		third.setIndicator("Water Reminder");
		third.setContent(new Intent(getApplicationContext(), com.reminderbyrohit.normalfiles.MainActivity.class));

		tabHost.addTab(first);
//		tabHost.addTab(sec);
		tabHost.addTab(third);



	}



}
