package com.microsoft.office.microsoftlauncher;

import android.graphics.drawable.Drawable;

/**
 * Created by kailasl on 3/21/2015.
 */
public class AppDetail implements Comparable<AppDetail> {
    CharSequence label;
    CharSequence name;
    Drawable icon;

    @Override
    public int compareTo(AppDetail another) {
       return  label.toString().compareTo(another.label.toString());
    }
}
