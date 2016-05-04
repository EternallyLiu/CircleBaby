package cn.timeface.circle.baby.api.models.objs;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/4/28.
 */
public class BabyObj extends BaseObj {
    String age;
    String avatar;
    int babyId;
    int bithday;
    String blood;
    String constellation;
    int gender;
    String name;

    public BabyObj() {
    }

    public BabyObj(String age, String avatar, int babyId, int bithday, String blood, String constellation, int gender, String name) {
        this.age = age;
        this.avatar = avatar;
        this.babyId = babyId;
        this.bithday = bithday;
        this.blood = blood;
        this.constellation = constellation;
        this.gender = gender;
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getBabyId() {
        return babyId;
    }

    public void setBabyId(int babyId) {
        this.babyId = babyId;
    }

    public int getBithday() {
        return bithday;
    }

    public void setBithday(int bithday) {
        this.bithday = bithday;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
