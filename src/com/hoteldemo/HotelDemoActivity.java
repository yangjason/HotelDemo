package com.hoteldemo;

import android.app.Activity;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPlanNode;
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
import com.baidu.mapapi.RouteOverlay;
import com.baidu.mapapi.TransitOverlay;
//import com.baidu.mapapi.demo.R;
import com.baidu.mapapi.*;

public class HotelDemoActivity extends MapActivity {

	BMapManager mBMapMan = null;
	MapView mMapView = null;
	MKSearch mMapSearch = null;
	MapController mMapController = null;
	
	LocationListener mLocationListener = null;
	
	Button mBtnSearch = null;
	EditText mSearchCity = null;
	EditText mSearchAddr = null;
	
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		 
		mBMapMan = new BMapManager(getApplication());
		boolean bInit = mBMapMan.init(
				"2D027B3CAA60689DB0A12E08320BCEDB503ACD54", null);
		Log.i("hoteljason", " bInit=" + bInit);
		super.initMapActivity(mBMapMan);
		
		mMapSearch = new MKSearch();
		
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mSearchCity = (EditText) findViewById(R.id.inputCity);
		mSearchAddr = (EditText) findViewById(R.id.inputAddr);	
		CheckBox locationCB = (CheckBox) findViewById(R.id.locationCB);
		mBtnSearch = (Button) findViewById(R.id.bntSearch);
		
		/***** Location CheckBox Listener start ***/
		locationCB
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							bUpdateLocation = true;
							if (mBMapMan != null) {
								mBMapMan.getLocationManager()
										.requestLocationUpdates(
												mLocationListener);
								mBMapMan.start();
							}
							mMapController = mMapView.getController(); // 得到
																		// mMapView
																		// 的控制权,可以用它控制和驱动平移和缩放
							mTLongitude = mLongitude;
							mTLatitude = mLatitude;
							GeoPoint point = new GeoPoint(mTLatitude,
									mTLongitude);// 给定的经纬度构造一个 GeoPoint,单位是微度 (度
													// * 1E6)
							mMapController.setCenter(point); // 设置地图中心点
							// mMapController.setZoom(30);// 设置地图 zoom 级别
						} else {
							bUpdateLocation = false;
							mMapController = mMapView.getController(); // 得到
																		// mMapView
																		// 的控制权,可以用它控制和驱动平移和缩放
							GeoPoint point = new GeoPoint((int) (39.915 * 1E6),
									(int) (116.404 * 1E6));// 给定的经纬度构造一个
															// GeoPoint,单位是微度 (度
															// * 1E6)
							mTLongitude = (int) (116.404 * 1E6);
							mTLatitude = (int) (39.915 * 1E6);
							mMapController.setCenter(point); // 设置地图中心点
							
							// mMapController.setZoom(30);// 设置地图 zoom 级别
						}
						// print distance
						mDistance = 0;;
						Location locationA = new Location("point A");

						locationA.setLatitude(mStartLat);
						locationA.setLongitude(mStartLon);

						Location locationB = new Location("point B");

						locationB.setLatitude(mEndLat);
						locationB.setLongitude(mEndLon);

						float distance = locationA.distanceTo(locationB);
						Log.i("jason", "distance11=" + distance);
						Log.i("jason",
								"distance="
										+ CalDistance.getDistance(mStartLat,
												mStartLon, mEndLat, mEndLon)
										* 1.00);
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
					Toast.makeText(HotelDemoActivity.this, "error",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (res.getCurrentNumPois() > 0) {

					PoiOverlay poiOverlay = new PoiOverlay(
							HotelDemoActivity.this, mMapView);
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
					Toast.makeText(HotelDemoActivity.this, strInfo,
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
					Toast.makeText(HotelDemoActivity.this,
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
						new OverItemTA(marker, HotelDemoActivity.this,
								res.geoPt, res.strAddr));
				mMapView.getOverlays().add(new GetOverlay());

				// Toast.makeText(HotelDemoActivity.this, strInfo,
				// Toast.LENGTH_LONG).show();
				Toast.makeText(HotelDemoActivity.this, res.strAddr,
						Toast.LENGTH_LONG).show();

			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {

			}

		});
		
		/*---- Map Search Listener end ----*/
		
		/***** Location Listener start ***/
		mLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					mLongitude = (int) (location.getLongitude() * 1E6);
					mLatitude = (int) (location.getLatitude() * 1E6);
					String strLog = String.format("position:\r\n"
							+ "Longitude:%d\r\n" + "Latitude:%d", mLongitude,
							mLatitude);

					Log.i("HotelDemo jason", " location " + strLog);
					if (bUpdateLocation && null != mMapController) {
						mMapController = mMapView.getController(); // 得到
																	// mMapView
																	// 的控制权,可以用它控制和驱动平移和缩放
						GeoPoint point = new GeoPoint(mLatitude, mLongitude);// 给定的经纬度构造一个
																				// GeoPoint,单位是微度
																				// (度
																				// *
																				// 1E6)
						mMapController.setCenter(point); // 设置地图中心点
						// mMapController.setZoom(30);// 设置地图 zoom 级别
					}
				}
			}
		};
		/*---- Location Listener end ----*/
		
		/****** add OverLay start *****/
		
		/** /0000 OverItemT start 0000/ **/
		Drawable marker = getResources().getDrawable(R.drawable.iconmarka); 
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight()); 
		overitem = new OverItemT(marker, this, 1);
		// mMapView.getOverlays().add(overitem); 
		/** /0000 OverItemT end 0000/ **/
		
		mMapView.getOverlays().add(new YanziOverlay());
		
		mMapView.getOverlays().add(new GetOverlay());
		
		/** /0000 MyLocationOverlay start 0000/ **/
		
		// MyLocationOverlay mylocTest = new MyLocationOverlay(this, mMapView);
		// mLocationManager = mBMapMan.getLocationManager();
		// mBMapMan.getLocationManager().enableProvider(
		// MKLocationManager.MK_NETWORK_PROVIDER);
		// mBMapMan.getLocationManager().disableProvider(MKLocationManager.MK_GPS_PROVIDER);

		// mylocTest.enableMyLocation(); // 启用定位
		// mylocTest.enableCompass();
		// 启用指南针
		// mMapView.getOverlays().add(mylocTest);
       

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
			mBMapMan.getLocationManager().requestLocationUpdates(
					mLocationListener);
			mBMapMan.start();
			// mMapView.setTraffic(true);
		}
		super.onResume();
	}

	class OverItemT extends ItemizedOverlay<OverlayItem> {

		public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
		private Drawable marker;
		private Context mContext;

		private double mLat1 = 39.90923; // point1纬度
		private double mLon1 = 116.357428; // point1经度

		private double mLat2 = 39.90923;
		private double mLon2 = 116.397428;

		private double mLat3 = 39.90923;
		private double mLon3 = 116.437428;

		OverlayItem overLayItem = null;

		public OverItemT(Drawable marker, Context context, int count) {
			super(boundCenterBottom(marker));

			this.marker = marker;
			this.mContext = context;

			// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
			// GeoPoint p1 = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 *
			// 1E6));
			// GeoPoint p2 = new GeoPoint((int) (mLat2 * 1E6), (int) (mLon2 *
			// 1E6));
			//
			// // 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
			// mGeoList.add(new OverlayItem(p1, "P1", "point1"));
			// mGeoList.add(new OverlayItem(p2, "P2", "point2"));
			//
			// if(count == 3)
			// {
			// GeoPoint p3 = new GeoPoint((int) (mLat3 * 1E6), (int) (mLon3 *
			// 1E6));
			// mGeoList.add(new OverlayItem(p3, "P3", "point3"));
			// }
			populate(); // createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
		}

		public void updateOverlay() {
			populate();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// Projection接口用于屏幕像素坐标和经纬度坐标之间的变换
			Projection projection = mapView.getProjection();
			// for (int index = size() - 1; index >= 0; index--) { // 遍历mGeoList
			// OverlayItem overLayItem = getItem(index); // 得到给定索引的item
			//
			// String title = overLayItem.getTitle();
			// // 把经纬度变换到相对于MapView左上角的屏幕像素坐标
			// Point point = projection.toPixels(overLayItem.getPoint(), null);
			//
			// // 可在此处添加您的绘制代码
			// Paint paintText = new Paint();
			// paintText.setColor(Color.BLUE);
			// paintText.setTextSize(15);
			// canvas.drawText(title, point.x-30, point.y, paintText); // 绘制文本
			// }
			// for (int index = size() - 1; index >= 0; index--)
			{ // 遍历mGeoList
				GeoPoint point1 = new GeoPoint(mLatitude, mLongitude);
				OverlayItem overLayItem = new OverlayItem(point1, "---燕子---",
						"燕子1"); // 得到给定索引的item
				overLayItem.setMarker(marker);
				String title = overLayItem.getTitle();
				// 把经纬度变换到相对于MapView左上角的屏幕像素坐标
				Point point = projection.toPixels(overLayItem.getPoint(), null);

				// 可在此处添加您的绘制代码
				Paint paintText = new Paint();
				paintText.setColor(Color.BLUE);
				paintText.setTextSize(15);
				canvas.drawText(title, point.x - 30, point.y, paintText); // 绘制文本
			}
			Log.i("HotelDemo--jason", "  OverItemT -draw");
			super.draw(canvas, mapView, shadow);
			// 调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
			boundCenterBottom(marker);
		}

		@Override
		protected OverlayItem createItem(int i) {
			// TODO Auto-generated method stub
			GeoPoint point1 = new GeoPoint(mLatitude, mLongitude);
			OverlayItem overLayItem = new OverlayItem(point1, "p1", "p1"); // 得到给定索引的item
			overLayItem.setMarker(marker);
			return overLayItem;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		// 处理当点击事件
		protected boolean onTap(int i) {
			// setFocus(mGeoList.get(i));
			// // 更新气泡位置,并使之显示
			// GeoPoint pt = mGeoList.get(i).getPoint();
			// mMapView.updateViewLayout( mPopView,
			// new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
			// LayoutParams.WRAP_CONTENT,
			// pt, MapView.LayoutParams.BOTTOM_CENTER));
			// mPopView.setVisibility(View.VISIBLE);
			// Toast.makeText(this.mContext, mGeoList.get(i).getSnippet(),
			// Toast.LENGTH_SHORT).show();
			return true;
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// // TODO Auto-generated method stub
			// // 消去弹出的气泡
			// mPopView.setVisibility(View.GONE);
			return super.onTap(arg0, arg1);
		}
	}

	public class YanziOverlay extends Overlay {
		// GeoPoint geoPoint = new GeoPoint((int) (39.915 * 1E6), (int) (116.404
		// * 1E6));
		Paint paint = new Paint();

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			GeoPoint geoPoint = new GeoPoint(mTLatitude, mTLongitude);
			// 在天安门的位置绘制一个 String
			Point point = mMapView.getProjection().toPixels(geoPoint, null);
			paint.setColor(Color.BLUE);
			paint.setTextSize(20);
			canvas.drawText("★燕子★", point.x, point.y, paint);

		}
	}

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