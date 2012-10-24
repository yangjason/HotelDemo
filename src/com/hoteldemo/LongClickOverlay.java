package com.hoteldemo;

import android.content.Context;
import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;

public class LongClickOverlay extends Overlay implements OnDoubleTapListener, OnGestureListener {

	private GestureDetector gestureScanner = new GestureDetector(this);
	private Context mContext;
	float x = 0;
	float y = 0;

	public LongClickOverlay(Context context) {
		this.mContext = context;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean arg2) {
		// TODO Auto-generated method stub
		super.draw(canvas, mapView, arg2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0, MapView arg1) {

		return gestureScanner.onTouchEvent(arg0);
	}

	@Override
	public void onLongPress(MotionEvent e) {
		x = e.getX();
		y = e.getY();
		
		if (mContext instanceof MainPannelActivity) {
			//((MainPannelActivity) mContext).showPopuOnMap((int) x, (int) y);
			((MainPannelActivity) mContext).onLongPress(e);
//			// ShowMap showMap = (ShowMap) mContext;
//			// MapView.LayoutParams geoLP = (MapView.LayoutParams)
//			// showMap.popView.getLayoutParams();
//			// geoLP.point = p;
//			// showMap.mapView.updateViewLayout(showMap.popView, geoLP);
//			// ((ShowMap) mContext).popView.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) {
		return super.onTap(arg0, arg1);
	}

}
