package com.hoteldemo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
//import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.PoiOverlay;
import com.baidu.mapapi.Projection;
import com.hoteldemo.HotelDemoActivity.GetOverlay;
import com.hoteldemo.HotelDemoActivity.OverItemT;
import com.hoteldemo.HotelDemoActivity.OverItemTA;
import com.hoteldemo.HotelDemoActivity.YanziOverlay;

public class MeasureActivity extends MapActivity {
	BMapManager mBMapMan = null;
	MapView mMapView = null;
	MKSearch mMapSearch = null;
	MapController mMapController = null;
	
	LocationListener mLocationListener = null;
	
	Button mBtnSearch = null;
	EditText mSearchCity = null;
	EditText mSearchAddr = null;
	CheckBox mShowSearch = null;
	CheckBox locationCB = null;
	LinearLayout mSearchLayout = null;
	View mPopView = null;	
	OverItemT overitem = null;
	
	int mLongitude = (int) (116.404 * 1E6);
	int mLatitude = (int) (39.915 * 1E6);

	int mTLongitude = mLongitude;
	int mTLatitude = mLatitude;
	boolean bUpdateLocation = false;
	double mStartLat = 32.14524;
	double mStartLon = 118.757219;
	double mEndLat = 32.088886;
	double mEndLon = 118.784676;
	float mDistance = 0;
   //GPS  open 
	LocationManager mLocationManager;
	private final LocationListener gpsLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			Toast.makeText(MeasureActivity.this, "onLocationChanged",
					Toast.LENGTH_LONG).show();
			dealGpsLocation(location);
		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	};
	
	private final GpsStatus.NmeaListener nmeaListener = new GpsStatus.NmeaListener() {
		public void onNmeaReceived(long timestamp, String nmea) {
			Toast.makeText(MeasureActivity.this, "onNmeaReceived",
					Toast.LENGTH_LONG).show();
		}
	};
	private void dealGpsLocation(Location location) {
		
	}
	private int turnOnGPS() {
		final String GPS_ON = "1";
		boolean bGpsOn = false;
		String strFileConten;
		Settings.Secure.setLocationProviderEnabled(getContentResolver(),
				LocationManager.GPS_PROVIDER, true);

		Settings.Secure.putInt(getContentResolver(),
				Settings.Secure.ACCESSIBILITY_ENABLED, 1);
		bGpsOn = mLocationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);


		return 1;
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measure);
		//gps
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//turnOnGPS();
		mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER,1000, 0, (android.location.LocationListener) gpsLocationListener);
//		mLocationManager.addNmeaListener(nmeaListener);
		mBMapMan = new BMapManager(getApplication());
		boolean bInit = mBMapMan.init(
				"2D027B3CAA60689DB0A12E08320BCEDB503ACD54", null);
		Log.i("hoteljason", " bInit=" + bInit);
		super.initMapActivity(mBMapMan);
		
		mMapSearch = new MKSearch();
		
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mSearchCity = (EditText) findViewById(R.id.inputCity);
		mSearchAddr = (EditText) findViewById(R.id.inputAddr);		
		locationCB = (CheckBox) findViewById(R.id.locationCB);
		mShowSearch = (CheckBox) findViewById(R.id.showSearch);
		mBtnSearch = (Button) findViewById(R.id.bntSearch);
		mSearchLayout = (LinearLayout)findViewById(R.id.searchLayout);
		/***** mShowSearch CheckBox Listener start ***/
		mShowSearch
		.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(mSearchLayout.getVisibility() == View.VISIBLE){
					mSearchLayout.setVisibility(View.GONE);
				}else{
					mSearchLayout.setVisibility(View.VISIBLE);
				}
				
				
			}
		});
		/*---- Location CheckBox Listener end ----*/
				
		/***** Search Button click Listener start ***/
		OnClickListener clickListener = new OnClickListener() {
			public void onClick(View v) {
				searchButtonProcess(v);
			}
		};
		mBtnSearch.setOnClickListener(clickListener);
		/*------ Search Button click Listener end -----*/
		
		/***** Map Search Listener start ***/
		mMapSearch.init(mBMapMan, new MKSearchListener() {

			public void onGetPoiResult(MKPoiResult res, int type, int error) {

				if (error != 0 || res == null) {
					Toast.makeText(MeasureActivity.this, "error",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (res.getCurrentNumPois() > 0) {

					PoiOverlay poiOverlay = new PoiOverlay(
							MeasureActivity.this, mMapView);
					poiOverlay.setData(res.getAllPoi());
					mMapView.getOverlays().clear();
					mMapView.getOverlays().add(poiOverlay);
					mMapView.invalidate();
					mMapView.getController().animateTo(res.getPoi(0).pt);
				} else if (res.getCityListNum() > 0) {
					String strInfo = "��";
					for (int i = 0; i < res.getCityListNum(); i++) {
						strInfo += res.getCityListInfo(i).city;
						strInfo += ",";
					}
					strInfo += "�ҵ����";
					Toast.makeText(MeasureActivity.this, strInfo,
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
					Toast.makeText(MeasureActivity.this,
							"###error###	onGetAddrResult", Toast.LENGTH_LONG)
							.show();
					return;
				}
				mMapView.getController().animateTo(res.geoPt);

				String strInfo = String.format(
						"Latitude=%f\r\n LongitudeE6=%f\r\n",
						res.geoPt.getLatitudeE6() / 1e6,
						res.geoPt.getLongitudeE6() / 1e6);

				Drawable marker = getResources().getDrawable(
						R.drawable.iconmarka); // �õ���Ҫ���ڵ�ͼ�ϵ���Դ
				marker.setBounds(0, 0, marker.getIntrinsicWidth(),
						marker.getIntrinsicHeight()); //
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(
						new OverItemTA(marker, MeasureActivity.this,
								res.geoPt, res.strAddr));
				mMapView.getOverlays().add(new GetOverlay());

				// Toast.makeText(HotelDemoActivity.this, strInfo,
				// Toast.LENGTH_LONG).show();
				Toast.makeText(MeasureActivity.this, res.strAddr,
						Toast.LENGTH_LONG).show();

			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {

			}

		});
		
		/*---- Map Search Listener end ----*/
		
		/***** Location Listener start ***/
//		mLocationListener = new LocationListener() {
//			@Override
//			public void onLocationChanged(Location location) {
//				if (location != null) {
//					mLongitude = (int) (location.getLongitude() * 1E6);
//					mLatitude = (int) (location.getLatitude() * 1E6);
//					String strLog = String.format("position:\r\n"
//							+ "Longitude:%d\r\n" + "Latitude:%d", mLongitude,
//							mLatitude);
//					if (bUpdateLocation && null != mMapController) {
//						mMapController = mMapView.getController(); // 得到
//																	// mMapView
//																	// 的控制权,可以用它控制和驱动平移和缩放
//						GeoPoint point = new GeoPoint(mLatitude, mLongitude);// 给定的经纬度构造一个
//																				// GeoPoint,单位是微度
//																				// (度
//																				// *
//																				// 1E6)
//						mMapController.setCenter(point); // 设置地图中心点
//						Toast.makeText(MeasureActivity.this, strLog,
//								Toast.LENGTH_LONG).show();
//						// mMapController.setZoom(30);// 设置地图 zoom 级别
//					}
//				}
//			}
//		};
		/*---- Location Listener end ----*/
		
		/****** add OverLay start *****/
		mMapView.getOverlays().add(new GetOverlay());
		
		/** /0000 MyLocationOverlay start 0000/ **/
		
		 MyLocationOverlay mylocTest = new MyLocationOverlay(this, mMapView);
		 MKLocationManager mLocationManager = mBMapMan.getLocationManager();
//		 mBMapMan.getLocationManager().enableProvider(
//		 MKLocationManager.MK_NETWORK_PROVIDER);
//		 mBMapMan.getLocationManager().disableProvider(MKLocationManager.MK_GPS_PROVIDER);

		 mylocTest.enableMyLocation(); // 启用定位
		 mylocTest.enableCompass();
		 mMapView.getOverlays().add(mylocTest);
		
		/** /0000 MyLocationOverlay end 0000/ **/
		
		
		/****** add OverLay end *****/
		/*---- add OverLay end ---*/
	}

	void searchButtonProcess(View v) {
		if (mBtnSearch.equals(v)) {
			// Intent intent = null;
			// intent = new Intent(PoiSearch.this, MapViewDemo.class);
			// this.startActivity(intent);

			// EditText editCity = (EditText)findViewById(R.id.city);
			// mSearch.geocode(mSearchCity.getText().toString(),
			// mSearchAddr.getText().toString());
			mMapSearch.geocode(mSearchAddr.getText().toString(), mSearchCity
					.getText().toString());
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onDestroy() {
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onDestroy();
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

		if (mBMapMan != null) {
//			mBMapMan.getLocationManager().requestLocationUpdates(
//					mLocationListener);
			mBMapMan.start();
			// mMapView.setTraffic(true);
		}
		super.onResume();
	}



//	public class YanziOverlay extends Overlay {
//		// GeoPoint geoPoint = new GeoPoint((int) (39.915 * 1E6), (int) (116.404
//		// * 1E6));
//		Paint paint = new Paint();
//
//		@Override
//		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
//			GeoPoint geoPoint = new GeoPoint(mTLatitude, mTLongitude);
//			// 在天安门的位置绘制一个 String
//			Point point = mMapView.getProjection().toPixels(geoPoint, null);
//			paint.setColor(Color.BLUE);
//			paint.setTextSize(20);
//			canvas.drawText("★燕子★", point.x, point.y, paint);
//
//		}
//	}

	class GetOverlay extends Overlay {
		GeoPoint mGeo = null;
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean arg2) {
			super.draw(canvas, mapView, arg2);
			if (mGeo == null) {
				Log.d("jason distance draw", "-------mGeo == null--");
				return;
			}
			Log.d("jason distance draw", arg2 + "-------draw--");
			Projection projection = mapView.getProjection();
			// 把经纬度变换到相对于MapView左上角的屏幕像素坐标
			Point point = projection.toPixels(mGeo, null);
			// 可在此处添加您的绘制代码
			Paint paintText = new Paint();
			paintText.setColor(Color.CYAN);
			paintText.setTextSize(45);
			canvas.drawText("●", point.x - 9, point.y + 13, paintText); // 绘制文本
		}

		@Override
		public boolean onTap(GeoPoint geo, MapView arg1) {
			mGeo = geo;
			Log.d("jason distance", geo.getLongitudeE6() / 1E6 + "----------"
					+ geo.getLatitudeE6() / 1E6);
			// mSearch.geocode(mSearchAddr.getText().toString(),mSearchCity.getText().toString()
			// );
			mMapSearch.reverseGeocode(geo);
			return super.onTap(geo, arg1);
		}
	}

	class OverItemTA extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();

		public OverItemTA(Drawable marker, Context context, GeoPoint pt,
				String title) {
			super(boundCenterBottom(marker));

			mGeoList.add(new OverlayItem(pt, title, null));

			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mGeoList.get(i);
		}

		@Override
		public int size() {
			return mGeoList.size();
		}

		@Override
		public boolean onSnapToItem(int i, int j, Point point, MapView mapview) {
			Log.e("ItemizedOverlayDemo", "enter onSnapToItem()!");
			return false;
		}
	}

}
