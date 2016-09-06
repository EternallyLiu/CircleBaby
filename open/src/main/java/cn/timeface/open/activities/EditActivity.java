package cn.timeface.open.activities;

import android.app.Activity;
import android.app.ProgressDialog;
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
import cn.timeface.open.adapters.BgImageAdapter;
import cn.timeface.open.adapters.CoverColorAdapter;
import cn.timeface.open.adapters.LayoutAdapter;
import cn.timeface.open.adapters.PendantAdapter;
import cn.timeface.open.adapters.TemplateAdapter;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFBookBackgroundModel;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookElementModel;
import cn.timeface.open.api.models.objs.TFOBookImageModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.objs.TFOSimpleTemplate;
import cn.timeface.open.api.models.response.CoverColor;
import cn.timeface.open.api.models.response.CoverTemplateInfo;
import cn.timeface.open.api.models.response.EditBookCover;
import cn.timeface.open.api.models.response.EditPod;
import cn.timeface.open.api.models.response.SimplePageTemplate;
import cn.timeface.open.constants.Constant;
import cn.timeface.open.events.ChangeStickerStatusEvent;
import cn.timeface.open.events.SelectColorEvent;
import cn.timeface.open.events.SelectTemplateEvent;
import cn.timeface.open.managers.interfaces.IChangeFocusPageListener;
import cn.timeface.open.managers.interfaces.IEventBus;
import cn.timeface.open.managers.interfaces.ISelectModel;
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

public class EditActivity extends BaseAppCompatActivity implements IEventBus, IChangeFocusPageListener {
    public static float SCALE = 1.0f;
    public static final int EDIT_TEXT = 109;
    public static final int EDIT_IMAGE = 110;

    TFOBookModel bookModel;
    TFOBookContentModel rightModel;
    TFOBookContentModel leftModel;
    PageFrameLayout podFrameLayout;
    View focusView;
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
    private LayoutAdapter layoutAdapter;

    private CoverColorAdapter colorAdapter;
    private float pageScale = 1.0f;
    private float orgScale = 1.f;

    ProgressDialog pd;

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

        this.podFrameLayout = (PageFrameLayout) findViewById(R.id.pod);
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

        this.podFrameLayout.setChangeFocusPageListener(this);

        if (isCover) {
            tvEditLayout.setVisibility(View.GONE);
            tvEditBg.setVisibility(View.GONE);
            tvEditPendant.setVisibility(View.GONE);
            tvEditBeauty.setVisibility(View.GONE);
        } else {
            //不是封面
            tvEditTemplate.setVisibility(View.GONE);
            tvBackgroundColor.setVisibility(View.GONE);

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
        ViewTreeObserver vto = podFrameLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                podFrameLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                screenInfo = new Point(podFrameLayout.getWidth(), podFrameLayout.getHeight());
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

    private void showProgressDialog(String msg) {
        if (pd == null) {
            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(false);
        }

        if (pd.isShowing()) {
            pd.dismiss();
        }
        pd.setMessage(msg);

        pd.show();
    }

    private void dismissProgressDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    private void setupViews() {
        podFrameLayout.removeAllViews();
        pageView = new PageView(this, true, leftModel, rightModel, isCover);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) pageView.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        podFrameLayout.addView(pageView, lp);
        podFrameLayout.setCurrentPage(PageFrameLayout.LEFT);

        focusView = new View(this);
        focusView.setBackgroundResource(R.drawable.shape_focus);
        lp = new FrameLayout.LayoutParams(bookModel.getBookHeight(), bookModel.getBookWidth());
        lp.gravity = Gravity.CENTER_VERTICAL;
        podFrameLayout.setFocusView(focusView, bookModel.getBookWidth());
        podFrameLayout.addView(focusView, lp);
        focusView.setVisibility(View.GONE);
    }

    public void clickEditType(View view) {
        int viewId = view.getId();
        if (view.isSelected() && rvSelection.getVisibility() == View.VISIBLE) {
            rvSelection.setVisibility(View.GONE);
            focusView.setVisibility(View.GONE);
            return;
        }
        changeSelectTypeBg(view);
        focusView.setVisibility(View.VISIBLE);
        if (viewId == R.id.tv_edit_layout) {
            //编辑布局
            reqContentLayout();
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

    private void reqContentLayout() {
        if (layoutAdapter != null) {
            rvSelection.setAdapter(layoutAdapter);
            showSelectRL(true);
            return;
        }

        List<String> contentIDs = new ArrayList<>(2);
        if (leftModel != null) {
            contentIDs.add(leftModel.getContentId());
        }
        if (rightModel != null) {
            contentIDs.add(rightModel.getContentId());
        }

        Subscription subscribe = apiService.pageTemplate(bookModel.getBookId(), new Gson().toJson(contentIDs))
                .compose(SchedulersCompat.<BaseResponse<List<SimplePageTemplate>>>applyIoSchedulers())
                .subscribe(new Action1<BaseResponse<List<SimplePageTemplate>>>() {
                    @Override
                    public void call(BaseResponse<List<SimplePageTemplate>> listBaseResponse) {
                        layoutAdapter = new LayoutAdapter(EditActivity.this, listBaseResponse.getData());
                        rvSelection.setAdapter(layoutAdapter);
                        layoutAdapter.setSelectModel(getFocusModel());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d(TAG, "reqContentLayout: " + throwable);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        showSelectRL(true);
                    }
                });
        addSubscription(subscribe);
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
        focusView.setVisibility(show ? View.VISIBLE : View.GONE);
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
                .compose(SchedulersCompat.<BaseResponse<List<TFBookBackgroundModel>>>applyIoSchedulers())
                .subscribe(new Action1<BaseResponse<List<TFBookBackgroundModel>>>() {
                    @Override
                    public void call(BaseResponse<List<TFBookBackgroundModel>> listBaseResponse) {
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

    private void setupBgListData(List<TFBookBackgroundModel> data) {
        bgImageAdapter = new BgImageAdapter(this, data);
        rvSelection.setAdapter(bgImageAdapter);
        bgImageAdapter.setSelectModel(getFocusModel());
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
                .compose(SchedulersCompat.<BaseResponse<List<CoverColor>>>applyIoSchedulers())
                .subscribe(new Action1<BaseResponse<List<CoverColor>>>() {
                    @Override
                    public void call(BaseResponse<List<CoverColor>> listBaseResponse) {
                        colorAdapter = new CoverColorAdapter(EditActivity.this, listBaseResponse.getData());
                        rvSelection.setAdapter(colorAdapter);
//                        colorAdapter.setSelectModel(getFocusModel());
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
                em.setElementLeft((float) Math.ceil(em.getElementLeft() + (double) screenInfo.x / 2));
            }

            if (em.getElementLeft() >= screenInfo.x / 2) {
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
        list.add(leftModel);
        list.add(0, rightModel);

        if (isCover) {
            //保存封面
            apiService.editBookCover(bookModel.getBookId(), bookModel.getBookTitle(), bookModel.getBookAuthor(), bookModel.getTemplateId(), new Gson().toJson(list))
                    .compose(SchedulersCompat.<BaseResponse<EditBookCover>>applyIoSchedulers())
                    .subscribe(new Action1<BaseResponse<EditBookCover>>() {
                                   @Override
                                   public void call(BaseResponse<EditBookCover> response) {
                                       Log.i(TAG, "doSave: cover success");
                                       Intent data = new Intent();
                                       data.putExtra("left_model", leftModel);
                                       data.putExtra("right_model", rightModel);
                                       data.putExtra("book_id", response.getData().getBookId());
                                       setResult(RESULT_OK, data);
                                       finish();
                                   }
                               }
                            , new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                }
                            });
        } else {
            //保存内页
            apiService.editPod(bookModel.getBookId(), new Gson().toJson(list))
                    .compose(SchedulersCompat.<BaseResponse<EditPod>>applyIoSchedulers())
                    .subscribe(new Action1<BaseResponse<EditPod>>() {
                        @Override
                        public void call(BaseResponse<EditPod> response) {
                            Log.i(TAG, "doSave: content success");
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

            if (elementModel == null) {
                return;
            }

            switch (elementModel.getElementType()) {
                case TFOBookElementModel.TYPE_TEXT:
                    EditTextActivity.open4result(this, EDIT_TEXT, bookModel.getBookId(), contentId, elementModel);
                    break;
                case TFOBookElementModel.TYPE_IMAGE:
                    CropImageActivity.open4result(this, EDIT_IMAGE, elementModel, contentId);
                    break;
            }
        }
    }

    /**
     * 选中的挂件
     *
     * @param view
     */
    public void clickEditLayout(View view) {
//        showSelectRL(false);
        SimplePageTemplate templateModel = (SimplePageTemplate) view.getTag(R.string.tag_obj);
        reqNewPageLayout(templateModel);
    }

    private void reqNewPageLayout(SimplePageTemplate template) {
        List<TFOBookContentModel> contentList = new ArrayList<>(2);
        if (template.single()) {
            if (podFrameLayout.getPageOrientation() == PageFrameLayout.LEFT) {
                leftModel.resetPageScale();
                contentList.add(leftModel);
            } else if (podFrameLayout.getPageOrientation() == PageFrameLayout.RIGHT) {
                rightModel.resetPageScale();
                contentList.add(rightModel);
            }
        } else {
            leftModel.resetPageScale();
            rightModel.resetPageScale();
            contentList.add(leftModel);
            contentList.add(rightModel);
        }

        Subscription subscribe = apiService.reformat(bookModel.getBookId(), template.getTemplateId(), new Gson().toJson(contentList))
                .compose(SchedulersCompat.<BaseResponse<List<TFOBookContentModel>>>applyIoSchedulers())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showProgressDialog("正在重新排版");
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        dismissProgressDialog();
                    }
                })
                .subscribe(new Action1<BaseResponse<List<TFOBookContentModel>>>() {
                               @Override
                               public void call(BaseResponse<List<TFOBookContentModel>> listBaseResponse) {
                                   if (listBaseResponse.getData().size() == 2) {
                                       leftModel = listBaseResponse.getData().get(0);
                                       rightModel = listBaseResponse.getData().get(1);
                                       rightModel.setRightPage(true);
                                       leftModel.setPageScale(pageScale);
                                       rightModel.setPageScale(pageScale);
                                   } else if (listBaseResponse.getData().size() == 1) {
                                       switch (podFrameLayout.getPageOrientation()) {
                                           case PageFrameLayout.LEFT:
                                               leftModel = listBaseResponse.getData().get(0);
                                               leftModel.setPageScale(pageScale);
                                               break;
                                           case PageFrameLayout.RIGHT:
                                               rightModel = listBaseResponse.getData().get(0);
                                               rightModel.setRightPage(true);
                                               rightModel.setPageScale(pageScale);
                                               break;
                                       }
                                   }
                                   setupViews();
                               }
                           }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
        addSubscription(subscribe);
    }

    /**
     * 选中的背景图片
     *
     * @param view
     */
    public void clickChangeBg(View view) {
//        showSelectRL(false);
        TFBookBackgroundModel bookBgModel = (TFBookBackgroundModel) view.getTag(R.string.tag_obj);
        bgImageAdapter.setSelectModel(getFocusModel());
        int pageOrientation = podFrameLayout.getPageOrientation();
        pageView.setPageBgPicture(bookBgModel, pageOrientation);
    }

    public TFOBookContentModel getFocusModel() {
        if (podFrameLayout.getCurrentPage() == PageFrameLayout.LEFT) {
            return leftModel;
        } else if (podFrameLayout.getCurrentPage() == PageFrameLayout.RIGHT) {
            return rightModel;
        }
        return null;
    }

    /**
     * 选中的挂件
     *
     * @param view
     */
    public void clickPendant(View view) {
//        showSelectRL(false);
        TFOBookImageModel imageModel = (TFOBookImageModel) view.getTag(R.string.tag_obj);
        {
            //缩放imageModel,如果不做这一步,初始化的挂件会非常大,看起来不和谐
            imageModel.setImageScale(Math.min(bookModel.getBookWidth() / imageModel.getImageWidth() / 2, bookModel.getBookHeight() / imageModel.getImageHeight() / 2));
        }
        TFOBookElementModel elementModel = new TFOBookElementModel(imageModel);
        elementModel.setPageScale(pageScale);
        switch (podFrameLayout.getPageOrientation()) {
            case PageFrameLayout.LEFT:
                leftModel.getElementList().add(elementModel);
                break;
            case PageFrameLayout.RIGHT:
                elementModel.setRight(true);
                rightModel.getElementList().add(elementModel);
                break;
        }
        setupViews();
    }

    @Subscribe
    public void selectTemplateEvent(final SelectTemplateEvent templateEvent) {
        final int templateId = templateEvent.getTemplateId();
        templateAdapter.setSelTemplateId(templateId);
        Subscription subscribe = apiService.templateInfo(templateId, bookModel.getBookId())
                .compose(SchedulersCompat.<BaseResponse<CoverTemplateInfo>>applyIoSchedulers())
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
//                        showSelectRL(false);
                    }
                })
                .subscribe(new Action1<BaseResponse<CoverTemplateInfo>>() {
                    @Override
                    public void call(BaseResponse<CoverTemplateInfo> templateInfoBaseResponse) {
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
                        bookModel.setTemplateId(templateId);
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
        CoverColor coverColor = colorEvent.getCoverColor();
        colorAdapter.setSelectedColor(coverColor.getCoverBackgroundColor());
        pageView.setPageColor(coverColor.getCoverBackgroundColor());
        //// TODO: 8/24/16  textcolor 待完善
//        showSelectRL(false);
    }

    @Override
    protected void onDestroy() {
        if (bookModel != null) {
            bookModel.resetPageScale();
            bookModel.setPageScale(orgScale);
        }
        dismissProgressDialog();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TFOBookElementModel editedModel;
        String editedContentId;
        switch (requestCode) {
            case EDIT_TEXT:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }

                editedModel = data.getParcelableExtra(Constant.ELEMENT_MODEL);
                editedContentId = data.getStringExtra(Constant.CONTENT_ID);
                editedModel.setPageScale(pageScale);//先设置缩放比

                switch (editedModel.getElementFlag()) {
                    case TFOBookElementModel.ELEMENT_TYPE_BOOK_TITLE:
                        this.bookModel.setBookTitle(editedModel.getElementContent());
                        break;
                    case TFOBookElementModel.ELEMENT_TYPE_BOOK_AUTHOR:
                        this.bookModel.setBookAuthor(editedModel.getElementContent());
                        break;
                }

                Log.i(TAG, "onActivityResult: 111111  modeljson = " + new Gson().toJson(editedModel));

                //更换model
                changeElementModel(editedContentId, editedModel);
                setupViews();
                break;
            case EDIT_IMAGE:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }
                editedModel = data.getParcelableExtra(Constant.ELEMENT_MODEL);
                editedContentId = data.getStringExtra(Constant.CONTENT_ID);
                Log.i(TAG, "onActivityResult: 111111  modeljson = " + new Gson().toJson(editedModel));

                //更换model
                changeElementModel(editedContentId, editedModel);
                setupViews();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 更换element mode
     * 删除原有的element,再add编辑后的element
     *
     * @param editedContentId
     * @param edited
     * @return
     */
    public boolean changeElementModel(String editedContentId, TFOBookElementModel edited) {

        if (leftModel != null && leftModel.getContentId().equals(editedContentId)) {
            for (TFOBookElementModel model : leftModel.getElementList()) {
                if (model.getElementId() == edited.getElementId()) {
                    leftModel.getElementList().remove(model);
                    leftModel.getElementList().add(edited);
                    return true;
                }
            }
        }

        if (rightModel != null && rightModel.getContentId().equals(editedContentId)) {
            for (TFOBookElementModel model : rightModel.getElementList()) {
                if (model.getElementId() == edited.getElementId()) {
                    rightModel.getElementList().remove(model);
                    rightModel.getElementList().add(edited);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onChangeFocusPage(int focusPage) {
        if (rvSelection.getVisibility() != View.VISIBLE) {
            return;
        }

        if (rvSelection.getAdapter() instanceof ISelectModel) {
            ((ISelectModel) rvSelection.getAdapter()).setSelectModel(getFocusModel());
        }
    }
}
