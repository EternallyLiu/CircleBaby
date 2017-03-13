package cn.timeface.circle.baby.support.mvp.presenter;

import cn.timeface.circle.baby.support.mvp.bases.BasePresenter;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.mvp.presentations.BookPresentation;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;

/**
 * book 各作品presenter
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public class BookPresenter extends BasePresenter<BookPresentation.View, BookModel> implements BookPresentation.Presenter {

    BookModel bookModel;

    public BookPresenter(BookPresentation.View view) {
        bookModel = new BookModel();
        setup(view, bookModel);
    }

    @Override
    public void create() {

    }

    @Override
    public void loadData(int bookType, int permissionType) {
        view.setStateView(true);
        view.addSubscription(
                model.productionList(bookType, permissionType)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(
                                response -> {
                                    view.setBookData(response.getDataList(), response.hasPic());
                                    view.setStateView(false);
                                },
                                throwable -> {
                                    view.showErr("数据获取失败");
                                }
                        )
        );
    }

    @Override
    public void edit() {

    }

    @Override
    public void askForPrint() {

    }

    @Override
    public void changeTheme() {

    }
}
