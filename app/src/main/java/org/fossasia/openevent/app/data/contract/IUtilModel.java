package org.fossasia.openevent.app.data.contract;

import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;

import java.util.Set;

import io.reactivex.Completable;

public interface IUtilModel {

    String getResourceString(@StringRes int stringId);

    @ColorInt int getResourceColor(@ColorRes int colorId);

    String getString(String key, String defaultValue);

    void saveString(String key, String value);

    long getLong(String key, long defaultValue);

    void setLong(String key, long value);

    Set<String> getStringSet(String key, Set<String> defaultValue);

    void saveStringSet(String key, Set<String> value);

    void addStringSetElement(String key, String value);

    String getToken();

    void saveToken(String token);

    boolean isConnected();

    Completable deleteDatabase();

}
