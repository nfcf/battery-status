package nfcf.BatteryStatus.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nfcf.BatteryStatus.AppContext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{

	//The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/" + AppContext.getContext().getPackageName() + "/databases/";
	
	private String dbName = null;
	private String dbVersion = null;
	private Context ctx = null;
	protected SQLiteDatabase db = null; 

	/**
	 * Constructor
	 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * @param context
	 */
	public DataBaseHelper(Context context, String dbName, String dbVersion) {
		super(context, dbName, null, 1);
		
		this.dbName = dbName;
		this.dbVersion = dbVersion;
		this.ctx = context;
	}	

	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 * */
	public void createDataBase() throws IOException{

		boolean dbIsValid = checkDataBase();

		if(dbIsValid){
			//do nothing - database already exist and has a valid version
		}else{

			//By calling this method and empty database will be created into the default system path
			//of your application so we are gonna be able to overwrite that database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();

				//Write current version to database
				ContentValues values = new ContentValues(); 
				values.put("db_version", dbVersion);

				openDataBase();
				db.update("android_metadata", values, null, null);

			} catch (IOException e) {

				throw new Error("Error copying database");

			} catch (Exception ex) {

				Log.e("createDataBase", ex.toString());

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase()
	{
		Boolean dbIsValid = false; 

		try {
			db = SQLiteDatabase.openDatabase( DB_PATH + dbName, null, SQLiteDatabase.OPEN_READONLY);
			
			String version = getDBVersion();
			if (version != null && version.equals(dbVersion)) dbIsValid = true;
		}catch(SQLiteException e){

			//database doesn't exist yet.

		}finally{
			if(db != null) { db.close(); db = null;}
		}

		return dbIsValid;
	}

	/**
	 * Copies your database from your local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException{

		//Open your local db as the input stream
		InputStream inStream = ctx.getAssets().open(dbName);

		// Path to the just created empty db
		String sFileName = DB_PATH + dbName;

		//Open the empty db as the output stream
		OutputStream outStream = new FileOutputStream(sFileName);

		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inStream.read(buffer))>0){
			outStream.write(buffer, 0, length);
		}

		//Close the streams
		outStream.flush();
		outStream.close();
		inStream.close();

	}

	public void openDataBase() throws SQLException{

		if (db == null || !db.isOpen()){
			//Open the database
			String dbPath = DB_PATH + dbName;
			db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
		}

	}

	@Override
	public synchronized void close() {

		if(db != null)
			db.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private String getDBVersion() {
		String returnValue = null;
		Cursor cur = null;

		try	{
			cur = db.query(true, "android_metadata", new String[] {"db_version"}, 
					null, null, null, null, null, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			returnValue = cur.isNull(0) ? null : cur.getString(0);
		}catch(Exception e){
			Log.e("getDBVersion", e.toString());
		}finally{
			if (cur != null) {cur.close();}
		}

		return returnValue;
	}
	
	public void vacuumDataBase() {
		db.execSQL("VACUUM");
		close();
		openDataBase();
	}

	public int getIntValue(String table, String field, String selection, String[] selectionArgs) {
		int returnValue = 0;
		Cursor cur = null;

		try	{
			cur = this.getReadableDatabase().query(true, table, new String[] {field}, 
					selection, selectionArgs, null, null, null, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			returnValue = cur.isNull(0) ? 0 : cur.getInt(0);
		}catch(SQLException e){
			Log.e("getIntValue", e.toString());
		}finally{
			if (cur != null) {cur.close();}
		}

		return returnValue;
	}

	public double getDoubleValue(String table, String field, String selection, String[] selectionArgs) {
		double returnValue = 0.0;
		Cursor cur = null;

		try	{
			cur = db.query(true, table, new String[] {field}, 
					selection, selectionArgs, null, null, null, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			returnValue = cur.isNull(0) ? 0.0 : cur.getDouble(0);
		}catch(SQLException e){
			Log.e("getDoubleValue", e.toString());
		}finally{
			if (cur != null) {cur.close();}
		}

		return returnValue;
	}

	public float getFloatValue(String table, String field, String selection, String[] selectionArgs) {
		float returnValue = 0;
		Cursor cur = null;

		try	{
			cur = db.query(true, table, new String[] {field}, 
					selection, selectionArgs, null, null, null, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			returnValue = cur.isNull(0) ? 0 : cur.getFloat(0);
		}catch(SQLException e){
			Log.e("getFloatValue", e.toString());
		}finally{
			if (cur != null) {cur.close();}
		}

		return returnValue;
	}

	public long getLongValue(String table, String field, String selection, String[] selectionArgs) {
		long returnValue = 0;
		Cursor cur = null;

		try	{
			cur = db.query(true, table, new String[] {field}, 
					selection, selectionArgs, null, null, null, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			returnValue = cur.isNull(0) ? 0 : cur.getLong(0);
		}catch(SQLException e){
			Log.e("getLongValue", e.toString());
		}finally{
			if (cur != null) {cur.close();}
		}

		return returnValue;
	}

	public String getStringValue(String table, String field, String selection, String[] selectionArgs) {
		String returnValue = null;
		Cursor cur = null;

		try	{
			cur = db.query(true, table, new String[] {field}, 
					selection, selectionArgs, null, null, null, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			returnValue = cur.isNull(0) ? null : cur.getString(0);
		}catch(SQLException e){
			Log.e("getStringValue", e.toString());
		}finally{
			if (cur != null) {cur.close();}
		}

		return returnValue;
	}
	
	public Boolean getBooleanValue(String table, String field, String selection, String[] selectionArgs) {
		Boolean returnValue = null;
		Cursor cur = null;

		try	{
			cur = db.query(true, table, new String[] {field}, 
					selection, selectionArgs, null, null, null, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			returnValue = cur.isNull(0) ? null : cur.getShort(0) != 0;
		}catch(SQLException e){
			Log.e("getBooleanValue", e.toString());
		}finally{
			if (cur != null) {cur.close();}
		}

		return returnValue;
	}


}
