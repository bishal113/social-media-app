package com.example.lenovo.uploadpic.comment;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lenovo.uploadpic.R;

import java.util.ArrayList;

/**
 * Created by Lenovo on 12-06-2018.
 */

public class CommentListAdapter extends ArrayAdapter<CommentObject> {
    public CommentListAdapter(Context context, ArrayList<CommentObject> commentObjects) {
        super(context,0,commentObjects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.comment_lists, parent, false);
        }

        CommentObject object=getItem(position);
        TextView username=(TextView)listItemView.findViewById(R.id.usernamecomments);
        TextView commments=(TextView)listItemView.findViewById(R.id.commenttext);

        username.setText(object.getUsername());
        commments.setText(object.getComment());

        return listItemView;
    }
}
