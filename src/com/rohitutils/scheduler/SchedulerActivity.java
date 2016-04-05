package com.rohitutils.scheduler;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rohit.smartnotifier.R;
import com.rohit.smartnotifier.*;

public class SchedulerActivity extends Activity implements OnItemClickListener {

	private SharedPreferences mPrefs;
	private LayoutInflater mFactory;
	private ListView mAlarmsList;
	private Cursor mCursor;


	private void addNewProfile() {
		startActivityForResult(new Intent(this, SetProfile.class), 0);
	}

	private void updateIndicatorAndProfile(boolean enabled, ImageView bar,
			Profile profile) {
		bar.setImageResource(enabled ? R.drawable.ic_indicator_on
				: R.drawable.ic_indicator_off);
		Profiles.enableProfile(this, profile.id, enabled);
	}

	private class ProfileAdapter extends CursorAdapter {
		public ProfileAdapter(Context context, Cursor cursor) {
			super(context, cursor);
		}

		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View ret = mFactory.inflate(R.layout.profile_row, parent, false);

			Clock digitalClock = (Clock) ret
					.findViewById(R.id.digitalClock);
			digitalClock.setLive(false);
			return ret;
		}

		public void bindView(View view, Context context, Cursor cursor) {
			final Profile profile = new Profile(cursor);

			View indicator = view.findViewById(R.id.indicator);


			final ImageView barOnOff = (ImageView) indicator
					.findViewById(R.id.bar_onoff);
			barOnOff.setImageResource(profile.enabled ? R.drawable.ic_indicator_on
					: R.drawable.ic_indicator_off);


			final CheckBox clockOnOff = (CheckBox) indicator
					.findViewById(R.id.clock_onoff);
			clockOnOff.setChecked(profile.enabled);


			indicator.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					clockOnOff.toggle();
					updateIndicatorAndProfile(clockOnOff.isChecked(), barOnOff,
							profile);
				}
			});

			Clock digitalClock = (Clock) view
					.findViewById(R.id.digitalClock);


			final Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, profile.starthour);
			c.set(Calendar.MINUTE, profile.startminutes);
			
			final Calendar c2 = Calendar.getInstance();
			c2.set(Calendar.HOUR_OF_DAY, profile.endhour);
			c2.set(Calendar.MINUTE, profile.endminutes);
			
			digitalClock.updateTime(c, c2);
			digitalClock.setTypeface(Typeface.DEFAULT);


			TextView daysOfWeekView = (TextView) digitalClock
					.findViewById(R.id.daysOfWeek);

			final String daysOfWeekStr = profile.repeatPref.toString(
					SchedulerActivity.this, false);
			if (daysOfWeekStr != null && daysOfWeekStr.length() != 0) {
				daysOfWeekView.setText(daysOfWeekStr);
				daysOfWeekView.setVisibility(View.VISIBLE);
			} else {
				daysOfWeekView.setVisibility(View.GONE);
			}


			TextView labelView = (TextView) view.findViewById(R.id.label);
			if (profile.label != null && profile.label.length() != 0) {
				labelView.setText(profile.label);
				labelView.setVisibility(View.VISIBLE);
			} else {
				labelView.setVisibility(View.GONE);
			}
		}
	};

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final int id = (int) info.id;

		if (id == -1) {
			return super.onContextItemSelected(item);
		}
		switch (item.getItemId()) {
		case R.id.delete_profile:

			new AlertDialog.Builder(this)
			.setTitle(getString(R.string.delete_profile))
			.setMessage(getString(R.string.delete_profile_confirm))
			.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int w) {
					Profiles.deleteProfile(
							SchedulerActivity.this, id);
					DbAdapter helper = DbAdapter.factory(SchedulerActivity.this);
					Cursor cur = helper.fetchAllProfiles();
					((ProfileAdapter) mAlarmsList.getAdapter()).changeCursor(cur);
				}
			}).setNegativeButton(android.R.string.cancel, null)
			.show();
			
			return true;

		case R.id.enable_profile:
			final Cursor c = (Cursor) mAlarmsList.getAdapter().getItem(
					info.position);
			final Profile profile = new Profile(c);
			Profiles.enableProfile(this, profile.id, !profile.enabled);
			return true;

		case R.id.edit_profile:
			Intent intent = new Intent(this, SetProfile.class);
			intent.putExtra(DbAdapter.KEY_ROWID, id);
			startActivityForResult(intent, 0);
			return true;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
		DbAdapter helper = DbAdapter.factory(this);
		Cursor cur = helper.fetchAllProfiles();
		((ProfileAdapter) mAlarmsList.getAdapter()).changeCursor(cur);
	}

	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {

		getMenuInflater().inflate(R.menu.context_menu, menu);


		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		final Cursor c = (Cursor) mAlarmsList.getAdapter().getItem(
				(int) info.position);
		final Profile profile = new Profile(c);


		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, profile.starthour);
		cal.set(Calendar.MINUTE, profile.startminutes);
		final String time = Profiles.formatTime(this, cal);


		final View v = mFactory.inflate(R.layout.context_menu_header, null);
		TextView textView = (TextView) v.findViewById(R.id.header_time);
		textView.setText(time);
		textView = (TextView) v.findViewById(R.id.header_label);
		textView.setText(profile.label);


		menu.setHeaderView(v);

		if (profile.enabled) {
			menu.findItem(R.id.enable_profile).setTitle(
					R.string.disable_profile);
		}
	}

	private void updateLayout() {

		setContentView(R.layout.main);
		mAlarmsList = (ListView) findViewById(R.id.profiles_list);
		ProfileAdapter adapter = new ProfileAdapter(this, mCursor);
		mAlarmsList.setAdapter(adapter);
		mAlarmsList.setVerticalScrollBarEnabled(true);
		mAlarmsList.setOnItemClickListener(this);
		mAlarmsList.setOnCreateContextMenuListener(this);

		View addAlarm = findViewById(R.id.add_profile);
		addAlarm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addNewProfile();
			}
		});

		addAlarm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				v.setSelected(hasFocus);
			}
		});

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mFactory = LayoutInflater.from(this);

		DbAdapter helper = DbAdapter.factory(this);

		mCursor = helper.fetchAllProfiles();
		updateLayout();
	}

/*	public void onResume() {
	
		ProfileDbAdapter helper = ProfileDbAdapter.factory(this);
		Cursor cur = helper.fetchAllProfiles();
		((ProfileAdapter) mAlarmsList.getAdapter()).changeCursor(cur);
	}*/
	
	public void onItemClick(AdapterView parent, View v, int pos, long id) {
		Intent intent = new Intent(this, SetProfile.class);
		intent.putExtra(DbAdapter.KEY_ROWID, (int) id);
		startActivityForResult(intent, 0);
	}
}