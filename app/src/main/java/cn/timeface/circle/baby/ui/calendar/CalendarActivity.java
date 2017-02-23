package cn.timeface.circle.baby.ui.calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.SelectPhotoActivity;
import cn.timeface.circle.baby.support.api.exception.ResultException;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.CalendarModel;
import cn.timeface.circle.baby.support.mvp.presentations.CalendarPresentation;
import cn.timeface.circle.baby.support.mvp.presenter.CalendarPresenter;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.ui.calendar.dialog.UploadImagesDialog;
import cn.timeface.circle.baby.views.IconText;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import cn.timeface.common.utils.DeviceUtil;
import cn.timeface.open.api.bean.obj.TFOBookContentModel;
import cn.timeface.open.constant.TFOConstant;
import cn.timeface.open.event.ContentChangeEvent;
import cn.timeface.open.event.ContentChangeTempEvent;
import cn.timeface.open.view.BookPodView;
import cn.timeface.circle.baby.ui.calendar.bean.CommemorationDataManger;
import cn.timeface.circle.baby.ui.calendar.dialog.UploadImageProgressDialog;
import cn.timeface.circle.baby.ui.calendar.events.CommemorationAddedEvent;
import cn.timeface.circle.baby.ui.calendar.events.CommemorationDeleteEvent;
import cn.timeface.circle.baby.ui.calendar.events.CommemorationUpdateEvent;
import zhy.com.highlight.HighLight;

//import cn.timeface.support.mvp.model.CalendarModel;

/**
 * 台历编辑界面
 * <p>
 * Created by JieGuo on 16/9/29.
 */

public class CalendarActivity extends BasePresenterAppCompatActivity
        implements CalendarPresentation.View, Toolbar.OnMenuItemClickListener,
        View.OnClickListener, ViewPager.OnPageChangeListener, IEventBus, UploadImagesDialog.Action {

    private static final int REQUEST_CHOOSE = 145;

    @Bind(R.id.book_pod_view)
    BookPodView bookPodView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_left)
    IconText tvLeft;
    @Bind(R.id.tv_month)
    TextView tvMonth;
    @Bind(R.id.tv_right)
    IconText tvRight;
    @Bind(R.id.btn_change_style)
    AppCompatButton btnChangeStyle;
    @Bind(R.id.btn_add_commemoration)
    AppCompatButton btnAddCommemoration;
    @Bind(R.id.rg_sides)
    RadioGroup rgSides;
    @Bind(R.id.stateView)
    TFStateView stateView;

    private CalendarPresentation.Presenter calendarPresenter;
    private CommemorationDataManger dataManger;
    private UploadImageProgressDialog uploadImageProgressDialog;
    private UploadImagesDialog uploadImagesDialog;
    private TFProgressDialog progressDialog;
    private int selectedType = 1;
    private String bookId, remoteId, remoteType;
    private List<String> contentIds = new ArrayList<>();

    public static void open(Context context, int type) {
        open(context, type, "");
    }

    public static void open(Context context, int type, String bookId) {
        open(context, type, bookId, "", "");
    }

    /**
     * 打开一本台历
     *
     * @param context  context
     * @param type     type
     * @param bookId   book id
     * @param remoteId remote id
     */
    public static void open(Context context, int type, String bookId, String remoteId, String remoteType) {
        Intent intent = new Intent(context, CalendarActivity.class);
        intent.putExtra("type", type);
        if (!TextUtils.isEmpty(bookId)) {
            intent.putExtra("bookId", bookId);
        }
        if (!TextUtils.isEmpty(remoteId)) {
            intent.putExtra("remoteId", remoteId);
        }
        if (!TextUtils.isEmpty(remoteType)) {
            intent.putExtra("remoteType", remoteType);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar_2017);
        ButterKnife.bind(this);
        toolbar.setTitle("制作台历");

        toolbar.setNavigationOnClickListener(v -> {
            showCloseDialog();
        });
        toolbar.inflateMenu(R.menu.menu_activity_publish_finish);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setBackgroundColor(Color.TRANSPARENT);

        selectedType = getIntent().getIntExtra("type", 2);
        if (getIntent().hasExtra("bookId")) {
            bookId = getIntent().getStringExtra("bookId");
        }
        if (getIntent().hasExtra("remoteId")) {
            remoteId = getIntent().getStringExtra("remoteId");
        }
        if (getIntent().hasExtra("remoteType")) {
            remoteType = getIntent().getStringExtra("remoteType");
        }
        bookPodView.setHasCoverTop(true);
        //new TimeFaceOpenSDKModel();
        setEvents();

        if (!TextUtils.isEmpty(bookId) && !TextUtils.isEmpty(remoteId)) {
            showProgress();
            loadData();
        } else {
            uploadImagesDialog = UploadImagesDialog.newInstance();
            uploadImageProgressDialog = UploadImageProgressDialog.newInstance();
            uploadImagesDialog.setAction(this);
            uploadImagesDialog.show(getSupportFragmentManager(), "upload.");
            hideLoading();
            hideProgress();
        }

        if (selectedType == 1) {
            View v = ButterKnife.findById(this, R.id.ll_month);
            ViewGroup.LayoutParams lp = v.getLayoutParams();
            if (lp instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) lp).bottomMargin = DeviceUtil.dpToPx(getResources(), 100);
                v.requestLayout();
            }
        }
    }

    private void setEvents() {
        tvLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        btnChangeStyle.setOnClickListener(this);
        btnAddCommemoration.setOnClickListener(this);
        stateView.setOnRetryListener(this::loadData);

        rgSides.getChildAt(0).setSelected(true);
        rgSides.setOnCheckedChangeListener((group, checkedId) -> {
            Log.v(TAG, "checked : " + checkedId);
            if (checkedId == R.id.radio_front) {
                showFrontSide();
            } else if (checkedId == R.id.radio_back) {
                showBackSide();
            }
            rgSides.getChildAt(0).setSelected(checkedId == R.id.radio_front);
            rgSides.getChildAt(1).setSelected(checkedId == R.id.radio_back);

            btnChangeStyle.setEnabled(checkedId == R.id.radio_front);
            if (btnChangeStyle.isEnabled() && bookPodView.getCurrentPageData() != null) {
                String contentId = bookPodView.getCurrentPageData()
                        .get(0).getContentId();
                //setCurrentTemplateSize();
                calendarPresenter.refreshTemplate(contentId);
            } else {
                setCurrentTemplateSize(0, 0);
            }
        });
        bookPodView.addOnPageChangeListener(this);
    }

    private void loadData() {
        showProgress();

        // 主线程上做这个事情太慢啦
        new Thread(() -> {

            if (calendarPresenter == null || dataManger == null) {
                Log.i(TAG, "正在初始化");
                if (calendarPresenter == null) {
                    calendarPresenter = new CalendarPresenter(this);
                }
                if (dataManger == null) {
                    dataManger = CommemorationDataManger.getInstance();
                }
            }

            if (!TextUtils.isEmpty(bookId) && !TextUtils.isEmpty(remoteId)) {

                calendarPresenter.getByRemoteId(remoteId, remoteType, response -> {

                    hideProgress();
                    try {
                        calendarPresenter.loadAllPageTemplate();
                        setupPodViewData();
                    } catch (Throwable e) {
                        showToast("加载数据失败");
                        finish();
                        Log.e(TAG, "error", e);
                    }
                }, throwable -> {
                    if (throwable instanceof ResultException) {
                        showToast(throwable.getMessage());
                    }
                    stateView.showException(throwable);
                    Log.e(TAG, "error", throwable);
                });
            } else {

                calendarPresenter.create(
                        selectedType == 1 ? CalendarModel.BOOK_TYPE_CALENDAR_HORIZONTAL
                                : CalendarModel.BOOK_TYPE_CALENDAR_VERTICAL,
                        response -> {
                            hideProgress();
                            try {
                                setupPodViewData();
                                showGuide();
                            } catch (Throwable e) {
                                showToast("加载数据失败");
                                finish();
                                Log.e(TAG, "error", e);
                            }
                        }, throwable -> {
                            if (throwable instanceof ResultException) {
                                showToast(throwable.getMessage());
                            }
                            stateView.showException(throwable);
                            Log.e(TAG, "error", throwable);
                        });
            }
        }).start();
    }

    private void setupPodViewData() throws Throwable {
        bookPodView.setupPodData(getSupportFragmentManager(),
                calendarPresenter.getFrontSide(), true);

        calendarPresenter.getOriginalModel().setPageScale(
                calendarPresenter.getFrontSide().getMyViewScale()
        );
        calendarPresenter.getBackSide().setPageScale(
                calendarPresenter.getFrontSide().getMyViewScale()
        );
        bookPodView.notifyDataSetChanged();
        tvMonth.setText("1月");
    }

    private synchronized void showProgress() {
        stateView.setVisibility(View.VISIBLE);
        stateView.loading();
    }

    private synchronized void hideProgress() {
        try {
            stateView.finish();
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
    }

    private void showFrontSide() {
        int index = bookPodView.getCurrentIndex();
        bookPodView.setupPodData(getSupportFragmentManager(),
                calendarPresenter.getFrontSide(), true);
        bookPodView.setCurrentIndex(index);
    }

    private void showBackSide() {
        int index = bookPodView.getCurrentIndex();
        bookPodView.setupPodData(getSupportFragmentManager(),
                calendarPresenter.getBackSide(), true);
        bookPodView.setCurrentIndex(index);
    }

    @Override
    public void setCurrentTemplateSize(int current, int count) {

        if (current == 0 && count == 0) {
            btnChangeStyle.setText("切换版式");
            btnChangeStyle.setEnabled(false);
        } else {
            btnChangeStyle.setText(
                    String.format(Locale.CHINESE, "切换版式（%s/%s）", current + 1, count)
            );
            btnChangeStyle.setEnabled(true);
        }
    }

    @Override
    public void refreshView() {
        boolean isFrontSide = ButterKnife.findById(this, R.id.radio_front).isSelected();
        int lastIndex = bookPodView.getCurrentIndex();
        bookPodView.setupPodData(
                getSupportFragmentManager(),
                isFrontSide ? calendarPresenter.getFrontSide() : calendarPresenter.getBackSide(),
                true
        );
        bookPodView.setCurrentIndex(lastIndex);
        bookPodView.notifyDataSetChanged();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.menu_complete:
                    calendarPresenter.save();
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tv_left:
                    bookPodView.clickPre();
                    break;

                case R.id.tv_right:
                    bookPodView.clickNext();
                    break;

                case R.id.btn_change_style: {
                    String contentId = bookPodView.getCurrentPageData()
                            .get(0).getContentId();
                    calendarPresenter.changeTemplate(contentId);
                }
                break;

                case R.id.btn_add_commemoration:
                    if (calendarPresenter.getOriginalModel() != null) {
                        int month = bookPodView.getCurrentIndex() + 1;
                        if (TextUtils.isEmpty(calendarPresenter.getOriginalModel().getBookId())) {
                            Log.e(TAG, "error : book id is empty.");
                            CommemorationActivity.open(this, month, "");
                        } else {
                            CommemorationActivity.open(this, month,
                                    calendarPresenter.getOriginalModel().getBookId());
                        }
                    }
                    break;

                default:
                    break;
            }
        } catch (Throwable e) {
            Log.e(TAG, "error", e);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tvMonth.setText(String.format(Locale.CHINESE, "%s月", (position + 1)));
        try {
            TFOBookContentModel contentModel =
                    bookPodView.getCurrentPageData().get(0);
            calendarPresenter.refreshTemplate(contentModel.getContentId());
        } catch (Exception e) {
            Log.e(TAG, "change template index in current page error", e);
        }

        tvLeft.setEnabled(true);
        tvRight.setEnabled(true);
        if (position == 0) {
            tvLeft.setEnabled(false);
        } else if (position == bookPodView.getPageCount() - 1) {
            tvRight.setEnabled(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (calendarPresenter != null) {
            calendarPresenter = null;
        }
        if (dataManger != null) {
            dataManger.destroy();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (dataManger != null) {
            dataManger.destroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TFOConstant.EDIT_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                calendarPresenter.editImage(
                        bookPodView.getCurrentPageData().get(0),
                        data
                );
            } catch (Throwable e) {
                Log.e(TAG, "error", e);
            }
        } else if (requestCode == TFOConstant.EDIT_TEXT && resultCode == RESULT_OK && data != null) {

            try {
                calendarPresenter.editText(
                        bookPodView.getCurrentPageData().get(0),
                        data
                );
            } catch (Throwable e) {
                Log.e(TAG, "error", e);
            }
        } else if (requestCode == REQUEST_CHOOSE && resultCode == RESULT_OK && data != null) {

            try {
                onChooseImageResult(data);
            } catch (Exception e) {
                Log.e(TAG, "error", e);
            }
        }
    }

    @Override
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = TFProgressDialog.getInstance(
                    getString(R.string.loading)
            );
        }
        progressDialog.show(getSupportFragmentManager(), "loading");
    }

    @Override
    public void hideLoading() {
        if (progressDialog == null) {
            return;
        }

        progressDialog.dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onAddCommemoration(CommemorationAddedEvent event) {
        if (calendarPresenter != null) {
            calendarPresenter.addCommemorationByDay(
                    bookPodView.getCurrentPageData().get(0),
                    event.getMonth(),
                    event.getDay(),
                    event.getContent()
            );
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    @SuppressWarnings("unused")
    public void onUpdateCommemoration(CommemorationUpdateEvent event) {
        if (calendarPresenter != null) {
            calendarPresenter.updateCommemorationByDay(
                    bookPodView.getCurrentPageData().get(0),
                    event.getOldMonth(), event.getOldDay(), event.getOldContent(),
                    event.getMonth(), event.getDay(), event.getContent()
            );
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    @SuppressWarnings("unused")
    public void onDeleteCommemoration(CommemorationDeleteEvent event) {
        if (calendarPresenter != null) {
            calendarPresenter.deleteCommemorationByDay(
                    bookPodView.getCurrentPageData().get(0),
                    event.getMonth(),
                    event.getDay(),
                    event.getContent()
            );
        }
    }

    @Override
    public int getCurrentPageIndex() {
        if (bookPodView == null) {
            return 0;
        }
        return bookPodView.getCurrentIndex();
    }

    @Override
    public void onFinish() {
        finish();
    }

    @Override
    public void onUpload() {
        SelectPhotoActivity.openForResult(
                this,
                new ArrayList<>(), 12, REQUEST_CHOOSE);
    }

    private void onChooseImageResult(Intent data) throws Exception {
        showProgress();

        // 同步会太慢，所以选择异步
        new Thread(() -> {
            ArrayList<ImgObj> images = data.getParcelableArrayListExtra("result_select_image_list");
            List<PhotoModel> photoModels = new ArrayList<PhotoModel>();
            for(ImgObj imgObj : images){
                photoModels.add(PhotoModel.getPhotoModel(imgObj.getId(), imgObj.getLocalPath(), imgObj.getUrl()));
            }

            if (calendarPresenter == null) {
                calendarPresenter = new CalendarPresenter(this);
            }

            if (photoModels.size() < 1) {
                return;
            } else {
                if (uploadImagesDialog != null) {
                    uploadImagesDialog.dismiss();
                }
            }

            try {
                int type = selectedType == 1 ? CalendarModel.BOOK_TYPE_CALENDAR_HORIZONTAL
                        : CalendarModel.BOOK_TYPE_CALENDAR_VERTICAL;
                calendarPresenter.uploadImageWithProgress(type, photoModels, response -> {
                    hideProgress();
                    hideUploadProgressDialog();
                    try {
                        setupPodViewData();
                    } catch (Throwable e) {
                        showToast("加载数据失败");
                        finish();
                        Log.e(TAG, "error", e);
                    }
                }, throwable -> {
                    stateView.showException(throwable);
                    Log.e(TAG, "error", throwable);
                });
            } catch (Exception e) {
                Log.e(TAG, "error", e);
            }
        }).start();
        //loadData();
    }

    @Override
    public void onDoLatter() {
        showProgress();
        loadData();
    }

    @Override
    public void onBackPressed() {
        showCloseDialog();
    }

    private void showCloseDialog() {
        calendarPresenter.closeActivity();
    }

    public void showUploadProgressDialog() {
        if (uploadImageProgressDialog != null) {
            uploadImageProgressDialog.show(getSupportFragmentManager(), "upload.");
        }
    }

    public void hideUploadProgressDialog() {

        if (uploadImageProgressDialog != null) {
            uploadImageProgressDialog.dismiss();
        }
    }

    private void showGuide() {
        if (FastData.getBoolean("guide:calendar", false)) {
            return;
        }
        HighLight highLight = new HighLight(this);
        HighLight highLight2 = new HighLight(this);

        highLight2.addHighLight(R.id.btn_add_commemoration, R.layout.guide_calendar_2, new HighLight.OnPosCallback() {
            @Override
            public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                marginInfo.leftMargin = rectF.left;
                marginInfo.bottomMargin = DeviceUtil.dpToPx(getResources(), 80);
            }
        }).setClickCallback(() -> {
            highLight2.remove();
            FastData.putBoolean("guide:calendar", true);
        });

        highLight.addHighLight(R.id.btn_change_style, R.layout.guide_calendar_1, new HighLight.OnPosCallback() {
            @Override
            public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                marginInfo.bottomMargin = DeviceUtil.dpToPx(getResources(), 80);
                marginInfo.leftMargin = DeviceUtil.dpToPx(getResources(), 30);
            }
        }).setClickCallback(() -> {
            highLight.remove();
            highLight2.show();
        }).show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        for(String contentId : contentIds){
             EventBus.getDefault().post(new ContentChangeTempEvent(contentId));
        }
        contentIds.clear();
    }

    @Subscribe
    public void onUpdateEvent(ContentChangeEvent event){
        if(!contentIds.contains(event.getContentId())){
            contentIds.add(event.getContentId());
        }
    }

}
