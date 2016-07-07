package cn.timeface.open.fragments.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;

import cn.timeface.open.activities.base.BaseAppCompatActivity;
import cn.timeface.open.api.services.ApiService;
import cn.timeface.open.managers.interfaces.IEventBus;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * author: rayboot  Created on 15/12/3.
 * email : sy0725work@gmail.com
 */
public class BaseFragment extends Fragment {
    protected final String TAG = this.getClass().getSimpleName();
    public static final ApiService apiService = BaseAppCompatActivity.apiService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this instanceof IEventBus) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
}