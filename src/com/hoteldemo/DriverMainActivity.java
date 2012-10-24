package com.hoteldemo;

import java.io.IOException;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.hoteldemo.MainPannelActivity.SettingMenuClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class DriverMainActivity extends MapActivity
		implements
			OnFocusChangeListener,
			OnClickListener,
			OnCheckedChangeListener {
	static final int ACCOUNT_REQUEST_CODE = 1;
	static final int DRVR_STATE_IDLE = 1;
	static final int DRVR_STATE_SCEDULEDRV = 2;// wait to scedule driver for
												// passenger
	static final int DRVR_STATE_ONTHEWAY = 3;
	int mDriverState = DRVR_STATE_IDLE;

	SharedPreferences mDataPreferenc = null;
	BMapManager mBMapMan = null;
	MapController mMapController = null;
	MapView mMapView = null;
	MKSearch mMapSearch = null;
	MKPoiResult mPoiRsult = null;

	View mPopPassenger = null;
	View mPopPassengerClick = null;
	TextView mPName = null;
	TextView mPTime = null;
	TextView mPTel = null;
	TextView mPAddr = null;

	int mSelPoiRsultItem = 0;
	LocationListener mLocationListener = null;
	LongClickOverlay mLongClickOverlay = new LongClickOverlay(this);
	SettingMenuClickListener mSettingMenuClickLstn = new SettingMenuClickListener();
	View mOrderBtn = null;
	View mSettingBtn = null;

	View mOrderUI = null;
	View mSetting = null;
	View mLastFocusBtn = null;

	AudioManager mAudioManage = null;

	CheckBox mSetCitySetting = null;
	CheckBox mIsVoiceTip = null;
	CheckBox mIsVoiceNtfyDstnc = null;

	EditText mSearchCitySetting = null;
	EditText mParePhoneNum = null;
	EditText mUpdtDrvPeriod = null;
	EditText mNotifyDistance = null;

	PopupWindow mPopupWindow = null;
	Button mCancel = null;
	View mCallCenterImg = null;
	View mCallCenterTxt = null;

	Button mOrderControl = null;
	TextView mOrderState = null;

	LinearLayout mOrderContrlPnnl = null;
	AlertDialog mCntrlDlg = null;

	TextView mOrdrCntrlPName = null;
	TextView mOrdrCntrlPTel = null;
	TextView mOrdrCntrlPTime = null;
	TextView mOrdrCntrlPAddr = null;
	TextView mOrdrCntrlState = null;
	Button mOrdrCntrlCarGo = null;
	Button mOrdrCntrlCancelOrder = null;
	Button mOrdrCntrlCompleteOrder = null;
	Button mOrdrCntrlBack = null;

	View mSettingAccntMng = null;
	Button mSaveSetting = null;
	SmsManager smsManager = SmsManager.getDefault();

	OrderCntrlClickListener mOrderListener = new OrderCntrlClickListener();
	Runnable mReportLctn = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			sendLctnUpdateMsg();
			myHandler.postDelayed(mReportLctn, Integer.parseInt(mDataPreferenc
					.getString(DataPreferenceKey.UPDTDRVPRD_KEY, "2"))*1000*60);
		}
	};
	protected static Handler connectHandlerDrv = null;
	protected Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case DriverService.MSG_COMING :
//					String strMsgType = MsgFormat
//							.getMsgType(MsgFormat.mMsgQuene.get(0));
//					if (strMsgType.equals(MsgFormat.MSGTYPE_ORDER)) {
//						// dealMsgInIdle();
//
//					}
//					Log.i("jason", " strMsgType=" + strMsgType);
//					Toast.makeText(DriverMainActivity.this,
//							" DriverMainActivity type= " + strMsgType,
//							Toast.LENGTH_LONG).show();
//					;

			}
		}
	};

	@Override
	public void onNewIntent(Intent intent) {
		if (intent.getBooleanExtra(DriverService.PASSENGER_MSG, false)) {
			dealPassegnerMsg();
		 }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (null != mPopupWindow && mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
				return true;
			}
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("提示");
			alert.setMessage("尽量 隐藏，不要 退出");
			alert.setPositiveButton("退出",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if(mDriverState != DRVR_STATE_IDLE){
								sendCancelMsg();
								mDriverState = DRVR_STATE_IDLE;
							}
							DriverMainActivity.this.finish();
						}
					});
			alert.setNegativeButton("隐藏",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent MyIntent = new Intent(Intent.ACTION_MAIN);
							MyIntent.addCategory(Intent.CATEGORY_HOME);
							startActivity(MyIntent);
						}
					});
			AlertDialog ad = alert.create();
			ad.show();			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.drivermainpannel);
		mDataPreferenc = this.getSharedPreferences(
				DataPreferenceKey.PREFERENCE_NAME, 0);
		mBMapMan = new BMapManager(getApplication());
		boolean bInit = mBMapMan.init(
				"2D027B3CAA60689DB0A12E08320BCEDB503ACD54", null);
		super.initMapActivity(mBMapMan);
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mBMapMan.getLocationManager().enableProvider(
				MKLocationManager.MK_NETWORK_PROVIDER);
		mAudioManage = (AudioManager) getSystemService(this.AUDIO_SERVICE);
		/***** Location Listener start ***/
		mLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					int mLongitude = (int) (location.getLongitude() * 1E6);
					int mLatitude = (int) (location.getLatitude() * 1E6);
					String strLog = String.format("position:\r\n"
							+ "Longitude:%d\r\n" + "Latitude:%d", mLongitude,
							mLatitude);
//					Toast.makeText(DriverMainActivity.this, strLog,
//							Toast.LENGTH_LONG).show();
					mDataPreferenc
							.edit()
							.putInt(DataPreferenceKey.CURLATITUDE_KEY,
									mLatitude).commit();
					mDataPreferenc
							.edit()
							.putInt(DataPreferenceKey.CURLONGITUDE_KEY,
									mLongitude).commit();
					if (null != mMapController) {
						mMapController = mMapView.getController();
						GeoPoint point = new GeoPoint(mLatitude, mLongitude);// 给定的经纬度构造一个
						mMapController.setCenter(point); // 设置地图中心点
						// mMapController.setZoom(30);// 设置地图 zoom 级别
					}
				}
			}
		};

		/*---- Location Listener end ----*/
		MyLocationOverlay mylocTest = new MyLocationOverlay(this, mMapView);
		MKLocationManager mLocationManager = mBMapMan.getLocationManager();

		mBMapMan.getLocationManager().enableProvider(
				MKLocationManager.MK_NETWORK_PROVIDER);
		// mBMapMan.getLocationManager().disableProvider(MKLocationManager.MK_GPS_PROVIDER);
		mylocTest.enableMyLocation(); // 启用定位
		mylocTest.enableCompass();
		mMapView.getOverlays().add(mylocTest);

		mPopPassenger = getLayoutInflater()
				.inflate(R.layout.poppassenger, null);
		mMapView.addView(mPopPassenger, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));

		mPopPassengerClick = mPopPassenger.findViewById(R.id.click);
		mPName = (TextView) mPopPassenger.findViewById(R.id.pname);
		mPTime = (TextView) mPopPassenger.findViewById(R.id.ptime);
		mPTel = (TextView) mPopPassenger.findViewById(R.id.ptel);
		mPAddr = (TextView) mPopPassenger.findViewById(R.id.paddr);
		mPopPassengerClick.setOnClickListener(this);
		mPopPassenger.setVisibility(View.GONE);

		mOrderBtn = this.findViewById(R.id.mainbtn_order);
		mOrderBtn.setOnFocusChangeListener(this);

		mSettingBtn = this.findViewById(R.id.mainbtn_setting);
		mSettingBtn.setOnFocusChangeListener(this);

		mOrderUI = this.findViewById(R.id.flipui_order);
		mSetting = this.findViewById(R.id.flipui_setting);
		mCallCenterImg = this.findViewById(R.id.callCenterImg);
		mCallCenterImg.setOnClickListener(this);
		mCallCenterTxt = this.findViewById(R.id.callCenterTxt);
		mCallCenterTxt.setOnClickListener(this);

		mSetCitySetting = (CheckBox) this.findViewById(R.id.setting_issetcity);
		mSetCitySetting.setChecked(mDataPreferenc.getBoolean(
				DataPreferenceKey.ISSETCITY_KEY, false));

		mIsVoiceTip = (CheckBox) this.findViewById(R.id.setting_isvoicetip);
		mIsVoiceTip.setChecked(mDataPreferenc.getBoolean(
				DataPreferenceKey.ISVOICE_KEY, true));

		mIsVoiceNtfyDstnc = (CheckBox) this
				.findViewById(R.id.setting_isvoicentfyDstnc);
		mIsVoiceNtfyDstnc.setChecked(mDataPreferenc.getBoolean(
				DataPreferenceKey.ISVOICENTFYDSTNC_KEY, true));

		mSearchCitySetting = (EditText) this
				.findViewById(R.id.setting_searchcity);
		mSearchCitySetting.setText(mDataPreferenc.getString(
				DataPreferenceKey.SEARCHCITY_KEY, "南京"));
		mSearchCitySetting.setSelection(mSearchCitySetting.length());

		mParePhoneNum = (EditText) this.findViewById(R.id.setting_parephonenum);
		mParePhoneNum.setText(mDataPreferenc.getString(
				DataPreferenceKey.PAREPHONENUM_KEY, "15951616861"));
		mParePhoneNum.setSelection(mParePhoneNum.length());

		mUpdtDrvPeriod = (EditText) this.findViewById(R.id.sttng_updtdrvprd);
		mUpdtDrvPeriod.setText(mDataPreferenc.getString(
				DataPreferenceKey.UPDTDRVPRD_KEY, "2"));
		mUpdtDrvPeriod.setSelection(mUpdtDrvPeriod.length());

		mNotifyDistance = (EditText) this.findViewById(R.id.sttng_ntfydstnc);
		mNotifyDistance.setText(mDataPreferenc.getString(
				DataPreferenceKey.NTFYDSTNC_KEY, "2"));
		mNotifyDistance.setSelection(mNotifyDistance.length());

		mSearchCitySetting.setEnabled(mSetCitySetting.isChecked());
		mSetCitySetting.setOnCheckedChangeListener(this);

		mSettingAccntMng = this.findViewById(R.id.settingaccntmng);
		mSettingAccntMng.setOnClickListener(mSettingMenuClickLstn);
		mSaveSetting = (Button) this.findViewById(R.id.savesetting);
		mSaveSetting.setOnClickListener(this);

		mOrderControl = (Button) this.findViewById(R.id.ordercontrol);
		mOrderControl.setOnClickListener(this);

		mOrderState = (TextView) this.findViewById(R.id.orderstate);
		mDriverState = DRVR_STATE_IDLE;
		connectHandlerDrv = myHandler;
		MsgFormat.mMsgQuene.clear();
		Intent mIntent = new Intent(this, DriverService.class);
		// mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startService(mIntent);
	}

	public void dealPassegnerMsg() {
		switch (mDriverState) {
			case DRVR_STATE_IDLE :				
				dealOrderMsgInIdle();
				break;
			default:
				dealMsgNoIdle();
				break;
		}
		
	}
	public void dealOrderMsgInIdle() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("乘客请求订单");
		String msgTotal ="";
		for (int i = 0; i < MsgFormat.mMsgQuene.size(); i++) {
			msgTotal = msgTotal + MsgFormat.mMsgQuene.get(i);
		}
		String strMsgType = MsgFormat
		.getMsgType(msgTotal);
		if (!strMsgType.equals(MsgFormat.MSGTYPE_ORDER)) {
			return;
		}
		String time = "00:11";
		time = MsgFormat.getItemContent(MsgFormat.TIME_TITLE, msgTotal);
		String place = "未知";
		place = MsgFormat.getItemContent(MsgFormat.WAITADDR_TITLE, msgTotal);
		String reqestInfo = "乘客请求:" + "\r\n" + "接人时间：" + time + "\r\n"
				+ "接人地点：" + place + "\r\n" + "是否接受订单?";

		alert.setMessage(reqestInfo);
		alert.setCancelable(false);
		alert.setPositiveButton("拒绝", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				MsgFormat.mMsgQuene.clear();
				sendMsgToPare(makeOrderResultMsg(false));
				dialog.dismiss();
			}
		});

		alert.setNegativeButton("接受", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mDriverState = DRVR_STATE_SCEDULEDRV;
				saveOrderData();
				sendMsgToPare(makeOrderResultMsg(true));
				dialog.dismiss();
				showPassenger();
				updateUIAccdToDrvState();
			}
		});
		AlertDialog ad = alert.create();
		ad.show();
	}

	public void dealMsgNoIdle(){
		String msgTotal = "";
		for (int i = 0; i < MsgFormat.mMsgQuene.size(); i++) {
			msgTotal = msgTotal + MsgFormat.mMsgQuene.get(i);
		}
		String strMsgType = MsgFormat
		.getMsgType(msgTotal);
		if (strMsgType.equals(MsgFormat.MSGTYPE_CANCEL)) {
			myHandler.removeCallbacks(mReportLctn);
			mDriverState = DRVR_STATE_IDLE;
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("乘客请求取消订单");
			alert.setMessage("乘客已取消订单!");
			alert.setPositiveButton("返回", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {					
				}
			});
			updateUIAccdToDrvState();
			AlertDialog ad = alert.create();
			ad.show();
			
			MsgFormat.mMsgQuene.clear();
			return;
		}else if(strMsgType.equals(MsgFormat.MSGTYPE_OVER)){
			myHandler.removeCallbacks(mReportLctn);
			mDriverState = DRVR_STATE_IDLE;			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("乘客请求完成订单");
			alert.setMessage("乘客已结束订单!");
			alert.setPositiveButton("返回", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					
				}
			});
			AlertDialog ad = alert.create();
			ad.show();
			updateUIAccdToDrvState();
			MsgFormat.mMsgQuene.clear();
			return;
		}else{
			MsgFormat.mMsgQuene.clear();
		}
	}
	
	public void saveOrderData() {
		String msgTotal = null;
		for (int i = 0; i < MsgFormat.mMsgQuene.size(); i++) {
			msgTotal = msgTotal + MsgFormat.mMsgQuene.get(i);
		}
		String temp = "";
		temp = MsgFormat.getItemContent(MsgFormat.NAME_TITLE, msgTotal);
		mDataPreferenc.edit().putString(DataPreferenceKey.PSSNGRNAME_KEY, temp)
				.commit();

		temp = MsgFormat.getItemContent(MsgFormat.TELNUM_TITLE, msgTotal);
		mDataPreferenc.edit().putString(DataPreferenceKey.PSSNGRTEL_KEY, temp)
				.commit();

		temp = MsgFormat.getItemContent(MsgFormat.TIME_TITLE, msgTotal);
		mDataPreferenc.edit().putString(DataPreferenceKey.WAITTIME_KEY, temp)
				.commit();

		temp = MsgFormat.getItemContent(MsgFormat.WAITLCTN_TITLE, msgTotal);
		mDataPreferenc.edit().putString(DataPreferenceKey.WAITPOINT_KEY, temp)
				.commit();
		temp = MsgFormat.getItemContent(MsgFormat.WAITADDR_TITLE, msgTotal);

		mDataPreferenc.edit()
				.putString(DataPreferenceKey.WAITLCTNADDR_KEY, temp).commit();
		MsgFormat.mMsgQuene.clear();

	}

	public void showPassenger() {
		String strPoint = mDataPreferenc.getString(
				DataPreferenceKey.WAITPOINT_KEY, "未知");
		int index = strPoint.indexOf(":");
		if (-1 != index) {
			int Latitude = Integer.parseInt(strPoint.subSequence(0, index)
					.toString());
			int Longtitude = Integer.parseInt(strPoint.subSequence(index + 1,
					strPoint.length()).toString());
			GeoPoint p = new GeoPoint(Latitude, Longtitude);
			MapView.LayoutParams geoLP = (MapView.LayoutParams) mPopPassenger
					.getLayoutParams();
			geoLP.point = p;
			mMapView.updateViewLayout(mPopPassenger, geoLP);

			mPopPassenger.setVisibility(View.VISIBLE);
			mPName.setText(mDataPreferenc.getString(
					DataPreferenceKey.PSSNGRNAME_KEY, "未知"));
			mPTime.setText(mDataPreferenc.getString(
					DataPreferenceKey.WAITTIME_KEY, "未知"));
			mPTel.setText(mDataPreferenc.getString(
					DataPreferenceKey.PSSNGRTEL_KEY, "未知"));
			mPAddr.setText(mDataPreferenc.getString(
					DataPreferenceKey.WAITLCTNADDR_KEY, "未知"));
			mMapView.invalidate();
		}
	}

	public void updateUIAccdToDrvState() {
		switch (mDriverState) {
			case DRVR_STATE_IDLE :
				mOrderState.setText(getStatTip());
				mOrderControl.setEnabled(false);
				mSettingBtn.setEnabled(true);
				mPopPassenger.setVisibility(View.INVISIBLE);
				break;
			case DRVR_STATE_SCEDULEDRV :
				mOrderControl.setEnabled(true);
				mOrderState.setText(getStatTip());
				mSettingBtn.setEnabled(false);
				break;
			case DRVR_STATE_ONTHEWAY :
				mOrderControl.setEnabled(true);
				mOrderState.setText(getStatTip());
				break;
		}

	}

	public void resetState() {

	}
	public String makeOrderResultMsg(boolean accept) {
		String result = MsgFormat.MSG_HEAD;
		result = result
				+ MsgFormat.makeMsgItem(MsgFormat.MSGTYPE_TITLE,
						MsgFormat.MSGTYPE_ORDERRSULT);
		if (accept) {
			result = result
					+ MsgFormat.makeMsgItem(MsgFormat.ORDERRSULT_TITLE,
							MsgFormat.RSULT_YES);
		} else {
			result = result
					+ MsgFormat.makeMsgItem(MsgFormat.ORDERRSULT_TITLE,
							MsgFormat.RSULT_NO);
		}
		result = result + MsgFormat.MSG_END;
		return result;
	}

	public boolean sendMsgToPare(String str) {
		String pareNum = mDataPreferenc.getString(
				DataPreferenceKey.PAREPHONENUM_KEY, "15951616861");
		smsManager.sendTextMessage(pareNum, null, str, null, null);
		return true;
	}

	public boolean sendCarStartMsg() {
		String msg = MsgFormat.MSG_HEAD;
		msg = msg
				+ MsgFormat.makeMsgItem(MsgFormat.MSGTYPE_TITLE,
						MsgFormat.MSGTYPE_CRASTART);
		String temp = "";
		// name,call number, time,waitlctn,waitaddr
		for (int i = 0; i < 5; i++) {
			switch (i) {
				case 1 :
					temp = MsgFormat.makeMsgItem(MsgFormat.NAME_TITLE,
							mDataPreferenc.getString(
									DataPreferenceKey.ACCOUNTNAME_KEY, "李小四"));
					break;
				case 2 :
					temp = MsgFormat.makeMsgItem(MsgFormat.TELNUM_TITLE,
							mDataPreferenc.getString(
									DataPreferenceKey.MOBILENUM_KEY,
									"15951616861"));
					break;
				case 3 :
					temp = MsgFormat.makeMsgItem(
							MsgFormat.GPSP_TITLE,
							mDataPreferenc.getInt(
									DataPreferenceKey.CURLATITUDE_KEY, 1)
									+ ":"
									+ mDataPreferenc.getInt(
											DataPreferenceKey.CURLONGITUDE_KEY,
											1));
					break;
				case 4 :
					temp = MsgFormat.makeMsgItem(
							MsgFormat.CARINFO_TITLE,
							mDataPreferenc.getString(
									DataPreferenceKey.CARID_KEY, "未知")
									+ "  "
									+ mDataPreferenc.getString(
											DataPreferenceKey.CARMODEL_KEY,
											"未知")
									+ "  "
									+ mDataPreferenc.getString(
											DataPreferenceKey.CARFEATURE_KEY,
											"未知"));
					break;
			}
			// out the limit of one piece of message
			if (MsgFormat.MSGMAXLENGTH <= (temp.length() + msg.length() + MsgFormat.MSG_END
					.length())) {
				sendMsgToPare(msg);
				msg = temp;
			} else {
				msg = msg + temp;
			}
		}

		msg = msg + MsgFormat.MSG_END;
		sendMsgToPare(msg);
		return true;
	}

	public boolean sendLctnUpdateMsg() {
		String msg = MsgFormat.MSG_HEAD;
		msg = msg
				+ MsgFormat.makeMsgItem(MsgFormat.MSGTYPE_TITLE,
						MsgFormat.MSGTYPE_LCTNUPDATE);
		msg = msg
				+ MsgFormat.makeMsgItem(
						MsgFormat.GPSP_TITLE,
						mDataPreferenc.getInt(
								DataPreferenceKey.CURLATITUDE_KEY, 1)
								+ ":"
								+ mDataPreferenc.getInt(
										DataPreferenceKey.CURLONGITUDE_KEY, 1));		
		msg = msg + MsgFormat.MSG_END;		
		
		return sendMsgToPare(msg);
	}

	public boolean sendCancelMsg() {
		String msg = MsgFormat.MSG_HEAD;
		msg = msg
				+ MsgFormat.makeMsgItem(MsgFormat.MSGTYPE_TITLE,
						MsgFormat.MSGTYPE_CANCEL);
		msg = msg + MsgFormat.MSG_END;
		return sendMsgToPare(msg);
	}

	public boolean sendOverMsg() {
		String msg = MsgFormat.MSG_HEAD;
		msg = msg
				+ MsgFormat.makeMsgItem(MsgFormat.MSGTYPE_TITLE,
						MsgFormat.MSGTYPE_OVER);
		msg = msg + MsgFormat.MSG_END;
		return sendMsgToPare(msg);
	}

	@Override
	protected void onDestroy() {
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		Intent mIntent = new Intent(this, DriverService.class);

		this.stopService(mIntent);
		super.onDestroy();
		connectHandlerDrv = null;
	}

	@Override
	protected void onPause() {
		// if (mBMapMan != null) {
		// mBMapMan.stop();
		// }
		super.onPause();
	}

	@Override
	protected void onResume() {

		if (mBMapMan != null) {
			mBMapMan.getLocationManager().requestLocationUpdates(
					mLocationListener);
			mBMapMan.getLocationManager().setNotifyInternal(70,40);//seconds
			mBMapMan.start();
			mMapController = mMapView.getController();
		}
		super.onResume();
	}
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (v == mOrderBtn) {
			if (hasFocus) {
				if (null != mLastFocusBtn)
					mLastFocusBtn.setPressed(false);
				mLastFocusBtn = mOrderBtn;
				mAudioManage.playSoundEffect(AudioManager.FX_KEY_CLICK);
				mOrderUI.setVisibility(View.VISIBLE);;
				mSetting.setVisibility(View.INVISIBLE);
			}
			if (null != mLastFocusBtn) {
				mLastFocusBtn.setPressed(true);
			}
		} else if (v == mSettingBtn) {
			if (hasFocus) {
				if (null != mLastFocusBtn)
					mLastFocusBtn.setPressed(false);
				mLastFocusBtn = mSettingBtn;
				mAudioManage.playSoundEffect(AudioManager.FX_KEY_CLICK);
				mSetting.setVisibility(View.VISIBLE);

				mOrderUI.setVisibility(View.INVISIBLE);
			}
			if (null != mLastFocusBtn) {
				mLastFocusBtn.setPressed(true);
			}
		} else {
			if (null != mLastFocusBtn) {
				mLastFocusBtn.setPressed(true);
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACCOUNT_REQUEST_CODE) {
			if (resultCode == this.RESULT_OK) {
				int radioId = mDataPreferenc.getInt(DataPreferenceKey.TYPE_KEY,
						0);
				if (DataPreferenceKey.TYPE_PASSENGER == radioId) {
					Toast.makeText(DriverMainActivity.this, "已转为乘客模式，请重新启动 ",
							Toast.LENGTH_LONG).show();
					Intent mIntent = new Intent(this, DriverService.class);
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					this.stopService(mIntent);
					this.finish();
				}
			}
		}
	}

	class SettingMenuClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == mSettingAccntMng) {
				Intent i = new Intent(DriverMainActivity.this,
						AccountManageActivity.class);
				startActivityForResult(i, ACCOUNT_REQUEST_CODE);
			}
		}
	}

	class OrderCntrlClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == mOrdrCntrlCancelOrder) {
				sendCancelMsg();
				myHandler.removeCallbacks(mReportLctn);
				mDriverState = DRVR_STATE_IDLE;
			} else if (v == mOrdrCntrlCompleteOrder) {
				sendOverMsg();
				myHandler.removeCallbacks(mReportLctn);
				mDriverState = DRVR_STATE_IDLE;
			} else if (v == mOrdrCntrlCarGo) {
				sendCarStartMsg();
				mDriverState = DRVR_STATE_ONTHEWAY;
				
				myHandler.postDelayed(mReportLctn, Integer.parseInt(mDataPreferenc
						.getString(DataPreferenceKey.UPDTDRVPRD_KEY, "2"))*1000*60);
			} else if (v == mOrdrCntrlBack) {
				;
			}

			updateUIAccdToDrvState();
			mCntrlDlg.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mSaveSetting) {

			mDataPreferenc
					.edit()
					.putString(DataPreferenceKey.SEARCHCITY_KEY,
							mSearchCitySetting.getText().toString()).commit();
			mDataPreferenc
					.edit()
					.putString(DataPreferenceKey.PAREPHONENUM_KEY,
							mParePhoneNum.getText().toString()).commit();
			mDataPreferenc
					.edit()
					.putString(DataPreferenceKey.UPDTDRVPRD_KEY,
							mUpdtDrvPeriod.getText().toString()).commit();

			mDataPreferenc
					.edit()
					.putString(DataPreferenceKey.NTFYDSTNC_KEY,
							mNotifyDistance.getText().toString()).commit();

			mDataPreferenc
					.edit()
					.putBoolean(DataPreferenceKey.ISSETCITY_KEY,
							mSetCitySetting.isChecked()).commit();
			mDataPreferenc
					.edit()
					.putBoolean(DataPreferenceKey.ISVOICE_KEY,
							mIsVoiceTip.isChecked()).commit();
			mDataPreferenc
					.edit()
					.putBoolean(DataPreferenceKey.ISVOICENTFYDSTNC_KEY,
							mIsVoiceNtfyDstnc.isChecked()).commit();
			Toast.makeText(this, "已保存当前设置", Toast.LENGTH_LONG).show();
		} else if (v == this.mPopPassengerClick) {
			String inputStr = mDataPreferenc.getString(
					DataPreferenceKey.PSSNGRTEL_KEY, "未知");
			Intent myIntentDial = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + inputStr));
			startActivity(myIntentDial);
		} else if (v == mCallCenterTxt || v == mCallCenterImg) {
			LayoutInflater mLayoutInflater = (LayoutInflater) this
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View music_popunwindwow = mLayoutInflater.inflate(
					R.layout.callcenter, null);
			mCancel = (Button) music_popunwindwow.findViewById(R.id.btnCancel);
			mPopupWindow = new PopupWindow(music_popunwindwow,
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			mPopupWindow.setOutsideTouchable(true);

			mPopupWindow.showAtLocation(findViewById(R.id.topPannel),
					Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
			mCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mPopupWindow.dismiss();
				}
			});
		} else if (v == mOrderControl) {
			orderControl();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (mSetCitySetting == buttonView) {
			mSearchCitySetting.setEnabled(isChecked);
		}
	}

	public void orderControl() {
		AlertDialog.Builder mOrderCntrlDlg = new AlertDialog.Builder(this);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(this.LAYOUT_INFLATER_SERVICE);
		mOrderContrlPnnl = (LinearLayout) inflater.inflate(
				R.layout.ordercontrol, null);
		mOrderCntrlDlg.setView(mOrderContrlPnnl);

		mOrdrCntrlPName = (TextView) mOrderContrlPnnl.findViewById(R.id.pname);
		mOrdrCntrlPTel = (TextView) mOrderContrlPnnl.findViewById(R.id.ptel);
		mOrdrCntrlPTime = (TextView) mOrderContrlPnnl.findViewById(R.id.ptime);
		mOrdrCntrlPAddr = (TextView) mOrderContrlPnnl.findViewById(R.id.paddr);
		mOrdrCntrlState = (TextView) mOrderContrlPnnl
				.findViewById(R.id.carstate);

		mOrdrCntrlCarGo = (Button) mOrderContrlPnnl.findViewById(R.id.cargo);
		mOrdrCntrlCancelOrder = (Button) mOrderContrlPnnl
				.findViewById(R.id.cancelorder);
		mOrdrCntrlCompleteOrder = (Button) mOrderContrlPnnl
				.findViewById(R.id.completeorder);
		mOrdrCntrlBack = (Button) mOrderContrlPnnl.findViewById(R.id.back);

		mOrdrCntrlPName.setText(mDataPreferenc.getString(
				DataPreferenceKey.PSSNGRNAME_KEY, "未知"));
		mOrdrCntrlPTime.setText(mDataPreferenc.getString(
				DataPreferenceKey.WAITTIME_KEY, "未知"));
		mOrdrCntrlPTel.setText(mDataPreferenc.getString(
				DataPreferenceKey.PSSNGRTEL_KEY, "未知"));
		mOrdrCntrlPAddr.setText(mDataPreferenc.getString(
				DataPreferenceKey.WAITLCTNADDR_KEY, "未知"));
		mOrdrCntrlState.setText(getStatTip());

		if (mDriverState == DRVR_STATE_SCEDULEDRV) {
			mOrdrCntrlCarGo.setEnabled(true);
		} else {
			mOrdrCntrlCarGo.setEnabled(false);
		}

		mOrdrCntrlCarGo.setOnClickListener(mOrderListener);
		mOrdrCntrlCancelOrder.setOnClickListener(mOrderListener);
		mOrdrCntrlCompleteOrder.setOnClickListener(mOrderListener);
		mOrdrCntrlBack.setOnClickListener(mOrderListener);

		mCntrlDlg = mOrderCntrlDlg.show();
	}

	public String getStatTip() {
		switch (mDriverState) {
			case DRVR_STATE_IDLE :
				return ("暂无订单");
			case DRVR_STATE_SCEDULEDRV :
				return ("等待派车");
			case DRVR_STATE_ONTHEWAY :
				return ("接客途中");
			default :
				return "";
		}
	}
}
