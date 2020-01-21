package com.easy.weather;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.gyf.barlibrary.ImmersionBar;

public abstract class BaseActivity extends AppCompatActivity {
    public InputMethodManager imManager;//输入法Manager
    protected ImmersionBar mImmersionBar;//修改状态栏的背景
    public final String ACTIVITY_SIMPLE_NAME = this.getClass().getSimpleName();//获取当前activity名称
    protected ACTIVITY_STATUS mStatus;//activity当前状态
    protected LayoutInflater mInflater;
    private View mShadowView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayout());
        imManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //初始化沉浸式
        if (isImmersionBarEnabled()) {
            initImmersionBar(R.color.color_5CCC5C, R.color.color_000000, false);
        }
        mInflater = getLayoutInflater();
        initView(savedInstanceState);
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mStatus = ACTIVITY_STATUS.START;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStatus = ACTIVITY_STATUS.PAUSE;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStatus = ACTIVITY_STATUS.RESUME;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStatus = ACTIVITY_STATUS.DESTROY;
        //在BaseActivity里销毁mImmersionBar
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
    }

    //阴影视图
    private void initShadowView() {
        this.mShadowView = new View(this);
        this.mShadowView.setBackgroundColor(getResources().getColor(R.color.color_00000000));
        this.mShadowView.setVisibility(View.GONE);
        this.mShadowView.setClickable(true);
        addContentView(this.mShadowView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * @return: 获取当前activity的状态
     */
    public ACTIVITY_STATUS getCurrentStatus() {
        return this.mStatus;
    }

    /**
     * 根据Uri判定跳转到某个页面
     *
     * @return
     */
    protected Uri getUri() {
        if (getIntent() != null) {
            Uri data = getIntent().getData();
            if (data != null) {
                return data;
            }
//            return Uri.parse("mctag://village");
        }
        return null;
    }

    /**
     * 修改状态栏的背景
     *
     * @param highStatusBarColor
     * @param lowStatusBarColor
     * @param isChange
     */
    protected void initImmersionBar(@ColorRes int highStatusBarColor, @ColorRes int lowStatusBarColor, boolean isChange) {
        //在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this);
        //如果设备支持状态栏变色（背景色默认为白色(可更改,在子类重写initImmersionBar方法，传入第一个参数颜色即可),字体颜色可更改：系统默认为白色；statusBarDarkFont为true时：为黑色；statusBarDarkFont为false时：为白色，本项目大部分地方状态栏字体为黑色，所以默认为true,即为黑色。
        if (ImmersionBar.isSupportStatusBarDarkFont()) {
            mImmersionBar.statusBarDarkFont(isChange)
                    .fitsSystemWindows(true)
                    .statusBarColor(highStatusBarColor)
                    .keyboardEnable(true)
                    .init();
            //如果设备不支持状态栏变色(背景色现在默认为黑色(可更改,在子类重写initImmersionBar方法，传入第二个参数颜色即可)，字体颜色不可更改，所以一直为黑色)
        } else {
            mImmersionBar.fitsSystemWindows(true)
                    .statusBarColor(lowStatusBarColor)
                    .keyboardEnable(true)
                    .init();
        }
    }

    /**
     * 是否可以使用沉浸式
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //监听整个页面，点击空白处隐藏输入法
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                imManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    protected abstract int getContentLayout();

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void initEvent();

    /**
     * 保存activity每个生命周期的状态。
     */
    public static enum ACTIVITY_STATUS {
        START, RESUME, PAUSE, STOP, DESTROY
    }
}
