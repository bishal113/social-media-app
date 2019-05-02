package com.example.lenovo.uploadpic;



import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.uploadpic.MediaAdapter.MediaListAdapter;
import com.example.lenovo.uploadpic.MediaAdapter.MediaObject;
import com.example.lenovo.uploadpic.authentication.LoginActivity;
import com.example.lenovo.uploadpic.gps.GPSTracker;
import com.example.lenovo.uploadpic.profile.ProfileActivity;
import com.example.lenovo.uploadpic.storage.PrefManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener{
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_TAKE_VIDEO=3;
    static final int SELECT_PICTURE=2;
    static final int SELECT_VIDEO=4;
   // private static final String TAG = "MainActivity";
    FloatingActionButton b;
    String currentpath;
    String uri="http://192.168.43.58/androiduploads/upload.php?apicall=extract";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    LocationManager locationManager;
    public static String Location_Address="";
    private static  Location MyLocation=null;
    public static double fusedLatitude = 0.0;
    public static  double fusedLongitude = 0.0;

    PrefManager prefManager;

    DownloadData downloadData;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<MediaObject> myDataset=new ArrayList<MediaObject>();
    MediaListAdapter mAdapter;
    static final Integer REQUEST=999;
    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b = (FloatingActionButton) findViewById(R.id.button);

        prefManager=new PrefManager(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,linearLayoutManager.HORIZONTAL));
        mAdapter = new MediaListAdapter(myDataset,getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        userid=prefManager.getUserId();
        Log.e("userid",prefManager.getUserId());

        //gpsTracker = new GPSTracker(this);
//        if (Build.VERSION.SDK_INT >= 23) {
//            String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION};
//            if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
//                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST );
//            } else {
//                checkGpsStatus();
//            }
//        } else {
//            checkGpsStatus();
//        }


        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout) ;
         downloadData = new DownloadData(this);
         refreshcontent();
         //downloadData.execute("http://192.168.43.58/androiduploads/upload.php?apicall=extract");


//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                //swipeRefreshLayout.setEnabled(false);
//                mAdapter.notifyDataSetChanged();
//                if(!swipeRefreshLayout.isRefreshing()) {
//                    downloadData.execute("http://192.168.43.58/androiduploads/upload.php?apicall=extract");
//                }else{
//                    swipeRefreshLayout.setRefreshing(false);
//                }
//
//            }
//        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
//                Intent intent=new Intent(MainActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
                myDataset.clear();
                mAdapter.notifyDataSetChanged();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 1s
                        new DownloadData(MainActivity.this).execute(uri);
                    }
                }, 1000);


                swipeRefreshLayout.setRefreshing(false);
            }
        });




//        swipeRefreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                //swipeRefreshLayout.setEnabled(false);
//                swipeRefreshLayout.setRefreshing(true);
//                downloadData.execute("http://192.168.43.58/androiduploads/upload.php?apicall=extract");
//               // mAdapter.notifyDataSetChanged();
//
//            }
//        });

//        DownloadData downloadData = new DownloadData(this);
//        downloadData.execute("http://192.168.43.58/androiduploads/upload.php?apicall=extract");

       // mAdapter.notifyDataSetChanged();

//        if(! CheckGpsStatus()){
//            showSettingsAlert();
//        }else{
//            if (checkPlayServices()) {
//                startFusedLocation();
//                registerRequestUpdate(this);
//            }
//            getLocation();
//
//        }


      //  Toast.makeText(this,Location_Address,Toast.LENGTH_SHORT).show();

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }
        }catch (Exception e){}
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 101);
            }
        }catch (Exception e){}
    }

    //Outside the onCreate function.


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.myprofile){
            //Profile Activity
            startActivity(new Intent(this, ProfileActivity.class));
            finish();

            return true;
        }
        else if(id==R.id.logout){
            prefManager.clearSession();

            Toast.makeText(this,"Logged Out",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void refreshcontent(){
                swipeRefreshLayout.setRefreshing(true);
                downloadData.execute(uri);
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

    @Override
    protected void onStop() {
        stopFusedLocation();
        super.onStop();
    }


//    LocationManager locationManager;
//    boolean isGPSEnabled=false, canGetLocation=false;



//    public void checkGpsStatus() {
//        try {
//            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//
//            // getting GPS status
//            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            if(!isGPSEnabled){
//                showSettingsAlert();
//            }else
//            {
//                getLocation();
//                startService(new Intent(this, GPSTracker.class));
//            }
//
//        }catch (SecurityException nn){}catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private static boolean hasPermissions(Context context, String... permissions) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
//            for (String permission : permissions) {
//                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//                    return false;
//                }
//            }
//        }
//        return true;
//
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case 999:
//                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                        getLocation();
//                        startService(new Intent(this, GPSTracker.class));
//                        //call get location here
//                    } else {
//                        Toast.makeText(MainActivity.this, "The app was not allowed to access your location", Toast.LENGTH_LONG).show();
//                    }
//                break;
//
//        }
//    }


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



//    @Override
//    public void onRefresh(){
//        swipeRefreshLayout.setEnabled(true);
//        downloadData.execute("http://192.168.43.58/androiduploads/upload.php?apicall=extract");
//        mAdapter.notifyDataSetChanged();
//        if(swipeRefreshLayout.isRefreshing()) {
//            swipeRefreshLayout.setRefreshing(false);
//        }
//    }

    private File createImgFile() throws IOException {

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss",Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Pictures");
        File picture = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentpath = picture.getAbsolutePath();
//        try{
//            GPSTracker gpsTracker=new GPSTracker(this);
//            ExifInterface exifInterface=new ExifInterface(currentpath);
//            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE,""+gpsTracker.getLatitude());
//            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,""+gpsTracker.getLongitude());
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return picture;
    }

    private File createVdoFile() throws IOException {

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss",Locale.ENGLISH).format(new Date());
        String videoFileName = "Video_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Videos");
        File video = File.createTempFile(
                videoFileName,
                ".mp4",
                storageDir
        );

        currentpath = video.getAbsolutePath();
        return video;
    }

    public void onclick(View view) {
        final CharSequence[] select={"Photo","Video"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload");
        builder.setItems(select, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                if (item == 0) {
                    uploadpic();
                } else {
                    uploadvdo();
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void uploadpic() {
        try {
            final CharSequence[] items = {
                    "Take photo from Camera", "Use photo from Gallery"};

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Upload Photo");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    if (item == 0) {
                        captureimage();
                    } else {
                        selectimgfrmgallery();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            Log.i("Upload Image", e.toString());
        }
    }


    public void uploadvdo(){
        try {
            final CharSequence[] item1 = {
                    "Record Video using Camera", "Use Video from Gallery"};

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Upload Video");
            builder.setItems(item1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    if (item == 0) {
                        recordvideo();
                    } else {
                        selectvdofrmgallery();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }catch (Exception e){

            Log.i("Upload Video", e.toString());
        }
    }

    public void recordvideo(){

        Intent tkvideointent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (tkvideointent.resolveActivity(getPackageManager()) != null) {
            File videoFile = null;
            try {
                videoFile = createVdoFile();

            } catch (IOException e) {

            }

            if (videoFile != null) {
                Uri vdouri = Uri.fromFile(videoFile);
                //Uri vdouri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", videoFile);
                tkvideointent.putExtra(MediaStore.EXTRA_OUTPUT,vdouri);
                startActivityForResult(tkvideointent, REQUEST_TAKE_VIDEO);
            }

        }
    }

    public void selectvdofrmgallery(){
        Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),SELECT_VIDEO);
    }

    public void selectimgfrmgallery(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        //intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }
    public void captureimage() {
        Intent tkpictureintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tkpictureintent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImgFile();

            } catch (IOException e) {

            }

            if (photoFile != null) {
                //Uri photouri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                Uri photouri=Uri.fromFile(photoFile);
                tkpictureintent.putExtra(MediaStore.EXTRA_OUTPUT, photouri);
                startActivityForResult(tkpictureintent, REQUEST_TAKE_PHOTO);
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Intent intent = new Intent(MainActivity.this, uploadpic.class);
            intent.putExtra("data", currentpath);
            intent.putExtra("mykey",REQUEST_TAKE_PHOTO);
            startActivity(intent);
            finish();
        }
        else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
//            Uri selectedImageUri = data.getData();
//            String[] filepath={MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(selectedImageUri, filepath, null, null, null);
//            cursor.moveToFirst();
//            int columnindex=cursor.getColumnIndex(filepath[0]);
//            currentimgpath =cursor.getString(columnindex);
//            cursor.close();
//            Intent in = new Intent(MainActivity.this, uploadpic.class);
//            in.putExtra("data", currentimgpath);
//            startActivity(in);
            // Get the url from data
            Uri selectedImageUri = data.getData();
            if (null != selectedImageUri) {
                // Get the path from the Uri
                 currentpath = getPathFromURI(selectedImageUri);
               // Log.i(TAG, "Image Path : " + currentimgpath);
                Intent in = new Intent(MainActivity.this, uploadpic.class);
                in.putExtra("data", currentpath);
                in.putExtra("mykey",SELECT_PICTURE);
                startActivity(in);
                finish();
            }
        }
        else if(requestCode==SELECT_VIDEO && resultCode ==RESULT_OK){
            Uri selectedVdoUri=data.getData();
            if(selectedVdoUri!=null){
                currentpath=getvdoPathFromURI(selectedVdoUri);
                Intent i=new Intent(MainActivity.this,uploadpic.class);
                i.putExtra("data",currentpath);
                i.putExtra("mykey",SELECT_VIDEO);
                startActivity(i);
                finish();

            }

        }
        else if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK) {
            Intent intent = new Intent(MainActivity.this, uploadpic.class);
            intent.putExtra("data", currentpath);
            intent.putExtra("mykey",REQUEST_TAKE_VIDEO);
            startActivity(intent);
            finish();
        }

    }

     /* Get the real path from the URI */
        public String getPathFromURI(Uri contentUri) {
            String res = null;
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
            return res;
        }

    public String getvdoPathFromURI(Uri contentUri) {
        String reslt = null;
        String[] pro = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, pro, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            reslt = cursor.getString(column_index);
        }
        cursor.close();
        return reslt;
    }

    @Override
    public void onBackPressed() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure to Exit")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.setTitle("Exit ?");
        alert.show();
    }


    private class DownloadData extends AsyncTask<String,Void,Void> {

        private String mUrl;
        Context context;
        private DownloadData(Context context){
            this.context = context;
        }

        @Override
        public Void doInBackground(String...urls) {
            mUrl = urls[0];
            LoadImage(mUrl,context);
            return null;
        }

        public void LoadImage( String url,final Context c){

            try{
                RequestQueue queue = Volley.newRequestQueue(c);
                Log.d("Path ", url);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.d("Response Result",response);
                                try {
                                    JSONObject myNewJsonObj=new JSONObject(response);
                                    if (!myNewJsonObj.getBoolean("error")) {

                                        JSONArray nAdListArray=myNewJsonObj.getJSONArray("images");
                                        JSONArray likedImages = myNewJsonObj.getJSONArray("liked_images");

                                        MediaObject mediaObject = null;

                                        ArrayList<Integer> arrayList = new ArrayList<>();
                                        for(int i=0; i<likedImages.length(); i++){
                                            int x = (int)likedImages.get(i);
                                            arrayList.add(x);
                                        }

                                        int count=0;

                                        if(nAdListArray.length()>0){
                                            for(int m=0;m<nAdListArray.length();m++) {
                                                JSONObject nAddObj=nAdListArray.getJSONObject(m);
                                                String[] add1 = Location_Address.split(",");
                                                String[] add2 = nAddObj.getString("address").split(",");
                                             //   if(add1[1].equalsIgnoreCase(add2[1])) {
                                                    mediaObject = new MediaObject(nAddObj.getInt("id"), nAddObj.getString("name"), nAddObj.getString("image"), nAddObj.getString("filetype"), nAddObj.getInt("agrees"), nAddObj.getString("username"), nAddObj.getString("profilepic"), nAddObj.getString("address"),nAddObj.getInt("comments"));

                                                if(count==0){
                                                    mediaObject.setImage_liked(arrayList);
                                                    count++;
                                                }

                                                myDataset.add(mediaObject);

                                                Log.e("id", "" + nAddObj.getInt("id"));
                                                Log.e("name", "" + nAddObj.getString("name"));
                                                Log.e("image", "" + nAddObj.getString("image"));
                                                Log.e("profilepic", "" + nAddObj.getString("profilepic"));
                                                Log.e("LoadAgrees", "" + nAddObj.getInt("agrees"));
                                              //  }
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }


                                        Log.e("array list",""+arrayList);

                                        mAdapter.notifyDataSetChanged();
                                    }

                                }catch (JSONException e){
                                    Log.d("error ", "" + e.toString());
                                }catch (Exception e){
                                    Log.d("error ", "" + e.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            Log.d("error ", "" + error.getMessage());
                            Toast.makeText(c, "Unable to connect server", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("userid", userid);
                        return params;
                    }
                };
                queue.add(stringRequest);
            }catch (Exception e){
                Log.d("error ", "" + e.toString());
            }
        }
    }

//    public void getLocation()
//    {
//        try {
//            String bestProvider = null;
//            Location location = null;
//            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//            Criteria criteria = new Criteria();
//            bestProvider = locationManager.getBestProvider(criteria, true);
//
//            location = locationManager.getLastKnownLocation(bestProvider);
//            if (location != null) {
//                Toast.makeText(gpsTracker, "Get Location", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(gpsTracker, "Unable To Get location", Toast.LENGTH_SHORT).show();
//            }
//            //locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
//        }catch (SecurityException e){}catch (Exception e){}
//    }

    // apps.kushal@gmail.com

//    public class DownloadDataCallback extends DiffUtil.Callback{
//
//        private final List<DownloadData> mOldList;
//        private final List<DownloadData>mNewList;
//
//        public DownloadDataCallback(List<DownloadData> OldDownloadList, List<DownloadData> NewDownloadList) {
//            this.mOldList=OldDownloadList;
//            this.mNewList=NewDownloadList;
//        }
//
//        @Override
//        public int getOldListSize() {
//            return mOldList.size();
//        }
//
//        @Override
//        public int getNewListSize() {
//            return mNewList.size();
//        }
//
//        @Override
//        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//            return mOldList.get(oldItemPosition).LoadImage();)==mNewList.get(newItemPosition).getStatus();
//        }
//
//        @Override
//        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//            final DownloadData oldDownloadData=mOldList.get(oldItemPosition);
//            final DownloadData newDownloadData=mNewList.get(newItemPosition);
//
//            return oldDownloadData.getStatus().equals(newDownloadData.getStatus());
//        }
//    }


    /**
     * ..........GPS Code..........
     */


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
                                registerRequestUpdate(MainActivity.this);
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
       // Location_Address = addresses.get(0).getAddressLine(0);
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
                    Toast.makeText(MainActivity.this, "Latitude:"+loc1.getLatitude() + "\n Longitude: " + loc1.getLongitude(), Toast.LENGTH_SHORT).show();
                    setFusedLatitude(loc1.getLatitude());
                    setFusedLongitude(loc1.getLongitude());
                    MyLocation=loc1;
                }else{
                    flag++;
                    setFusedLatitude(loc.getLatitude());
                    setFusedLongitude(loc.getLongitude());
                    MyLocation=loc;
                    Toast.makeText(MainActivity.this, "Latitude:"+loc.getLatitude() + "\n Longitude: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
                }

                if(flag==0){
                    Toast.makeText(MainActivity.this, "could not get lat or long", Toast.LENGTH_SHORT).show();
                }
                //try {
                //To find the String Address of Location
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
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
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        //Location_Address=   addresses.get(0).getAddressLine(0);
                        Location_Address= addresses.get(0).getSubLocality()+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getAdminArea();
                        Toast.makeText(MainActivity.this,Location_Address,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MainActivity.this, "Waiting for GPS Location.\\nPlease Enable GPS.", Toast.LENGTH_SHORT).show();
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



