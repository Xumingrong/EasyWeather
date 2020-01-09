package com.easy.weather.weather;

import android.content.Context;
import android.graphics.Canvas;


/**
 * 作者：Meteor
 * 日期：2020-01-08
 * tip：未知天气
 */
public class UnknownDrawer extends BaseDrawer {

	public UnknownDrawer(Context context, boolean isNight) {
		super(context, isNight);
	}

	@Override
	public boolean drawWeather(Canvas canvas, float alpha) {
		return true;//这里返回false会出现有时候不刷新的问题
	}


}
