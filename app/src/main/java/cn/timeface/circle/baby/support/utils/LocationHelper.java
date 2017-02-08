package cn.timeface.circle.baby.support.utils;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * @author rayboot
 * @from 14/11/19 09:44
 * @TODO
 */
public class LocationHelper {
    LocationClient mLocationClient;
    Context context;
    BDLocationListener locationListener;

    public LocationHelper(Context context, BDLocationListener locationListener) {
        this.context = context;
        this.locationListener = locationListener;
        mLocationClient = new LocationClient(context, getLocationOption());
    }

    public LocationClient getLocationClient() {
        return mLocationClient;
    }

    public void setLocationListener(BDLocationListener locationListener) {
        this.locationListener = locationListener;
        mLocationClient.registerNotifyLocationListener(locationListener);
    }

    public void startLocation() {
        if (this.locationListener == null) {
            throw new IllegalArgumentException("locationListener must not be null.");
        }
        mLocationClient.start();
        mLocationClient.requestLocation();
        mLocationClient.registerLocationListener(locationListener);
    }

    public void stopLocation() {
        mLocationClient.unRegisterLocationListener(locationListener);
        mLocationClient.stop();
//        mLocationClient.cancleError();
    }

    private LocationClientOption getLocationOption() {
        LocationClientOption option = new LocationClientOption();
        //设置定位模式
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //返回的定位结果是百度经纬度,默认值gcj02
        option.setCoorType("gcj02");
        //设置发起定位请求的间隔时间为5000ms
        option.setScanSpan(10000);
        //返回的定位结果包含地址信息
        option.setIsNeedAddress(true);
        //返回的定位结果包含手机机头的方向
        option.setNeedDeviceDirect(true);
        //设置产品线名称
        option.setProdName(context.getPackageName());
        return option;
    }

    //public class MyLocationListener implements BDLocationListener
    //{
    //
    //    @Override
    //    public void onReceiveLocation(BDLocation location) {
    //        //Receive Location
    //        StringBuffer sb = new StringBuffer(256);
    //        sb.append("time : ");
    //        sb.append(location.getTime());
    //        sb.append("\nerror code : ");
    //        sb.append(location.getLocType());
    //        sb.append("\nlatitude : ");
    //        sb.append(location.getLatitude());
    //        sb.append("\nlontitude : ");
    //        sb.append(location.getLongitude());
    //        sb.append("\nradius : ");
    //        sb.append(location.getRadius());
    //        if (location.getLocType() == BDLocation.TypeGpsLocation){
    //            sb.append("\nspeed : ");
    //            sb.append(location.getSpeed());
    //            sb.append("\nsatellite : ");
    //            sb.append(location.getSatelliteNumber());
    //            sb.append("\ndirection : ");
    //            sb.append("\naddr : ");
    //            sb.append(location.getAddrStr());
    //            sb.append(location.getDirection());
    //        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
    //            sb.append("\naddr : ");
    //            sb.append(location.getAddrStr());
    //            sb.append("\noperationers : ");
    //            sb.append(location.getOperators());
    //        }
    //        Log.i("BaiduLocationApiDem", sb.toString());
    //    }
    //
    //
    //}

}
