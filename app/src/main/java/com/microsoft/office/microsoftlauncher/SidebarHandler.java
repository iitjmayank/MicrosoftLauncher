package com.microsoft.office.microsoftlauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SidebarHandler  {
    private ListView listView;
    private PackageManager manager;
    private List<AppDetail> apps;
    private  CustomDrawerLayout drawingLayout;
    public void createHandler( final WindowManager windowManager,final WindowManager.LayoutParams params, LayoutInflater inflater) {
        drawingLayout = (CustomDrawerLayout) inflater.inflate(R.layout.activity_sidebar, null);
        listView = (ListView) drawingLayout.findViewById(R.id.left_drawer);
        drawingLayout.setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);
        drawingLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                WindowManager.LayoutParams newParams = new WindowManager.LayoutParams();
                newParams.copyFrom(params);
                newParams.width=150;
               windowManager.updateViewLayout(drawingLayout, newParams);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                windowManager.updateViewLayout(drawingLayout, params);
            }

        });
        loadApps(((View) drawingLayout).getContext());
        loadListView(((View) drawingLayout).getContext());
        addClickListener(((View) drawingLayout).getContext());
        windowManager.addView(drawingLayout, params);
    }

    private void loadApps(Context context) {
        manager = context.getPackageManager();
        apps = new ArrayList<AppDetail>();

        List<String> quickLaunchAppNames = Arrays.asList(context.getResources().getStringArray(R.array.quick_launch_apps));

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(intent, 0);
        for( ResolveInfo ri: availableActivities ) {
            if(!quickLaunchAppNames.contains(ri.activityInfo.packageName))
                continue;
            AppDetail appDetail = new AppDetail();
            appDetail.label = ri.loadLabel(manager);
            appDetail.name = ri.activityInfo.packageName;
            appDetail.icon = ri.activityInfo.loadIcon( manager );
            apps.add(appDetail);
        }
        Collections.sort(apps);
    }


    private void loadListView(final Context context) {
        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(context, R.layout.list_item, apps) {
          @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if( convertView == null ) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
                }

                ImageView appIcon = (ImageView) convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(apps.get(position).icon);

                TextView appLabel = (TextView) convertView.findViewById(R.id.item_app_label);
                appLabel.setText( apps.get(position).label );


                return convertView;
            }
        };
        listView.setAdapter(adapter);
    }


    private void addClickListener(final Context context){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = manager.getLaunchIntentForPackage(apps.get(position).name.toString());
                context.startActivity(intent);
                drawingLayout.closeDrawer(Gravity.RIGHT);
            }
        });
    }

}
