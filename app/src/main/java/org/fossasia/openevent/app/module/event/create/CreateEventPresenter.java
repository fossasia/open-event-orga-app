package org.fossasia.openevent.app.module.event.create;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.common.utils.core.CurrencyUtils;
import org.fossasia.openevent.app.common.utils.core.DateUtils;
import org.fossasia.openevent.app.common.utils.core.StringUtils;
import org.fossasia.openevent.app.module.event.create.contract.ICreateEventPresenter;
import org.fossasia.openevent.app.module.event.create.contract.ICreateEventView;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneous;

public class CreateEventPresenter extends BasePresenter<ICreateEventView> implements ICreateEventPresenter {

    private final IEventRepository eventRepository;
    private final Event event = new Event();

    @Inject
    public CreateEventPresenter(IEventRepository eventRepository) {
        this.eventRepository = eventRepository;
        LocalDateTime current = LocalDateTime.now();

        String isoDate = DateUtils.formatDateToIso(current);
        event.getStartsAt().set(isoDate);
        event.getEndsAt().set(isoDate);
    }

    @Override
    public void start() {
        getView().attachCurrencyCodesList(CurrencyUtils.getCurrencyCodesList());

        //set default timezone
        List<String> timeZoneList = getView().getTimeZoneList();
        getView().setDefaultTimeZone(
            timeZoneList.indexOf(TimeZone.getDefault().getID())
        );

        List<String> currencyList = CurrencyUtils.getCurrencyCodesList();
        getView().setDefaultCurrency(
            currencyList.indexOf(Currency.getInstance(Locale.getDefault()).getCurrencyCode())
        );
    }

    @Override
    public Event getEvent() {
        return event;
    }

    private boolean verify() {
        try {
            ZonedDateTime start = DateUtils.getDate(event.getStartsAt().get());
            ZonedDateTime end = DateUtils.getDate(event.getEndsAt().get());

            if (!end.isAfter(start)) {
                getView().showError("End time should be after start time");
                return false;
            }
            return true;
        } catch (DateTimeParseException pe) {
            getView().showError("Please enter date in correct format");
            return false;
        }
    }

    protected void nullifyEmptyFields(Event event) {
        event.setLogoUrl(StringUtils.emptyToNull(event.getLogoUrl()));
        event.setTicketUrl(StringUtils.emptyToNull(event.getTicketUrl()));
        event.setOriginalImageUrl(StringUtils.emptyToNull(event.getOriginalImageUrl()));
        event.setExternalEventUrl(StringUtils.emptyToNull(event.getExternalEventUrl()));
        event.setPaypalEmail(StringUtils.emptyToNull(event.getPaypalEmail()));
    }

    @Override
    public void createEvent() {
        if (!verify())
            return;

        nullifyEmptyFields(event);

        eventRepository
            .createEvent(event)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(createdEvent -> {
                getView().onSuccess("Event Created Successfully");
                getView().close();
            }, Logger::logError);
    }

    /**
     * Returns the most accurate and searchable address substring, which a user can search for.
     * Also makes sure that the substring doesn't contain any numbers by matching it to the regex,
     * as those are more likely to be house numbers or block numbers.
     * @param address full address string of a location
     * @return searchable address substring
     */
    public String getSearchableLocationName(String address) {
        String primary = address.substring(0, address.indexOf(','));
        if (primary.matches(".*\\d+.*")) { //contains number => not likely to be searchable
            return address.substring(address.indexOf(',') + 2, address.indexOf(",", address.indexOf(',') + 1));
        } else return primary;
    }
}
