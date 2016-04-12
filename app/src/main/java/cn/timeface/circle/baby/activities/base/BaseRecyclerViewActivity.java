package cn.timeface.circle.baby.activities.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import cn.timeface.circle.baby.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.utils.ptr.TFPTRRecyclerViewHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BaseRecyclerViewActivity extends AppCompatActivity {



    @Bind(cn.timeface.circle.baby.R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(cn.timeface.circle.baby.R.id.rv_content)
    protected RecyclerView rvContent;
    @Bind(cn.timeface.circle.baby.R.id.srl_refresh_layout)
    protected SwipeRefreshLayout srlRefreshLayout;

    protected TFPTRRecyclerViewHelper tfptrListViewHelper;
    protected IPTRRecyclerListener ptrListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cn.timeface.circle.baby.R.layout.activity_base_recycler_view);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    protected void setupPTR() {
        tfptrListViewHelper =
                new TFPTRRecyclerViewHelper(this, rvContent, srlRefreshLayout)
                        .setTFPTRMode(TFPTRRecyclerViewHelper.Mode.BOTH)
                        .tfPtrListener(ptrListener);
    }

}
