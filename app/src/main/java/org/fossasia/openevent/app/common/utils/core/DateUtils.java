package org.fossasia.openevent.app.common.utils.core;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.app.ContextManager;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class DateUtils {

    public static final String FORMAT_12H = "hh:mm a";
    public static final String FORMAT_24H = "HH:mm";
    public static final String FORMAT_DATE_COMPLETE = "EE, dd MMM yyyy";
    public static final String FORMAT_DAY_COMPLETE = "HH:mm, EE, dd MMM yyyy";

    private static final String INVALID_DATE = "Invalid Date";
    private static final Map<String, DateTimeFormatter> formatterMap = new HashMap<>();

    private static boolean showLocal = false;

    private static DateTimeFormatter getFormatter(@NonNull String format) {
        if (!formatterMap.containsKey(format))
            formatterMap.put(format, DateTimeFormatter.ofPattern(format));

        return formatterMap.get(format);
    }

    // Internal convenience methods to reduce boilerplate

    @NonNull
    private static String formatDate(@NonNull String format, @NonNull ZonedDateTime isoDate) {
        return getFormatter(format).format(isoDate);
    }

    @NonNull
    private static ZoneId getZoneId() {
        if (showLocal || ContextManager.getSelectedEvent() == null)
            return ZoneId.systemDefault();
        else
            return ZoneId.of(ContextManager.getSelectedEvent().getTimezone());
    }

    // Public methods

    public static void setShowLocal(boolean showLocal) {
        DateUtils.showLocal = showLocal;
    }

    @NonNull
    public static String formatDateToIso(@NonNull LocalDateTime date) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(date.atZone(getZoneId()));
    }

    @NonNull
    public static ZonedDateTime getDate(@NonNull String isoDateString) {
        if (isoDateString == null)
            return ZonedDateTime.now();
        return ZonedDateTime.parse(isoDateString).withZoneSameInstant(getZoneId());
    }

    // Currently unused but should be used in future to hide fields if not using default string
    @NonNull
    public static String formatDate(@NonNull String format, @NonNull String isoDateString) {
        return formatDate(format, getDate(isoDateString));
    }

    @NonNull
    public static String formatDateWithDefault(@NonNull String format, @NonNull String isoString, @NonNull String defaultString) {
        String formatted = defaultString;

        try {
            formatted = formatDate(format, isoString);
        } catch (DateTimeParseException pe) {
            Timber.e(pe);
            Timber.e("Error parsing date %s with format %s and default string %s",
                isoString,
                format,
                defaultString);
        }

        return formatted;
    }

    @NonNull
    public static String formatDateWithDefault(@NonNull String format, @NonNull String isoString) {
        return formatDateWithDefault(format, isoString, INVALID_DATE);
    }

}
