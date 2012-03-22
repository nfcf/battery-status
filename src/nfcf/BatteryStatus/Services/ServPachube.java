package nfcf.BatteryStatus.Services;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import nfcf.BatteryStatus.AppContext;
import nfcf.BatteryStatus.R;
import nfcf.BatteryStatus.Classes.DAL;
import nfcf.BatteryStatus.Classes.PachubeAPI;
import nfcf.BatteryStatus.Classes.Settings;
import nfcf.BatteryStatus.Utils.PhoneUtils;

import org.acra.ErrorReporter;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServPachube extends Service {

	static DAL db = null;
	Timer timerPachube = null;

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
		Log.d("Pachube Service", "Stopping");
		if (Settings.getPachubeInterval() == 0) {
			if (timerPachube != null) {
				timerPachube.cancel();
				timerPachube.purge();
			}
		}
	}

	@Override
	public void onStart(Intent intent, int startid) {
		super.onStart(intent, startid);
		Log.d("Pachube Service", "Starting");

		if (Settings.getPachubeInterval() == 0) {
			timerPachube = new Timer();
			timerPachube.schedule(new TimerTask() {
				@Override
				public void run() {
					sendDataPoints();
				}
			}, 1000, Settings.getBatteryInterval() * 1000 * 60); //Uses the battery interval but only when the phone is not sleeping
		} else {
			sendDataPoints();
			stopSelf();
		}
	}
	
	private static Boolean sendSpecificDataPoints(HashMap<String, Object>[] mapArray, Double divider) {
		Boolean success = true;
		String type = null;
		String dateLimit = null;
		
		if (mapArray != null && mapArray.length > 0) {
			JSONObject jsonMainObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			
			try {
				jsonMainObject.put("datapoints", jsonArray);

				int i = 0;
				for (HashMap<String, Object> map : mapArray) {
					JSONObject jsonDatapoints = new JSONObject();
					type = map.get("type").toString();
					jsonDatapoints.put("at", map.get("occurred_at"));
					if (divider == null || divider == 0.0) {
						jsonDatapoints.put("value", map.get("value"));	
					} else {
						jsonDatapoints.put("value", (double)((Long)map.get("value") / divider));	
					}
					jsonArray.put(jsonDatapoints);
					
					i++;
					if (i==250) {
						dateLimit = map.get("occurred_at").toString();
						break;
					}
				}
			} catch (Exception e) {
				Log.e("sendSpecificDataPoints",e.toString());
				if (type != null) ErrorReporter.getInstance().putCustomData("datastream", type);
				ErrorReporter.getInstance().handleSilentException(e);
			}
			
			if (jsonArray.length() > 0) { 
				if (PachubeAPI.sendDataPoints(Settings.getFeed(), type, Settings.getKey(), jsonMainObject.toString())) {
					db.deleteDatapoints(type, dateLimit);
					Log.d("sendDataPoints", type + " datapoints sent!");
				} else {
					success = false;
				}
			} 
		}

		return success;
	}

	public static String sendDataPoints() {
		Boolean success = true;
		Boolean hasNewDatapoints = false;

		if (PhoneUtils.isOnline(AppContext.getContext())) {
			db = AppContext.getDB();
			db.openDataBase();

			HashMap<String, Object>[] mapArray = null;

			mapArray = db != null ? db.getDatapoints(AppContext.LEVEL) : null;
			if (mapArray != null) {
				hasNewDatapoints = true; 
				success = sendSpecificDataPoints(mapArray, null);
			}

			mapArray = db != null ? db.getDatapoints(AppContext.PLUGGED) : null;
			if (mapArray != null) {
				hasNewDatapoints = true; 
				success = sendSpecificDataPoints(mapArray, null);
			}

			mapArray = db != null ? db.getDatapoints(AppContext.TEMPERATURE) : null;
			if (mapArray != null) {
				hasNewDatapoints = true; 
				success = sendSpecificDataPoints(mapArray, 10.0);
			}

			mapArray = db != null ? db.getDatapoints(AppContext.VOLTAGE) : null;
			if (mapArray != null) {
				hasNewDatapoints = true; 
				success = sendSpecificDataPoints(mapArray, 1000.0);
			}

			mapArray = db != null ? db.getDatapoints(AppContext.SCREEN) : null;
			if (mapArray != null) {
				hasNewDatapoints = true; 
				success = sendSpecificDataPoints(mapArray, 10.0);
			}

			mapArray = db != null ? db.getDatapoints(AppContext.WIFI) : null;
			if (mapArray != null) {
				hasNewDatapoints = true; 
				success = sendSpecificDataPoints(mapArray, null);
			}

			mapArray = db != null ? db.getDatapoints(AppContext.NETWORK) : null;
			if (mapArray != null) {
				hasNewDatapoints = true; 
				success = sendSpecificDataPoints(mapArray, 10.0);
			}
			
			mapArray = db != null ? db.getDatapoints(AppContext.PHONECALL) : null;
			if (mapArray != null) {
				hasNewDatapoints = true; 
				success = sendSpecificDataPoints(mapArray, null);
			}

			mapArray = db != null ? db.getDatapoints(AppContext.BLUETOOTH) : null;
			if (mapArray != null) {
				hasNewDatapoints = true; 
				success = sendSpecificDataPoints(mapArray, null);
			}
			
			mapArray = db != null ? db.getDatapoints(AppContext.RXTX) : null;
			if (mapArray != null) {
				hasNewDatapoints = true; 
				success = sendSpecificDataPoints(mapArray, 1000.0);
			}

			if (hasNewDatapoints) {
				return success ? AppContext.getContext().getString(R.string.msgSyncSuccess) : AppContext.getContext().getString(R.string.msgSyncFailure);
			} else {
				return AppContext.getContext().getString(R.string.msgSyncNoData);
			}
		} else {
			Log.d("sendDataPoints", "datapoints NOT sent!");
			return AppContext.getContext().getString(R.string.noInternetConnection);
		}

	}

}