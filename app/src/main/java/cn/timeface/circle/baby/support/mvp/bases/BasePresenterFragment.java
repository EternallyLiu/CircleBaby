package cn.timeface.circle.baby.support.mvp.bases;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.services.ApiService;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.presentations.BasePresenterView;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * mvp base presenter fragment
 * author : YW.SUN Created on 2017/1/11
 * email : sunyw10@gmail.com
 */
public class BasePresenterFragment extends Fragment implements BasePresenterView {

    protected final String TAG = this.getClass().getSimpleName();
    private CompositeSubscription mCompositeSubscription;

    public ApiService apiService;

    public CompositeSubscription getCompositeSubscription() {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        return this.mCompositeSubscription;
    }


    public void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        this.mCompositeSubscription.add(s);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiFactory.getApi().getApiService();
        if (this instanceof IEventBus) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }

        if (this instanceof IEventBus) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public AppCompatActivity getCurrentActivity() {
        return ((AppCompatActivity) getActivity());
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
