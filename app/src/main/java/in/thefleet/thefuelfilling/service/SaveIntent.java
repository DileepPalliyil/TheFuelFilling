package in.thefleet.thefuelfilling.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import in.thefleet.thefuelfilling.FleetsDBOpenHelper;
import in.thefleet.thefuelfilling.FleetsDataSource;
import in.thefleet.thefuelfilling.MainActivity;
import in.thefleet.thefuelfilling.PostFilling;
import in.thefleet.thefuelfilling.PostedDBOpenHelper;
import in.thefleet.thefuelfilling.PostedDataSource;

public class SaveIntent extends IntentService  {

    private String saveFilter;
    private String postFilter;
    private String updateFilter;
    public static final String TAG = "SaveIntent";
    private Context context;

    public String fleetUrl = MainActivity.FLEETS_BASE_URL + MainActivity.imsiSIM;


    public SaveIntent(){
        super("save_filling_thread");
        this.context=context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"Service started",Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"Service stopped",Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this) {

            saveFilter = FleetsDBOpenHelper.FLEETS_CKM + " is not null" + " and "
                    + FleetsDBOpenHelper.FLEETS_ERRFLG + " is null";

            Cursor cursor = getContentResolver().query(FleetsDataSource.CONTENT_URI,
                    FleetsDBOpenHelper.ALL_COLUMNS, saveFilter, null, null);

            Log.d(TAG,"Currsor recovered "+ cursor.getCount()+" rows ");
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                String fleetRegno = cursor.getString(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_REGNO));
                String fleetOkm = cursor.getString(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_OKM));
                String fleetCkm = cursor.getString(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_CKM));
                String fleetQTY = cursor.getString(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_QTY));
                String fleetId = cursor.getString(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_ID));
                String sim = cursor.getString(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_SIM));
                int stationId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_SID)));
                double fuelPrice = cursor.getDouble(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_FPRICE));
                String fillDate = cursor.getString(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_CREATED));
                String invoice = cursor.getString(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_INV));
                updateFilter = FleetsDBOpenHelper.FLEETS_ID + "=" + fleetId;

                Log.d(TAG, "Cursor row results fleetid/regno/ckm :"+fleetId+"/"+fleetRegno+"/"+fleetCkm+"/"+stationId+"/"
                                                                                              +fuelPrice);
                String fillingJson = "{'IMEI_No':" + "'" +sim  + "'" + ","
                        + "'Filling_Fleet_Reg_No':" + "'" + fleetRegno + "'" + ","
                        + "'Filling_Opening_KM':" + "'" + fleetOkm + "'" + ","
                        + "'Filling_Closing_KM':" + "'" + fleetCkm + "'" + ","
                        + "'Filling_HSD_Qty':" + "'" + fleetQTY + "'" + ","
                        + "'Filling_Rate_Per_Litre':" + "'" + fuelPrice + "'" + ","
                        + "'Filling_Station_ID':" + "'" + stationId + "'" + ","
                        + "'Filling_Date':" + "'" + fillDate + "'" + ","
                        + "'Filling_Station_Invoice':" + "'" + invoice + "'"
                        + "}";
              //  Log.d(TAG,"Json string : fillingJson"+fillingJson);
              //  + "'Filling_Driver_Name':'dileep',"
              //  + "'Status':'1',"
             //           + "'Group_Name':'global',"
             //           + "'Created_Date':'2016-09-17'"
               // + ","

                String url = "http://thefleet.in/fleetmasterservice.svc/addfilling";
                PostFilling post = new PostFilling(this);

                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    String response = null;
                    try {
                        response = post.post(url, fillingJson);
                        Log.d("SaveIntent", "Response is: " + response + " for "+fleetRegno);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Exception " + e.getMessage());
                    }

                    if (response != null) {

                        if (response.substring(response.length() - 2).equals("2}")) {
                            //Set error flag in DB
                            ContentValues value = new ContentValues();
                            value.put(FleetsDBOpenHelper.FLEETS_ERRFLG, 1);
                            //Update db with error flag
                            getContentResolver().update(FleetsDataSource.CONTENT_URI, value, updateFilter, null);
                        } else if (response.substring(response.length() - 2).equals("1}")) {
                            ContentValues value = new ContentValues();
                            value.put(FleetsDBOpenHelper.FLEETS_ERRFLG, 1);
                            //Update db with error flag
                            getContentResolver().update(FleetsDataSource.CONTENT_URI, value, updateFilter, null);
                        } else if (response.substring(response.length() - 2).equals("0}")) {
                            ContentValues value = new ContentValues();
                            value.put(FleetsDBOpenHelper.FLEETS_OKM, fleetCkm);
                            value.put(FleetsDBOpenHelper.FLEETS_CKM, (byte[]) null);
                            value.put(FleetsDBOpenHelper.FLEETS_LQTY, fleetQTY);
                            value.put(FleetsDBOpenHelper.FLEETS_QTY, (byte[]) null);
                            value.put(FleetsDBOpenHelper.FLEETS_ERRFLG, (byte[]) null);
                            value.put(FleetsDBOpenHelper.FLEETS_CREATED, getTodaysDate().toString());

                            Cursor cursor_delpost = getContentResolver().query(PostedDataSource.CONTENT_URI3,
                                    PostedDBOpenHelper.ALL_COLUMNS, null, null, null);
                            if (cursor_delpost.getCount()>= 2) {
                                //Delete 1 row if the count is 2 or more for storing maximum 3 rows in db
                                postFilter = "rowid in (select min(rowid) from sfleets)";
                                getContentResolver().delete(
                                        PostedDataSource.CONTENT_URI3, postFilter, null);
                            }
                            cursor_delpost.close();
                            //Insert the filling int posted db
                            ContentValues pvalues = new ContentValues();
                            pvalues.put(PostedDBOpenHelper.SFLEETS_KEY, fleetId);
                            pvalues.put(PostedDBOpenHelper.SFLEETS_OKM, fleetOkm);
                            pvalues.put(PostedDBOpenHelper.SFLEETS_CKM, fleetCkm);
                            pvalues.put(PostedDBOpenHelper.SFLEETS_QTY, fleetQTY);
                            pvalues.put(PostedDBOpenHelper.SFLEETS_REGNO, fleetRegno);
                            pvalues.put(PostedDBOpenHelper.SFLEETS_SID, stationId);
                            getContentResolver().insert(PostedDataSource.CONTENT_URI3,pvalues);

                            //Update db with error flag
                            getContentResolver().update(FleetsDataSource.CONTENT_URI, value, updateFilter, null);

                        } else {
                             ContentValues value = new ContentValues();
                             value.put(FleetsDBOpenHelper.FLEETS_ERRFLG, 1);
                            //Update db with error flag
                             getContentResolver().update(FleetsDataSource.CONTENT_URI, value, updateFilter, null);
                        }
                    }else {
                        Log.d(TAG, "No response from server on post.");
                    }
                }
            }

            Cursor cursor_fillchk = getContentResolver().query(FleetsDataSource.CONTENT_URI,
                    FleetsDBOpenHelper.ALL_COLUMNS, saveFilter, null, null);
            Log.d(TAG,"after cursor excuted, count of local db afte execution: "+cursor_fillchk.getCount());
            if(cursor_fillchk.getCount()==0) {
                cancelAlarm2();
            }
        }
    }

    public void cancelAlarm2() {
        Intent intent2 = new Intent(getApplicationContext(), NetworkBroadcast.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, NetworkBroadcast.REQUEST_CODE,
                intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        if (pIntent != null) {
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pIntent);
        }
        Log.d(TAG,"Alarm cancelled");
    }

    public String getTodaysDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
}



