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
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.open.R;
import cn.timeface.open.activities.base.BaseAppCompatActivity;
import cn.timeface.open.adapters.BgImageAdapter;
import cn.timeface.open.adapters.CoverColorAdapter;
import cn.timeface.open.adapters.PendantAdapter;
import cn.timeface.open.adapters.TemplateAdapter;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFBookBgModel;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookElementModel;
import cn.timeface.open.api.models.objs.TFOBookImageModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.objs.TFOSimpleTemplate;
import cn.timeface.open.api.models.response.EditPod;
import cn.timeface.open.api.models.response.TemplateInfo;
import cn.timeface.open.events.ChangeStickerStatusEvent;
import cn.timeface.open.events.SelectColorEvent;
import cn.timeface.open.events.SelectTemplateEvent;
import cn.timeface.open.managers.interfaces.IEventBus;
import cn.timeface.open.utils.BookModelCache;
import cn.timeface.open.utils.rxutils.SchedulersCompat;
import cn.timeface.open.views.EditDoubleContentView;
import cn.timeface.open.views.PageFrameLayout;
import cn.timeface.open.views.PageView;
import cn.timeface.open.views.StickerView;
import cn.timeface.widget.drawabletextview.DrawableTextView;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

public class EditActivity extends BaseAppCompatActivity implements IEventBus {
    public static float SCALE = 1.0f;
    public static final int EDIT_TEXT = 109;

    TFOBookModel bookModel;
    TFOBookContentModel rightModel;
    TFOBookContentModel leftModel;
    PageFrameLayout pod;
    LinearLayout llEditController;
    Point screenInfo;
    PageView pageView;
    boolean isCover = false;
    DrawableTextView tvEditTemplate;
    DrawableTextView tvBackgroundColor;
    DrawableTextView tvEditLayout;
    DrawableTextView tvEditBg;
    DrawableTextView tvEditPendant;
    DrawableTextView tvEditBeauty;
    RecyclerView rvSelection;
    TemplateAdapter templateAdapter;
    private BgImageAdapter bgImageAdapter;
    private PendantAdapter pendantAdapter;

    private CoverColorAdapter colorAdapter;
    private float pageScale = 1.0f;
    private float orgScale = 1.f;

    public static void open4result(Activity activity, int requestCode, TFOBookContentModel contentModel, boolean isCover) {
        open4result(activity, requestCode, null, contentModel, isCover);
    }

    public static void open4result(Activity activity, int requestCode, TFOBookContentModel leftModel, TFOBookContentModel rightModel, boolean isCover) {
        Intent intent = new Intent(activity, EditActivity.class);
        intent.putExtra("right_model", rightModel);
        intent.putExtra("left_model", leftModel);
        intent.putExtra("is_cover", isCover);
        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookModel = BookModelCache.getInstance().getBookModel();
        rightModel = getIntent().getParcelableExtra("right_model");
        leftModel = getIntent().getParcelableExtra("left_model");
        this.isCover = getIntent().getBooleanExtra("is_cover", false);
        setContentView(R.layout.activity_edit);
        this.pod = (PageFrameLayout) findViewById(R.id.pod);
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

        {
            //首先还原数据
            orgScale = bookModel.getMyViewScale();
            bookModel.resetPageScale();
            if (leftModel != null) leftModel.resetPageScale();
            if (rightModel != null) rightModel.resetPageScale();
        }


        //增加整体布局监听
        ViewTreeObserver vto = pod.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                pod.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                screenInfo = new Point(pod.getWidth(), pod.getHeight());
                if (screenInfo.y > screenInfo.x) {
                    //竖屏
                    pageScale = screenInfo.x / (float) bookModel.getBookWidth();
                } else {
                    //横屏
                    float pageW = bookModel.getBookWidth() * 2;
                    float pageH = bookModel.getBookHeight();

                    pageScale = screenInfo.y / pageH;
                    if (pageW * pageScale > screenInfo.x) {
                        //按照高度拉伸,如果拉伸后宽度超出屏幕
                        pageScale = screenInfo.x / pageW;
                    }
                }

                if (leftModel != null) leftModel.setPageScale(pageScale);
                if (rightModel != null) rightModel.setPageScale(pageScale);
                bookModel.setPageScale(pageScale);
                setupViews();
            }
        });
    }

    private void setupViews() {
        pod.removeAllViews();
        pageView = new PageView(this, true, leftModel, rightModel, isCover);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) pageView.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        pod.addView(pageView, lp);
    }

    public void clickEditType(View view) {
        int viewId = view.getId();
        if (view.isSelected() && rvSelection.getVisibility() == View.VISIBLE) {
            rvSelection.setVisibility(View.GONE);
            return;
        }
        changeSelectTypeBg(view);
        if (viewId == R.id.tv_edit_layout) {

        } else if (viewId == R.id.tv_edit_bg) {
            //背景图片
            reqBookBgImgList();
        } else if (viewId == R.id.tv_edit_pendant) {
            //挂件
            reqBookPendantList();
        } else if (viewId == R.id.tv_edit_beauty) {

        } else if (viewId == R.id.tv_edit_template) {
            //模板
            reqTemplateList();
        } else if (viewId == R.id.tv_background_color) {
            //背景颜色
            reqCoverBgColor();

        }

    }


    private void changeSelectTypeBg(View view) {
        tvEditLayout.setSelected(false);
        tvEditPendant.setSelected(false);
        tvEditTemplate.setSelected(false);
        tvEditBeauty.setSelected(false);
        tvEditBg.setSelected(false);
        tvBackgroundColor.setSelected(false);
        view.setSelected(true);
    }

    private void showSelectRL(boolean show) {
        rvSelection.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void reqBookPendantList() {
        if (pendantAdapter != null) {
            rvSelection.setAdapter(pendantAdapter);
            showSelectRL(true);
            return;
        }
        Subscription subscribe = apiService.getAttachPendantList(bookModel.getBookId(), String.valueOf(bookModel.getBookType()))
                .compose(SchedulersCompat.<BaseResponse<List<TFOBookImageModel>>>applyIoSchedulers())
                .subscribe(new Action1<BaseResponse<List<TFOBookImageModel>>>() {
                    @Override
                    public void call(BaseResponse<List<TFOBookImageModel>> listBaseResponse) {
                        pendantAdapter = new PendantAdapter(EditActivity.this, listBaseResponse.getData());
                        rvSelection.setAdapter(pendantAdapter);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d(TAG, "reqBookPendantList: " + throwable);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        showSelectRL(true);
                    }
                });
        addSubscription(subscribe);
    }

    private void reqBookBgImgList() {
        if (bgImageAdapter != null) {
            rvSelection.setAdapter(bgImageAdapter);
            showSelectRL(true);
            return;
        }
        Subscription subscribe = apiService.getAttachBgList(bookModel.getBookId(), String.valueOf(bookModel.getBookType()))
                .compose(SchedulersCompat.<BaseResponse<List<TFBookBgModel>>>applyIoSchedulers())
                .subscribe(new Action1<BaseResponse<List<TFBookBgModel>>>() {
                    @Override
                    public void call(BaseResponse<List<TFBookBgModel>> listBaseResponse) {
                        setupBgListData(listBaseResponse.getData());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d(TAG, "reqBookBgImgList: " + throwable);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        showSelectRL(true);
                    }
                });
        addSubscription(subscribe);

    }

    private void setupBgListData(List<TFBookBgModel> data) {
        bgImageAdapter = new BgImageAdapter(this, data);
        rvSelection.setAdapter(bgImageAdapter);
    }

    private void reqTemplateList() {
        if (templateAdapter != null) {
            rvSelection.setAdapter(templateAdapter);
            showSelectRL(true);
            return;
        }
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
                        }, new Action0() {
                            @Override
                            public void call() {
                                showSelectRL(true);
                            }
                        });
    }

    private void reqCoverBgColor() {
        if (colorAdapter != null) {
            rvSelection.setAdapter(colorAdapter);
            showSelectRL(true);
            return;
        }
        Subscription subscribe = apiService.getAttachColorList(bookModel.getBookId(), String.valueOf(bookModel.getBookType()))
                .compose(SchedulersCompat.<BaseResponse<List<String>>>applyIoSchedulers())
                .subscribe(new Action1<BaseResponse<List<String>>>() {
                    @Override
                    public void call(BaseResponse<List<String>> listBaseResponse) {
                        colorAdapter = new CoverColorAdapter(EditActivity.this, listBaseResponse.getData());
                        rvSelection.setAdapter(colorAdapter);
                        showSelectRL(true);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        addSubscription(subscribe);
    }

    private void setTemplateListData(List<TFOSimpleTemplate> data) {
        templateAdapter = new TemplateAdapter(this, data);
        rvSelection.setAdapter(templateAdapter);
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

            //做位移变换!!!!!!!!!!
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

            Log.i(TAG, "onCreate: make change  left = " + (em.getElementLeft() + (em.isRight() ? screenInfo.x / 2 : 0)) + " top = " + em.getElementTop());
        }

        if (rightModel != null) rightModel.resetPageScale();
        if (leftModel != null) leftModel.resetPageScale();

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
                EditTextActivity.open4result(this, EDIT_TEXT, bookModel.getBookId(), contentId, elementModel);
            }
        }
    }

    /**
     * 选中的背景图片
     *
     * @param view
     */
    public void clickChangeBg(View view) {
        showSelectRL(false);
        TFBookBgModel bookBgModel = (TFBookBgModel) view.getTag(R.string.tag_obj);
        bgImageAdapter.setSelBgColor(bookBgModel);
        int pageOrientation = pod.getPageOrientation();
        pageView.setPageBgPicture(bookBgModel, pageOrientation);
    }

    /**
     * 选中的挂件
     *
     * @param view
     */
    public void clickPendant(View view) {
        showSelectRL(false);
        TFOBookImageModel imageModel = (TFOBookImageModel) view.getTag(R.string.tag_obj);
        pendantAdapter.setSelImgModel(imageModel);
        Toast.makeText(EditActivity.this, imageModel.toString(), Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void selectTemplateEvent(final SelectTemplateEvent templateEvent) {
        final String templateId = templateEvent.getTemplateId();
        templateAdapter.setSelTemplateId(Integer.parseInt(templateId));
        Subscription subscribe = apiService.templateInfo(templateId, bookModel.getBookId())
                .compose(SchedulersCompat.<BaseResponse<TemplateInfo>>applyIoSchedulers())
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        showSelectRL(false);
                    }
                })
                .subscribe(new Action1<BaseResponse<TemplateInfo>>() {
                    @Override
                    public void call(BaseResponse<TemplateInfo> templateInfoBaseResponse) {
                        List<TFOBookContentModel> contentModels = templateInfoBaseResponse.getData().getContent_list();
                        if (contentModels == null || contentModels.size() == 0) return;
                        for (TFOBookContentModel cm : contentModels) {
                            if (cm.getPageType() == TFOBookContentModel.PAGE_RIGHT) {
                                rightModel = cm;
                                for (TFOBookElementModel em : rightModel.getElementList()) {
                                    em.setRight(true);
                                }
                            } else if (cm.getPageType() == TFOBookContentModel.PAGE_LEFT) {
                                leftModel = cm;
                            }
                        }
                        if (leftModel != null) leftModel.setPageScale(pageScale);
                        if (rightModel != null) rightModel.setPageScale(pageScale);
                        setupViews();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "templateInfo: " + throwable.getMessage());
                    }
                });
        addSubscription(subscribe);

    }

    @Subscribe
    public void selectColorEvent(SelectColorEvent colorEvent) {
        String color = colorEvent.getColor();
        colorAdapter.setSelectedColor(color);
        pageView.setPageBG(color);
        showSelectRL(false);
    }

    @Override
    protected void onDestroy() {
        if (bookModel != null) {
            bookModel.resetPageScale();
            bookModel.setPageScale(orgScale);
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EDIT_TEXT:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }

                TFOBookElementModel editedModel = data.getParcelableExtra("edit_text_result");
                editedModel.setPageScale(pageScale);//先设置缩放比

                TFOBookElementModel orgModel = null;

            {
                //更换model
                if (leftModel != null) {
                    for (TFOBookElementModel model : leftModel.getElementList()) {
                        if (model.getElementId() == editedModel.getElementId()) {
                            orgModel = model;
                            leftModel.getElementList().remove(orgModel);
                            leftModel.getElementList().add(editedModel);
                            break;
                        }
                    }
                }

                if (rightModel != null && orgModel == null) {
                    for (TFOBookElementModel model : rightModel.getElementList()) {
                        if (model.getElementId() == editedModel.getElementId()) {
                            orgModel = model;
                            leftModel.getElementList().remove(orgModel);
                            leftModel.getElementList().add(editedModel);
                            break;
                        }
                    }
                }
            }
            setupViews();
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
