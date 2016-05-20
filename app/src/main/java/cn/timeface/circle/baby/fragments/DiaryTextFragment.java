package cn.timeface.circle.baby.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class DiaryTextFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.tv_complete)
    TextView tvComplete;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_change)
    TextView tvChange;
    @Bind(R.id.lv)
    ListView lv;
    @Bind(R.id.et_content)
    EditText etContent;

    private MyAdapter myAdapter;

    public DiaryTextFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diarytext, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        tvComplete.setOnClickListener(this);
        tvChange.setOnClickListener(this);

        reqData();

        return view;
    }

    private void reqData() {
        apiService.getTextList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(diaryTextResponse -> {
                    setDataList(diaryTextResponse.getDataList());
                }, throwable -> {
                    Log.e(TAG, "getTextList:");
                });

    }

    private void setDataList(List<String> dataList) {
        if (myAdapter == null) {
            myAdapter = new MyAdapter(dataList);
        }else{
            myAdapter.setList(dataList);
        }
        lv.setAdapter(myAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_complete:
                String content = etContent.getText().toString();
                if(TextUtils.isEmpty(content)){
                    ToastUtil.showToast("记录宝宝今天的成长吧~");
                    return;
                }
                if(content.length()>60){
                    ToastUtil.showToast("日记不能超过60字哦~");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("content",content);
                getActivity().setResult(Activity.RESULT_OK,intent);
                getActivity().finish();
                break;
            case R.id.tv_change:
                reqData();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class MyAdapter extends BaseAdapter {
        private List<String> list;

        public MyAdapter(List<String> list) {
            this.list = list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public List<String> getList() {
            return list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getActivity());
            textView.setText(list.get(position));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("content",list.get(position));
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
            });
            return textView;
        }
    }
}
