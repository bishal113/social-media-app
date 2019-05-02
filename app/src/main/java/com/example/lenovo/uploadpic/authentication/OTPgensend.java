package com.example.lenovo.uploadpic.authentication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Lenovo on 11-06-2018.
 */

public class OTPgensend {
    Context context;
    String email;
    public OTPgensend(Context c,String email){
        this.context=c;
        this.email=email;
    }
    private class OTPClass extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... strings) {
            sendOtp(strings[0],strings[1]);
            return null;
        }

        private void sendOtp(final String email,final String msg){
            String url = "http://192.168.43.58/androiduploads/sendmail.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response Result",response);
                            try {
                                JSONObject myNewJsonObj=new JSONObject(response);
                                if (!myNewJsonObj.getBoolean("error")) {
                                    Toast.makeText(context, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
                                Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Toast.makeText(context, "Unknown", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        Log.d("error 1 ", "" + error.getLocalizedMessage());
                        Log.d("error ", "" + error.getMessage());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }){

                @Override
                protected Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<>();
                    params.put("to_mail", email);
                    params.put("otp", msg);
                    return params;
                }
            };

            Volley.newRequestQueue(context).add(stringRequest);
        }
    }

    public String generateOTP(){
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        String value = "0123456789";
        for (int i = 0; i < 5; i++)
        {
            stringBuilder.append(value.charAt(random.nextInt(value.length())));
        }

        String message="Your One Time Password is "+stringBuilder.toString();
        OTPClass otpClass=new OTPClass();
        otpClass.execute(email,message);
        return stringBuilder.toString();
    }
}
