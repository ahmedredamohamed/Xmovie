package com.example.devar.xmovie;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment {
    String moviesjsonStr = null;
    GridView gridView;
    String sortType="popular";
ProgressBar progressBar;
    public  ArrayList<Movie> jsmovie (String s) throws JSONException
    {
        ArrayList<Movie> List=new ArrayList<>();
        JSONObject obj =new JSONObject(s);
        JSONArray jsonArray=obj.getJSONArray("results");
        for(int i=0 ;i<jsonArray.length() ; i++){
            JSONObject object=jsonArray.getJSONObject(i);
            int id=object.getInt("id");
            String path= object.getString("poster_path");
            String overview= object.getString("overview");
            String title= object.getString("title");
            String rdata= object.getString("release_date");
            float vote=(float)object.getDouble("vote_average");
            String back= object.getString( "backdrop_path");
            Movie item= new Movie();
            item.setId(id);
            item.setBackground(back);
            item.setDate(rdata);
            item.setOverview(overview);
            item.setPoster(path);
            item.setTitle(title);
            item.setRating(vote);
            List.add(item);
        }
        return List;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        DownaloadMovie downaloadMovie = new DownaloadMovie();
        downaloadMovie.execute();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main,menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id==R.id.favorite)
        {
            favorite db=new favorite(getActivity());
            Cursor cursor=db.Fetch_all();
            if(cursor!=null)
            {
                ArrayList<Movie> data=new ArrayList<>();
                for (int i=0;i<cursor.getCount();i++)
                {
                    Movie temp=new Movie();
                    temp.setId(cursor.getInt(1));
                    temp.setPoster(cursor.getString(2));
                    temp.setTitle(cursor.getString(3));
                    temp.setOverview(cursor.getString(4));
                    temp.setRating(cursor.getFloat(5));
                    temp.setDate(cursor.getString(6));
                    temp.setBackground(cursor.getString(7));
                    cursor.moveToNext();
                    data.add(temp);
                }
                    gridView.setAdapter(new MovieAdapter(getActivity(),data));
               }
            else
            {
                Toast.makeText(getActivity(), "No Favorites", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public View onCreateView (final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_main, container, false);
        gridView=(GridView)v.findViewById(R.id.Grid);
        progressBar=(ProgressBar)v.findViewById(R.id.prog);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie item = (Movie) adapterView.getItemAtPosition(i);
                if ((getActivity().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    DetailActivityFragment detailsFragment = new DetailActivityFragment();
                    Bundle extras = new Bundle();
                    extras.putInt("id",item.getId());
                    extras.putString("poster",item.getPoster());
                    extras.putString("title",item.getTitle());
                    extras.putString("overview",item.getOverview());
                    extras.putString(("date"),item.getDate());
                    extras.putFloat(("rate"),item.getRating());
                    extras.putString("background",item.getBackground());
                    detailsFragment.setArguments(extras);
                    getFragmentManager().beginTransaction().replace(R.id.Dframe, detailsFragment).commit();
                }
                else {
                    Intent intent = new Intent(getActivity(),DetailsActivity.class);
                    intent.putExtra("id",item.getId());
                    intent.putExtra("poster",item.getPoster());
                    intent.putExtra("title", item.getTitle());
                    intent.putExtra("overview", item.getOverview());
                    intent.putExtra("date", item.getDate());
                    intent.putExtra("rate", item.getRating());
                    intent.putExtra("background", item.getBackground());
                    startActivity(intent);
                }
            }
        });
        setHasOptionsMenu(true);
        return v;
    }

    public  class DownaloadMovie extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                String sharedPreferencesString=sharedPreferences.getString("sort","most popular");
                String api_key="56b97ff259acaff235cab79cbd341154";
                if(sharedPreferencesString.equals("top rated")) {
                 sortType="top_rated";
              }
                final String  mUrl =
            "https://api.themoviedb.org/3/movie/"+sortType+"?api_key="+api_key;
                URL url = new URL( mUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                moviesjsonStr = buffer.toString();
            }
            catch (IOException e) {
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (final IOException e) {
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                ArrayList<Movie> list = jsmovie(moviesjsonStr);
                gridView.setAdapter(new MovieAdapter(getActivity().getApplicationContext(),list));
                progressBar.setVisibility(View.GONE);
            }
            catch (Exception ex){
            }
        }
    }
}





