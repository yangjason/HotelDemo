package com.hoteldemo;

import java.util.Calendar;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class BookingCarActivity extends MapActivity
		implements
			View.OnClickListener {
	SharedPreferences userInfo = null;
	View mSetBookTime = null;
	TextView mBookTime = null;
	TextView mClientName = null;
	TextView mMobileNum = null;
	TextView mWaitLctnInfo = null;

	View mSetcarparklocation = null;

	private int mHour;
	private int mMinute;
	static final int TIME_DIALOG_ID = 0;
	TimePickerDialog mTimerDialog = null;

	Button mCancel;
	Button mOk;

	BMapManager mBMapMan = null;
	MapController mMapController = null;
	MapView mMapView = null;
	LocationListener mLocationListener = null;

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;
			userInfo.edit()
					.putString(DataPreferenceKey.WAITTIME_KEY,
							mHour + ":" + mMinute).commit();
			updateDisplay();
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bookingcarmain);
		userInfo = this.getSharedPreferences(DataPreferenceKey.PREFERENCE_NAME,
				0);

		// mBMapMan = new BMapManager(getApplication());
		// boolean bInit = mBMapMan.init(
		// "2D027B3CAA60689DB0A12E08320BCEDB503ACD54", null);
		// super.initMapActivity(mBMapMan);
		// mMapView = (MapView) findViewById(R.id.baidumapsView);

		mSetBookTime = this.findViewById(R.id.setbooktime);
		mSetBookTime.setOnClickListener(this);

		mClientName = (TextView) this.findViewById(R.id.clientname);
		mClientName.setText(userInfo.getString(
				DataPreferenceKey.ACCOUNTNAME_KEY, "张小三"));

		mMobileNum = (TextView) this.findViewById(R.id.mobilenum);
		mMobileNum.setText(userInfo.getString(DataPreferenceKey.MOBILENUM_KEY,
				"15812345678"));

		mWaitLctnInfo = (TextView) this.findViewById(R.id.waitlctninfo);
		String strTitle = userInfo.getString(
				DataPreferenceKey.WAITLCTNTITLE_KEY, "未设定");
		String strAddr = userInfo.getString(DataPreferenceKey.WAITLCTNADDR_KEY,
				"未设定");
		if (strTitle.equals("到这里来接我")) {
			mWaitLctnInfo.setText(strAddr);
		} else {

			int location = strTitle.indexOf("到 ");
			if (!(location < 0)) {
				String left = strTitle.substring(0, location);
				String right = strTitle.substring(location + 2,
						strTitle.length());
				strTitle = left + right;

				location = strTitle.indexOf(" 接我");
				left = strTitle.substring(0, location);
				strTitle = left;
			}

			mWaitLctnInfo.setText(strTitle + "\r\n" + strAddr);
		}

		mSetcarparklocation = this.findViewById(R.id.setcarparklocation);

		mBookTime = (TextView) this.findViewById(R.id.booktime);
		final Calendar c = Calendar.getInstance();
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		if(mMinute > 40){
			mHour = mHour + 1;
			mMinute = mMinute - 40;
		}else if(mMinute == 40){
			mHour = mHour + 1;
			mMinute = 0;
		}else{
			mMinute +=20;
		}
		updateDisplay();

		mCancel = (Button) this.findViewById(R.id.cancel);
		mOk = (Button) this.findViewById(R.id.ok);
		mCancel.setOnClickListener(this);
		mOk.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mSetBookTime) {
			showDialog(TIME_DIALOG_ID);
		} else if (v == mSetcarparklocation) {

		} else if (v == mCancel) {
			this.setResult(this.RESULT_CANCELED);
			this.finish();
		} else if (v == mOk) {
			// check whether set pare phone
			String str = userInfo.getString(DataPreferenceKey.PAREPHONENUM_KEY,
					"");
			if (isNumeric(str)) {
//				Toast.makeText(this, str, Toast.LENGTH_LONG).show();
				sendBookMsg();
				// makeBookMsg();
				setResult(this.RESULT_OK);
			}else{
				Toast.makeText(this,"号码:"+str+" 请重新设定", Toast.LENGTH_LONG).show();
				setResult(this.RESULT_CANCELED);
			}
			
			this.finish();
		}
	}

	protected boolean sendBookMsg() {
		String bookMsg = null;
		SmsManager smsManager = SmsManager.getDefault();
		String pareNum = userInfo.getString(DataPreferenceKey.PAREPHONENUM_KEY,
				"15951616861");
		bookMsg = MsgFormat.MSG_HEAD;

		bookMsg = bookMsg
				+ makeMsgItem(MsgFormat.MSGTYPE_TITLE, MsgFormat.MSGTYPE_ORDER);
		String temp = "";
		// name,call number, time,waitlctn,waitaddr
		for (int i = 0; i < 6; i++) {
			switch (i) {
				case 1 :
					temp = makeMsgItem(MsgFormat.NAME_TITLE,
							userInfo.getString(
									DataPreferenceKey.ACCOUNTNAME_KEY, "李小四"));
					break;
				case 2 :
					temp = makeMsgItem(MsgFormat.TELNUM_TITLE,
							userInfo.getString(DataPreferenceKey.MOBILENUM_KEY,
									"15951616861"));
					break;
				case 3 :
					temp = makeMsgItem(MsgFormat.TIME_TITLE,
							userInfo.getString(DataPreferenceKey.WAITTIME_KEY,
									"00:00"));
					break;
				case 4 :
					temp = makeMsgItem(
							MsgFormat.WAITLCTN_TITLE,
							userInfo.getInt(DataPreferenceKey.WAITLATITUDE_KEY,
									1)
									+ ":"
									+ userInfo
											.getInt(DataPreferenceKey.WAITLONGITUDE_KEY,
													1));
					break;
				case 5 :
					temp = makeMsgItem(MsgFormat.WAITADDR_TITLE, mWaitLctnInfo
							.getText().toString());
					break;
			}

			// out the limit of one piece of message
			if (MsgFormat.MSGMAXLENGTH <= (temp.length() + bookMsg.length() + MsgFormat.MSG_END
					.length())) {
				smsManager.sendTextMessage(pareNum, null, bookMsg, null, null);
				Log.i("jason", " message more than  140");
				bookMsg = temp;
			} else {
				bookMsg = bookMsg + temp;
				Log.i("jason", " message else i=" + i + " bookMsg=" + bookMsg);
			}
		}

		smsManager = SmsManager.getDefault();
		bookMsg = bookMsg + MsgFormat.MSG_END;
		smsManager.sendTextMessage(pareNum, null, bookMsg, null, null);// 转发给

		return true;
	}

	protected boolean makeBookMsg() {

		String bookMsg = MsgFormat.MSG_HEAD
				+ makeMsgItem(MsgFormat.MSGTYPE_TITLE,
						MsgFormat.MSGTYPE_ORDERRSULT)
				+ makeMsgItem(MsgFormat.ORDERRSULT_TITLE, MsgFormat.RSULT_YES)
				+ MsgFormat.MSG_END;
		SmsManager smsManager = SmsManager.getDefault();
		String pareNum = userInfo.getString(DataPreferenceKey.PAREPHONENUM_KEY,
				"15951616861");
		smsManager.sendTextMessage(pareNum, null, bookMsg, null, null);// 转发给

		return true;
	}

	protected String makeMsgItem(String title, String content) {
		String item = null;
		item = MsgFormat.TITLE_START + title + MsgFormat.TITLE_END + content
				+ MsgFormat.ITEM_END;
		return item;
	}

	private void updateDisplay() {
		mBookTime.setText(new StringBuilder().append(pad(mHour)).append(" 时 ")
		.append(pad(mMinute)).append(" 分 "));
		userInfo.edit()
		.putString(DataPreferenceKey.WAITTIME_KEY,
				mBookTime.getText().toString()).commit();

	}

	private static String pad(int c) {

		if (c >= 10)

			return String.valueOf(c);

		else

			return "0" + String.valueOf(c);
	}
	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		if (str.length() != 11)
			return false;

		return true;
	}
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {

			case TIME_DIALOG_ID :
				mTimerDialog = new TimePickerDialog(this, mTimeSetListener,
						mHour, mMinute, true);
				return mTimerDialog;

		}

		return null;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onResume() {

		// if (mBMapMan != null) {
		// // mBMapMan.getLocationManager().requestLocationUpdates(
		// // mLocationListener);
		// mBMapMan.start();
		// // mMapController = mMapView.getController();
		// // mMapView.setTraffic(true);
		// }
		super.onResume();

	}
}