package in.thefleet.thefuelfilling.parsers;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.thefleet.thefuelfilling.model.Fleet;

public class FleetJSONParser {
    public static List<Fleet> parseFeed(String content) {
        //Proceed only if url retrieves data for not getting null pointer exception
        if (content != null) {

            try {
                JSONObject jobj = new JSONObject(content);

                List<Fleet> fleetList = new ArrayList<>();
                JSONArray ar = jobj.optJSONArray("getFleetDetailsResult");
                for (int i = 0; i < ar.length(); i++) {

                    JSONObject obj = ar.getJSONObject(i);
                    Fleet fleet = new Fleet();

                    fleet.setFleet_ID(obj.getInt("Fleet_ID"));
                    fleet.setRegNo(obj.getString("Fleet_Reg_No"));
                    fleet.setFuel_Type(obj.getString("Fuel_Type"));
                    fleet.setMakeName(obj.getString("Make_Name"));
                    fleet.setModelName(obj.getString("Model_Name"));
                    fleet.setFleet_Avg_Milage(obj.getDouble("Fleet_Avg_Milage"));
                    fleet.setTypName(obj.getString("Type_Name"));
                    fleet.setOpening_KM(obj.getInt("Opening_KM"));
                    fleet.setLastQty(obj.getInt("LastQty"));

                    fleetList.add(fleet);

                }

                return fleetList;

            }catch(JSONException e){

                Log.d("FleetJSONParser", "In parser exception", e);
                return null;
            }

        } else {
            Log.d("FleetJSONParser", "Null String return");
            return null;
        }

    }

}
