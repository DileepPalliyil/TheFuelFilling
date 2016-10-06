package in.thefleet.thefuelfilling;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import in.thefleet.thefuelfilling.phne.TelephonyInfo;


public class FleetPreferenceActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            // Create the new ListPref
            ListPreference simListPref = new ListPreference(getActivity());

            // Get the Preference Category which we want to add the ListPreference to
            PreferenceCategory targetCategory = (PreferenceCategory) findPreference("TARGET_CATEGORY");

            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(getActivity());
            if (telephonyInfo.isDualSIM()) {
                CharSequence[] simNames = new CharSequence[]{"Sim 1", "Sim 2","Test Sim"};
                CharSequence[] simValues = new CharSequence[]{ "1", "2","3" };
                simListPref.setEntries(simNames);
                simListPref.setEntryValues(simValues);
                simListPref.setDefaultValue("1");
                simListPref.setKey("simValue");
                simListPref.setTitle("Select Registred Sim ");
                simListPref.setSummary("Select theregistred  sim (Sim 1 or Sim 2 or Test Sim) in the system for data access.");
                simListPref.setPersistent(true);
            }else {
                CharSequence[] simNames = new CharSequence[]{"Sim 1","Test Sim"};
                CharSequence[] simValues = new CharSequence[]{ "1","3"};
                simListPref.setEntries(simNames);
                simListPref.setEntryValues(simValues);
                simListPref.setDefaultValue("1");
                simListPref.setKey("simValue");
                simListPref.setTitle("Select Registred Sim ");
                simListPref.setSummary("Select theregistred  sim (Sim 1 or Test Sim) in the system for data access.");
                simListPref.setPersistent(true);
            }

            // Add the ListPref to the Pref category
            targetCategory.addPreference(simListPref);

            // Create the new ListPref
            ListPreference fleetListPref = new ListPreference(getActivity());
            CharSequence[] fleetType = new CharSequence[]{"All", "Bus","Car","Truck","Bike","LightVehicle"};
            CharSequence[] fleetTypValues = new CharSequence[]{ "all", "Bus","Car","Truck","Bike","LightVehicle" };
            fleetListPref.setEntries(fleetType);
            fleetListPref.setEntryValues(fleetTypValues);
            fleetListPref.setDefaultValue("all");
            fleetListPref.setKey("fleetTypValue");
            fleetListPref.setTitle("Select Fleet Type ");
            fleetListPref.setSummary("Choose the vehicle type preference.");
            fleetListPref.setPersistent(true);

            // Add the ListPref to the Pref category
            targetCategory.addPreference(fleetListPref);

        }
    }
}
