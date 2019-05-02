package com.example.lenovo.uploadpic.authentication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.uploadpic.MainActivity;
import com.example.lenovo.uploadpic.R;
import com.example.lenovo.uploadpic.storage.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText nEdittext_L_Password,nEdittext_L_Username;
   // String androidId="";
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        try {
//            androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//            Log.d("androidId ",androidId);
//        }catch (Exception e){}
        prefManager=new PrefManager(this);
        if(prefManager.isLoggedIn()){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }

        nEdittext_L_Password=(EditText) findViewById(R.id.l_password);
        nEdittext_L_Username= (EditText) findViewById(R.id.l_email);
    }

    public void ViewNewRegister(View v){
        try{
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            finish();
        }catch (Exception e){e.printStackTrace();}
    }

    public void doLogin(View v){
        boolean valid=true;
        String   sEditText_Username=nEdittext_L_Username.getText().toString();
        String   sEditText_Password=nEdittext_L_Password.getText().toString();
        try {
            if (sEditText_Username.isEmpty()) {
                nEdittext_L_Username.setError("Enter a valid username");
                valid = false;
            } else {
                nEdittext_L_Username.setError(null);
            }
        }catch (Exception e){
            nEdittext_L_Username.setError("Enter a valid email address");
            valid = false;
        }
        try {
            if (sEditText_Password.isEmpty() || sEditText_Password.length() < 6 || sEditText_Password.length() > 15) {
                nEdittext_L_Password.setError("Enter a valid password");
                valid = false;
            } else {
                nEdittext_L_Password.setError(null);
            }
        }catch (Exception e){
            nEdittext_L_Password.setError("Enter a valid password");
            valid = false;
        }
        if(valid){
            Login( sEditText_Username, sEditText_Password);
        }
    }

    public void Login( final String username, final String password){

        try{
            String LoginPath = "http://192.168.43.58/androiduploads/uploadauth.php?apicall=login";
            ((ProgressBar)findViewById(R.id.progress)).setVisibility(View.VISIBLE);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, LoginPath,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((ProgressBar)findViewById(R.id.progress)).setVisibility(View.GONE);
                            Log.d("Response Result",response);
                            try {
                                JSONObject myNewJsonObj=new JSONObject(response);
                                if (!myNewJsonObj.getBoolean("error")) {
                                    JSONObject info=myNewJsonObj.getJSONObject("userinfo");
                                    new PrefManager(LoginActivity.this).createLogin(
                                            info.getInt("id"),
                                            info.getString("name"),
                                            info.getString("email"),
                                            info.getString("mobile"),
                                            info.getString("profile"));
                                    Log.e("LogInId",""+info.getInt("id"));
                                   // new PrefManager(LoginActivity.this).setUserPass("pass");
                                    Toast.makeText(LoginActivity.this,myNewJsonObj.getString("message"),Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
                                Log.d("JSONException Result",e.toString());
                                Toast.makeText(LoginActivity.this, "Unable to login", Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Log.d("JSONException Result",e.toString());
                                Toast.makeText(LoginActivity.this, "Unable to login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ((ProgressBar)findViewById(R.id.progress)).setVisibility(View.GONE);
                    try {
                        Log.d("error 1 ", "" + error.getLocalizedMessage());
                        Log.d("error ", "" + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Unable to login", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){ e.printStackTrace();}
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username_email", username);
                    params.put("password", password);
                    //params.put("d_id", androidId);

                    return params;
                }
            };
            queue.add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ForgotPassword(View v){
        try{
            startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            finish();
        }catch (Exception e){}
    }

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

}
