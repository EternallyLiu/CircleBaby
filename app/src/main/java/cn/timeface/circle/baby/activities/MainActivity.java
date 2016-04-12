package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(cn.timeface.circle.baby.R.id.toolbar)
    Toolbar toolbar;
    @Bind(cn.timeface.circle.baby.R.id.rv_content)
    RecyclerView rvContent;
    @Bind(cn.timeface.circle.baby.R.id.srl_refresh_layout)
    SwipeRefreshLayout srlRefreshLayout;

    public static void open(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cn.timeface.circle.baby.R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(cn.timeface.circle.baby.R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvContent.setLayoutManager(layoutManager);
    }
}
