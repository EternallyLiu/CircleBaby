package cn.timeface.circle.baby.support.api.models.objs;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * 地理位置信息obj
 * author : YW.SUN Created on 2017/2/21
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class LocationInfoObj {
    private String area;
    private String areaDetail;
    private String city;
    private String country;
    private String district;
    private LocationObj location;
    private String province;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaDetail() {
        return areaDetail;
    }

    public void setAreaDetail(String areaDetail) {
        this.areaDetail = areaDetail;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public LocationObj getLocation() {
        return location;
    }

    public void setLocation(LocationObj location) {
        this.location = location;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
