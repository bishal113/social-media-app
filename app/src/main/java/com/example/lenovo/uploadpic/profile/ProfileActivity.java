package com.example.lenovo.uploadpic.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.uploadpic.MainActivity;
import com.example.lenovo.uploadpic.R;
import com.example.lenovo.uploadpic.Volley.AppHelper;
import com.example.lenovo.uploadpic.Volley.VolleyMultipartRequest;
import com.example.lenovo.uploadpic.Volley.VolleySingleton;
import com.example.lenovo.uploadpic.authentication.LoginActivity;
import com.example.lenovo.uploadpic.authentication.RegisterActivity;
import com.example.lenovo.uploadpic.storage.PrefManager;
import com.example.lenovo.uploadpic.uploadpic;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

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



public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    PrefManager prefManager;
    TextView username,useremail,userphno;
    ImageButton selectprfpic,editname,editemail,editphno;
    String userpicpath;
    Bitmap bitmap;
    CircularImageView profileView;
    ProgressDialog progressDialog;
    long milli;
    String imagename;

    static final int EDIT_NAME=1;
    static final int EDIT_EMAIL=2;
    static final int EDIT_PHNO=3;
    static final int TAKE_PHOTO=4;
    static final int SELECT_PHOTO=5;

    private static final String EXTRACT_PROFILE_PHOTO = "http://192.168.43.58/androiduploads/uploadauth.php?apicall=extractprofile";


    ArrayList<String> myList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        myList=new ArrayList<String>();
        myList.add("Gallery");
        myList.add("Camera");
        myList.add("Remove photo");

        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back);

        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        prefManager=new PrefManager(this);
        username=(TextView)findViewById(R.id.username);
        useremail=(TextView)findViewById(R.id.emailid);
        userphno=(TextView)findViewById(R.id.userphno);
        selectprfpic=(ImageButton)findViewById(R.id.selectprfpic);
        editname=(ImageButton)findViewById(R.id.editname);
        editemail=(ImageButton)findViewById(R.id.editemail);
        editphno=(ImageButton)findViewById(R.id.editphno);
        profileView=(CircularImageView)findViewById(R.id.profile);

        selectprfpic.setOnClickListener(this);
        editname.setOnClickListener(this);
        editemail.setOnClickListener(this);
        editphno.setOnClickListener(this);

        username.setText(prefManager.getUserName());
        useremail.setText(prefManager.getEmailAddress());
        userphno.setText(prefManager.getMobileNumber());


        String profileimage = prefManager.getProfileImage();
        Picasso.with(this)
                .load(profileimage)
                .placeholder(R.drawable.profilepic)
                .error(R.drawable.profilepic)
                .into(profileView);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startActivity(new Intent(this,MainActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.selectprfpic:
                Log.e("SelectProfilePic","");
               // Toast.makeText(this,"Click",Toast.LENGTH_SHORT).show();
                selectprofilepic();
                break;
            case R.id.editname:
                Intent intent=new Intent(this,EditprofileActivity.class);
                intent.putExtra("profilekey",EDIT_NAME);
                startActivity(intent);
                finish();
                break;
            case R.id.editemail:
                Intent i=new Intent(this,EditprofileActivity.class);
                i.putExtra("profilekey",EDIT_EMAIL);
                startActivity(i);
                finish();
                break;
            case R.id.editphno:
                Intent in=new Intent(this,EditprofileActivity.class);
                in.putExtra("profilekey",EDIT_PHNO);
                startActivity(in);
                finish();
                break;
            default:
                Toast.makeText(this,"Please click the correct button",Toast.LENGTH_LONG).show();
                break;


        }
    }

    public void selectprofilepic(){
        try {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

            builder.setTitle("Profile Photo");
            LayoutInflater layoutInflater= getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.listview,null);
            builder.setView(view);

            ListView lv=(ListView)view.findViewById(R.id.profilelistview);



//            builder.setNeutralButton("", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int item) {
//                    // Do something with the selection
//                    if (item == R.id.cameraalert) {
//                        Log.e("Camera","");
//                        Toast.makeText(ProfileActivity.this,"itemselected",Toast.LENGTH_SHORT).show();
//                    } else {
//
//                    }
//                }
//            });
//            AlertDialog alert = builder.create();
            MyAdapter myAdapter=new MyAdapter(ProfileActivity.this,R.layout.listview_item,myList);
            lv.setAdapter(myAdapter);

            final AlertDialog alert = builder.create();

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position==0){
                        selectprofileimgfrmgallery();
                        Toast.makeText(ProfileActivity.this,"Gallery Opened",Toast.LENGTH_SHORT).show();
                        alert.cancel();
                    }
                    else if(position==1){
                        Toast.makeText(ProfileActivity.this,"Camera Opened",Toast.LENGTH_SHORT).show();
                        try {
                            captureimage();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        alert.cancel();
                    }
                    else{
                        removeprofile();
                        prefManager.removeprofileImg();
                        profileView.setImageDrawable(getResources().getDrawable(R.drawable.profilepic));
                        Toast.makeText(ProfileActivity.this,"Profile photo removed",Toast.LENGTH_SHORT).show();
                        alert.cancel();
                    }


                }
            });

            alert.show();
        } catch (Exception e) {
            Log.i("Upload Image", e.toString());
        }
    }

    class MainListHolder {

        private TextView tvText;

    }



    private class ViewHolder {

        TextView tvSname;
        ImageView imageView;

    }

    class MyAdapter extends ArrayAdapter<String> {

        LayoutInflater inflater;

        Context myContext;

        List<String> newList;

        public MyAdapter(Context context, int resource, List<String> list) {

            super(context, resource, list);

            // TODO Auto-generated constructor stub

            myContext = context;

            newList = list;

            inflater = LayoutInflater.from(context);

        }

        @Override

        public View getView(final int position, View view, ViewGroup parent) {

            final ViewHolder holder;

            if (view == null) {

                holder = new ViewHolder();

                view = inflater.inflate(R.layout.listview_item, null);

                holder.tvSname = (TextView) view.findViewById(R.id.textalert);
                holder.imageView=(ImageView)view.findViewById(R.id.dialog_imageview);

                view.setTag(holder);

            } else {

                holder = (ViewHolder) view.getTag();

            }

            holder.tvSname.setText(newList.get(position).toString());

            if(position==0){
                holder.imageView.setImageDrawable(getResources().getDrawable(R.drawable.gallery));
            }
            else if(position==1){
                holder.imageView.setImageDrawable(getResources().getDrawable(R.drawable.profilecamera));
            }
            else{
                holder.imageView.setImageDrawable(getResources().getDrawable(R.drawable.trash));
            }

            return view;

        }

    }

    private File createuserpicfile()throws Exception{
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Profile_Images");
        File picture = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        userpicpath=picture.getAbsolutePath();

        return picture;
    }

    public void captureimage()throws Exception {
        Intent tkpictureintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tkpictureintent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createuserpicfile();

            }catch(IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                Uri photouri=Uri.fromFile(photoFile);
                tkpictureintent.putExtra(MediaStore.EXTRA_OUTPUT, photouri);
                startActivityForResult(tkpictureintent, TAKE_PHOTO);
            }

        }

    }

    @Override
    protected void onActivityResult(int reqcode,int rescode,Intent data){
        if(reqcode==TAKE_PHOTO && rescode== RESULT_OK){
            File imgpath=new File(userpicpath);
            bitmap = BitmapFactory.decodeFile(imgpath.getAbsolutePath());
            profileView.setImageBitmap(bitmap);
            profiledata();
//            ProfilePhotoExtract profilePhotoExtract = new ProfilePhotoExtract();
//            profilePhotoExtract.execute(EXTRACT_PROFILE_PHOTO);
        }
        else if(rescode==RESULT_CANCELED){
            Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show();
        }
        else if(reqcode==SELECT_PHOTO && rescode==RESULT_OK){
            Uri profilepicuri=data.getData();
            if(profilepicuri!=null){
                userpicpath=getProfilePathFromURI(profilepicuri);
                File imgpath=new File(userpicpath);
                bitmap = BitmapFactory.decodeFile(imgpath.getAbsolutePath());
                profileView.setImageBitmap(bitmap);
                profiledata();
            }
        }
    }

    public String getProfilePathFromURI(Uri contentUri) {
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

    public void profiledata(){
        String url="http://192.168.43.58/androiduploads/uploadauth.php?apicall=profileimg";
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();
        VolleyMultipartRequest volleyMultipartRequest=new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public  void onResponse(NetworkResponse response) {
                        //Toast.makeText(get)
                        String profilelink="http://192.168.43.58/androiduploads/uploads/"+imagename;
                        prefManager.setProfileImage(profilelink);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                prefManager=new PrefManager(ProfileActivity.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("id",prefManager.getUserId()+"");
                Log.e("user_id",""+prefManager.getUserId());

                return params;
            }

            @Override
            protected  Map<String,DataPart> getByteData(){
                Map<String,DataPart>params=new HashMap<>();
                milli = System.currentTimeMillis();
                imagename=milli+".jpg";

                params.put("profile_pic", new DataPart(imagename, AppHelper.getFileDataFromDrawable(getBaseContext(), profileView.getDrawable())));

                return params;

            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(volleyMultipartRequest);
    }

    public void selectprofileimgfrmgallery(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    public void removeprofile(){
        String RegisterPath="http://192.168.43.58/androiduploads/uploadauth.php?apicall=deleteprofile";
        try{
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

                                    Toast.makeText(ProfileActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProfileActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
                                Toast.makeText(ProfileActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Toast.makeText(ProfileActivity.this, "Unable to register", Toast.LENGTH_SHORT).show();
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
                    params.put("id",prefManager.getUserId());

                    return params;
                }
            };
            queue.add(stringRequest);

        }catch (Exception e){}
    }




/**
 * Volley Extract methode for Profile pic extraction.
 */
//    private class ProfilePhotoExtract extends AsyncTask<String, Void, Void>{
//
//        @Override
//        protected Void doInBackground(String... params) {
//            extractPhoto(params[0]);
//            return null;
//        }
//
//        private void extractPhoto(String url){
//            try{
//
//                RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
//                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                Log.d("Response Result",response);
//                                try {
//                                    JSONObject myNewJsonObj=new JSONObject(response);
//                                    if (!myNewJsonObj.getBoolean("error")) {
//                                        String photourl = myNewJsonObj.getString("images");
//                                        prefManager = new PrefManager(ProfileActivity.this);
//                                        prefManager.setProfileImage(photourl);
//                                        Toast.makeText(ProfileActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        Toast.makeText(ProfileActivity.this, myNewJsonObj.getString("message"), Toast.LENGTH_SHORT).show();
//                                    }
//                                }catch (JSONException e){
//                                    Toast.makeText(ProfileActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
//                                    Log.e("JSONException",e.toString());
//                                }catch (Exception e){
//                                    Toast.makeText(ProfileActivity.this, "Cannot extract photo", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        (findViewById(R.id.progress)).setVisibility(View.GONE);
//                        try {
//                            Log.d("error 1 ", "" + error.getLocalizedMessage());
//                            Log.d("error ", "" + error.getMessage());
//                        }catch (Exception e){}
//                    }
//                }){
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        Map<String, String> params = new HashMap<>();
//                        params.put("userid",prefManager.getUserId());
//                        return params;
//                    }
//                };
//                queue.add(stringRequest);
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
//
//    }


}
