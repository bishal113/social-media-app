package com.example.lenovo.uploadpic.MediaAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.support.v7.widget.RecyclerView;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.uploadpic.MainActivity;
import com.example.lenovo.uploadpic.R;
import com.example.lenovo.uploadpic.comment.CommentActivity;
import com.example.lenovo.uploadpic.storage.PrefManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * Created by USER on 3/17/2018.
 */

public class MediaListAdapter extends RecyclerView.Adapter<MediaListAdapter.ViewHolder> {

    ArrayList<MediaObject> mDataset = new ArrayList<MediaObject>();
    Context mContext;

    PrefManager prefManager;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mTitle,addressview;
        private ImageView Icon;
        private VideoView VdoIcon;
        private Button agree_btn,commentbtn;
        private TextView agree_textview,userTextView,commenttextview,comments_count;
        private CircularImageView profilepic;
        private ImageButton play,pause;



        public ViewHolder(LinearLayout v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.text_title);
            Icon = (ImageView) v.findViewById(R.id.icon);
            VdoIcon = (VideoView) v.findViewById(R.id.vdoicon);
            agree_btn = (Button) v.findViewById(R.id.agree_btn);
            agree_textview = (TextView) v.findViewById(R.id.agree_textview);
            userTextView = (TextView) v.findViewById(R.id.username);
            profilepic=(CircularImageView)v.findViewById(R.id.profileimg);
            commentbtn=(Button)v.findViewById(R.id.comment_btn);
            commenttextview=(TextView)v.findViewById(R.id.comment_text);
            addressview=(TextView)v.findViewById(R.id.addressview);
            play=(ImageButton)v.findViewById(R.id.play);
            pause=(ImageButton)v.findViewById(R.id.pause);
            comments_count=(TextView)v.findViewById(R.id.comment_textview);



//            agree_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e("Agreed Position",""+pos);
//                    pos = 0;
//                    count+=1;
//                    agree_textview.setText(count+" AGREED");
//                    String url = "http://192.168.43.58/androiduploads/uploadvote.php?apicall=agree";
//                    VolleyMultipartRequest volleyMultipartRequest=new VolleyMultipartRequest(Request.Method.POST, url,
//                            new Response.Listener<NetworkResponse>(){
//                                @Override
//                                public  void onResponse(NetworkResponse response) {
//                                    //Toast.makeText(get)
//                                }
//                            }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//
//                        }
//                    }){
//                        @Override
//                        protected Map<String, String> getParams() {
//                            Map<String, String> params = new HashMap<String, String>();
//                            //params.put("image",encodedImage );
//                            params.put("agrees",""+count);
//                            params.put("id",""+pos);
//                            return params;
//                        }
//                    };
//                }
//            });

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MediaListAdapter(ArrayList<MediaObject> myDataset, Context mContext) {
        mDataset = myDataset;
        this.mContext = mContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MediaListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        LinearLayout v1 = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image_item, parent, false);

        ViewHolder vh = new ViewHolder(v1);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        prefManager=new PrefManager(mContext);
        try {
          //  holder.agree_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.voteicon, 0, 0, 0);
            final MediaObject nAdModel = mDataset.get(position);
            final int id=nAdModel.getId();      //image id
            holder.agree_textview.setText(""+nAdModel.getAgree()+ " AGREED");
            holder.comments_count.setText(""+nAdModel.getComments()+ " COMMENTS");
            holder.mTitle.setText(nAdModel.getImageName());
            ArrayList<Integer> likedImages = nAdModel.getImage_liked();

            Log.e("likedImages SIze","-->"+likedImages.size());
            //final int count = nAdModel.getAgree();
            holder.addressview.setText(nAdModel.getAddress());
            holder.agree_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = nAdModel.getAgree();
                    count+=1;
                    holder.agree_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.voteicon_color, 0, 0, 0);
                    holder.agree_btn.setTextColor(Color.parseColor("#2ECC71"));
                    holder.agree_btn.setEnabled(false);
                    holder.agree_textview.setText(count + " AGREED");
                    agree agdb=new agree();
                    agdb.execute(count,id);
                    Log.e("MediaListAdapter",""+id);
                }
            });

            holder.userTextView.setText(nAdModel.getUsername());

            String encodedUrl = nAdModel.getImagePath().replaceAll(" ", "%20");
            Log.e("URL", "" + nAdModel.getImagePath());
            //holder.VdoIcon.setVisibility(GONE);
            //  holder.Icon.setVisibility(GONE);
//            nAdModel.getImagePath();
            //holder.Icon.getBackground().setAlpha(100);
                Picasso.with(this.mContext)
                        .load(nAdModel.getProfilepic())
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(holder.profilepic);

            if (nAdModel.getFiletype().equals("image")) {
                holder.Icon.setVisibility(VISIBLE);
                holder.VdoIcon.setVisibility(GONE);
                Picasso.with(this.mContext)
                        .load(encodedUrl)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(holder.Icon);
            }
            if (nAdModel.getFiletype().equals("video")) {
                holder.VdoIcon.setVisibility(VISIBLE);
                holder.Icon.setVisibility(GONE);
                Log.e("Imagelistadapter", nAdModel.getImagePath());


//                mediaController.setAnchorView(holder.VdoIcon);
//                holder.VdoIcon.setMediaController(mediaController);
                // mediaController.show();
//                if(mediaController.isShowing()){
//                    mediaController.hide();
//                }

                //Uri uri = Uri.parse(nAdModel.getImagePath());
                holder.play.setVisibility(View.VISIBLE);
                holder.pause.setVisibility(View.GONE);
                //holder.VdoIcon.setVideoURI(uri);
//                holder.VdoIcon.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        holder.VdoIcon.start();
//                    }
//                });

            }
           // final MediaController mediaController=new MediaController(mContext);

            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mediaController.setAnchorView(holder.VdoIcon);
//                    holder.VdoIcon.setMediaController(mediaController);
                    Uri uri = Uri.parse(nAdModel.getImagePath());
                    holder.VdoIcon.setVideoURI(uri);
                    holder.play.setVisibility(View.GONE);
                    holder.pause.setVisibility(View.VISIBLE);
                    holder.VdoIcon.seekTo(0);
                    holder.VdoIcon.start();
                }
            });

            holder.pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.play.setVisibility(View.VISIBLE);
                    holder.pause.setVisibility(View.GONE);
                    holder.VdoIcon.seekTo(0);
                    holder.VdoIcon.stopPlayback();
                }
            });

            holder.commentbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, CommentActivity.class);
                    intent.putExtra("ID",id);
                    intent.putExtra("comments",nAdModel.getComments());
                    v.getContext().startActivity(intent);
                    ((Activity)v.getContext()).finish();
                }
            });
            if( likedImages.contains(id)){

                holder.agree_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.voteicon_color, 0, 0, 0);
                holder.agree_btn.setTextColor(Color.parseColor("#2ECC71"));
                holder.agree_btn.setEnabled(false);

                Log.e("Liked image id" ,""+id);

            }else{

                holder.agree_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.voteicon, 0, 0, 0);
                holder.agree_btn.setTextColor(Color.parseColor("#000000"));
                holder.agree_btn.setEnabled(true);

                Log.e("default Image id" ,""+id);

            }
/*

            if(likedImages.size()>0){
                likedImages.contains(id)

                for(int i=0; i<likedImages.size(); i++){
                    if(id==likedImages.get(i)){
                        holder.agree_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.voteicon_color, 0, 0, 0);
                        holder.agree_btn.setTextColor(Color.parseColor("#2ECC71"));
                        holder.agree_btn.setEnabled(false);
                    }
                }
            }
*/

//            holder.commenttextview.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent=new Intent(mContext, CommentActivity.class);
//                    intent.putExtra("ID",id);
//                    v.getContext().startActivity(intent);
//                }
//            });

            holder.VdoIcon.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    holder.play.setVisibility(View.VISIBLE);
                    holder.pause.setVisibility(View.GONE);
                    holder.VdoIcon.seekTo(0);
                }
            });

//                 Blurry.with(mContext)
//                         .radius(10)
//                         .sampling(8)
//                         .color(Color.argb(66, 255, 255, 0))
//                         .async()
//                         .onto(holder.Icon);

        } catch (Exception e) {

            Log.e("Liked image id" ,""+e.toString());
        }


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface ItemListener {
        void onItemClick(MediaObject item, int click_status);
    }


//    private class BitmapImage extends AsyncTask<String,Void,Bitmap> {
//
//        ViewHolder v;
//        public BitmapImage(ViewHolder v){
//            this.v = v;
//        }
//        private Bitmap createBitmapFromURL(String imagePath){
//            URL url;
//            HttpURLConnection httpURLConnection = null;
//            Bitmap bitmap = null;
//            try{
//                url = new URL(imagePath);
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.connect();
//                InputStream input = httpURLConnection.getInputStream();
//                bitmap = BitmapFactory.decodeStream(input);
//                //Log.e("Bitmap","returned");
//            }catch(Exception e){
//                Log.e("createBitmapFromURL",""+e);
//            }finally{
//                if(httpURLConnection!=null)
//                    httpURLConnection.disconnect();
//            }
//            return bitmap;
//        }
//
//        @Override
//        protected Bitmap doInBackground(String... strings) {
//            Bitmap b = createBitmapFromURL(strings[0]);
//            return b;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap b){
//            v.Icon.setImageBitmap(b);
//        }
//    }

    private class agree extends AsyncTask<Integer,Void,Void>{

        @Override
        protected Void doInBackground(Integer... params) {
            updatedb(params[0],params[1]);
            return null;
        }

        private void updatedb(final int x, final int y){
            String url = "http://192.168.43.58/androiduploads/uploadvote.php?apicall=agree";
            Log.e("Agrees(updateDB)",""+x);
            Log.e("ID(updateDB)",""+y);
                    StringRequest request=new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>(){
                                @Override
                                public  void onResponse(String response) {
                                    try{
                                        Log.e("response",response);
                                        JSONObject ob = new JSONObject(response);
                                        String message = ob.getString("message");
                                        Log.e("updateDB",message);
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            //params.put("image",encodedImage );
                            prefManager = new PrefManager(mContext);
                            params.put("agrees",""+x);
                            params.put("id",""+y);
                            params.put("user_id",prefManager.getUserId());
                            return params;
                        }
                    };

            //VolleySingleton.getInstance(mContext).addToRequestQueue(request);
            Volley.newRequestQueue(mContext).add(request);
        }
    }

}