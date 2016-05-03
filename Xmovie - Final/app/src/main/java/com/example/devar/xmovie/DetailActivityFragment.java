package com.example.devar.xmovie;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

public class DetailActivityFragment extends Fragment {
    private ImageView imageView;
    private TextView overviewText;
    private TextView dateText;
    private RatingBar rating;
    ProgressBar progressBar;
    int id;
    String trailerjsonstring;
    String reviewjsonstring;
    ListView listView;
    ListView review;
    favorite db;
    Movie m;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_detailsfragment, container, false);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            id=bundle.getInt("id");
            String poster=bundle.getString("poster");
            String title = bundle.getString("title");
            String image = bundle.getString("background");
            String overview = bundle.getString("overview");
            String date = bundle.getString("date");
            float rate = bundle.getFloat("rate");
            m=new Movie();
            m.setDate(date);
            m.setPoster(poster);
            m.setTitle(title);
            m.setRating(rate);
            m.setOverview(overview);
            m.setBackground(image);
            m.setId(id);
            getActivity().setTitle(title);
            overviewText = (TextView) v.findViewById(R.id.overview);
            dateText = (TextView) v.findViewById(R.id.release);
            rating = (RatingBar) v.findViewById(R.id.rate);
            imageView = (ImageView) v.findViewById(R.id.image);
            overviewText.setText(overview);
            dateText.setText(date);
            rating.setVisibility(View.VISIBLE);
            rating.setRating(rate / 2);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w780/" + image).into(imageView);
            DownloasTrailers downloasTrailers=new DownloasTrailers();
            downloasTrailers.execute(id);
            DownloasReviews downloasReviews=new DownloasReviews();
            downloasReviews.execute(id);
            listView=(ListView) v.findViewById(R.id.trailers);
            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            review=(ListView)v.findViewById(R.id.review);
            review.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            progressBar =(ProgressBar)v.findViewById(R.id.progess);
            progressBar.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Trailer trailer = (Trailer) parent.getItemAtPosition(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("www.youtube.com/watch?v=" + trailer.getKey()));
                    startActivity(Intent.createChooser(intent, "Complete action using"));
                }
            });
            setHasOptionsMenu(true);
        }
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        db=new favorite(getActivity());
        if(id==R.id.fav)
        {
            if(db.ifexist(m.getTitle()))
            {
                Toast.makeText(getActivity(),"Already Favorite",Toast.LENGTH_LONG);

            }
            else
            {
                db.add(m);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public  class DownloasTrailers extends AsyncTask<Integer,Void,Void> {

        @Override
        protected Void doInBackground(Integer... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                String api_key = "56b97ff259acaff235cab79cbd341154";
                final String mUrl =
                        "https://api.themoviedb.org/3/movie/" + voids[0] +"/videos?api_key=" + api_key;
                URL url = new URL(mUrl);
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
                trailerjsonstring = buffer.toString();
            } catch (IOException e) {
                return null;
            } finally {
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
                ArrayList<Trailer> list = jsontotrailer(trailerjsonstring);
                listView.setAdapter(new TrailerAdapter(getActivity().getApplicationContext(), list));
                progressBar.setVisibility(View.GONE);
            }
            catch (Exception ex) {
            }
        }

        public ArrayList<Trailer> jsontotrailer(String s) throws JSONException {
            ArrayList<Trailer> List = new ArrayList<>();
            JSONObject obj = new JSONObject(s);
            JSONArray jsonArray = obj.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String key = object.getString("key");
                String name = object.getString("name");
                Trailer trailer = new Trailer();
                trailer.setKey(key);
                trailer.setName(name);
                List.add(trailer);
            }
            return List;
        }
    }

    public  class DownloasReviews extends AsyncTask<Integer,Void,Void> {

        public ArrayList<Review> jsontoreview(String s) throws JSONException {
            ArrayList<Review> List = new ArrayList<>();
            JSONObject obj = new JSONObject(s);
            JSONArray jsonArray = obj.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String author = object.getString("author");
                String content = object.getString("content");
                Review review = new Review();
                review.setAuthor(author);
                review.setContent(content);
                List.add(review);
            }
            return List;
        }

        @Override
        protected Void doInBackground(Integer... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                String api_key = "56b97ff259acaff235cab79cbd341154";
                final String mUrl =
                        "https://api.themoviedb.org/3/movie/" + voids[0] +"/reviews?api_key=" + api_key;
                URL url = new URL(mUrl);
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
                reviewjsonstring = buffer.toString();
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
                ArrayList<Review> list = jsontoreview(reviewjsonstring);
                review.setAdapter(new ReviewAdapter(getActivity().getApplicationContext(),list));
            }
            catch (Exception ex) {
            }
        }
    }
}