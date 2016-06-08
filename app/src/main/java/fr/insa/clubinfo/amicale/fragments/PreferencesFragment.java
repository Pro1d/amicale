package fr.insa.clubinfo.amicale.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import fr.insa.clubinfo.amicale.R;

/**
 * Created by Proïd on 07/06/2016.
 */

public class PreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_preferences);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // To get values : getPreferenceManager().getSharedPreferences();
    }

}
