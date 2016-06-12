package cn.timeface.circle.baby.api.models.db;

import android.text.TextUtils;

import java.util.List;

/**
 * @author wxw
 * @from 2015/12/15
 * @TODO 地理位置反解 Model
 */
public class LocationModel {
    /**
     * 结构化地址信息
     */
    private String formatted_address;
    /**
     * 地址元素
     */
    private AddressComponent addressComponent;
    /**
     * poi信息列表
     */
    private List<POI> pois;

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public AddressComponent getAddressComponent() {
        return addressComponent;
    }

    public void setAddressComponent(AddressComponent addressComponent) {
        this.addressComponent = addressComponent;
    }

    public List<POI> getPois() {
        return pois;
    }

    public void setPois(List<POI> pois) {
        this.pois = pois;
    }

    /**
     * 获取景区
     */
    public String getScenic() {
        if (pois != null && pois.size() > 0) {
            String name = pois.get(0).getName();
            return TextUtils.isEmpty(name) ? "" : name;
        } else if (addressComponent != null) {
            return addressComponent.township;
        }
        return "";
    }

    /**
     * 地址元素
     */
    public static class AddressComponent {
        private String province;//坐标点所在省名称
        private String city;//坐标点所在城市名称
        private String citycode;//城市编码
        private String district;//坐标点所在区
        private String adcode;//行政区编码
        private String township;//坐标点所在乡镇/街道（此街道为社区街道，不是道路信息）

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCitycode() {
            return citycode;
        }

        public void setCitycode(String citycode) {
            this.citycode = citycode;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getAdcode() {
            return adcode;
        }

        public void setAdcode(String adcode) {
            this.adcode = adcode;
        }

        public String getTownship() {
            return township;
        }

        public void setTownship(String township) {
            this.township = township;
        }
    }

    /**
     * Point of Interest 信息点
     * 每个POI包含四方面信息，名称、类别、经度纬度、附近的酒店饭店商铺等信息
     */
    public static class POI {
        private String id;//兴趣点id
        private String name;//兴趣点名称
        private String type;//兴趣点类型
        private String direction;//方向
        private String distance;//该POI到请求坐标的距离
        private String location;//坐标点
        private String address;//poi地址信息
        private String poiweight;//

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPoiweight() {
            return poiweight;
        }

        public void setPoiweight(String poiweight) {
            this.poiweight = poiweight;
        }
    }
}
