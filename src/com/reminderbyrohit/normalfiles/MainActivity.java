package com.reminderbyrohit.normalfiles;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.rohit.smartnotifier.R;
import com.rohit.smartnotifier.*;


public class MainActivity extends FragmentActivity {

    private ViewPager reminderViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Getting handles for views
        reminderViewPager = (ViewPager) findViewById(R.id.reminder_container_view_pager);
        // Setting adapter for view pager
        reminderViewPager.setAdapter(new RAdapter(getSupportFragmentManager()));
    }

}
