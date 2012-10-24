package com.hoteldemo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class GetData extends BroadcastReceiver {
	private static final String mACTION="android.provider.Telephony.SMS_RECEIVED";
	final static int MSG_PAREPHONE = 1;
	Handler serviceHandler = null;
	Context mCntxt = null;
	String dealNum1= null;
	String dealNum2= null;
	SharedPreferences mDataPreferenc = null;
	public GetData(Handler h,Context context) { 
		serviceHandler = h;
		mCntxt = context;
		mDataPreferenc = context.getSharedPreferences(
				DataPreferenceKey.PREFERENCE_NAME, 0);
		dealNum1 = mDataPreferenc.getString(
				DataPreferenceKey.PAREPHONENUM_KEY, "15951616861");
		dealNum2 = "+86"+dealNum1;
    }  
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("jason"," GetData onReceive");
		if(intent.getAction().equals(mACTION))
		{
		StringBuilder sb=new StringBuilder();
		String msgContent = null;
		Bundle bundle=intent.getExtras();
		if(bundle!=null)
		{
		Object[] obj=(Object[])bundle.get("pdus");

		//构建信息对象
		SmsMessage[] message=new SmsMessage[obj.length];
		for(int i=0;i<obj.length;i++)
		{
		message[i]=SmsMessage.createFromPdu((byte[])obj[i]);
		}

		for(SmsMessage currentMessage:message)
		{
//		sb.append("come from：\n");                    
//		//发送人的电话号码
//		sb.append(currentMessage.getDisplayOriginatingAddress());
//		sb.append("\n-------传来的信息---------\n");
//		//发送的信息内容
//		sb.append(currentMessage.getDisplayMessageBody());
		dealNum1 = mDataPreferenc.getString(
				DataPreferenceKey.PAREPHONENUM_KEY, "15951616861");
		dealNum2 = "+86"+dealNum1;
		if(dealNum1.equals(currentMessage.getDisplayOriginatingAddress())||dealNum2.equals(currentMessage.getDisplayOriginatingAddress())){
//			Toast.makeText(context,"停止发送", Toast.LENGTH_LONG).show();			
			abortBroadcast();
			msgContent = currentMessage.getDisplayMessageBody();
			if(MsgFormat.isValidMsg(msgContent)){
				Log.i("jason"," isValidMsg = true");
				MsgFormat.mMsgQuene.add(msgContent);
			}
			if(MsgFormat.isMsgEnd(msgContent)){
				Log.i("jason"," isMsgEnd = true");
				if( serviceHandler != null) serviceHandler.sendEmptyMessage(MSG_PAREPHONE);
			}
		}
		}
		}
		//以Notification显示来讯信息
			Log.i("jason"," message content is =  "+sb.toString()+" dealNum1="+dealNum1+" dealNum2="+dealNum2);

		}

	}

}
