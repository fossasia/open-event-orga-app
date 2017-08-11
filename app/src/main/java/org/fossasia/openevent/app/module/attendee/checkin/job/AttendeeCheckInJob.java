package org.fossasia.openevent.app.module.attendee.checkin.job;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.repository.AttendeeRepository;

import javax.inject.Inject;

import timber.log.Timber;

public class AttendeeCheckInJob extends Job {

    public static final String TAG = "attendee_check_in";

    @Inject
    AttendeeRepository attendeeRepository;

    @NonNull
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException") // No information on possible exceptions available
    protected Result onRunJob(Params params) {
        OrgaApplication.getAppComponent().inject(this);

        Timber.d("Running batch job : %s", TAG);

        Iterable<Attendee> attendees = attendeeRepository.getPendingCheckIns().blockingIterable();
        for (Attendee attendee : attendees) {
            Timber.d("Trying to toggle attendee status -> %s", attendee);
            try {
                Attendee toggled = attendeeRepository.toggleAttendeeCheckStatus(attendee).blockingFirst();
                Timber.d("Attendee check in job succeeded for attendee: %s", toggled);
            } catch (Exception exception) {
                Timber.e("Attendee Check In Job Failed for attendee status -> %ss\n" +
                        "With error: %s\n" +
                        "The job is rescheduled", attendee, exception.getMessage());
                return Result.RESCHEDULE;
            }
        }

        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(AttendeeCheckInJob.TAG)
            .setExecutionWindow(1, 5000L)
            .setBackoffCriteria(10000L, JobRequest.BackoffPolicy.EXPONENTIAL)
            .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
            .setRequirementsEnforced(true)
            .setPersisted(true)
            .setUpdateCurrent(true)
            .build()
            .schedule();
    }
}
