package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.timeface.circle.baby.fragments.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    @Bind(cn.timeface.circle.baby.R.id.content)
    TextView content;

    private String mParam1;


    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(cn.timeface.circle.baby.R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        content.setText(mParam1);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
