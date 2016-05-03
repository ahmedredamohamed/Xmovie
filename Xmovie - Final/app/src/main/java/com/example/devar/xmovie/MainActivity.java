package com.example.devar.xmovie;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    boolean tablet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (findViewById(R.id.Dframe) != null) {
            tablet= true;
        }
        else {
            tablet = false;
        }
        if (null == savedInstanceState) {
            if(tablet) {
                DetailActivityFragment detailsfragment = new DetailActivityFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.Dframe, detailsfragment);
                ft.commit();
            }
        }
    }
}
