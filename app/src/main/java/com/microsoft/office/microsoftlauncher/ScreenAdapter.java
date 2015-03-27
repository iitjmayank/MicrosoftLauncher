package com.microsoft.office.microsoftlauncher;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maagarwa on 3/23/2015.
 */
public class ScreenAdapter extends PagerAdapter{

    Context context;
    List<View> screens = new ArrayList<View>();
    LayoutInflater inflater;

    public ScreenAdapter(Context context) {
        this.context = context;
    }

    public void addScreen(View screen) {
        screens.add(screen);
    }

    @Override
    public int getCount() {
        return screens.size();
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
        View itemView = screens.get(position);

        // Locate the TextViews in viewpager_item.xml
       // screenName = (TextView) itemView.findViewById(R.id.screenName);

        // Capture position and set to the TextViews
       // screenName.setText("Screen" + position);

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
