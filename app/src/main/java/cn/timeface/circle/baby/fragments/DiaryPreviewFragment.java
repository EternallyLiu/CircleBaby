package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.HorizontalListViewAdapter;
import cn.timeface.circle.baby.adapters.HorizontalListViewAdapter2;
import cn.timeface.circle.baby.api.models.objs.BabyObj;
import cn.timeface.circle.baby.api.models.objs.Paper;
import cn.timeface.circle.baby.api.models.responses.DiaryPaperResponse;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.HorizontalListView;

public class DiaryPreviewFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.tv_complete)
    TextView tvComplete;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_bg)
    ImageView ivBg;
    @Bind(R.id.iv_diary)
    ImageView ivDiary;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.lv_horizontal)
    HorizontalListView lvHorizontal;
    private HorizontalListViewAdapter2 adapter;
    private DiaryPaperResponse diaryPaperResponse;
    private String time;
    private String content;
    private String url;

    public DiaryPreviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        time = getArguments().getString("time");
        content = getArguments().getString("content");
        url = getArguments().getString("url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diarypreview, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        tvTime.setText(time);
        tvContent.setText(content);
        GlideUtil.displayImage(url, ivDiary);

        reqDiaryBgList();

        lvHorizontal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlideUtil.displayImage(diaryPaperResponse.getDataList().get(position).getPaperUrl(), ivBg);
                adapter.setSelectIndex(position);
                adapter.notifyDataSetChanged();
            }
        });
        tvComplete.setOnClickListener(this);

        return view;
    }

    private void reqDiaryBgList() {
        apiService.getPaperList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(diaryPaperResponse -> {
                    this.diaryPaperResponse = diaryPaperResponse;
                    setDataList(diaryPaperResponse.getDataList());
                }, throwable -> {
                    Log.e(TAG, "getPaperList:");
                });

    }

    private void setDataList(List<Paper> dataList) {
        if(adapter == null){
            adapter = new HorizontalListViewAdapter2(getActivity(), dataList);
        }else{
            adapter.setList(dataList);
            adapter.notifyDataSetChanged();
        }
        lvHorizontal.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_complete:
                //日记发布


                break;
        }
    }
}
