package com.timothykocik.booklisting;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Fetch Data from Google Books API
 */

public class FetchData extends AsyncTask<String, Void, ArrayList<ResultStrs>> {

    private final String LOG_TAG = FetchData.class.getSimpleName();

    public ArrayList<ResultStrs> getFetchDataFromJson(String fetchJsonStr) throws JSONException {


        JSONObject fetchDataJson = new JSONObject(fetchJsonStr);
        JSONArray itemsArray = fetchDataJson.getJSONArray("items");
        ArrayList<ResultStrs> resultStrs = new ArrayList<ResultStrs>();

        for (int i = 0; i < itemsArray.length(); i++) {

            JSONObject bookList = itemsArray.getJSONObject(i);
            JSONObject volInfo = bookList.getJSONObject("volumeInfo");
            String title = volInfo.getString("title");
            String author = "N/A";
            String publisher = volInfo.optString("publisher");

            if (publisher.equals("")) {
                publisher = "N/A";
            }
            JSONArray authors = volInfo.optJSONArray("authors");
            if (authors != null) {
                authors = volInfo.getJSONArray("authors");
                author = authors.getString(0);
            }

            resultStrs.add(new ResultStrs(title, author, publisher));
        }
        return resultStrs;
    }


    @Override
    protected ArrayList<ResultStrs> doInBackground(String... params) {

        // Will contain raw JSON response as a String
        String fetchJsonStr = null;
        if (params.length == 0) {
            return null;
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Construct the URL
            final String BOOKS_BASE_URL =
                    "https://www.googleapis.com/books/v1/volumes?";
            final String QUERY_PARAM = "q";
            final String KEY = "key";

            Uri builtUri = Uri.parse(BOOKS_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(KEY, "ADD YOUR API KEY HERE")
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to Google Books API, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.
                return null;
            }
            fetchJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            return getFetchDataFromJson(fetchJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;

    }
}
