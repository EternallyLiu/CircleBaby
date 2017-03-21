package cn.timeface.circle.baby.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.SelectThemeActivity;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;
import cn.timeface.circle.baby.support.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.services.ApiService;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;

/**
 * 作品操作菜单按钮dialog
 * author : YW.SUN Created on 2017/2/4
 * email : sunyw10@gmail.com
 */
public class ProductionMenuDialog extends DialogFragment implements View.OnClickListener {
    @Bind(R.id.tv_change_theme)
    TextView tvChangeTheme;
    @Bind(R.id.tv_delete)
    TextView tvDelete;

    ApiService apiService = ApiFactory.getApi().getApiService();
    BookObj bookObj;
    boolean changeTheme;

    public static ProductionMenuDialog newInstance(BookObj bookObj, boolean changeTheme) {
        ProductionMenuDialog productionMenuDialog = new ProductionMenuDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("book_obj", bookObj);
        bundle.putBoolean("change_theme", changeTheme);
        productionMenuDialog.setArguments(bundle);
        return productionMenuDialog;
    }

    public ProductionMenuDialog() {
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.trans);
        getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_production_menu, container, false);
        ButterKnife.bind(this, view);

        bookObj = getArguments().getParcelable("book_obj");
        changeTheme = getArguments().getBoolean("change_theme");
        tvChangeTheme.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvChangeTheme.setVisibility(changeTheme ? View.VISIBLE : View.GONE);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View view) {
        //更换主题
        if (view.getId() == R.id.tv_change_theme) {
            apiService.queryImageInfoList("", BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .doOnUnsubscribe(() -> dismiss())
                    .subscribe(response -> {
                        if (response.success()) {
                            List<ImageInfoListObj> dataList = response.getDataList();
                            for (ImageInfoListObj obj : dataList) {
                                List<MediaObj> mediaList = obj.getMediaList();
                                int timeId = obj.getTimeId();
                                for (MediaObj mediaObj : mediaList) {
                                    mediaObj.setTimeId(timeId);
                                }
                            }

                            SelectThemeActivity.open(
                                    getActivity(),
                                    String.valueOf(bookObj.getBookId()),
                                    String.valueOf(bookObj.getOpenBookId()),
                                    bookObj.getBaby().getBabyId(),
                                    bookObj.getAuthor().getNickName(),
                                    bookObj.getBookName());
                        } else {
                            ToastUtil.showToast(response.getInfo());
                        }
                    }, error -> {
                        Log.e(ProductionMenuDialog.class.getSimpleName(), "queryImageInfoList:");
                    });

        //删除
        } else if (view.getId() == R.id.tv_delete) {
            ((BasePresenterAppCompatActivity) getActivity()).addSubscription(
                    apiService.deleteBook(String.valueOf(bookObj.getBookId()))
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .doOnUnsubscribe(()->dismiss())
                            .subscribe(response -> {
                                if (response.success()) {
                                    EventBus.getDefault().post(new BookOptionEvent(BookOptionEvent.BOOK_OPTION_DELETE, bookObj.getBookType(), String.valueOf(bookObj.getBookId())));
                                } else {
                                    ToastUtil.showToast(response.getInfo());
                                }
                            }, error -> Log.e(ProductionMenuDialog.class.getSimpleName(), "deleteBook:"))
            );
        }
    }
}
