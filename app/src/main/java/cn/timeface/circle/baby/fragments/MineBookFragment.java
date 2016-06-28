package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.MineBookAdapter;
import cn.timeface.circle.baby.api.models.objs.MineBookObj;
import cn.timeface.circle.baby.fragments.base.BaseFragment;

public class MineBookFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    List<MineBookObj> bookList = new ArrayList<>();

    public MineBookFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_minebook, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ivAdd.setOnClickListener(this);
        reqData();
        return view;
    }

    private void reqData() {
        setDataList(bookList);
    }

    private void setDataList(List<MineBookObj> bookList) {
        List<MineBookObj> mineBookObjs = mokeData(bookList);
        MineBookAdapter mineBookAdapter = new MineBookAdapter(getActivity(), mineBookObjs);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contentRecyclerView.setAdapter(mineBookAdapter);
    }

    private List<MineBookObj> mokeData(List<MineBookObj> bookList) {
        MineBookObj mineBookObj1 = new MineBookObj("http://static.timeface.cn/coverpage/c7f9d26b0960f6e678fe17f25421d2bc.png", 1465193897000L, 50, "测试一本书", 1, "240796762256", "Melvin");
        MineBookObj mineBookObj2 = new MineBookObj("http://static.timeface.cn/coverpage/55b9e7b678ff7ae02b9a4ef2fcf9d7b4.png", 1465193897000L, 50, "Melvin的时光书", 1, "252251922680", "Melvin");
        MineBookObj mineBookObj3 = new MineBookObj("http://static.timeface.cn/coverpage/c17a1b4718d3e413576d4e4e18ca815f.png", 1465193897000L, 50, "测试一本书2222312", 1, "261970035390", "Melvin");
        bookList.add(mineBookObj1);
        bookList.add(mineBookObj2);
        bookList.add(mineBookObj3);
        return bookList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                FragmentBridgeActivity.open(getContext(), "AddBookListFragment");
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
