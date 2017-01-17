package cn.timeface.circle.baby.ui.images;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.images.beans.TipObj;
import cn.timeface.circle.baby.ui.images.views.FlowLayout;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;

/**
 * author : wangshuai Created on 2017/1/16
 * email : wangs1992321@gmail.com
 */
public class TagAddFragment extends BaseFragment implements TextWatcher, View.OnClickListener {


    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.right)
    TextView right;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.input)
    EditText input;
    @Bind(R.id.flow_layout)
    FlowLayout flowLayout;
    @Bind(R.id.tag_his)
    FlowLayout tagHis;
    @Bind(R.id.tag_suggest)
    FlowLayout tagSuggest;


    private List<TipObj> hisList = null;
    private List<TipObj> recommentList = null;
    private LayoutInflater inflater = null;
    private List<TipObj> selectList = new ArrayList<>();

    private long mediaId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("mediaId"))
            mediaId = bundle.getLong("mediaId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_add, container, false);

        ButterKnife.bind(this, view);
        input.addTextChangedListener(this);
        right.setText(R.string.save_tip);
        right.setOnClickListener(this);
        back.setOnClickListener(this);
        title.setText(R.string.tag_add_title);
        reqData();
        if (mediaId <= 0) {
            ToastUtil.showToast("对不起！图片信息不能为空");
            getActivity().finish();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void reqData() {
        addSubscription(apiService.getTips("", "")
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {

                    if (response.success()) {
                        hisList = response.getHistoryTips();
                        recommentList = response.getRecommendTips();
                        initHisList();
                        initRecomment();
                    }

                }, throwable -> {
                }));
    }

    private int textColor;


    private void initHisList() {
        if (tagHis.getChildCount() > 0)
            tagHis.removeAllViews();
        if (hisList != null || hisList.size() > 0) {
            for (int i = 0; i < hisList.size(); i++) {
                View view = addTips(hisList.get(i));
                TextView tagNmae = (TextView) view.findViewById(R.id.tag_name);
                ImageView delete = (ImageView) view.findViewById(R.id.delete);
                delete.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    tagNmae.setBackground(getNoSelectDrawable());
                } else tagNmae.setBackgroundDrawable(getNoSelectDrawable());
                tagNmae.setTextColor(getTextColor());
                tagHis.addView(view);
            }
        }

    }

    private void initRecomment() {
        if (tagSuggest.getChildCount() > 0)
            tagSuggest.removeAllViews();
        if (recommentList != null || recommentList.size() > 0) {
            for (int i = 0; i < recommentList.size(); i++) {
                View view = addTips(recommentList.get(i));
                TextView tagNmae = (TextView) view.findViewById(R.id.tag_name);
                ImageView delete = (ImageView) view.findViewById(R.id.delete);
                delete.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    tagNmae.setBackground(getNoSelectDrawable());
                } else tagNmae.setBackgroundDrawable(getNoSelectDrawable());
                tagNmae.setTextColor(getTextColor());
                tagSuggest.addView(view);

            }
        }

    }

    private Drawable noSelectDrawable = null;


    /**
     * 加载标签布局
     *
     * @param tip
     * @return
     */
    private View addTips(TipObj tip) {
        String tag = tip.getTipName();
        if (TextUtils.isEmpty(tag))
            return null;
        if (inflater == null)
            inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.tag_item_layout, flowLayout, false);
        TextView tagNmae = (TextView) view.findViewById(R.id.tag_name);
        tagNmae.setText(tag.trim());
        tagNmae.setOnClickListener(this);
        view.setTag(R.id.tag_add, tip);
        return view;
    }

    /**
     * 获取添加标签到的索引位置中
     *
     * @return
     */
    private int getIndex() {
        int count = flowLayout.getChildCount();
        if (count <= 1)
            return 0;
        else return count - 1;
    }

    private void addView(String tag) {
        tag = Utils.getLengthString(24, tag);
        addView(new TipObj(tag));
    }

    /**
     * 添加标签到选择布局中
     *
     * @param tag
     */
    private void addView(TipObj tag) {
        if (selectList.contains(tag))
            return;
        View tageView = addTips(tag);
        ImageView delete = (ImageView) tageView.findViewById(R.id.delete);
        delete.setVisibility(View.VISIBLE);
        delete.setOnClickListener(this);
        if (tageView != null) {
            flowLayout.addView(tageView, getIndex());
            selectList.add(tag);
        }
        if (selectList.size() >= 5) {
            input.setVisibility(View.GONE);
            flowLayout.removeView(input);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String tag = s.toString();
        if (!TextUtils.isEmpty(tag) && (tag.endsWith(" ") || Utils.getByteSize(tag) >= 24)) {
            addView(s.toString());
            input.setText("");
        }
    }

    private void save() {
        if (selectList == null || selectList.size() <= 0) {
            ToastUtil.showToast(getString(R.string.tag_save_null_tip));
            return;
        }
        String json = null;
        try {
            json = LoganSquare.serialize(selectList, TipObj.class);
            LogUtil.showLog("json:" + json);
        } catch (IOException e) {
            LogUtil.showLog("异常!");
            e.printStackTrace();
        }
//        if (TextUtils.isEmpty(json))
//            return;
//        addSubscription(apiService.addLabel(mediaId + "", "")
//                .compose(SchedulersCompat.applyIoSchedulers())
//                .subscribe(response -> {
//                    if (response.success()) {
//                        getActivity().finish();
//                    }
//
//                }, throwable -> {
//                }));
    }

    @Override
    public void onClick(View v) {
        View parent = ((View) v.getParent());
        TipObj tipObj = (TipObj) parent.getTag(R.id.tag_add);
        switch (v.getId()) {
            case R.id.tag_name:
                if (!selectList.contains(tipObj))
                    addView(tipObj);
                break;
            case R.id.delete:
                if (selectList.contains(tipObj)) {
                    flowLayout.removeView(parent);
                    selectList.remove(tipObj);
                    if (selectList.size() == 4) {
                        flowLayout.addView(input);
                        input.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.back:
                getActivity().finish();
                break;
            case R.id.right:
                save();
                break;
        }
    }

    public Drawable getNoSelectDrawable() {
        if (noSelectDrawable == null)
            noSelectDrawable = getActivity().getResources().getDrawable(R.drawable.tag_item_no_select);
        return noSelectDrawable;
    }

    public int getTextColor() {
        if (textColor <= 0)
            textColor = getActivity().getResources().getColor(R.color.aluminum);
        return textColor;
    }
}
