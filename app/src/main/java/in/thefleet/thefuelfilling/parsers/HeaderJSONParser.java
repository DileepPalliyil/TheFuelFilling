package in.thefleet.thefuelfilling.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.thefleet.thefuelfilling.model.Header;

/**
 * Created by DILEEP on 17-10-2016.
 */
public class HeaderJSONParser {

    public static List<Header> parseFeed(String content) {
        //Proceed only if url retrieves data for not getting null pointer exception
        if (content != null) {

            try {
                JSONObject jobj = new JSONObject(content);

                List<Header> headerList = new ArrayList<>();
                JSONArray ar = jobj.optJSONArray("getHeaderResult");
                for (int i = 0; i < ar.length(); i++) {

                    JSONObject obj = ar.getJSONObject(i);
                    Header header = new Header();

                    header.setHeaderName(obj.getString("HeaderName"));
                    header.setUser_Name(obj.getString("User_Name"));

                    headerList.add(header);
                }

                return headerList;

            }catch(JSONException e){

                Log.d("HeaderJSONParser", "In parser exception", e);
                return null;
            }

        } else {
            Log.d("HeaderJSONParser", "Null String return");
            return null;
        }

    }
}
