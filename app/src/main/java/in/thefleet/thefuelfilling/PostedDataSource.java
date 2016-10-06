package in.thefleet.thefuelfilling;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;


public class PostedDataSource extends ContentProvider {

    private static final String TAG = "FleetsDataSource";

    private static final String AUTHORITY = "in.thefleet.thefuelfilling.posteddatasource";
    private static final String BASE_PATH = "sfleets";
    public static final Uri CONTENT_URI3 = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int SFLEETS = 1;
    private static final int SFLEETS_ID = 2;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "sFleet";

    static  {
        uriMatcher.addURI(AUTHORITY,BASE_PATH,SFLEETS);
        uriMatcher.addURI(AUTHORITY,BASE_PATH + "/#",SFLEETS_ID);

    };
    private SQLiteDatabase database;
    PostedDBOpenHelper dbhelper;

    @Override
    public boolean onCreate() {
        dbhelper = new PostedDBOpenHelper(getContext());
        database = dbhelper.getWritableDatabase();
        Log.i(TAG, "Database opened");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == SFLEETS_ID) {
            selection = PostedDBOpenHelper.SFLEETS_ID + "=" + uri.getLastPathSegment();
        }
        // Log.d(TAG,"Selection: "+selection);
        return database.query(PostedDBOpenHelper.TABLE_SFLEETS,PostedDBOpenHelper.ALL_COLUMNS,selection,null,null,null,
                PostedDBOpenHelper.SFLEETS_CREATED + " DESC");
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(PostedDBOpenHelper.TABLE_SFLEETS,null,values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(PostedDBOpenHelper.TABLE_SFLEETS,selection,selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[]selectionArgs) {
        return database.update(PostedDBOpenHelper.TABLE_SFLEETS,values,selection,selectionArgs);
    }
}
