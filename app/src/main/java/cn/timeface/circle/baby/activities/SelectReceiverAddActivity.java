package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wbtech.ums.UmsAgent;
import com.wbtech.ums.common.UmsConstants;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.SelectAddressAdapter;
import cn.timeface.circle.baby.api.models.AddressItem;
import cn.timeface.circle.baby.dialogs.PopBottomDialog;
import cn.timeface.circle.baby.events.AddAddressFinishEvent;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.ErrorViewContent;
import cn.timeface.circle.baby.views.HttpStatusCodes;
import cn.timeface.circle.baby.views.TFStateView;
import rx.Subscription;

/**
 * @author SUN
 * @from 2014/12/4
 * @TODO
 */
public class SelectReceiverAddActivity extends BaseAppCompatActivity implements IEventBus {

    @Bind(R.id.stateView)
    TFStateView mStateView;
    @Bind(R.id.lv_list)
    ListView listView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private List<AddressItem> dataList = new ArrayList<>();
    private SelectAddressAdapter adapter;
    private String addressId;
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            finish();
            EventBus.getDefault().post(new AddAddressFinishEvent((AddressItem) adapter.getItem(position), AddAddressFinishEvent.TYPE_SELECT));
        }
    };
    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            final View v = adapter.getDropDownView(position, view, parent);
            final AddressItem item = dataList.get(position);
            //            v.setBackgroundResource(0xffe6e6e6);
            if (!item.getId().equals(addressId)) {
                v.setBackgroundResource(R.color.bg16);
            }
            PopBottomDialog dialog = PopBottomDialog.newInstance("修改", "删除", "取消");
            dialog.setClickListener(new PopBottomDialog.ClickListener() {

                @Override
                public void onItem3Click() {
                    if (!item.getId().equals(addressId)) {
                        v.setBackgroundResource(R.drawable.selector_click_able);
                    }
                }

                @Override
                public void onItem2Click() {
                    // 删除
                    if (!item.getId().equals(addressId)) {
                        v.setBackgroundResource(R.drawable.selector_click_able);
                    }
                    reqDeleteData(item);
                }

                @Override
                public void onItem1Click() {
                    // 修改
                    if (!item.getId().equals(addressId)) {
                        v.setBackgroundResource(R.drawable.selector_click_able);
                    }
                    AddressAddActivity.open(SelectReceiverAddActivity.this, item, AddAddressFinishEvent.TYPE_CHANGE);
                }
            });

            dialog.show(getSupportFragmentManager(), "dialog");
            return true;
        }
    };

    public static void open(Context context, String addressId) {
        Intent intent = new Intent(context, SelectReceiverAddActivity.class);
        intent.putExtra("addressId", addressId);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_book_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:
                this.startActivity(new Intent(this, AddressAddActivity.class));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addressId = getIntent().getStringExtra("addressId");
        reqData();

        adapter = new SelectAddressAdapter(this, dataList);
        adapter.setAddressId(addressId);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);
        listView.setOnItemLongClickListener(itemLongClickListener);
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        return super.onPrepareOptionsPanel(view, menu);
    }

    private void reqData() {
        if (adapter == null || adapter.getCount() == 0) {
            mStateView.loading();
        }

        // wangxiaowei unfix mStateView
        Subscription s = apiService.getAddressList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            mStateView.finish();
                            if (response.success()) {
                                List<AddressItem> items = response.getDataList();
                                dataList.clear();
                                dataList.addAll(items);
                                adapter.notifyDataSetChanged();

                                if (dataList == null || dataList.size() == 0) {
                                    mStateView.setState(ErrorViewContent.getContentObj(HttpStatusCodes.NO_MESSAGE));
                                    mStateView.setTitle(getString(R.string.no_receive_address));
                                }
                            } else {
                                Toast.makeText(SelectReceiverAddActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            mStateView.showException(throwable);
                        }
                );
        addSubscription(s);
    }

    private void reqDeleteData(final AddressItem item) {
        Subscription s = apiService.delAddress(item.getId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        EventBus.getDefault().post(new AddAddressFinishEvent(item, AddAddressFinishEvent.TYPE_DELET, dataList.size() - 1));
                        reqData();
                    }
                });
        addSubscription(s);
    }

    @Subscribe
    public void onEvent(AddAddressFinishEvent event) {
        reqData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmsAgent.onResume(this, UmsConstants.MODULE_PERSONAL_CENTER + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmsAgent.onPause(this);
    }

}