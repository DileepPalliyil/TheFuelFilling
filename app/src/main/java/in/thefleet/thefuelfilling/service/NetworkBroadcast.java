package in.thefleet.thefuelfilling.service;

//To start the save to server activity

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.thefleet.thefuelfilling.online.isOnline;


public class NetworkBroadcast extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;

    private Context context;

    public NetworkBroadcast(){
        this.context=context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isOnline.isNetworkConnected(context)) {
          //  Log.d("Networkbroadcast","Network available");
            Intent i = new Intent(context, SaveIntent.class);
            context.startService(i);
        }
    }
}
