package com.example.devar.xmovie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ReviewAdapter extends BaseAdapter {
    Context context;
    ArrayList<Review> reviews;
    ReviewAdapter(Context context,ArrayList<Review> list)
    {
        this.context=context;
        this.reviews=list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        TextView author;
        TextView content;
        if(convertView==null)
            convertView = inflater.inflate(R.layout.review, null);



        author = (TextView) convertView.findViewById(R.id.author);
        content = (TextView) convertView.findViewById(R.id.content);
        Review review=reviews.get(position);
        author.setText(review.getAuthor());
        content.setText(review.getContent());
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public int getCount() {
        return reviews.size();
    }
}
