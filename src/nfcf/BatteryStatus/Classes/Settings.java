package nfcf.BatteryStatus.Classes;

import nfcf.BatteryStatus.AppContext;

public class Settings {

	public static String getUser() {
		return AppContext.settings.getString(AppContext.SETTINGS_USER,"");
	}

	public static void setUser(String value){
		AppContext.settings.edit().putString(AppContext.SETTINGS_USER,value).commit();
	}
	
	public static String getPass() {
		return AppContext.settings.getString(AppContext.SETTINGS_PASS,"");
	}

	public static void setPass(String value){
		AppContext.settings.edit().putString(AppContext.SETTINGS_PASS,value).commit();
	}
	
	public static String getKey() {
		return AppContext.settings.getString(AppContext.SETTINGS_KEY,"");
	}

	public static void setKey(String value){
		AppContext.settings.edit().putString(AppContext.SETTINGS_KEY,value).commit();
	}

	public static String getFeed() {
		return AppContext.settings.getString(AppContext.SETTINGS_FEED,"");
	}

	public static void setFeed(String value){
		if (value == null) value = "";
		AppContext.settings.edit().putString(AppContext.SETTINGS_FEED,value).commit();
	}

	
	public static int getBatteryInterval()
	{
		return AppContext.settings.getInt(AppContext.SETTINGS_BATTERY_INTERVAL,5);
	}

	public static void setBatteryInterval(int value){
		AppContext.settings.edit().putInt(AppContext.SETTINGS_BATTERY_INTERVAL,value).commit();
	}

	public static int getCosmInterval()
	{
		return AppContext.settings.getInt(AppContext.SETTINGS_COSM_INTERVAL,30);
	}

	public static void setCosmInterval(int value){
		AppContext.settings.edit().putInt(AppContext.SETTINGS_COSM_INTERVAL,value).commit();
	}


	public static boolean getPrivate()
	{
		return AppContext.settings.getBoolean(AppContext.SETTINGS_PRIVATE,false);
	}

	public static void setPrivate(boolean value){
		AppContext.settings.edit().putBoolean(AppContext.SETTINGS_PRIVATE,value).commit();
	}
	
	
	public static boolean getServiceStarted()
	{
		return AppContext.settings.getBoolean(AppContext.SETTINGS_SERVICES_STARTED,false);
	}

	public static void setServiceStarted(boolean value){
		AppContext.settings.edit().putBoolean(AppContext.SETTINGS_SERVICES_STARTED,value).commit();
	}

}
