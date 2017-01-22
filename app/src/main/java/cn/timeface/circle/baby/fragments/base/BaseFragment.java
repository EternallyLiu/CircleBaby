package cn.timeface.circle.baby.fragments.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;

import butterknife.ButterKnife;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.api.services.ApiService;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * author: rayboot  Created on 15/12/3.
 * email : sy0725work@gmail.com
 */
public class BaseFragment extends Fragment {
    protected final String TAG = this.getClass().getSimpleName();
    public static final ApiService apiService = BaseAppCompatActivity.apiService;

    protected void initActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.showLog(getClass().getName());
        if (this instanceof IEventBus) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (getActivity() instanceof BaseAppCompatActivity) {
            CompositeSubscription subscription = ((BaseAppCompatActivity) getActivity()).getCompositeSubscription();
            if (subscription != null) {
                subscription.unsubscribe();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this instanceof IEventBus) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void addSubscription(Subscription s) {
        if (getActivity() instanceof BaseAppCompatActivity) {
            ((BaseAppCompatActivity) getActivity()).addSubscription(s);
        }
    }

    public void setActionBar(android.support.v7.widget.Toolbar view) {
        if (getActivity() instanceof AppCompatActivity) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(view);
//            }
        }
    }

    public ActionBar getActionBar() {
        if (getActivity() instanceof AppCompatActivity) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            return supportActionBar;
//            }
        }
        return null;
    }

}
