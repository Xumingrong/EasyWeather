package com.easy.weather.weather;

import android.content.Context;
import android.graphics.Canvas;


/**
 * 作者：Meteor
 * 日期：2020-01-08 23:07
 * tip：默认天气
 */
public class DefaultDrawer extends BaseDrawer{

	public DefaultDrawer(Context context) {
		super(context, true);
	}

	@Override
	public boolean drawWeather(Canvas canvas, float alpha) {
		return false;
	}

	@Override
	protected int[] getSkyBackgroundGradient() {
		return SkyBackground.BLACK;
	}
}
