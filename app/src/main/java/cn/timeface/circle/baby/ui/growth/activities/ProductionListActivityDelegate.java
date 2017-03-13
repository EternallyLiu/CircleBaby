package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;

import cn.timeface.circle.baby.support.mvp.model.BookModel;

/**
 * 作品页面代理
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public class ProductionListActivityDelegate {

    public static void dispatchProductionList(Context context, int bookType) {
        switch (bookType) {
            case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK:
            case BookModel.BOOK_TYPE_CALENDAR:
            case BookModel.BOOK_TYPE_NOTEBOOK:
            case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS:
            case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK:
            case BookModel.BOOK_TYPE_PAINTING:
                BookListActivity.open(context, bookType);
                break;

            case BookModel.BOOK_TYPE_DIARY_CARD:
                DiaryCardListActivity.open(context);
                break;
            case BookModel.BOOK_TYPE_RECOGNIZE_PHOTO_CARD:
                RecognizeCardListActivity.open(context);
                break;
        }
    }
}
