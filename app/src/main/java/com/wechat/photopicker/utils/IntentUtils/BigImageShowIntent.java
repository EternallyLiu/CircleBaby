package com.wechat.photopicker.utils.IntentUtils;


import android.content.Context;
import android.content.Intent;

import com.wechat.photopicker.BigImageShowActivity;

import java.util.ArrayList;

/**
 * Created by yellowstart on 15/12/16.
 */

public class BigImageShowIntent extends Intent {

    public static final String KEY_PHOTO_PATHS = "PHOTO_PATHS";
    public static final String KEY_SELECTOR_POSITION = "SELECTOR_POSITION";

    public BigImageShowIntent(Context packageContext , Class<?> clas){
        super(packageContext,clas);
    }

    public BigImageShowIntent(Context packageContext){
        super(packageContext, BigImageShowActivity.class);
    }

    public void setSelectorPosition(int position){
        this.putExtra(KEY_SELECTOR_POSITION,position);
    }

    public void setPhotoPaths(ArrayList<String> paths) {
        this.putStringArrayListExtra(KEY_PHOTO_PATHS,paths);
    }
}
