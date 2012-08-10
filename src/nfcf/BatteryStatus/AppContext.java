package nfcf.BatteryStatus;

import nfcf.BatteryStatus.Activities.ActMain;
import nfcf.BatteryStatus.Classes.DAL;
import nfcf.BatteryStatus.Classes.Settings;
import nfcf.BatteryStatus.Services.ServBattery;
import nfcf.BatteryStatus.Services.ServCollectData;
import nfcf.BatteryStatus.Services.ServSendData;
import nfcf.BatteryStatus.Utils.ObscuredSharedPreferences;
import nfcf.BatteryStatus.Utils.PhoneUtils;
import nfcf.BatteryStatus.Utils.StringUtils;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.apache.http.HttpStatus;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

//import com.google.android.apps.analytics.GoogleAnalyticsTracker;

@ReportsCrashes(formKey = "dExtalhDVmtCY3VWc0hWZU53NTJMMGc6MQ", mode = ReportingInteractionMode.TOAST, forceCloseDialogAfterToast = false, resToastText = R.string.crashText)
public class AppContext extends Application {
	private static AppContext instance = null;
	private static DAL db = null;
	public static NotificationManager mNotificationManager;
	// public static GoogleAnalyticsTracker tracker = null;

	public static PendingIntent pendingBatteryIntent = null;
	public static PendingIntent pendingSendDataIntent = null;

	public static final String START_SERVICES_COMPLETED = "nfcf.BatteryStatus.intent.action.START_SERVICES_COMPLETED";
	public static final String FORCE_SYNC_COMPLETED = "nfcf.BatteryStatus.intent.action.FORCE_SYNC_COMPLETED";
	public static final int NOTIFICATION_ID = 1;

	public static final String LEVEL = "level";
	public static final String TEMPERATURE = "temperature";
	public static final String VOLTAGE = "voltage";
	public static final String PLUGGED = "plugged";
	public static final String SCREEN = "screen";
	public static final String WIFI = "wifi";
	public static final String NETWORK = "network";
	public static final String BLUETOOTH = "bluetooth";
	public static final String PHONECALL = "phonecall";
	public static final String RXTX = "RxTx";

	private static final String LAST_VALUES_FILENAME = "lastValues";
	private static final String SETTINGS_FILENAME = "settings";

	public static final String SETTINGS_USER = "user";
	public static final String SETTINGS_PASS = "pass";
	public static final String SETTINGS_KEY = "key";
	public static final String SETTINGS_FEED = "feed";
	public static final String SETTINGS_BATTERY_INTERVAL = "battery_interval";
	public static final String SETTINGS_COSM_INTERVAL = "pachube_interval";
	public static final String SETTINGS_PRIVATE = "private";
	public static final String SETTINGS_NOTIFICATION = "notification";
	public static final String SETTINGS_SERVICES_STARTED = "services_started";

	public static final int STATUS_WRONG_KEY = -1;
	public static final int STATUS_WRONG_FEED = -2;
	public static final int STATUS_WRONG_KEY_AND_FEED = -3;

	public static int lastStatusCode = 0;

	public static SharedPreferences settings = null;
	public static SharedPreferences lastValue = null;

	@Override
	public void onCreate() {
		// The following line triggers the initialization of ACRA
		ACRA.init(this);

		super.onCreate();

		db = new DAL(this);

		settings = new ObscuredSharedPreferences(this, this.getSharedPreferences(SETTINGS_FILENAME, Context.MODE_PRIVATE));
		lastValue = this.getSharedPreferences(LAST_VALUES_FILENAME, Context.MODE_PRIVATE);

		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// tracker = GoogleAnalyticsTracker.getInstance();
		// tracker.startNewSession("UA-29342743-1", 30, this);
 
		// Checks to see if the service is running. If not (and it should), start it
		if (Settings.getServiceStarted()) {
			ActivityManager manager = (ActivityManager) AppContext.getContext().getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
				if ("nfcf.BatteryStatus.Services.ServCollectData".equals(service.service.getClassName())) {
					return;
				}
			}
			AppContext.startServices(); 
		}
	}

	public AppContext() {
		instance = this;
	}

	public static AppContext getContext() {
		return instance;
	}

	public static DAL getDB() {
		return db;
	}

	public static void startServices() {
		Context ctx = AppContext.getContext();

		if (!StringUtils.isNullOrBlank(Settings.getKey()) && !StringUtils.isNullOrBlank(Settings.getFeed())) {

			Settings.setServiceStarted(true);
			
			Intent batteryIntent = new Intent(ctx, ServBattery.class);
			AppContext.pendingBatteryIntent = PendingIntent.getService(AppContext.getContext(), 0, batteryIntent, 0);

			Intent sendDataIntent = new Intent(ctx, ServSendData.class);
			AppContext.pendingSendDataIntent = PendingIntent.getService(AppContext.getContext(), 0, sendDataIntent, 0);

			AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, Settings.getBatteryInterval() * 60 * 1000,
					AppContext.pendingBatteryIntent);

			if (Settings.getCosmInterval() > 0) {
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, Settings.getCosmInterval() * 60 * 1000,
						AppContext.pendingSendDataIntent);
			} else {
				ctx.startService(new Intent(AppContext.getContext(), ServSendData.class));
			}

			ctx.startService(new Intent(AppContext.getContext(), ServCollectData.class));

			// //Set the notification icon and message
			// int icon = R.drawable.ic_launcher;
			// CharSequence tickerText = ctx.getText(R.string.msgStartServices);
			// long when = System.currentTimeMillis();
			// //Set the notification contents and which intent to call when
			// clicked
			// Notification notification = new Notification(icon, tickerText,
			// when);
			// CharSequence contentTitle = "Battery Status";
			// CharSequence contentText = "Collecting data...";
			// Intent notificationIntent = new Intent(ctx, ActMain.class);
			// PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
			// notificationIntent, 0);
			//
			// notification.setLatestEventInfo(ctx, contentTitle, contentText,
			// contentIntent);
			// notification.flags |= Notification.FLAG_ONGOING_EVENT;
			//
			// mNotificationManager.notify(NOTIFICATION_ID, notification);

			if (!Settings.getNotification()) Toast.makeText(ctx, R.string.msgStartServices, Toast.LENGTH_LONG).show();

		} else {
			Settings.setServiceStarted(false);
			if (AppContext.lastStatusCode == HttpStatus.SC_UNAUTHORIZED) {
				Toast.makeText(ctx, ctx.getString(R.string.couldNotConnect) + " " + ctx.getString(R.string.wrongCredentials), Toast.LENGTH_LONG).show();
			} else if (!PhoneUtils.isOnline(ctx)) {
				Toast.makeText(ctx, ctx.getString(R.string.couldNotConnect) + " " + ctx.getString(R.string.noInternetConnection), Toast.LENGTH_LONG).show();
			} else if (StringUtils.isNullOrBlank(Settings.getKey())) {
				Toast.makeText(ctx, ctx.getString(R.string.errorKey), Toast.LENGTH_LONG).show();
			} else if (StringUtils.isNullOrBlank(Settings.getFeed())) {
				Toast.makeText(ctx, ctx.getString(R.string.errorFeed), Toast.LENGTH_LONG).show();
			}
		}

		try {
			ActMain.getInstance().updateControls();
		} catch (Exception ex) {

		}

	}

	public static void stopServices() {
		Context ctx = AppContext.getContext();
		
		Settings.setServiceStarted(false);

		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
		alarmManager.cancel(AppContext.pendingBatteryIntent);

		if (Settings.getCosmInterval() > 0) {
			alarmManager.cancel(AppContext.pendingSendDataIntent);
		} else {
			ctx.stopService(new Intent(ctx, ServSendData.class));
		}

		ctx.stopService(new Intent(ctx, ServCollectData.class));

		// mNotificationManager.cancel(NOTIFICATION_ID);

		try {
			if (!Settings.getNotification()) Toast.makeText(ActMain.getInstance(), R.string.msgStopServices, Toast.LENGTH_LONG).show();
			ActMain.getInstance().updateControls();
		} catch (Exception ex) {

		}
	}

}
