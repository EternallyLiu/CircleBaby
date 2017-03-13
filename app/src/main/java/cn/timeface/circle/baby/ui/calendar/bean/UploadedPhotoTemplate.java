package cn.timeface.circle.baby.ui.calendar.bean;

import cn.timeface.circle.baby.support.api.models.db.PhotoModel_Table;
import cn.timeface.open.api.bean.obj.TFOResourceObj;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel;

import android.text.TextUtils;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import static android.R.attr.value;

/**
 * Created by JieGuo on 16/11/10.
 */

public class UploadedPhotoTemplate extends TFOResourceObj {
    private static final String TAG = "UploadedPhotoTemplate";

    public UploadedPhotoTemplate(PhotoModel photo) {
        //草   这个地方photoModel并不是真正的photoModel，草草草，而且高度和宽度可能是相反的
        PhotoModel pm = getOne(photo.getPhotoId(), photo.getLocalPath(), photo.getUrl());

        image_id = "diy/calendar/" + photo.getObjectKey().substring(6);
        image_url = photo.getUrl();
        image_width = pm.getWidth();
        image_height = pm.getHeight();
        image_date = 0;
        image_orientation = pm.getOrientation();
        Log.v(TAG, "image_orientation : ---> " + photo.getOrientation());
    }

    private PhotoModel getOne(String id, String localPath, String url) {
        String value = !TextUtils.isEmpty(id) ? id : (!TextUtils.isEmpty(localPath) ? localPath : (!TextUtils.isEmpty(url) ? url : null));
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        return SQLite.select()
                .from(PhotoModel.class)
                .where(PhotoModel_Table.photo_id.is(value))
                .or(PhotoModel_Table.local_path.is(value))
                .or(PhotoModel_Table.url.is(value))
                .querySingle();
    }
}
