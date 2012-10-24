package com.hoteldemo;

import android.content.Context;
import android.content.SharedPreferences;

public class DataPreferenceKey {
	
	
	final static String PREFERENCE_NAME = "AccountManager";
	final static String ISREGISTER_KEY = "IsRegistered";
	final static String TYPE_KEY = "AccountTYPE";
	final static int    TYPE_PASSENGER = 1;
	final static int 	 TYPE_DRIVER = 2;
	
	
	final static String ACCOUNTNAME_KEY = "AccountName";
	final static String MOBILENUM_KEY = "MobileNum";
	
	final static String CARID_KEY = "CarId";
	final static String CARMODEL_KEY = "CarModel";
	final static String CARFEATURE_KEY = "CarFeature";
	
	final static String DRVRNAME_KEY = "DriverName";
	final static String DRVRTEL_KEY = "DriverTel";
	final static String DRVLCTN_KEY = "DriverLctn";
	final static String DRVCARINFO_KEY = "DrvCarInfo";
	
	final static String SETWAITPOINT_KEY = "IsSetWaitPoint"; 
	final static String WAITLATITUDE_KEY = "WaitLatitude";
	final static String WAITLONGITUDE_KEY = "WaitLongitude";
	final static String WAITTIME_KEY = "WaitTime";
	final static String WAITADDR_KEY = "WaitAddr";
	final static String WAITLCTNTITLE_KEY = "WaitLocationTitle";
	final static String WAITLCTNADDR_KEY = "WaitLocationAddr";
	final static String WAITPOINT_KEY = "WaitPoint";
	final static String PSSNGRNAME_KEY = "PassngerName";
	final static String PSSNGRTEL_KEY = "PssngrTel";
	
	
	final static String CURLATITUDE_KEY = "CurLatitude";
	final static String CURLONGITUDE_KEY = "CurLongitude";

	final static String ISSETCITY_KEY = "IsSetCity";
	final static String SEARCHCITY_KEY = "SearchCity";
	
	final static String ISVOICE_KEY = "IsVoice";
	final static String ISVOICENTFYDSTNC_KEY = "IsVoiceNtfyDstnc";
	
	final static String PAREPHONENUM_KEY = "ParePhoneNum";
	final static String UPDTDRVPRD_KEY = "UpdatePeriod";
	final static String NTFYDSTNC_KEY = "DistanceNotice";
	
	
//	/final static String 
	
	static boolean isRegister(Context con){
		SharedPreferences userInfo = con.getSharedPreferences(DataPreferenceKey.PREFERENCE_NAME, 0);
		return userInfo.getBoolean(ISREGISTER_KEY, false);
	}
	
	
	
	
	
	
	
}
