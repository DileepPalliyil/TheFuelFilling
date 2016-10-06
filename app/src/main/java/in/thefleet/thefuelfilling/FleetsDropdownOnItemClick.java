package in.thefleet.thefuelfilling;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FleetsDropdownOnItemClick implements android.widget.AdapterView.OnItemClickListener {

    Spinner spn;
    String TAG = "FleetssDropdownOnItemClick.java";
    private RadioGroup radioFuelGroup;
    String sid;
    double regularFprice;
    double premiumFprice;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // get the context and main activity to access variables
        final Context mContext = view.getContext();
        final MainActivity mainActivity = ((MainActivity) mContext);

        //Query the selected fleet to get other details
        Uri uri = Uri.parse(FleetsDataSource.CONTENT_URI + "/" + id);

        String fleetFilter = FleetsDBOpenHelper.FLEETS_ID + "=" + uri.getLastPathSegment();
        Log.d(TAG, "Fleet filter is: " + fleetFilter);

        //Store the filtered fleet for future use
        Globals g = Globals.getInstance();
        g.setfleetSelected(fleetFilter);

        spn = (Spinner) ((Activity) mContext).findViewById(R.id.spinStation);

        radioFuelGroup = (RadioGroup) mainActivity.findViewById(R.id.radiogfprice);
        radioFuelGroup.check(R.id.radioReular);

        //Query
        final Cursor cursor = mContext.getContentResolver().query(uri,
                FleetsDBOpenHelper.ALL_COLUMNS, fleetFilter, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            int fid = cursor.getInt(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_KEY));
            String okm = cursor.getString(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_OKM));
            double avg = cursor.getDouble(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_AVG));
            String ckm = cursor.getString(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_CKM));
            String qty = cursor.getString(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_QTY));
            double lstqty = cursor.getDouble(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_LQTY));
            String ftype = cursor.getString(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_FTYPE));
            //int sid = cursor.getInt(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_SID));
           // Log.d(TAG,"Station id is :"+sid);
            g.setavgSelected(avg);
            g.setlqtySelected(lstqty);
            g.setFidSelected(fid);

            TextView tv4 = (TextView) mainActivity.findViewById(R.id.textOKM);
            tv4.setText(okm);
            TextView tv5 = (TextView) mainActivity.findViewById(R.id.textCKM);
            tv5.setText(ckm);
            TextView tv6 = (TextView) mainActivity.findViewById(R.id.textQty);
            tv6.setText(qty);

            //Disable entry and button if previous filling is not saved
            if (tv5.length() != 0) {
                Button btnSave = (Button) mainActivity.findViewById(R.id.saveButton);
                btnSave.setBackgroundColor(Color.GRAY);
                btnSave.setEnabled(false);
                tv4.setEnabled(false);
                tv5.setEnabled(false);
                tv6.setEnabled(false);
                spn.setEnabled(false);
            }

            // add some animation when a list item was clicked
            Animation fadeInAnimation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_in);
            fadeInAnimation.setDuration(10);
            view.startAnimation(fadeInAnimation);

            // dismiss the pop up
            mainActivity.popupWindowFleets.dismiss();

            EditText et1 = (EditText) mainActivity.findViewById(R.id.textCKM);
            et1.requestFocus();

            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et1, 0);

            String selectedItemText = ((TextView) view.findViewById(R.id.textregNo)).getText().toString()+
                    "-"+ftype;
                 //   + "-" + ((TextView) view.findViewById(R.id.textFleetTyp)).getText().toString();
            mainActivity.buttonFleetsDropDown.setText(selectedItemText);

            Toast.makeText(mContext, "Fleet AVG is: " + avg, Toast.LENGTH_SHORT).show();


            String stationFilter = StationDBOpenHelper.STATION_FKEY + "=" + g.getFidSelected();
            Cursor cursor_stndd = mContext.getContentResolver().query(StationsDataSource.CONTENT_URI2,
                    StationDBOpenHelper.ALL_COLUMNS, stationFilter, null,
                    null);
            ArrayList<String> al = new ArrayList<String>();
            for (cursor_stndd.moveToFirst(); !cursor_stndd.isAfterLast(); cursor_stndd.moveToNext()) {
                String name = cursor_stndd.getString(cursor_stndd.getColumnIndex(StationDBOpenHelper.STATION_KEY)) + "-" +
                        cursor_stndd.getString(cursor_stndd.getColumnIndex(StationDBOpenHelper.STATION_NAME)) + "-" +
                        cursor_stndd.getString(cursor_stndd.getColumnIndex(StationDBOpenHelper.STATION_LOCN)) + "-" +
                        cursor_stndd.getString(cursor_stndd.getColumnIndex(StationDBOpenHelper.STATION_DIS));

                al.add(name);

                // Log.d(TAG,"Station name in add spinn :"+name );
            }

            ArrayAdapter<String> aa1 = new ArrayAdapter<String>(
                    mContext, R.layout.station_spinner, R.id.textSpin,
                    al);
            spn.setAdapter(aa1);

            cursor_stndd.close();

            spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    sid = spn.getSelectedItem().toString().substring(0,
                            spn.getSelectedItem().toString().indexOf('-'));
                    Globals g = Globals.getInstance();
                    g.setSidSelected(Integer.valueOf(sid.trim()));
                    Log.d(TAG,"Selected station id2 :"+g.getSidSelected());

                    if (selectedItem != parent.getItemAtPosition(0).toString()) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Station selected is not nearest!Still want to continue or reset to nearest?")
                                .setCancelable(false)
                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("Reset", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        spn.setSelection(0);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                    //Display fuel prices for selected stations
                    String priceFilter = StationDBOpenHelper.STATION_KEY + "=" + Integer.valueOf(sid.trim());
                    Cursor cursor_fpice = mContext.getContentResolver().query(StationsDataSource.CONTENT_URI2,
                            StationDBOpenHelper.ALL_COLUMNS, priceFilter, null,
                            null);
                    if (cursor_fpice != null) {
                        cursor_fpice.moveToFirst();
                        regularFprice = cursor_fpice.getDouble(cursor_fpice.getColumnIndex(StationDBOpenHelper.STATION_RPRICE));
                        premiumFprice = cursor_fpice.getDouble(cursor_fpice.getColumnIndex(StationDBOpenHelper.STATION_PPRICE));
                        cursor_fpice.close();

                        TextView textFprice = (TextView) mainActivity.findViewById(R.id.textFprice);
                        textFprice.setVisibility(view.VISIBLE);
                        textFprice.setText("Regular :"+regularFprice+ "/"+"Premium :"+premiumFprice);
                    }else {
                        TextView textFprice = (TextView) mainActivity.findViewById(R.id.textFprice);
                        textFprice.setVisibility(view.GONE);
                    }
                     Log.d(TAG,"Selected station fuel prices are regular :"+regularFprice+" Premium :"+premiumFprice);


                } // to close the onItemSelected

                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }


    }

    }


