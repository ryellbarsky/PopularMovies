package com.ryellbarsky.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String API_URL = "http://api.themoviedb.org/3/discover/movie";
    private String POSTER_URL = "http://image.tmdb.org/t/p/w185/";
    private String BACKDROP_URL = "http://image.tmdb.org/t/p/w780/";
    private String PARCELABLE_MOVIE_ITEM_EXTRA = "parselableMovieItem";
    private MovieGridViewAdapter moviesAdapter;
    private ArrayList<MovieItem> movies;
    private String API_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Properties properties = new Properties();

        try {
            AssetManager am = getAssets();
            InputStream inputStream = am.open(getString(R.string.properties_file));
            properties.load(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting properties from file", e);
        }
        API_KEY = properties.getProperty("API_KEY");

        setContentView(R.layout.activity_main);

        final GridView movieGridView = (GridView) findViewById(R.id.movie_grid_view);
        movies = new ArrayList<>();
        moviesAdapter = new MovieGridViewAdapter(this, R.layout.movie_grid_item, movies);
        movieGridView.setAdapter(moviesAdapter);

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieItem item = (MovieItem) movieGridView.getAdapter().getItem(position);
                Intent movieDetailIntent = new Intent(MainActivity.this, MovieDetailActivity.class);
                movieDetailIntent.putExtra(PARCELABLE_MOVIE_ITEM_EXTRA, item);
                startActivity(movieDetailIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        movies.clear();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = settings.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_sort_order_default));
        new FetchMoviesTask().execute(sortOrder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Integer> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected Integer doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJson = null;

            Uri.Builder builder = Uri.parse(API_URL).buildUpon();
            builder.appendQueryParameter("sort_by", params[0].equals(getString(R.string.pref_sort_order_popular)) ?
                    getString(R.string.sort_order_param_popularity) : getString(R.string.sort_order_param_rating));
            builder.appendQueryParameter("api_key", API_KEY);

            try {
                URL url = new URL(builder.toString());
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

                if (buffer.length() == 0) {
                    return 0;
                }

                movieJson = buffer.toString();

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "The URL to receive movie data was not properly formed", e);
                return 0;
            } catch (IOException e) {
                Log.e(LOG_TAG, "There was an error reading movie data from the input stream", e);
                return 0;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                populateMovieItemsArrayList(movieJson);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error getting data from JSON", e);
                return 0;
            }
            return 1;
        }


        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                moviesAdapter.populateMovieGridView(movies);
            } else {
                Toast.makeText(MainActivity.this, "Error retrieving movie data", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * This method parses the JSON returned from the API and populates the ArrayList of MovieItems
         * @param movieJsonStr the JSON result from the API
         * @throws JSONException
         */
        private void populateMovieItemsArrayList(String movieJsonStr)
                throws JSONException {

            final String MOVIE_ID = "id";
            final String POSTER_PATH = "poster_path";
            final String TITLE = "original_title";
            final String MOVIE_BACKDROP = "backdrop_path";
            final String MOVIE_RELEASE_DATE = "release_date";
            final String MOVIE_RATING = "vote_average";
            final String MOVIE_SYNOPSIS = "overview";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject movieResult = movieArray.getJSONObject(i);
                MovieItem movie = new MovieItem();
                movie.setMovieId(Integer.parseInt(movieResult.getString(MOVIE_ID)));
                movie.setMoviePosterImageUrl(POSTER_URL + movieResult.getString(POSTER_PATH));
                movie.setMovieTitle(movieResult.getString(TITLE));
                movie.setMovieBackdropImageUrl(BACKDROP_URL + movieResult.getString(MOVIE_BACKDROP));
                movie.setMovieReleaseDate(movieResult.getString(MOVIE_RELEASE_DATE));
                movie.setMovieRating(movieResult.getString(MOVIE_RATING));
                movie.setMovieSynopsis(movieResult.getString(MOVIE_SYNOPSIS));
                movies.add(movie);
            }
        }
    }
}
