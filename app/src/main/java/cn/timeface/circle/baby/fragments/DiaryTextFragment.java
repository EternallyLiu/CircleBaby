package cn.timeface.circle.baby.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.responses.DiaryTextResponse;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;

public class DiaryTextFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_change)
    TextView tvChange;
    @Bind(R.id.lv)
    ListView lv;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.tv_auto)
    TextView tvAuto;

    private MyAdapter myAdapter;
    private DiaryTextResponse diaryTextResponse;
    private String content;

    public DiaryTextFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        content = getArguments().getString("diary_content");
        if(content.contains("\r\n")){
            content.replace("\r\n","\n");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diarytext, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("日记卡片配文");
        }
        etContent.setText(content);
        tvChange.setOnClickListener(this);
        tvAuto.setOnClickListener(this);

        return view;
    }

    private void reqData() {
        apiService.getDiaryDefaultTextList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(diaryTextResponse -> {
                    this.diaryTextResponse = diaryTextResponse;
                    setDataList(diaryTextResponse.getDataList());
                }, throwable -> {
                    Log.e(TAG, "getDiaryDefaultTextList:");
                });

    }

    private void setDataList(List<String> dataList) {
        List<String> strings = new ArrayList<>();
        while (strings.size() < 3) {
            int v = (int) (Math.random() * (dataList.size() - 1));
            if (!strings.contains(dataList.get(v))) {
                strings.add(dataList.get(v));
            }
        }
        if (myAdapter == null) {
            myAdapter = new MyAdapter(strings);
        } else {
            myAdapter.setList(strings);
        }
        lv.setAdapter(myAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_auto:
                tvAuto.setSelected(true);
                lv.setVisibility(View.VISIBLE);
                reqData();
                break;
            case R.id.tv_change:
                if (diaryTextResponse != null) {
                    setDataList(diaryTextResponse.getDataList());
                }
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
            View view = View.inflate(getContext(), R.layout.item_diary_text, null);
            TextView textView = (TextView) view.findViewById(R.id.tv_text);
//            TextView textView = new TextView(getActivity());
            textView.setText(list.get(position));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("content", list.get(position));
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
            });
            return textView;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_complete, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.complete) {
            String content = etContent.getText().toString();
            if (TextUtils.isEmpty(content)) {
                ToastUtil.showToast("记录宝宝今天的成长吧~");
                return true;
            }
            if (content.length() > 54) {
                ToastUtil.showToast("不能超过54个字哦~");
                return true;
            }
            String[] split = content.split("\n");
            if (split.length > 3) {
                content = "";
                for(int i =0;i<split.length;i++){
                    content +=split[i];
                }
                split = content.split("\n");
            }
            for(int i =0;i<split.length;i++){
                String s = split[i];
                if(split.length ==1 && s.length() > 18){
                    String substring1 = s.substring(0, 18);
                    if(s.length()>36){
                        String substring2 = s.substring(18, 36);
                        String substring3 = s.substring(36);
                        content = substring1+"\n"+substring2+"\n"+substring3;
                    }else{
                        String substring2 = s.substring(18);
                        content = substring1+"\n"+substring2;
                    }
                }

            }

            Intent intent = new Intent();
            intent.putExtra("content", content);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
