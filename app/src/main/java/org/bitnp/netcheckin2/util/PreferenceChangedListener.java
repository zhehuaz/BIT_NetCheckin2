package org.bitnp.netcheckin2.util;

/**
 * Created by langley on 3/27/15.
 */
public interface PreferenceChangedListener {

    public enum PreferenceKey{
        USERNAME, PASSWORD, IS_AUTO_LOGIN, IS_AUTO_LOGOUT, IS_KEEPALIVE, INTERVAL, UID,
        IS_SLIENT, RELOG_INTERVAL
    }

    public void onPreferenceChanged(PreferenceKey key);
}
