package com.hoteldemo;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * $@name%张小三#$
 * @author compal
 *
 */

public class MsgFormat {
	static List<String> mMsgQuene = new ArrayList();
	final static int    MSGMAXLENGTH = 70;
	final static String MSG_HEAD="$";
	final static String MSG_END="$";
	
	// passenger ----> driver
	final static String TITLE_START = "@";
	final static String TITLE_END = "%";
	final static String ITEM_END = "#";
	final static String NAME_TITLE = "n";
	final static String TELNUM_TITLE = "p";
	final static String TIME_TITLE = "t";//小时：分钟
	final static String WAITLCTN_TITLE = "w";
	final static String WAITADDR_TITLE = "waddr";//wait address 
	
	final static String MSGTYPE_TITLE = "type";
	
	final static String MSGTYPE_UNKNOWN  = "0";
	
	final static String MSGTYPE_ORDER  = "1";	
	
	final static String MSGTYPE_ORDERRSULT  = "2";
	
	final static String ORDERRSULT_TITLE = "result";//driver --->passenger
	final static String RSULT_YES = "r1";
	final static String RSULT_NO = "r2";
	
	final static String MSGTYPE_CANCEL = "3";
	
	final static String MSGTYPE_OVER = "4";
	

	final static String MSGTYPE_CRASTART = "5";
//	final static String NAME_TITLE = "n";
//	final static String TELNUM_TITLE = "p";
// final static String GPSP_TITLE = "g";//latitude:longitude 121.531213:31.147093 
	final static String CARINFO_TITLE = "c";
	
	final static String MSGTYPE_LCTNUPDATE = "6";
	final static String GPSP_TITLE = "g";//latitude:longitude 121.531213:31.147093 

	
	public static boolean isValidMsg(String msgStr){
		Log.i("jason", "msgStr.charAt(0)="+msgStr.charAt(0));
		Log.i("jason", "MSG_HEAD.charAt(0)="+MSG_HEAD.charAt(0));
		return ( (msgStr.charAt(0) == MSG_HEAD.charAt(0)) ||  (msgStr.charAt(0) == TITLE_START.charAt(0)) );
	}
	
	public static boolean isMsgEnd(String msgStr){
		Log.i("jason", "msgStr.charAt(msgStr.length()-1)="+msgStr.charAt(msgStr.length()-1));
		return msgStr.charAt(msgStr.length()-1) == MSG_END.charAt(MSG_END.length()-1);
	}
	
	public static String getMsgType(String msg){
		if(null != msg){
			if(msg.startsWith(MSG_HEAD)){
				int msgTypeIndex = msg.indexOf(MsgFormat.MSGTYPE_TITLE);
				int msgTypeEnd = msg.indexOf(MsgFormat.ITEM_END);
				if((msgTypeIndex != -1)&&(msgTypeEnd != -1)){
					return msg.substring(msgTypeIndex+MSGTYPE_TITLE.length()+TITLE_END.length(), msgTypeEnd);
				}
				
			}
		}
		return MSGTYPE_UNKNOWN;
	}
	
	public static String getItemContent(String itemTitle,String msg){
		int index = msg.indexOf(itemTitle+TITLE_END);
		
		if(-1 != index){
			int end =  msg.indexOf(ITEM_END,index);
			if(-1 != end){
				return msg.substring(index+itemTitle.length()+TITLE_END.length(),end);
			}
		}
		
		return null;
	}
	protected static String makeMsgItem(String title,String content){
		String item = null;
		item = MsgFormat.TITLE_START+title+MsgFormat.TITLE_END+content+MsgFormat.ITEM_END;
		return item;
	}
//	final static String 
}
