package org.fossasia.openevent.app.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.fossasia.openevent.app.Adapters.AttendeeListAdapter;
import org.fossasia.openevent.app.Api.ApiCall;
import org.fossasia.openevent.app.Interfaces.VolleyCallBack;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.Utils.Constants;
import org.fossasia.openevent.app.Utils.Network;
import org.fossasia.openevent.app.model.AttendeeDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttendeeListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    AttendeeDetails[] attendeeDetails;
    static ArrayList<AttendeeDetails> attendeeDetailsArrayList = new ArrayList<>();
    AttendeeListAdapter attendeeListAdapter;
    Button btnBarCodeScanner;
    long id;
    public static final int REQ_CODE = 123;
    public static final String TAG = "AttendeeListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_list);
        Intent i = getIntent();
        id = i.getLongExtra("id",0);
        recyclerView = (RecyclerView) findViewById(R.id.rvAttendeeList);
        btnBarCodeScanner = (Button) findViewById(R.id.btnScanQr);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        attendeeListAdapter = new AttendeeListAdapter(attendeeDetailsArrayList, this , id);
        recyclerView.setAdapter(attendeeListAdapter);
        recyclerView.setLayoutManager(layoutManager);
        getAttendees();

    }

    public void getAttendees(){
        if(Network.isNetworkConnected(this)) {
            ApiCall.callApi(this, EventDetailsActivity.urlAttendees
                    , new VolleyCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            Gson gson = new Gson();
                            attendeeDetails = gson.fromJson(result, AttendeeDetails[].class);
                            List<AttendeeDetails> attendeeDetailsesList = Arrays.asList(attendeeDetails);
                            attendeeDetailsArrayList.addAll(attendeeDetailsesList);
                            attendeeListAdapter.notifyDataSetChanged();
                            btnBarCodeScanner.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(VolleyError error) {

                        }
                    });

            btnBarCodeScanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(AttendeeListActivity.this, ScanQRActivity.class);
                    startActivityForResult(i, 123);
                }
            });
        }else{
            Toast.makeText(this, Constants.noNetwork , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            if (requestCode == REQ_CODE) {
                String identifier = data.getStringExtra(Constants.scannedIdentifier);
                long id = data.getLongExtra(Constants.scannedId, 0);
                int index = data.getIntExtra(Constants.scannedIndex, -1);
                if (index != -1)
                    checkInAlertBuiler(index);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void checkInAlertBuiler(final int index){
        AlertDialog.Builder builder
                  = new AlertDialog.Builder(this);
        String alertTitle  = "";
        final AttendeeDetails thisAttendee = attendeeDetailsArrayList.get(index);
        if(thisAttendee.getCheckedIn()){
            alertTitle = Constants.AttendeeCheckingOut;
        }else{
            alertTitle = Constants.attendeeChechingIn;
        }

        builder.setTitle(alertTitle).setMessage(thisAttendee.getFirstname() + " "
                + thisAttendee.getLastname() + "\n"
                + "Ticket: " + thisAttendee.getTicket().getType() )
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: inside ok");
                        changeCheckStatus(thisAttendee.getId() , index);
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }

    public void changeCheckStatus(Long thisAttendeeId, final int position){
        if(Network.isNetworkConnected(this)) {
            ApiCall.PostApiCall(this, Constants.eventDetails + id + Constants.attendeesToggle + thisAttendeeId, new VolleyCallBack() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "onSuccess: " + result);
                    Gson gson = new Gson();
                    AttendeeDetails newattendeeDetailses = gson.fromJson(result, AttendeeDetails.class);
                    attendeeDetailsArrayList.set(position, newattendeeDetailses);
                    attendeeListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        }else{
            Toast.makeText(this, Constants.noNetwork , Toast.LENGTH_SHORT).show();
        }

    }
}
