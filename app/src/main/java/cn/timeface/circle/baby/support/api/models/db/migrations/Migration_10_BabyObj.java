package cn.timeface.circle.baby.support.api.models.db.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import cn.timeface.circle.baby.support.api.models.db.AppDatabase;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj_Table;

/**
 * author : wangshuai Created on 2017/3/6
 * email : wangs1992321@gmail.com
 */
@Migration(version = 10,database = AppDatabase.class)
public class Migration_10_BabyObj extends AlterTableMigration<BabyObj> {
    public Migration_10_BabyObj(Class<BabyObj> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.TEXT,BabyObj_Table.realName.getNameAlias().name());
        addColumn(SQLiteType.INTEGER,BabyObj_Table.showRealName.getNameAlias().name());
    }
}
