package com.microsoft.office.microsoftlauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

public class LauncherActivity extends Activity {

    ViewPager viewPager;
    PagerAdapter adapter;
    String[] screens;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Get the view from viewpager_main.xml
        setContentView(R.layout.launcher);

        // Generate sample data
        screens = new String[] { "Work Screen", "Home Screen", "Personal Screen"};

        // Locate the ViewPager in viewpager_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager);

        // Pass results to ViewPagerAdapter Class
        adapter = new ScreenAdapter(LauncherActivity.this, screens);
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

        Intent intent = new Intent( this, BackgroundService.class );
        startService(intent);
    }
}