package com.example.lenovo.uploadpic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lenovo.uploadpic.Volley.AppHelper;
import com.example.lenovo.uploadpic.Volley.VolleyMultipartRequest;
import com.example.lenovo.uploadpic.Volley.VolleySingleton;
import com.example.lenovo.uploadpic.storage.PrefManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class uploadpic extends AppCompatActivity implements View.OnClickListener, LocationListener {
    ImageView imageView;
    VideoView videoView;
    EditText editText,gpslocation;
    Button button,button1;
    ImageButton gpsbtn;
    Bitmap bitmap;
    ProgressDialog progressDialog;
    int getcode;
    String getpath;

    PrefManager prefManager;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    LocationManager locationManager;
    public static String Location_Address="";
    private static  Location MyLocation=null;
    public static double fusedLatitude = 0.0;
    public static  double fusedLongitude = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpic);

        imageView=(ImageView)findViewById(R.id.imgviewid);
        videoView=(VideoView)findViewById(R.id.vdoviewid);
        editText=(EditText)findViewById(R.id.edittextid);
        button=(Button)findViewById(R.id.uploadbtn);
        button1=(Button)findViewById(R.id.cancelbtn);
        gpsbtn=(ImageButton)findViewById(R.id.gpsbtn);
        gpslocation=(EditText)findViewById(R.id.gpslocation);
        button.setOnClickListener(this);
        button1.setOnClickListener(this);
        gpsbtn.setOnClickListener(this);
        Intent i=getIntent();
        getcode=i.getIntExtra("mykey",0);
        getpath=i.getStringExtra("data");
        Log.e("uploadpic",getpath);
        if(getcode==1||getcode==2){
             showimage(getpath);
        }
        else if(getcode==3||getcode==4){
            showvdo(getpath);
        }
        else{
            Toast.makeText(this,"Camera Crashed",Toast.LENGTH_SHORT).show();
        }
    }

    public void showimage(String getpath){
        imageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        String comprssedpath=compressImage(getpath);
        File imgpath=new File(comprssedpath);
        bitmap = BitmapFactory.decodeFile(imgpath.getAbsolutePath());
        imageView.setImageBitmap(bitmap);

    }

    public void showvdo(String getpath){
        videoView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
       // Log.e("uploadpic Path: ",getpath,null);
        Uri uri=Uri.parse(getpath);
        videoView.setVideoURI(uri);
        videoView.start();
    }

    public void onClick(View view){
        switch (view.getId())
        {
            case R.id.uploadbtn:
                uploadimage();
                //Toast.makeText(this,"Uploading...",Toast.LENGTH_SHORT).show();
                Intent i=new Intent(uploadpic.this,MainActivity.class);
                startActivity(i);
                finish();
                break;

            case R.id.cancelbtn:
                onBackPressed();
                break;

            case R.id.gpsbtn:
                if(! CheckGpsStatus()){
                    showSettingsAlert();
                }else{
                    if (checkPlayServices()) {
                        startFusedLocation();
                        registerRequestUpdate(this);
                    }
                    getLocation();

                }

                gpslocation.setText(Location_Address);
        }

    }

    @Override
    public void onBackPressed() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(uploadpic.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(uploadpic.this, "Uploading Cancelled", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.setTitle("Cancel Upload?");
        alert.show();
    }
    private void uploadimage(){
        String url="http://192.168.43.58/androiduploads/upload.php?apicall=upload";
        progressDialog = new ProgressDialog(uploadpic.this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        //bm is the bitmap object
//        byte[] b = baos.toByteArray();
//        final String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//        RequestQueue requestQueue= Volley.newRequestQueue(this);
        VolleyMultipartRequest volleyMultipartRequest=new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public  void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                prefManager=new PrefManager(uploadpic.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("id",prefManager.getUserId()+"");
                Log.e("user_id",""+prefManager.getUserId());
                params.put("title",editText.getText().toString().trim());
                if(getcode==1||getcode==2)
                        params.put("filetype","image");
                else if(getcode==3||getcode==4)
                    params.put("filetype","video");

                String locationtext=gpslocation.getText().toString();
                params.put("address",locationtext);

                return params;
            }

            @Override
            protected  Map<String,DataPart> getByteData(){
                Map<String,DataPart>params=new HashMap<>();
                long imagename = System.currentTimeMillis();
                if(getcode==1||getcode==2){
                    params.put("image_video", new DataPart(imagename+".jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), imageView.getDrawable())));
                }
                else if(getcode==3||getcode==4){
                   params.put("image_video", new DataPart(imagename+".mp4", AppHelper.getFileDataFromDrawable(getpath)));
                }
                return params;

            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(volleyMultipartRequest);
    }

    public String compressImage(String imageUri) {
        String filePath="";
        try {
            filePath = getRealPathFromURI(imageUri);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas;
            if (scaledBitmap != null) {
                canvas = new Canvas(scaledBitmap);
                canvas.setMatrix(scaleMatrix);
                canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
            }


            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);

                } else if (orientation == 3) {
                    matrix.postRotate(180);

                } else if (orientation == 8) {
                    matrix.postRotate(270);

                }
                if (scaledBitmap != null) {
                    scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out;

            try {
                out = new FileOutputStream(filePath);
                if (scaledBitmap != null) {
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor =getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }

    }

    /**
     * ..........GPS Code..........
     */

    @Override
    protected void onStop() {
        stopFusedLocation();
        super.onStop();
    }

    public void showSettingsAlert() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("GPS is settings");
        // Setting Dialog Message
        builder.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        builder.setNeutralButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        android.app.AlertDialog alert = builder.create();
        alert.show();
    }


    private boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                Toast.makeText(getApplicationContext(),
                        "This device is supported. Please download google play services", Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    public void startFusedLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnectionSuspended(int cause) {

                        }

                        @Override
                        public void onConnected(Bundle connectionHint) {
                            try{
                                registerRequestUpdate(uploadpic.this);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                        @Override
                        public void onConnectionFailed(ConnectionResult result) {

                        }
                    }).build();
            mGoogleApiClient.connect();
        } else {
            mGoogleApiClient.connect();
        }
    }

    public void stopFusedLocation() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public void registerRequestUpdate(final LocationListener listener) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // every second

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!isGoogleApiClientConnected()) {
                        mGoogleApiClient.connect();
                    }
                    registerRequestUpdate(listener);
                }
            }
        }, 1000);
    }

    public boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    @Override
    public void onLocationChanged(Location location) {
        setFusedLatitude(location.getLatitude());
        setFusedLongitude(location.getLongitude());
        //GpsLocation.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        //Toast.makeText(this, "Get Location From Fused API", Toast.LENGTH_SHORT).show();
        MyLocation=location;
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            //Location_Address = addresses.get(0).getAddressLine(0);
            Location_Address= addresses.get(0).getSubLocality()+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getAdminArea();
            Toast.makeText(this,Location_Address,Toast.LENGTH_SHORT).show();

//        GpsLocation.setText(GpsLocation.getText() + "\n"+addresses.get(0).getAddressLine(0));
//        GpsError.setText("");
        }catch(Exception e)
        {
            Location_Address="";
        }
        //Log.d("", "Get Location From Fused API  Lat:"+ getFusedLatitude()+"  Lng:  "+ getFusedLongitude());
        try {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void getLocation() {
        try {
            int flag=0;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            try {
                Location loc= locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER );
                if(loc==null){
                    flag++;
                    Location loc1= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Toast.makeText(uploadpic.this, "Latitude:"+loc1.getLatitude() + "\n Longitude: " + loc1.getLongitude(), Toast.LENGTH_SHORT).show();
                    setFusedLatitude(loc1.getLatitude());
                    setFusedLongitude(loc1.getLongitude());
                    MyLocation=loc1;
                }else{
                    flag++;
                    setFusedLatitude(loc.getLatitude());
                    setFusedLongitude(loc.getLongitude());
                    MyLocation=loc;
                    Toast.makeText(uploadpic.this, "Latitude:"+loc.getLatitude() + "\n Longitude: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
                }

                if(flag==0){
                    Toast.makeText(uploadpic.this, "could not get lat or long", Toast.LENGTH_SHORT).show();
                }
                //try {
                //To find the String Address of Location
                Geocoder geocoder = new Geocoder(uploadpic.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                //Location_Address=   addresses.get(0).getAddressLine(0);
                Location_Address= addresses.get(0).getSubLocality()+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getAdminArea();
                Toast.makeText(this,Location_Address,Toast.LENGTH_SHORT).show();
            }catch(SecurityException ex) {}
            catch(Exception e)
            {
                Location_Address="";
            }

        }catch (Exception e){
            Log.d("Error",e.toString());
        }

        try{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    setFusedLatitude(location.getLatitude());
                    setFusedLongitude(location.getLongitude());
                    MyLocation=location;
                    //GpsLocation.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
                    //Toast.makeText(MainActivity.this, "Get Location From GPS", Toast.LENGTH_SHORT).show();
                    //Log.d("", "Get Location From GPS  Lat:"+ getFusedLatitude()+"  Lng:  "+ getFusedLongitude());
                    try {
                        Geocoder geocoder = new Geocoder(uploadpic.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        Location_Address= addresses.get(0).getSubLocality()+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getAdminArea();
                        //Location_Address=   addresses.get(0).getAddressLine(0);
                        Toast.makeText(uploadpic.this,Location_Address,Toast.LENGTH_SHORT).show();
                        //GpsLocation.setText(GpsLocation.getText() + "\n"+addresses.get(0).getAddressLine(0));
                        //GpsError.setText("");
                    }catch(Exception e){
                        Location_Address="";
                    }
                    //{
                    //  Location_Address="";
                    //}
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                    //GpsError.setText("Waiting for GPS Location.\nPlease Enable GPS.");
                    showSettingsAlert();
                    Toast.makeText(uploadpic.this, "Waiting for GPS Location.\\nPlease Enable GPS.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }


    public static void  setFusedLatitude(double lat) {
        fusedLatitude = lat;
    }

    public static void setFusedLongitude(double lon) {
        fusedLongitude = lon;
    }

    public double getFusedLatitude() {
        return fusedLatitude;
    }

    public double getFusedLongitude() {
        return fusedLongitude;
    }

    public boolean CheckGpsStatus(){
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){
            return  false;
        }
    }
}
