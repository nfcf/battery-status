package nfcf.BatteryStatus.Services;

import nfcf.BatteryStatus.AppContext;
import nfcf.BatteryStatus.Classes.DAL;
import nfcf.BatteryStatus.Classes.LastValue;

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
		
		//if (Settings.getServiceStarted()) {
			unregisterReceiver(batteryReceiver);
		//}
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
			
			
			//Update Screen stats
			db.setDatapoint(AppContext.SCREEN, LastValue.getScreenBrightness());
			
			//Update Wifi stats
			db.setDatapoint(AppContext.WIFI, LastValue.getWifiState());

			//Update Network stats
			db.setDatapoint(AppContext.NETWORK, LastValue.getNetworkState());
			
			//Update Phone Call stats
			db.setDatapoint(AppContext.PHONECALL, LastValue.getPhoneCallState());
			
			//Update Bluetooth stats
			db.setDatapoint(AppContext.BLUETOOTH, LastValue.getBluetoothState());
			
			//Update RxTx Traffic stats
			long RxTxBytes = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
			long valueToRecord = RxTxBytes - LastValue.getRxTxBytes() >= 0 ? RxTxBytes - LastValue.getRxTxBytes() : RxTxBytes;
			db.setDatapoint(AppContext.RXTX, valueToRecord);
			LastValue.setRxTxBytes(RxTxBytes);
		}
	};
    
}

