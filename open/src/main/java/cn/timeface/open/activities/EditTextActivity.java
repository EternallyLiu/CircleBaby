package cn.timeface.open.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import cn.timeface.open.R;
import cn.timeface.open.activities.base.BaseAppCompatActivity;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOBookElementModel;
import cn.timeface.open.utils.rxutils.SchedulersCompat;
import rx.functions.Action1;

public class EditTextActivity extends BaseAppCompatActivity {

    Toolbar toolbar;
    TextView tvTip;
    TextView tvTextCount;
    FloatingActionButton fab;
    String contentId;
    String bookId;
    TFOBookElementModel elementModel;
    int maxTextCount = 100;
    EditText etContent;

    public static void open4result(Activity activity, int requestCode, String book_id, String content_id, TFOBookElementModel elementModel) {
        Intent intent = new Intent(activity, EditTextActivity.class);
        intent.putExtra("content_id", content_id);
        intent.putExtra("book_id", book_id);
        intent.putExtra("element_model", elementModel);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.tvTip = (TextView) findViewById(R.id.tv_tip);
        this.tvTextCount = (TextView) findViewById(R.id.tv_text_count);
        this.etContent = (EditText) findViewById(R.id.et_content);
        this.fab = (FloatingActionButton) findViewById(R.id.fab);

        contentId = getIntent().getStringExtra("content_id");
        bookId = getIntent().getStringExtra("book_id");
        elementModel = getIntent().getParcelableExtra("element_model");
        {
            //还原model
            elementModel.resetPageScale();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("编辑描述");

        maxTextCount = elementModel.getTextContentExpand().getMaxTextCount();

        tvTip.setText(String.format("此处最多支持%s字", maxTextCount));
        etContent.setText(elementModel.getElementContent());
        setCount();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqData();
            }
        });

        etContent.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int nSelStart = etContent.getSelectionStart();
                        int nSelEnd = etContent.getSelectionEnd();

                        if (s.length() > maxTextCount) {
                            s.delete(nSelStart - 1, nSelEnd);
                            etContent.setTextKeepState(s);
                        }
                        setCount();
                    }
                }
        );
    }

    private void reqData() {
        apiService
                .editText(bookId, contentId, new Gson().toJson(elementModel), etContent.getText().toString())
                .compose(SchedulersCompat.<BaseResponse<cn.timeface.open.api.models.response.EditText>>applyIoSchedulers())
                .subscribe(
                        new Action1<BaseResponse<cn.timeface.open.api.models.response.EditText>>() {
                            @Override
                            public void call(BaseResponse<cn.timeface.open.api.models.response.EditText> response) {
                                Log.i(TAG, "reqData: " + response.toString());
                                if (response.success()) {
                                    Intent data = new Intent();
                                    data.putExtra("edit_text_result", response.getData().getElementModel());
                                    setResult(Activity.RESULT_OK, data);
                                    finish();
                                } else {
                                    Toast.makeText(EditTextActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        }
                );
    }

    private void setCount() {
        tvTextCount.setText(String.format("%d/%d", etContent.getText().toString().length(), maxTextCount));
    }
}
