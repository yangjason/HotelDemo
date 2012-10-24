package com.hoteldemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class PopActivity extends Activity{
	SharedPreferences mDataPreferenc = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.popactvty);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
}
