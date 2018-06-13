package org.fossasia.openevent.app.utils;

import org.fossasia.openevent.app.data.event.Event;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Pure Android free static utility class
 * No Android specific code should be added
 *
 * All static Android specific utility go into
 * ui/ViewUtils and others to data/UtilModel
 */
public final class Utils {

    private Utils() {
        // Never Called
    }

    /**
     * Copy from TextUtils to use out of Android
     * @param str CharSequence to be checked
     * @return boolean denoting if str is null or empty
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static String optionalString(String string) {
        return isEmpty(string) ? "" : string;
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DD anomaly
    public static String formatOptionalString(String format, String... args) {
        String[] newArgs = new String[args.length];

        for (int i = 0; i < args.length; i++) {
            newArgs[i] = optionalString(args[i]);
        }

        return String.format(format, (Object[]) newArgs);
    }

    public interface PropertyMatcher<T> {
        boolean isEqual(T first, T second);
    }

    public static <E> Single<Integer> indexOf(List<E> items, E item, PropertyMatcher<E> propertyMatcher) {
        return Observable.fromIterable(items)
            .takeWhile(thisItem -> !propertyMatcher.isEqual(thisItem, item))
            .count()
            .map(count -> count == items.size() ? -1 : count.intValue());
    }

    public static String getShareableInformation(Event event) {
        String doubleLineBreak = "\n\n";
        StringBuilder data = new StringBuilder(20);
        data.append(event.getName())
            .append(doubleLineBreak)
            .append("Starts: ").append(DateUtils.formatDateWithDefault(DateUtils.FORMAT_DAY_COMPLETE, event.getStartsAt()))
            .append(doubleLineBreak)
            .append("Ends at: ").append(DateUtils.formatDateWithDefault(DateUtils.FORMAT_DAY_COMPLETE, event.getEndsAt()));

        if (event.getExternalEventUrl() != null) {
            data.append(doubleLineBreak).append("Url: ").append(event.getExternalEventUrl());
        }

        return data.toString();
    }

}
