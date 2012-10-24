package com.hoteldemo;

import java.io.FileOutputStream;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.PoiOverlay;

import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.hoteldemo.DriverMainActivity.OrderCntrlClickListener;
import com.hoteldemo.HotelDemoActivity.GetOverlay;
import com.hoteldemo.HotelDemoActivity.OverItemTA;

public class MainPannelActivity extends MapActivity
		implements
			View.OnClickListener,
			View.OnFocusChangeListener,
			OnCheckedChangeListener,
			OnInitListener {
	static final int ACCOUNT_REQUEST_CODE = 1;
	static final int BOOKCAR_REQUEST_CODE = 2;

	static final int PSSNGER_ST_IDLE = 0;
	static final int PSSNGER_ST_WAITORDERRESULT = 1;
	static final int PSSNGER_ST_WAITSCEDULECAR = 2;
	static final int PSSNGER_ST_WAITCAR = 3;

	int mPassengerST = PSSNGER_ST_IDLE;

	BMapManager mBMapMan = null;
	MapController mMapController = null;
	MapView mMapView = null;
	MKSearch mMapSearch = null;
	MKPoiResult mPoiRsult = null;
	int mSelPoiRsultItem = 0;
	LocationListener mLocationListener = null;
	LongClickOverlay mLongClickOverlay = new LongClickOverlay(this);

	View mOneKeyCallBtn = null;
	View mOrderBtn = null;
	View mSettingBtn = null;
	View mLastFocusBtn = null;

	View mOneKeyCallUI = null;
	View mOrderUI = null;
	View mSetting = null;

	View mCallCenterImg = null;
	View mCallCenterTxt = null;
	Button mLocationSearch = null;
	Button mCarTakeMe = null;
	View mSearchTip = null;
	EditText mSearchInput = null;

	Button mCancel = null;
	PopupWindow mPopupWindow = null;

	View mOrderMenuOneCall = null;
	View mOrderMenuCallCar = null;
	View mOrderMenuAccountManage = null;

	OrderMenuClickListener mOrderMenuClickLstn = new OrderMenuClickListener();
	SettingMenuClickListener mSettingMenuClickLstn = new SettingMenuClickListener();
	boolean isJustCreate = false;
	OrderCntrlClickListener mOrderListener = new OrderCntrlClickListener();
	ShowInMapClickListener mShowPointListener = new ShowInMapClickListener();
	
	AudioManager mAudioManage = null;
	View mPopWaitLctn = null;
	TextView mPopWaitAddr = null;
	TextView mPopWaitTitle = null;

	View mPopCarLctn = null;

	View mPopWaitLctnClick = null;

	View mPopCarLctnClick = null;
	TextView mDName = null;
	TextView mDTel = null;
	TextView mDCarInfo = null;

	LinearLayout mEditPopWaitlayout = null;
	EditText mEditPopWaitTitle = null;
	EditText mEditPopWaitAddr = null;

	SharedPreferences mDataPreferenc = null;
	boolean mIsSetWaitLocation = false;

	String mSearchCity = "南京";

	String mLocationCity = "南京";
	boolean mNeedGetCity = true;

	CheckBox mSetCitySetting = null;
	CheckBox mIsVoiceTip = null;
	CheckBox mIsVoiceNtfyDstnc = null;

	EditText mSearchCitySetting = null;
	EditText mParePhoneNum = null;
	EditText mUpdtDrvPeriod = null;
	EditText mNotifyDistance = null;

	View mSettingAccntMng = null;
	Button mSaveSetting = null;

	ProgressDialog mpDialog = null;
	Runnable mOrderTmOutRun = null;

	View mOrder_search_o = null;
	View mOrder_search_s = null;

	View mLctncntrl_callkey_l = null;
	View mLctncntrl_callkey_c = null;

	TextView mOrderCarST = null;
	Button mControlInOrder = null;
	TextView mOrdrCntrlPName = null;
	TextView mOrdrCntrlPTel = null;
	TextView mOrdrCntrlPTime = null;
	TextView mOrdrCntrlPAddr = null;
	LinearLayout mOrderContrlPnnl = null;
	AlertDialog mCntrlDlg = null;
	TextView mOrdrCntrlState = null;
	Button mOrdrCntrlCarGo = null;
	Button mOrdrCntrlCancelOrder = null;
	Button mOrdrCntrlCompleteOrder = null;
	Button mOrdrCntrlBack = null;
	
	ImageButton mShowCurLctn = null;
	ImageButton mShowDrv = null;
	SmsManager smsManager = SmsManager.getDefault();
	private TextToSpeech mTTS;
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	protected static Handler handlForService = new Handler() {
		public void handleMessage(Message msg) {
			if (null != connectHandler)
				connectHandler.sendMessage(msg);
		}
	};

	protected static Handler connectHandler = null;
	protected Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			}
		}
	};

	public void dealMsg() {
		myHandler.removeCallbacks(mOrderTmOutRun);
		mpDialog.cancel();
	}
	public void dealDriverMsg() {
		switch (this.mPassengerST) {
			case PSSNGER_ST_WAITORDERRESULT :
				dealOrderFeedBack();
				break;
			case PSSNGER_ST_WAITSCEDULECAR :
				dealStartCarMsg();
				break;
			case PSSNGER_ST_IDLE :
				dealCancelOverMsg();
				break;
			case PSSNGER_ST_WAITCAR :
				dealUpdateMsg();
				break;
			default :
				dealCancelOverMsg();
				break;
		}
	}

	public void dealUpdateMsg() {
		String msgTotal = "";
		for (int i = 0; i < MsgFormat.mMsgQuene.size(); i++) {
			msgTotal = msgTotal + MsgFormat.mMsgQuene.get(i);
		}
		String strMsgType = MsgFormat.getMsgType(msgTotal);
			if (strMsgType.equals(MsgFormat.MSGTYPE_LCTNUPDATE)) {
			String temp = MsgFormat.getItemContent(MsgFormat.GPSP_TITLE,
					msgTotal);
			mDataPreferenc.edit()
					.putString(DataPreferenceKey.DRVLCTN_KEY, temp).commit();
			MsgFormat.mMsgQuene.clear();
			showDriverLctn();
		} else {
			dealCancelOverMsg();
		}
	}
	public void dealStartCarMsg() {
		String msgTotal = "";
		for (int i = 0; i < MsgFormat.mMsgQuene.size(); i++) {
			msgTotal = msgTotal + MsgFormat.mMsgQuene.get(i);
		}
		String strMsgType = MsgFormat.getMsgType(msgTotal);
		if (strMsgType.equals(MsgFormat.MSGTYPE_CRASTART)) {
			String temp = "";
			temp = MsgFormat.getItemContent(MsgFormat.NAME_TITLE, msgTotal);
			mDataPreferenc.edit()
					.putString(DataPreferenceKey.DRVRNAME_KEY, temp).commit();
	
			temp = MsgFormat.getItemContent(MsgFormat.TELNUM_TITLE, msgTotal);
			mDataPreferenc.edit()
					.putString(DataPreferenceKey.DRVRTEL_KEY, temp).commit();

			temp = MsgFormat.getItemContent(MsgFormat.GPSP_TITLE, msgTotal);
			mDataPreferenc.edit()
					.putString(DataPreferenceKey.DRVLCTN_KEY, temp).commit();

			temp = MsgFormat.getItemContent(MsgFormat.CARINFO_TITLE, msgTotal);
			mDataPreferenc.edit()
					.putString(DataPreferenceKey.DRVCARINFO_KEY, temp).commit();
			MsgFormat.mMsgQuene.clear();
			mPassengerST = PSSNGER_ST_WAITCAR;
			updateUIAccdToPssgState();
			showDriverLctn();
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("服务订单");
			alert.setMessage("司机正在赶来!");
			alert.setPositiveButton("返回",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
			AlertDialog ad = alert.create();
			ad.show();
		} else {
			dealCancelOverMsg();
		}
	}

	public void showDriverLctn() {
		String strPoint = mDataPreferenc.getString(
				DataPreferenceKey.DRVLCTN_KEY, "未知");
		int index = strPoint.indexOf(":");
		if (-1 != index) {
			int dLatitude = Integer.parseInt(strPoint.subSequence(0, index)
					.toString());
			int dLongtitude = Integer.parseInt(strPoint.subSequence(index + 1,
					strPoint.length()).toString());
			GeoPoint p = new GeoPoint(dLatitude, dLongtitude);
			MapView.LayoutParams geoLP = (MapView.LayoutParams) mPopCarLctn
					.getLayoutParams();
			geoLP.point = p;
			mMapView.updateViewLayout(mPopCarLctn, geoLP);

			mPopCarLctn.setVisibility(View.VISIBLE);
			mDName.setText(mDataPreferenc.getString(
					DataPreferenceKey.DRVRNAME_KEY, "未知"));
			mDTel.setText(mDataPreferenc.getString(
					DataPreferenceKey.DRVRTEL_KEY, "未知"));
			mDCarInfo.setText(mDataPreferenc.getString(
					DataPreferenceKey.DRVCARINFO_KEY, "未知"));
			int wLatitude = this.mDataPreferenc.getInt(
					DataPreferenceKey.WAITLATITUDE_KEY, dLatitude);
			int wLong = this.mDataPreferenc.getInt(
					DataPreferenceKey.WAITLONGITUDE_KEY, dLongtitude);
			int latPoint = (int) (((float) dLatitude + (float) wLatitude) / 2.0);
			int longPoint = (int) ((((float) dLongtitude + (float) wLong) / 2.0));
			mMapView.getController().setCenter(
					new GeoPoint(dLatitude, dLongtitude));
			mMapView.getController().zoomToSpan((int)Math.abs(((float)(latPoint-dLatitude)*2.5)), (int)Math.abs((longPoint-dLongtitude)*2.5));
			mMapView.invalidate();
			// Calculate the distance between these two points
			float[] results = new float[1];
			Location.distanceBetween(((double)wLatitude)/1E6, ((double)wLong)/1E6, ((double)dLatitude)/1E6, ((double)dLongtitude)/1E6,
					results);	
					// results[0];
			String distance =  Float.valueOf(results[0] / 1000).toString();
			if(distance.indexOf('.') > 0){
				distance = distance.substring(0,distance.indexOf('.')+2);
			}
			String tip = "车距:" + distance
					+ " 千米";
			speachTxt(tip);
			mOrderCarST.setText(tip);
		}
	}

	public void dealOrderFeedBack() {
		String msgTotal = "";
		for (int i = 0; i < MsgFormat.mMsgQuene.size(); i++) {
			msgTotal = msgTotal + MsgFormat.mMsgQuene.get(i);
		}
		String strMsgType = MsgFormat.getMsgType(msgTotal);
		if (strMsgType.equals(MsgFormat.MSGTYPE_ORDERRSULT)) {
			String result = MsgFormat.getItemContent(
					MsgFormat.ORDERRSULT_TITLE, msgTotal);
				if (result.equals(MsgFormat.RSULT_YES)) {
				myHandler.removeCallbacks(mOrderTmOutRun);
				mPassengerST = PSSNGER_ST_WAITSCEDULECAR;
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("服务订单");
				alert.setMessage("成功，预定!请准时候车!");
				alert.setPositiveButton("返回",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				mpDialog.cancel();
				updateUIAccdToPssgState();
				AlertDialog ad = alert.create();
				ad.show();
				MsgFormat.mMsgQuene.clear();
			} else {
				myHandler.removeCallbacks(mOrderTmOutRun);
				mPassengerST = PSSNGER_ST_IDLE;
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("服务订单");
				alert.setMessage("失败 ! 无法提供服务！");
				alert.setPositiveButton("返回",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				updateUIAccdToPssgState();
				AlertDialog ad = alert.create();
				ad.show();
				MsgFormat.mMsgQuene.clear();
				mpDialog.cancel();
			}

		} else {
			dealCancelOverMsg();
		}

	}
	public void dealCancelOverMsg() {
		String msgTotal = "";
		for (int i = 0; i < MsgFormat.mMsgQuene.size(); i++) {
			msgTotal = msgTotal + MsgFormat.mMsgQuene.get(i);
		}
		String strMsgType = MsgFormat.getMsgType(msgTotal);
			if (strMsgType.equals(MsgFormat.MSGTYPE_CANCEL)) {
			myHandler.removeCallbacks(mOrderTmOutRun);
			mPassengerST = PSSNGER_ST_IDLE;
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("请求取消订单");
			alert.setMessage("服务中心已取消订单!");
			alert.setPositiveButton("返回",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
			updateUIAccdToPssgState();
			AlertDialog ad = alert.create();
			ad.show();

			MsgFormat.mMsgQuene.clear();
			return;
		} else if (strMsgType.equals(MsgFormat.MSGTYPE_OVER)) {
			myHandler.removeCallbacks(mOrderTmOutRun);
			mPassengerST = PSSNGER_ST_IDLE;
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("请求取消订单");
			alert.setMessage("服务中心已取消订单!");
			alert.setPositiveButton("返回",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});
			AlertDialog ad = alert.create();
			ad.show();
			updateUIAccdToPssgState();
			MsgFormat.mMsgQuene.clear();
			return;
		} else {
			MsgFormat.mMsgQuene.clear();
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
			if (intent.getBooleanExtra(PassengerService.DRIVER_MSG, false)) {
			dealDriverMsg();
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.getIntent();
		mDataPreferenc = this.getSharedPreferences(
				DataPreferenceKey.PREFERENCE_NAME, 0);

		setContentView(R.layout.mainpannel);

		mTTS = new TextToSpeech(getApplicationContext(), this);

		int result = mTTS.setLanguage(Locale.CHINA);
		if (result == TextToSpeech.LANG_MISSING_DATA
				|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
			;
		}
		isJustCreate = true;

		mOneKeyCallBtn = this.findViewById(R.id.mainbtn_onekeycall);
		mOneKeyCallBtn.setOnFocusChangeListener(this);

		mOrderBtn = this.findViewById(R.id.mainbtn_order);
		mOrderBtn.setOnFocusChangeListener(this);

		mSettingBtn = this.findViewById(R.id.mainbtn_setting);
		mSettingBtn.setOnFocusChangeListener(this);
		mSettingAccntMng = this.findViewById(R.id.settingaccntmng);
		mSettingAccntMng.setOnClickListener(mSettingMenuClickLstn);

		mOneKeyCallUI = this.findViewById(R.id.flipui_onekeycall);
		mCallCenterImg = this.findViewById(R.id.callCenterImg);
		mCallCenterImg.setOnClickListener(this);
		mCallCenterTxt = this.findViewById(R.id.callCenterTxt);
		mCallCenterTxt.setOnClickListener(this);
		mLocationSearch = (Button) this
				.findViewById(R.id.onekeycall_search_btn);
		mLocationSearch.setOnClickListener(this);
		mCarTakeMe = (Button) this.findViewById(R.id.onekeycall_takeme_btn);
		mCarTakeMe.setOnClickListener(this);
		mSearchTip = this.findViewById(R.id.akeycall_searchtip);
		mSearchInput = (EditText) this.findViewById(R.id.akeycall_searchinput);
		mSearchInput.setOnFocusChangeListener(this);

		mOrderUI = this.findViewById(R.id.flipui_order);
		mOrderMenuOneCall = this.findViewById(R.id.ordermenu_onecall);
		mOrderMenuOneCall.setOnClickListener(mOrderMenuClickLstn);

		mOrderMenuCallCar = this.findViewById(R.id.ordermenu_callcar);
		mOrderMenuCallCar.setOnClickListener(mOrderMenuClickLstn);

		mOrderMenuAccountManage = this
				.findViewById(R.id.ordermenu_accountmanage);
		mOrderMenuAccountManage.setOnClickListener(mOrderMenuClickLstn);

		mSetting = this.findViewById(R.id.flipui_setting);

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

		if (!mSetCitySetting.isChecked()) {
			mSearchCitySetting.setEnabled(false);
		}

		mSetCitySetting.setOnCheckedChangeListener(this);

		mSaveSetting = (Button) this.findViewById(R.id.savesetting);
		mSaveSetting.setOnClickListener(this);

		mBMapMan = new BMapManager(getApplication());
		boolean bInit = mBMapMan.init(
				"2D027B3CAA60689DB0A12E08320BCEDB503ACD54", null);
		super.initMapActivity(mBMapMan);

		mMapView = (MapView) findViewById(R.id.bmapsView);

		/***** Location Listener start ***/
		mLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					int mLongitude = (int) (location.getLongitude() * 1E6);
					int mLatitude = (int) (location.getLatitude() * 1E6);
					mDataPreferenc.edit()
					.putInt(DataPreferenceKey.CURLATITUDE_KEY,mLatitude)
					.commit();
					mDataPreferenc
					.edit()
					.putInt(DataPreferenceKey.CURLONGITUDE_KEY, mLongitude)
					.commit();
					
					
					if (null != mMapController) {
						mMapController = mMapView.getController(); // 得到
						// mMapView
						// 的控制权,可以用它控制和驱动平移和缩放
						GeoPoint point = new GeoPoint(mLatitude, mLongitude);// 给定的经纬度构造一个
						// GeoPoint,单位是微度
						// (度
						// *
						// 1E6)

						if (!mIsSetWaitLocation) {
							mMapSearch.reverseGeocode(point);
						}
						if (mPassengerST == PSSNGER_ST_IDLE)
							mMapController.setCenter(point); // 设置地图中心点
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
		// 启用指南针
		mMapView.getOverlays().add(mylocTest);

		mAudioManage = (AudioManager) getSystemService(this.AUDIO_SERVICE);
		mMapSearch = new MKSearch();
		/***** Map Search Listener start ***/
		mMapSearch.init(mBMapMan, new MKSearchListener() {
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				if (error != 0 || res == null) {
					Toast.makeText(MainPannelActivity.this, "error",
							Toast.LENGTH_LONG).show();
					return;
				}
				CharSequence[] poiItems = null;
				if (res.getCurrentNumPois() > 0) {
					MKPoiInfo tempInfo = null;
					poiItems = new CharSequence[res.getCurrentNumPois()];
					for (int i = 0; i < res.getCurrentNumPois(); i++) {
						tempInfo = res.getPoi(i);
						poiItems[i] = tempInfo.name + "\r\n" + "--"
								+ tempInfo.address;
					}
					if (null != poiItems) {
						mPoiRsult = res;
						alertDlgSearchLocation(poiItems);

					}
					// PoiOverlay poiOverlay = new PoiOverlay(
					// MainPannelActivity.this, mMapView);
					// poiOverlay.setData(res.getAllPoi());
					// mMapView.getOverlays().clear();
					// mMapView.getOverlays().add(poiOverlay);
					// mMapView.getOverlays().add(mLongClickOverlay);
					// mMapView.invalidate();
					// mMapView.getController().animateTo(res.getPoi(0).pt);
				} else if (res.getCityListNum() > 0) {
					String strInfo = "��";
					for (int i = 0; i < res.getCityListNum(); i++) {
						strInfo += res.getCityListInfo(i).city;
						strInfo += ",";
					}
					strInfo += "�ҵ����";
					Toast.makeText(MainPannelActivity.this, strInfo,
							Toast.LENGTH_LONG).show();
				}
			}
			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0 || res == null) {
					Toast.makeText(MainPannelActivity.this, "请重新选择",
							Toast.LENGTH_LONG).show();
					return;
				}

				String strTitle = "到这里来接我";
				mPopWaitTitle.setText(strTitle);

				if (mNeedGetCity) {
					mLocationCity = getCityFromAddr(res.strAddr);
					mNeedGetCity = false;
				}

				String strAdrr = res.strAddr;
				strAdrr = (strAdrr.substring(0, (strAdrr.length() > 15
						? 15
						: (strAdrr.length()))));
				if (res.strAddr.length() > 15) {
					strAdrr = strAdrr + "...";
				}
				mPopWaitAddr.setText(strAdrr);
				saveWaitLctnInfo(strTitle, strAdrr, res.geoPt.getLatitudeE6(),
						res.geoPt.getLongitudeE6());
				showPopuOnMap(mPopWaitLctn, res.geoPt);

			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}
		});

		/*---- Map Search Listener end ----*/
		mMapView.getOverlays().add(mLongClickOverlay);

		mPopWaitLctn = getLayoutInflater().inflate(R.layout.poplctninfo, null);
		mMapView.addView(mPopWaitLctn, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));
		mPopWaitLctn.setVisibility(View.GONE);
		mPopWaitLctnClick = mPopWaitLctn.findViewById(R.id.click);
		mPopWaitAddr = (TextView) mPopWaitLctn
				.findViewById(R.id.map_bubbleText);
		mPopWaitTitle = (TextView) mPopWaitLctn
				.findViewById(R.id.map_bubbleTitle);
		mPopWaitLctnClick.setOnClickListener(this);

		mPopCarLctn = getLayoutInflater().inflate(R.layout.popcarinmap, null);
		mMapView.addView(mPopCarLctn, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));
		mPopCarLctn.setVisibility(View.GONE);
		mPopCarLctnClick = mPopCarLctn.findViewById(R.id.click);
		mPopCarLctnClick.setOnClickListener(this);
		mDName = (TextView) mPopCarLctn.findViewById(R.id.dname);
		mDTel = (TextView) mPopCarLctn.findViewById(R.id.dtel);
		mDCarInfo = (TextView) mPopCarLctn.findViewById(R.id.dcarinfo);

		mOrder_search_o = this.findViewById(R.id.order_search_o);
		mOrder_search_s = this.findViewById(R.id.order_search_s);
		mLctncntrl_callkey_l = this.findViewById(R.id.lctncntrl_callkey_l);
		mLctncntrl_callkey_c = this.findViewById(R.id.lctncntrl_callkey_c);
		mOrderCarST = (TextView) this.findViewById(R.id.ordercarstate);
		mControlInOrder = (Button) this.findViewById(R.id.controlinorder);
		mControlInOrder.setOnClickListener(this);

		mShowDrv = (ImageButton) this.findViewById(R.id.showdrv_inmap);
		mShowDrv.setOnClickListener(mShowPointListener);
		mShowCurLctn = (ImageButton) this.findViewById(R.id.showcurlctn_inmap);
		mShowCurLctn.setOnClickListener(mShowPointListener);
		
		connectHandler = myHandler;
		mPassengerST = PSSNGER_ST_IDLE;
		MsgFormat.mMsgQuene.clear();
		Intent mIntent = new Intent(this, PassengerService.class);
		this.startService(mIntent);
	}
	public void showPopuOnMap(int x, int y) {
		GeoPoint p = mMapView.getProjection().fromPixels(x, y);
		MapView.LayoutParams geoLP = (MapView.LayoutParams) mPopWaitLctn
				.getLayoutParams();
		geoLP.point = p;
		mMapView.updateViewLayout(mPopWaitLctn, geoLP);
		mPopWaitLctn.setVisibility(View.VISIBLE);

		GeoPoint pcar = mMapView.getProjection().fromPixels(x + 50, y + 50);
		MapView.LayoutParams geoLPcar = (MapView.LayoutParams) mPopCarLctn
				.getLayoutParams();
		geoLPcar.point = pcar;
		mMapView.updateViewLayout(mPopCarLctn, geoLPcar);
		mPopCarLctn.setVisibility(View.VISIBLE);

		mMapView.invalidate();
	}

	public void showPopuOnMap(View popView, GeoPoint p) {
		MapView.LayoutParams geoLP = (MapView.LayoutParams) popView
				.getLayoutParams();
		geoLP.point = p;
		mDataPreferenc.edit()
				.putInt(DataPreferenceKey.WAITLATITUDE_KEY, p.getLatitudeE6())
				.commit();
		mDataPreferenc
				.edit()
				.putInt(DataPreferenceKey.WAITLONGITUDE_KEY, p.getLongitudeE6())
				.commit();

		mMapView.updateViewLayout(popView, geoLP);
		popView.setVisibility(View.VISIBLE);
		mMapView.getController().animateTo(p);
		mMapView.invalidate();
	}

	public String getCityFromAddr(String addr) {
		int location1 = -1;
		int location2 = -1;
		String tempAddr = null;
		if (addr != null) {
			location1 = addr.indexOf("省");
			location2 = addr.indexOf("市");
			if (location1 < 0)
				location1 = 0;
			if (location2 > location1) {
				return addr.substring(location1 == 0 ? 0 : (location1 + 1),
						location2);
			}
			if (location2 < 0)
				location2 = 0;

			if (location2 > location1) {
				return addr.substring(location1 + 1, location2);
			}
		}
		return null;
	}

	public void getSearchCity() {
		if (mSetCitySetting.isChecked()) {
			mSearchCity = mSearchCitySetting.getText().toString();
			return;
		} else {
			mSearchCity = mLocationCity;
		}
		return;
	}

	protected void saveWaitLctnInfo(String lctnTitle, String lctnAddr,
			int Latitude, int Longitude) {

		if (null != lctnTitle) {
			mDataPreferenc.edit()
					.putString(DataPreferenceKey.WAITLCTNTITLE_KEY, lctnTitle)
					.commit();
		}

		if (null != lctnAddr) {
			mDataPreferenc.edit()
					.putString(DataPreferenceKey.WAITLCTNADDR_KEY, lctnAddr)
					.commit();
		}
		mDataPreferenc.edit()
				.putInt(DataPreferenceKey.WAITLATITUDE_KEY, Latitude).commit();
		mDataPreferenc.edit()
				.putInt(DataPreferenceKey.WAITLONGITUDE_KEY, Longitude)
				.commit();

	}

	protected void saveWaitLctnInfo(String lctnTitle, String lctnAddr) {

		if (null != lctnTitle) {
			mDataPreferenc.edit()
					.putString(DataPreferenceKey.WAITLCTNTITLE_KEY, lctnTitle)
					.commit();
		}

		if (null != lctnAddr) {
			mDataPreferenc.edit()
					.putString(DataPreferenceKey.WAITLCTNADDR_KEY, lctnAddr)
					.commit();
		}
	}

	public void onLongPress(MotionEvent e) {
		if (mPassengerST == this.PSSNGER_ST_IDLE) {
			float x = e.getX();
			float y = e.getY();
			GeoPoint geo = mMapView.getProjection()
					.fromPixels((int) x, (int) y);
			mIsSetWaitLocation = true;
			mMapSearch.reverseGeocode(geo);
		}
	}
	public void speachTxt(String str){
		if(mDataPreferenc.getBoolean(
				DataPreferenceKey.ISVOICE_KEY, true))
		mTTS.speak(str, TextToSpeech.QUEUE_FLUSH, null);
	}
	private void alertDlgSearchLocation(CharSequence[] showItems) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("请选择乘车地址:");
		alert.setSingleChoiceItems(showItems, -1,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int item) {
						/* User clicked on a radio button do some stuff */
						mSelPoiRsultItem = item;
					}
				});

		alert.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
//				Toast.makeText(MainPannelActivity.this,
//						mPoiRsult.getPoi(mSelPoiRsultItem).name,
//						Toast.LENGTH_LONG).show();

				String strAdrr = mPoiRsult.getPoi(mSelPoiRsultItem).address;
				strAdrr = (strAdrr.substring(0, (strAdrr.length() > 15
						? 15
						: (strAdrr.length()))));
				if (mPoiRsult.getPoi(mSelPoiRsultItem).address.length() > 15) {
					strAdrr = strAdrr + "...";
				}
				mPopWaitAddr.setText(strAdrr);

				strAdrr = mPoiRsult.getPoi(mSelPoiRsultItem).name;
				strAdrr = (strAdrr.substring(0, (strAdrr.length() > 15
						? 15
						: (strAdrr.length()))));
				if (mPoiRsult.getPoi(mSelPoiRsultItem).name.length() > 8) {
					strAdrr = strAdrr + "..";
				}
				mPopWaitTitle.setText("到 " + strAdrr + " 接我");
				mIsSetWaitLocation = true;
				saveWaitLctnInfo(mPopWaitTitle.getText().toString(),
						mPopWaitAddr.getText().toString(),
						mPoiRsult.getPoi(mSelPoiRsultItem).pt.getLatitudeE6(),
						mPoiRsult.getPoi(mSelPoiRsultItem).pt.getLongitudeE6());
				showPopuOnMap(mPopWaitLctn,
						mPoiRsult.getPoi(mSelPoiRsultItem).pt);
			}
		});

		alert.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog ad = alert.create();
		ad.show();
	}

	public void search() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(this.LAYOUT_INFLATER_SERVICE);
		mEditPopWaitlayout = (LinearLayout) inflater.inflate(
				R.layout.dialogeditview, null);
		dialog.setView(mEditPopWaitlayout);

		mEditPopWaitTitle = (EditText) mEditPopWaitlayout
				.findViewById(R.id.title);
		mEditPopWaitTitle.setText(mPopWaitTitle.getText().toString());
		mEditPopWaitTitle.setSelection(mEditPopWaitTitle.length());

		mEditPopWaitAddr = (EditText) mEditPopWaitlayout
				.findViewById(R.id.address);
		mEditPopWaitAddr.setText(mPopWaitAddr.getText().toString());
		mEditPopWaitAddr.setSelection(mEditPopWaitAddr.length());
		dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		dialog.setNegativeButton("修改", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mPopWaitAddr.setText(mEditPopWaitAddr.getText().toString());
				mPopWaitTitle.setText(mEditPopWaitTitle.getText().toString());
				saveWaitLctnInfo(mPopWaitTitle.getText().toString(),
						mPopWaitAddr.getText().toString());
				mMapView.invalidate();
			}

		});

		dialog.show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
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
							if(mPassengerST != PSSNGER_ST_IDLE){
								sendCancelMsg();
								mPassengerST = PSSNGER_ST_IDLE;
							}
							MainPannelActivity.this.finish();
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
			updateUIAccdToPssgState();
			AlertDialog ad = alert.create();
			ad.show();			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mCallCenterTxt || v == mCallCenterImg) {
			LayoutInflater mLayoutInflater = (LayoutInflater) MainPannelActivity.this
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
		} else if (v == mLocationSearch) {
			String str = mSearchInput.getText().toString();
			if(str.equals("") || str.equals(R.string.car_take_location)){
				Toast.makeText(this, "请输入有效信息！", Toast.LENGTH_LONG)
				.show();
				return;
			}
				
			{
				this.getSearchCity();
				mMapSearch.poiSearchInCity(mSearchCity, str);
			}
		} else if (v == mCarTakeMe) {
			Intent i = new Intent(MainPannelActivity.this,
					BookingCarActivity.class);
			startActivityForResult(i, BOOKCAR_REQUEST_CODE);
		} else if (v == mPopWaitLctnClick) {
			if (mPassengerST == this.PSSNGER_ST_IDLE)
				search();
		} else if (v == mPopCarLctnClick) {
			String inputStr = mDataPreferenc.getString(
					DataPreferenceKey.DRVRTEL_KEY, "未知");
			Intent myIntentDial = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + inputStr));
			startActivity(myIntentDial);
		} else if (v == mSaveSetting) {

			if (!isNumeric(mParePhoneNum.getText().toString())) {
				Toast.makeText(this, "手机号码格式格式不正确,请重新填写", Toast.LENGTH_LONG)
						.show();
				return;
			}
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

			Toast.makeText(MainPannelActivity.this, "已保存当前设置",
					Toast.LENGTH_LONG).show();
		} else if (v == mControlInOrder) {
			orderControl();
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
		mOrdrCntrlState.setVisibility(View.GONE);

		mOrdrCntrlCarGo = (Button) mOrderContrlPnnl.findViewById(R.id.cargo);
		mOrdrCntrlCarGo.setVisibility(View.GONE);

		mOrdrCntrlCancelOrder = (Button) mOrderContrlPnnl
				.findViewById(R.id.cancelorder);
		mOrdrCntrlCompleteOrder = (Button) mOrderContrlPnnl
				.findViewById(R.id.completeorder);
		mOrdrCntrlBack = (Button) mOrderContrlPnnl.findViewById(R.id.back);

		mOrdrCntrlPName.setText(mDataPreferenc.getString(
				DataPreferenceKey.ACCOUNTNAME_KEY, "未知"));
		mOrdrCntrlPTime.setText(mDataPreferenc.getString(
				DataPreferenceKey.WAITTIME_KEY, "未知"));
		mOrdrCntrlPTel.setText(mDataPreferenc.getString(
				DataPreferenceKey.MOBILENUM_KEY, "未知"));
		// mOrdrCntrlPAddr.setText(mDataPreferenc.getString(
		// DataPreferenceKey.WAITTIME_KEY, "未知"));

		String strTitle = mDataPreferenc.getString(
				DataPreferenceKey.WAITLCTNTITLE_KEY, "未设定");
		String strAddr = mDataPreferenc.getString(
				DataPreferenceKey.WAITLCTNADDR_KEY, "未设定");
		if (strTitle.equals("到这里来接我")) {
			mOrdrCntrlPAddr.setText(strAddr);
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

			mOrdrCntrlPAddr.setText(strTitle + "\r\n" + strAddr);
		}

		// mOrdrCntrlState.setText(getStatTip());

		// mOrdrCntrlCarGo.setOnClickListener(mOrderListener);
		mOrdrCntrlCancelOrder.setOnClickListener(mOrderListener);
		mOrdrCntrlCompleteOrder.setOnClickListener(mOrderListener);
		mOrdrCntrlBack.setOnClickListener(mOrderListener);

		mCntrlDlg = mOrderCntrlDlg.show();
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
	public void onFocusChange(View v, boolean hasFocus) {

		if (v == mOneKeyCallBtn) {
			if (hasFocus) {
				if (null != mLastFocusBtn)
					mLastFocusBtn.setPressed(false);
				mLastFocusBtn = mOneKeyCallBtn;
				mAudioManage.playSoundEffect(AudioManager.FX_KEY_CLICK);
				mSearchInput.setTextColor(Color.GRAY);
				mSearchInput.setText(R.string.car_take_location);
				mSearchTip.setVisibility(View.VISIBLE);
				mOneKeyCallUI.setVisibility(View.VISIBLE);
				mOrderUI.setVisibility(View.INVISIBLE);
				mSetting.setVisibility(View.INVISIBLE);
				
				Log.i("jason"," onFocusChange mSearchTip.setVisibility(View.VISIBLE);");
			}
			if (null != mLastFocusBtn) {
				mLastFocusBtn.setPressed(true);
			}
		} else if (v == mOrderBtn) {
			if (hasFocus) {
				if (null != mLastFocusBtn)
					mLastFocusBtn.setPressed(false);
				mLastFocusBtn = mOrderBtn;
				mAudioManage.playSoundEffect(AudioManager.FX_KEY_CLICK);
				mOrderUI.setVisibility(View.VISIBLE);
				mOneKeyCallUI.setVisibility(View.INVISIBLE);
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
				mOneKeyCallUI.setVisibility(View.INVISIBLE);
				mOrderUI.setVisibility(View.INVISIBLE);

			}
			if (null != mLastFocusBtn) {
				mLastFocusBtn.setPressed(true);
			}
		} else {

			if (null != mLastFocusBtn) {
				mLastFocusBtn.setPressed(true);
			}
			if (v == mSearchInput) {

				if (hasFocus) {
					mOneKeyCallBtn.setPressed(true);
//					mSearchTip.setVisibility(View.INVISIBLE);
					mSearchTip.setVisibility(View.GONE);
					mSearchInput.setText("");
					mSearchInput.setTextColor(Color.BLACK);
				}
			}
			if (v == mSearchCitySetting) {
				mDataPreferenc
						.edit()
						.putString(DataPreferenceKey.SEARCHCITY_KEY,
								mSearchCitySetting.getText().toString())
						.commit();
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onDestroy();
		Intent mIntent = new Intent(this, PassengerService.class);
		this.stopService(mIntent);
		connectHandler = null;
	}

	@Override
	protected void onPause() {
		if (mBMapMan != null) {
			mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {

		if (!DataPreferenceKey.isRegister(this)) {
			Intent i = new Intent(MainPannelActivity.this,
					AccountManageActivity.class);
			startActivity(i);
		} else {
		}
		if (mBMapMan != null) {
			mBMapMan.getLocationManager().requestLocationUpdates(
					mLocationListener);
			mBMapMan.getLocationManager().setNotifyInternal(70,40);//seconds
			mBMapMan.start();
			
			mMapController = mMapView.getController();
			// mMapView.setTraffic(true);
		}
		super.onResume();
		// if (isJustCreate) {
		// mOrderBtn.requestFocusFromTouch();
		// isJustCreate = false;
		// }
		if (mSearchInput != null && mSearchInput.isFocused()) {
			mOneKeyCallBtn.setPressed(true);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACCOUNT_REQUEST_CODE) {
			if (resultCode == this.RESULT_OK) {
				int radioId = mDataPreferenc.getInt(DataPreferenceKey.TYPE_KEY,
						0);
				if (DataPreferenceKey.TYPE_DRIVER == radioId) {
					Toast.makeText(MainPannelActivity.this, "已转为司机模式，请重新启动 ",
							Toast.LENGTH_LONG).show();
					Intent mIntent = new Intent(this, PassengerService.class);
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					this.stopService(mIntent);
					this.finish();
				}
			}
		} else if (requestCode == BOOKCAR_REQUEST_CODE) {
			if (resultCode == this.RESULT_OK) {
				mpDialog = new ProgressDialog(this);
				mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
				mpDialog.setTitle("提交订单");// 设置标题
				mpDialog.setMessage("等待服务订单确认");
				mpDialog.setIndeterminate(true);// 设置进度条是否为不明确
				mpDialog.setCancelable(false);// 设置进度条是否可以按退回键取消
				mpDialog.setButton("取消订单",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								myHandler.removeCallbacks(mOrderTmOutRun);
								// dialog.cancel();
								sendCancelMsg();
								mPassengerST = PSSNGER_ST_IDLE;
							}

						});
				mPassengerST = PSSNGER_ST_WAITORDERRESULT;
				mOrderTmOutRun = new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub

						mPassengerST = PSSNGER_ST_IDLE;
						mpDialog.setMessage("失败，预定超时");
						mpDialog.cancel();
						AlertDialog.Builder alert = new AlertDialog.Builder(
								MainPannelActivity.this);
						alert.setTitle("提交订单");
						alert.setMessage("提交失败,请重新操作");
						alert.setCancelable(false);// 设置进度条是否可以按退回键取消
						alert.setPositiveButton("返回",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// dialog.cancel();
									}

								});
						alert.create();
						alert.show();

					}
				};
				myHandler.postDelayed(mOrderTmOutRun, 240 * 1000);
				mpDialog.show();
			}
		}
	}

	class OrderMenuClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == mOrderMenuOneCall) {
				mOneKeyCallUI.setVisibility(View.VISIBLE);
				mOneKeyCallBtn.requestFocusFromTouch();
				mOneKeyCallBtn.setPressed(true);
				mOrderUI.setVisibility(View.INVISIBLE);
				mSetting.setVisibility(View.INVISIBLE);
			} else if (v == mOrderMenuCallCar) {

			} else if (v == mOrderMenuAccountManage) {
				Intent i = new Intent(MainPannelActivity.this,
						AccountManageActivity.class);

				startActivityForResult(i, ACCOUNT_REQUEST_CODE);
			}

		}
	}

	class SettingMenuClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == mSettingAccntMng) {
				Intent i = new Intent(MainPannelActivity.this,
						AccountManageActivity.class);
				startActivityForResult(i, ACCOUNT_REQUEST_CODE);;
			}
		}
	}
	
	class ShowInMapClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == mShowCurLctn) {	
				int wLatitude = mDataPreferenc.getInt(
						DataPreferenceKey.WAITLATITUDE_KEY, 0);
				int wLong = mDataPreferenc.getInt(
						DataPreferenceKey.WAITLONGITUDE_KEY, 0);
				mMapView.getController().animateTo(
						new GeoPoint(mDataPreferenc.getInt(DataPreferenceKey.CURLATITUDE_KEY, wLatitude), mDataPreferenc.getInt(DataPreferenceKey.CURLONGITUDE_KEY, wLong)));
				mMapView.invalidate();
			}else if(v == mShowDrv){
				String strPoint = mDataPreferenc.getString(
						DataPreferenceKey.DRVLCTN_KEY, "未知");
				int index = strPoint.indexOf(":");
				if (-1 != index) {
					int dLatitude = Integer.parseInt(strPoint.subSequence(0, index)
							.toString());
					int dLongtitude = Integer.parseInt(strPoint.subSequence(index + 1,
							strPoint.length()).toString());
				
					GeoPoint p = new GeoPoint(dLatitude, dLongtitude);
					MapView.LayoutParams geoLP = (MapView.LayoutParams) mPopCarLctn
							.getLayoutParams();
					int wLatitude = mDataPreferenc.getInt(
							DataPreferenceKey.WAITLATITUDE_KEY, dLatitude);
					int wLong = mDataPreferenc.getInt(
							DataPreferenceKey.WAITLONGITUDE_KEY, dLongtitude);
					int latPoint = (int) (((float) dLatitude + (float) wLatitude) / 2.0);
					int longPoint = (int) ((((float) dLongtitude + (float) wLong) / 2.0));
					mMapView.getController().animateTo(
							new GeoPoint(dLatitude, dLongtitude));
					mMapView.getController().zoomToSpan((int)Math.abs(((float)(latPoint-dLatitude)*2.5)), (int)Math.abs((longPoint-dLongtitude)*2.5));
					mMapView.invalidate();
				}
			}
		}
	}
	public boolean sendCancelMsg() {
		String msg = MsgFormat.MSG_HEAD;
		msg = msg
				+ MsgFormat.makeMsgItem(MsgFormat.MSGTYPE_TITLE,
						MsgFormat.MSGTYPE_CANCEL);
		msg = msg + MsgFormat.MSG_END;
		return sendMsgToPare(msg);
	}
	public boolean sendMsgToPare(String str) {
		String pareNum = mDataPreferenc.getString(
				DataPreferenceKey.PAREPHONENUM_KEY, "15951616861");
		smsManager.sendTextMessage(pareNum, null, str, null, null);
		return true;
	}

	public boolean sendOverMsg() {
		String msg = MsgFormat.MSG_HEAD;
		msg = msg
				+ MsgFormat.makeMsgItem(MsgFormat.MSGTYPE_TITLE,
						MsgFormat.MSGTYPE_OVER);
		msg = msg + MsgFormat.MSG_END;
		return sendMsgToPare(msg);
	}
	class OrderCntrlClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == mOrdrCntrlCancelOrder) {
				sendCancelMsg();
				mPassengerST = PSSNGER_ST_IDLE;
			} else if (v == mOrdrCntrlCompleteOrder) {
				sendOverMsg();
				mPassengerST = PSSNGER_ST_IDLE;
			} else if (v == mOrdrCntrlBack) {
				;
			}
			updateUIAccdToPssgState();
			mCntrlDlg.dismiss();
		}
	}

	// write record file
	public static int writeRecordFile(String strFilePath, String strContent,
			boolean append) throws IOException {
		try {
			FileWriter mFileWriter = new FileWriter(strFilePath, append);
			mFileWriter.write(strContent);
			mFileWriter.flush();
			mFileWriter.close();
		} catch (IOException e) {

			// throw e;
		}
		return 0;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (mSetCitySetting == buttonView) {
			mSearchCitySetting.setEnabled(isChecked);
		}
	}

	public void updateUIAccdToPssgState() {
		switch (mPassengerST) {
			case PSSNGER_ST_IDLE :
				mOrder_search_o.setVisibility(View.INVISIBLE);
				mOrder_search_s.setVisibility(View.VISIBLE);
				mLctncntrl_callkey_l.setVisibility(View.INVISIBLE);
				mLctncntrl_callkey_c.setVisibility(View.VISIBLE);
				mOneKeyCallBtn.setEnabled(true);
				mOrderBtn.setEnabled(true);
				mSettingBtn.setEnabled(true);
				mPopCarLctn.setVisibility(View.INVISIBLE);
				break;
			case PSSNGER_ST_WAITSCEDULECAR :
				mOrder_search_o.setVisibility(View.VISIBLE);
				mOrder_search_s.setVisibility(View.INVISIBLE);
				mLctncntrl_callkey_l.setVisibility(View.VISIBLE);
				mLctncntrl_callkey_c.setVisibility(View.INVISIBLE);
				mOneKeyCallBtn.setEnabled(true);
				mOrderBtn.setEnabled(false);
				mSettingBtn.setEnabled(false);
				mOrderCarST.setText("指派车辆中");
				mPopCarLctn.setVisibility(View.INVISIBLE);
				break;

			case PSSNGER_ST_WAITCAR :
				mOrder_search_o.setVisibility(View.VISIBLE);
				mOrder_search_s.setVisibility(View.INVISIBLE);
				mLctncntrl_callkey_l.setVisibility(View.VISIBLE);
				mLctncntrl_callkey_c.setVisibility(View.INVISIBLE);
				mOneKeyCallBtn.setEnabled(true);
				mOrderBtn.setEnabled(false);
				mSettingBtn.setEnabled(false);
				mOrderCarST.setText("在 路 上");
				mPopCarLctn.setVisibility(View.VISIBLE);
				break;
			// case DRVR_STATE_IDLE :
			// mOrderState.setText(getStatTip());
			// mOrderControl.setEnabled(false);
			// mSettingBtn.setEnabled(true);
			// mPopPassenger.setVisibility(View.INVISIBLE);
			// break;
			// case DRVR_STATE_SCEDULEDRV :
			// mOrderControl.setEnabled(true);
			// mOrderState.setText(getStatTip());
			// mSettingBtn.setEnabled(false);
			// break;
			// case DRVR_STATE_ONTHEWAY :
			// mOrderControl.setEnabled(true);
			// mOrderState.setText(getStatTip());
			// break;
		}

	}
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub

	}

}
