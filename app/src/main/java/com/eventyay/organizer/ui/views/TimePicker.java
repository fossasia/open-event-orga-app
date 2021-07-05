package com.eventyay.organizer.ui.views;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.eventyay.organizer.data.event.serializer.ObservableString;
import com.eventyay.organizer.utils.DateUtils;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimePicker extends AbstractDateTimePicker {
    public TimePicker(Context context) {
        super(context);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setValue(String value) {
        ObservableString observableValue = getValue();
        if (observableValue.get() == null || !TextUtils.equals(observableValue.get(), value)) {
            observableValue.set(value);
            String format = DateUtils.FORMAT_24H;

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = null;
            try {
                date = dateFormat.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DATE);

            bindTemporal(value, format, zonedDateTime ->
                new TimePickerDialog(this.getContext(), (picker, hourOfDay, minute) ->
                    setPickedDate(
                        LocalDateTime.of(LocalDate.of(year,month,day), LocalTime.of(hourOfDay, minute)), format),
                    zonedDateTime.getHour(), zonedDateTime.getMinute(), true));
        }
    }
}
