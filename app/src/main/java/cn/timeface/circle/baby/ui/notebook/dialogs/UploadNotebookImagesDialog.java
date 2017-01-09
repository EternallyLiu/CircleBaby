package cn.timeface.circle.baby.ui.notebook.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;

/**
 * Created by JieGuo on 16/11/23.
 */

public class UploadNotebookImagesDialog extends UploadImagesDialog {

    public static UploadNotebookImagesDialog newInstance() {
        UploadNotebookImagesDialog dialog = new UploadNotebookImagesDialog();
        // dialog.setArguments(new Bundle());
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        TextView textView = ButterKnife.findById(view, R.id.tv_title);
        if (textView != null) {
            textView.setText("12张插页图片可以批量上传哦~");
        }
        return view;
    }
}
