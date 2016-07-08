package cn.timeface.open.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.open.R;
import cn.timeface.open.activities.base.BaseAppCompatActivity;
import cn.timeface.open.adapters.TemplateAdapter;
import cn.timeface.open.adapters.base.BaseRecyclerAdapter;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookElementModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.response.EditPod;
import cn.timeface.open.api.models.objs.TFOSimpleTemplate;
import cn.timeface.open.events.ChangeStickerStatusEvent;
import cn.timeface.open.managers.interfaces.IEventBus;
import cn.timeface.open.utils.BookModelCache;
import cn.timeface.open.utils.rxutils.SchedulersCompat;
import cn.timeface.open.views.EditDoubleContentView;
import cn.timeface.open.views.PageView;
import cn.timeface.open.views.StickerView;
import cn.timeface.widget.drawabletextview.DrawableTextView;
import rx.functions.Action1;

public class EditActivity extends BaseAppCompatActivity implements IEventBus {

    public static float SCALE = 1.0f;
    TFOBookModel bookModel;
    TFOBookContentModel rightModel;
    TFOBookContentModel leftModel;
    FrameLayout pod;
    LinearLayout llEditController;
    Point screenInfo;
    float pageScale = 1.f;
    PageView pageView;
    boolean isCover = false;
    DrawableTextView tvEditTemplate;
    DrawableTextView tvBackgroundColor;
    DrawableTextView tvEditLayout;
    DrawableTextView tvEditBg;
    DrawableTextView tvEditPendant;
    DrawableTextView tvEditBeauty;
    RecyclerView rvSelection;
    BaseRecyclerAdapter selectionAdapter;

    public static void open4result(Activity activity, int requestCode, float pageScale, TFOBookContentModel contentModel) {
        open4result(activity, requestCode, pageScale, null, contentModel, false);
    }

    public static void open4result(Activity activity, int requestCode, float pageScale, TFOBookContentModel leftModel, TFOBookContentModel rightModel, boolean isCover) {
        Intent intent = new Intent(activity, EditActivity.class);
        intent.putExtra("right_model", rightModel);
        intent.putExtra("left_model", leftModel);
        intent.putExtra("page_scale", pageScale);
        intent.putExtra("is_cover", isCover);
        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookModel = BookModelCache.getInstance().getBookModel();
        rightModel = getIntent().getParcelableExtra("right_model");
        leftModel = getIntent().getParcelableExtra("left_model");
        pageScale = getIntent().getFloatExtra("page_scale", 1.f);
        this.isCover = getIntent().getBooleanExtra("is_cover", false);

        setContentView(R.layout.activity_edit);
        this.pod = (FrameLayout) findViewById(R.id.pod);
        this.llEditController = (LinearLayout) findViewById(R.id.ll_edit_controller);
        this.tvEditTemplate = (DrawableTextView) findViewById(R.id.tv_edit_template);
        this.tvBackgroundColor = (DrawableTextView) findViewById(R.id.tv_background_color);
        this.tvEditLayout = (DrawableTextView) findViewById(R.id.tv_edit_layout);
        this.tvEditBg = (DrawableTextView) findViewById(R.id.tv_edit_bg);
        this.tvEditPendant = (DrawableTextView) findViewById(R.id.tv_edit_pendant);
        this.tvEditBeauty = (DrawableTextView) findViewById(R.id.tv_edit_beauty);
        this.rvSelection = (RecyclerView) findViewById(R.id.rv_selection);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSelection.setLayoutManager(layoutManager);

        if (!isCover) {
            tvEditTemplate.setVisibility(View.GONE);
            tvBackgroundColor.setVisibility(View.GONE);
        } else {
            tvEditLayout.setVisibility(View.GONE);
            tvEditBg.setVisibility(View.GONE);
            tvEditPendant.setVisibility(View.GONE);
            tvEditBeauty.setVisibility(View.GONE);
        }

        //增加整体布局监听
        ViewTreeObserver vto = pod.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                pod.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                screenInfo = new Point(pod.getWidth(), pod.getHeight());
                setupViews();
            }
        });

    }

    private void setupViews() {
        pageView = new PageView(this, true, leftModel, rightModel, isCover);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) pageView.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        pod.addView(pageView, lp);
    }

    public void clickEditType(View view) {
        if (view.getId() == R.id.tv_edit_layout) {

        } else if (view.getId() == R.id.tv_edit_bg) {

        } else if (view.getId() == R.id.tv_edit_pendant) {

        } else if (view.getId() == R.id.tv_edit_beauty) {

        } else if (view.getId() == R.id.tv_edit_template) {
            reqTemplateList();
        } else if (view.getId() == R.id.tv_background_color) {

        }
    }

    private void reqTemplateList() {
        apiService.getTemplateList(bookModel.getBookType())
                .compose(SchedulersCompat.<BaseResponse<List<TFOSimpleTemplate>>>applyIoSchedulers())
                .subscribe(new Action1<BaseResponse<List<TFOSimpleTemplate>>>() {
                               @Override
                               public void call(BaseResponse<List<TFOSimpleTemplate>> listBaseResponse) {
                                   setTemplateListData(listBaseResponse.getData());
                               }
                           }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
    }

    private void setTemplateListData(List<TFOSimpleTemplate> data) {
        selectionAdapter = new TemplateAdapter(this, data);
        rvSelection.setAdapter(selectionAdapter);
    }

    public void clickFinish(View view) {
        if (view.getId() == R.id.iv_back) {
            finish();
        } else if (view.getId() == R.id.iv_ok) {
            doSave();
        }
    }

    private void doSave() {
        rightModel.getElementList().clear();
        leftModel.getElementList().clear();
        EditDoubleContentView contentView = (EditDoubleContentView) pageView.getContentView();
        List<TFOBookElementModel> elementModels = new ArrayList<>();
        for (StickerView sv : contentView.getStickerViews()) {
            TFOBookElementModel em = sv.getFixedElementModel();
            Log.i(TAG, "onCreate: make change  left = " + (em.getElementLeft() + (em.isRight() ? screenInfo.x / 2 : 0)) + " top = " + em.getElementTop());

            if (em.isRight()) {
                em.setElementLeft(em.getElementLeft() + screenInfo.x / 2);
            }

            if (em.getElementLeft() > screenInfo.x / 2) {
                em.setElementLeft(em.getElementLeft() - screenInfo.x / 2);
                rightModel.getElementList().add(em);
            } else {
                leftModel.getElementList().add(em);
            }

            em.resetPageScale(pageScale);
            Log.i(TAG, "onCreate: make change  left = " + (em.getElementLeft() + (em.isRight() ? screenInfo.x / 2 : 0)) + " top = " + em.getElementTop());
        }
        List<TFOBookContentModel> list = new ArrayList<>();
        list.add(rightModel);
        list.add(0, leftModel);

        apiService.editPod(bookModel.getBookId(), new Gson().toJson(list))
                .compose(SchedulersCompat.<BaseResponse<EditPod>>applyIoSchedulers())
                .subscribe(new Action1<BaseResponse<EditPod>>() {
                    @Override
                    public void call(BaseResponse<EditPod> response) {
                        Log.i(TAG, "doSave: success");
                        Intent data = new Intent();
                        data.putExtra("left_model", leftModel);
                        data.putExtra("right_model", rightModel);
                        data.putExtra("book_id", response.getData().getBookId());
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    @Subscribe
    public void eventClickSticker(ChangeStickerStatusEvent event) {
        if (pageView == null) {
            return;
        }
        if (event.getStatus() == MotionEvent.ACTION_DOWN) {
            EditDoubleContentView contentView = (EditDoubleContentView) pageView.getContentView();
            for (StickerView sv : contentView.getStickerViews()) {
                if (sv.isShowControlItems()) {
                    sv.showControlItems(false);
                }
            }
            event.getStickerView().showControlItems(true);
        } else if (event.getStatus() == MotionEvent.ACTION_UP) {
            Log.i(TAG, "eventClickSticker: click");
            String contentId = (String) event.getStickerView().getTag(R.string.tag_ex);

            TFOBookElementModel elementModel = (TFOBookElementModel) event.getStickerView().getTag(R.string.tag_obj);

            if (elementModel != null
                    && elementModel.getElementType() == TFOBookElementModel.TYPE_TEXT) {
                EditTextActivity.open(this, bookModel.getBookId(), contentId, elementModel);
                return;
            }
        }
    }
}
