package com.hoteldemo;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class DriverService extends Service{
	final static int MSG_COMING = 1;
	final static String MSG_ORDER_REQUEST = "order";
	final static String PASSENGER_MSG = "PassengerMsg";
	GetData recevier = null;
	ProgressDialog    mpDialog = null;
	boolean isInAcceptOrder = false;
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	protected Handler myHandler=new Handler(){
		public void handleMessage(Message msg){		     
				switch(msg.what){
					case GetData.MSG_PAREPHONE:						
							sendPassengerMsg();						
					default:
//						Toast.makeText(DriverService.this,
//								" DriverService handleMessage "+MsgFormat.mMsgQuene.get(0),
//								Toast.LENGTH_LONG).show();
						Log.i("jason"," DriverService getmsg = "+MsgFormat.mMsgQuene.get(0));
						
				}
			}
		 };
		 
	public boolean sendPassengerMsg(){		
		Intent i = new Intent(this,DriverMainActivity.class);		
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra(PASSENGER_MSG, true);
		startActivity(i);		
        return true;
	}	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate(){
		recevier = new GetData(myHandler,this); 
		IntentFilter filter = new IntentFilter(ACTION);  
		filter.setPriority(filter.SYSTEM_HIGH_PRIORITY);//设置优先级最大 
		registerReceiver(recevier, filter);
//		Toast.makeText(DriverService.this,
//				" DriverService ",
//				Toast.LENGTH_LONG).show();
		MsgFormat.mMsgQuene.clear();//
	}
	
	
	public void onDestroy(){
		super.onDestroy();
		this.unregisterReceiver(recevier);
		MsgFormat.mMsgQuene.clear();//

	}
	
}
