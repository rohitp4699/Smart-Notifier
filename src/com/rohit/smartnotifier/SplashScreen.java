package com.rohit.smartnotifier;

import com.rohit.smartnotifier.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.splashui);
		
		
		Typeface typeface= Typeface.createFromAsset(getAssets(), "fonts/caviardreams_bold.ttf");
		
		
		((TextView)findViewById(R.id.textView1)).setTypeface(typeface);
		((TextView)findViewById(R.id.textView2)).setTypeface(typeface);
		((TextView)findViewById(R.id.textView3)).setTypeface(typeface);
		
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {

				startActivity(new Intent(getApplicationContext(), MainTab.class));
				finish();
			}
		}, 5000);
		
		
		
	}

}
