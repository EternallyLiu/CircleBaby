package cn.timeface.circle.baby.support.mvp;

import cn.timeface.circle.baby.support.mvp.presentations.BasePresenterView;

/**
 * 基础Presenter 必需要保证有两个基本参数哈,这就是Presenter运行时必需要的东西。
 * 要是不提供,弄死你.
 * <p>
 * Created by JieGuo on 16/8/31.
 */

public abstract class BasePresenter<VIEW extends BasePresenterView, MODEL extends BasePresenterModel> {

    protected final String TAG = this.getClass().getSimpleName();

    protected VIEW view;
    protected MODEL model;

    protected void setup(VIEW view, MODEL model) {

        if (view == null || model == null) {
            throw new RuntimeException("我操,这个不能为空的 请不要尝试传个空的过来.");
        }
        this.view = view;
        this.model = model;
    }
}
