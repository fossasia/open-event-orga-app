package org.fossasia.openevent.app.core.main;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import android.view.MenuItem;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.core.event.about.AboutEventActivity;

class DrawerNavigator {

    private final Context context;
    private final MainPresenter mainPresenter;
    private final FragmentNavigator fragmentNavigator;

    private AlertDialog logoutDialog;

    DrawerNavigator(Context context, FragmentNavigator fragmentNavigator, MainPresenter mainPresenter) {
        this.context = context;
        this.fragmentNavigator = fragmentNavigator;
        this.mainPresenter = mainPresenter;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    void setLogoutDialog(AlertDialog logoutDialog) {
        this.logoutDialog = logoutDialog;
    }

    void selectItem(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout)
            showLogoutDialog();
        else if (id == R.id.nav_about_event) {
            Intent intent = new Intent(context, AboutEventActivity.class);
            intent.putExtra(AboutEventActivity.EVENT_ID, fragmentNavigator.getEventId());
            context.startActivity(intent);
        } else
            fragmentNavigator.loadFragment(id);
    }

    private void showLogoutDialog() {
        if (logoutDialog == null)
            logoutDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.logout_confirmation)
                .setMessage(R.string.logout_confirmation_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> mainPresenter.logout())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create();

        logoutDialog.show();
    }
}
