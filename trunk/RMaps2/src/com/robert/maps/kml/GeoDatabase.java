package com.robert.maps.kml;

import java.io.File;
import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.robert.maps.R;
import com.robert.maps.utils.RSQLiteOpenHelper;
import com.robert.maps.utils.Ut;

public class GeoDatabase {
	protected final Context mCtx;
	private SQLiteDatabase mDatabase;
	protected final SimpleDateFormat DATE_FORMAT_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	public GeoDatabase(Context ctx) {
		super();
		mCtx = ctx;
		mDatabase = getDatabase();
	}

	public void addPoi(String aName, String aDescr, double aLat, double aLon, double aAlt, int aCategoryId,
			int aPointSourceId) {
		if (isDatabaseReady()) {
			final ContentValues cv = new ContentValues();
			cv.put("name", aName);
			cv.put("descr", aDescr);
			cv.put("lat", aLat);
			cv.put("lon", aLon);
			cv.put("alt", aAlt);
			cv.put("categoryid", aCategoryId);
			cv.put("pointsourceid", aPointSourceId);
			this.mDatabase.insert("points", null, cv);
		}
	}

	public void updatePoi(int id, String aName, String aDescr, double aLat, double aLon, double aAlt, int aCategoryId,
			int aPointSourceId) {
		if (isDatabaseReady()) {
			final ContentValues cv = new ContentValues();
			cv.put("name", aName);
			cv.put("descr", aDescr);
			cv.put("lat", aLat);
			cv.put("lon", aLon);
			cv.put("alt", aAlt);
			cv.put("categoryid", aCategoryId);
			cv.put("pointsourceid", aPointSourceId);
			String[] args = {"" + id};
			this.mDatabase.update("points", cv, "pointid = @1", args);
		}
	}

	public Cursor getPoiListCursor() {
		if (isDatabaseReady()) {
			// �� ������ ������� �����
			return mDatabase.rawQuery("SELECT lat, lon, name, descr, pointid, pointid _id, pointid ID FROM points ORDER BY lat, lon", null);
		}

		return null;
	}

	public Cursor getPoi(final int id) {
		if (isDatabaseReady()) {
			// �� ������ ������� �����
			return mDatabase.rawQuery("SELECT lat, lon, name, descr, pointid FROM points WHERE pointid = " + id + " ORDER BY lat, lon", null);
		}

		return null;
	}

	public void deletePoi(final int id) {
		if (isDatabaseReady()) {
			mDatabase.execSQL("DELETE FROM points WHERE pointid = " + id);
		}
	}

	private boolean isDatabaseReady() {
		boolean ret = true;

		if(mDatabase == null)
			mDatabase = getDatabase();
		
		if(mDatabase == null)
			ret = false;

		if(ret == false)
			Toast.makeText(mCtx, mCtx.getText(R.string.message_geodata_notavailable), Toast.LENGTH_LONG).show();

		return ret;
	}
	
	public void FreeDatabase(){
		if(mDatabase != null){
			mDatabase.close();
			Ut.dd("Close database isOpen="+mDatabase.isOpen());
			mDatabase = null;
		}
	}

	protected SQLiteDatabase getDatabase() {
		File folder = Ut.getRMapsFolder("data", false);
		if(!folder.exists()) // no sdcard
			return null;
		
		Ut.dd("open database");
		SQLiteDatabase db = new GeoDatabaseHelper(mCtx, folder.getAbsolutePath() + "/geodata.db").getWritableDatabase();

		return db;
	}
	
	protected class GeoDatabaseHelper extends RSQLiteOpenHelper {
		public GeoDatabaseHelper(final Context context, final String name) {
			super(context, name, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE IF NOT EXISTS 'points' (pointid INTEGER NOT NULL PRIMARY KEY UNIQUE, name VARCHAR, descr VARCHAR, lat FLOAT DEFAULT '0', lon FLOAT DEFAULT '0', alt FLOAT DEFAULT '0', categoryid INTEGER, pointsourceid INTEGER);");
			db.execSQL("CREATE TABLE IF NOT EXISTS 'pointsource' (pointsourceid INTEGER NOT NULL PRIMARY KEY UNIQUE, name VARCHAR);");
			db.execSQL("CREATE TABLE IF NOT EXISTS 'category' (categoryid INTEGER NOT NULL PRIMARY KEY UNIQUE, name VARCHAR);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			Ut.dd("Upgrade data.db from ver.0 to ver.1");
			// for ver.1
		}
		
	}
}