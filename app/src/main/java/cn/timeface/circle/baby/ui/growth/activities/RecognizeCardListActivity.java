package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;
import cn.timeface.circle.baby.support.mvp.presentations.CardPresentation;
import cn.timeface.circle.baby.support.mvp.presenter.CardPresenter;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.ui.growth.adapters.RecognizeCardListAdapter;

/**
 * 识图卡片列表
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public class RecognizeCardListActivity extends ProductionListActivity implements CardPresentation.RecognizeCardView {
    RecognizeCardListAdapter cardListAdapter;
    CardPresenter cardPresenter;

    public static void open(Context context){
        Intent intent = new Intent(context, RecognizeCardListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardPresenter = new CardPresenter(this);
        cardPresenter.loadRecognizeCard();
        getSupportActionBar().setTitle(FastData.getBabyName() + "识图卡片");
        rvBooks.setPadding(
                getResources().getDimensionPixelOffset(R.dimen.size_16),
                0,
                getResources().getDimensionPixelOffset(R.dimen.size_16),
                0);
    }

    @Override
    public void setStateView(boolean loading) {
        if(loading){
            stateView.setVisibility(View.VISIBLE);
            stateView.loading();
        } else {
            stateView.finish();
        }
    }

    @Override
    public void showError(String errMsg) {
        showToast(errMsg);
    }

    @Override
    public void setRecognizeCardData(List<KnowledgeCardObj> knowledgeCardObjs) {
        if(cardListAdapter == null){
            rvBooks.setLayoutManager(new GridLayoutManager(this, 2));
            cardListAdapter = new RecognizeCardListAdapter(this, knowledgeCardObjs);
            rvBooks.setAdapter(cardListAdapter);
        } else {
            cardListAdapter.setListData(knowledgeCardObjs);
            cardListAdapter.notifyDataSetChanged();
        }
    }
}
