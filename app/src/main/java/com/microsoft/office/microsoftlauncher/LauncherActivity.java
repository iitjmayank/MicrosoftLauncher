package com.microsoft.office.microsoftlauncher;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LauncherActivity extends Activity {

    ViewPager viewPager;
    ScreenAdapter adapter;
    String[] screens;
    Intent backgroundService;

    AppWidgetManager appWidgetManager;
    AppWidgetHost appWidgetHost;

    int APPWIDGET_HOST_ID = 900;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Get the view from viewpager_main.xml
        setContentView(R.layout.launcher);

        appWidgetManager = AppWidgetManager.getInstance(getBaseContext());
        appWidgetHost = new AppWidgetHost(this, APPWIDGET_HOST_ID);

        // Locate the ViewPager in viewpager_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adapter = new ScreenAdapter(LauncherActivity.this);

        View screen1 = inflater.inflate(R.layout.work_screen, null,
                false);

        // Pass results to ViewPagerAdapter Class
        adapter.addScreen(screen1);
        View screen2 = inflater.inflate(R.layout.screen, null,
                false);
        adapter.addScreen(screen2);

        View screen3 = inflater.inflate(R.layout.personal_screen, null,
                false);
        adapter.addScreen(screen3);
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

        List<AppWidgetProviderInfo> widgetList = appWidgetManager.getInstalledProviders();

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.search_bar);
        LinearLayout screen2Child = (LinearLayout)screen2.findViewById(R.id.screen2);
        LinearLayout work_chid = (LinearLayout) screen1.findViewById(R.id.work_chid);

        for(AppWidgetProviderInfo info : widgetList){
            //To get the google search box
            Log.w("Widget", info.provider.getClassName());
            if(info.provider.getClassName().equals("com.microsoft.clients.bing.widget.BingWidgetProvider")){
                addHostView(layout, info);
            }

            if(info.provider.getClassName().equals("com.android.alarmclock.DigitalAppWidgetProvider")) {
                addHostView(screen2Child,info);
            }

            if (info.provider.getClassName().equals("com.google.android.keep.homescreenwidget.MemoryAppWidgetProvider")) {
                addHostView(screen2Child,info);
            }

            if (info.provider.getClassName().equals("com.android.calendar.widget.CalendarAppWidgetProvider")) {
                addHostView(work_chid,info);
            }

            if (info.provider.getClassName().equals("com.acompli.acompli.InboxWidgetProvider")) {
                addHostView(work_chid,info);
            }
        }

        backgroundService = new Intent( this, BackgroundService.class );
        startService(backgroundService);
        loadApplicationBar();
    }

    private void loadApplicationBar() {
          GridView  gridView = (GridView) findViewById(R.id.application_bar);

          SidebarHandler sidebarHandler = new SidebarHandler(R.array.quick_launch_apps);
          final List<AppDetail> apps = sidebarHandler.loadApps(getApplicationContext());
          final int mid = apps.size()/2;
          apps.add(mid, null); // Mid position will have apps button
          ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this, R.layout.list_item, apps) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    if( convertView == null ) {

                        if (position == mid) {
                            convertView = getLayoutInflater().inflate(R.layout.apps_button, null);
                        } else {
                            convertView = getLayoutInflater().inflate(R.layout.list_item, null);
                        }
                    }

                    if( position != 3) {
                        ImageView appIcon = (ImageView) convertView.findViewById(R.id.item_app_icon);
                        //This line is commented because of the crash bug
                       // appIcon.setImageDrawable(apps.get(position).icon);
                    }
                    return convertView;
                }
            };
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(apps.get(position).name.toString());
                    LauncherActivity.this.startActivity(intent);
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(backgroundService);
    }

    public void showApps( View view ) {
        Intent intent = new Intent( this, AppsListActivity.class );
        startActivity(intent);
    }

   void addHostView(ViewGroup layout, AppWidgetProviderInfo providerInfo) {

       int id = appWidgetHost.allocateAppWidgetId();

       AppWidgetHostView searchView = appWidgetHost.createView(this, id, providerInfo);

       searchView.setAppWidget(id, providerInfo);
       layout.addView(searchView);

   }

}