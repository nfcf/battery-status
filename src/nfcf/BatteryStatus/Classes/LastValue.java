package nfcf.BatteryStatus.Classes;

import nfcf.BatteryStatus.AppContext;

public class LastValue {

	public static int getBatteryLevel() {
		return AppContext.lastValue.getInt(AppContext.LEVEL, 100);
	}

	public static void setBatteryLevel(int value) {
		AppContext.lastValue.edit().putInt(AppContext.LEVEL, value).commit();
	}

	public static int getScreenBrightness() {
		return AppContext.lastValue.getInt(AppContext.SCREEN, 0);
	}

	public static void setScreenBrightness(int value) {
		AppContext.lastValue.edit().putInt(AppContext.SCREEN, value).commit();
	}

	public static int getWifiState() {
		return AppContext.lastValue.getInt(AppContext.WIFI, 0);
	}

	public static void setWifiState(int value) {
		AppContext.lastValue.edit().putInt(AppContext.WIFI, value).commit();
	}

	public static int getNetworkState() {
		return AppContext.lastValue.getInt(AppContext.NETWORK, 0);
	}

	public static void setNetworkState(int value) {
		AppContext.lastValue.edit().putInt(AppContext.NETWORK, value).commit();
	}

	public static int getPhoneCallState() {
		return AppContext.lastValue.getInt(AppContext.PHONECALL, 0);
	}

	public static void setPhoneCallState(int value) {
		AppContext.lastValue.edit().putInt(AppContext.PHONECALL, value).commit();
	}

	public static int getBluetoothState() {
		return AppContext.lastValue.getInt(AppContext.BLUETOOTH, 0);
	}

	public static void setBluetoothState(int value) {
		AppContext.lastValue.edit().putInt(AppContext.BLUETOOTH, value).commit();
	}

	public static long getRxTxBytes() {
		return AppContext.lastValue.getLong(AppContext.RXTX, 0);
	}

	public static void setRxTxBytes(long value) {
		AppContext.lastValue.edit().putLong(AppContext.RXTX, value).commit();
	}

	public static float getDepletionRate() {
		return AppContext.lastValue.getFloat(AppContext.DEPLETION, 0);
	}

	public static void setDepletionRate(float value) {
		AppContext.lastValue.edit().putFloat(AppContext.DEPLETION, value).commit();
	}

	public static long getCollectTime() {
		return AppContext.lastValue.getLong(AppContext.TIME, 0);
	}

	public static void setCollectTime(long value) {
		AppContext.lastValue.edit().putLong(AppContext.TIME, value).commit();
	}
}
