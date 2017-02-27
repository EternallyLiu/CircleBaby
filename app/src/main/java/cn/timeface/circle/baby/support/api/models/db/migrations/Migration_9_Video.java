package cn.timeface.circle.baby.support.api.models.db.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import cn.timeface.circle.baby.support.api.models.VideoInfo;
import cn.timeface.circle.baby.support.api.models.VideoInfo_Table;
import cn.timeface.circle.baby.support.api.models.db.AppDatabase;

/**
 * author : wangshuai Created on 2017/2/23
 * email : wangs1992321@gmail.com
 */
@Migration(version = 9,database = AppDatabase.class)
public class Migration_9_Video extends AlterTableMigration<VideoInfo>{
    public Migration_9_Video(Class<VideoInfo> table) {
        super(table);
    }


    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.TEXT, VideoInfo_Table.videoUrl.getNameAlias().name());
        addColumn(SQLiteType.INTEGER,VideoInfo_Table.type.getNameAlias().name());
    }
}
