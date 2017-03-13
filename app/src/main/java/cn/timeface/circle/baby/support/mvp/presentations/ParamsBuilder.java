package cn.timeface.circle.baby.support.mvp.presentations;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by JieGuo on 16/9/12.
 */

public abstract class ParamsBuilder {

    private Map<String, String> params = new WeakHashMap<>();

    protected void put(String key, String value) {
        params.put(key, value);
    }

    public Map<String, String> build() {
        return params;
    }
}
