package com.rohitutils.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rohit.smartnotifier.R;
import com.rohit.smartnotifier.*;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class Clock extends LinearLayout {

    private final static String M12 = "h:mm";

    private Calendar mCalendar;
    private Calendar mCalendar1;
    private String mFormat;
    private TextView mTimeDisplay;
    private TextView mTimeDisplay1;
    private AmPm mAmPm;
    private ContentObserver mFormatChangeObserver;
    private boolean mLive = true;
    private boolean mAttached;


    private final Handler mHandler = new Handler();
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mLive && intent.getAction().equals(
                            Intent.ACTION_TIMEZONE_CHANGED)) {
                    mCalendar = Calendar.getInstance();
                }
                // Post a runnable to avoid blocking the broadcast.
                mHandler.post(new Runnable() {
                        public void run() {
                            updateTime();
                        }
                });
            }
        };

    private static Typeface sTypeface;

    static class AmPm {
        private TextView mAmPm;
        private TextView mAmPm1;
        private String mAmString, mPmString;
        

        AmPm(View parent) {
            mAmPm = (TextView) parent.findViewById(R.id.am_pm);

            String[] ampm = new DateFormatSymbols().getAmPmStrings();
            mAmString = ampm[0];
            mPmString = ampm[1];
            
            mAmPm1 = (TextView) parent.findViewById(R.id.am_pm1);
            
        }

        void setShowAmPm(boolean show) {
            mAmPm.setVisibility(show ? View.VISIBLE : View.GONE);
            mAmPm1.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        void setIsMorning(boolean isMorning) {
            mAmPm.setText(isMorning ? mAmString : mPmString);
            mAmPm1.setText(isMorning ? mAmString : mPmString);
        }
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }
        @Override
        public void onChange(boolean selfChange) {
            setDateFormat();
            updateTime();
        }
    }

    public Clock(Context context) {
        this(context, null);
    }

    public Clock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (sTypeface == null) {
            sTypeface = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Clockopia.ttf");
        }
        mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
        mTimeDisplay.setTypeface(sTypeface);
        mTimeDisplay1 = (TextView) findViewById(R.id.timeDisplay1);
        mTimeDisplay1.setTypeface(sTypeface);
        mAmPm = new AmPm(this);
        mCalendar = Calendar.getInstance();
        mCalendar1 = Calendar.getInstance();

        setDateFormat();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

       
        if (mAttached) return;
        mAttached = true;

        if (mLive) {
            /* monitor time ticks, time changed, timezone */
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            getContext().registerReceiver(mIntentReceiver, filter);
        }

        /* monitor 12/24-hour display preference */
        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);

        updateTime();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (!mAttached) return;
        mAttached = false;

        if (mLive) {
            getContext().unregisterReceiver(mIntentReceiver);
        }
        getContext().getContentResolver().unregisterContentObserver(
                mFormatChangeObserver);
    }


    void updateTime(Calendar c, Calendar c1) {
        mCalendar = c;
        mCalendar1 = c1;
        updateTime();
    }

    private void updateTime() {
        if (mLive) {
            mCalendar.setTimeInMillis(System.currentTimeMillis());
        }

        CharSequence newTime = DateFormat.format(mFormat, mCalendar);
        CharSequence newTime1 = DateFormat.format(mFormat, mCalendar1);
        mTimeDisplay.setText(newTime);
        mTimeDisplay1.setText("-" + newTime1);
        mAmPm.setIsMorning(mCalendar.get(Calendar.AM_PM) == 0);
    }

    private void setDateFormat() {
        mFormat = Profiles.get24HourMode(getContext()) ? Profiles.M24 : M12;
        mAmPm.setShowAmPm(mFormat == M12);
    }

    void setLive(boolean live) {
        mLive = live;
    }

    void setTypeface(Typeface tf) {
        mTimeDisplay.setTypeface(tf);
        mTimeDisplay1.setTypeface(tf);
    }
}
