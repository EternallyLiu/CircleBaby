package cn.timeface.circle.baby.ui.growth.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 作品列表页面
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public abstract class ProductionListActivity extends BasePresenterAppCompatActivity{

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv_books)
    RecyclerView rvBooks;
    @Bind(R.id.ll_empty)
    LinearLayout llEmpty;
    @Bind(R.id.content_book_list)
    RelativeLayout contentBookList;
    @Bind(R.id.tv_tip)
    TextView tvTip;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;

    protected int bookType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.bookType = getIntent().getIntExtra("book_type", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mine_book, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
