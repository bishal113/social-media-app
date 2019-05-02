package com.example.lenovo.uploadpic.profile;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.lenovo.uploadpic.storage.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditprofileActivity extends AppCompatActivity implements View.OnClickListener {

    EditText nameedit,emailedit,phnoedit;
    int getkey;

    Button cancelbtn,okbtn;

    String editdata;

    PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        Intent i=getIntent();
        getkey=i.getIntExtra("profilekey",0);

        prefManager=new PrefManager(this);

        nameedit=(EditText)findViewById(R.id.nameedit);
        emailedit=(EditText)findViewById(R.id.emailedit);
        phnoedit=(EditText)findViewById(R.id.phnoedit);
        cancelbtn=(Button)findViewById(R.id.cancelbtn);
        okbtn=(Button)findViewById(R.id.okbtn);

        okbtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);

        if(getkey==1){
            android.support.v7.app.ActionBar actionBar=getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Edit Username");
            }
            nameedit.setVisibility(View.VISIBLE);
            emailedit.setVisibility(View.GONE);
            phnoedit.setVisibility(View.GONE);
            nameedit.setText(prefManager.getUserName());
        }
        else if(getkey==2){
            android.support.v7.app.ActionBar actionBar=getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Edit Email id");
            }
            emailedit.setVisibility(View.VISIBLE);
            nameedit.setVisibility(View.GONE);
            phnoedit.setVisibility(View.GONE);
            emailedit.setText(prefManager.getEmailAddress());
        }
        else if(getkey==3){
            android.support.v7.app.ActionBar actionBar=getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Edit Phone Number");
            }
            phnoedit.setVisibility(View.VISIBLE);
            nameedit.setVisibility(View.GONE);
            emailedit.setVisibility(View.GONE);
            phnoedit.setText(prefManager.getMobileNumber());
        }

    }

    public void edit(){
        if(getkey==1){
            nameedit.setVisibility(View.VISIBLE);
            emailedit.setVisibility(View.GONE);
            phnoedit.setVisibility(View.GONE);
            editdata=nameedit.getText().toString();
            prefManager.setUserName(editdata);
        }
        else if(getkey==2){
            emailedit.setVisibility(View.VISIBLE);
            nameedit.setVisibility(View.GONE);
            phnoedit.setVisibility(View.GONE);
            editdata=emailedit.getText().toString();
            prefManager.setEmailAddress(editdata);
        }
        else if(getkey==3){
            phnoedit.setVisibility(View.VISIBLE);
            nameedit.setVisibility(View.GONE);
            emailedit.setVisibility(View.GONE);
            editdata=phnoedit.getText().toString();
            prefManager.setMobile(editdata);
        }
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this,ProfileActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.okbtn:
                edit();
                updatedata(prefManager.getUserId(),editdata,getkey);
                startActivity(new Intent(this,ProfileActivity.class));
                finish();
                break;
            case R.id.cancelbtn:
                startActivity(new Intent(this,ProfileActivity.class));
                finish();
                break;

        }
    }

    public void updatedata(final String id,final String value,final int key){
        String url="http://192.168.43.58/androiduploads/uploadauth.php?apicall=update";
        try{

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            Log.d("Path ",url);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response Result",response);
                            try {
                                JSONObject myNewJsonObj=new JSONObject(response);
                                if (!myNewJsonObj.getBoolean("error")) {

                                    Toast.makeText(EditprofileActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditprofileActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
                                Toast.makeText(EditprofileActivity.this, "Unable to register", Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Toast.makeText(EditprofileActivity.this, "Unable to register", Toast.LENGTH_SHORT).show();
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
                    params.put("id",id);
                    params.put("change", value);
                    params.put("userkey",""+key);

                    return params;
                }
            };
            queue.add(stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));

        }catch (Exception e){}
    }
}
