package org.fossasia.openevent.app.common.app.binding;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.widget.Button;

import org.fossasia.openevent.app.common.utils.core.DateUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZonedDateTime;

import io.reactivex.functions.Function;

public class DateBindings {

    private static void setPickedDate(LocalDateTime pickedDate, Button button, String format,
                                      ObservableField<String> date) {
        String isoDate = DateUtils.formatDateToIso(pickedDate);
        date.set(isoDate);
        button.setText(DateUtils.formatDateWithDefault(format, isoDate));
    }

    private static void bindTemporal(Button button, ObservableField<String> date, String format,
                                     Function<ZonedDateTime, AlertDialog> dialogProvider) {
        String isoDate = date.get();
        button.setText(DateUtils.formatDateWithDefault(format, isoDate));

        button.setOnClickListener(view -> {
            ZonedDateTime zonedDateTime = DateUtils.getDate(isoDate);
            try {
                dialogProvider.apply(zonedDateTime).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @BindingAdapter("date")
    public static void bindDate(Button button, ObservableField<String> date) {
        String format = DateUtils.FORMAT_DATE_COMPLETE;

        bindTemporal(button, date, format, zonedDateTime ->
            new DatePickerDialog(button.getContext(), (picker, year, month, dayOfMonth) ->
                setPickedDate(
                    LocalDateTime.of(LocalDate.of(year, month + 1, dayOfMonth), zonedDateTime.toLocalTime()),
                    button, format, date),
                zonedDateTime.getYear(), zonedDateTime.getMonthValue() - 1, zonedDateTime.getDayOfMonth()));
    }

    @BindingAdapter("time")
    public static void bindTime(Button button, ObservableField<String> time) {
        String format = DateUtils.FORMAT_24H;

        bindTemporal(button, time, format, zonedDateTime ->
            new TimePickerDialog(button.getContext(), (picker, hourOfDay, minute) ->
                setPickedDate(
                    LocalDateTime.of(zonedDateTime.toLocalDate(), LocalTime.of(hourOfDay, minute)),
                    button, format, time),
                zonedDateTime.getHour(), zonedDateTime.getMinute(), true));
    }

}
