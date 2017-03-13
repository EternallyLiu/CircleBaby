package cn.timeface.circle.baby.support.api.models;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.db.AppDatabase;

/**
 * @author SunYanwei (QQ:707831837)
 * @from 2014年9月11日下午2:02:38
 * @TODO 省市区字典
 */
@com.raizlabs.android.dbflow.annotation.Table(database = AppDatabase.class)
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class DistrictModel extends BaseModel {
    @com.raizlabs.android.dbflow.annotation.Column(name = "locationId")
    @PrimaryKey
    String locationId;

    @com.raizlabs.android.dbflow.annotation.Column(name = "locationName")
    String locationName;

    @com.raizlabs.android.dbflow.annotation.Column(name = "locationPid")
    String locationPid;

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationPid() {
        return locationPid;
    }

    public void setLocationPid(String locationPid) {
        this.locationPid = locationPid;
    }

    /**
     * 查询省,市,区
     */
    public static DistrictModel query(String id) {
        return SQLite.select().from(DistrictModel.class).where(DistrictModel_Table.locationId.is(id)).querySingle();

    }

    public static DistrictModel queryByName(String name) {
        return SQLite.select().from(DistrictModel.class).where(DistrictModel_Table.locationName.is(name)).querySingle();
    }

    public static List<DistrictModel> queryDicts() {
        return queryDictsByParentId("0");
    }

    public static List<DistrictModel> queryDictsByParentId(String id) {
        return SQLite.select().from(DistrictModel.class).where(DistrictModel_Table.locationPid.is(id)).queryList();
    }

    public static void deleteAll() {
        SQLite.delete().from(DistrictModel.class).execute();
    }

}
