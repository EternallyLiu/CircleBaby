package cn.timeface.circle.baby.api.models.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * author: rayboot  Created on 15/12/3.
 * email : sy0725work@gmail.com
 */
@Table(databaseName = AppDatabase.NAME)
public class DBSampleTable extends BaseModel {

    @Column
    @PrimaryKey
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
