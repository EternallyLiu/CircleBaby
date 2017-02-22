package cn.timeface.circle.baby.ui.calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxRadioGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.events.CartBuyNowEvent;
import cn.timeface.circle.baby.support.api.exception.ResultException;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.CalendarModel;
import cn.timeface.circle.baby.support.mvp.presentations.BookPresentation;
import cn.timeface.circle.baby.support.mvp.presentations.CalendarPresentation;
import cn.timeface.circle.baby.support.mvp.presenter.BookPresenter;
import cn.timeface.circle.baby.support.mvp.presenter.CalendarPresenter;
import cn.timeface.circle.baby.ui.calendar.bean.CommemorationDataManger;
import cn.timeface.circle.baby.views.IconText;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import cn.timeface.common.utils.DeviceUtil;
import cn.timeface.open.api.bean.obj.TFOBookContentModel;
import cn.timeface.open.view.BookPodView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by JieGuo on 16/10/17.
 */

public class CalendarPreviewActivity extends BasePresenterAppCompatActivity implements
				CalendarPresentation.View, ViewPager.OnPageChangeListener, BookPresentation.View, IEventBus {

	@Bind(R.id.toolbar)
	Toolbar toolbar;
	@Bind(R.id.radio_front)
	AppCompatRadioButton radioFront;
	@Bind(R.id.radio_back)
	AppCompatRadioButton radioBack;
	@Bind(R.id.rg_sides)
	RadioGroup rgSides;
	@Bind(R.id.book_pod_view)
	BookPodView bookPodView;
	@Bind(R.id.tv_left)
	IconText tvLeft;
	@Bind(R.id.tv_month)
	TextView tvMonth;
	@Bind(R.id.tv_right)
	IconText tvRight;
	@Bind(R.id.stateView)
	TFStateView stateView;
	@Bind(R.id.tv_edit)
	TextView tvEdit;
	@Bind(R.id.tv_print)
	TextView tvPrint;
	@Bind(R.id.tv_delete)
	TextView tvDelete;
	@Bind(R.id.ll_controller)
	LinearLayout llController;

	private String remoteId = "";
	private String id = "", bookType = "";
	private CalendarPresentation.Presenter presenter = new CalendarPresenter(this);
	private BookPresentation.Presenter bookPresenter = new BookPresenter(this);
	private TFProgressDialog progressDialog = TFProgressDialog.getInstance("正在加载...");
	private boolean isCrowdfunding = false;

	/**
	 * open with a book id
	 *
	 * @param context   context
	 * @param sdkBookId sdk book id the id in timeFace server.
	 */
	public static void open(Context context, String sdkBookId, String bookType, String remoteId) {
		open(context, sdkBookId, bookType, remoteId, false);
	}

	public static void open(Context context, String sdkBookId, String bookType, String remoteId, boolean isCrowdfunding) {
		Intent intent = new Intent(context, CalendarPreviewActivity.class);
		intent.putExtra("calendarId", sdkBookId);
		intent.putExtra("bookType", bookType);
		if (!TextUtils.isEmpty(remoteId)) {
			intent.putExtra("remoteId", remoteId);
		}
		intent.putExtra("isCrowdfunding", isCrowdfunding);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_calendar_2017_preview);
		ButterKnife.bind(this);

		remoteId = getIntent().getStringExtra("remoteId");

		id = getIntent().getStringExtra("calendarId");
		bookType = getIntent().getStringExtra("bookType");
		if (TextUtils.isEmpty(id) || TextUtils.isEmpty(bookType)) {
			showToast("数据错误");
			return;
		}

		isCrowdfunding = getIntent().getBooleanExtra("isCrowdfunding", false);

		toolbar.setTitle("台历预览");
		toolbar.inflateMenu(R.menu.menu_share);
		toolbar.setNavigationOnClickListener(v -> finish());
		toolbar.setOnMenuItemClickListener(item -> {
			try {
				share();
			} catch (Exception e) {
				showToast("数据错误");
				Log.e(TAG, "error", e);
			}
			return false;
		});
		bookPodView.setHasCoverTop(true);

		stateView.setOnRetryListener(this::loadData);
		loadData();
		rgSides.getChildAt(0).setSelected(true);


		if (isCrowdfunding) {
			llController.setVisibility(View.INVISIBLE);
		}

		if (String.valueOf(CalendarModel.BOOK_TYPE_CALENDAR_ACTIVITY_STARS).equals(bookType)) {
			tvEdit.setVisibility(View.GONE);
		}
	}

	private void loadData() {
		presenter.getByRemoteId(remoteId, bookType, response -> {

			try {
				setupPOD();
			} catch (Throwable throwable) {
				showToast("数据不完整");
				finish();
				Log.e(TAG, "error", throwable);
			}
			bindEvents();
		}, throwable -> {
			if (throwable instanceof ResultException) {
				runOnUiThread(new TimerTask() {
					@Override
					public void run() {
						showToast(throwable.getLocalizedMessage());
						finish();
					}
				});
			} else {
				stateView.showException(throwable);
			}
			Log.e(TAG, "error", throwable);
		});
	}

	private void setupPOD() throws Throwable {
		bookPodView.setupPodData(getSupportFragmentManager(),
						presenter.getFrontSideWithCover(), false);
		tvMonth.setText("封面");
		hideLoading();
	}

	private void bindEvents() {
		RxRadioGroup.checkedChanges(rgSides
		).skip(1
		).subscribe(integer -> {
			if (bookPodView == null || bookPodView.getCurrentPageData() == null) {
				return;
			}
			Log.v(TAG, "checked : " + integer);
			try {
				if (integer == R.id.radio_front) {
					showFrontSide();
				} else if (integer == R.id.radio_back) {
					showBackSide();
				}
			} catch (Throwable throwable) {
				Log.e(TAG, "error", throwable);
			}
			rgSides.getChildAt(0).setSelected(integer == R.id.radio_front);
			rgSides.getChildAt(1).setSelected(integer == R.id.radio_back);

			updateMonthText();
		}, throwable -> {
			Log.e(TAG, "error", throwable);
		});
		RxView.clicks(tvLeft).subscribe(aVoid -> {
			bookPodView.clickPre();

			updateMonthText();
		});
		RxView.clicks(tvRight).subscribe(aVoid -> {
			bookPodView.clickNext();
			updateMonthText();
		});

		bookPodView.addOnPageChangeListener(this);
		RxView.clicks(tvEdit).subscribe(aVoid -> {

			if (TextUtils.isEmpty(id) || TextUtils.isEmpty(remoteId)) {
				Log.e(TAG, "data error", new Throwable("id or remote id is null."));
				return;
			}

			CalendarActivity.open(this, 0, id, remoteId, bookType);
			finish();
		}, throwable -> Log.e(TAG, "error", throwable));

		RxView.clicks(tvPrint).subscribe(aVoid -> {

							progressDialog.show(getSupportFragmentManager(), "progressDialog");
							// TODO: 2/16/17 添加应刷对话框
//                    bookPresenter.askForPrint(
//                            remoteId,
//                            String.valueOf(((CalendarPresenter) presenter).getRemoteBook().getBookType()),
//                            response -> {
//                                progressDialog.dismiss();
//
////                                CartPrintCalendarPropertyDialog propertyDialog = CartPrintCalendarPropertyDialog.getInstance(
////                                        null,
////                                        null,
////                                        response.getDataList(),
////                                        remoteId,
////                                        String.valueOf(((CalendarPresenter) presenter).getRemoteBook().getBookType()),
////                                        CartPrintPropertyDialog.REQUEST_CODE_CALENDAR,
////                                        response.getPrintCode(),
////                                        ((CalendarPresenter) presenter).getRemoteBook().getBookCover(),
////                                        0,
////                                        "");
////                                propertyDialog.show(getSupportFragmentManager(), "calendar");
//                            },
//                            throwable -> {
//                                progressDialog.dismiss();
//                                if(throwable instanceof ResultException){
//                                    Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(this, "服务器返回失败", Toast.LENGTH_SHORT).show();
//                                }
//                                Log.e(TAG, "error", throwable);
//                            });
						},
						throwable -> {
							Log.e(TAG, "error", throwable);
						});

		bookPodView.setOnPagerWillAppear((pageView, rect) -> {
			try {
				bookPodView.addView(showTopBar(rect));
			} catch (Throwable e) {
				Log.e(TAG, "error", e);
			}
		});
		RxView.clicks(tvDelete).subscribe(aVoid -> {
			delete();
		}, throwable -> Log.e(TAG, "error", throwable));
	}

	private void delete() {
		TFDialog deleteDialog = TFDialog.getInstance();
		deleteDialog.setMessage("您真的要删除这个作品吗？");
		deleteDialog.setPositiveButton(R.string.dialog_submit, v -> {
			deleteDialog.dismiss();
			progressDialog.show(getSupportFragmentManager(), "");
			presenter.deleteRemoteBook();
		});
		deleteDialog.setNegativeButton(R.string.dialog_cancel, v -> {
			deleteDialog.dismiss();
		});
		deleteDialog.show(getSupportFragmentManager(), "deleteRemoteNotebook dialog");
	}

	private ImageView showTopBar(Rect rect) {
		ImageView imageView = new ImageView(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						rect.width(),
						DeviceUtil.dpToPx(getResources(), 20));
		params.topMargin = rect.top - DeviceUtil.dpToPx(getResources(), 10);
		params.leftMargin = rect.left;
		imageView.setLayoutParams(params);
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView.setImageResource(R.drawable.ic_calendar_top_cc);
		return imageView;
	}

	@Override
	public void setCurrentTemplateSize(int current, int count) {

	}

	@Override
	public void showLoading() {
		stateView.setVisibility(View.VISIBLE);
		stateView.loading();
	}

	@Override
	public void hideLoading() {
		stateView.finish();
	}

	@Override
	public void refreshView() {
		// ignore
	}

	@Override
	public int getCurrentPageIndex() {
		return bookPodView.getCurrentIndex();
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		updateMonthText();


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
	public void finish() {
		super.finish();
		CommemorationDataManger.getInstance().destroy();
	}

	private void updateMonthText() {

		TFOBookContentModel content = bookPodView.getCurrentPageData().get(0);
		Observable.just(content
		).observeOn(Schedulers.computation()
		).flatMap(contentModel -> {
			String text = "";
			if (content.getContentType() == TFOBookContentModel.CONTENT_TYPE_FENG1
							|| content.getContentType() == TFOBookContentModel.CONTENT_TYPE_FENG2
							|| content.getContentType() == TFOBookContentModel.CONTENT_TYPE_FENG3) {
				text = "封面";
			} else if (content.getContentType() == TFOBookContentModel.CONTENT_TYPE_FENG4) {
				text = "封底";
			} else if (!TextUtils.isEmpty(content.getTemplateFileName())) {
				String fileName = content.getTemplateFileName();
				String[] names = fileName.split("_");
				if (names.length > 1) {
					text = String.format(Locale.CHINESE, "%s月", names[0]);
				}
			} else {
				text = "未知";
			}

			return Observable.just(text);
		}).observeOn(AndroidSchedulers.mainThread()
		).subscribe(aString -> {

			tvMonth.setText(aString);
		}, throwable -> {
			Log.e(TAG, "error", throwable);
		});
	}

	private void showFrontSide() throws Throwable {
		int index = bookPodView.getCurrentIndex();
		bookPodView.setupPodData(getSupportFragmentManager(),
						presenter.getFrontSideWithCover(), false);
		bookPodView.setCurrentIndex(index);
		updateMonthText();
	}

	private void showBackSide() throws Throwable {
		int index = bookPodView.getCurrentIndex();
		bookPodView.setupPodData(getSupportFragmentManager(),
						presenter.getBackSideWithCover(), false);
		bookPodView.setCurrentIndex(index);
		updateMonthText();
	}

	private void share() throws Exception {
		//UmsAgent.onEvent(this, LogConstant.BOOK_SHARE, id);

		presenter.createShareUrl();
	}

//    @Override
//    public void refreshTimeBookTagList(List<BookTagItem> dataList) {
//
//    }
//
//    @Override
//    public void printHadDelete() {
//        TFDialog tougueDialog = TFDialog.getInstance();
//        tougueDialog.setMessage(getString(R.string.cart_print_code_limit_had_delete));
//        tougueDialog.show(getSupportFragmentManager(), "dialog");
//    }
//
//    @Override
//    public void printLimitLess() {}
//
//    @Override
//    public void printLimitMore() {}
//
//    @Override
//    public void printSplit() {}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(CartBuyNowEvent event) {
//        if (event != null &&
//                event.requestCode == CartPrintPropertyDialog.REQUEST_CODE_CALENDAR) {
//            if (event.response.success()) {
//                WebOrderActivity.openConfirmOrder(this, event.response.getOrderId());
//            } else {
//                Toast.makeText(this, event.response.info, Toast.LENGTH_SHORT).show();
//            }
//        }
	}

	@Override
	public void showErr(String errMsg) {

	}

	@Override
	public void setStateView(boolean loading) {

	}

	@Override
	public void setBookData(List<BookObj> bookObjs) {

	}
}
