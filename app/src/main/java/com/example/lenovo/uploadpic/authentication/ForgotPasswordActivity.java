package com.example.lenovo.uploadpic.authentication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import com.example.lenovo.uploadpic.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    ProgressBar progressBar;
    Integer OtpRequestLimit=5; 
    public boolean isRequest=false;
    EditText nEdittext_Password,nEdittext_ConfPassword,nEdittext_Email;
    LinearLayout linearLayout,linearLayout_password;

    String   sEdittext_Password;
    String   sEdittext_ConfPassword;
    String email,Otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        if (Build.VERSION.SDK_INT > 20) {
            try {
                Window window = this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                try {
                    if (Build.VERSION.SDK_INT > 20) {
                        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
                    }
                } catch (SecurityException e) {
                    Log.e("", e.toString());
                } catch (Exception e) {
                    Log.e("", e.toString());
                }
            } catch (Exception e) {
            }
        }
        setContentView(R.layout.activity_forgot_password);
        progressBar=(ProgressBar)findViewById(R.id.progress);

        nEdittext_Password=(EditText) findViewById(R.id.new_pass);
        nEdittext_ConfPassword=(EditText) findViewById(R.id.conf_pass);
        nEdittext_Email=  ((EditText)findViewById(R.id.contact_email));
        linearLayout=(LinearLayout)findViewById(R.id.input_number_frm);
        linearLayout_password=(LinearLayout)findViewById(R.id.new_pass_container);

    }
    public void Close(View v){
        try{
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }catch (Exception e){}
    }

    public void OtpRequest(View v){

        try{
            email = nEdittext_Email.getText().toString();
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                nEdittext_Email.setError("Invalid email");
            }else{

                ResetPassword(email);

            }

        }catch (Exception e){
            nEdittext_Email.setError("Invalid email");
        }

    }
    public void UpdateNow(View v){
        try{
            if(ValidateResponse()){
                startActivity(new Intent(ForgotPasswordActivity.this, OTPActivity.class)
                        .putExtra("activity",2)
                        .putExtra("email",email)
                        .putExtra("pass",sEdittext_Password)
                        .putExtra("otp",Otp));

                finish();
            }
        }catch (Exception e){}

    }
//    public void OtpVerify(View v){
//        try{
//
//            ((LinearLayout)findViewById(R.id.layout_otp)).setVisibility(View.GONE);
//            ((LinearLayout)findViewById(R.id.new_pass_container)).setVisibility(View.VISIBLE);
//            ((LinearLayout)findViewById(R.id.layout_entry_frm)).setVisibility(View.VISIBLE);
//            ((LinearLayout)findViewById(R.id.input_number_frm)).setVisibility(View.GONE);
//        }catch (Exception e){}
//
//    }
//    public void ResendOtp(View v){
//        try{
//            Utils.dismissKeyboard(this);
//        }catch (Exception e){}
//        try{
//            if(!isRequest) {
//                isRequest = true;
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        isRequest = false;
//                    }
//                }, 10000);
//                if (OtpRequestLimit > 0) {
//                    OtpRequestLimit--;
//                    String no = ((EditText) findViewById(R.id.contact_email)).getText().toString();
//                    RequestOtp(no);
//                } else {
//                    Toast.makeText(this, "You have already exceed the resend request", Toast.LENGTH_SHORT).show();
//                }
//            }else{
//                Toast.makeText(this, "Wait...", Toast.LENGTH_SHORT).show();
//            }
//        }catch (Exception e){}
//
//    }


//    public void RequestOtp(final String number){
//
//        try{
//            String url = "";
//            progressBar.setVisibility(View.VISIBLE);
//            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            progressBar.setVisibility(View.GONE);
//                            Log.d("Response Result",response);
//                            try {
//                                JSONObject myNewJsonObj=new JSONObject(response);
//                                if (myNewJsonObj.getBoolean("success")) {
//
//                                    ((LinearLayout)findViewById(R.id.new_pass_container)).setVisibility(View.VISIBLE);
//                                    ((LinearLayout)findViewById(R.id.layout_entry_frm)).setVisibility(View.VISIBLE);
//                                    ((LinearLayout)findViewById(R.id.input_number_frm)).setVisibility(View.GONE);
//
//                                } else {
//                                    String msg="Unable to send otp";
//                                    if (myNewJsonObj.getBoolean("error")) {
//                                        msg=  myNewJsonObj.getJSONArray("error_message").getJSONArray(0).getString(0);
//                                    }
//                                    Toast.makeText(ForgotPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
//
//                                }
//                            }catch (JSONException e){
//                                Toast.makeText(ForgotPasswordActivity.this, "Unable to send otp", Toast.LENGTH_SHORT).show();
//                            }catch (Exception e){
//                                Toast.makeText(ForgotPasswordActivity.this, "Unable to send otp", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    progressBar.setVisibility(View.GONE);
//                    try {
//                        Log.d("error 1 ", "" + error.getLocalizedMessage());
//                        Log.d("error ", "" + error.getMessage());
//                        Toast.makeText(getApplicationContext(), "Unable to send otp", Toast.LENGTH_SHORT).show();
//                    }catch (Exception e){}
//                }
//            }){
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("number", number);
//                    return params;
//                }
//            };
//            queue.add(stringRequest);
//
//        }catch (Exception e){}
//
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }


    public boolean ValidateResponse(){
        boolean valid=true;
        sEdittext_Password=nEdittext_Password.getText().toString();
        sEdittext_ConfPassword=nEdittext_ConfPassword.getText().toString();
        try {
            if (sEdittext_Password.isEmpty() || sEdittext_Password.length() < 6 || sEdittext_Password.length() > 15) {
                nEdittext_Password.setError("Enter a valid password");
                valid = false;
            } else {
                nEdittext_Password.setError(null);
                if(sEdittext_Password.equalsIgnoreCase(sEdittext_ConfPassword)==false){
                    nEdittext_ConfPassword.setError("Entered password is not confirmed");
                    valid = false;
                }
            }
        }catch (Exception e){
            nEdittext_Password.setError("Enter a valid password");
            valid = false;
        }
        return  valid;
    }

    public void ResetPassword( final String email){

        try{
            String url="http://192.168.43.58/androiduploads/uploadauth.php?apicall=resetpassword";
            ((ProgressBar)findViewById(R.id.progress)).setVisibility(View.VISIBLE);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((ProgressBar)findViewById(R.id.progress)).setVisibility(View.GONE);
                            Log.d("Response Result",response);
                            try {
                                JSONObject myNewJsonObj=new JSONObject(response);
                                if (!myNewJsonObj.getBoolean("error")) {

                                    linearLayout.setVisibility(View.GONE);
                                    linearLayout_password.setVisibility(View.VISIBLE);
                                    OTPgensend otPgensend=new OTPgensend(ForgotPasswordActivity.this,email);
                                    Otp=otPgensend.generateOTP();



//                                    startActivity(new Intent(ForgotPasswordActivity.this, OTPActivity.class)
//                                            .putExtra("activity",2));
//                                    finish();
                                    Toast.makeText(ForgotPasswordActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    nEdittext_Email.setError("Invalid Email Address");
                                    Toast.makeText(ForgotPasswordActivity.this, "Email address no registered with us", Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
                                Toast.makeText(ForgotPasswordActivity.this, "Email address no registered with us", Toast.LENGTH_SHORT).show();

                            }catch (Exception e){
                                Toast.makeText(ForgotPasswordActivity.this, "Email address no registered with us", Toast.LENGTH_SHORT).show();

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
                    params.put("email", email);

                    return params;
                }
            };
            queue.add(stringRequest);

        }catch (Exception e){}

    }

}
