package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.components.RxDialogFragment;
import com.wechat.photopicker.fragment.BigImageFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.ButterKnife;
import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.support.api.Api;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.services.ApiService;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.BaseEmptyAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineExObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.circle.response.CircleIndexInfoResponse;
import cn.timeface.circle.baby.ui.circle.timelines.activity.CircleTimeLineDetailActivitiy;
import cn.timeface.circle.baby.ui.circle.timelines.activity.HomwWorkListActivity;
import cn.timeface.circle.baby.ui.circle.timelines.bean.CircleHomeWorkHeader;
import cn.timeface.circle.baby.ui.images.views.DeleteDialog;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseViewHolder;
import cn.timeface.circle.baby.ui.timelines.adapters.TimeLineGroupListAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;
import cn.timeface.circle.baby.ui.timelines.views.SelectImageView;
import cn.timeface.circle.baby.ui.timelines.views.TimeLineMarker;
import cn.timeface.circle.baby.views.TFStateView;
import rx.Observable;

/**
 * author : wangshuai Created on 2017/3/17
 * email : wangs1992321@gmail.com
 */
public class CircleTimeLineAdapter extends BaseEmptyAdapter {
    private boolean hasWork = false;
    private static final int VIEW_TYPE_HEADER = -49999;
    private static final int VIEW_TYPE_FOOTER = -39999;

    private int paddingImage = 4;
    private int maxImageHeight = 240;
    private DeleteDialog deleteDialog;
    private RecyclerView.LayoutParams emptyLayoutParams;

    private CircleIndexInfoResponse headerInfo;

    public CircleTimeLineAdapter(Context activity) {
        super(activity);
        maxImageHeight = (int) (context().getResources().getDimension(R.dimen.size_120) * 1.5f);
        paddingImage = (int) (context().getResources().getDimension(R.dimen.size_4));
    }

    @Override
    public int getViewLayoutID(int viewType) {
        switch (viewType) {
            case 1:
                return R.layout.circle_time_line_list_layout;
            case 2:
                return R.layout.header_circle_dynamic_list;
        }
        return 0;
    }

    @Override
    public int getViewType(int position) {
        if (getItem(position) instanceof CircleTimelineObj) {
            return 1;
        } else if (getItem(position) instanceof CircleIndexInfoResponse) {
            return 2;
        }
        return 0;
    }

    /**
     * 处理头部
     *
     * @param headerView
     * @param postion
     * @param header
     */
    private void doHeader(View headerView, int postion, CircleIndexInfoResponse header) {
        ImageView ivCircleCover = ViewHolder.getView(headerView, R.id.iv_circle_cover);
        TextView tvCircleName = ViewHolder.getView(headerView, R.id.tv_circle_name);
        TextView tvHomework = ViewHolder.getView(headerView, R.id.tv_homework);
        TextView tvHomeworkDetail = ViewHolder.getView(headerView, R.id.tv_homework_detail);
        RelativeLayout rlHomework = ViewHolder.getView(headerView, R.id.rl_homework);

        tvCircleName.setText(header.getGrowthCircle().getCircleName());
        Glide.with(context())
                .load(header.getGrowthCircle().getCircleCoverUrl())
                .centerCrop()
                .into(ivCircleCover);

        // 圈作业
        if (header.getLastSchoolTask() != null
                && header.getLastSchoolTask().getTeacher() != null) {
            rlHomework.setVisibility(View.VISIBLE);
            tvHomework.setText("“" + header.getLastSchoolTask().getTeacher().getCircleNickName()
                    + "” 发起了新的作业 “" + header.getLastSchoolTask().getTitle() + "”");
            tvHomeworkDetail.setOnClickListener(v -> {
                // 跳转作业详情
                HomwWorkListActivity.open(context());
            });
        } else {
            rlHomework.setVisibility(View.GONE);
        }
    }

    private void doTimeLine(View contentView, int position, CircleTimelineObj timelineObj) {
        TextView tvDateTime = ViewHolder.getView(contentView, R.id.tv_date_time);
        TextView tvTitle = ViewHolder.getView(contentView, R.id.tv_title);
        TextView tvDetail = ViewHolder.getView(contentView, R.id.tv_detail);
        ImageView ivIcon = ViewHolder.getView(contentView, R.id.iv_icon);
        TextView tvName = ViewHolder.getView(contentView, R.id.tv_name);
        TextView tvDelete = ViewHolder.getView(contentView, R.id.tv_delete);
        ImageView ivMessage = ViewHolder.getView(contentView, R.id.iv_message);
        SelectImageView ivLike = ViewHolder.getView(contentView, R.id.iv_like);
        LinearLayout gv = ViewHolder.getView(contentView, R.id.ll_gv);
        TimeLineMarker marker = ViewHolder.getView(contentView, R.id.line);
        TextView picCount = ViewHolder.getView(contentView, R.id.pic_count);
        RelativeLayout rlPicCount = ViewHolder.getView(contentView, R.id.rl_pic_count);
        tvDelete.setTag(R.id.recycler_item_click_tag, timelineObj);
        tvDelete.setOnClickListener(this);
        tvDelete.setVisibility(FastData.getCircleUserInfo().getCircleUserType() == 1 || timelineObj.getPublisher().getCircleUserId() == FastData.getCircleUserId() ? View.VISIBLE : View.GONE);
        int count = doGrid(gv, position, timelineObj.getMediaList());
        if (count < timelineObj.getMediaList().size()) {
            rlPicCount.setVisibility(View.VISIBLE);
            picCount.setText(String.format("共%d张图片  ", timelineObj.getMediaList().size()));
        } else rlPicCount.setVisibility(View.GONE);
        tvDateTime.setText(DateUtil.formatDate("yyyy年MM月dd日 EEEE", timelineObj.getCreateDate()));
        tvTitle.setText(timelineObj.getTitle());
        tvTitle.setVisibility(TextUtils.isEmpty(timelineObj.getTitle()) ? View.GONE : View.VISIBLE);
        tvDetail.setText(timelineObj.getContent());
        tvDetail.setVisibility(TextUtils.isEmpty(timelineObj.getContent()) ? View.GONE : View.VISIBLE);
        tvName.setText(timelineObj.getPublisher().getCircleNickName());
        GlideUtil.displayImageCircle(timelineObj.getPublisher().getCircleAvatarUrl(), ivIcon);
        ivLike.setChecked(timelineObj.getLike() % 2 == 0 ? false : true);
        ivLike.setTag(R.id.recycler_item_click_tag, timelineObj);
        ivLike.setOnClickListener(this);
        marker.setDrawBegin(true);
        marker.setDrawEnd(true);
        if (position == getRealItemSize() - 1) {
            marker.setDrawEnd(false);
        }
        if (position == 0 && isHasWork()) {
            marker.setDrawBegin(true);
        } else if (position == 0) {
            marker.setDrawBegin(false);
        }

    }

    private View getView(int groupIndex, int index, LinearLayout rowLineaylayout, CircleMediaObj mediaObj) {
        View view = inflater.inflate(R.layout.time_line_list_image, rowLineaylayout, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rowLineaylayout.getLayoutParams();
        int width = (mediaObj.getW() * params.height) / mediaObj.getH();
        LinearLayout.LayoutParams itemParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (itemParams == null)
            itemParams = new LinearLayout.LayoutParams(width, params.height);
        else {
            itemParams.height = params.height;
            itemParams.width = width;
        }
        view.setLayoutParams(itemParams);
        imageView.setTag(R.id.recycler_item_input_tag, groupIndex);
        imageView.setTag(R.id.recycler_item_click_tag, index);
        imageView.setOnClickListener(this);
        GlideUtil.displayImage(TextUtils.isEmpty(mediaObj.getLocalPath()) ? mediaObj.getImgUrl() :
                mediaObj.getLocalPath(), imageView);
        return view;
    }

    private int doGrid(LinearLayout gv, int position, List<CircleMediaObj> list) {
        gv.setVisibility(View.GONE);
        if (gv.getChildCount() > 0)
            gv.removeAllViews();
        LinearLayout rowView = null;
        int height = maxImageHeight, width = 0;
        int count = 0;
        int startIndex = count;
        for (int i = 0; i < TimeLineGroupListAdapter.MAX_ROW_COUNT; i++) {
            if (count >= list.size()) {
                break;
            }
            width = 0;
            height = maxImageHeight;
            rowView = new LinearLayout(context());
            rowView.setOrientation(LinearLayout.HORIZONTAL);
            rowView.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for (int k = count; k < list.size(); k++) {
                count++;
                CircleMediaObj mediaObj = list.get(k);
                if (mediaObj.getH() <= 0 || mediaObj.getH() <= 0) {
                    continue;
                }
                if (mediaObj.getH() > height)
                    width += ((mediaObj.getW() * height) / mediaObj.getH());
                else {
                    if (width <= 0) {
                        height = mediaObj.getH();
                        width = mediaObj.getW();
                    } else if (mediaObj.getH() == height) {
                        height = mediaObj.getH();
                        width += (mediaObj.getW());
                    } else {
                        width = width * mediaObj.getH() / height;
                        height = mediaObj.getH();
                        width += (mediaObj.getW());
                    }
                }
                if (list.size() - count == 1) {
                    continue;
                } else {
                    if (width > App.mScreenWidth || count - startIndex >= TimeLineGroupListAdapter.ROW_MAX_COUNT) {
                        break;
                    } else continue;
                }
            }
            height = (App.mScreenWidth * height) / width;
            width = App.mScreenWidth;
            rowParams.height = height;
            rowView.setPadding(0, paddingImage, 0, 0);
            rowView.setLayoutParams(rowParams);
            for (int j = startIndex; j < count; j++) {
                View view = getView(position, j, rowView, list.get(j));
                if (j >= startIndex && j < count - 1) {
                    view.setPadding(0, 0, (startIndex + 1) == count ? 0 : paddingImage, 0);
                } else {
                    view.setPadding(0, 0, 0, 0);
                }
                rowView.addView(view);
            }
            startIndex = count;
            gv.addView(rowView);
        }
        if (gv.getChildCount() > 0) gv.setVisibility(View.VISIBLE);
        else gv.setVisibility(View.GONE);
        return count;
    }

    @Override
    public void addEmpty() {
        addEmpty(1);
    }

    @Override
    public void initView(View contentView, int position) {

        switch (getItemViewType(position)) {
            case 1:
                doTimeLine(contentView, position, getItem(position));
                break;
            case 2:
                doHeader(contentView, position, getItem(position));
                break;
            case BaseEmptyAdapter.EMPTY_CODE:
                TFStateView tfStateView = ViewHolder.getView(contentView, R.id.tf_stateView);
                emptyLayoutParams = (RecyclerView.LayoutParams) contentView.getLayoutParams();
                if (emptyLayoutParams == null) {
                    emptyLayoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                contentView.setLayoutParams(emptyLayoutParams);
                switch (getEmptyItem().getOperationType()) {
                    case -1:
                        tfStateView.showException(getEmptyItem().getThrowable());
                        break;
                    case 0:
                        tfStateView.empty(R.string.circle_no_dynamic, R.drawable.ic_dynamic_empty);
                        break;
                    case 1:
                        tfStateView.loading();
                        break;
                    case 2:
                        tfStateView.finish();
                        break;
                }
                break;
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_like:
                v.setEnabled(false);
                Observable.defer(() -> Observable.just((CircleTimelineObj) v.getTag(R.id.recycler_item_click_tag)))
                        .flatMap(circleTimelineObj -> ApiFactory.getApi().getApiService().circleLike(circleTimelineObj.getCircleTimelineId(), circleTimelineObj.getLike() == 0 ? 1 : 0))
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .doOnNext(baseResponse -> v.setEnabled(true))
                        .subscribe(baseResponse -> {
                            if (baseResponse.success()) {
                                SelectImageView imageView = (SelectImageView) v;
                                imageView.setChecked(!imageView.isChecked());
                            }
                        }, throwable -> {
                            if (v != null)
                                v.setEnabled(true);
                        });
                break;
            case R.id.icon:
                Observable.defer(() -> Observable.just(v))
                        .map(view -> (int) view.getTag(R.id.recycler_item_click_tag))
                        .subscribe(integer -> {
                            int postion = (int) v.getTag(R.id.recycler_item_input_tag);
                            CircleTimelineObj currentTimeLineObj = getItem(postion);
                            FragmentBridgeActivity.openBigimageFragment(context(), 0, MediaObj.getMediaArray(currentTimeLineObj.getMediaList()), MediaObj.getUrls(currentTimeLineObj.getMediaList()), integer, BigImageFragment.CIRCLE_MEDIA_IMAGE_EDITOR, true, false);
                        }, throwable -> LogUtil.showError(throwable));
                break;
            case R.id.tv_delete:
                if (deleteDialog == null) {
                    deleteDialog = new DeleteDialog(context());
                    deleteDialog.setMessage("您确定删除这条圈动态么？");
                    deleteDialog.setMessageGravity(Gravity.CENTER);
                }
                deleteDialog.setSubmitListener(() -> Observable.defer(() -> Observable.just((CircleTimelineObj) v.getTag(R.id.recycler_item_click_tag)))
                        .filter(circleTimeLineObj -> circleTimeLineObj != null)
                        .map(circleTimeLineObj -> circleTimeLineObj.getCircleTimelineId())
                        .flatMap(aLong -> ApiFactory.getApi().getApiService().deleteCircleTimeLine(aLong))
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(baseResponse -> {
                            if (baseResponse.success()) {
                                deleteItem(v.getTag(R.id.recycler_item_click_tag));
                            } else {
                                ToastUtil.showToast(context(), baseResponse.getInfo());
                            }
                        }, throwable -> LogUtil.showError(throwable)));
                if (!deleteDialog.isShowing())
                    deleteDialog.show();
                break;
            default:
                Observable.defer(() -> Observable.just((Integer) v.getTag(R.id.recycler_item_click_tag)))
                        .filter(integer -> integer >= 0)
                        .map(integer -> getItem(integer))
                        .subscribe(o -> CircleTimeLineDetailActivitiy.open(context(), (CircleTimelineObj) o), throwable -> LogUtil.showError(throwable));
                break;
        }
    }

    @Override
    public void addList(boolean isClear, List list) {
        if (isClear && getHeaderInfo() != null && !list.contains(getHeaderInfo()))
            list.add(0, getHeaderInfo());
        super.addList(isClear, list);
    }

    public CircleIndexInfoResponse getHeaderInfo() {
        return headerInfo;
    }

    public void setHeaderInfo(CircleIndexInfoResponse headerInfo) {
        this.headerInfo = headerInfo;
    }

    public boolean isHasWork() {
        return hasWork;
    }

    public void setHasWork(boolean hasWork) {
        this.hasWork = hasWork;
    }
}
