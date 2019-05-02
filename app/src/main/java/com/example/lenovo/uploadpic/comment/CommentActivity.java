package com.example.lenovo.uploadpic.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.uploadpic.MainActivity;
import com.example.lenovo.uploadpic.MediaAdapter.MediaListAdapter;
import com.example.lenovo.uploadpic.MediaAdapter.MediaObject;
import com.example.lenovo.uploadpic.R;
import com.example.lenovo.uploadpic.profile.ProfileActivity;
import com.example.lenovo.uploadpic.storage.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    EditText editcomment;
    ImageButton sent;
    String comment;
    PrefManager prefManager;
    ArrayList<CommentObject> myDataset=new ArrayList<CommentObject>();
    CommentListAdapter mAdapter;
    ListView lv;
    int id;

    int comments_count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent i=getIntent();
        id=i.getIntExtra("ID",0);
        comments_count=i.getIntExtra("comments",0);

        editcomment=(EditText)findViewById(R.id.editcomment);
        sent=(ImageButton)findViewById(R.id.sent);
        lv=(ListView)findViewById(R.id.commentlistview);

        sent.setOnClickListener(this);
        prefManager=new PrefManager(this);

        mAdapter=new CommentListAdapter(this,myDataset);
        lv.setAdapter(mAdapter);

        extractcomment();

    }

    @Override
    public void onClick(View v) {
        comment=editcomment.getText().toString();
        if(comment.equals("")){
            Toast.makeText(this,"Please write something.",Toast.LENGTH_SHORT).show();
        }
        else{
            comments_count+=1;
            uploadcomment();
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(editcomment.getWindowToken(),0);
            editcomment.setText("");
            //startActivity(new Intent(this,CommentActivity.class).putExtra("ID",id));
            //finish();
            myDataset.clear();
            mAdapter.notifyDataSetChanged();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do it after sometime because it does not get updated immediately.
                    extractcomment();
                }
            }, 1000);

        }
    }

    public void uploadcomment(){
        String RegisterPath="http://192.168.43.58/androiduploads/uploadcomment.php?apicall=comment";
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
//                                    startActivity(new Intent(CommentActivity.this, LoginActivity.class));
//                                    finish();
                                    Toast.makeText(CommentActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CommentActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
                                Toast.makeText(CommentActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Toast.makeText(CommentActivity.this, "Comment send failed", Toast.LENGTH_SHORT).show();
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
                    params.put("username", prefManager.getUserName());
                    params.put("comments", comment);
                    params.put("image_id",""+id);
                    params.put("comments_count",""+comments_count);

                    return params;
                }
            };
            queue.add(stringRequest);

        }catch (Exception e){}
    }


    public void extractcomment(){
        String RegisterPath="http://192.168.43.58/androiduploads/uploadcomment.php?apicall=extractcomment";
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

                                    JSONArray nAdListArray=myNewJsonObj.getJSONArray("comments");
                                    if(nAdListArray.length()>0){
                                        for(int m=0;m<nAdListArray.length();m++) {
                                            JSONObject nAddObj=nAdListArray.getJSONObject(m);
                                            myDataset.add( new CommentObject(nAddObj.getString("username"),nAddObj.getString("comment")));

                                            mAdapter.notifyDataSetChanged();


                                        }

                                        mAdapter.notifyDataSetChanged();
                                    }

                                    Toast.makeText(CommentActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CommentActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
                                Toast.makeText(CommentActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }catch (Exception e){
                                Toast.makeText(CommentActivity.this, "Comment send failed", Toast.LENGTH_SHORT).show();
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
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("image_id", "" +id);

                    return params;
                }
            };
            queue.add(stringRequest);

        }catch (Exception e){}
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
