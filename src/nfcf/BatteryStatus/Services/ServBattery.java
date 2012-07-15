package nfcf.BatteryStatus.Services;

import nfcf.BatteryStatus.AppContext;
import nfcf.BatteryStatus.R;
import nfcf.BatteryStatus.Activities.ActMain;
import nfcf.BatteryStatus.Classes.DAL;
import nfcf.BatteryStatus.Classes.LastValue;
import nfcf.BatteryStatus.Classes.Settings;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

public class ServBattery extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("Battery Service", "Stopping");

		unregisterReceiver(batteryReceiver);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		super.onStart(intent, startid);
		Log.d("Battery Service", "Starting");

		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryReceiver, filter);

		stopSelf();
	}

	private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			DAL db = AppContext.getDB();
			db.openDataBase();

			db.setDatapoint(AppContext.LEVEL, intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0));
			db.setDatapoint(AppContext.PLUGGED, intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0));
			db.setDatapoint(AppContext.VOLTAGE, intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0));
			db.setDatapoint(AppContext.TEMPERATURE, intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0));

			// Update Screen stats
			db.setDatapoint(AppContext.SCREEN, LastValue.getScreenBrightness());

			// Update Wifi stats
			db.setDatapoint(AppContext.WIFI, LastValue.getWifiState());

			// Update Network stats
			db.setDatapoint(AppContext.NETWORK, LastValue.getNetworkState());

			// Update Phone Call stats
			db.setDatapoint(AppContext.PHONECALL, LastValue.getPhoneCallState());

			// Update Bluetooth stats
			db.setDatapoint(AppContext.BLUETOOTH, LastValue.getBluetoothState());

			// Update RxTx Traffic stats
			long RxTxBytes = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
			long valueToRecord = RxTxBytes - LastValue.getRxTxBytes() >= 0 ? RxTxBytes - LastValue.getRxTxBytes() : RxTxBytes;
			db.setDatapoint(AppContext.RXTX, valueToRecord);
			LastValue.setRxTxBytes(RxTxBytes);

			if (Settings.getNotification()) {
				// Set the notification icon and message
				int icon = context.getResources().getIdentifier("ic_launcher", "drawable", getPackageName());
				CharSequence tickerText = context.getText(R.string.msgStartServices);
				long when = System.currentTimeMillis();
				// Set the notification contents and which intent to call when
				// clicked
				Notification notification = new Notification(icon, tickerText, when);
				CharSequence contentTitle = getText(R.string.app_name);
				CharSequence contentText = (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) == 0 ? getText(R.string.unplugged) : (intent.getIntExtra(
						BatteryManager.EXTRA_PLUGGED, 0) == 1 ? getText(R.string.charging) : getText(R.string.chargingUSB)))
						+ " / "
						+ intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
						+ "% / "
						+ (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0)
						+ "ºC"
						+ " / " + (intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000.0) + "V";
				Intent notificationIntent = new Intent(context, ActMain.class);
				PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
				notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
				// notification.flags |= Notification.FLAG_ONGOING_EVENT;

				AppContext.mNotificationManager.notify(AppContext.NOTIFICATION_ID, notification);
			}
		}
	};

}
