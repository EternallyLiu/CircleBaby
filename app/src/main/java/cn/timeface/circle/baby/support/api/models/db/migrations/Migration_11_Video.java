package cn.timeface.circle.baby.support.api.models.db.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import cn.timeface.circle.baby.support.api.models.VideoInfo;
import cn.timeface.circle.baby.support.api.models.VideoInfo_Table;
import cn.timeface.circle.baby.support.api.models.db.AppDatabase;

/**
 * author : wangshuai Created on 2017/2/23
 * email : wangs1992321@gmail.com
 */
@Migration(version = 11,database = AppDatabase.class)
public class Migration_11_Video extends AlterTableMigration<VideoInfo>{
    public Migration_11_Video(Class<VideoInfo> table) {
        super(table);
    }


    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.INTEGER, VideoInfo_Table.modifiedDate.getNameAlias().name());
        addColumn(SQLiteType.INTEGER,VideoInfo_Table.takenDate.getNameAlias().name());
    }
}
