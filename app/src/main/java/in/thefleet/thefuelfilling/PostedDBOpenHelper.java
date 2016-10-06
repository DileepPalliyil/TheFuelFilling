package in.thefleet.thefuelfilling;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PostedDBOpenHelper extends SQLiteOpenHelper{

    //Constants for db name and version
    private static final String DATABASE_NAME = "sfleets.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "SavedFleetsDBAdapter";

    //Constants for identifying table and columns
    public static final String TABLE_SFLEETS = "sfleets";
    public static final String SFLEETS_ID = "_id";
    public static final String SFLEETS_KEY = "Fleet_ID";
    public static final String SFLEETS_REGNO = "fleetsRegNo";
    public static final String SFLEETS_OKM = "fleetsOKM";
    public static final String SFLEETS_CKM = "fleetsCKM";
    public static final String SFLEETS_QTY = "fleetsQTY";
    public static final String SFLEETS_SID= "fleetsSid";
    public static final String SFLEETS_CREATED = "fleetsCreated";

    public static final String[] ALL_COLUMNS =
            {SFLEETS_ID,SFLEETS_KEY,SFLEETS_REGNO,SFLEETS_OKM,SFLEETS_CKM,SFLEETS_QTY,SFLEETS_SID,SFLEETS_CREATED};
    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_SFLEETS + " (" +
                    SFLEETS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SFLEETS_KEY + " INTEGER, " +
                    SFLEETS_REGNO + " TEXT, " +
                    SFLEETS_OKM + " INTEGER, " +
                    SFLEETS_CKM + " INTEGER, " +
                    SFLEETS_QTY + " DOUBLE, " +
                    SFLEETS_SID + " INTEGER, "  +
                    SFLEETS_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    public PostedDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(TAG, TABLE_CREATE);
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SFLEETS);
        onCreate(db);
    }

}
