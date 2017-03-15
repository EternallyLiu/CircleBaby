package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.ui.circle.bean.CircleContentObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.circle.timelines.bean.ItemObj;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import rx.Observable;

/**
 * author : wangshuai Created on 2017/3/15
 * email : wangs1992321@gmail.com
 */
public class PublishAdapter extends BaseAdapter {

    //item类型
    private static final int ITEM_TYPE_INPUT_CONTENT = -39999;
    private static final int ITEM_TYPE_INPUT_TITLE = -39998;
    private static final int ITEM_TYPE_MEDIA = -39997;
    private static final int ITEM_TYPE_SELECT_ACTIVE = -39996;
    private static final int ITEM_TYPE_SELECT_TIME = -39995;
    private static final int ITEM_TYPE_NOTIFY_TIMELINE = -39994;

    //适配器功能类型
    //圈动态
    public static final int TYPE_TIMELINE = -49999;
    //圈作业
    public static final int TYPE_WORK = -49998;
    public static final int TYPE_SCHOOL = -49997;
    private int type = TYPE_TIMELINE;

    public PublishAdapter(Context activity, int type) {
        super(activity);
        setType(type);
    }

    public PublishAdapter(Context activity, CircleContentObj circleContentObj) {
        super(activity);
        if (circleContentObj != null)
            if (circleContentObj instanceof CircleTimelineObj) {
                initTimeLine((CircleTimelineObj) circleContentObj);
            } else if (circleContentObj instanceof CircleHomeworkObj) {
                initWork((CircleHomeworkObj) circleContentObj);
            } else if (circleContentObj instanceof CircleSchoolTaskObj) {
                initSchool((CircleSchoolTaskObj) circleContentObj);
            }
    }

    public void initSchool(CircleSchoolTaskObj homeworkObj) {
        setType(TYPE_SCHOOL);
        list.add(new ItemObj(ITEM_TYPE_INPUT_TITLE, homeworkObj.getTitle()));
        list.add(new ItemObj(ITEM_TYPE_INPUT_CONTENT, homeworkObj.getContent()));
        for (int i = 0; i < homeworkObj.getMediaList().size(); i++) {
            list.add(new ItemObj(ITEM_TYPE_MEDIA, homeworkObj.getMediaList().get(i)));
        }
        addList(true, list);
    }

    public void initWork(CircleHomeworkObj homeworkObj) {
        setType(TYPE_WORK);
        list.add(new ItemObj(ITEM_TYPE_INPUT_TITLE, homeworkObj.getTitle()));
        list.add(new ItemObj(ITEM_TYPE_INPUT_CONTENT, homeworkObj.getContent()));
        for (int i = 0; i < homeworkObj.getMediaList().size(); i++) {
            list.add(new ItemObj(ITEM_TYPE_MEDIA, homeworkObj.getMediaList().get(i)));
        }
        addList(true, list);
    }

    public void initTimeLine(CircleTimelineObj timelineObj) {
        setType(TYPE_TIMELINE);
        ArrayList<ItemObj> list = new ArrayList<>(0);
        list.add(new ItemObj(ITEM_TYPE_INPUT_TITLE, timelineObj.getTitle()));
        list.add(new ItemObj(ITEM_TYPE_INPUT_CONTENT, timelineObj.getContent()));
        list.add(new ItemObj(ITEM_TYPE_SELECT_ACTIVE, timelineObj.getActivityAlbum()));
        list.add(new ItemObj(ITEM_TYPE_SELECT_TIME, timelineObj.getRecordDate()));
        list.add(new ItemObj(ITEM_TYPE_NOTIFY_TIMELINE, timelineObj.getIsSync()));
        for (int i = 0; i < timelineObj.getMediaList().size(); i++) {
            list.add(new ItemObj(ITEM_TYPE_MEDIA, timelineObj.getMediaList().get(i)));
        }
        addList(true, list);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        switch (viewType) {
            case ITEM_TYPE_INPUT_TITLE:
                return R.layout.circle_publish_input_title_layout;
            case ITEM_TYPE_INPUT_CONTENT:
                return R.layout.circle_publish_input_content_layout;
            case ITEM_TYPE_SELECT_ACTIVE:
                return R.layout.circle_publish_select_active;
            case ITEM_TYPE_SELECT_TIME:
                return R.layout.circle_publish_select_time;
            case ITEM_TYPE_NOTIFY_TIMELINE:
                return R.layout.circle_publish_notify_timeline;
            case ITEM_TYPE_MEDIA:
                return R.layout.circle_publish_media;

        }
        return 0;
    }

    @Override
    public int getViewType(int position) {
        ItemObj itemObj = getItem(position);
        return itemObj.getType();
//        switch (position) {
//            case 0:
//                return ITEM_TYPE_INPUT_TITLE;
//            case 1:
//                return ITEM_TYPE_INPUT_CONTENT;
//            case 2:
//                return getType() == TYPE_TIMELINE ? ITEM_TYPE_SELECT_ACTIVE : ITEM_TYPE_MEDIA;
//            case 3:
//                return getType() == TYPE_TIMELINE ? ITEM_TYPE_SELECT_TIME :
//                        ITEM_TYPE_MEDIA;
//            case 4:
//                return getType() == TYPE_TIMELINE ? ITEM_TYPE_NOTIFY_TIMELINE : ITEM_TYPE_MEDIA;
//            default:
//                return ITEM_TYPE_MEDIA;
//        }
    }

    @Override
    public void initView(View contentView, int position) {

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
