package com.rohitutils.scheduler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class HelperDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "buffaloedu";

	private static final int DATABASE_VERSION = 1;

	public HelperDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase database) {
		TableForProfile.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		TableForProfile.onUpgrade(database, oldVersion, newVersion);
	}
}
