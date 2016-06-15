package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.api.models.objs.MineBookObj;
import cn.timeface.circle.baby.fragments.base.BaseFragment;

public class MineBookFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    List<MineBookObj> bookList;

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
        bookList = mokeData(bookList);
        

    }

    private List<MineBookObj> mokeData(List<MineBookObj> bookList) {
        MineBookObj mineBookObj = new MineBookObj("http://img.pconline.com.cn/images/upload/upc/tx/itbbs/1402/27/c4/31612517_1393474458218_mthumb.jpg", 1465193897000L, 50, "啦啦啦时光书", 1);
        for (int i = 0; i < 5; i++) {
            bookList.add(mineBookObj);
        }
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
