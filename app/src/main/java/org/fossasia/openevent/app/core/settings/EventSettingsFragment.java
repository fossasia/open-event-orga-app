package org.fossasia.openevent.app.core.settings;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.ui.ViewUtils;

public class EventSettingsFragment extends PreferenceFragmentCompat {

    public static EventSettingsFragment newInstance() {
        return new EventSettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(Constants.FOSS_PREFS);

        setPreferencesFromResource(R.xml.event_preferences, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.setTitle(this, getString(R.string.event_settings));
    }


    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        CountryPreferenceFragmentCompat dialogFragment = null;
        if (preference instanceof CountryPreference)
            dialogFragment = CountryPreferenceFragmentCompat.newInstance(Constants.PREF_PAYMENT_COUNTRY);

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 1);
            dialogFragment.show(this.getFragmentManager(),
                "android.support.v7.preference" +
                    ".PreferenceFragment.DIALOG");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
