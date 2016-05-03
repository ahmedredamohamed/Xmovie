package com.example.devar.xmovie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class TrailerAdapter extends BaseAdapter {
    private Context context;
    public ArrayList<Trailer> list;
    TrailerAdapter(Context context,ArrayList<Trailer> list)
    {
        this.context=context;
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        TextView text;
        ImageView image;
        if(convertView==null)
            convertView=inflater.inflate(R.layout.trialer,null);

        text=(TextView)convertView.findViewById(R.id.name);
        image=(ImageView) convertView.findViewById(R.id.play);;
        Trailer trailer=list.get(position);
        text.setText(trailer.getName());

        return convertView;
    }
}
