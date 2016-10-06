package in.thefleet.thefuelfilling.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.thefleet.thefuelfilling.model.Station;

public class StationJSONParser {
    public static List<Station> parseFeed(String content) {
        //Proceed only if url retrieves data for not getting null pointer exception
        if (content != null) {

            try {
                JSONObject jobj = new JSONObject(content);

                List<Station> stationList = new ArrayList<>();
                JSONArray ar = jobj.optJSONArray("getStationResult");
                for (int i = 0; i < ar.length(); i++) {

                    JSONObject obj = ar.getJSONObject(i);
                    Station station = new Station();

                    station.setStation_ID(obj.getInt("Station_ID"));
                    station.setFleet_ID(obj.getInt("Fleet_ID"));
                    station.setLatitude(obj.getDouble("Latitude"));
                    station.setLongitude(obj.getDouble("Longitude"));
                    station.setLocation_Name(obj.getString("Location_Name"));
                    station.setState_Name(obj.getString("State_Name"));
                    station.setStation_Name(obj.getString("Station_Name"));
                    station.setRegularPrice(obj.getDouble("RegularPrice"));
                    station.setPremiumPrice(obj.getDouble("PremiumPrice"));

                    stationList.add(station);

                }

                return stationList;

            }catch(JSONException e){

                Log.d("StationJSONParser", "In parser exception", e);
                return null;
            }

        } else {
            Log.d("StationJSONParser", "Null String return");
            return null;
        }

    }

}