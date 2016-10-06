package in.thefleet.thefuelfilling;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class StationsDataSource extends ContentProvider {

    private static final String TAG = "StationsDataSource";

    private static final String AUTHORITY2 = "in.thefleet.thefuelfilling.stationsdatasource";
    private static final String BASE_PATH2 = "stations";
    public static final Uri CONTENT_URI2 = Uri.parse("content://" + AUTHORITY2 + "/" + BASE_PATH2 );

    // Constant to identify the requested operation
    private static final int STATIONS = 1;
    private static final int STATIONS_ID = 2;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Stations";

    static  {
        uriMatcher.addURI(AUTHORITY2,BASE_PATH2,STATIONS);
        uriMatcher.addURI(AUTHORITY2,BASE_PATH2 + "/#",STATIONS_ID);

    };
    private SQLiteDatabase database;
    StationDBOpenHelper dbhelper;


    @Override
    public boolean onCreate() {
        dbhelper = new StationDBOpenHelper(getContext());
        database = dbhelper.getWritableDatabase();
        Log.i(TAG, "Stations Database opened");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == STATIONS_ID) {
            selection = StationDBOpenHelper.STATION_ID + "=" + uri.getLastPathSegment();
        }
        // Log.d(TAG,"Selection: "+selection);
        return database.query(StationDBOpenHelper.TABLE_STATIONS,StationDBOpenHelper.ALL_COLUMNS,selection,null,null,null,
                StationDBOpenHelper.STATION_DIS + " ASC");
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(StationDBOpenHelper.TABLE_STATIONS,null,values);
        return Uri.parse(BASE_PATH2 + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(StationDBOpenHelper.TABLE_STATIONS,selection,selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[]selectionArgs) {
        return database.update(StationDBOpenHelper.TABLE_STATIONS,values,selection,selectionArgs);
    }
}