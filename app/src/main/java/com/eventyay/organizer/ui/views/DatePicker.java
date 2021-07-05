package com.eventyay.organizer.ui.views;

import android.app.DatePickerDialog;
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

public class DatePicker extends AbstractDateTimePicker {
    public DatePicker(Context context) {
        super(context);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setValue(String value) {
        ObservableString observableValue = getValue();
        if (observableValue.get() == null || !TextUtils.equals(observableValue.get(), value)) {
            observableValue.set(value);
            String format = DateUtils.FORMAT_DATE_COMPLETE;

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = null;
            try {
                date = dateFormat.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            bindTemporal(value, format, zonedDateTime ->
                new DatePickerDialog(this.getContext(), (picker, year, month, dayOfMonth) ->
                    setPickedDate(
                        LocalDateTime.of(LocalDate.of(year, month + 1, dayOfMonth), LocalTime.of(hour,minute)), format),
                    zonedDateTime.getYear(), zonedDateTime.getMonthValue() - 1, zonedDateTime.getDayOfMonth()));
        }
    }
}
