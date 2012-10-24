package com.hoteldemo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AccountManageActivity extends Activity implements View.OnClickListener, OnFocusChangeListener{
	RadioGroup mRadiogroupId;
	RadioButton mRadioBtnPassenger;
	RadioButton mRadioBtnDriver;
	Button mCancel ;
	Button mOk;
	
	EditText mName = null;
	EditText mMobileNum = null;
	EditText mCarId = null;
	EditText mCarModel = null;
	EditText mCarFeature = null;
	
	View mCarpara = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.account);
		mCarpara = findViewById(R.id.carparam);
		SharedPreferences userInfo = this.getSharedPreferences(DataPreferenceKey.PREFERENCE_NAME, 0);

		mRadiogroupId = (RadioGroup) findViewById(R.id.radiogroupId);
		mRadioBtnPassenger = (RadioButton) findViewById(R.id.radiobtnPassenger);
		mRadioBtnDriver = (RadioButton) findViewById(R.id.radiobtnDriver);

		mRadiogroupId
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == mRadioBtnPassenger.getId()) {
							mCarpara.setVisibility(View.INVISIBLE);
						} else {
							mCarpara.setVisibility(View.VISIBLE);
						}
					}
				}); 		
		mRadiogroupId.clearCheck();
		
		
		mName = (EditText) this.findViewById(R.id.acountname);
		

		
		mMobileNum = (EditText) this.findViewById(R.id.mobilenum);
	
		
		mCarId = (EditText) this.findViewById(R.id.carid);		
		mCarModel = (EditText) this.findViewById(R.id.carmodel);		
		mCarFeature = (EditText) this.findViewById(R.id.carfeature);		
		if(!DataPreferenceKey.isRegister(this)){
			mRadiogroupId.check(mRadioBtnPassenger.getId());
		}else{
			int radioId = userInfo.getInt(DataPreferenceKey.TYPE_KEY, 0);
			
			if(DataPreferenceKey.TYPE_PASSENGER == radioId){
				mRadiogroupId.check(mRadioBtnPassenger.getId());
			}else if(DataPreferenceKey.TYPE_DRIVER == radioId){
				mRadiogroupId.check(mRadioBtnDriver.getId());
			}
			
			mName.setText(userInfo.getString(DataPreferenceKey.ACCOUNTNAME_KEY, "张小三"));
			mMobileNum.setText(userInfo.getString(DataPreferenceKey.MOBILENUM_KEY, "15812345678"));
			mCarId.setText(userInfo.getString(DataPreferenceKey.CARID_KEY, "苏A-12345"));
			mCarModel.setText(userInfo.getString(DataPreferenceKey.CARMODEL_KEY,"奥迪A4"));
			mCarFeature.setText(userInfo.getString(DataPreferenceKey.CARFEATURE_KEY,"银色"));

		}		
		mCarFeature.setSelection(mCarFeature.length());
		mCarModel.setSelection(mCarModel.length());
		mCarId.setSelection(mCarId.length());
		mName.setSelection(mName.length());		
		mMobileNum.setSelection(mMobileNum.length());
		
		mCancel = (Button) this.findViewById(R.id.cancel);
		mOk = (Button) this.findViewById(R.id.ok);
		
		mCancel.setOnClickListener(this);
		mOk.setOnClickListener(this);
		
//		SharedPreferences userInfo = getSharedPreferences(
//				DataPreferenceKey.sPreferencName, 0);
//		String username = userInfo.getString(DataPreferenceKey.sAccountNameKey,
//				" null");
//		Toast.makeText(AccountManageActivity.this, username, Toast.LENGTH_LONG)
//				.show();

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == mCancel){
			this.setResult(this.RESULT_CANCELED);
			this.finish();
		}else if(v == mOk){
			int id = mRadiogroupId.getCheckedRadioButtonId();
			if(!isNumeric(mMobileNum.getText().toString())){
				Toast.makeText(this,"手机号码格式格式不正确,请重新填写",
						Toast.LENGTH_LONG).show();
				return ;
			}
			if(id == mRadioBtnPassenger.getId()){
				SharedPreferences userInfo = this.getSharedPreferences(DataPreferenceKey.PREFERENCE_NAME, 0);
				userInfo.edit().putInt(DataPreferenceKey.TYPE_KEY, DataPreferenceKey.TYPE_PASSENGER).commit();				
				userInfo.edit().putString(DataPreferenceKey.ACCOUNTNAME_KEY, mName.getText().toString()).commit();
				userInfo.edit().putString(DataPreferenceKey.MOBILENUM_KEY, mMobileNum.getText().toString()).commit();
				userInfo.edit().putBoolean(DataPreferenceKey.ISREGISTER_KEY, true).commit();
							
			}else if(id == mRadioBtnDriver.getId()){
				SharedPreferences userInfo = this.getSharedPreferences(DataPreferenceKey.PREFERENCE_NAME, 0);
				userInfo.edit().putInt(DataPreferenceKey.TYPE_KEY, DataPreferenceKey.TYPE_DRIVER).commit();				
				userInfo.edit().putString(DataPreferenceKey.ACCOUNTNAME_KEY, mName.getText().toString()).commit();
				userInfo.edit().putString(DataPreferenceKey.MOBILENUM_KEY, mMobileNum.getText().toString()).commit();
				userInfo.edit().putString(DataPreferenceKey.CARID_KEY, mCarId.getText().toString()).commit();
				userInfo.edit().putString(DataPreferenceKey.CARMODEL_KEY, mCarModel.getText().toString()).commit();
				userInfo.edit().putString(DataPreferenceKey.CARFEATURE_KEY, mCarFeature.getText().toString()).commit();
				
				userInfo.edit().putBoolean(DataPreferenceKey.ISREGISTER_KEY, true).commit();				
			}else{
				Toast.makeText(AccountManageActivity.this, "no Radio", Toast.LENGTH_LONG)
				.show();
			}
			
			this.setResult(this.RESULT_OK);
			this.finish();
		}
	}
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		
	}
	public static boolean isNumeric(String str){
		for (int i = 0; i < str.length(); i++){
		   System.out.println(str.charAt(i));
		   if (!Character.isDigit(str.charAt(i))){
		    return false;
		   }
		}
		if(str.length() != 11) return false;
		
		return true;
	}
	
	 

}
