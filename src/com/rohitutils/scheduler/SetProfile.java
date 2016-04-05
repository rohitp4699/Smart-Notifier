package com.rohitutils.scheduler;


import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.rohit.smartnotifier.R;
import com.rohit.smartnotifier.*;


public class SetProfile extends PreferenceActivity 
implements TimePickerDialog.OnTimeSetListener,
Preference.OnPreferenceChangeListener {

	private int     id;
	private int     startHour;
	private int     startMinutes;
	private int 	endHour;
	private int 	endMinutes;
	private boolean isTimePickerCancel;
	private Profile   mOriginalProfile;
	private EditTextPreference label;
	private CheckBoxPreference enable;
	private Preference startTime;
	private Preference endTime;
	private Preference clickedTime;
	private CheckBoxPreference vibratecb;
	private CheckBoxPreference slientcb;
	private PreferenceFile preferencefile;

	

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);


		setContentView(R.layout.set_profile);

		addPreferencesFromResource(R.xml.profile_prefs);

		label = (EditTextPreference) findPreference("label");
		label.setOnPreferenceChangeListener(
				new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference p,
							Object newValue) {
						String val = (String) newValue;

						p.setSummary(val);
						//Put this in to the database
						if (val != null && !val.equals(label.getText())) {
							// Call through to the generic listener.
							return SetProfile.this.onPreferenceChange(p,
									newValue);
						}
						return true;
					}
				});
		enable = (CheckBoxPreference) findPreference("enabled");
		enable.setOnPreferenceChangeListener(
				new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference p,
							Object newValue) {
						return SetProfile.this.onPreferenceChange(p, newValue);
					}
				});
		startTime = findPreference("starttime");
		endTime = findPreference("endtime");
		vibratecb = (CheckBoxPreference) findPreference("vibrate");
		vibratecb.setOnPreferenceChangeListener(this);
		slientcb = (CheckBoxPreference) findPreference("silent");
		preferencefile = (PreferenceFile) findPreference("setRepeat");
		preferencefile.setOnPreferenceChangeListener(this);

		Intent i = getIntent();
		Profile profile;

		id = i.getIntExtra(DbAdapter.KEY_ROWID,-1);
		if (id == -1) {
			// No alarm id means create a new alarm.
			profile = new Profile();
		} else {
			/* load alarm details from database */
			profile = Profiles.getProfile(this, id);
			// Bad alarm, bail to avoid a NPE.
			if (profile == null) {
				finish();
				return;
			}
		}
		mOriginalProfile = profile;
		updatePrefs(mOriginalProfile);

		getListView().setItemsCanFocus(true);

		Button b = (Button) findViewById(R.id.profile_save);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				saveProfile();
				finish();
			}
		});

		final Button revert = (Button) findViewById(R.id.profile_revert);
		revert.setEnabled(false);
		revert.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int newId = id;
				updatePrefs(mOriginalProfile);

				if (mOriginalProfile.id == -1) {
					Profiles.deleteProfile(SetProfile.this, id);
				} else {
					saveProfile();
				}
				revert.setEnabled(false);
			}
		});

		b = (Button) findViewById(R.id.profile_delete);
		if (id == -1) {
			b.setEnabled(false);
		} else {
			b.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					deleteAlarm();
				}
			});
		}


		if (id == -1) {
			// Assume the user hit cancel
			isTimePickerCancel = true;
			clickedTime = startTime;
			showTimePicker();
		}
	}


	private static final Handler sHandler = new Handler();

	public boolean onPreferenceChange(final Preference p, Object newValue) {
		sHandler.post(new Runnable() {
			public void run() {
				// Editing any preference (except enable) enables the alarm.
				if (p != enable) {
					enable.setChecked(true);
				}
				saveProfileAndEnableRevert();
			}
		});
		return true;
	}

	private long saveProfileAndEnableRevert() {

		final Button revert = (Button) findViewById(R.id.profile_revert);
		revert.setEnabled(true);
		return saveProfile();
	}

	private long saveProfile() {
		Profile profile = new Profile();
		profile.id = id;
		profile.enabled = enable.isChecked();
		profile.starthour = startHour;
		profile.startminutes = startMinutes;
		profile.endhour = endHour;
		profile.endminutes = endMinutes;
		profile.repeatPref = preferencefile.getDaysOfWeek();
		profile.vibrate = vibratecb.isChecked();
		profile.label = label.getText();
		profile.silent = slientcb.isChecked();

		long time = 0;
		if (profile.id == -1) {
			Profiles.addProfile(this, profile);
			id = profile.id;
		} else {
			Profiles.saveProfile(this, profile);
		}
		return time;
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if (preference == startTime || preference == endTime) {
			clickedTime = preference;
			showTimePicker();
		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// onTimeSet is called when the user clicks "Set"
		isTimePickerCancel = false;
		if ( clickedTime == startTime ) {
			startHour = hourOfDay;
			startMinutes = minute;
			updateTime(startTime);
		}

		if ( clickedTime == endTime) {
			endHour = hourOfDay;
			endMinutes = minute;
			updateTime(endTime);
		}

		enable.setChecked(true);
		saveProfileAndEnableRevert();
	}

	@Override
	public void onBackPressed() {
		if (!isTimePickerCancel) {
			saveProfile();
		}
		finish();
	}

	private void showTimePicker() {
		if ( clickedTime == startTime) {
			new TimePickerDialog(this, this, startHour, startMinutes,
					DateFormat.is24HourFormat(this)).show();
		} else {
			new TimePickerDialog(this, this, endHour, endMinutes,
					DateFormat.is24HourFormat(this)).show();

		}
	}

	private void deleteAlarm() {
		new AlertDialog.Builder(this)
		.setTitle(getString(R.string.delete_profile))
		.setMessage(getString(R.string.delete_profile_confirm))
		.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int w) {
				Profiles.deleteProfile(SetProfile.this, id);
				finish();
			}
		})
		.setNegativeButton(android.R.string.cancel, null)
		.show();
	}

	private void updatePrefs(Profile profile) {
		id = profile.id;
		enable.setChecked(profile.enabled);
		label.setText(profile.label);
		label.setSummary(profile.label);
		startHour = profile.starthour;
		startMinutes = profile.startminutes;
		endHour = profile.endhour;
		endMinutes = profile.endminutes;
		preferencefile.setDaysOfWeek(profile.repeatPref);
		vibratecb.setChecked(profile.vibrate);
		slientcb.setChecked(profile.silent);


		updateTime();
	}

	private void updateTime(Preference preference) {
		if ( preference == startTime)
			startTime.setSummary(Profiles.formatTime(this, startHour, startMinutes, preferencefile.getDaysOfWeek()));
		if (preference == endTime)
			endTime.setSummary(Profiles.formatTime(this, endHour, endMinutes, preferencefile.getDaysOfWeek()));
	}

	private void updateTime() {

		startTime.setSummary(Profiles.formatTime(this, startHour, startMinutes, preferencefile.getDaysOfWeek()));
		endTime.setSummary(Profiles.formatTime(this, endHour, endMinutes, preferencefile.getDaysOfWeek()));
	}

}
