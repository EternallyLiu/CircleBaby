package cn.timeface.circle.baby.ui.growth.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 作品列表fragment
 * author : YW.SUN Created on 2017/2/13
 * email : sunyw10@gmail.com
 */
public class ProductionListFragment extends BasePresenterFragment {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_tip)
    TextView tvTip;
    @Bind(R.id.rv_books)
    RecyclerView rvBooks;
    @Bind(R.id.ll_empty)
    LinearLayout llEmpty;
    @Bind(R.id.btn_ask_for_print)
    Button btnAskForPrint;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;
    @Bind(R.id.content_book_list)
    RelativeLayout contentBookList;

    protected int bookType;

    public static ProductionListFragment newInstance(int bookType){
        ProductionListFragment fragment = new ProductionListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("book_type", bookType);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ProductionListFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_production_list, container, false);
        ButterKnife.bind(this, view);
        this.bookType = getArguments().getInt("book_type", 0);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
