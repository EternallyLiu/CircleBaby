package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.AddBookListAdapter;
import cn.timeface.circle.baby.api.models.objs.BookObj;
import cn.timeface.circle.baby.api.models.objs.BookTypeListObj;
import cn.timeface.circle.baby.api.models.objs.MineBookObj;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class EnsureOrderFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rl_addaddress)
    RelativeLayout rlAddaddress;
    @Bind(R.id.express)
    TextView express;
    @Bind(R.id.tv_express1)
    TextView tvExpress1;
    @Bind(R.id.tv_express2)
    TextView tvExpress2;
    @Bind(R.id.rl_express)
    RelativeLayout rlExpress;
    @Bind(R.id.iv_book)
    ImageView ivBook;
    @Bind(R.id.tv_size)
    TextView tvSize;
    @Bind(R.id.ll_size)
    LinearLayout llSize;
    @Bind(R.id.color)
    TextView color;
    @Bind(R.id.tv_color)
    TextView tvColor;
    @Bind(R.id.tv_price)
    TextView tvPrice;
    @Bind(R.id.rl_color)
    RelativeLayout rlColor;
    @Bind(R.id.paper)
    TextView paper;
    @Bind(R.id.tv_paper)
    TextView tvPaper;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.rl_paper)
    RelativeLayout rlPaper;
    @Bind(R.id.tv_bind)
    TextView tvBind;
    @Bind(R.id.tv_agreement)
    TextView tvAgreement;
    @Bind(R.id.all)
    TextView all;
    @Bind(R.id.rmb)
    TextView rmb;
    @Bind(R.id.tv_price2)
    TextView tvPrice2;
    @Bind(R.id.tv_submit_order)
    TextView tvSubmitOrder;
    @Bind(R.id.tv_name)
    TextView tvName;

    private BookObj bookObj;
    private MineBookObj mineBookObj;

    public EnsureOrderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookObj = getArguments().getParcelable("BookObj");
        mineBookObj = getArguments().getParcelable("MineBookObj");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ensureorder, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        return view;
    }

    private void initData() {
        tvExpress1.setSelected(true);
        tvName.setText(mineBookObj.getTitle());
        tvSize.setText(bookObj.getSize());
        tvColor.setText(bookObj.getColor());
        tvPaper.setText(bookObj.getPaper());
        tvBind.setText(bookObj.getBind());
        tvCount.setText(bookObj.getCount()+"");


        rlAddaddress.setOnClickListener(this);
        tvExpress1.setOnClickListener(this);
        tvExpress2.setOnClickListener(this);
        tvSubmitOrder.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_addaddress:
                FragmentBridgeActivity.open(getContext(), "SelectAddressFragment");
                break;
            case R.id.tv_express1:
                break;
            case R.id.tv_express2:
                break;
            case R.id.tv_submit_order:
                break;
        }
    }
}
