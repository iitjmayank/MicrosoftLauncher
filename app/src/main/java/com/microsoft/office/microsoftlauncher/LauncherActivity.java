package com.microsoft.office.microsoftlauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Layout;
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
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Get the view from viewpager_main.xml
        setContentView(R.layout.launcher);

        // Generate sample data
        screens = new String[] { "Work Screen", "Home Screen", "Personal Screen"};

        // Locate the ViewPager in viewpager_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View screen1 = inflater.inflate(R.layout.screen, null,
                false);

        // Pass results to ViewPagerAdapter Class
        adapter = new ScreenAdapter(LauncherActivity.this);
        adapter.addScreen(screen1);
        View screen2 = inflater.inflate(R.layout.screen, null,
                false);
        adapter.addScreen(screen2);

        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

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
                        appIcon.setImageDrawable(apps.get(position).icon);
                    }
                    return convertView;
                }
            };
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent  = getPackageManager().getLaunchIntentForPackage(apps.get(position).name.toString());
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
        startActivity( intent );
    }
}