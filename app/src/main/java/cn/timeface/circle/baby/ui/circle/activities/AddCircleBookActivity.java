package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.AddCircleBookAdapter;
import cn.timeface.circle.baby.ui.circle.photo.bean.CircleBookTypeObj;

/**
 * 添加圈作品
 * Created by lidonglin on 2017/3/22.
 */
public class AddCircleBookActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;

    private LinearLayoutManager layoutManager;
    private AddCircleBookAdapter addCircleBookAdapter;

    public static void open(Context context) {
        context.startActivity(new Intent(context, AddCircleBookActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cirlce_book);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reqData();
    }

    private void reqData() {
        apiService.circleBookTypeList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            if (response.success()) {
                                setData(response.getDataList());
                            } else {
                                ToastUtil.showToast(response.getInfo());
                            }
                        },
                        throwable -> {
                            Log.e(TAG, throwable.getLocalizedMessage());
                        }
                );
    }

    private void setData(List<CircleBookTypeObj> dataList) {
        if (addCircleBookAdapter == null) {
            contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            addCircleBookAdapter = new AddCircleBookAdapter(this, dataList, this);
//            contentRecyclerView.addItemDecoration(
//                    new HorizontalDividerItemDecoration.Builder(this)
//                            .sizeResId(R.dimen.view_space_medium)
//                            .colorResId(R.color.trans)
//                            .build()
//            );
            contentRecyclerView.setAdapter(addCircleBookAdapter);
        } else {
            addCircleBookAdapter.setListData(dataList);
            addCircleBookAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        CircleBookTypeObj typeObj = (CircleBookTypeObj) view.getTag(R.string.tag_obj);
        switch (view.getId()){
            case R.id.tv_creat:
                //家校纪念册
                if(typeObj.getBookType() == BookModel.CIRCLE_BOOK_TYPE_FAMILY_SCHOOL){
                    CircleSelectServeHomeWorksActivity.open(this, String.valueOf(FastData.getCircleId()));
                //圈照片书
                } else if(typeObj.getBookType() == BookModel.CIRCLE_BOOK_TYPE_PHOTO){
                    CircleSelectSeverAlbumsActivity.open(
                            this,
                            String.valueOf(FastData.getCircleId()),
                            BookModel.CIRCLE_BOOK_TYPE_PHOTO,
                            "",
                            175,
                            "");
                //圈时光书
                } else if(typeObj.getBookType() == BookModel.CIRCLE_BOOK_TYPE_TIME) {
                    CircleSelectServerTimesActivity.open(
                            this,
                            BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK,
                            TypeConstants.OPEN_BOOK_TYPE_PAINTING,
                            "",
                            "",
                            String.valueOf(FastData.getCircleId()));
                } else {
                    Log.e(TAG, "无法识别的作品类型");
                }
                break;
            case R.id.tv_detail:

                break;
        }
    }
}
