package com.microsoft.office.microsoftlauncher;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by kailasl on 3/24/2015.
 */
public class BackgroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
               // Shrink the window to wrap the content rather than filling the screen
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                // Display it on top of other application windows, but only for the current user
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                // Dont let it grab input focus
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                // Make underlying application window visible through any transparent parts
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.RIGHT;
        params.width = CustomDrawerLayout.DEFAULT_WIDTH;

        new SidebarHandler().createHandler(windowManager, params,  layoutInflater);
    }
}
