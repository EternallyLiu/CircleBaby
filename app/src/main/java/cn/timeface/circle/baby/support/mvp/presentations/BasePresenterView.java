package cn.timeface.circle.baby.support.mvp.presentations;

import android.support.v7.app.AppCompatActivity;

import rx.Subscription;

/**
 * 基础的PresenterView
 * <p>
 * Created by JieGuo on 16/8/31.
 */

public interface BasePresenterView {

    AppCompatActivity getCurrentActivity();

    void addSubscription(Subscription s);
}
