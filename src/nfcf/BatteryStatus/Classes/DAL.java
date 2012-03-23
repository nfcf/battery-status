package nfcf.BatteryStatus.Classes;

import java.io.IOException;
import java.util.HashMap;

import nfcf.BatteryStatus.Utils.DataBaseHelper;
import nfcf.BatteryStatus.Utils.StringUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


public class DAL extends DataBaseHelper {

	private static final String DB_NAME = "BatteryStatus.s3db";
	private static final String DB_VERSION = "1.3.0";
	
	public DAL(Context context) {
		super(context, DB_NAME, DB_VERSION);

		try {

			createDataBase();

		} catch (IOException ioe) {

			throw new Error("Unable to create database");

		}
	}
	
	//private Boolean _service_started = null;
	
	//public void clearCache()
	//{
		//_service_started = null;
	//}
	
//	public Boolean getServiceStarted()
//	{
//		if (_service_started == null) {
//			_service_started = this.getBooleanValue("config","service_started", null, null); 
//		} 
//		return _service_started;
//	}
//	
//	public void setServiceStarted(Boolean value){
//		try{
//        	ContentValues values = new ContentValues();
//            values.put("service_started", value);
//            
//            db.update("config", values, null, null);
//            _service_started = value;
//        }catch(Exception e){
//        	Log.e("setServiceStarted", e.toString());
//        }
//	}

	public void deleteDatapoints(String type, String idLimit) {
		if (StringUtils.isNullOrBlank(idLimit)){
			db.delete("datapoints", "type=?", new String[]{type});
		} else {
			db.delete("datapoints", "type=? AND _id<=?", new String[]{type, idLimit});
		}
		
	}
	
	public void setDatapoint(String type, int value){
		setDatapoint(type, (long) value);
	}
	
	public void setDatapoint(String type, long value){
		try{
        	ContentValues values = new ContentValues();
            values.put("type", type);
            values.put("value", value);
            
            db.insertOrThrow("datapoints", null, values);
        }catch(Exception e){
        	Log.e("setDatapoint", e.toString());
        }
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Object>[] getDatapoints(String type){
		HashMap<String, Object>[] map = null;
		int i = 0;
		Cursor cur = null;

		try {
			cur = db.query("datapoints", new String[]{"_id","type","value","occurred_at"}, "type=?", new String[]{type}, null, null, "_id ASC");
			if (cur.getCount() > 0)
			{
				map = new HashMap[cur.getCount()];
				cur.moveToFirst();
				while (cur.isAfterLast() == false) {
					map[i] = new HashMap<String, Object>();
					map[i].put("id", cur.isNull(0) ? null : cur.getInt(0));
					map[i].put("type", cur.isNull(1) ? null : cur.getString(1));
					map[i].put("value", cur.isNull(2) ? null : cur.getLong(2));
					map[i].put("occurred_at", cur.getString(3));

					i++;
					cur.moveToNext();
				}
			}
		} catch (Exception e) {
			Log.e("getDatapoints", e.toString());
		}finally {
			if (cur != null) {cur.close();}
		}

		return map;
	}
}
