package com.example.targilim;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?&units=metric&q=";
    final String APP_ID_URL = "&appid=";
    final String APP_ID = "5a50565e4e371c28faa56d856e4e7e98";

    final String IMAGE_URL = "http://openweathermap.org/img/w/";
    final String IMAGE_EXTENSION = ".png";
    final String REPLACE = " ";
    final String REPLACE_WITH = "%20";
    ImageView imageView;
    EditText location;
    TextView result;
    TextView temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.resultIV);
        location = (EditText)findViewById(R.id.locationET);
        result = (TextView)findViewById(R.id.resultTV);
        temperature = (TextView)findViewById(R.id.temperatureTV);

        findViewById(R.id.goBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weather = WEATHER_URL + location.getText().toString()
                        .replaceAll(REPLACE, REPLACE_WITH) + APP_ID_URL + APP_ID;

                new Download().execute(weather);
            }
        });
    }

    public class Download extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {

            BufferedReader input = null;
            HttpURLConnection connection = null;
            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line=input.readLine())!=null){
                    response.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                if (input!=null){
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(connection!=null){
                    connection.disconnect();
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String jsonText) {
            Gson gson = new Gson();

            JSONResponse jsonResponse= gson.fromJson(jsonText, JSONResponse.class);

            if (jsonResponse == null)
                return;

            result.setText( jsonResponse.weather.get(0).description );

            temperature.setText(jsonResponse.main.temp);

            Picasso.with(MainActivity.this).load(
                    IMAGE_URL + jsonResponse.weather.get(0).icon + IMAGE_EXTENSION).into(imageView);
        }
    }
}
