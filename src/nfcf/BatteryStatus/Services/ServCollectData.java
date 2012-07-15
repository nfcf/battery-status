package nfcf.BatteryStatus.Services;

import org.acra.ErrorReporter;

import nfcf.BatteryStatus.AppContext;
import nfcf.BatteryStatus.R;
import nfcf.BatteryStatus.Activities.ActMain;
import nfcf.BatteryStatus.Classes.DAL;
import nfcf.BatteryStatus.Classes.LastValue;
import nfcf.BatteryStatus.Classes.Settings;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ServCollectData extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("Other Data Service", "Starting");

		IntentFilter filter1 = new IntentFilter(Intent.ACTION_SCREEN_ON);
		registerReceiver(screenReceiver, filter1);
		IntentFilter filter2 = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenReceiver, filter2);
		IntentFilter filter3 = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
		registerReceiver(wifiReceiver, filter3);
		IntentFilter filter4 = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(networkReceiver, filter4);
		IntentFilter filter5 = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		registerReceiver(phoneCallReceiver, filter5);
		IntentFilter filter6 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(bluetoothReceiver, filter6);

		if (Settings.getNotification()) {
			// Set the notification icon and message
			Context ctx = AppContext.getContext();
			int icon = ctx.getResources().getIdentifier("ic_launcher", "drawable", getPackageName());
			CharSequence tickerText = ctx.getText(R.string.msgStartServices);
			long when = System.currentTimeMillis();
			// Set the notification contents and which intent to call when
			// clicked
			Notification notification = new Notification(icon, tickerText, when);
			CharSequence contentTitle = getText(R.string.app_name);
			CharSequence contentText = getText(R.string.collectingData);
			Intent notificationIntent = new Intent(ctx, ActMain.class);
			PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
			notification.setLatestEventInfo(ctx, contentTitle, contentText, contentIntent);

			// Shows a notification and signals that this service is top
			// priority
			startForeground(AppContext.NOTIFICATION_ID, notification);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("Other Data Service", "Stopping");

		unregisterReceiver(screenReceiver);
		unregisterReceiver(wifiReceiver);
		unregisterReceiver(networkReceiver);
		unregisterReceiver(bluetoothReceiver);
		unregisterReceiver(phoneCallReceiver);

		// If something killed the service and shouldn't have, try restarting it
		if (Settings.getServiceStarted())
			AppContext.startServices();

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		ErrorReporter.getInstance().handleSilentException(new Exception("Low memory condition detected"));

		if (Settings.getNotification()) {
			// Stops the service and dismisses the notification
			stopForeground(true);
		}

		stopSelf();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		// I want this service to continue running until it is explicitly
		// stopped. So return Start_Sticky.
		return START_STICKY;
	}

	private BroadcastReceiver screenReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			DAL db = AppContext.getDB();
			db.openDataBase();

			int brightness = 0;
			try {
				brightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
			} catch (SettingNotFoundException e) {
				Log.e("screenReceiver", e.toString());
			}

			db.setDatapoint(AppContext.SCREEN, intent.getAction().equals(Intent.ACTION_SCREEN_OFF) ? 0 : brightness);
			LastValue.setScreenBrightness(intent.getAction().equals(Intent.ACTION_SCREEN_OFF) ? 0 : brightness);
		}

	};

	private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			DAL db = AppContext.getDB();
			db.openDataBase();

			int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

			db.setDatapoint(AppContext.WIFI, extraWifiState == WifiManager.WIFI_STATE_ENABLED ? 1 : 0);
			LastValue.setWifiState(extraWifiState == WifiManager.WIFI_STATE_ENABLED ? 1 : 0);

		}

	};

	public static final int NETWORK_TYPE_EHRPD = 14; // Level 11
	public static final int NETWORK_TYPE_HSPAP = 15; // Level 13
	public static final int NETWORK_TYPE_LTE = 13; // Level 11
	public static final int NETWORK_TYPE_EVDO_B = 12; // Level 9

	private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			DAL db = AppContext.getDB();
			db.openDataBase();

			int networkType = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType();

			/*
			 * if (PhoneUtils.isOnline(AppContext.getContext())) {
			 * db.setDatapoint(AppContext.NETWORK, 0); } else
			 */if (networkType == TelephonyManager.NETWORK_TYPE_GPRS || networkType == TelephonyManager.NETWORK_TYPE_CDMA
					|| networkType == TelephonyManager.NETWORK_TYPE_IDEN) {
				db.setDatapoint(AppContext.NETWORK, 20);
				LastValue.setNetworkState(20);
			} else if (networkType == TelephonyManager.NETWORK_TYPE_EDGE || networkType == TelephonyManager.NETWORK_TYPE_1xRTT) {
				db.setDatapoint(AppContext.NETWORK, 25);
				LastValue.setNetworkState(25);
			} else if (networkType == TelephonyManager.NETWORK_TYPE_UMTS || networkType == TelephonyManager.NETWORK_TYPE_HSPA
					|| networkType == NETWORK_TYPE_EHRPD || networkType == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| networkType == TelephonyManager.NETWORK_TYPE_EVDO_A || networkType == NETWORK_TYPE_EVDO_B) {
				db.setDatapoint(AppContext.NETWORK, 30);
				LastValue.setNetworkState(30);
			} else if (networkType == TelephonyManager.NETWORK_TYPE_HSDPA || networkType == TelephonyManager.NETWORK_TYPE_HSUPA
					|| networkType == NETWORK_TYPE_HSPAP) {
				db.setDatapoint(AppContext.NETWORK, 35);
				LastValue.setNetworkState(35);
			} else if (networkType == NETWORK_TYPE_LTE) {
				db.setDatapoint(AppContext.NETWORK, 40);
				LastValue.setNetworkState(40);
			} else {
				db.setDatapoint(AppContext.NETWORK, 0);
				LastValue.setNetworkState(0);
			}
		}
	};

	private BroadcastReceiver phoneCallReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			DAL db = AppContext.getDB();
			db.openDataBase();

			int phoneCallState = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getCallState();

			db.setDatapoint(AppContext.PHONECALL, phoneCallState > 1 ? 1 : 0); // 0
																				// -
																				// IDLE;
																				// 1
																				// -
																				// RINGING;
																				// 2
																				// -
																				// CALL
																				// ESTABLISHED
			LastValue.setPhoneCallState(phoneCallState > 1 ? 1 : 0);
		}

	};

	private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			DAL db = AppContext.getDB();
			db.openDataBase();

			int extraBluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

			db.setDatapoint(AppContext.BLUETOOTH, extraBluetoothState == BluetoothAdapter.STATE_OFF ? 0 : 1);
			LastValue.setBluetoothState(extraBluetoothState == BluetoothAdapter.STATE_OFF ? 0 : 1);
		}

	};

}

// May be useful in the future

// public static boolean isConnectionFast(int type, int subType){
// if(type==ConnectivityManager.TYPE_WIFI){
// System.out.println("CONNECTED VIA WIFI");
// return true;
// }else if(type==ConnectivityManager.TYPE_MOBILE){
// switch(subType){
// case TelephonyManager.NETWORK_TYPE_1xRTT:
// return false; // ~ 50-100 kbps
// case TelephonyManager.NETWORK_TYPE_CDMA:
// return false; // ~ 14-64 kbps
// case TelephonyManager.NETWORK_TYPE_EDGE:
// return false; // ~ 50-100 kbps
// case TelephonyManager.NETWORK_TYPE_EVDO_0:
// return true; // ~ 400-1000 kbps
// case TelephonyManager.NETWORK_TYPE_EVDO_A:
// return true; // ~ 600-1400 kbps
// case TelephonyManager.NETWORK_TYPE_GPRS:
// return false; // ~ 100 kbps
// case TelephonyManager.NETWORK_TYPE_HSDPA:
// return true; // ~ 2-14 Mbps
// case TelephonyManager.NETWORK_TYPE_HSPA:
// return true; // ~ 700-1700 kbps
// case TelephonyManager.NETWORK_TYPE_HSUPA:
// return true; // ~ 1-23 Mbps
// case TelephonyManager.NETWORK_TYPE_UMTS:
// return true; // ~ 400-7000 kbps
// // NOT AVAILABLE YET IN API LEVEL 7
// case Connectivity.NETWORK_TYPE_EHRPD:
// return true; // ~ 1-2 Mbps
// case Connectivity.NETWORK_TYPE_EVDO_B:
// return true; // ~ 5 Mbps
// case Connectivity.NETWORK_TYPE_HSPAP:
// return true; // ~ 10-20 Mbps
// case Connectivity.NETWORK_TYPE_IDEN:
// return false; // ~25 kbps
// case Connectivity.NETWORK_TYPE_LTE:
// return true; // ~ 10+ Mbps
// // Unknown
// case TelephonyManager.NETWORK_TYPE_UNKNOWN:
// return false;
// default:
// return false;
// }
// }else{
// return false;
// }
// }
