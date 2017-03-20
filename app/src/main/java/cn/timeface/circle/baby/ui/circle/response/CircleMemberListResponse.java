package cn.timeface.circle.baby.ui.circle.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleMemberObj;

/**
 * 圈成员列表response
 * Created by lidonglin on 2017/3/18.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleMemberListResponse extends BaseResponse {
    private List<CircleMemberObj> applicants; //申请入圈的人
    private List<CircleMemberObj> normals;    //普通成员
    private List<CircleMemberObj> teachers;   //老师

    public List<CircleMemberObj> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<CircleMemberObj> applicants) {
        this.applicants = applicants;
    }

    public List<CircleMemberObj> getNormals() {
        return normals;
    }

    public void setNormals(List<CircleMemberObj> normals) {
        this.normals = normals;
    }

    public List<CircleMemberObj> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<CircleMemberObj> teachers) {
        this.teachers = teachers;
    }
}
