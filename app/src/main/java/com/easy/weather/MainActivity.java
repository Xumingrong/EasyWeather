package com.easy.weather;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.easy.weather.api.ApiManager;
import com.easy.weather.utils.UiUtil;
import com.easy.weather.view.DynamicWeatherView;
import com.easy.weather.view.WeatherFragmentPagerAdapter;
import com.easy.weather.view.WeatherViewPager;
import com.easy.weather.api.ApiManager.Area;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;//状态栏
    private WeatherViewPager viewPager;//viewPager
    private FloatingActionButton fab;//悬浮键
    private DrawerLayout drawer;
    private NavigationView navigationView;//侧边栏

    public static Typeface typeface;

    public static Typeface getTypeface(Context context) {
        return typeface;
    }

    private DynamicWeatherView weatherView;
    private AlphaAnimation alphaAnimation;


    public WeatherViewPager getViewPager() {
        return viewPager;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        viewPager = (WeatherViewPager) findViewById(R.id.main_viewpager);
        weatherView = (DynamicWeatherView) findViewById(R.id.main_dynamicweatherview);
        fab = findViewById(R.id.fab);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (typeface == null) {
            typeface = Typeface.createFromAsset(getAssets(), "fonts/mxx_font2.ttf");
        }

        if (Build.VERSION.SDK_INT >= 19) {
            viewPager.setPadding(0, UiUtil.getStatusBarHeight(), 0, 0);
        }
        alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(260);
        viewPager.setAnimation(alphaAnimation);
        loadAreaToViewPager();
        initListener();
    }

    //初始化监听事件
    private void initListener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {

                } else if (id == R.id.nav_tools) {

                } else if (id == R.id.nav_share) {

                } else if (id == R.id.nav_send) {

                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
    }

    public void loadAreaToViewPager() {
        final ArrayList<Area> selectedAreas = ApiManager.loadSelectedArea(this);
        final BaseFragment[] fragments = new BaseFragment[selectedAreas.size() + 1];
        for (int i = 0; i < selectedAreas.size(); i++) {
            final Area area = selectedAreas.get(i);
            fragments[i] = WeatherFragment.makeInstance(area, ApiManager.loadWeather(this, area.id));
        }
        viewPager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOnPageChangeListener(new WeatherViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                weatherView.setDrawerType(((SimpleFragmentPagerAdapter) viewPager.getAdapter()).getItem(
                        position).getDrawerType());
            }
        });
        viewPager.setCurrentItem(1, false);
    }

    public void updateCurDrawerType() {
        weatherView.setDrawerType(((SimpleFragmentPagerAdapter) viewPager.getAdapter()).getItem(
                viewPager.getCurrentItem()).getDrawerType());
    }

    @Override
    protected void onResume() {
        super.onResume();
        weatherView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        weatherView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        weatherView.onDestroy();
    }

    public static class SimpleFragmentPagerAdapter extends WeatherFragmentPagerAdapter {
        private BaseFragment[] fragments;

        public SimpleFragmentPagerAdapter(FragmentManager fragmentManager, BaseFragment[] fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }


        @Override
        public BaseFragment getItem(int position) {
            BaseFragment fragment = fragments[position];
            fragment.setRetainInstance(true);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position].getTitle();
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(((Fragment) object).getView());
            super.destroyItem(container, position, object);
        }
    }

    //首页设置功能
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
