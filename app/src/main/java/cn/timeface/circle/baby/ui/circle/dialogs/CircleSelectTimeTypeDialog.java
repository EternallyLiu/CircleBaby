package cn.timeface.circle.baby.ui.circle.dialogs;

import android.support.v4.app.DialogFragment;

/**
 * 选择圈时光类型dialog
 * author : sunyanwei Created on 17-3-20
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectTimeTypeDialog extends DialogFragment {

    public static int TIME_TYPE_ALL = 1;//全部
    public static int TIME_TYPE_ME = 2;//按我发布的
    public static int TIME_TYPE_ABOUT_BABY = 3;//按@我宝宝的

    public interface SelectTypeListener{
        void selectTypeAll();
        void selectTypeMe();
        void selectTypeAboutMyBaby();
    }

//    public static CircleSelectTimeTypeDialog newInstance(){
//
//    }
}
