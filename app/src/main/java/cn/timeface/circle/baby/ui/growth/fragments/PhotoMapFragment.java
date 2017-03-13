package cn.timeface.circle.baby.ui.growth.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.LocationObj;
import cn.timeface.circle.baby.support.api.models.objs.MarkOptionPhoto;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaWrapObj;
import cn.timeface.circle.baby.support.api.models.responses.QueryPhotoByLocationResponse;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.PhotoMarkerCluster;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.circle.baby.views.dialog.LoadingDialog;
import rx.Observable;

/**
 * 展示图片的地理位置的信息
 */
public class PhotoMapFragment extends BasePresenterFragment implements AMap.OnCameraChangeListener {
    private AMap aMap;

    @Bind(R.id.map)
    MapView mapView;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;

    //声明AMapLocationClient类对象
    public AMapLocationClient locationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption locationOption = null;
    private double lat = 39.91667;
    private double lon = 116.41667;
    //声明GeocodeSearch对象用于逆编码位置信息
    private GeocodeSearch geoCoderSearch;

    SelectLocation selectLocation;

    private ArrayList<MarkOptionPhoto> photoModels = new ArrayList<>();

    private ArrayList<MarkOptionPhoto> markerOptionsList = new ArrayList<MarkOptionPhoto>();
    private ArrayList<MarkOptionPhoto> markerOptionsListInView = new ArrayList<MarkOptionPhoto>();
    private ArrayList<PhotoMarkerCluster> clustersMarker = new ArrayList<>();

    private int height;
    private int width;
    Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    resetMarks();
                    break;
            }
        }
    };

    public interface SelectLocation{
        void clickLocation(LocationObj locationObj, List<MediaWrapObj> mediaWrapObjs);
    }

    public static PhotoMapFragment newInstance(SelectLocation selectLocation){
        PhotoMapFragment mapFragment = new PhotoMapFragment();
        mapFragment.selectLocation = selectLocation;
        return mapFragment;
    }

    public PhotoMapFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marker_activity, container, false);
        ButterKnife.bind(this, view);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        mapView.onCreate(savedInstanceState); // 此方法必须重写

        //初始化定位
        locationClient = new AMapLocationClient(getContext());

        init();
        reqData();
        return view;
    }

    private void reqData(){
        stateView.setVisibility(View.VISIBLE);
        stateView.loading();
        Observable<QueryPhotoByLocationResponse> photoResponseObservable = apiService.queryPhotoByLocation(FastData.getBabyId());

        if(photoResponseObservable == null) return;
        photoResponseObservable.compose(SchedulersCompat.applyIoSchedulers())
                .doOnUnsubscribe(() -> stateView.finish())
                .subscribe(
                        response -> {
                            if(response.success()){
                                setListData(response.getDataList());
                            } else {
                                ToastUtil.showToast(response.info);
                            }
                        },
                        throwable -> {
                            Log.e(TAG, throwable.getLocalizedMessage());
                        }
                );
    }

    private void setListData(List<MediaObj> mediaObjs){
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 4.5f));
        //开始对PhotoModel进行聚簇
        //为了避免传递对象到Kmeans的工具类中
        photoModels.clear();
        markerOptionsList.clear();
        //计算当前的经纬度坐标到屏幕的坐标的转换
            for (int j = 0; j < mediaObjs.size(); j++) {
                MediaObj curMediaObj = mediaObjs.get(j);
                if (curMediaObj.getLocation() != null && curMediaObj.getLocation().getLat() != 0 && curMediaObj.getLocation().getLog()!= 0) {
                    MarkOptionPhoto markOptionPhoto = new MarkOptionPhoto();
                    markOptionPhoto.setMediaObj(curMediaObj);
                    photoModels.add(markOptionPhoto);

                    //每一张图片最后落实到最后  是自己真实的图片  并且数量是不显示的
                    View view = getActivity().getLayoutInflater().inflate(
                            R.layout.item_map_photo, null);
                    TextView carNumTextView = (TextView) view.findViewById(R.id.tv_map_photo_num);
                    ImageView photoLayout = (ImageView) view
                            .findViewById(R.id.img_map_photo_cover);
                    carNumTextView.setVisibility(View.GONE);
                    //这里面要判断当前的图片的本地的图片是否是存在的
                    try {
                        markOptionPhoto.setMarkerOptions(new MarkerOptions()
                                .position(new LatLng(curMediaObj.getLocation().getLat(), curMediaObj.getLocation().getLog()))
                        );
                        markerOptionsList.add(markOptionPhoto);
                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }
        }
        timeHandler.sendEmptyMessageDelayed(0, 500);
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setOnCameraChangeListener(this);
            aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    LatLng clickLatLng = marker.getPosition();
                    for (PhotoMarkerCluster markerCluster : clustersMarker) {
                        LatLng markerLatlng = markerCluster.getOptions().getPosition();
                        if (clickLatLng.latitude == markerLatlng.latitude &&
                                clickLatLng.longitude == markerLatlng.longitude) {
                            //根据经纬度定位位置信息
                            LatLonPoint latLonPoint = new LatLonPoint(clickLatLng.latitude, clickLatLng.longitude);
                            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
                            LoadingDialog loadingDialog = LoadingDialog.getInstance();
                            loadingDialog.show(getFragmentManager(), TAG);
                            geoCoderSearch = new GeocodeSearch(getContext());
                            geoCoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                                @Override
                                public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                                    loadingDialog.dismiss();
                                    String addressName = "";
                                    if (rCode == 1000) {
                                        if (result != null && result.getRegeocodeAddress() != null
                                                && result.getRegeocodeAddress().getFormatAddress() != null) {
                                            if (aMap.getScalePerPixel() >= 2000) {
                                                addressName = result.getRegeocodeAddress().getProvince();
                                            } else if (aMap.getScalePerPixel() >= 1000) {
                                                addressName = result.getRegeocodeAddress().getProvince();
                                            } else if (aMap.getScalePerPixel() >= 500) {
                                                addressName = result.getRegeocodeAddress().getProvince() +
                                                        result.getRegeocodeAddress().getCity();
                                            } else if (aMap.getScalePerPixel() >= 300){
                                                addressName = result.getRegeocodeAddress().getProvince() +
                                                        result.getRegeocodeAddress().getCity() +
                                                        result.getRegeocodeAddress().getDistrict();
                                            } else {
                                                addressName = result.getRegeocodeAddress().getFormatAddress();
                                            }


                                            /*携带参数跳转到图片编辑做书的界面*/
//                                            ArrayList<PhotoModel> selectedPhotoList = markerCluster.getSelectedPhotoList();
//                                            PhotoTimeSortEvent photoTimeSortEvent = new PhotoTimeSortEvent(PhotoTimeSortEvent.SORT_PHOTO_TIME_MAP,
//                                                    addressName);
                                            //SelectPhotoActivity.open(getActivity(), selectedPhotoList, photoTimeSortEvent);
                                        } else {
//                                            ArrayList<PhotoModel> selectedPhotoList = markerCluster.getSelectedPhotoList();
//                                            PhotoTimeSortEvent photoTimeSortEvent = new PhotoTimeSortEvent(PhotoTimeSortEvent.SORT_PHOTO_TIME_MAP,
//                                                    "");
                                            //SelectPhotoActivity.open(getActivity(), selectedPhotoList, photoTimeSortEvent);
                                        }
                                    } else {
//                                        ArrayList<PhotoModel> selectedPhotoList = markerCluster.getSelectedPhotoList();
//                                        PhotoTimeSortEvent photoTimeSortEvent = new PhotoTimeSortEvent(PhotoTimeSortEvent.SORT_PHOTO_TIME_MAP,
//                                                "");
                                        //SelectPhotoActivity.open(getActivity(), selectedPhotoList, photoTimeSortEvent);


                                        List<MediaObj> selectMedias = markerCluster.getSelectedPhotoList();
                                        StringBuffer sb = new StringBuffer("[");
                                        int index = 0;
                                        for(MediaObj mediaObj : selectMedias){
                                            index++;
                                            if(index < selectMedias.size()){
                                                sb.append(mediaObj.getId());
                                                sb.append(",");
                                            } else {
                                                sb.append("]");
                                            }
                                        }

                                        addSubscription(
                                                apiService.groupPhotoByLocation(sb.toString())
                                                        .compose(SchedulersCompat.applyIoSchedulers())
                                                        .subscribe(
                                                                response -> {
                                                                    if(response.success()){
                                                                        selectLocation.clickLocation(new LocationObj(markerLatlng.latitude, markerLatlng.longitude), response.getDataList());
                                                                    } else {
                                                                        ToastUtil.showToast(response.info);
                                                                    }
                                                                },
                                                                throwable -> {
                                                                    Log.e(TAG, throwable.getLocalizedMessage());
                                                                }
                                                        )
                                        );
                                    }
                                }

                                @Override
                                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                                }
                            });
                            //从定位更新搜索
                            geoCoderSearch.getFromLocationAsyn(query);
                            break;
                        }
                    }
                    return false;
                }
            });
        }
        setUpMap();
    }

    /**
     * 配置定位参数
     */
    private void setUpMap() {
        //初始化定位参数
        locationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        locationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        locationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        locationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        locationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        //locationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        locationClient.setLocationOption(locationOption);
        //启动定位
        locationClient.startLocation();
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationClient.stopLocation();//停止定位
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
        if (locationClient != null) {
            locationClient.onDestroy();//销毁定位客户端。
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (getActivity() != null) {
            timeHandler.sendEmptyMessage(0);
        }
    }

    public void resetMarks() {
        Projection projection = aMap.getProjection();
        Point p = null;
        markerOptionsListInView.clear();
        for (MarkOptionPhoto mp : markerOptionsList) {
            p = projection.toScreenLocation(mp.getMarkerOptions().getPosition());
            if (p.x < 0 || p.y < 0 || p.x > width || p.y > height) {
            } else {
                markerOptionsListInView.add(mp);
            }
        }
        clustersMarker.clear();
        for (MarkOptionPhoto mp : markerOptionsListInView) {
            if (clustersMarker.size() == 0) {
                clustersMarker.add(new PhotoMarkerCluster(getActivity(),
                        mp, projection, 60));
            } else {
                boolean isIn = false;
                for (PhotoMarkerCluster cluster : clustersMarker) {
                    if (cluster.getBounds().contains(mp.getMarkerOptions().getPosition())) {
                        cluster.addMarker(mp);
                        isIn = true;
                        break;
                    }
                }
                if (!isIn) {
                    clustersMarker.add(new PhotoMarkerCluster(
                            getActivity(), mp, projection, 60));
                }
            }
        }
        aMap.clear();
        for (PhotoMarkerCluster mmc : clustersMarker) {
            Log.v("zhangjiaofa", "当前聚簇的个数 = " + mmc
            .getIncludeMarkers().size());
            mmc.setpositionAndIcon(aMap);
        }
    }

}
