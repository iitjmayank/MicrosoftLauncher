package com.microsoft.office.microsoftlauncher;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by kailasl on 3/29/2015.
 */
public class AddWidgetAdapter {
    private ViewGroup workingViewGroup;
    private View view;
    private AppWidgetManager appWidgetManager;
    private AppWidgetHost appWidgetHost;
    private Activity workingActivity;
    public static int REQUEST_PICK_APPWIDGET = 501;
    public static int REQUEST_CREATE_APPWIDGET = 2131361794;
    public AddWidgetAdapter(Activity activity, ViewGroup viewGroup, View view) {
        this.workingViewGroup = viewGroup;
        this.view = view;
        this.appWidgetManager = AppWidgetManager.getInstance(viewGroup.getContext());
        this.appWidgetHost = new AppWidgetHost(viewGroup.getContext(), R.id.APPWIDGET_HOST_ID);
        this.workingActivity = activity;
    }


    public void addWidget() {
        selectWidget();
    }

    void selectWidget() {
        int appWidgetId = this.appWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        workingActivity.startActivityForResult(pickIntent, R.id.REQUEST_PICK_APPWIDGET);
    }

    void addEmptyData(Intent pickIntent) {
        ArrayList customInfo = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList customExtras = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }

    public void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            workingActivity.startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            createWidget(data);
        }
    }


    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
        AppWidgetHostView hostView = appWidgetHost.createView(workingViewGroup.getContext(), appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        workingViewGroup.removeView(view);
        workingViewGroup.addView(hostView);
        appWidgetHost.startListening();
    }




    public void removeWidget(AppWidgetHostView hostView) {
        appWidgetHost.deleteAppWidgetId(hostView.getAppWidgetId());
        workingViewGroup.removeView(hostView);
    }


}