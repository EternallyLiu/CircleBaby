package cn.timeface.circle.baby.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CreateBabyActivity;
import cn.timeface.circle.baby.activities.InviteCodeActivity;
import cn.timeface.circle.baby.adapters.ChangebabyAdapter;
import cn.timeface.circle.baby.api.models.objs.BabyListInfo;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.ClearableEditText;

public class ChangeInfoFragment extends BaseFragment{


    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_ok)
    TextView tvOk;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.cet)
    ClearableEditText cet;
    private ChangebabyAdapter adapter;
    private String info;
    private String title;


    public ChangeInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = getArguments().getString("info");
        title = getArguments().getString("action_bar_title");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_changeinfo, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        tvTitle.setText(title);
        cet.setText(info);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = cet.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("data",input);
                getActivity().setResult(Activity.RESULT_OK,intent);
//                getActivity().finish();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
