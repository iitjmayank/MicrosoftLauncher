package com.microsoft.office.microsoftlauncher;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;

/**
 * Created by kailasl on 3/24/2015.
 */
public class CustomDrawerLayout extends DrawerLayout {

    static int DEFAULT_WIDTH = 10;
    public CustomDrawerLayout(Context context)
    {
        super(context);
    }

    public CustomDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomDrawerLayout( Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if( widthMeasureSpec < 0)
            widthMeasureSpec  = DEFAULT_WIDTH;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
