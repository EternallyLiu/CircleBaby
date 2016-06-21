package cn.timeface.circle.baby.api.models.objs;


import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * author: rayboot  Created on 16/1/20.
 * email : sy0725work@gmail.com
 */
public class AddressObj extends BaseObj implements Parcelable {
    String address;
    String area;
    String city;
    String contacts;
    String contactsPhone;
    String id;
    String prov;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeString(this.area);
        dest.writeString(this.city);
        dest.writeString(this.contacts);
        dest.writeString(this.contactsPhone);
        dest.writeString(this.id);
        dest.writeString(this.prov);
    }

    public AddressObj() {
    }

    protected AddressObj(Parcel in) {
        this.address = in.readString();
        this.area = in.readString();
        this.city = in.readString();
        this.contacts = in.readString();
        this.contactsPhone = in.readString();
        this.id = in.readString();
        this.prov = in.readString();
    }

    public static final Creator<AddressObj> CREATOR = new Creator<AddressObj>() {
        @Override
        public AddressObj createFromParcel(Parcel source) {
            return new AddressObj(source);
        }

        @Override
        public AddressObj[] newArray(int size) {
            return new AddressObj[size];
        }
    };
}
