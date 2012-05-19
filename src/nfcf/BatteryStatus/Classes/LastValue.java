package nfcf.BatteryStatus.Classes;

import nfcf.BatteryStatus.AppContext;

public class LastValue {

	public static int getScreenBrightness() {
		return AppContext.lastValue.getInt(AppContext.SCREEN,0);
	}

	public static void setScreenBrightness(int value){
		AppContext.lastValue.edit().putInt(AppContext.SCREEN,value).commit();
	}
	
	public static int getWifiState() {
		return AppContext.lastValue.getInt(AppContext.WIFI,0);
	}

	public static void setWifiState(int value){
		AppContext.lastValue.edit().putInt(AppContext.WIFI,value).commit();
	}
	
	public static int getNetworkState() {
		return AppContext.lastValue.getInt(AppContext.NETWORK,0);
	}

	public static void setNetworkState(int value){
		AppContext.lastValue.edit().putInt(AppContext.NETWORK,value).commit();
	}

	public static int getPhoneCallState() {
		return AppContext.lastValue.getInt(AppContext.PHONECALL,0);
	}

	public static void setPhoneCallState(int value){
		AppContext.lastValue.edit().putInt(AppContext.PHONECALL,value).commit();
	}
	
	public static int getBluetoothState() {
		return AppContext.lastValue.getInt(AppContext.BLUETOOTH,0);
	}

	public static void setBluetoothState(int value){
		AppContext.lastValue.edit().putInt(AppContext.BLUETOOTH,value).commit();
	}

	
	public static long getRxTxBytes()
	{
		return AppContext.lastValue.getLong(AppContext.RXTX,0);
	}

	public static void setRxTxBytes(long value){
		AppContext.lastValue.edit().putLong(AppContext.RXTX,value).commit();
	}

}
