package in.thefleet.thefuelfilling;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StationDBOpenHelper extends SQLiteOpenHelper{

        //Constants for db name and version
        private static final String DATABASE_NAME = "stations.db";
        private static final int DATABASE_VERSION = 1;
        private static final String TAG = "StationsDbAdapter";

        //Constants for identifying table and columns
        public static final String TABLE_STATIONS = "stations";
        public static final String STATION_ID = "_id";
        public static final String STATION_KEY = "stationID";
        public static final String STATION_NAME = "stationName";
        public static final String STATION_LAT = "stationLat";
        public static final String STATION_LON = "stationLon";
        public static final String STATION_LOCN = "stationLocn";
        public static final String STATION_STATE = "stationState";
        public static final String STATION_FKEY = "stationFkey";
        public static final String STATION_DIS = "stationDistance";
        public static final String STATION_RPRICE = "regularPrice";
        public static final String STATION_PPRICE = "premiumPrice";
        public static final String STATION_CREATED = "stationCreated";

        public static final String[] ALL_COLUMNS =
                {STATION_ID,STATION_KEY,STATION_NAME,STATION_LAT,STATION_LON,
                        STATION_LOCN,STATION_STATE,STATION_FKEY,STATION_DIS,STATION_RPRICE,
                        STATION_PPRICE,STATION_CREATED};

        //SQL to create table
        private static final String TABLE_CREATE =
                "CREATE TABLE " + TABLE_STATIONS + " (" +
                        STATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        STATION_KEY + " INTEGER, " +
                        STATION_NAME + " TEXT, " +
                        STATION_LAT + " DOUBLE, " +
                        STATION_LON + " DOUBLE, " +
                        STATION_LOCN + " TEXT, " +
                        STATION_STATE + " TEXT, " +
                        STATION_FKEY + " INTEGER, " +
                        STATION_DIS + " DOUBLE, " +
                        STATION_RPRICE + " DOUBLE, " +
                        STATION_PPRICE + " DOUBLE, " +
                        STATION_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                        ")";

        public StationDBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, TABLE_CREATE);
            db.execSQL(TABLE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_STATIONS);
            onCreate(db);
        }
    }


