package cn.timeface.circle.baby.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.mvp.model.BookModel;

/**
 * 选择照片/时光筛选条件（按时间，按发布人，按地点，按标签）
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
 */
public class SelectContentTypeDialog extends BasePresenterFragment implements View.OnClickListener {

    @Bind(R.id.tv_type_time)
    TextView tvTypeTime;
    @Bind(R.id.tv_type_user)
    TextView tvTypeUser;
    @Bind(R.id.tv_type_location)
    TextView tvTypeLocation;
    @Bind(R.id.tv_type_label)
    TextView tvTypeLabel;
    @Bind(R.id.rl_root)
    RelativeLayout rlRoot;

    @Bind({R.id.tv_type_time, R.id.tv_type_user, R.id.tv_type_location, R.id.tv_type_label})
    TextView[] textViews;

    public static int CONTENT_TYPE_PHOTO = 0;//选择照片（按时间/按发布人/按地点/按标签）
    public static int CONTENT_TYPE_TIME = 1;//选择时光（只有按时间/按发布人）
    SelectTypeListener selectTypeListener;
    int contentType;
    int bookType;

    public interface SelectTypeListener{
        void selectTypeTime();
        void selectTypeUser();
        void selectTypeLocation();
        void selectTypeLabel();
        void setTypeText(String title);//设置标题名称
        void dismiss();
    }

    public static SelectContentTypeDialog newInstance(SelectTypeListener selectTypeListener, int contentType, int bookType){
        SelectContentTypeDialog selectContentTypeDialog = new SelectContentTypeDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("content_type", contentType);
        bundle.putInt("book_type", bookType);
        selectContentTypeDialog.setSelectTypeListener(selectTypeListener);
        selectContentTypeDialog.setArguments(bundle);
        return selectContentTypeDialog;
    }

    public SelectContentTypeDialog() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_content_type, container, false);
        ButterKnife.bind(this, view);
        tvTypeTime.setOnClickListener(this);
        tvTypeUser.setOnClickListener(this);
        tvTypeLocation.setOnClickListener(this);
        tvTypeLabel.setOnClickListener(this);
        rlRoot.setOnClickListener(this);
        contentType = getArguments().getInt("content_type", CONTENT_TYPE_PHOTO);
        bookType = getArguments().getInt("book_type", 0);
        if(contentType == CONTENT_TYPE_TIME){
            tvTypeLocation.setVisibility(View.INVISIBLE);
            tvTypeLabel.setVisibility(View.INVISIBLE);
        }
        //绘画集 默认按标签
        if(bookType == BookModel.BOOK_TYPE_PAINTING){
            tvTypeLabel.setSelected(true);
        //其他 默认按时间
        } else {
            tvTypeTime.setSelected(true);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_type_time:
                selectTypeListener.selectTypeTime();
                setBtnsEnable(view.getId());
                break;

            //跳转成员选择页面
            case R.id.tv_type_user:
                selectTypeListener.setTypeText("按发布人");//设置标题
                selectTypeListener.selectTypeUser();
                setBtnsEnable(view.getId());
                break;

            case R.id.tv_type_location:
                selectTypeListener.selectTypeLocation();
                setBtnsEnable(view.getId());
                break;

            case R.id.tv_type_label:
                selectTypeListener.selectTypeLabel();
                setBtnsEnable(view.getId());
                break;

            case R.id.rl_root:
                selectTypeListener.dismiss();
                break;
        }
    }

    public void setSelectTypeListener(SelectTypeListener selectTypeListener) {
        this.selectTypeListener = selectTypeListener;
    }

    public void setBtnsEnable(int resId){
        for(TextView textView : textViews){
            if(textView.getId() == resId){
                textView.setSelected(true);
            } else {
                textView.setSelected(false);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
