package com.example.nqc_app.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.nqc_app.R;


/**
 * Created by User on 2016/11/6.
 */
public class ActionBarTitle extends Application{

    public static Typeface typeface;
    public static void applyFont(ActionBar actionBar, Context context, String title){
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);

        LayoutInflater inflator = LayoutInflater.from(context);
        View v = inflator.inflate(R.layout.actionbar, null);
        typeface =Typeface.createFromAsset(context.getAssets(), "fonts/W4.ttc");
        TextView BarTitle = (TextView)v.findViewById(R.id.Bartitle);
        actionBar.setSubtitle("SubTitle");
        BarTitle.setText(title);
        BarTitle.setTypeface(typeface);
        actionBar.setCustomView(v);
    }
}
