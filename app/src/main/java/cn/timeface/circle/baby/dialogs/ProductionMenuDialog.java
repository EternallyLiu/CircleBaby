package cn.timeface.circle.baby.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.SelectThemeActivity;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.support.api.ApiFactory;
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
    String bookId;
    String openBookId;
    int bookType;
    boolean changeTheme;
    int babyId;

    public static ProductionMenuDialog newInstance(int bookType, String bookId, boolean changeTheme, String openBookId, int babyId) {
        ProductionMenuDialog productionMenuDialog = new ProductionMenuDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("book_type", bookType);
        bundle.putString("book_id", bookId);
        bundle.putBoolean("change_theme", changeTheme);
        bundle.putString("open_book_id", openBookId);
        bundle.putInt("baby_id", babyId);
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

        bookId = getArguments().getString("book_id");
        openBookId = getArguments().getString("open_book_id");
        bookType = getArguments().getInt("book_type");
        changeTheme = getArguments().getBoolean("change_theme");
        babyId = getArguments().getInt("baby_id");
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
                            //照片书，进入选择主题界面
//                            Intent intent = new Intent(getActivity(), SelectThemeActivity.class);
//                            intent.putParcelableArrayListExtra("dataList", (ArrayList<? extends Parcelable>) dataList);
//                            startActivity(intent);
                            SelectThemeActivity.open(getActivity(), bookId, openBookId, babyId);
                        } else {
                            ToastUtil.showToast(response.getInfo());
                        }
                    }, error -> {
                        Log.e(ProductionMenuDialog.class.getSimpleName(), "queryImageInfoList:");
                    });

        //删除
        } else if (view.getId() == R.id.tv_delete) {
            ((BasePresenterAppCompatActivity) getActivity()).addSubscription(
                    apiService.deleteBook(bookId)
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .doOnUnsubscribe(()->dismiss())
                            .subscribe(response -> {
                                if (response.success()) {
                                    EventBus.getDefault().post(new BookOptionEvent(BookOptionEvent.BOOK_OPTION_DELETE, bookType, bookId));
                                } else {
                                    ToastUtil.showToast(response.getInfo());
                                }
                            }, error -> {
                                Log.e(ProductionMenuDialog.class.getSimpleName(), "deleteBook:");
                            })
            );
        }
    }
}
