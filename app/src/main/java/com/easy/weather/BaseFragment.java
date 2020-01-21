package com.easy.weather;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.easy.weather.activity.MainActivity;
import com.easy.weather.weather.BaseDrawer;

public abstract class BaseFragment extends Fragment {
	public abstract String getTitle();
	public abstract void onSelected();
	public abstract BaseDrawer.Type getDrawerType();
	
	protected void notifyActivityUpdate() {
		if (getUserVisibleHint()) {
			Activity activity = getActivity();
			if (activity != null) {
				((MainActivity) activity).updateCurDrawerType();
			}
		}
	}
	protected void toast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}
}
