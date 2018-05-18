package org.fossasia.openevent.app.ui.views;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;

import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.data.event.serializer.ObservableString;
import org.fossasia.openevent.app.utils.DateUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import timber.log.Timber;

public abstract class AbstractDateTimePicker extends android.support.v7.widget.AppCompatButton {
    private final ObservableString value = new ObservableString();
    private OnDateTimeChangedListener onDateChangedListener;

    public AbstractDateTimePicker(Context context) {
        super(context);
        init();
    }

    public AbstractDateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AbstractDateTimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LocalDateTime current = LocalDateTime.now();
        String isoDate = DateUtils.formatDateToIso(current);
        setValue(isoDate);
    }

    protected void bindTemporal(String date, String format, Function<ZonedDateTime, AlertDialog> dialogProvider) {
        if (date == null)
            return;

        this.setText(DateUtils.formatDateWithDefault(format, date));

        this.setOnClickListener(view -> {
            ZonedDateTime zonedDateTime = ZonedDateTime.now();
            try {
                zonedDateTime = DateUtils.getDate(date);
            } catch (DateTimeParseException pe) {
                Timber.e(pe);
            }
            dialogProvider.apply(zonedDateTime).show();
        });
    }

    public void setOnDateChangedListener(OnDateTimeChangedListener onDateChangedListener) {
        this.onDateChangedListener = onDateChangedListener;
    }

    public void setPickedDate(LocalDateTime pickedDate, String format) {
        String isoDate = DateUtils.formatDateToIso(pickedDate);
        this.value.set(isoDate);
        String formattedDate = DateUtils.formatDateWithDefault(format, isoDate);
        this.setText(formattedDate);
        if (onDateChangedListener != null) {
            onDateChangedListener.onDateChanged(value);
        }
    }

    public abstract void setValue(String value);

    public ObservableString getValue() {
        return value;
    }
}
