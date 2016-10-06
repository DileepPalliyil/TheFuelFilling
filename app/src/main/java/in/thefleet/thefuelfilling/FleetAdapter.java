package in.thefleet.thefuelfilling;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class FleetAdapter extends CursorAdapter {

    public FleetAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.item_fleet,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (cursor.getCount() > 0) {

            String regNo = cursor.getString(
                    cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_REGNO));
            // Log.d("Fleets Adapter",regNo);
            TextView tv = (TextView) view.findViewById(R.id.textregNo);
            tv.setText(regNo);

            String fleetTyp = cursor.getString(
                    cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_TYPE));

            TextView tv3 = (TextView) view.findViewById(R.id.textFleetTyp);

            int fleetOKM = cursor.getInt(
                    cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_OKM));
            int fleetLQTY = cursor.getInt(
                    cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_LQTY));
            String fuelprice = cursor.getString(
                    cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_FTYPE));

            tv3.setText(fleetTyp +","+ "OKM :"+ fleetOKM +","+"LastQty: "+fleetLQTY);
            int fleetErr = cursor.getInt(cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_ERRFLG));

            if (fleetErr == 1) {
                tv.setTextColor(Color.RED);
            } else {
                tv.setTextColor(Color.BLACK);
            }
        }
    }
}

