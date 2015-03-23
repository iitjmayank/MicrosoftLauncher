package com.microsoft.office.microsoftlauncher;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by maagarwa on 3/23/2015.
 */
public class ScreenAdapter extends PagerAdapter{

    Context context;
    String[] screen;
    LayoutInflater inflater;

    public ScreenAdapter(Context context, String[] screen) {
        this.context = context;
        this.screen = screen;
    }

    @Override
    public int getCount() {
        return screen.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // Declare Variables
        TextView screenName;



        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.screen, container,
                false);

        // Locate the TextViews in viewpager_item.xml
        screenName = (TextView) itemView.findViewById(R.id.screenName);

        // Capture position and set to the TextViews
        screenName.setText(screen[position]);

        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}
