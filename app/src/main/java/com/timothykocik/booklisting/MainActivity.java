package com.timothykocik.booklisting;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button search = (Button) findViewById(R.id.search);

        final int REQUEST_INTERNET_PERMISSION = 1;
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.INTERNET},
                    REQUEST_INTERNET_PERMISSION);
        } else search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText searchText = (EditText) findViewById(R.id.search_text);

                    /*Adding this to try to make keyboard go away after typing*/
                    /*Reference used from: http://www.workingfromhere.com/blog/2011/04/27/close-hide-the-android-soft-keyboard*/
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String searchString = searchText.getText().toString();
                FetchData fetch = new FetchData();
                AsyncTask<String, Void, ArrayList<ResultStrs>> getJsonData = fetch.execute(searchString);
                try {
                    ListView lv = (ListView) findViewById(R.id.list);


                    lv.setEmptyView(findViewById(R.id.empty_text));
                    String[] bookResult = new String[getJsonData.get().size()];
                    for (int i = 0; i < getJsonData.get().size(); i++) {
                        bookResult[i] = String.valueOf(getJsonData.get().get(i).getTitle()) + "\n" +
                                "Author: " + String.valueOf(getJsonData.get().get(i).getAuthor()) + "\n" +
                                "Publisher: " + String.valueOf(getJsonData.get().get(i).getPublisher());
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            MainActivity.this,
                            R.layout.book_list,
                            R.id.book_content,
                            bookResult);
                    lv.setAdapter(arrayAdapter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}