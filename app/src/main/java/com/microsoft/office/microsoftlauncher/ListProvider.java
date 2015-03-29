package com.microsoft.office.microsoftlauncher;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 * 
 */
public class ListProvider implements RemoteViewsFactory {
	// private ArrayList<ListItem> listItemList = new ArrayList<ListItem>();
    private List<File> listItemList = new ArrayList<File>();
	private Context context = null;
	private int appWidgetId;

	public ListProvider(Context context, Intent intent) {
		this.context = context;
		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		populateListItem();
	}

	private void populateListItem() {
//		for (int i = 0; i < 10; i++) {
//			ListItem listItem = new ListItem();
//			listItem.heading = "Heading" + i;
//			listItem.content = i
//					+ " This is the content of the app widget listview.Nice content though";
//			listItemList.add(listItem);
//		}

        File file;

        file =  Environment.getExternalStorageDirectory();
//        file = new File( root_sd  ) ;

        Log.i("Path ", file.getAbsolutePath());



        List<File> allFilesList = getAllFiles(file);
        Log.i("File Count", String.valueOf(allFilesList.size()));
        Iterator<File> iter1 = allFilesList.iterator();
        while (iter1.hasNext())
        {
            File childFile = iter1.next();
            String fileName = childFile.getName();
            Log.i("Before Add", fileName);
            if (fileName.endsWith(".pptx") || fileName.endsWith(".docx") || fileName.endsWith(".xlsx"))
                listItemList.add( childFile);
        }

        LastModifiedFileSort(listItemList);

	}

    public List<File> LastModifiedFileSort (List<File> list)
    {
        Collections.sort(list, new Comparator<File>() {
            public int compare(final File f1, final File f2) {
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });
        return list;
    }

    public List<File> getAllFiles(File file) {

        if( !file.exists())
            throw new RuntimeException("File is invalid");
        List<File> files = new ArrayList<File>();

        if( file.isDirectory() )
        {
            Log.i("DIR PATH", file.getAbsolutePath());
            int i = 0;
            File fileList[] = file.listFiles();
            Log.i("FILE COUNT: ", String.valueOf(fileList.length));
            for(File file1: fileList)
                files.addAll(getAllFiles(file1));
        } else
        {
            Log.i("File Path", file.getAbsolutePath());
            files.add(file);
        }
        Log.i("Return File LIst size", String.valueOf(files.size()));
        return files;
    }

	@Override
	public int getCount() {
		return listItemList.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 *Similar to getView of Adapter where instead of View
	 *we return RemoteViews 
	 * 
	 */
	@Override
	public RemoteViews getViewAt(int position) {
		final RemoteViews remoteView = new RemoteViews(
				context.getPackageName(), R.layout.list_row);
		File listItem = listItemList.get(position);
        Date lastMod = new Date(listItem.lastModified());
        String str = "LastModified: " + lastMod + "";
        str = str.substring(0,str.indexOf("GMT"));

		remoteView.setTextViewText(R.id.heading, listItem.getName());
        remoteView.setTextViewText(R.id.content, str);

        if(listItem.getName().endsWith(".pptx")) {
            remoteView.setImageViewResource(R.id.imageView, R.drawable.ic_ppt);
        }
        else if(listItem.getName().endsWith(".docx"))
        {
            remoteView.setImageViewResource(R.id.imageView, R.drawable.ic_word);
        }
        else if(listItem.getName().endsWith(".xlsx"))
        {
            remoteView.setImageViewResource(R.id.imageView, R.drawable.ic_excel);
        }

        return remoteView;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
	}

	@Override
	public void onDestroy() {
	}

}
