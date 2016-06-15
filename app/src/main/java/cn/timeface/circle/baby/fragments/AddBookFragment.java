package cn.timeface.circle.baby.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.AddBookAdapter;
import cn.timeface.circle.baby.api.models.objs.BookTypeListObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.GlideUtil;

public class AddBookFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_bookname)
    TextView tvBookname;
    @Bind(R.id.tv_desc)
    TextView tvDesc;
    @Bind(R.id.iv_creatbook)
    ImageView ivCreatbook;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    private BookTypeListObj bookTypeListObj;
    private List<MediaObj> imgList;

    public AddBookFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookTypeListObj = getArguments().getParcelable("BookTypeListObj");
        imgList = bookTypeListObj.getImgList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addbook, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        return view;
    }

    private void initData() {
        tvTitle.setText(bookTypeListObj.getCoverTitle());
        tvBookname.setText(bookTypeListObj.getTitle());
        tvDesc.setText(bookTypeListObj.getDescription());

        AddBookAdapter adapter = new AddBookAdapter(getContext(), imgList);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contentRecyclerView.setAdapter(adapter);

        ivCreatbook.setOnClickListener(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_creatbook:

                break;
        }
    }
}
