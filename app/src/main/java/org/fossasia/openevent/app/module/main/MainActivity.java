package org.fossasia.openevent.app.module.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseActivity;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.databinding.MainActivityBinding;
import org.fossasia.openevent.app.databinding.MainNavHeaderBinding;
import org.fossasia.openevent.app.module.attendee.list.AttendeesFragment;
import org.fossasia.openevent.app.module.event.dashboard.EventDashboardFragment;
import org.fossasia.openevent.app.module.event.list.EventListFragment;
import org.fossasia.openevent.app.module.login.LoginActivity;
import org.fossasia.openevent.app.module.main.contract.IMainPresenter;
import org.fossasia.openevent.app.module.main.contract.IMainView;
import org.fossasia.openevent.app.module.settings.SettingsFragment;
import org.fossasia.openevent.app.module.ticket.list.TicketsFragment;

import javax.inject.Inject;

import dagger.Lazy;

public class MainActivity extends BaseActivity<IMainPresenter> implements NavigationView.OnNavigationItemSelectedListener, IMainView {

    public static final String EVENT_KEY = "event";
    private static final int BACK_PRESS_RESET_TIME = 2000;
    private long backPressed;
    private long eventId = -1;

    @Inject
    Lazy<IMainPresenter> presenterProvider;

    private FragmentManager fragmentManager;
    private AlertDialog logoutDialog;

    private MainActivityBinding binding;
    private MainNavHeaderBinding headerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OrgaApplication
            .getAppComponent(this)
            .inject(this);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);

        headerBinding = MainNavHeaderBinding.bind(binding.navView.getHeaderView(0));

        setSupportActionBar(binding.main.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.main.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        binding.navView.getMenu().setGroupVisible(R.id.subMenu, false);
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().attach(this);
        getPresenter().start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Picasso.with().cancelTag(MainActivity.class);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (backPressed + BACK_PRESS_RESET_TIME > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        binding.drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                int id = item.getItemId();

                if (id == R.id.nav_logout)
                    showLogoutDialog();
                else
                    loadFragment(id);

                binding.drawerLayout.removeDrawerListener(this);
            }
        });
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Lazy<IMainPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.main_activity;
    }

    @Override
    public void loadInitialPage(long eventId) {
        if (eventId != -1) {
            binding.navView.getMenu().setGroupVisible(R.id.subMenu, true);
            this.eventId = eventId;
            loadFragment(R.id.nav_dashboard);
        } else {
            loadFragment(R.id.nav_events);
        }
    }

    @Override
    public void showResult(Event event) {
        headerBinding.setEvent(event);
    }

    @Override
    public void onLogout() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void loadFragment(int navItemId) {
        binding.navView.setCheckedItem(navItemId);

        Fragment fragment;
        switch (navItemId) {
            case R.id.nav_dashboard:
                fragment = EventDashboardFragment.newInstance(eventId);
                break;
            case R.id.nav_attendees:
                fragment = AttendeesFragment.newInstance(eventId);
                break;
            case R.id.nav_tickets:
                fragment = TicketsFragment.newInstance(eventId);
                break;
            case R.id.nav_events:
                fragment = EventListFragment.newInstance();
                break;
            case R.id.nav_settings:
                fragment = SettingsFragment.newInstance();
                break;
            default:
                fragment = EventDashboardFragment.newInstance(eventId);
        }
        setTitle(binding.navView.getMenu().findItem(navItemId).getTitle());
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    private void showLogoutDialog() {
        if (logoutDialog == null)
            logoutDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.logout_confirmation)
                .setMessage(R.string.logout_confirmation_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> getPresenter().logout())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create();

        logoutDialog.show();
    }
}
