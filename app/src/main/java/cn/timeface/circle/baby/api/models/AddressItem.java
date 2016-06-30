package cn.timeface.circle.baby.api.models;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.api.models.base.BaseModule;


/**
 * Created by lidonglin on 2016/6/20.
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class AddressItem extends BaseModule {

    /**
     * serial
     */
    private static final long serialVersionUID = 1L;

    private String id;// 数据ID
    private String contacts;// 联系人名称
    private String contactsPhone;// 联系人电话
    private String prov; // 省ID
    private String city; // 市ID
    private String area; // 区ID
    private String address;// 详细收货地址

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContactsPhone() {
        return contactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        this.contactsPhone = contactsPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
