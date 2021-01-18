package com.example.weather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;
    private Button button;
    private TextView textViewDescription;

    private String api = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=f8eb4467a8f5834415aad1908c7007c3&lang=ru&units=metric";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = findViewById(R.id.editTextCity);
        button = findViewById(R.id.button);
        textViewDescription = findViewById(R.id.textView);

    }

    public void onGetWeather(View view) {
        String city = editTextCity.getText().toString().trim();
        if (!city.isEmpty()){
            DownloadTask task = new DownloadTask();
            String url = String.format(api, city);
            task.execute(url);
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();

                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }

                Log.i("MyData", result.toString());
                return result.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String desc = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                String weather = String.format("%s\nТемпература %s\nНа улице %s", city, temp, desc);
                textViewDescription.setText(weather);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
