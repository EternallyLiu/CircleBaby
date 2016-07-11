package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.timeface.open.activities.PODActivity;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.objs.TFOPublishObj;

public class MyPODActivity extends PODActivity {

    public static void open(Context context, String bookId, int bookType, TFOPublishObj publishObj) {
        Intent intent = new Intent(context, MyPODActivity.class);
        intent.putExtra("book_type", bookType);
        intent.putExtra("book_id", bookId);
        intent.putExtra("publish_obj", publishObj);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void createBookInfo(TFOBookModel bookModel) {

    }

}
