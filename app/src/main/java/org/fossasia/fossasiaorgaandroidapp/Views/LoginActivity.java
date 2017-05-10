package org.fossasia.fossasiaorgaandroidapp.Views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.fossasia.fossasiaorgaandroidapp.Api.LoginCall;
import org.fossasia.fossasiaorgaandroidapp.Interfaces.VolleyCallBack;
import org.fossasia.fossasiaorgaandroidapp.MainActivity;
import org.fossasia.fossasiaorgaandroidapp.R;
import org.fossasia.fossasiaorgaandroidapp.Utils.CheckLogin;
import org.fossasia.fossasiaorgaandroidapp.Utils.Constants;
import org.fossasia.fossasiaorgaandroidapp.Utils.Network;
import org.fossasia.fossasiaorgaandroidapp.model.LoginDetails;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    Button btnLogin;
    EditText etEmail;
    EditText etPassword;
    String email = "";
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        String token = CheckLogin.isLogin(this);
        //check if token is already present and start Event Activity
//        if(!token.equals("null")){
//            startEventActivity();
//            finish();
//        }

        btnLogin = (Button) findViewById(R.id.btnLogin);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Login Button Clicked");
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                LoginDetails loginDetails = new LoginDetails(email, password);

                VolleyCallBack volleyCallBack  = new VolleyCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "onSuccess: " + result);
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.fossPrefs,Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.sharedPrefsToken, result);
                        editor.commit();
                        startEventActivity();
                        finish();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Log.d(TAG, "onError: " + error);
                    }
                };
                if(Network.isNetworkConnected(LoginActivity.this)) {
                    LoginCall.login(LoginActivity.this, loginDetails, volleyCallBack);
                }else{
                    Toast.makeText(LoginActivity.this, Constants.noNetwork, Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    public void startEventActivity(){
        Intent i =  new Intent(LoginActivity.this, EventsActivity.class);
        startActivity(i);
    }






}
