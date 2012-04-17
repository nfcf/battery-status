package nfcf.BatteryStatus.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nfcf.BatteryStatus.AppContext;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class DataBaseHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/" + AppContext.getContext().getPackageName() + "/databases/";
 
	private String dbName = null;
	private Context ctx = null;
	protected SQLiteDatabase db = null;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 */
	public DataBaseHelper(Context context, String dbName, int dbVersion) {
		super(context, dbName, null, dbVersion);

		this.dbName = dbName;
		this.ctx = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExists = checkDataBase();
		
		openDataBase();	// this is needed to create an empty DB if it doesn't exist. If it does, it's used to check the DB version
		close();
		
		if (dbExists) {
			// do nothing - database already exist. onUpgrade will take care of the version differences if there are any.
		} else {

			try {
				
				copyDataBase();

			} catch (Exception ex) {
				Log.e("createDataBase", ex.toString());
			}
		}
		
		openDataBase();	//needed to make sure the onUpgrade works everytime (i.e.: if the version changes twice between executions and no openDatabase is called in between)

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {
		Boolean dbIsValid = false;

		try {
			db = SQLiteDatabase.openDatabase(DB_PATH + dbName, null, SQLiteDatabase.OPEN_READONLY);

			dbIsValid = true;
		} catch (SQLiteException e) {

			// database doesn't exist yet.

		} finally {
			close();
		}

		return dbIsValid;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream inStream = ctx.getAssets().open(dbName);

		// Path to the just created empty db
		String sFileName = DB_PATH + dbName;

		// Open the empty db as the output stream
		OutputStream outStream = new FileOutputStream(sFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inStream.read(buffer)) > 0) {
			outStream.write(buffer, 0, length);
		}

		// Close the streams
		outStream.flush();
		outStream.close();
		inStream.close();
	}

	public void openDataBase() throws SQLException {
		if (db == null || !db.isOpen()) {
			db = this.getWritableDatabase();
		}
	}
	
	public void vacuumDataBase() {
		if (db == null || !db.isOpen()) {
			db.execSQL("VACUUM");
			close();
			openDataBase();
		}
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	/// This method must be overrided in the child class. By default it just deletes the existing DB and copies a new one from the assets folder
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("onUpgrade", "Upgrading database from version " + oldVersion + " to " + newVersion + "...");
		
		try {
			ctx.deleteDatabase(dbName);
			copyDataBase();
		} catch (Exception e) {
			Log.e("onUpgrade", e.toString());
		}
	}
	
	@Override
	public synchronized void close() {

		if (db != null) {
			db.close();
			db = null;
		}

		super.close();

	}

	public int getIntValue(String table, String field, String selection, String[] selectionArgs) {
		int returnValue = 0;
		Cursor cur = null;

		try {
			cur = this.getReadableDatabase().query(true, table,
					new String[] { field }, selection, selectionArgs, null,
					null, null, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			returnValue = cur.isNull(0) ? 0 : cur.getInt(0);
		} catch (SQLException e) {
			Log.e("getIntValue", e.toString());
		} finally {
			if (cur != null) {
				cur.close();
			}
		}

		return returnValue;
	}

	public double getDoubleValue(String table, String field, String selection, String[] selectionArgs) {
		double returnValue = 0.0;
		Cursor cur = null;

		try {
			cur = db.query(true, table, new String[] { field }, selection,
					selectionArgs, null, null, null, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			returnValue = cur.isNull(0) ? 0.0 : cur.getDouble(0);
		} catch (SQLException e) {
			Log.e("getDoubleValue", e.toString());
		} finally {
			if (cur != null) {
				cur.close();
			}
		}

		return returnValue;
	}

	public float getFloatValue(String table, String field, String selection, String[] selectionArgs) {
		float returnValue = 0;
		Cursor cur = null;

		try {
			cur = db.query(true, table, new String[] { field }, selection,
					selectionArgs, null, null, null, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			returnValue = cur.isNull(0) ? 0 : cur.getFloat(0);
		} catch (SQLException e) {
			Log.e("getFloatValue", e.toString());
		} finally {
			if (cur != null) {
				cur.close();
			}
		}

		return returnValue;
	}

	public long getLongValue(String table, String field, String selection, String[] selectionArgs) {
		long returnValue = 0;
		Cursor cur = null;

		try {
			cur = db.query(true, table, new String[] { field }, selection,
					selectionArgs, null, null, null, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			returnValue = cur.isNull(0) ? 0 : cur.getLong(0);
		} catch (SQLException e) {
			Log.e("getLongValue", e.toString());
		} finally {
			if (cur != null) {
				cur.close();
			}
		}

		return returnValue;
	}

	public String getStringValue(String table, String field, String selection, String[] selectionArgs) {
		String returnValue = null;
		Cursor cur = null;

		try {
			cur = db.query(true, table, new String[] { field }, selection,
					selectionArgs, null, null, null, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			returnValue = cur.isNull(0) ? null : cur.getString(0);
		} catch (SQLException e) {
			Log.e("getStringValue", e.toString());
		} finally {
			if (cur != null) {
				cur.close();
			}
		}

		return returnValue;
	}

	public Boolean getBooleanValue(String table, String field, String selection, String[] selectionArgs) {
		Boolean returnValue = null;
		Cursor cur = null;

		try {
			cur = db.query(true, table, new String[] { field }, selection,
					selectionArgs, null, null, null, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			returnValue = cur.isNull(0) ? null : cur.getShort(0) != 0;
		} catch (SQLException e) {
			Log.e("getBooleanValue", e.toString());
		} finally {
			if (cur != null) {
				cur.close();
			}
		}

		return returnValue;
	}

}
