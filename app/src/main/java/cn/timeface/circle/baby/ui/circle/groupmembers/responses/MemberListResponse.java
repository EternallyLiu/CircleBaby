package cn.timeface.circle.baby.ui.circle.groupmembers.responses;


import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.MenemberInfo;

/**
 * Created by wangwei on 2017/3/21.
 */

public class MemberListResponse extends BaseResponse {
   private List<MenemberInfo> teachers;
   private List<MenemberInfo> applicants;
   private List<MenemberInfo> normals;

    public List<MenemberInfo> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<MenemberInfo> teachers) {
        this.teachers = teachers;
    }

    public List<MenemberInfo> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<MenemberInfo> applicants) {
        this.applicants = applicants;
    }

    public List<MenemberInfo> getNormals() {
        return normals;
    }

    public void setNormals(List<MenemberInfo> normals) {
        this.normals = normals;
    }

    public MemberListResponse(List<MenemberInfo> teachers, List<MenemberInfo> applicants, List<MenemberInfo> normals) {
        this.teachers = teachers;
        this.applicants = applicants;

        this.normals = normals;
    }
}
