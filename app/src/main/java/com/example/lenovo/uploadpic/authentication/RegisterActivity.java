package com.example.lenovo.uploadpic.authentication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.example.lenovo.uploadpic.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class RegisterActivity extends AppCompatActivity {

    EditText nEdittext_Username, nEdittext_Password, nEdittext_ConfPassword,nEdittext_Email,nEdittext_Phone;
    TextView nTextview,textView;
    ProgressBar progressBar;
    Button button;

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
                        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    }
                } catch (SecurityException e) {
                    Log.e("", e.toString());
                } catch (Exception e) {
                    Log.e("", e.toString());
                }
            } catch (Exception e) {
            }
        }
        setContentView(R.layout.activity_register);
        nEdittext_Username= (EditText) findViewById(R.id.name);
        nEdittext_Email= (EditText) findViewById(R.id.email);
        nEdittext_Phone= (EditText) findViewById(R.id.mobile);
        nEdittext_Password= (EditText) findViewById(R.id.password);
        nEdittext_ConfPassword= (EditText) findViewById(R.id.confpass);
        nTextview=(TextView)findViewById(R.id.tncview);
        progressBar=(ProgressBar)findViewById(R.id.progress);
        textView = (TextView) findViewById(R.id.terms);
        button=(Button)findViewById(R.id.okbtn);

        SpannableString myString = new SpannableString("Read Terms and Conditions properly and carefully. By clicking Register, you agree to all the Terms & Conditions.");

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(RegisterActivity.this, TermsAndConditionActivity.class));
                textView.setVisibility(View.GONE);
                nTextview.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);

            }
        };
        //For Click
        myString.setSpan(clickableSpan,93,111, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //For UnderLine
        myString.setSpan(new UnderlineSpan(),93,111,0);
        //For Bold
        myString.setSpan(new StyleSpan(Typeface.BOLD),93,111,0);
        //Finally you can set to textView.

        textView.setText(myString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

//        getReadSMSPermission(new RequestPermissionAction() {
//            @Override
//            public void permissionDenied() {
//            }
//
//            @Override
//            public void permissionGranted() {
//            }
//        });
    }

    public void Close(View v){
        try{
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }catch (Exception e){}
    }

    public void ok(View view){
        nTextview.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);
    }

    public  void Register(View v){
        boolean valid=true;
        String   sEdittext_Username=nEdittext_Username.getText().toString();
        String   sEdittext_Email=nEdittext_Email.getText().toString();
        String   sEdittext_Phone=nEdittext_Phone.getText().toString();
        String   sEdittext_Password=nEdittext_Password.getText().toString();
        String   sEdittext_ConfPassword=nEdittext_ConfPassword.getText().toString();
        try {
            if (sEdittext_Username.isEmpty() || sEdittext_Username.length() < 3 || sEdittext_Username.length() > 35) {
                nEdittext_Username.setError("Enter a valid name");
                valid = false;
            } else {
                nEdittext_Username.setError(null);

            }
        }catch (Exception e){
            nEdittext_Username.setError("Enter a valid name");
            valid = false;
        }

        try {
            if (sEdittext_Email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(sEdittext_Email).matches()) {
                nEdittext_Email.setError("Enter a valid email address");
                valid = false;
            } else {
                nEdittext_Email.setError(null);
            }
        }catch (Exception e){
            nEdittext_Email.setError("Enter a valid email address");
            valid = false;
        }
        try {
            if (sEdittext_Phone.isEmpty() || sEdittext_Phone.length()!=10) {
                nEdittext_Phone.setError("Enter a valid mobile number");
                valid = false;
            } else {
                nEdittext_Phone.setError(null);

            }
        }catch (Exception e){
            nEdittext_Phone.setError("Enter a valid mobile number");
            valid = false;
        }

        try {
            if (sEdittext_Password.isEmpty() || sEdittext_Password.length() < 6 || sEdittext_Password.length() > 25) {
                nEdittext_Password.setError("Enter a valid password.(min. 6 char)");
                valid = false;
            } else {
                nEdittext_Password.setError(null);
                if(!sEdittext_Password.equalsIgnoreCase(sEdittext_ConfPassword)){
                    nEdittext_ConfPassword.setError("Entered password is not confirmed");
                    valid = false;
                }
            }
        }catch (Exception e){
            nEdittext_Password.setError("Enter a valid password");
            valid = false;
        }
        if(valid){
            Log.d("User Details : ",""+ sEdittext_Username+"    "+sEdittext_Email+"    "+ sEdittext_Password);
            OTPgensend otPgensend=new OTPgensend(this,sEdittext_Email);
            String Otp = otPgensend.generateOTP();


//            String message = "Your One Time Password is "+Otp;
//            OTPClass otpClass = new OTPClass();
//            otpClass.execute(sEdittext_Email,message);

            startActivity(new Intent(this,OTPActivity.class)
                    .putExtra("username",sEdittext_Username)
                    .putExtra("email",sEdittext_Email)
                    .putExtra("phone",sEdittext_Phone)
                    .putExtra("password",sEdittext_Password)
                    .putExtra("confpass",sEdittext_ConfPassword)
                    .putExtra("otp",Otp)
                    .putExtra("activity",1));
            finish();
        }

    }

//    private class OTPClass extends AsyncTask<String,Void,Void> {
//
//        @Override
//        protected Void doInBackground(String... strings) {
//            sendOtp(strings[0],strings[1]);
//            return null;
//        }
//
//        private void sendOtp(final String email,final String msg){
//            String url = "http://192.168.43.58/androiduploads/sendmail.php";
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.d("Response Result",response);
//                            try {
//                                JSONObject myNewJsonObj=new JSONObject(response);
//                                if (!myNewJsonObj.getBoolean("error")) {
//                                    Toast.makeText(RegisterActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
//                                }
//                            }catch (JSONException e){
//                                Toast.makeText(RegisterActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
//                            }catch (Exception e){
//                                Toast.makeText(RegisterActivity.this, "Unknown", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    try {
//                        Log.d("error 1 ", "" + error.getLocalizedMessage());
//                        Log.d("error ", "" + error.getMessage());
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }){
//
//                @Override
//                protected Map<String, String> getParams(){
//                    Map<String, String> params = new HashMap<>();
//                    params.put("to_mail", email);
//                    params.put("otp", msg);
//                    return params;
//                }
//            };
//
//            Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
//        }
//    }
//
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

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }


}

