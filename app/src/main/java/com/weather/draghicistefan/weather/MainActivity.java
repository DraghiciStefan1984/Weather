package com.weather.draghicistefan.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity
{
    private EditText cityEditText;
    private Button weatherButton;
    private TextView weatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        cityEditText= (EditText) findViewById(R.id.cityEditText);
        weatherButton= (Button) findViewById(R.id.weatherButton);
        weatherTextView= (TextView) findViewById(R.id.weatherTextView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void findWeather(View view)
    {
        Log.i("AppInfo", cityEditText.getText().toString());
        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(cityEditText.getWindowToken(), 0);

        try
        {
            String encodedCityName= URLDecoder.decode(cityEditText.getText().toString(), "UTF-8");
            DownloadTask downloadTask=new DownloadTask();
            downloadTask.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=44db6a862fba0b067b1930da0d769e98");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            String result="";
            URL url=null;
            HttpURLConnection urlConnection=null;
            try
            {
                url=new URL(params[0]);
                urlConnection= (HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1)
                {
                    char current= (char) data;
                    result+=current;
                    data=reader.read();
                }
                return result;
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            try
            {
                String message="";
                JSONObject jsonObject=new JSONObject(result);
                String weatherInfo=jsonObject.getString("weather");
                Log.i("Appinfo", weatherInfo);
                JSONArray jsonArray=new JSONArray(weatherInfo);
                for(int i=0; i<jsonArray.length(); i++)
                {
                    JSONObject jsonPart=jsonArray.getJSONObject(i);
                    String main=jsonPart.getString("main");
                    String description=jsonPart.getString("description");
                    if(main!="" && description!="")
                    {
                        message+=main+": "+description;
                    }
                    if(message!="") weatherTextView.setText(message);
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
    }
}
