package com.example.devar.xmovie;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MovieAdapter extends BaseAdapter {
    private Context context;
    public ArrayList<Movie> data;

    @Override
    public int getCount() {
        return data.size();
    }

    public MovieAdapter(Context o,ArrayList<Movie> data) {
        context = o;
        this.data=data;

    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)

            convertView = inflater.inflate(R.layout.item, null);
        ImageView imgView= (ImageView) convertView.findViewById(R.id.img);


        Movie image=data.get(position);

        Picasso.with(context).load("http://image.tmdb.org/t/p/w342" + image.getPoster()).into(imgView);

        return convertView;
    }
}


