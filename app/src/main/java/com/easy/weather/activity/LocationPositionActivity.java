package com.easy.weather.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.easy.weather.BaseActivity;
import com.easy.weather.R;
import com.mctag.internetgarden.Constant;
import com.mctag.internetgarden.R;
import com.mctag.internetgarden.adapter.CommonAdapter;
import com.mctag.internetgarden.adapter.base.BaseRecyclerViewHelper;
import com.mctag.internetgarden.adapter.base.ViewHolder;
import com.mctag.internetgarden.base.BaseActivity;
import com.mctag.internetgarden.base.MyBaseApplication;
import com.mctag.internetgarden.bean.LocationInfo;
import com.mctag.internetgarden.utils.EdittextFocusUtils;
import com.mctag.internetgarden.utils.SharedPreferenceUtils;
import com.mctag.internetgarden.widget.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 作者：Meteor on 2018/3/13 10:55
 * 邮箱：15537171227@163.com
 * 定位位置选择
 */
public class LocationPositionActivity extends BaseActivity implements View.OnClickListener, AMapLocationListener, PoiSearch.OnPoiSearchListener {
    @BindView(R.id.location_position_back)
    ImageView locationPositionBack;
    @BindView(R.id.location_position_input)
    EditText locationPositionInput;
    @BindView(R.id.location_position_search)
    TextView locationPositionSearch;
    @BindView(R.id.location_position_current_location)
    TextView locationPositionCurrentLocation;
    @BindView(R.id.location_position_rv)
    RecyclerView locationPositionRv;

    private AMapLocationClient mLocationClient = null;////定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private String location;//位置
    private Double lng = 0.0, lat = 0.0;//经度，纬度

    private List<LocationInfo> locationInfoList = new ArrayList<>();
    private CommonAdapter commonAdapter;
    private PoiSearch.Query query;
    private PoiSearch search;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_location_position;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        showProgress();
        loadLocation();

        locationPositionRv.setLayoutManager(new LinearLayoutManager(this));
        locationPositionRv.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    @Override
    protected void initEvent() {
        locationPositionBack.setOnClickListener(this);
        locationPositionInput.setOnClickListener(this);
        locationPositionCurrentLocation.setOnClickListener(this);
        locationPositionSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_position_back:
                LocationPositionActivity.this.finish();
                break;
            case R.id.location_position_input:
                EdittextFocusUtils.findFocusAndShowInputMethodManager(locationPositionInput, imManager);
                break;
            case R.id.location_position_current_location:
                SharedPreferenceUtils.setSharedPreference(this, "lngNearbyLocation", lng.toString().equals("0.0") ? "" : lng.toString());
                SharedPreferenceUtils.setSharedPreference(this, "latNearbyLocation", lat.toString().equals("0.0") ? "" : lat.toString());
                SharedPreferenceUtils.setSharedPreference(this, "streetLocation", location);
                setResult(Constant.MAIN_ACTIVITY_NEARBY_RESULT_CODE);
                LocationPositionActivity.this.finish();
                break;
            case R.id.location_position_search:
                EdittextFocusUtils.clearFocusAndCloseInputMethodManager(locationPositionInput, imManager);
                doSearchQuery();
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            locationPositionCurrentLocation.setText(aMapLocation.getStreet());
            location = aMapLocation.getStreet();
            lat = aMapLocation.getLatitude();//获取纬度
            lng = aMapLocation.getLongitude();//获取经度
            aMapLocation.getAccuracy();//获取精度信息
            query = new PoiSearch.Query("", "生活服务", "");
            query.setPageSize(10);
            search = new PoiSearch(this, query);
            search.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat, lng), 10000));
            search.setOnPoiSearchListener(this);
            search.searchPOIAsyn();

        } else {
            location = "定位失败";
            locationPositionCurrentLocation.setText("定位失败");
            SharedPreferenceUtils.setSharedPreference(this, "lngNearbyLocation", "");
            SharedPreferenceUtils.setSharedPreference(this, "latNearbyLocation", "");
            SharedPreferenceUtils.setSharedPreference(this, "streetLocation", "定位失败");
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        ArrayList<PoiItem> pois = poiResult.getPois();
        locationInfoList.clear();
        for (PoiItem poi : pois) {
            String title = poi.getTitle();
            LocationInfo info = new LocationInfo();
            info.setAddress(title);
            LatLonPoint point = poi.getLatLonPoint();
            info.setLatitude(point.getLatitude());
            info.setLonTitude(point.getLongitude());
            locationInfoList.add(info);
        }
        commonAdapter = new CommonAdapter<LocationInfo>(LocationPositionActivity.this, R.layout.search_village_village_list_item, locationInfoList) {
            @Override
            protected void convert(ViewHolder holder, LocationInfo responseNearbyListBean, int position) {
                holder.setText(R.id.search_village_village_list_item_tv, locationInfoList.get(position).getAddress());//附近地址
            }
        };
        locationPositionRv.setAdapter(commonAdapter);
        commonAdapter.setOnItemClickListener(new BaseRecyclerViewHelper.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                SharedPreferenceUtils.setSharedPreference(LocationPositionActivity.this, "lngNearbyLocation", locationInfoList.get(position).getLonTitude().toString());
                SharedPreferenceUtils.setSharedPreference(LocationPositionActivity.this, "latNearbyLocation", locationInfoList.get(position).getLatitude().toString());
                SharedPreferenceUtils.setSharedPreference(LocationPositionActivity.this, "streetLocation", locationInfoList.get(position).getAddress());
                setResult(Constant.MAIN_ACTIVITY_NEARBY_RESULT_CODE);
                LocationPositionActivity.this.finish();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        commonAdapter.notifyDataSetChanged();
        hideProgress();
    }


    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        String keyWord = locationPositionInput.getText().toString().trim();
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query = new PoiSearch.Query(keyWord, "生活服务", "");
        query.setPageSize(10);// 设置每页最多返回多少条poiItem
        search = new PoiSearch(this, query);
        search.setOnPoiSearchListener(this);
        search.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat, lng), 10000, true));//设置搜索范围
        search.searchPOIAsyn();// 异步搜索
    }

    /**
     * 定位
     */
    private void loadLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(MyBaseApplication.getContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //监听整个页面，点击空白处隐藏输入法
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                imManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                EdittextFocusUtils.clearFocus(locationPositionInput);
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
