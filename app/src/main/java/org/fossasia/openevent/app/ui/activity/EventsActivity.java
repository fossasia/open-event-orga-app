package org.fossasia.openevent.app.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.network.api.ApiCall;
import org.fossasia.openevent.app.data.network.interfaces.IVolleyCallBack;
import org.fossasia.openevent.app.ui.adapters.EventListAdapter;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.Network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsActivity extends AppCompatActivity {

    public static final String TAG = "EventsActivity";
    @BindView(R.id.rvEventList)
    RecyclerView recyclerView;
    static List<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        ButterKnife.bind(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final EventListAdapter eventListAdapter = new EventListAdapter(events, this);
        recyclerView.setAdapter(eventListAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this , DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);


        IVolleyCallBack volleyCallBack = new IVolleyCallBack() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                Event[] userEvents = gson.fromJson(response , Event[].class);
                for (Event event : userEvents){
                    Log.d(TAG, "onSuccess: " + event.getName());
                }
                List<Event> eventList = Arrays.asList(userEvents);
                events.clear();
                events.addAll(eventList);
                eventListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                Log.d(TAG, "onError: " + error.toString());
                Toast.makeText(EventsActivity.this, "Could not fetch Events", Toast.LENGTH_SHORT).show();

            }
        };
        if(Network.isNetworkConnected(this)) {
            ApiCall.callApi(this, Constants.USER_EVENTS, volleyCallBack);
        }else{
            Toast.makeText(this, Constants.NO_NETWORK, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
