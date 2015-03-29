package com.microsoft.office.microsoftlauncher;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.support.v4.app.NotificationCompat;
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
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LauncherActivity extends Activity {

    private static Map<Integer, AddWidgetAdapter> widgetAdapterMap = new HashMap<>();
    public static int REQUEST_PICK_APPWIDGET = 501;
    public static int REQUEST_CREATE_APPWIDGET = 2131361794;
    ViewPager viewPager;
    ScreenAdapter adapter;
    String[] screens;
    Intent backgroundService;

    AppWidgetManager appWidgetManager;
    AppWidgetHost appWidgetHost;
    PackageManager pm;
    LayoutInflater inflater;

    //int APPWIDGET_HOST_ID = 900;
    int AppCount = 4;
    Drawable appIcon[] = new Drawable[AppCount];

    final static String[] packageName = {"com.microsoft.office.word","com.microsoft.office.excel", "com.microsoft.office.powerpoint","com.microsoft.office.lync15"};

    LinearLayout screen2Child;
    LinearLayout work_chid;
    LinearLayout personal_child;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        appIcon[0] = getResources().getDrawable(R.drawable.wordnotinstalled);
        appIcon[1] = getResources().getDrawable(R.drawable.excelnotinstalled);
        appIcon[2] = getResources().getDrawable(R.drawable.powerpointnotinstalled);
        appIcon[3] = getResources().getDrawable(R.drawable.wordnotinstalled);

        pm = getPackageManager();

        //Set AppIcon value if Installed
        setAppIcon();
        // Get the view from viewpager_main.xml
        setContentView(R.layout.launcher);

        appWidgetManager = AppWidgetManager.getInstance(getBaseContext());
        appWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);

        // Locate the ViewPager in viewpager_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        screen2Child = (LinearLayout)screen2.findViewById(R.id.screen2);
        work_chid = (LinearLayout) screen1.findViewById(R.id.work_chid);
        personal_child = (LinearLayout) screen3.findViewById(R.id.personal_child);

        for(AppWidgetProviderInfo info : widgetList){
            //To get the google search box
            Log.w("Widget", info.provider.getClassName());
            if(info.provider.getClassName().equals("com.microsoft.clients.bing.widget.BingWidgetProvider")){
                addHostView(layout, info, true);
            }

            if(info.provider.getClassName().equals("com.android.alarmclock.DigitalAppWidgetProvider")) {
                addHostView(screen2Child,info, true);
            }

            if (info.provider.getClassName().equals("com.google.android.keep.homescreenwidget.MemoryAppWidgetProvider")) {
                addHostView(screen2Child,info, true);
            }

            if (info.provider.getClassName().equals("com.android.calendar.widget.CalendarAppWidgetProvider")) {
                addHostView(work_chid,info, true);
            }

            if (info.provider.getClassName().equals("com.acompli.acompli.InboxWidgetProvider")) {
                addHostView(work_chid,info, true);
            }

            if (info.provider.getClassName().equals("flipboard.widget.FlipboardWidgetSmall")) {
                addHostView(personal_child,info, true);
            }

            if (info.provider.getClassName().equals("com.facebook.katana.FacebookWidgetProvider")) {
                addHostView(personal_child,info, true);
            }

            if (info.provider.getClassName().equals("com.microsoft.office.microsoftlauncher.WidgetProvider")) {
                addHostView(personal_child,info, true);
            }
        }

        GridView productivityView = (GridView)screen1.findViewById(R.id.productivity_bar);
        productivityView.setAdapter(new ImageAdapter(this));
        productivityView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getPackageManager().getLaunchIntentForPackage(packageName[position]);
                if (intent!=null) {
                    startActivity(intent);
                }
                else {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName[position])));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName[position])));
                    }
                }
            }
        });

        backgroundService = new Intent( this, BackgroundService.class );
        startService(backgroundService);
        loadApplicationBar();

        NotifyIfGoogleDocUsed();
    }

    void NotifyIfGoogleDocUsed() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfo = activityManager.getRunningAppProcesses();
        for(int i = 0; i < processInfo.size(); i++)
        {
            if(processInfo.get(i).processName.equals("com.google.android.apps.docs.editors.docs")) {
                sendNotification(LauncherActivity.packageName[0], "word");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        NotifyIfGoogleDocUsed();
    }

    private void loadApplicationBar() {
          GridView  gridView = (GridView) findViewById(R.id.application_bar);

          SidebarHandler sidebarHandler = new SidebarHandler(R.array.quick_launch_apps);
          final List<AppDetail> apps = sidebarHandler.loadApps(getApplicationContext());
          final int mid = apps.size()/2;
          apps.add(mid, null); // Mid position will have apps button
          gridView.setNumColumns(apps.size());
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

                    if( position != mid) {
                        ImageView appIcon = (ImageView) convertView.findViewById(R.id.item_app_icon);
                        appIcon.setImageDrawable(apps.get(position).icon);
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

   int addHostView(final ViewGroup layout, final AppWidgetProviderInfo providerInfo, final boolean replacable) {

       int id = appWidgetHost.allocateAppWidgetId();

       AppWidgetHostView searchView = appWidgetHost.createView(this, id, providerInfo);

       searchView.setAppWidget(id, providerInfo);
       layout.addView(searchView);
       final Activity activity = this;
       final PackageManager manager = getPackageManager();
       if( replacable ) {
           searchView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   int appWidgetId = appWidgetHost.allocateAppWidgetId();
                   widgetAdapterMap.put(appWidgetId, new AddWidgetAdapter(activity, layout, v));
                   selectWidget(appWidgetId);
               }
           });
       }
       appWidgetHost.startListening();
    return id;
   }

   class ImageAdapter extends BaseAdapter {
       private Context mContext;

       public ImageAdapter(Context c) {
           mContext = c;
       }

       public int getCount() {
           return appIcon.length;
       }

       public Object getItem(int position) {
           return null;
       }

       public long getItemId(int position) {
           return 0;
       }

       public View getView(int position, View convertView, ViewGroup parent) {
           ImageView imageView;
           if (convertView == null) {
               // if it's not recycled, initialize some attributes
               View view = inflater.inflate(R.layout.list_item, null,
                       false);
               imageView = (ImageView) view.findViewById(R.id.item_app_icon);

               //imageView.setPadding(8, 8, 8, 8);
           } else {
               imageView = (ImageView) convertView;
           }

           imageView.setImageDrawable(appIcon[position]);
           return imageView;
       }
   }

   void setAppIcon() {
       for (int i = 0; i< AppCount; i++) {
           try{
               if (pm.getApplicationIcon(packageName[i]) != null) {
                   Log.w("PackageManager", packageName[i]);
                   appIcon[i] = pm.getApplicationIcon(packageName[i]);

               }
           }catch (PackageManager.NameNotFoundException ne) {

           }
       }
   }

    void selectWidget(int appWidgetId) {
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, R.id.REQUEST_PICK_APPWIDGET);
    }
    void addEmptyData(Intent pickIntent) {
        ArrayList customInfo = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList customExtras = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        int widgetId = (int) data.getExtras().get(AppWidgetManager.EXTRA_APPWIDGET_ID);
        AddWidgetAdapter addWidgetAdapter = widgetAdapterMap.get(widgetId);

        if (resultCode == RESULT_OK ) {
            if (requestCode == REQUEST_PICK_APPWIDGET) {
                addWidgetAdapter.configureWidget(data);
            } else  {
                addWidgetAdapter.createWidget(data);
            }
        }
        else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                appWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            createWidget(data);
        }
    }


    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
        AppWidgetHostView hostView = appWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        personal_child.addView(hostView);
    }


    @Override
    protected void onStart() {
        super.onStart();
        appWidgetHost.startListening();
    }
    @Override
    protected void onStop() {
        appWidgetHost.stopListening();
        super.onStop();
    }

    public void removeWidget(AppWidgetHostView hostView) {
        appWidgetHost.deleteAppWidgetId(hostView.getAppWidgetId());
        personal_child.removeView(hostView);
    }


    void sendNotification(String packageName, String applicationName ) {

        // Toast.makeText(getApplicationContext(), "Chrome browser is running", Toast.LENGTH_LONG).show();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Set to word icon
        builder.setSmallIcon(R.drawable.wordnotinstalled);
        builder.setContentTitle("Install " + applicationName);
        builder.setContentText("Try Microsoft " + applicationName+"! Tap to install");

        builder.setAutoCancel(true);
        //Pull out the packagenames for the apps you want user to install
        // final String appPackageName = "com.microsoft.amp.apps.bingnews"; // Can also use getPackageName(), as below

        //Create intent to launch the package
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //Add the above intent to pending itent
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pIntent);

        NotificationManager manager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        manager.notify(100, builder.build());
        // startActivity(intent);
    }
}