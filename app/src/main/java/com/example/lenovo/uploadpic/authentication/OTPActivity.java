package com.example.lenovo.uploadpic.authentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.uploadpic.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OTPActivity extends AppCompatActivity {

    private String Otp;
    private String email;
    private String username;
    private String password;
    private String confpass;
    private String phone;
    int activity;

    EditText otpInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        Intent intent = getIntent();
        activity = intent.getIntExtra("activity",0);
        if(activity==1){
            username = intent.getStringExtra("username");
            email = intent.getStringExtra("email");
            phone = intent.getStringExtra("phone");
            password = intent.getStringExtra("password");
            confpass = intent.getStringExtra("confpass");
            Otp = intent.getStringExtra("otp");
        }
        else if(activity==2){
            email = intent.getStringExtra("email");
            password=intent.getStringExtra("pass");
            Otp=intent.getStringExtra("otp");
        }

        otpInput =(EditText) findViewById(R.id.otp);
    }

    public void ResendOtp(View v){
        OTPgensend otPgensend=new OTPgensend(this,email);
        Otp=otPgensend.generateOTP();
    }

//    public String generateOTP(){
//        StringBuilder stringBuilder = new StringBuilder();
//        Random random = new Random();
//        String value = "0123456789";
//        for (int i = 0; i < 5; i++)
//        {
//            stringBuilder.append(value.charAt(random.nextInt(value.length())));
//        }
//        return stringBuilder.toString();
//    }

    public void verify(View v){
        String entertedOtp = otpInput.getText().toString();
        if(entertedOtp.equals(Otp)){
            Toast.makeText(this,"Email verified",Toast.LENGTH_SHORT).show();
            if(activity==1) {
                RegisterNow(username, email, password, confpass, phone);
            }
            else if(activity==2){
                updatepassword(password,email);
            }
        }
        else{
            Toast.makeText(this,"Email not verified. Enter again.",Toast.LENGTH_SHORT).show();
        }
    }

    public void RegisterNow( final String name,final String email, final String password, final String con_password,final String phno){

        String RegisterPath="http://192.168.43.58/androiduploads/uploadauth.php?apicall=register";
        try{
            // int socketTimeout = 60000; //10 seconds - change to what you want
//            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//            postRequest.setRetryPolicy(policy)
//                        .setRetryPolicy(policy);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            Log.d("Path ",RegisterPath);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, RegisterPath,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response Result",response);
                            try {
                                JSONObject myNewJsonObj=new JSONObject(response);
                                if (!myNewJsonObj.getBoolean("error")) {

//                                    String validatekey =myNewJsonObj.getString("validatekey");
//                                    //prefManager.setTempKey(validatekey);
//                                    Bundle bun=new Bundle();
//                                    bun.putInt(CONSTANT.EVENT_TYPE,CONSTANT.REGISTRATION_EVENT);
//                                    startActivity(new Intent(RegisterActivity.this, OtpVerifiedActivity.class).putExtras(bun));
                                    /*startActivity(new Intent(RegisterActivity.this, SuccessActivity.class).putExtras(bun));
                                    finish();*/
                                    startActivity(new Intent(OTPActivity.this, LoginActivity.class));
                                    finish();
                                    Toast.makeText(OTPActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OTPActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
                                Toast.makeText(OTPActivity.this, "Unable to register", Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Toast.makeText(OTPActivity.this, "Unable to register", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    (findViewById(R.id.progress)).setVisibility(View.GONE);
                    try {
                        Log.d("error 1 ", "" + error.getLocalizedMessage());
                        Log.d("error ", "" + error.getMessage());
                    }catch (Exception e){}
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    params.put("conf_password", con_password);
                    params.put("name", name);
                    params.put("phno",phno);

                    return params;
                }
            };
            queue.add(stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));

        }catch (Exception e){}

    }

    public void updatepassword(final String pass, final String email){
        String url="http://192.168.43.58/androiduploads/uploadauth.php?apicall=updatepassword";
        try{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response Result",response);
                            try {
                                JSONObject myNewJsonObj=new JSONObject(response);
                                if (!myNewJsonObj.getBoolean("error")) {

                                    startActivity(new Intent(OTPActivity.this, LoginActivity.class));
                                    finish();

                                    Toast.makeText(OTPActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OTPActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
                                Toast.makeText(OTPActivity.this, "JSONException", Toast.LENGTH_SHORT).show();

                            }catch (Exception e){
                                Toast.makeText(OTPActivity.this, "Some Error Occured. Please Try Again", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ((ProgressBar)findViewById(R.id.progress)).setVisibility(View.GONE);
                    try {
                        Log.d("error 1 ", "" + error.getLocalizedMessage());
                        Log.d("error ", "" + error.getMessage());
                    }catch (Exception e){}
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("pass", pass);
                    params.put("email",email);

                    return params;
                }
            };
            queue.add(stringRequest);

        }catch (Exception e){
//            Toast.makeText(this,"Notworking",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void onBackPressed(){
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}
