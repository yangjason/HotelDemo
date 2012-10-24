package com.hoteldemo;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class PassengerService extends Service {
	GetData recevier = null;
	final static int MSG_COMING = 1;
	final static String DRIVER_MSG = "DriverMsg";
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	protected Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case GetData.MSG_PAREPHONE :
					sendPassengerMsg();
					break;
				default :
//					Toast.makeText(
//							PassengerService.this,
//							" PassengerService handleMessage "
//									+ MsgFormat.mMsgQuene.get(0),
//							Toast.LENGTH_LONG).show();
//					Log.i("jason", " PassengerService getmsg = "
//							+ MsgFormat.mMsgQuene.get(0));
//					if (MainPannelActivity.connectHandler != null) {
//						MainPannelActivity.connectHandler
//								.sendEmptyMessage(MSG_COMING);
//					}
			}
		}
	};
	public boolean sendPassengerMsg() {
		Intent i = new Intent(this, MainPannelActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra(DRIVER_MSG, true);
		startActivity(i);
		return true;
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		recevier = new GetData(myHandler, this);
		IntentFilter filter = new IntentFilter(ACTION);
		filter.setPriority(filter.SYSTEM_HIGH_PRIORITY);// 设置优先级最大
		// filter.setPriority(priority)
		registerReceiver(recevier, filter);
//		Toast.makeText(PassengerService.this, " PassengerService ",
//				Toast.LENGTH_LONG).show();
		MsgFormat.mMsgQuene.clear();//
	}

	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(recevier);
		MsgFormat.mMsgQuene.clear();//

	}

}
