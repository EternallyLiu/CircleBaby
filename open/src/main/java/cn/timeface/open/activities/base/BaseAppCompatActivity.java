package cn.timeface.open.activities.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;

import cn.timeface.open.api.ApiFactory;
import cn.timeface.open.api.services.ApiService;
import cn.timeface.open.managers.interfaces.IEventBus;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author rayboot
 * @from 14/11/3 14:04
 * @TODO
 */
public class BaseAppCompatActivity extends AppCompatActivity {

    public static final ApiService apiService = ApiFactory.getApi().getApiService();
    protected final String TAG = this.getClass().getSimpleName();

    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this instanceof IEventBus) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (this instanceof IEventBus) {
            EventBus.getDefault().unregister(this);
        }
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public CompositeSubscription getCompositeSubscription() {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        return this.mCompositeSubscription;
    }

    public void addSubscription(Subscription s) {
        getCompositeSubscription().add(s);
    }
}
