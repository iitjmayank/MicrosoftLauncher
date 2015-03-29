package com.microsoft.office.microsoftlauncher;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.RemoteViews;

import java.io.File;

public class WidgetProvider extends AppWidgetProvider {
    public static String FILE_OPEN = "fileOpen";
    public static String FILE_PATH = "filePath";
	/* 
	 * this method is called every 30 mins as specified on widgetinfo.xml
	 * this method is also called on every phone reboot
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		final int N = appWidgetIds.length;

		/*int[] appWidgetIds holds ids of multiple instance of your widget
		 * meaning you are placing more than one widgets on your homescreen*/
		for (int i = 0; i < N; ++i) {
			RemoteViews remoteViews = updateWidgetListView(context,
					appWidgetIds[i]);
			appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

		//which layout to show on widget
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		
		//RemoteViews Service needed to provide adapter for ListView
		Intent svcIntent = new Intent(context, WidgetService.class);
		//passing app widget id to that RemoteViews Service
		svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        //setting a unique Uri to the intent
		//don't know its purpose to me right now
		svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        //setting adapter to listview of the widget
        remoteViews.setRemoteAdapter(appWidgetId, R.id.listViewWidget,
                svcIntent);

        //setting an empty view in case of no data
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);

        Intent fileOpenIntent = new Intent( context, WidgetProvider.class);
        fileOpenIntent.setAction(FILE_OPEN);
        fileOpenIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        fileOpenIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, fileOpenIntent, PendingIntent.FLAG_UPDATE_CURRENT );
        remoteViews.setPendingIntentTemplate(R.id.listViewWidget, pendingIntent);

		return remoteViews;
	}

    @Override
    public void onReceive(Context context, Intent intent) {

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if( intent.getAction().equals(FILE_OPEN)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            String filePath = intent.getStringExtra(FILE_PATH);
            Log.i("File Name ", filePath);

            File targetFile = new File(filePath);
            Uri uriPath = Uri.fromFile(targetFile);
            Intent fileOpenIntent = new Intent(Intent.ACTION_VIEW);
            fileOpenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(filePath));
            fileOpenIntent.setDataAndType(uriPath, mimetype);
            fileOpenIntent.setType(mimetype);
            context.startActivity(fileOpenIntent);
        }
       super.onReceive(context,intent);
    }
}
