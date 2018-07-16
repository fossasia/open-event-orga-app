package org.fossasia.openevent.app.core.event.create;

import android.support.design.widget.TextInputLayout;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;
import org.fossasia.openevent.app.common.Function;

import java.util.List;

public interface CreateEventView extends Progressive, Erroneous, Successful {

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> fuck, String tsr);

    void attachCurrencyCodesList(List<String> currencyCodesList);

    void close();

    List<String> getTimeZoneList();

    void setDefaultTimeZone(int index);

    void setDefaultCurrency(int index);

}
