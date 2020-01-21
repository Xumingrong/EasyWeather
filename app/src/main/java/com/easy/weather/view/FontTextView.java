package com.easy.weather.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.easy.weather.activity.MainActivity;

public class FontTextView extends TextView {
	public FontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(isInEditMode()){
			return ;
		}
		setTypeface(MainActivity.getTypeface(context));
	}
}
