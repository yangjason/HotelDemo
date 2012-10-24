package com.hoteldemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class StartActivity extends Activity{
	SharedPreferences mDataPreferenc = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDataPreferenc = this.getSharedPreferences(
				DataPreferenceKey.PREFERENCE_NAME, 0);
	}
	
	@Override
	protected void onResume() {

		if (!DataPreferenceKey.isRegister(this)) {
			Intent i = new Intent(StartActivity.this,
					AccountManageActivity.class);
			startActivity(i);
		} else {
			if (DataPreferenceKey.isRegister(this)) {
				int radioId = mDataPreferenc.getInt(DataPreferenceKey.TYPE_KEY, 0);
				
				if(DataPreferenceKey.TYPE_DRIVER == radioId){
					Intent i = new Intent(StartActivity.this,
							DriverMainActivity.class);
					//this.finish();
					startActivity(i);
					this.finish();
					
				}else{
					
					Intent i = new Intent(StartActivity.this,
							MainPannelActivity.class);
					//this.finish();
					startActivity(i);
					this.finish();
				}
			} 
		}
		super.onResume();
		
	}
}
