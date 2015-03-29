package com.microsoft.office.microsoftlauncher;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainWidget extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.v("OnUpdate", "OnUpdate method");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTime(context, appWidgetManager), 1, 10000);
    }

    private class MyTime extends TimerTask {
        RemoteViews remoteViews;
        AppWidgetManager appWidgetManager;
        ComponentName thisWidget;
        Context context;
        public MyTime(Context context, AppWidgetManager appWidgetManager) {
            this.context = context;
            this.appWidgetManager = appWidgetManager;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.main_widget);
            thisWidget = new ComponentName(context, MainWidget.class);
        }

        @Override
        public void run() {

            final String  urlString = "http://service.weather.microsoft.com/en-IN/weather/current/17,78?formcode=ANDLSB";

            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {

                        URL url = new URL(urlString);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(10000 /* milliseconds */);
                        conn.setConnectTimeout(15000 /* milliseconds */);
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        // Starts the query
                        conn.connect();
                        InputStream stream = conn.getInputStream();

                        java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
                        String data = s.hasNext() ? s.next() : "";

                        Log.i("Data", data);

                        JSONObject jsonResponse = new JSONObject(data);

                        JSONArray responses = jsonResponse.getJSONArray("responses");

                        JSONObject jsonObject  = responses.getJSONObject(0);

                        JSONArray weather = jsonObject.getJSONArray("weather");

                        JSONObject current = weather.getJSONObject(0);

                        JSONObject objectTemp  = current.getJSONObject("current");
                        String temperature = objectTemp.getString("temp");
                        String cap = objectTemp.getString("cap");

                        Log.i("Temperature ", temperature);
                        Log.i("Cap         ", cap);

                        // Temperature unit

                        JSONObject unit = jsonResponse.getJSONObject("units");

                        String temperatureUnit = unit.getString("temperature");

                        Log.v("Unit", temperatureUnit);

                        Bitmap bitmap;
                        if (cap.contains("Cloudy"))
                            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cloudy);
                        else if (cap.contains("Rainy"))
                            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.rainy);
                        else
                            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sunny);

                        remoteViews.setImageViewBitmap(R.id.image, bitmap);
                        remoteViews.setTextViewText(R.id.temperature, temperature + temperatureUnit );
                        remoteViews.setTextViewText(R.id.location, "Hyderabad");
                        remoteViews.setTextViewText(R.id.sky, cap);
                        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                        stream.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }


    }
}
