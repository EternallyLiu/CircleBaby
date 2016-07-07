package cn.timeface.circle.baby.api.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;

import java.util.List;

/**
 * @author SunYanwei (QQ:707831837)
 * @from 2014年9月11日下午2:02:38
 * @TODO 省市区字典
 */
@Table(name = "district_table")
public class DistrictModel extends Model {
    @Column(name = "locationId")
    @PrimaryKey
    public String locationId;

    @Column(name = "locationName")
    public String locationName;

    @Column(name = "locationPid")
    public String locationPid;

    /**
     * 查询省,市,区
     */
    public static DistrictModel query(long proId) {

        return new Select().from(DistrictModel.class).where("locationId = ?", proId)
                .executeSingle();
    }

    public static DistrictModel query(String id) {

        return new Select().from(DistrictModel.class).where("locationId = ?", id)
                .executeSingle();
    }

    public static DistrictModel queryByName(String name) {

        return new Select().from(DistrictModel.class).where("locationName = ?", name)
                .executeSingle();
    }

    public static List<DistrictModel> queryDicts() {
        return new Select().from(DistrictModel.class).where("locationPid = ?", "0")
                .execute();
    }

    public static List<DistrictModel> queryDictsByParentId(String id) {
        return new Select().from(DistrictModel.class).where("locationPid = ?", id)
                .execute();
    }

    public static void deleteAll() {
        new Delete().from(DistrictModel.class).execute();
    }

}
