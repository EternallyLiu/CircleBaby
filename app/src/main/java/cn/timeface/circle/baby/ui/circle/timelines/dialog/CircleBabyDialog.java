package cn.timeface.circle.baby.ui.circle.timelines.dialog;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.RelateBabyAdapter;
import cn.timeface.circle.baby.ui.circle.timelines.bean.CircleBabyObj;
import cn.timeface.circle.baby.ui.images.views.FlipImageView;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.views.dialog.BaseDialog;
import rx.Observable;

/**
 * author : wangshuai Created on 2017/3/20
 * email : wangs1992321@gmail.com
 */
public class CircleBabyDialog extends BaseDialog implements View.OnClickListener, BaseAdapter.OnItemClickLister {

    private RecyclerView contentRecyclerView;
    private Button btnSubmit;

    private List<Long> babyIds = new ArrayList<>(0);
    private List<Long> cancleCircleId = new ArrayList<>(0);

    private RelateBabyAdapter adapter = null;

    public CircleBabyDialog(Context context, CircleMediaObj mediaObj) {
        this(context);
        adapter.addList(true, mediaObj.getRelateBabys());
    }

    private CircleBabyDialog(Context context) {
        super(context);
        init();
    }

    private CircleBabyDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    private CircleBabyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_circle_baby, null);
        this.setContentView(mView);
        contentRecyclerView = (RecyclerView) mView.findViewById(R.id.content_recycler_view);
        btnSubmit = (Button) mView.findViewById(R.id.btn_submit);
        adapter = new RelateBabyAdapter(getContext());
        contentRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5, GridLayoutManager.VERTICAL, false));
        contentRecyclerView.setAdapter(adapter);
        adapter.setItemClickLister(this);
        btnSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (getCircleBabyCallBack() != null)
            getCircleBabyCallBack().circleResult(JSONUtils.parse2JSONString(babyIds), JSONUtils.parse2JSONString(cancleCircleId));
    }

    @Override
    public void onItemClick(View view, int position) {
        FlipImageView ivSelect = (FlipImageView) view.findViewById(R.id.iv_select);
        CircleBabyObj babyObj = adapter.getItem(position);
        if (babyObj.getBabyId() > 0) {
            if (ivSelect.getStatus() == RelateBabyAdapter.STATUS_SELECT) {
                ivSelect.changeStatus(RelateBabyAdapter.STATUS_NONE);
                ivSelect.setVisibility(View.GONE);
                if (babyIds.contains(babyObj.getBabyId())) {
                    babyIds.remove(babyIds.indexOf(babyObj.getBabyId()));
                } else if (!cancleCircleId.contains(babyObj.getBabyId())) {
                    cancleCircleId.add(babyObj.getBabyId());
                }
            } else if (ivSelect.getStatus() != RelateBabyAdapter.STATUS_FINAL) {
                ivSelect.setVisibility(View.VISIBLE);
                ivSelect.changeStatus(RelateBabyAdapter.STATUS_SELECT);
                if (!babyIds.contains(babyObj.getBabyId())) {
                    babyIds.add(babyObj.getBabyId());
                } else if (cancleCircleId.contains(babyObj.getBabyId())) {
                    cancleCircleId.remove(cancleCircleId.indexOf(babyObj.getBabyId()));
                }
            }
        }
    }

    private CircleBabyCallBack circleBabyCallBack;

    public CircleBabyCallBack getCircleBabyCallBack() {
        return circleBabyCallBack;
    }

    public void setCircleBabyCallBack(CircleBabyCallBack circleBabyCallBack) {
        this.circleBabyCallBack = circleBabyCallBack;
    }

    public interface CircleBabyCallBack {
        public void circleResult(String babyids, String cancelBabyIds);
    }

}
