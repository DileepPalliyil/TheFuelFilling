package in.thefleet.thefuelfilling;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import in.thefleet.thefuelfilling.model.Fleet;
import in.thefleet.thefuelfilling.model.Station;
import in.thefleet.thefuelfilling.online.isOnline;
import in.thefleet.thefuelfilling.parsers.FleetJSONParser;
import in.thefleet.thefuelfilling.parsers.StationJSONParser;
import in.thefleet.thefuelfilling.phne.TelephonyInfo;
import in.thefleet.thefuelfilling.service.NetworkBroadcast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

   // private static final int SAVED_REQUEST_CODE = 1001;

    private static final int INTERVAL = 1000 * 60;
    private static final int DELAY = 5000;
    private static final int LOADER1 = 1;


    private CursorAdapter cursorAdapter;
    List<Fleet> fleetList;
    List<Station> stationList;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    public static final String TAG = "MainActivity";
    public static final String FLEETS_BASE_URL =
            "http://thefleet.in/fleetmasterservice.svc/getFleetDetails/";
    public static final String STATIONS_BASE_URL =
            "http://thefleet.in/Fleetmasterservice.svc/getStation/";
   // private  int nearest_station_key = 0;

    //Distance of station  less than 1000 meters will be captured in onResume
   // private  double station_distance = 1000;

    public static String imsiSIM;
  //  String imsiSIM;
    public String fleetUrl = null;
    public String stationUrl = null;
    ProgressBar pb;
    private EditText ckmText;
    private EditText okmText;
    private EditText qtyText;
    private TextView regNoText;
    private String saveFilter;
    private String delFilter;
    private String dupFilter;
    private String disFilter;
    private String priceFilter;
    Spinner spn;
    int getFleetResult = 1;
    private Toast t_saved;
    private Toast t_nwnot;
    private Toast t_nwnot2;
    private Toast t_volleyf;
    private Toast t_volleys;

    private RadioGroup radioFuelGroup;
    private RadioButton radioRprice;
    private RadioButton radioPprice;

    private BroadcastReceiver broadcastReceiver;

    FleetsDataSource fleetsDataSource;
    PopupWindow popupWindowFleets;
    Button buttonFleetsDropDown;
    Button saveFillingBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        spn = (Spinner) findViewById(R.id.spinStation);

        radioFuelGroup = (RadioGroup)findViewById(R.id.radiogfprice);
        radioPprice = (RadioButton)findViewById(R.id.radioPremium);
        radioRprice = (RadioButton)findViewById(R.id.radioReular);
        radioRprice.setTextColor(Color.parseColor("#fe9c02"));
        radioPprice.setTextColor(Color.BLACK);
        Globals g = Globals.getInstance();
        g.setFpSelected(1); //Set to regular price

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       Snackbar.make(view, "Refreshing Data", Snackbar.LENGTH_LONG)
                                               .setAction("Action", null).show();
                                       clearEditText();
                                     //  getPhoneSims();
                                      // requestReadPhoneStatePermission();
                                       doPermissionGrantedTasks();

                                   }
                               });

        View.OnClickListener handler = new View.OnClickListener() {
            public void onClick(View v) {

                switch (v.getId()) {

                    case R.id.buttonFleetDropDown:
                        clearEditText();
                        findViewById(R.id.saveButton).setEnabled(true);
                        findViewById(R.id.saveButton).setBackgroundResource(R.drawable.button_border);
                        findViewById(R.id.textOKM).setEnabled(true);
                        findViewById(R.id.textCKM).setEnabled(true);
                        findViewById(R.id.textQty).setEnabled(true);
                        findViewById(R.id.spinStation).setEnabled(true);
                        restartLoader();

                        Cursor cursor = getContentResolver().query(FleetsDataSource.CONTENT_URI,
                                FleetsDBOpenHelper.ALL_COLUMNS,null,null,null);
                        int cnt = cursor.getCount();
                        cursor.close();
                        if (cnt > 0) {
                            popupWindowFleets = popupWindowFleets();
                            popupWindowFleets.showAsDropDown(v, -5, 0);
                        }else {
                            ErrorDialog alertcnt = new ErrorDialog();
                            alertcnt.showDialog(MainActivity.this, "No network and no data saved locally");
                        }

                        // show the list view as dropdown
                        /*if (isOnline.isNetworkConnected(MainActivity.this)) {
                            if (getFleetResult == 1) {
                                popupWindowFleets.showAsDropDown(v, -5, 0);
                            } else if (getFleetResult == 0) {
                                ErrorDialog alert0 = new ErrorDialog();
                                alert0.showDialog(MainActivity.this, "No fleet details from server.Try refreshing.");
                            }
                        }else {
                            //Check data exists
                            Cursor cursor = getContentResolver().query(FleetsDataSource.CONTENT_URI,
                                    FleetsDBOpenHelper.ALL_COLUMNS,null,null,null);
                            int cnt = cursor.getCount();

                            if (cnt > 0) {
                                popupWindowFleets = popupWindowFleets();
                                popupWindowFleets.showAsDropDown(v, -5, 0);
                            }else {
                                ErrorDialog alertcnt = new ErrorDialog();
                                alertcnt.showDialog(MainActivity.this, "No network and no data saved locally");
                            }
                        }*/

                          break;
                    case R.id.saveButton:
                        saveFilling();
                        break;
                }
            }


                private void saveFilling() {

                Globals g = Globals.getInstance();

                okmText = (EditText) findViewById(R.id.textOKM);
                ckmText = (EditText) findViewById(R.id.textCKM);
                qtyText = (EditText) findViewById(R.id.textQty);
                regNoText = (TextView) findViewById(R.id.textregNo);


                //Check for null values and alert
                if ((ckmText.length() !=0) && (qtyText.length() !=0) ) {
                    int ckm = Integer.parseInt(ckmText.getText().toString().trim());
                    Integer ckmOkm = Integer.valueOf(ckmText.getText().toString())
                            - Integer.valueOf(okmText.getText().toString());

                    String sid =null;
                    if (spn.getCount()!=0) {
                       sid = spn.getSelectedItem().toString().substring(0,
                                spn.getSelectedItem().toString().indexOf('-'));
                    }

                    double avg = g.getavgSelected();

                  //  double recavg = ckmOkm / Integer.valueOf(qtyText.getText().toString());
                    double recavg = ckmOkm / g.getlqtySelected();
                    double upperavg = avg + (avg * .7);
                    double loweravg = avg - (avg * .7);

                    if ((ckmOkm > 0) && (recavg > loweravg && recavg < upperavg)) {
                        //Check the value already exist in db
                        String ckmChk = FleetsDBOpenHelper.FLEETS_CKM + " = " + ckm + " and "
                                + FleetsDBOpenHelper.FLEETS_REGNO + " = " + regNoText;
                        Cursor cursor_ckmChk = getContentResolver().query(FleetsDataSource.CONTENT_URI,
                                FleetsDBOpenHelper.ALL_COLUMNS, ckmChk, null, null);
                        if (cursor_ckmChk.getCount() == 0){

                         if (sid !=null) {
                             ContentValues values = new ContentValues();
                             values.put(FleetsDBOpenHelper.FLEETS_CKM, ckm);
                             values.put(FleetsDBOpenHelper.FLEETS_QTY, Integer.valueOf(qtyText.getText().toString().trim()));
                             values.put(FleetsDBOpenHelper.FLEETS_SID, Integer.valueOf(sid.trim()));
                             values.put(FleetsDBOpenHelper.FLEETS_CREATED,"current_timestamp");

                            priceFilter = StationDBOpenHelper.STATION_KEY + "=" + Integer.valueOf(sid.trim());

                            Cursor cursor_fpchk = getContentResolver().query(StationsDataSource.CONTENT_URI2,
                                     StationDBOpenHelper.ALL_COLUMNS, priceFilter, null, null);

                            if( cursor_fpchk != null && cursor_fpchk.moveToFirst() ) {
                                Log.d(TAG,"Fp selected  :"+g.getFpSelected());
                                if (g.getFpSelected() == 1) {
                                    values.put(FleetsDBOpenHelper.FLEETS_FPRICE, cursor_fpchk.getDouble
                                            (cursor_fpchk.getColumnIndex(StationDBOpenHelper.STATION_RPRICE)));
                                } else if (g.getFpSelected() == 2) {
                                    values.put(FleetsDBOpenHelper.FLEETS_FPRICE, cursor_fpchk.getDouble
                                            (cursor_fpchk.getColumnIndex(StationDBOpenHelper.STATION_PPRICE)));
                                }
                            }
                             cursor_fpchk.close();

                             //Update db with filling details
                             getContentResolver().update(FleetsDataSource.CONTENT_URI, values, g.getfleetSelected(), null);

                             t_saved = Toast.makeText(MainActivity.this, "Filling Saved Locally.. ", Toast.LENGTH_LONG);
                             t_saved.show();
                             clearEditText();

                             saveFilter = FleetsDBOpenHelper.FLEETS_CKM + " is not null" + " and "
                                     + FleetsDBOpenHelper.FLEETS_ERRFLG + " is null";
                             Cursor cursor = getContentResolver().query(FleetsDataSource.CONTENT_URI,
                                     FleetsDBOpenHelper.ALL_COLUMNS, saveFilter, null, null);
                             cursor.close();
                             if (cursor.getCount() > 0) {
                                 if (!isOnline.isNetworkConnected(MainActivity.this)) {
                                     t_nwnot=Toast.makeText(MainActivity.this, "Network isn't available..", Toast.LENGTH_LONG);
                                     t_nwnot.cancel();
                                     //Schedule alarm if there is a row to save
                                     scheduleAlarm();
                                 } else {
                                     //If network available, cancel the service if it is working, save it ins server
                                     cancelAlarm();
                                     // saveFillingToServer();
                                     scheduleAlarm();
                                 }
                             }
                         }else {
                             ErrorDialog alertd = new ErrorDialog();
                             alertd.showDialog(MainActivity.this, "Error in station selection.Please try again.");
                         }
                     }else {
                            ErrorDialog alertd = new ErrorDialog();
                            alertd.showDialog(MainActivity.this, "Already saved..");
                        }

                    } else {
                        ErrorDialog alert1 = new ErrorDialog();
                        alert1.showDialog(MainActivity.this, "Incorrect values for CKM or Qty");
                    }

                } else {
                    //Null column condition check
                    ErrorDialog alertn = new ErrorDialog();
                    alertn.showDialog(MainActivity.this, "Enter all required values");
                }
            }

        };

       // Dropdown button
        buttonFleetsDropDown = (Button) findViewById(R.id.buttonFleetDropDown);
        buttonFleetsDropDown.setOnClickListener(handler);

        saveFillingBtn = (Button) findViewById(R.id.saveButton);
        saveFillingBtn.setOnClickListener(handler);

        // Identify phone sims and get data from JSON URL
        getPhoneSims();
    }

    private void clearEditText() {
        EditText okmc = (EditText) findViewById(R.id.textOKM);
        okmc.setText("");
        EditText ckmc = (EditText) findViewById(R.id.textCKM);
        ckmc.setText("");
        EditText qkmc = (EditText) findViewById(R.id.textQty);
        qkmc.setText("");
        Spinner spin = (Spinner) findViewById(R.id.spinStation);
        spin.setAdapter(null);
        Button fleetBtn = (Button) findViewById(R.id.buttonFleetDropDown);
        fleetBtn.setText("Select your fleet...");
        TextView fpriceview = (TextView) findViewById(R.id.textFprice);
        fpriceview.setText("Fuel Price");
    }


    @Override
    protected void onResume() {
        super.onResume();

        spn = (Spinner) findViewById(R.id.spinStation);
        Intent sti = new Intent(getApplicationContext().getApplicationContext(),StationLocService.class);
        startService(sti);

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

               Log.d(TAG, "The longitude is :" + intent.getExtras().get("longitude") + " Latitude is :" + intent.getExtras().get("latitude"));
               Cursor cursor_stations = getContentResolver().query(StationsDataSource.CONTENT_URI2,
                      StationDBOpenHelper.ALL_COLUMNS,null,null,null);

                 for (cursor_stations.moveToFirst(); !cursor_stations.isAfterLast(); cursor_stations.moveToNext()) {

                    double distance = geoCoordToMeter((Double) intent.getExtras().get("latitude"),
                           (Double) intent.getExtras().get("longitude"),
                            cursor_stations.getDouble(cursor_stations.getColumnIndex(StationDBOpenHelper.STATION_LAT)),
                            cursor_stations.getDouble(cursor_stations.getColumnIndex(StationDBOpenHelper.STATION_LON)));

                    String distance_filter = StationDBOpenHelper.STATION_KEY + "="
                         + cursor_stations.getInt(cursor_stations.getColumnIndex(StationDBOpenHelper.STATION_KEY));

                    ContentValues valuesd = new ContentValues();
                    valuesd.put(StationDBOpenHelper.STATION_DIS,round(distance,2));
                        //Update db with distance details

                    getContentResolver().update(StationsDataSource.CONTENT_URI2, valuesd,
                                distance_filter, null);
                    }
                    cursor_stations.close();
                    //Load station to spinner once after inserting distance to db

                    }
            };

        }
            registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
        }


    private double geoCoordToMeter(double latA, double lonA, double latB, double lonB) {
        double earthRadius = 6378.137d; // km
        double dLat = (latB - latA) * Math.PI / 180d;
        double dLon = (lonB - lonA) * Math.PI / 180d;
        double a = Math.sin(dLat / 2d) * Math.sin(dLat / 2d)
                + Math.cos(latA * Math.PI / 180d)
                * Math.cos(latB * Math.PI / 180d)
                * Math.sin(dLon / 2d) * Math.sin(dLon / 2);
        double c = 2d * Math.atan2(Math.sqrt(a), Math.sqrt(1d - a));
        double d = earthRadius * c;
        return round((d * 1000d)/1000,2);
    }

    private PopupWindow popupWindowFleets() {

        cursorAdapter = new FleetAdapter(this,null,0);

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        ListView listViewFleets = new ListView(this);
        listViewFleets.setAdapter(cursorAdapter);
        getLoaderManager().initLoader(LOADER1, null, this);


            // set the item click listener
            listViewFleets.setOnItemClickListener(new FleetsDropdownOnItemClick());
            // some other visual settings
            popupWindow.setFocusable(true);
            popupWindow.setWidth(buttonFleetsDropDown.getWidth());
            popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            popupWindow.setFocusable(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.setElevation(10);
            }
            // set the list view as pop up window content
            popupWindow.setContentView(listViewFleets);
            return popupWindow;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, FleetPreferenceActivity.class);
                startActivity(i);
                return true;
            default: return super.onOptionsItemSelected(item);

            case R.id.show_posted:
                Intent intent = new Intent(MainActivity.this,PostedFleets.class);
                Uri uri = Uri.parse(PostedDataSource.CONTENT_URI3 + "/" + id);
                intent.putExtra(PostedDataSource.CONTENT_ITEM_TYPE,uri);
               // startActivityForResult(intent,SAVED_REQUEST_CODE);
                startActivity(intent);
                return true;

            case R.id.clear_localdb:

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setMessage("All unsaved fillings will be deleted! Delete or Stop?")
                        .setCancelable(false)
                        .setPositiveButton("Stop", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Delete alrm if running and delete all from local db
                                cancelAlarm();
                                getContentResolver().delete(
                                        FleetsDataSource.CONTENT_URI, null, null);
                                getContentResolver().delete(
                                        StationsDataSource.CONTENT_URI2, null, null);
                                restartLoader();
                                getFleetResult = 0;
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();


                return true;
        }
    }

    public void getPhoneSims() {
        if (Build.VERSION.SDK_INT>=23) {
            Toast.makeText(MainActivity.this, "In getPhoneSims call", Toast.LENGTH_LONG).show();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                requestReadPhoneStatePermission();
            } else {
                Log.i(TAG,
                        "READ_PHONE_STATE permission is already been granted.");
              //  doPermissionGrantedTasks();
            }
        }else {
            doPermissionGrantedTasks();
        }
        //End of checking phone sims
    }


    private void requestReadPhoneStatePermission() {
        // READ_PHONE_STATE permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Toast.makeText(MainActivity.this, "In onRequestPermissionResult", Toast.LENGTH_LONG).show();
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            //If permision granted start calling URL and get data

            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
             //   doPermissionGrantedTasks();
            } else {

                alertAlert(getString(R.string.permissions_not_granted_read_phone_state));
            }
        }
    }

    //If permission denayed, show message and close the app
    private void alertAlert(String msg) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                })
                .setIcon(R.drawable.ic_alert_black_18dp)
                .show();
    }


    private void doPermissionGrantedTasks() {
        Toast.makeText(this, "In doPermission Granted Tasks", Toast.LENGTH_LONG).show();

        if (isOnline.isNetworkConnected(this)) {

            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
            boolean isSIMReady = telephonyInfo.isSIM1Ready();
            //Commented to test in avd. Uncomment for production
          //  if (isSIMReady) {
                SharedPreferences sv = PreferenceManager.getDefaultSharedPreferences(this);
                String simValue = sv.getString("simValue", "1");
                String fleetTypValue = sv.getString("fleetTypValue", "all");
                Toast.makeText(this, "Sim value:" + simValue, Toast.LENGTH_LONG).show();
                if (simValue.equals("1")) {
                    imsiSIM = telephonyInfo.getImsiSIM1();
                    Globals g = Globals.getInstance();
                    g.setImei(imsiSIM);
                    fleetUrl = MainActivity.FLEETS_BASE_URL + imsiSIM;
                    stationUrl = MainActivity.STATIONS_BASE_URL + imsiSIM;
                    requestData(fleetUrl);
                    requestStations(stationUrl);
                } else if (simValue.equals("2")) {
                    imsiSIM = telephonyInfo.getImsiSIM2();
                    Globals g = Globals.getInstance();
                    g.setImei(imsiSIM);
                    fleetUrl = MainActivity.FLEETS_BASE_URL + imsiSIM;
                    stationUrl = MainActivity.STATIONS_BASE_URL + imsiSIM;
                    requestData(fleetUrl);
                    requestStations(stationUrl);
                } else {
                    imsiSIM = "357327070825555";
                    Globals g = Globals.getInstance();
                    g.setImei(imsiSIM);
                    fleetUrl = MainActivity.FLEETS_BASE_URL + "357327070825555";
                    stationUrl = MainActivity.STATIONS_BASE_URL + "357327070825555";
                    requestData(fleetUrl);
                    requestStations(stationUrl);
                }
             /*   else if {
                    Toast.makeText(this, "Sim is not ready or no access.Try with different sim", Toast.LENGTH_LONG).show();
                }*/
            } else {
                t_nwnot2 =Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG);
                t_nwnot2.show();
            }
        }

    //Volley request
    private void requestData(String furi) {
        Log.d(TAG,"Fuel url :"+furi);
        StringRequest request1 = new StringRequest(furi,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (response.length()>0) {
                            fleetList = FleetJSONParser.parseFeed(response);
                            insertFleetDataToDb();
                            // initialize pop up window
                            popupWindowFleets = popupWindowFleets();
                            pb.setVisibility(View.INVISIBLE);
                            buttonFleetsDropDown.setEnabled(true);
                            getFleetResult = 1;
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        t_volleyf=Toast.makeText(MainActivity.this,"Volley Fleets:"+error.getMessage(),Toast.LENGTH_LONG);
                        t_volleyf.show();
                        pb.setVisibility(View.INVISIBLE);
                        buttonFleetsDropDown.setEnabled(true);
                        getFleetResult = 0;
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        int socketTimeout = 60000;// seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
        request1.setRetryPolicy(policy);

        queue.add(request1);
        pb.setVisibility(View.VISIBLE);
        buttonFleetsDropDown.setEnabled(false);
    }


    //Volley request stations
    private void requestStations(String suri) {
        Log.d(TAG,"station url :"+suri);
        StringRequest request2 = new StringRequest(suri,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        stationList = StationJSONParser.parseFeed(response);
                        insertStationDataToDb();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                     t_volleys=Toast.makeText(MainActivity.this, "Volley Station:" + error.getMessage(), Toast.LENGTH_LONG);
                     t_volleys.cancel();

                    }
                });
        RequestQueue queue2 = Volley.newRequestQueue(this);
        int socketTimeout = 60000;// seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
        request2.setRetryPolicy(policy);
        queue2.add(request2);
    }


    private void insertFleetDataToDb() {
        delFilter = FleetsDBOpenHelper.FLEETS_CKM + " is null";
        dupFilter = "rowid not in (select min(rowid) from fleets group by "
                                  +FleetsDBOpenHelper.FLEETS_REGNO +")";

            getContentResolver().delete(
                    FleetsDataSource.CONTENT_URI, delFilter, null);
            for (Fleet fleet : fleetList) {

                insertFleet( fleet.getFleet_ID(),fleet.getRegNo(),
                        fleet.getTypName(),fleet.getModelName(),fleet.getOpening_KM(),fleet.getFleet_Avg_Milage(),
                        fleet.getLastQty(),fleet.getFuel_Type(),getTodaysDate().toString());
            }

            getContentResolver().delete(
                    FleetsDataSource.CONTENT_URI, dupFilter, null);
    }

    private void insertFleet(int fleetsKey,String fleetsRegno,String fleetsType,
                 String fleetsModel,int fleetsOKM,double fleetsAVG,double fleetsLQTY,String fleetsFtype,String today) {
        Globals g = Globals.getInstance();

        ContentValues fvalues = new ContentValues();
        fvalues.put(FleetsDBOpenHelper.FLEETS_KEY, fleetsKey);
        fvalues.put(FleetsDBOpenHelper.FLEETS_REGNO, fleetsRegno);
        fvalues.put(FleetsDBOpenHelper.FLEETS_TYPE, fleetsType);
        fvalues.put(FleetsDBOpenHelper.FLEETS_MODEL, fleetsModel);
        fvalues.put(FleetsDBOpenHelper.FLEETS_OKM, fleetsOKM);
        fvalues.put(FleetsDBOpenHelper.FLEETS_AVG, fleetsAVG);
        fvalues.put(FleetsDBOpenHelper.FLEETS_LQTY, fleetsLQTY);
        fvalues.put(FleetsDBOpenHelper.FLEETS_FTYPE, fleetsFtype);
        fvalues.put(FleetsDBOpenHelper.FLEETS_SIM, g.getImei());
        fvalues.put(FleetsDBOpenHelper.FLEETS_CREATED, today);

        getContentResolver().insert(fleetsDataSource.CONTENT_URI,fvalues);
    }


    private void insertStationDataToDb() {

        if (stationList != null) {
           // Log.d(TAG,"Deleting stations");
            getContentResolver().delete(
                    StationsDataSource.CONTENT_URI2, null, null) ;
            for (Station station : stationList) {
                insertStation( station.getFleet_ID(),station.getStation_ID(),
                        station.getStation_Name(),station.getLatitude(),station.getLongitude(),station.getLocation_Name(),
                        station.getState_Name(),station.getRegularPrice(),station.getPremiumPrice(),
                        getTodaysDate().toString());
              //  Log.d(TAG,"Inserting stations "+ station.getStation_Name()+"-"+station.getLatitude()+"-"+station.getLongitude());
            }
        }
    }

    private void insertStation(int fleetsKey,int stationKey,String stationName,
                             double latitude,double longitude,String location,String state,double regular,
                               double premium,String today) {
        ContentValues svalues = new ContentValues();
        svalues.put(StationDBOpenHelper.STATION_FKEY, fleetsKey);
        svalues.put(StationDBOpenHelper.STATION_KEY, stationKey);
        svalues.put(StationDBOpenHelper.STATION_NAME, stationName);
        svalues.put(StationDBOpenHelper.STATION_LAT, latitude);
        svalues.put(StationDBOpenHelper.STATION_LON, longitude);
        svalues.put(StationDBOpenHelper.STATION_LOCN, location);
        svalues.put(StationDBOpenHelper.STATION_STATE, state);
        svalues.put(StationDBOpenHelper.STATION_RPRICE, regular);
        svalues.put(StationDBOpenHelper.STATION_PPRICE, premium);
        svalues.put(StationDBOpenHelper.STATION_CREATED, today);
        getContentResolver().insert(StationsDataSource.CONTENT_URI2,svalues);
    }


    public String getTodaysDate() {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar cal = Calendar.getInstance();
            return dateFormat.format(cal.getTime());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

         //   Log.d(TAG, "In onCreateLoader");
            return new CursorLoader(this, fleetsDataSource.CONTENT_URI,
                    null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case LOADER1:
                if (data != null && data.getCount() > 0) {
                 //   Log.d(TAG,"Before swap cursor");
                   if (this.cursorAdapter != null) {
                      this.cursorAdapter.swapCursor(data);
                    }
                }
                break;
        }
      }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
      //  Log.d(TAG,"In onLoaderReset");
        if (this.cursorAdapter != null) {
            this.cursorAdapter.swapCursor(null);
        }
    }

    private void restartLoader() {
      //  Log.d(TAG,"In restartLoader");
        getLoaderManager().restartLoader(LOADER1,null,this);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  Log.d(TAG,"In onActivityRequest");
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
    }*/
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // Setup a recurring alarm
    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), NetworkBroadcast.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, NetworkBroadcast.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, DELAY,
                INTERVAL, pIntent);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), NetworkBroadcast.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, NetworkBroadcast.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (pIntent != null) {
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pIntent);
        }
    }

    @Override
    protected void onDestroy() {
        if( cursorAdapter != null ) {
            cursorAdapter.changeCursor( null );
            cursorAdapter = null;
        }
        if (broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
        if  (t_saved !=null) {
            t_saved.cancel();
        }
        if  (t_nwnot2 !=null) {
            t_nwnot2.cancel();
        }
        if  (t_nwnot !=null) {
            t_nwnot.cancel();
        }
        if  (t_volleyf !=null) {
            t_volleyf.cancel();
        }
        if  (t_volleys !=null) {
            t_volleys.cancel();
        }

        Intent spd = new Intent(getApplicationContext().getApplicationContext(),StationLocService.class);
        stopService(spd);

        super.onDestroy();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void SelectFuelPrice(View view)
    {
        boolean checked = ((RadioButton) view).isChecked();
        Globals g = Globals.getInstance();
        g.setFpSelected(1);
        switch (view.getId())
        {
            case R.id.radioReular:
                if (checked){
                    radioRprice.setTextColor(Color.parseColor("#fe9c02"));
                    radioPprice.setTextColor(Color.BLACK);
                    g.setFpSelected(1);
                    Log.d(TAG,"First");
                }
                break;

            case R.id.radioPremium:
                if (checked){
                    radioPprice.setTextColor(Color.parseColor("#fe9c02"));
                    radioRprice.setTextColor(Color.BLACK);
                    Log.d(TAG,"Second");
                    g.setFpSelected(2);
                }
                break;
        }
    }
}

   