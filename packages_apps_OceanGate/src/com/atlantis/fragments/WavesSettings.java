/*
 * Copyright (C) 2023 AtlantisOS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.oceangate.fragments;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceScreen;
import android.content.Context;
import android.content.ContentResolver;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.nano.MetricsProto;
import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.atlantis.support.preferences.SystemSettingSwitchPreference;
import com.atlantis.support.preferences.SystemSettingSeekBarPreference;
import com.atlantis.support.preferences.SecureSettingSwitchPreference;
import com.atlantis.support.preferences.SystemSettingListPreference;
import com.android.settings.SettingsPreferenceFragment;
import com.atlantis.support.preferences.SystemSettingSeekBarPreference;

public class WavesSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

private static final String KEY_STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String KEY_STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String KEY_STATUS_BAR_BATTERY_TEXT_CHARGING = "status_bar_battery_text_charging";

    private static final int BATTERY_STYLE_PORTRAIT = 0;
    private static final int BATTERY_STYLE_TEXT = 4;
    private static final int BATTERY_STYLE_HIDDEN = 5;

    private SystemSettingListPreference mBatteryPercent;
    private SystemSettingListPreference mBatteryStyle;
    private SwitchPreference mBatteryTextCharging;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.o_settings_notifications);

        final PreferenceScreen prefScreen = getPreferenceScreen();
      
       int batterystyle = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY_STYLE, BATTERY_STYLE_PORTRAIT, UserHandle.USER_CURRENT);
        int batterypercent = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0, UserHandle.USER_CURRENT);

        mBatteryStyle = (SystemSettingListPreference) findPreference(KEY_STATUS_BAR_BATTERY_STYLE);
        mBatteryStyle.setOnPreferenceChangeListener(this);

        mBatteryPercent = (SystemSettingListPreference) findPreference(KEY_STATUS_BAR_SHOW_BATTERY_PERCENT);
        mBatteryPercent.setEnabled(
                batterystyle != BATTERY_STYLE_TEXT && batterystyle != BATTERY_STYLE_HIDDEN);
        mBatteryPercent.setOnPreferenceChangeListener(this);

        mBatteryTextCharging = (SwitchPreference) findPreference(KEY_STATUS_BAR_BATTERY_TEXT_CHARGING);
        mBatteryTextCharging.setEnabled(batterystyle == BATTERY_STYLE_HIDDEN ||
                (batterystyle != BATTERY_STYLE_TEXT && batterypercent != 2));
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBatteryStyle) {
            int value = Integer.parseInt((String) newValue);
            int batterypercent = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0, UserHandle.USER_CURRENT);
            mBatteryPercent.setEnabled(
                    value != BATTERY_STYLE_TEXT && value != BATTERY_STYLE_HIDDEN);
            mBatteryTextCharging.setEnabled(value == BATTERY_STYLE_HIDDEN ||
                    (value != BATTERY_STYLE_TEXT && batterypercent != 2));
            return true;
        } else if (preference == mBatteryPercent) {
            int value = Integer.parseInt((String) newValue);
            int batterystyle = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.STATUS_BAR_BATTERY_STYLE, BATTERY_STYLE_PORTRAIT, UserHandle.USER_CURRENT);
            mBatteryTextCharging.setEnabled(batterystyle == BATTERY_STYLE_HIDDEN ||
                    (batterystyle != BATTERY_STYLE_TEXT && value != 2));
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.OCEANGATE;
    }
}