package com.hoteldemo;

import android.util.Log;

public class ShareData {
	public boolean isStartSMS = false;
	public String smsSender;
	void setStartSMS(boolean isSms){
		isStartSMS = isSms;
		Log.v("TAG", "setStartSMS  isStartSMS="+isStartSMS);  
	}
	void setSenderNum(String sender){
		smsSender = sender;
	}
	boolean isOpenSms(){
		return isStartSMS;
	}
	String getSender(){
		return smsSender;
	}
	
}
