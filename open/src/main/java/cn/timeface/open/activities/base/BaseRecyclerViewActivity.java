package cn.timeface.open.activities.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import cn.timeface.open.R;
import cn.timeface.open.utils.ptr.IPTRRecyclerListener;
import cn.timeface.open.utils.ptr.TFPTRRecyclerViewHelper;

public class BaseRecyclerViewActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected RecyclerView rvContent;
    protected SwipeRefreshLayout srlRefreshLayout;

    protected TFPTRRecyclerViewHelper tfptrListViewHelper;
    protected IPTRRecyclerListener ptrListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_recycler_view);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.rvContent = (RecyclerView) findViewById(R.id.rv_content);
        this.srlRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_refresh_layout);

        setSupportActionBar(toolbar);
    }

    protected void setupPTR() {
        tfptrListViewHelper =
                new TFPTRRecyclerViewHelper(this, rvContent, srlRefreshLayout)
                        .setTFPTRMode(TFPTRRecyclerViewHelper.Mode.BOTH)
                        .tfPtrListener(ptrListener);
    }

}
