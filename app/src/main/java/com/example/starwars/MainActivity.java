package com.example.starwars;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";

    //ArrayList data structure to hold JSON objects
    ArrayList<String> quote = new ArrayList<>();
    ArrayList<String> author = new ArrayList<>();
    ArrayList<Integer> delay = new ArrayList<>();

    TextView tvQuote;
    TextView tvAuthor;
    int jsonLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing TextViews with quote and author jsonObjects
        tvQuote = findViewById(R.id.quote);
        tvAuthor = findViewById(R.id.author);

        try {
            //Declaring and initializing jsonArray from assets folder
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset());
            JSONObject jsonObject;
            jsonLength = jsonArray.length();

            //Adding jsonObjects into their corresponding data structure
            for(int i=0; i<jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
                quote.add(jsonObject.getString("quote"));
                author.add(jsonObject.getString("author"));
                delay.add(jsonObject.getInt("slideTransitionDelay"));

                Log.i(TAG,jsonObject.getString("quote"));
                Log.i(TAG,jsonObject.getString("author"));
                Log.i(TAG,jsonObject.getString("slideTransitionDelay"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Creating an instance of the Handler class to update the contents of the TextViews in the same thread
        final int counter = 0;
        final Handler handler = new Handler();

        class MyRunnable implements Runnable{
            private final Handler handler;
            private int counter;
            private final TextView textView;
            private final TextView textView2;

            //Configure the MyRunnable constructor with our counter and textview parameters
            //Counter increments through each jsonObject from the jsonArray
            public MyRunnable(Handler handler, int counter, TextView textView, TextView textView2 ){
                this.handler = handler;
                this.counter = counter;
                this.textView = textView;
                this.textView2 = textView2;
            }

            //Overriding the run method to initialize our TextViews within the specified time
            @Override
            public void run() {
                //Causes the runnable to be added in the message queue after specified amount of time(milliseconds)
                this.handler.postDelayed(this, delay.get(counter));
                this.textView.setText(quote.get(counter));
                this.textView2.setText(author.get(counter));
                this.counter++;

                //If we reach the end of the jsonArray, start from the beginning
                if(counter == jsonLength){
                    counter = 0;
                }
            }
        }
        //Calls our handler with our counter and textView parameters
        handler.post(new MyRunnable(handler, counter, tvQuote, tvAuthor));
    }

    //Load the JSON array from the asset file
    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("Star_Wars.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}